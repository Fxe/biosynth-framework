package edu.uminho.biosynth.core.data.io.dao.seed;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class JsonSeedMetaboliteDaoImpl implements IMetaboliteDao<SeedMetaboliteEntity>{

	public static int INITIAL_GEN_KEY = 0;
	
	private File jsonFile;
	
	private JsonNode rootNode;
	private Map<String, List<GenericCrossReference>> refMap = new HashMap<> ();
	private Map<Serializable, SeedMetaboliteEntity> metaboliteMap;
	
	public File getJsonFile() { return jsonFile;}
	public void setJsonFile(File jsonFile) { this.jsonFile = jsonFile;}
	
	private void buildXRefMap(JsonNode node, GenericCrossReference.Type type, Map<String, List<GenericCrossReference>> map, String ref) {
		Iterator<String> fields = node.getFieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			JsonNode uuid_array = node.get(field);
			for (int i = 0; i < uuid_array.size(); i++) {
				String uuid = uuid_array.get(i).asText();
//				System.out.println(uuid + " -> " + field.trim());
				GenericCrossReference xref = new GenericCrossReference(type, ref, field.trim());
				if (!map.containsKey(uuid)) {
					List<GenericCrossReference> xrefs = new ArrayList<> ();
					map.put(uuid, xrefs);
				}
				
				map.get(uuid).add(xref);
			}
		}
//		for (int i = 0; i < node.size(); i++) {
//			JsonNode elem = node.get(i);
//			System.out.println(elem);
//			CrossReference xref = new CrossReference(type, null, null);
//		}
	}
	
	@Override
	public SeedMetaboliteEntity find(Serializable id) {
		for (SeedMetaboliteEntity cpd : this.findAll()) {
			if (cpd.getEntry().equals(id)) return cpd;
		}
		return null;
	}

	@Override
	public List<SeedMetaboliteEntity> findAll() {
		this.metaboliteMap = new HashMap<> ();
		List<SeedMetaboliteEntity> res = new ArrayList<> ();
		JsonNode compounds = rootNode.get("compounds");
		for (int i = 0; i < compounds.size(); i++ ) {
			try {
				SeedMetaboliteEntity cpd = this.parseJsonSeedCompound(compounds.get(i));
				
				List<SeedCompoundCrossReferenceEntity> xrefs = new ArrayList<> ();
				if (refMap.containsKey(cpd.getUuid())) {
//					System.out.println(refMap.get(cpd.getUuid()));
					for (GenericCrossReference xref : refMap.get(cpd.getUuid())) {
						switch (xref.getType()) {
							case NAME:
								cpd.getSynonyms().add(xref.getValue());
								break;
							case SELF:
								if (cpd.getEntry() != null) {
									System.err.println("Duplicate SEED entry value for " + cpd.getUuid() + cpd.getEntry());
								} else {
									cpd.setEntry(xref.getValue());
								}
								break;
							case MODEL:
								xrefs.add(new SeedCompoundCrossReferenceEntity(xref));
								break;
							case DATABASE:
								xrefs.add(new SeedCompoundCrossReferenceEntity(xref));
								break;
							default:
								System.err.println(xref.getType() + " unsupported");
								break;
						}

					}
				}
				if (cpd.getEntry() == null) {
					System.err.println(cpd.getName() + cpd.getUuid() + " did not have entry value generating key ..");
					cpd.setEntry("cpdGENVALUE" + INITIAL_GEN_KEY++);
				}
				cpd.setCrossReferences(xrefs);
				res.add(cpd);
				this.metaboliteMap.put(cpd.getEntry(), cpd);
			} catch (IOException e) {
				System.err.println(i + " :: " + e.getMessage());
			}
		}
		return res;
	}

	@Override
	public Serializable save(SeedMetaboliteEntity entity) {
		throw new RuntimeException("Not Supported Operation");
	}

	public void initialize() {
		ObjectMapper m = new ObjectMapper();
		
		try {
			rootNode = m.readTree(jsonFile);
			for (int i = 0; i < rootNode.get("aliasSets").size(); i++) {
				System.out.println("Index - " + i);
				String attribute = rootNode.get("aliasSets").get(i).get("attribute").toString();
				String name = rootNode.get("aliasSets").get(i).get("name").toString();
				
				attribute = attribute.replaceAll("\"", "");
				name = name.replaceAll("\"", "");
				List<String> otherDbs = new ArrayList<> ();
				otherDbs.add("AraCyc.45632");
				otherDbs.add("MaizeCyc.45632");
//				otherDbs.add("nameMaizeCyc.45632");
//				otherDbs.add("searchnameMaizeCyc.45632");
				if (attribute.equals("compounds")) {
					if (name.startsWith("i") || name.equals("AraGEM.45632")) {
						System.out.println("Building Refs for metabolic model - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.MODEL, refMap, name);
					} else 
					if (name.equals("ModelSEED")) {
						System.out.println("Building Refs for compound entries - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.SELF, refMap, "SEED");
					} else
					if (name.equals("name")) {
						System.out.println("Building Refs for compound synonyms - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.NAME, refMap, "SYNONYM");
					} else
					if (name.equals("KEGG")) {
						System.out.println("Building Refs for compound KEGG XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "KEGG");
					} else
					if (name.equals("AraCyc.45632")) {
						System.out.println("Building Refs for compound BioCyc ARA XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "BIOCYC:ARA");
					} else
					if (name.equals("MaizeCyc.45632")) {
						System.out.println("Building Refs for compound PlantCyc maize XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "PLANTCYC:MAIZE");
					} else
					if (name.equals("obsolete")) {
//						System.out.println("Building Refs for obsolete compounds - " + name + " for " + attribute);
//						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
//								GenericCrossReference.Type.OBSOLETE, refMap, "obsolete");
					} else {
						System.out.println("NO MATCH FOR " + name);
					}
				} else {
					System.out.println("SKIPED " + attribute + " " + name);
				}
			}
//			buildXRefMap(rootNode.get("aliasSets").get(2) .get("aliases"), GenericCrossReference.Type.ECNUMBER, refMap, "ECNUMBER");
//			buildXRefMap(rootNode.get("aliasSets").get(1) .get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "SEED");
//			buildXRefMap(rootNode.get("aliasSets").get(0) .get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "SEED");
//			buildXRefMap(rootNode.get("aliasSets").get(22).get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "KEGG");
//			buildXRefMap(rootNode.get("aliasSets").get(52).get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "KEGG");
//			buildXRefMap(rootNode.get("aliasSets").get(4).get("aliases"), GenericCrossReference.Type.NAME, refMap, "NAME");

		} catch (JsonProcessingException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public SeedMetaboliteEntity parseJsonSeedCompound(JsonNode node) 
			throws JsonMappingException, JsonParseException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(node, SeedMetaboliteEntity.class);
	}
	
	@Override
	public List<Serializable> getAllMetaboliteIds() {
		if (this.metaboliteMap == null) {
			this.findAll();
		}
		return new ArrayList<>(this.metaboliteMap.keySet());
	}
	
	@Override
	public SeedMetaboliteEntity getMetaboliteInformation(Serializable id) {
		if (this.metaboliteMap == null) {
			this.findAll();
		}
		if (this.metaboliteMap.containsKey(id)) {
			return this.metaboliteMap.get(id);
		}
		return null;
	}
	
	@Override
	public SeedMetaboliteEntity saveMetaboliteInformation(
			SeedMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}
}
