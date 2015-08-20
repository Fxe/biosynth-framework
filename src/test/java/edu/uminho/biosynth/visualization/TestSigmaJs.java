package edu.uminho.biosynth.visualization;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSigmaJs {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	@Test
	public void test() throws JsonGenerationException, JsonMappingException, IOException {
		String jsonGraph = "{ \"nodes\": [ { \"id\": \"n0\", \"label\": \"A node\", \"x\": 0, \"y\": 0, \"size\": 3 }, { \"id\": \"n1\", \"label\": \"Another node\", \"x\": 3, \"y\": 1, \"size\": 2 }, { \"id\": \"n2\", \"label\": \"And a last one\", \"x\": 1, \"y\": 3, \"size\": 1 } ], \"edges\": [ { \"id\": \"e0\", \"source\": \"n0\", \"target\": \"n1\" }, { \"id\": \"e1\", \"source\": \"n1\", \"target\": \"n2\" }, { \"id\": \"e2\", \"source\": \"n2\", \"target\": \"n0\" } ] }";
		ObjectMapper mapper = new ObjectMapper();
		SigmaJsGraph graph = mapper.readValue(jsonGraph, SigmaJsGraph.class);
		
		System.out.println(graph);
	}
	
	@Test
	public void test2() throws JsonGenerationException, JsonMappingException, IOException {
		SigmaJsGraph graph = new SigmaJsGraph();
		SigmaJsNode node1 = new SigmaJsNode();
		node1.setId("n0");
		node1.setLabel("A node");
		node1.setX(0);
		node1.setY(0);
		node1.setSize(3);
		graph.getNodes().add(node1);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(graph);
		
		System.out.println(json);
	}
	
	@Test
	public void teste() throws JsonParseException, JsonMappingException, IOException {
		String s = "{META:ISOBUTYRYL-COA-MUTASE-RXN=9.99990000000001, META:ENZRXN-201-RXN=-9.99990000000001, META:1.2.1.25-RXN=9.99990000000001, META:BUTANAL-DEHYDROGENASE-RXN_NADP_NADPH=-9.99990000000001}";
		s = s.replaceAll("META:", "\"");
		s = s.replace("=", "\":");
		System.out.println(s);
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		HashMap<String,Double> graph = mapper.readValue(s, HashMap.class);
		System.out.println(graph);
		for (String reaction : graph.keySet()) {
			System.out.println(reaction);
			System.out.println(graph.get(reaction));
		}
	}

}
