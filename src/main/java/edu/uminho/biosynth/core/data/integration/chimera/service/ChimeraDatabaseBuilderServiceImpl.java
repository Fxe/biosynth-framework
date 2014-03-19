package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class ChimeraDatabaseBuilderServiceImpl implements ChimeraDatabaseBuilderService{

	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	@Autowired
	private IMetaboliteDao<IntegratedMetaboliteEntity> target;
	
	private IKeyGenerator<String> entryGenerator;
	
	private IntegrationSet currentIntegrationSet;
	
	public ChimeraDataDao getData() { return data;}
	public void setData(ChimeraDataDao data) { this.data = data;}

	public ChimeraMetadataDao getMeta() { return meta;}
	public void setMeta(ChimeraMetadataDao meta) { this.meta = meta;}
	
	public IMetaboliteDao<IntegratedMetaboliteEntity> getTarget() { return target;}
	public void setTarget(IMetaboliteDao<IntegratedMetaboliteEntity> target) { this.target = target;}
	
	public IKeyGenerator<String> getEntryGenerator() { return entryGenerator;}
	public void setEntryGenerator(IKeyGenerator<String> entryGenerator) { this.entryGenerator = entryGenerator;}
	
	private void addPropertyToIntegratedMetabolite(IntegratedMetaboliteEntity cpd, String prop, Object value) {
		//THIS METHDO IS DUMB MAKE ANNOTATION + JAVA REFLEX !??!! :-(
		switch (prop) {
			case "id": //This prop is even dumber ! D-:
				cpd.getModels().add((String)value);
				break;
			case "charge":
				cpd.getCharges().add((Integer)value);
				break;
			case "name":
				cpd.getNames().add((String)value);
				break;
			case "compartment":
				cpd.getCompartments().add((String)value);
				break;
			case "formula":
				cpd.getFormulas().add((String)value);
				break;
			case "smiles":
				cpd.getSmiles().add((String)value);
				break;
			case "inchi":
				cpd.getInchis().add((String)value);
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
			
			if (!cluster.getMemberMap().isEmpty()) {
				System.out.println("Creating compound " + cluster.getId());
				IntegratedMetaboliteEntity cpd = new IntegratedMetaboliteEntity();
				
				cpd.setEntry(entryGenerator.generateKey());
				cpd.setSource(this.currentIntegrationSet.getName());
				
				for (Serializable memberId: cluster.getMemberMap().keySet()) {
					Map<String, Object> nodeProps = this.data.getEntry((Long) memberId);
					if (!(Boolean)nodeProps.get("isProxy")) cpd.getSources().add((String)nodeProps.get("labels") + ":" + (String)nodeProps.get("entry"));
					System.out.println(nodeProps.get("labels") + " " + nodeProps.get("entry"));
					Map<String, List<Object>> data = this.data.getCompositeNode((Long)memberId);
//					System.out.println("DATA -> " + data);
					if (data.containsKey("crossreferences")) {
						for (Object xrefObj : data.get("crossreferences")) {
							IntegratedMetaboliteCrossreferenceEntity xref = (IntegratedMetaboliteCrossreferenceEntity) xrefObj;
//							IntegratedMetaboliteCrossreferenceEntity xref = 
//									new IntegratedMetaboliteCrossreferenceEntity(xref_);
							xref.setIntegratedMetaboliteEntity(cpd);
							cpd.getCrossreferences().add(xref);
						}
						data.remove("crossreferences");
					}
//					System.out.println(data);
					for (String property : data.keySet()) {
						for (Object value: data.get(property)) addPropertyToIntegratedMetabolite(cpd, property, value);
//						System.out.println("PROPERTY -> " + property);
					}
				}
//				System.out.println(cpd);
				res.add(cpd);
			}
			
		}
		
		return res;
	}
	
	@Override
	public void generateIntegratedDatabase() {
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

}
