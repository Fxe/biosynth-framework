package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.io.File;
import java.io.FileInputStream;
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

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedAliaseSet;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionReagentEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSeedReactionDaoImpl implements ReactionDao<SeedReactionEntity> {

	private static Logger LOGGER = LoggerFactory.getLogger(JsonSeedReactionDaoImpl.class);
	private static String ALIAS_REACTION_ATTRIBUTE = "reactions";
	private static String ALIAS_COMPOUND_ATTRIBUTE = "compounds";
	private static String SEED_ENTRY_ALIAS = "ModelSEED";
	
	private static String[] NAME_ALIAS = {"searchname", "name"};
	private static String[] ENZYME_CLASS_ALIAS = {"Enzyme Class"};
	private static String[] MODEL_ALIAS = {"iAbaylyiv4", "iAF1260", "iAF692", "iAG612", "iAO358", "iGT196", "iIN800", "iIT341", "iJN746", "iJR904", "iMEO21", "iMM904", "iMO1056", "iND750", "iNJ661", "iPS189", "iSB619", "iSO783", "iYO844"};
	private static String[] DATABASE_ALIAS = {"KEGG"};
	
	
	private String directory;
	private ObjectMapper objectMapper = new ObjectMapper();
	private Map<String, SeedCompartmentEntity> compartmentMap = new HashMap<> ();
	private Map<String, Map<String, SeedAliaseSet>> aliaseSetMap = new HashMap<> ();
	
	public static int INITIAL_GEN_KEY = 0;
	
	private Resource jsonFile;
	private JsonNode rootNode;
	
	private Map<String, List<GenericCrossReference>> refMap = new HashMap<> ();
	private Map<String, SeedReactionEntity> reactionMap = new HashMap<> ();
	
	public Resource getJsonFile() { return jsonFile;}
	public void setJsonFile(Resource jsonFile) { this.jsonFile = jsonFile;}
	
	@Deprecated
	public JsonSeedReactionDaoImpl(Resource jsonFile) {
		this.jsonFile = jsonFile;
		this.initialize();
		this.notsurewhattocall();
	}
	
	public JsonSeedReactionDaoImpl(String directory) {
		this.directory = directory;
		try {
			File actual = new File(directory + "/aliasSets");
	        for(File f : actual.listFiles()){
	            SeedAliaseSet seedAliaseSet = objectMapper.readValue(f, SeedAliaseSet.class);
	            this.addAliaseSet(seedAliaseSet);
	        }
	        
	        JsonNode jsonArray = objectMapper.readTree(new FileInputStream(new File(directory + "/compartments.json")));
	        for (int i = 0; i < jsonArray.size(); i++) {
	        	JsonNode compartmentJson = jsonArray.get(i);
	        	SeedCompartmentEntity entity = objectMapper.readValue(compartmentJson.toString(), SeedCompartmentEntity.class);
	        	this.compartmentMap.put(entity.getUuid(), entity);
	        }
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addAliaseSet(SeedAliaseSet aliaseSet) {
		String attribute = aliaseSet.getAttribute();
		String source = aliaseSet.getSource();
		if (!aliaseSetMap.containsKey(attribute)) {
			aliaseSetMap.put(attribute, new HashMap<String, SeedAliaseSet> ());
		}
		
		Map<String, SeedAliaseSet> map = aliaseSetMap.get(aliaseSet.getAttribute());
		map.put(source, aliaseSet);
		LOGGER.debug(String.format("Added aliase set %s %s", attribute, source));
	}
	
	public Set<String> getAliases(String attribute, String source, String uuid) {
		if (!this.aliaseSetMap.containsKey(attribute)) {
			System.out.println("Attribute " + attribute + " not found");
			return null;
		}
		
		Map<String, SeedAliaseSet> sourceMap = this.aliaseSetMap.get(attribute);
		if (!sourceMap.containsKey(source)) {
			System.out.println("Source " + source + " not found");
			return null;
		}
		return sourceMap.get(source).uuidToAliase.get(uuid);
	}
	
	@Override
	public SeedReactionEntity getReactionById(Long id) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public SeedReactionEntity getReactionByEntry(String entry) {
		SeedReactionEntity rxn = null;
		try {
			rxn = objectMapper.readValue(new File(directory + "/reactions/" + entry + ".json"), SeedReactionEntity.class);
			String uuid = rxn.getUuid();
			String entry_ = getSeedReactionEntryFromUuid(uuid);
			if (entry_ == null) entry_ = entry;
			rxn.setEntry(entry_);
			
			Set<String> compartments = new HashSet<> ();
			for (SeedReactionReagentEntity reagent : rxn.getReagents()) {
				
				//assume 
				//coefficient: < 0 -> left AND > 0 right
				String cpdEntry = this.getSeedCompoundEntryFromUuid(reagent.getCompound_uuid());
				if (cpdEntry == null) {
					String msg = String.format("Reaction:%s - Unable to fetch reagent entry. UUID: %s", entry_, reagent.getCompound_uuid());
					rxn = null;
					throw new JsonParseException(msg, null);
				}
				reagent.setCpdEntry(cpdEntry);
				
				double coefficient = reagent.getCoefficient();
				reagent.setStoichiometry(Math.abs(coefficient));
				if (coefficient < 0) {
					rxn.getLeftStoichiometry().put(cpdEntry, (double) coefficient);
				} else if (coefficient > 0) {
					rxn.getRightStoichiometry().put(cpdEntry, (double) coefficient);
				} else {
					LOGGER.error(String.format("%s reagent: %s has invalid coefficient value %d", entry_, reagent, coefficient));
				}
				SeedCompartmentEntity compartment = this.compartmentMap.get(reagent.getDestinationCompartment_uuid());
				String cmpEntry = compartment.getId();
				String cmpName = compartment.getName();
				reagent.setCmpEntry(cmpEntry);
				reagent.setCmpName(cmpName);
				compartments.add(cmpEntry);
			}
			if (compartments.size() == 1) {
				rxn.setTranslocation(false);
			} else if (compartments.size() > 1){
				rxn.setTranslocation(true);
			} else {
				rxn.setTranslocation(null);
				LOGGER.warn(String.format("%s invalid comparment set: %s", entry_, compartments));
			}
			//link to enzyme class
			LOGGER.debug("Setup enzyme class");
			for (String enzymeAliasSet : ENZYME_CLASS_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_REACTION_ATTRIBUTE).get(enzymeAliasSet).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_REACTION_ATTRIBUTE, enzymeAliasSet, aliases));
				for (String enzyme : aliases) rxn.getEnzymeClass().add(enzyme);
			}
			//links to names
			Set<String> names = new HashSet<> ();
			LOGGER.debug("Setup alternative names");
			for (String nameAliasSet : NAME_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_REACTION_ATTRIBUTE).get(nameAliasSet).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_REACTION_ATTRIBUTE, nameAliasSet, aliases));
				for (String nameAlias : aliases) names.add(nameAlias.toLowerCase());
			}
			rxn.getSynonyms().addAll(names);
			List<SeedReactionCrossreferenceEntity> xrefs = new ArrayList<> ();
			//links to models
			LOGGER.debug("Setup metabolic model references");
			for (String modelAliasSet : MODEL_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_REACTION_ATTRIBUTE).get(modelAliasSet).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_REACTION_ATTRIBUTE, modelAliasSet, aliases));
				for (String alias : aliases) {
					SeedReactionCrossreferenceEntity xref = new SeedReactionCrossreferenceEntity(GenericCrossReference.Type.MODEL, modelAliasSet, alias);
					xrefs.add(xref);
				}
			}
			//links to xrefs
			LOGGER.debug("Setup database crossreferences");
			for (String databaseAlias : DATABASE_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_REACTION_ATTRIBUTE).get(databaseAlias).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_REACTION_ATTRIBUTE, databaseAlias, aliases));
				for (String alias : aliases) {
					SeedReactionCrossreferenceEntity xref = new SeedReactionCrossreferenceEntity(GenericCrossReference.Type.DATABASE, "KEGG", alias);
					xrefs.add(xref);
				}
			}
			rxn.setCrossReferences(xrefs);
			
//			for (String att : this.aliaseSetMap.keySet()) {
//				for (String src : this.aliaseSetMap.get(att).keySet()) {
//					Set<String> aliases = this.aliaseSetMap.get(att).get(src).uuidToAliase.get(uuid);
//					if (aliases != null && !aliases.isEmpty())
//						System.out.println(String.format("%s:%s - %s", att, src, aliases));
//				}
//			}
			
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Parse exception: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IO exception: " + e.getMessage());
		}
		return rxn;
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
		Set<String> entries = new HashSet<> ();
		File actual = new File(directory + "/reactions");
        for(File f : actual.listFiles()){
        	String entry = f.getName().replace(".json", "");
        	entries.add(entry);
        }
		return entries;
	}
	
	@Deprecated
	private void notsurewhattocall() {
		this.reactionMap.clear();
		
		JsonNode reactions = rootNode.get("reactions");
		for (int i = 0; i < reactions.size(); i++ ) {
			
			LOGGER.debug("Reading record: " + i);
			
			try {
				SeedReactionEntity rxn = this.parseJsonSeedCompound(reactions.get(i));
				
				List<SeedReactionCrossreferenceEntity> xrefs = new ArrayList<> ();
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
								xrefs.add(new SeedReactionCrossreferenceEntity(xref));
								break;
							case DATABASE:
								xrefs.add(new SeedReactionCrossreferenceEntity(xref));
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
	
	@Deprecated
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

	@Deprecated
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

	private String getSeedReactionEntryFromUuid(String uuid) {
		Set<String> entries = this.aliaseSetMap.get(ALIAS_REACTION_ATTRIBUTE).get(SEED_ENTRY_ALIAS).uuidToAliase.get(uuid);
		if (entries == null) {
			LOGGER.debug("Entry not found for uuid: " + uuid);
			return null;
		}
		
		if (entries.isEmpty() || entries.size() > 1) {
			LOGGER.debug("Invalid entry set uuid: " + entries);
			return null;
		}
		
		return entries.iterator().next();
	}
	
	private String getSeedCompoundEntryFromUuid(String uuid) {
		Set<String> entries = this.aliaseSetMap.get(ALIAS_COMPOUND_ATTRIBUTE).get(SEED_ENTRY_ALIAS).uuidToAliase.get(uuid);
		if (entries == null) {
			LOGGER.debug("Entry not found for uuid: " + uuid);
			return null;
		}
		
		if (entries.isEmpty() || entries.size() > 1) {
			LOGGER.debug("Invalid entry set uuid: " + entries);
			return null;
		}
		
		return entries.iterator().next();
	}
	
	private SeedReactionEntity parseJsonSeedCompound(JsonNode node) 
			throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
//		node.
		return mapper.readValue(node.toString(), SeedReactionEntity.class);
	}
}
