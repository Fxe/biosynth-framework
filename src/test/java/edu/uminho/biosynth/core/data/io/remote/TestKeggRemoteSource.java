package edu.uminho.biosynth.core.data.io.remote;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;

public class TestKeggRemoteSource {

	private static KeggRemoteSource remote;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		remote = new KeggRemoteSource();
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
	
	private String keggMetaboliteEntityToString(KeggMetaboliteEntity cpd) {
		StringBuilder sb = new StringBuilder();
		sb.append("ID:\t\t").append(cpd.getId()).append('\n');
		sb.append("ENTRY:\t\t").append(cpd.getEntry()).append('\n');
		sb.append("NAME:\t\t").append(cpd.getName()).append('\n');
		sb.append("SOURCE:\t\t").append(cpd.getSource()).append('\n');
		sb.append("DESCRIPTION:\t").append(cpd.getDescription()).append('\n');
		sb.append("FORMULA:\t").append(cpd.getFormula()).append('\n');
		sb.append("CLASS:\t\t").append(cpd.getMetaboliteClass()).append('\n');
		sb.append("MASS:\t\t").append(cpd.getMass()).append('\n');
		sb.append("WEIGHT:\t\t").append(cpd.getMolWeight()).append('\n');
		sb.append("REMARK:\t\t").append(cpd.getRemark()).append('\n');
		sb.append("COMMENT:\t").append(cpd.getComment()).append('\n');
		sb.append("PATHWAYS:\t").append(cpd.getPathways()).append('\n');
		sb.append("REACTIONS:\t").append(cpd.getReactions()).append('\n');
		sb.append("CROSSREF:\t").append(cpd.getCrossReferences());
		return sb.toString();
	}

	//@Test
	public void testGetR00001() {
		KeggReactionEntity rxn = remote.getReactionInformation("R00001");
		System.out.println((rxn));
		assertEquals(true, rxn != null);
	}
	
	@Test
	public void testGetG10608() {
		KeggMetaboliteEntity cpd = remote.getMetaboliteInformation("G10608");
		System.out.println(keggMetaboliteEntityToString(cpd));
		assertEquals(true, cpd != null);
	}

	@Test
	public void testGetC00001() {
		KeggMetaboliteEntity cpd = remote.getMetaboliteInformation("C00001");
		System.out.println(keggMetaboliteEntityToString(cpd));
		cpd = remote.getMetaboliteInformation("C00755");
		System.out.println(keggMetaboliteEntityToString(cpd));
		cpd = remote.getMetaboliteInformation("G00100");
		System.out.println(keggMetaboliteEntityToString(cpd));
		assertEquals(true, cpd != null);
	}
}
