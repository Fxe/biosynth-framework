package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uminho.biosynth.visualization.builder.DotDigraphBuilder;

public class TestGraphVizGenerator {

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
	public void generateFromPathway() {
		GraphVizGenerator graphVizGenerator = new GraphVizGenerator();
		DotDigraphBuilder builder = new DotDigraphBuilder();
//		builder.ad
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws JsonParseException, JsonMappingException, IOException {
		//[META:RXN-11667, META:ENZRXN-201-RXN, META:RXN-11662, META:RXN-14193, META:BUTANAL-DEHYDROGENASE-RXN_NADP_NADPH, META:RXNI-2]
		String dist = "{META:ISOBUTYRYL-COA-MUTASE-RXN=9.99990000000001, META:ENZRXN-201-RXN=-9.99990000000001, META:1.2.1.25-RXN=9.99990000000001, META:BUTANAL-DEHYDROGENASE-RXN_NADP_NADPH=-9.99990000000001}";
		dist = dist.replaceAll("META:", "");
		dist = dist.replace("=", "\":");
		dist = dist.replace("_NADP_NADPH", "");
		dist = dist.replace(", ", ", \"");
		dist = dist.replace("{", "{\"");
		System.out.println(dist);
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,Double> fluxMap = null;
		
		fluxMap = mapper.readValue(dist, HashMap.class);
		
		System.out.println(fluxMap);
		
		String svgStr = GraphVizGenerator.generateSvg("");
		System.out.println(svgStr);
		assertNotNull(svgStr);
	}
	
	@Test
	public void testClean() {
		String dist = "{BUTANAL-DEHYDROGENASE-RXN=1, A=4}";
		dist = dist.replaceAll("META:", "");
		dist = dist.replace("=", "\":");
		dist = dist.replace("_NADP_NADPH", "");
		dist = dist.replace("_NAD_NADH", "");
		dist = dist.replace(", ", ", \"");
		dist = dist.replace("{", "{\"");
		System.out.println(dist);
	}

}
