package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedAliaseSet;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSeedMetaboliteDaoImpl implements MetaboliteDao<SeedMetaboliteEntity>{

	private static Logger LOGGER = LoggerFactory.getLogger(JsonSeedMetaboliteDaoImpl.class);
	private static String ALIAS_ATTRIBUTE = "compounds";
	private static String SEED_ENTRY_ALIAS = "ModelSEED";
	
	private static String[] NAME_ALIAS = {"searchname", "name"};
	private static String[] MODEL_ALIAS = {"iAbaylyiv4", "iAF1260", "iAF692", "iAG612", "iAO358", "iGT196", "iIN800", "iIT341", "iJN746", "iJR904", "iMEO21", "iMM904", "iMO1056", "iND750", "iNJ661", "iPS189", "iSB619", "iSO783", "iYO844"};
	private static String[] DATABASE_ALIAS = {"KEGG"};
	
	public static int INITIAL_GEN_KEY = 0;
	
	private String directory;
	
	private Resource jsonFile;
	
	private JsonNode rootNode;
	private Map<String, List<GenericCrossreference>> refMap = new HashMap<> ();
	private Map<String, SeedMetaboliteEntity> metaboliteMap = new HashMap<> ();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private Map<String, Map<String, SeedAliaseSet>> aliaseSetMap = new HashMap<> ();
	
	@Autowired
	public JsonSeedMetaboliteDaoImpl(Resource jsonFile) {
		this.jsonFile = jsonFile;
		
		initialize();
	}
	
	public JsonSeedMetaboliteDaoImpl(String directory) {
		this.directory = directory;
		try {
			File actual = new File(directory + "/aliasSets");
	        for(File f : actual.listFiles()){
	            SeedAliaseSet seedAliaseSet = objectMapper.readValue(f, SeedAliaseSet.class);
	            this.addAliaseSet(seedAliaseSet);
	        }
		} catch (JsonParseException | JsonMappingException e) {
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
	
	public Resource getJsonFile() { return jsonFile;}
	public void setJsonFile(Resource jsonFile) { this.jsonFile = jsonFile;}
	
	
	@Deprecated
	private void buildXRefMap(JsonNode node, ReferenceType type, Map<String, List<GenericCrossreference>> map, String ref) {
		Iterator<String> fields = node.fieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			JsonNode uuid_array = node.get(field);
			for (int i = 0; i < uuid_array.size(); i++) {
				String uuid = uuid_array.get(i).asText();
//				System.out.println(uuid + " -> " + field.trim());
				GenericCrossreference xref = new GenericCrossreference(type, ref, field.trim());
				if (!map.containsKey(uuid)) {
					List<GenericCrossreference> xrefs = new ArrayList<> ();
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
	
	@Deprecated
	public SeedMetaboliteEntity find(Serializable id) {
		for (SeedMetaboliteEntity cpd : this.findAll()) {
			if (cpd.getEntry().equals(id)) return cpd;
		}
		return null;
	}

	@Deprecated
	public List<SeedMetaboliteEntity> findAll() {
		this.metaboliteMap.clear();
		List<SeedMetaboliteEntity> res = new ArrayList<> ();
		JsonNode compounds = rootNode.get("compounds");
		for (int i = 0; i < compounds.size(); i++ ) {
			try {
				SeedMetaboliteEntity cpd = this.parseJsonSeedCompound(compounds.get(i));
				
				List<SeedMetaboliteCrossreferenceEntity> xrefs = new ArrayList<> ();
				if (refMap.containsKey(cpd.getUuid())) {
//					System.out.println(refMap.get(cpd.getUuid()));
					for (GenericCrossreference xref : refMap.get(cpd.getUuid())) {
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
								xrefs.add(new SeedMetaboliteCrossreferenceEntity(xref));
								break;
							case DATABASE:
								xrefs.add(new SeedMetaboliteCrossreferenceEntity(xref));
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

	@Deprecated
	public void initialize() {
		ObjectMapper m = new ObjectMapper();
		
		try {
			rootNode = m.readTree(jsonFile.getInputStream());
			
			LOGGER.debug("Loading 'aliasSets'");
			
			for (int i = 0; i < rootNode.get("aliasSets").size(); i++) {
				LOGGER.debug("Index - " + i);
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
								ReferenceType.MODEL, refMap, name);
					} else 
					if (name.equals("ModelSEED")) {
						System.out.println("Building Refs for compound entries - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								ReferenceType.SELF, refMap, "SEED");
					} else
					if (name.equals("name")) {
						System.out.println("Building Refs for compound synonyms - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								ReferenceType.NAME, refMap, "SYNONYM");
					} else
					if (name.equals("KEGG")) {
						System.out.println("Building Refs for compound KEGG XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								ReferenceType.DATABASE, refMap, "KEGG");
					} else
					if (name.equals("AraCyc.45632")) {
						System.out.println("Building Refs for compound BioCyc ARA XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								ReferenceType.DATABASE, refMap, "BIOCYC:ARA");
					} else
					if (name.equals("MaizeCyc.45632")) {
						System.out.println("Building Refs for compound PlantCyc maize XREF - " + name + " for " + attribute);
						buildXRefMap(rootNode.get("aliasSets").get(i).get("aliases"), 
								ReferenceType.DATABASE, refMap, "PLANTCYC:MAIZE");
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
//			buildXRefMap(rootNode.get("aliasSets").get(2) .get("aliases"), GenericCrossReference.Type.ECNUMBER, refMap, "ECNUMBER");
//			buildXRefMap(rootNode.get("aliasSets").get(1) .get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "SEED");
//			buildXRefMap(rootNode.get("aliasSets").get(0) .get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "SEED");
//			buildXRefMap(rootNode.get("aliasSets").get(22).get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "KEGG");
//			buildXRefMap(rootNode.get("aliasSets").get(52).get("aliases"), GenericCrossReference.Type.DATABASE, refMap, "KEGG");
//			buildXRefMap(rootNode.get("aliasSets").get(4).get("aliases"), GenericCrossReference.Type.NAME, refMap, "NAME");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	@Deprecated
	public SeedMetaboliteEntity parseJsonSeedCompound(JsonNode node) 
			throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(node.toString(), SeedMetaboliteEntity.class);
	}
	
	@Override
	public List<Serializable> getAllMetaboliteIds() {
		throw new RuntimeException("Unsupported Operation");
	}
	
	@Override
	public SeedMetaboliteEntity getMetaboliteById(Serializable id) {
		throw new RuntimeException("Unsupported operation");
	}
	
	@Override
	public SeedMetaboliteEntity saveMetabolite(
			SeedMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}
	
	@Override
	public Serializable saveMetabolite(Object entity) {
		throw new RuntimeException("Unsupported Operation");
	}
	
	private String getSeedEntryFromUuid(String uuid) {
		Set<String> entries = this.aliaseSetMap.get(ALIAS_ATTRIBUTE).get(SEED_ENTRY_ALIAS).uuidToAliase.get(uuid);
		if (entries == null) {
			return null;
		}
		
		if (entries.isEmpty() || entries.size() > 1) {
			return null;
		}
		
		return entries.iterator().next();
	}
	
	@Override
	public SeedMetaboliteEntity getMetaboliteByEntry(String entry) {
		SeedMetaboliteEntity cpd = null;
		try {
			cpd = objectMapper.readValue(new File(directory + "/compounds/" + entry + ".json"), SeedMetaboliteEntity.class);
			String uuid = cpd.getUuid();
			String entry_ = getSeedEntryFromUuid(uuid);
			if (entry_ == null) entry_ = entry;
			cpd.setEntry(entry_);
			List<SeedMetaboliteCrossreferenceEntity> xrefs = new ArrayList<> ();
			//links to names
			Set<String> names = new HashSet<> ();
			LOGGER.debug("Setup alternative names");
			for (String nameAliasSet : NAME_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_ATTRIBUTE).get(nameAliasSet).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_ATTRIBUTE, nameAliasSet, aliases));
				for (String nameAlias : aliases) names.add(nameAlias.toLowerCase());
			}
			cpd.getSynonyms().addAll(names);
			//links to models
			LOGGER.debug("Setup metabolic model references");
			for (String modelAliasSet : MODEL_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_ATTRIBUTE).get(modelAliasSet).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_ATTRIBUTE, modelAliasSet, aliases));
				for (String alias : aliases) {
					SeedMetaboliteCrossreferenceEntity xref = new SeedMetaboliteCrossreferenceEntity(ReferenceType.MODEL, modelAliasSet, alias);
					xrefs.add(xref);
				}
			}
			//links to xrefs
			LOGGER.debug("Setup database crossreferences");
			for (String databaseAlias : DATABASE_ALIAS) {
				Set<String> aliases = this.aliaseSetMap.get(ALIAS_ATTRIBUTE).get(databaseAlias).uuidToAliase.get(uuid);
				if (aliases == null) aliases = new HashSet<> ();
				LOGGER.trace(String.format("%s:%s - %s", ALIAS_ATTRIBUTE, databaseAlias, aliases));
				for (String alias : aliases) {
					SeedMetaboliteCrossreferenceEntity xref = new SeedMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "KEGG", alias);
					xrefs.add(xref);
				}
			}
			cpd.setCrossReferences(xrefs);
			
//			for (String att : this.aliaseSetMap.keySet()) {
//				for (String src : this.aliaseSetMap.get(att).keySet()) {
//					Set<String> aliases = this.aliaseSetMap.get(att).get(src).uuidToAliase.get(uuid);
//					if (aliases != null && !aliases.isEmpty())
//						System.out.println(String.format("%s:%s - %s", att, src, aliases));
//				}
//			}
		} catch (FileNotFoundException e) {
			LOGGER.debug("Metabolite " + entry + " does not exists: " + e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return cpd;
	}
	
	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> entries = new ArrayList<> ();
		File actual = new File(directory + "/compounds");
        for(File f : actual.listFiles()){
        	String entry = f.getName().replace(".json", "");
        	entries.add(entry);
        }
		return entries;
	}
}
