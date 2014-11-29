package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;

public class BiocycMetaboliteTransform
extends AbstractMetaboliteTransform<BioCycMetaboliteEntity>{

//	private final String BIOCYC_P_COMPOUND_METABOLITE_LABEL;
	private final static Logger LOGGER = LoggerFactory.getLogger(BiocycMetaboliteTransform.class);
	
	private Map<String, String> biggInternalIdToEntryMap;
	
	public BiocycMetaboliteTransform(String majorLabel, Map<String, String> biggInternalIdToEntryMap) {
		super(majorLabel, new BiobaseMetaboliteEtlDictionary<>(BioCycMetaboliteEntity.class));
		this.biggInternalIdToEntryMap = biggInternalIdToEntryMap;
//		this.BIOCYC_P_COMPOUND_METABOLITE_LABEL = majorLabel;
	}
	
	@Override
	protected void configureProperties(GraphMetaboliteEntity centralMetaboliteEntity, BioCycMetaboliteEntity metabolite) {
//		centralMetaboliteEntity.addProperty("inchi", metabolite.getInchi());
//		centralMetaboliteEntity.addProperty("smiles", metabolite.getSmiles());
//		centralMetaboliteEntity.addProperty("gibbs", metabolite.getGibbs());
//		centralMetaboliteEntity.addProperty("frameId", metabolite.getFrameId());
//		centralMetaboliteEntity.addProperty("cmlMolWeight", metabolite.getCmlMolWeight());
		super.configureProperties(centralMetaboliteEntity, metabolite);
	};

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BioCycMetaboliteEntity entity) {
		
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getInchi(), 
						METABOLITE_INCHI_LABEL, 
						METABOLITE_INCHI_RELATIONSHIP_TYPE));
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getSmiles(), 
						METABOLITE_SMILE_LABEL, 
						METABOLITE_SMILE_RELATIONSHIP_TYPE));
		for (String parent : entity.getParents()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair(
							PROPERTY_UNIQUE_KEY, 
							parent, 
							SUPER_METABOLITE_LABEL, 
							METABOLITE_INSTANCE_RELATIONSHIP_TYPE));
		}
	}
	
	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BioCycMetaboliteEntity entity) {
		
		for (String name : entity.getSynonyms()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair(
							PROPERTY_UNIQUE_KEY, 
							name, 
							METABOLITE_NAME_LABEL, 
							METABOLITE_NAME_RELATIONSHIP_TYPE));
		}
		
		super.configureNameLink(centralMetaboliteEntity, entity);
	}
	
	@Override
		protected void configureCrossreferences(
				GraphMetaboliteEntity centralMetaboliteEntity,
				BioCycMetaboliteEntity metabolite) {
			for (BioCycMetaboliteCrossreferenceEntity xref : metabolite.getCrossreferences()) {
				if (xref.getRef().equals("BIGG")) {
					if (biggInternalIdToEntryMap.containsKey(xref.getValue())) {
						xref.setValue(biggInternalIdToEntryMap.get(xref.getValue()));
					}
					LOGGER.debug("Internal Id replaced: " + xref);
				}
			}
			super.configureCrossreferences(centralMetaboliteEntity, metabolite);
		}

//	@Override
//	protected void configureCrossreferences(
//			CentralMetaboliteEntity centralMetaboliteEntity,
//			BioCycMetaboliteEntity entity) {
//
//		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (BioCycMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.putProperty("reference", xref.getRef());
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//		
//		centralMetaboliteEntity.setCrossreferences(crossreferences);
//	}

}
