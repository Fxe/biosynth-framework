package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

@SuppressWarnings("unused")
public class TestJsonSeedReactionDaoImpl {

	private static JsonSeedReactionDaoImpl jsonSeedReactionDaoImpl;
	private static String FOLDER = "D:/home/data/seed/maps/";
	
	private static void buildCompound() throws JsonProcessingException, IOException   {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode A= objectMapper.readTree(new File("F:/data/seed/aliasSets.json"));
		
		for (int i = 0; i < A.size(); i++) {
			Map<String, String> mapping = new HashMap<> ();
			
			
			System.out.println("=========" + i + " =========");
			JsonNode B = A.get(i);
			Iterator<String> b = B.fieldNames();
			while (b.hasNext()) {
				String field = b.next();
				Object data = B.get(field);
				System.out.println(field + " : " + StringUtils.abbreviate(data.toString(), 100));
			}
			
			String fileName = B.get("").asText();
			JsonNode aliases = B.get("aliases");
			Iterator<String> alias = aliases.fieldNames();
			while (alias.hasNext()) {
				String key = alias.next();
				JsonNode array = aliases.get(key);
				if (array.size() > 1 || array.size() < 1) System.out.println("@@@@@@@@@@@@@@@@@@@@@: " + array.size());
				String value = array.iterator().next().asText();
				System.out.println(key + " : " + value);
				mapping.put(key, value);
			}
			
			System.out.println("#####################################");
			String file = String.format("%s/%s.json", FOLDER, fileName);
			objectMapper.writeValue(new File(file), mapping);
		}
		
//		Map<String, String> query = new HashMap<> ();
//		for (int i = 0; i < A.size(); i++) {
//			JsonNode b = A.get(i);
//			System.out.println(b);
//			String uuid = b.get("uuid").asText();
//			
//			query.put(uuid, b.get("name").asText());
////			BioSynthUtilsIO.writeToFile(b.toString(), "D:/home/data/seed/cpd/" + uuid + ".json");
//			break;
//		}
		
		System.out.println(A.size());
		Iterator<String> a = A.fieldNames();
		
		while (a.hasNext()) {
			System.out.println(a.next());
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		buildCompound();
//		objectMapper.writeValue(new File("D:/home/data/seed/query/compound.json"), query);

//		BioSynthUtilsIO.writeToFile(A.get("reactions").toString(), "F:/data/seed/reactions.json");
//		BioSynthUtilsIO.writeToFile(A.get("locked").toString(), "F:/data/seed/locked.json");
//		BioSynthUtilsIO.writeToFile(A.get("name").toString(), "F:/data/seed/name.json");
//		BioSynthUtilsIO.writeToFile(A.get("compounds").toString(), "F:/data/seed/compounds.json");
//		BioSynthUtilsIO.writeToFile(A.get("uuid").toString(), "F:/data/seed/uuid.json");
//		BioSynthUtilsIO.writeToFile(A.get("aliasSets").toString(), "F:/data/seed/aliasSets.json");
//		BioSynthUtilsIO.writeToFile(A.get("cues").toString(), "F:/data/seed/cues.json");
//		BioSynthUtilsIO.writeToFile(A.get("compartments").toString(), "F:/data/seed/compartments.json");
//		BioSynthUtilsIO.writeToFile(A.get("media").toString(), "F:/data/seed/media.json");


		
//		jsonSeedReactionDaoImpl = new JsonSeedReactionDaoImpl(new FileSystemResource("D:/home/data/seed/seed.json"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=RuntimeException.class)
	public void test_get_all_ids() {
		jsonSeedReactionDaoImpl.getAllReactionIds();
	}

	@Test
	public void test_get_all_entries() {
		Set<String> entrySet = jsonSeedReactionDaoImpl.getAllReactionEntries();
		
		System.out.println(entrySet);
		assertTrue(entrySet.contains("rxn00148"));
	}
	
	@Test
	public void test_get_rxn_00148() {
		SeedReactionEntity seedReactionEntity = jsonSeedReactionDaoImpl.getReactionByEntry("rxn00148");
		
		System.out.println(seedReactionEntity);
		
		assertEquals("rxn00148", seedReactionEntity.getEntry());
	}
}
