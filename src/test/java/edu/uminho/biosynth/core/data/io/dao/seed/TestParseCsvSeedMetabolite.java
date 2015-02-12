package edu.uminho.biosynth.core.data.io.dao.seed;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.JsonSeedMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;

@SuppressWarnings("deprecation")
public class TestParseCsvSeedMetabolite {

	private static SessionFactory sessionFactory;
	private static JsonNode rootNode;
	private static Map<String, List<GenericCrossReference>> refMap;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("seed.cfg.xml");
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}
	


	public static void printJsonKeySet(JsonNode node) {
		Iterator<String> fields = node.fieldNames();
		System.out.println(node.size());

		while (fields.hasNext()) {
			System.out.println(fields.next());
		}
	}
	
	private static void printJsonValues(JsonNode node) {
		Iterator<String> fields = node.fieldNames();
		System.out.println(node.size());

		while (fields.hasNext()) {
			String field = fields.next();
			System.out.println(field + "\t" + node.get(field));
		}
	}
	

	
	public static SeedReactionEntity parseJsonSeedReaction(JsonNode node) 
			throws JsonMappingException, JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(node.toString(), SeedReactionEntity.class);
	}

	@Test
	public void testCompounds() {

		File csv = new File("D:/home/data/seed/seed.json");
		JsonSeedMetaboliteDaoImpl jsonSeedDao = new JsonSeedMetaboliteDaoImpl(new FileSystemResource(csv));
		
		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
		
//			printJsonValues(compounds.get(16983));
//			System.exit(0);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<SeedMetaboliteEntity> res = jsonSeedDao.findAll();
		int i = 0;
		for (SeedMetaboliteEntity cpd : res) {
			System.out.println(i);
			dao.save(cpd);
			if (i % 100 == 0) {
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
			i++;
		}
		tx.commit();

		assertEquals(true, true);
//		fail("Not yet implemented");
	}
	
	@Test
	public void testReactions() {
		try {
			IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
			printJsonKeySet(rootNode);
			
			JsonNode reactions = rootNode.get("reactions");
			
			printJsonValues(reactions.get(0));
			System.out.println(parseJsonSeedReaction(reactions.get(0)));
			Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
			for (int i = 0; i < reactions.size(); i++ ) {
				System.out.println(i);
				SeedReactionEntity reaction = parseJsonSeedReaction(reactions.get(i));
				List<SeedReactionCrossreferenceEntity> xrefs = new ArrayList<> ();
				for (GenericCrossReference xref : refMap.get(reaction.getUuid())) {
					xrefs.add(new SeedReactionCrossreferenceEntity(xref));
				}
				reaction.setCrossReferences(xrefs);
				dao.save(reaction);
				if (i % 100 == 0) {
					tx.commit();
					tx = sessionFactory.getCurrentSession().beginTransaction();
				}
			}
			tx.commit();
			
//			printJsonKeySet(reactions);
//			for (int i = 0; i < reactions.size(); i++) {
//				//D078E996-C209-11E1-9982-998743BA47CD
//				String uuid = reactions.get(i).get("uuid").asText();
//				if (uuid.equals("BD56C478-C209-11E1-9982-998743BA47CD")) {
//					printJsonValues(reactions.get(i));
//				}
//			}
//			printJsonValues(reactions.get(28));
			
		} catch (JsonProcessingException jpEx) {
			System.out.println(jpEx.getMessage());
		} catch (IOException ioEx) {
			System.out.println(ioEx.getMessage());
		}
		
		assertEquals(true, true);
//		fail("Not yet implemented");
	}
	
	
	public void testOthers() {
		ObjectMapper m = new ObjectMapper();
		try {
			JsonNode rootNode = m.readTree(new File("D:/seed.txt"));
			
			printJsonKeySet(rootNode);
			printJsonKeySet(rootNode.get("name"));
			printJsonKeySet(rootNode.get("aliasSets"));
//			for (int i = 0; i < rootNode.get("aliasSets").size(); i++) {
//				System.out.println(rootNode.get("aliasSets").get(i).get("source"));
//			}
			JsonNode modelSeed1 = rootNode.get("aliasSets").get(0);
			JsonNode modelSeed2 = rootNode.get("aliasSets").get(1);
//			printJsonValues(rootNode.get("aliasSets").get(0));
			System.out.println(modelSeed1.get("aliases").size());
			System.out.println(modelSeed2.get("aliases").size());
//			System.out.println(rootNode.get("aliasSets").get(2).get("aliases"));
//			System.out.println(rootNode.get("aliasSets").get(3).get("aliases").size());
//			System.out.println(rootNode.get("aliasSets").get(4).get("aliases").size());
//			System.out.println(rootNode.get("aliasSets").get(23).get("aliases"));
//			printJsonValues(rootNode.get("aliasSets").get(1));
//			printJsonValues(rootNode.get("aliasSets").get(50));
//			printJsonKeySet(rootNode.get("cues"));
//			printJsonValues(rootNode.get("cues").get(20));
			

			
			for (String uuid : refMap.keySet()) {
				System.out.println(uuid + " -> " + refMap.get(uuid));
			}
		} catch (JsonProcessingException jpEx) {
			System.out.println(jpEx.getMessage());
		} catch (IOException ioEx) {
			System.out.println(ioEx.getMessage());
		}
		
		assertEquals(true, true);
//		fail("Not yet implemented");
	}
	
	public void TestDate() {
		//2012-06-29T16:45:50
		DateTime d;
		d = new DateTime("2012-06-29T16:45:50");
		System.out.println(d);
		System.out.println(d.getYear());
		System.out.println(d.getMonthOfYear());
		System.out.println(d.getDayOfMonth());
		System.out.println(d.getHourOfDay());
		System.out.println(d.getMinuteOfHour());
		System.out.println(d.getSecondOfMinute());
	}
	
	public void TestAnalyseJson() throws Exception {
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("C:/Users/Filipe/Dropbox/workspace/data/seed/seed.txt")));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) sb.append(line);
		reader.close();
		System.out.println(sb.toString().getBytes().length);
		System.out.println(sb.substring(51380000, 51380900));
	}

}
