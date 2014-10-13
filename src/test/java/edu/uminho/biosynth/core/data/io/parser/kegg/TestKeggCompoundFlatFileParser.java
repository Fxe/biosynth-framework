package edu.uminho.biosynth.core.data.io.parser.kegg;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggCompoundFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;

public class TestKeggCompoundFlatFileParser {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testC00007() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C00007"));
		
		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( parser.getEntry());
		cpd.setName( parser.getName());
		cpd.setMass( parser.getMass());
		cpd.setMolWeight( parser.getMolWeight());
		cpd.setFormula( parser.getFormula());
		cpd.setRemark( parser.getRemark());
		cpd.setComment( parser.getComment());
		cpd.setEnzymes( parser.getEnzymes());
		cpd.setReactions( parser.getReactions());
		cpd.setPathways( parser.getPathways());
		cpd.setCrossReferences( parser.getCrossReferences());
		
		assertEquals( 11, cpd.getPathways().size());
	}
	
	@Test
	public void testC00755() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C00755"));

		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( parser.getEntry());
		cpd.setName( parser.getName());
		cpd.setMass( parser.getMass());
		cpd.setMolWeight( parser.getMolWeight());
		cpd.setFormula( parser.getFormula());
		cpd.setRemark( parser.getRemark());
		cpd.setComment( parser.getComment());
		cpd.setEnzymes( parser.getEnzymes());
		cpd.setReactions( parser.getReactions());
		cpd.setPathways( parser.getPathways());
		cpd.setCrossReferences( parser.getCrossReferences());

		assertEquals( 7, cpd.getPathways().size());
	}
	
	@Test
	public void testC01245() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C01245"));

		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( parser.getEntry());
		cpd.setName( parser.getName());
		cpd.setMass( parser.getMass());
		cpd.setMolWeight( parser.getMolWeight());
		cpd.setFormula( parser.getFormula());
		cpd.setRemark( parser.getRemark());
		cpd.setComment( parser.getComment());
		cpd.setEnzymes( parser.getEnzymes());
		cpd.setReactions( parser.getReactions());
		cpd.setPathways( parser.getPathways());
		cpd.setCrossReferences( parser.getCrossReferences());
		
		assertEquals( "C6H15O15P3", cpd.getFormula());
	}

}
