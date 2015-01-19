package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.data.integration.IntegrationMessageLevel;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.FormulaReader;

public class MetaboliteClusterQualityScreener implements EtlQualityScreen<IntegratedCluster> {

	private FormulaReader formulaConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaboliteClusterQualityScreener.class);
	
	private MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao;
	
	@Autowired
	public MetaboliteClusterQualityScreener(
			MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao, FormulaReader formulaConverter) {
		this.formulaConverter = formulaConverter;
		this.metaboliteDao = metaboliteDao;
	}
	
	@Override
	public void evaluate(IntegratedCluster integratedCluster) {
		Set<MetaboliteQualityLabel> labels = this.yay(integratedCluster);
		
		LOGGER.trace(labels.toString());
		
		Map<String, IntegratedClusterMeta> meta = new HashMap<> ();
		
		for (MetaboliteQualityLabel label : labels) {
			IntegratedClusterMeta clusterMeta = new IntegratedClusterMeta();
			IntegrationMessageLevel level;
			switch (label) {
				case FORMULA_EMPTY: level = IntegrationMessageLevel.INFO; break;
				case FORMULA_MISMATCH: level = IntegrationMessageLevel.ERROR; break;
				case FORMULA_MISMATCH_HYDROGEN: level = IntegrationMessageLevel.WARNING; break;
				case FORMULA_EXACT: level = IntegrationMessageLevel.INFO; break;
				
				case CHARGE_MISMATCH: level = IntegrationMessageLevel.WARNING; break;
				
				case CROSSREFERENCE_EXTERNAL: level = IntegrationMessageLevel.ERROR; break;
				case MULTIPLE_DATABASES: level = IntegrationMessageLevel.ERROR; break;
				
				case INCHI_MISMATCH: level = IntegrationMessageLevel.ERROR; break;
				case INCHI_MIXED_F: level = IntegrationMessageLevel.WARNING; break;
				case INCHI_STANDARD: level = IntegrationMessageLevel.INFO; break;
				case INCHI_NON_STANDARD: level = IntegrationMessageLevel.INFO; break;
				case INCHI_P_BLOCK_MISMATCH: level = IntegrationMessageLevel.WARNING; break;
				case INCHI_VERSION_MISMATCH: level = IntegrationMessageLevel.WARNING; break;
				case INCHI_VERSION_1: level = IntegrationMessageLevel.INFO; break;
				case INCHI_VERSION_2: level = IntegrationMessageLevel.INFO; break;
				case INCHI_SECOND_HASH_BLOCK_MISMATCH: level = IntegrationMessageLevel.ERROR; break;
				
				case SMILES_MISMATCH: level = IntegrationMessageLevel.ERROR; break;
				
				default:
					throw new RuntimeException("Unsupported Assertion Label: " + label);
			}
			
			clusterMeta.setLevel(level);
			clusterMeta.setIntegratedCluster(integratedCluster);
			clusterMeta.setMessage("massage !");
			clusterMeta.setMetaType(label.toString());
			
			meta.put(clusterMeta.getMetaType(), clusterMeta);
		}
		
		integratedCluster.setMeta(meta);
	}
	
	public Set<MetaboliteQualityLabel> yay(IntegratedCluster integratedCluster) {
		
		List<GraphMetaboliteEntity> cpdList = new ArrayList<> ();
	
		LOGGER.debug("Load Metabolites ...");
		for (IntegratedClusterMember member : integratedCluster.getMembers()) {
			Long referenceId = member.getMember().getReferenceId();
			GraphMetaboliteEntity cpd = metaboliteDao.getMetaboliteById("", referenceId);
			
			if (cpd != null) {
				cpdList.add(cpd);
			}
		}
		
		return this.yay(cpdList);
	}
	
	public Set<MetaboliteQualityLabel> yay(List<GraphMetaboliteEntity> cpdList) {
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		if (cpdList.isEmpty()) {
			LOGGER.warn("String empty cluster.");	
			return qualityLabels;
		}
		
		qualityLabels.addAll(verifyFormulas(cpdList));
		qualityLabels.addAll(verifyChemicalCharges(cpdList));
		qualityLabels.addAll(verifyStructure(cpdList));
		qualityLabels.addAll(verifyCrossreference(cpdList));
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyFormulas(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking formulas ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.MolecularFormula.toString());
		
		if (occurenceMap.size() == 1) {
			qualityLabels.add(MetaboliteQualityLabel.FORMULA_EXACT);
			return qualityLabels;
		}
		
		if (occurenceMap.isEmpty()) {
			qualityLabels.add(MetaboliteQualityLabel.FORMULA_EMPTY);
			return qualityLabels;
		}
		
		
		List<Map<String, Integer>> atomMapList = new ArrayList<> ();
		for (Object formula_ : occurenceMap.keySet()) {
			String formula = (String) formula_;
			Map<String, Integer> atomMap = formulaConverter.getAtomCountMap(formula);
			LOGGER.trace(String.format("%s => %s", formula, atomMap));
			atomMapList.add(atomMap);
		}
		
		Map<String, Integer> atomMapPivot = atomMapList.get(0);
		for (int i = 1; i < atomMapList.size(); i++) {
			Map<String, Integer> atomMap = atomMapList.get(i);
			for (String atom : atomMapPivot.keySet()) {
				//pivot is never null
				Integer atomCountPivot = atomMapPivot.get(atom);
				//atom map may be null (if formula has distinct elements)
				Integer atomCount = atomMap.get(atom);
				if (atomCount == null) atomCount = 0;
				LOGGER.trace(String.format("Check atom %s frequency ... %d --> %d", atom, atomCountPivot, atomCount));
				if (atomCount != atomCountPivot) {
					if (atom.equals("H")) {
						LOGGER.trace("Hydrogen Mismatch");
						qualityLabels.add(MetaboliteQualityLabel.FORMULA_MISMATCH_HYDROGEN);
					} else {
						LOGGER.trace("Element Mismatch");
						qualityLabels.add(MetaboliteQualityLabel.FORMULA_MISMATCH);
					}
				}
			}
		}
		
		
		if (qualityLabels.isEmpty()) qualityLabels.add(MetaboliteQualityLabel.FORMULA_EXACT);
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyChemicalCharges(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking chemical charge ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.Charge.toString());
		
		if (occurenceMap.size() > 1) qualityLabels.add(MetaboliteQualityLabel.CHARGE_MISMATCH);
		
		return qualityLabels;
	}
	
	public static String[] disassembleInchiKey(String inchiKey) {
		String[] result = new String[5];
		
		String[] inchiBlock = inchiKey.split("-");
		// AAAAAAAAAAAAAA-BBBBBBBBFV-P
		String fihbk = inchiBlock[0];
		String sihbk = inchiBlock[1].substring(0, 8);
		char fhbk = inchiBlock[1].charAt(8);
		char vhbk = inchiBlock[1].charAt(9);
		char phbk = inchiBlock[2].charAt(0);
		
		LOGGER.trace(String.format("%s -> %s -> %s _ %s _ %s", fihbk, sihbk, fhbk, vhbk, phbk));
		
		result[0] = fihbk;
		result[1] = sihbk;
		result[2] = Character.toString(fhbk);
		result[3] = Character.toString(vhbk);
		result[4] = Character.toString(phbk);
		
		return result;
	}
	
	public Set<MetaboliteQualityLabel> verifyStructure(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking structure ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> inchiOccurenceMap = this.collectPropertyKeys(cpdList, "inchiKey", MetabolitePropertyLabel.InChI.toString());
		Map<Object, Integer> smilesOccurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.SMILES.toString());
		
		if (inchiOccurenceMap.size() > 1) {
			Set<String> fihbkSet = new HashSet<> ();
			Set<String> sihbkSet = new HashSet<> ();
			Set<String> fhbkSet = new HashSet<> ();
			Set<String> vhbkSet = new HashSet<> ();
			Set<String> phbkSet = new HashSet<> ();
			
			
			for (Object inchi_ : inchiOccurenceMap.keySet()) {
				String[] inchiBlock = disassembleInchiKey((String) inchi_);
				
				fihbkSet.add(inchiBlock[0]);
				sihbkSet.add(inchiBlock[1]);
				fhbkSet.add(inchiBlock[2]);
				vhbkSet.add(inchiBlock[3]);
				phbkSet.add(inchiBlock[4]);
				
				
			}
			if (fihbkSet.size() > 1) {
				qualityLabels.add(MetaboliteQualityLabel.INCHI_MISMATCH);
			} else {
				if (sihbkSet.size() > 1) {
					qualityLabels.add(MetaboliteQualityLabel.INCHI_SECOND_HASH_BLOCK_MISMATCH);					
				}
				if (fhbkSet.size() > 1) {
					qualityLabels.add(MetaboliteQualityLabel.INCHI_MIXED_F);					
				} else if (fhbkSet.size() == 1) {
					String kind = fhbkSet.iterator().next();
					switch (kind) {
						case "S": qualityLabels.add(MetaboliteQualityLabel.INCHI_STANDARD); break;
						case "N": qualityLabels.add(MetaboliteQualityLabel.INCHI_NON_STANDARD); break;
						default: break;
					}
				}
				if (vhbkSet.size() > 1) {
					qualityLabels.add(MetaboliteQualityLabel.INCHI_VERSION_MISMATCH);					
				} else if (vhbkSet.size() == 1) {
					String version = vhbkSet.iterator().next();
					switch (version) {
						case "A": qualityLabels.add(MetaboliteQualityLabel.INCHI_VERSION_1); break;
						case "B": qualityLabels.add(MetaboliteQualityLabel.INCHI_VERSION_2); break;
						default: break;
					}
				}
				if (phbkSet.size() > 1) {
					qualityLabels.add(MetaboliteQualityLabel.INCHI_P_BLOCK_MISMATCH);					
				}
			}
			
		}
		
		if (smilesOccurenceMap.size() > 1) {
			qualityLabels.add(MetaboliteQualityLabel.SMILES_MISMATCH);
		}
		
		return qualityLabels;
	}
	

	
	
	private Map<Object, Integer> collectPropertyKeys(List<GraphMetaboliteEntity> cpdList, String majorLabel) {
		return collectPropertyKeys(cpdList, "key", majorLabel);
	}
	private Map<Object, Integer> collectPropertyKeys(List<GraphMetaboliteEntity> cpdList, String key, String majorLabel) {
		Map<Object, Integer> occurenceMap = new HashMap<> ();
		
		for (GraphMetaboliteEntity cpd: cpdList) {
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
				cpd.getPropertyEntities()) {
				if (property.getLeft().getLabels().contains(majorLabel)) {
					LOGGER.trace("Found: " + property.getLeft());
					
					GraphPropertyEntity propertyEntity = property.getLeft(); 
					CollectionUtils.increaseCount(occurenceMap, propertyEntity.getProperty(key, null), 1);
				}
			}
		}
		
		LOGGER.debug("Total: " + occurenceMap);
		
		return occurenceMap;
	}
	
	public Set<MetaboliteQualityLabel> verifyCrossreference(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking cross-reference ...");
		
		Set<Pair<String, String>> cpdEntries = new HashSet<> ();
		for (GraphMetaboliteEntity cpdEntity : cpdList) {
			Pair<String, String> entry = new ImmutablePair<> (cpdEntity.getMajorLabel(), cpdEntity.getEntry());
			
			if (entry.getLeft() == null || entry.getRight() == null) {
				LOGGER.warn("null key field: " + entry);
			}
			
			cpdEntries.add(entry);
		}
		
		LOGGER.trace(cpdEntries.toString());
		
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		boolean allInternal = true;
		
		for (GraphMetaboliteEntity cpd : cpdList) {
			for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair : cpd.getCrossreferences()) {
				GraphMetaboliteProxyEntity proxyEntity = proxyPair.getLeft();
				if(! (boolean) proxyEntity.getProperty("proxy", true)) {
					Pair<String, String> entry = new ImmutablePair<> (proxyEntity.getMajorLabel(), proxyEntity.getEntry());
					
					if (entry.getLeft() == null || entry.getRight() == null) {
						LOGGER.warn("null key field: " + entry);
					}
					
					if (cpdEntries.contains(entry)) {
						LOGGER.trace(String.format(" OK %s", entry));
					} else {
						LOGGER.trace(String.format("NOK %s", entry));
						allInternal = false;
					}
				}
			}
		}
		
		if (!allInternal) qualityLabels.add(MetaboliteQualityLabel.CROSSREFERENCE_EXTERNAL);
		
		return qualityLabels;
	}
}
