package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class JsonSeedReactionDaoImpl implements ReactionDao<SeedReactionEntity> {

	private static Logger LOGGER = LoggerFactory.getLogger(JsonSeedReactionDaoImpl.class);
	
	public static int INITIAL_GEN_KEY = 0;
	
	private Resource jsonFile;
	private JsonNode rootNode;
	
	private Map<String, List<GenericCrossReference>> refMap = new HashMap<> ();
	private Map<String, SeedReactionEntity> reactionMap = new HashMap<> ();
	
	public Resource getJsonFile() { return jsonFile;}
	public void setJsonFile(Resource jsonFile) { this.jsonFile = jsonFile;}
	
	public JsonSeedReactionDaoImpl(Resource jsonFile) {
		this.jsonFile = jsonFile;
		this.initialize();
		this.notsurewhattocall();
	}
	
	@Override
	public SeedReactionEntity getReactionById(Long id) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public SeedReactionEntity getReactionByEntry(String entry) {
		return this.reactionMap.get(entry);
	}

	@Override
	public SeedReactionEntity saveReaction(SeedReactionEntity reaction) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Set<Long> getAllReactionIds() {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Set<String> getAllReactionEntries() {
		return new HashSet<String> (this.reactionMap.keySet());
	}
	
	private void notsurewhattocall() {
		this.reactionMap.clear();
		
		JsonNode reactions = rootNode.get("reactions");
		for (int i = 0; i < reactions.size(); i++ ) {
			
			LOGGER.debug("Reading record: " + i);
			
			try {
				SeedReactionEntity rxn = this.parseJsonSeedCompound(reactions.get(i));
				
				List<SeedReactionCrossReferenceEntity> xrefs = new ArrayList<> ();
				if (refMap.containsKey(rxn.getUuid())) {
//					System.out.println(refMap.get(cpd.getUuid()));
					for (GenericCrossReference xref : refMap.get(rxn.getUuid())) {
						switch (xref.getType()) {
							case NAME:
//								rxn.getSynonyms().add(xref.getValue());
								break;
							case SELF:
								if (rxn.getEntry() != null) {
									System.err.println("Duplicate SEED entry value for " + rxn.getUuid() + rxn.getEntry());
								} else {
									rxn.setEntry(xref.getValue());
								}
								break;
							case MODEL:
								xrefs.add(new SeedReactionCrossReferenceEntity(xref));
								break;
							case DATABASE:
								xrefs.add(new SeedReactionCrossReferenceEntity(xref));
								break;
							default:
								System.err.println(xref.getType() + " unsupported");
								break;
						}

					}
				}
				if (rxn.getEntry() == null) {
					System.err.println(rxn.getName() + rxn.getUuid() + " did not have entry value generating key ..");
					rxn.setEntry("rxnGENVALUE" + INITIAL_GEN_KEY++);
				}
				rxn.setCrossReferences(xrefs);
				this.reactionMap.put(rxn.getEntry(), rxn);
			} catch (IOException e) {
				System.err.println(i + " :: " + e.getMessage());
			}
		}
	}
	
	private void buildXRefMap(JsonNode node, GenericCrossReference.Type type, Map<String, List<GenericCrossReference>> map, String ref) {
		Iterator<String> fields = node.fieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			JsonNode uuid_array = node.get(field);
			for (int i = 0; i < uuid_array.size(); i++) {
				String uuid = uuid_array.get(i).asText();
				GenericCrossReference xref = new GenericCrossReference(type, ref, field.trim());
				if (!map.containsKey(uuid)) {
					List<GenericCrossReference> xrefs = new ArrayList<> ();
					map.put(uuid, xrefs);
				}
				
				map.get(uuid).add(xref);
			}
		}
	}

	public void initialize() {
		ObjectMapper m = new ObjectMapper();
		
		try {
			rootNode = m.readTree(this.jsonFile.getFile());
			
			LOGGER.debug("Loading 'aliasSets'");
			
			for (int i = 0; i < rootNode.get("aliasSets").size(); i++) {
				LOGGER.debug("Index - " + i);
				String attribute = rootNode.get("aliasSets").get(i).get("attribute").toString();
				String name = rootNode.get("aliasSets").get(i).get("name").toString();
				
				attribute = attribute.replaceAll("\"", "");
				name = name.replaceAll("\"", "");
//				List<String> otherDbs = new ArrayList<> ();
//				otherDbs.add("AraCyc.45632");
//				otherDbs.add("MaizeCyc.45632");
//				otherDbs.add("nameMaizeCyc.45632");
//				otherDbs.add("searchnameMaizeCyc.45632");
				if (attribute.equals("reactions")) {
					if (name.startsWith("i") || name.equals("AraGEM.45632")) {
						LOGGER.debug("Building Refs for metabolic model - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.MODEL, refMap, name);
					} else 
					if (name.equals("ModelSEED")) {
						LOGGER.debug("Building Refs for compound entries - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.SELF, refMap, "SEED");
					} else
					if (name.equals("name")) {
						LOGGER.debug("Building Refs for compound synonyms - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.NAME, refMap, "SYNONYM");
					} else
					if (name.equals("KEGG")) {
						LOGGER.debug("Building Refs for compound KEGG XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "KEGG");
					} else
					if (name.equals("AraCyc.45632")) {
						LOGGER.debug("Building Refs for compound BioCyc ARA XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "BIOCYC:ARA");
					} else
					if (name.equals("MaizeCyc.45632")) {
						LOGGER.debug("Building Refs for compound PlantCyc maize XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								GenericCrossReference.Type.DATABASE, refMap, "PLANTCYC:MAIZE");
					} else
					if (name.equals("obsolete")) {
//						System.out.println("Building Refs for obsolete compounds - " + name + " for " + attribute);
//						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
//								GenericCrossReference.Type.OBSOLETE, refMap, "obsolete");
					} else {
						LOGGER.warn("NO MATCH FOR " + name);
					}
				} else {
					LOGGER.debug("SKIPED " + attribute + " " + name);
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	public void omg() {
		
		Iterator<String> a = rootNode.fieldNames();
		while (a.hasNext()) {
			String gg = a.next();
			System.out.println(gg);
			System.out.println(rootNode.get(gg).size());
		}
		
//		JsonNode reactions = rootNode.get("reactions");
//		for (int i = 0; i < reactions.size(); i++ ) {
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
//		}
	}
	
	public void comp() {
		JsonNode compartments = rootNode.get("compartments");
		for (int i = 0; i < compartments.size(); i++ ) {
			System.out.println(compartments.get(i));
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
		}
	}
	
	public void defaultNameSpace() {
		JsonNode compartments = rootNode.get("defaultNameSpace");
		for (int i = 0; i < compartments.size(); i++ ) {
			System.out.println(compartments.get(i));
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
		}
	}
	public void gg() {
		JsonNode compartments = rootNode.get("public");
		for (int i = 0; i < compartments.size(); i++ ) {
			System.out.println(compartments.get(i));
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
		}
	}
	
	public void publics() {
		JsonNode compartments = rootNode.get("media");
		for (int i = 0; i < compartments.size(); i++ ) {
			System.out.println(compartments.get(i));
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
		}
	}
	
	public void uuid() {
		JsonNode compartments = rootNode.get("uuid");
		for (int i = 0; i < compartments.size(); i++ ) {
			System.out.println(compartments.get(i));
//			try {
//				SeedReactionEntity entity = this.parseJsonSeedCompound(reactions.get(i));
//				System.out.println(entity);
//			} catch (JsonMappingException | JsonParseException e) {
//				LOGGER.error(e.getMessage());
//			} catch (IOException e) {
//				LOGGER.error(e.getMessage());
//			}
		}
	}
	
	private SeedReactionEntity parseJsonSeedCompound(JsonNode node) 
			throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
//		node.
		return mapper.readValue(node.toString(), SeedReactionEntity.class);
	}
}
