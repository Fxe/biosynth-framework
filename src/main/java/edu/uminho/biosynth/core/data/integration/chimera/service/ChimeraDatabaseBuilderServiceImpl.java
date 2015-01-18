package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteSourceProxy;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
//import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

@Service
@Transactional(value="integrationTransactionManager")
public class ChimeraDatabaseBuilderServiceImpl implements ChimeraDatabaseBuilderService{

	private static Logger LOGGER = Logger.getLogger(ChimeraDatabaseBuilderServiceImpl.class);
	
	@Autowired
	private IntegrationDataDao data;
	@Autowired
	private IntegrationMetadataDao meta;
//	@Autowired
//	private MetaboliteDao<IntegratedMetaboliteEntity> target;
	
	private IKeyGenerator<String> entryGenerator;
	
	private IntegrationSet currentIntegrationSet;
	
	public IntegrationSet getCurrentIntegrationSet() {
		return currentIntegrationSet;
	}
	public void setCurrentIntegrationSet(IntegrationSet currentIntegrationSet) {
		this.currentIntegrationSet = currentIntegrationSet;
	}
	public IntegrationDataDao getData() { return data;}
	public void setData(IntegrationDataDao data) { this.data = data;}

	public IntegrationMetadataDao getMeta() { return meta;}
	public void setMeta(IntegrationMetadataDao meta) { this.meta = meta;}
	
//	public MetaboliteDao<IntegratedMetaboliteEntity> getTarget() { return target;}
//	public void setTarget(MetaboliteDao<IntegratedMetaboliteEntity> target) { this.target = target;}
	
	public IKeyGenerator<String> getEntryGenerator() { return entryGenerator;}
	public void setEntryGenerator(IKeyGenerator<String> entryGenerator) { this.entryGenerator = entryGenerator;}
	
	private void addPropertyToIntegratedMetabolite(IntegratedMetaboliteEntity cpd, Long eid, String prop, Object value) {
		//THIS METHDO IS DUMB MAKE ANNOTATION + JAVA REFLEX !??!! :-(
		switch (prop) {
			case "id": //This prop is even dumber ! D-:
				if (!cpd.getModels().containsKey(eid)) {
					cpd.getModels().put(eid, new ArrayList<String> ());
				}
				cpd.getModels().get(eid).add((String)value);
				break;
			case "charge":
				cpd.getCharges().put(eid,(Integer)value);
				break;
			case "name":
				if (!cpd.getNames().containsKey(eid)) {
					cpd.getNames().put(eid, new ArrayList<String> ());
				}
				cpd.getNames().get(eid).add((String)value);
				break;
			case "compartment":
				if (!cpd.getCompartments().containsKey(eid)) {
					cpd.getCompartments().put(eid, new ArrayList<String> ());
				}
				cpd.getCompartments().get(eid).add((String)value);
				break;
			case "isoFormula":
				cpd.getIsoFormulas().put(eid,(String)value);
				break;
			case "formula":
				cpd.getFormulas().put(eid,(String)value);
				break;
			case "smiles":
				cpd.getSmiles().put(eid,(String)value);
				break;
			case "can":
				cpd.getCanSmiles().put(eid,(String)value);
				break;
			case "inchi":
				cpd.getInchis().put(eid,(String)value);
				break;
			case "entry":
				//THIS IS A BUG !
				break;
			default:
				throw new RuntimeException("Omg reading prop " + prop + " is not implemented :<");
		}
	}
	
	public List<IntegratedMetaboliteEntity> generateIntegratedMetabolites() {
		List<IntegratedMetaboliteEntity> res = new ArrayList<> ();
		
		for (Long id: this.currentIntegrationSet.getIntegratedClustersMap().keySet()) {			
			IntegratedCluster cluster = this.currentIntegrationSet.getIntegratedClustersMap().get(id);
			
			IntegratedMetaboliteEntity cpd = this.buildCompound(cluster);
			if (cpd != null) res.add(cpd);
		}
		
		return res;
	}
	
	@Override
	public void generateIntegratedDatabase() {
		if (this.currentIntegrationSet == null) {
			LOGGER.warn("No Integration Set selected - operation aborted");
			return;
		}
		List<IntegratedMetaboliteEntity> cpdList = this.generateIntegratedMetabolites();
		
		for(IntegratedMetaboliteEntity cpd: cpdList) {
			System.out.println(cpd);
		}
	}
	
	@Override
	public void resetTarget() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void changeIntegrationSet(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		this.currentIntegrationSet = integrationSet;
	}
	
	@Override
	public IntegratedMetaboliteEntity buildCompoundByClusterId(Long iid,
			String centry) {
		IntegratedCluster cluster = this.meta.getIntegratedClusterByEntry(centry, iid);
		return this.buildCompound(cluster);
	}
	
	@Override
	public IntegratedMetaboliteEntity buildCompoundByClusterId(Long id) {
		IntegratedCluster cluster = this.meta.getIntegratedClusterById(id);
		return this.buildCompound(cluster);
	}
	
	@Override
	public IntegratedMetaboliteEntity buildCompoundByClusterName(String cpdId) {
		IntegratedCluster cluster = this.meta.getIntegratedClusterByEntry(cpdId, this.currentIntegrationSet.getId());
		if (cluster == null) return null;
		return this.buildCompound(cluster);
	}

	@Override
	public IntegratedMetaboliteEntity buildCompoundByClusterMemberId(Long id) {
		IntegratedMember member = this.meta.getIntegratedMember(id);
		if (member == null) {
			LOGGER.warn(String.format("Member not found [%d]", id));
			return null;
		}
		
		if (member.getClusters().isEmpty()) return null;
		if (member.getClusters().size() > 1) {
			LOGGER.warn(String.format("Integrity fault - multiple clusters for member [%d]", id));
		}
		
		IntegratedCluster cluster = member.getClusters().iterator().next().getCluster();
		
		
		return this.buildCompound(cluster);
	}
	
	public IntegratedMetaboliteEntity buildCompound(String entry, Label...labels) {
		Node node = this.data.getCompositeNode(entry, labels);

		return this.buildCompoundByClusterMemberId(node.getId());
	}

	private IntegratedMetaboliteEntity buildCompound(IntegratedCluster cluster) {
		IntegratedMetaboliteEntity cpd = null;
		
		if (!cluster.getMembers().isEmpty()) {
			cpd = new IntegratedMetaboliteEntity();
			cpd.setId(cluster.getId());
			cpd.setEntry(cluster.getEntry());
			cpd.setSource(cluster.getIntegrationSet().getEntry());
			
			LOGGER.debug((String.format("Generating Integrated Metabolite[%s] from %s", cpd.getEntry(), cluster)));
			
			for (IntegratedClusterMember member: cluster.getMembers()) {
				Long memberId = member.getMember().getId();
				Map<String, Object> nodeProps = this.data.getEntryProperties((Long) memberId);

				if (!(Boolean)nodeProps.get("proxy")) {
					IntegratedMetaboliteSourceProxy proxy = new IntegratedMetaboliteSourceProxy();
					
					proxy.setIntegratedMetaboliteEntity(cpd);
					proxy.setEntry((String)nodeProps.get("entry"));
					proxy.setId(memberId);
					Set<String> labels = new HashSet<> (Arrays.asList(((String)nodeProps.get("labels")).split(":")));
					Set<String> labels_ = new HashSet<> (labels);
					labels_.remove("Compound");
					labels_.remove("BioCyc");
					proxy.setMajorLabel(labels_.iterator().next());
					if (labels_.size() != 1) {
						LOGGER.warn("MULTIPLE LABELS ARE SO MESSY ! " + labels_);
					}
					LOGGER.trace((String.format("Adding %s", proxy)));
//					proxy.setLabels(labels);
//					
					cpd.getSources().put(proxy.getId(), proxy);

				}
//				if (!(Boolean)nodeProps.get("isProxy")) cpd.getSources().add((String)nodeProps.get("labels") + ":" + (String)nodeProps.get("entry"));
//				System.out.println(nodeProps.get("labels") + " " + nodeProps.get("entry"));
				Map<String, List<Object>> data = this.data.getCompositeNode((Long)memberId);
//				System.out.println("DATA -> " + data);
				if (data.containsKey("crossreferences")) {
					for (Object xrefObj : data.get("crossreferences")) {
						IntegratedMetaboliteCrossreferenceEntity xref = (IntegratedMetaboliteCrossreferenceEntity) xrefObj;
						Map<Long, String> internalIds = this.meta.getIntegratedClusterWithElement(cluster.getIntegrationSet().getId(), memberId);
						if (internalIds.size() > 1) {
							LOGGER.warn(String.format("Found multiple memberships for %s - %s", memberId, internalIds));
						}
						if (!internalIds.isEmpty()) {
							Long internalId = internalIds.keySet().iterator().next();
							String internalEntry = internalIds.get(internalId);
							xref.setInternalEntry(internalEntry);
							xref.setInternalId(internalId);
						}
						 
//						IntegratedMetaboliteCrossreferenceEntity xref = 
//								new IntegratedMetaboliteCrossreferenceEntity(xref_);
						xref.setIntegratedMetaboliteEntity(cpd);
						cpd.getCrossreferences().add(xref);
					}
					data.remove("crossreferences");
				}
//				System.out.println(data);
				for (String property : data.keySet()) {
					for (Object value: data.get(property)) 
						addPropertyToIntegratedMetabolite(cpd, memberId, property, value);
//					System.out.println("PROPERTY -> " + property);
				}
			}
//			System.out.println(cpd);
		} else {
			LOGGER.warn(String.format("Skipped Cluster[%d] - Empty", cluster.getId()));
		}
		return cpd;
	}
	

}
