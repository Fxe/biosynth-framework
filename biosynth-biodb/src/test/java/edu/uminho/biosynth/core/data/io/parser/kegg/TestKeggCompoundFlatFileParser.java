package edu.uminho.biosynth.core.data.io.parser.kegg;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggCompoundFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.http.HttpRequest;

public class TestKeggCompoundFlatFileParser {

//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		
//	}
//
//	@Test
//	public void testC00007() {
//		String abc = null; 
//		try {
//			abc = HttpRequest.get("http://rest.kegg.jp/get/C00007");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		KeggCompoundFlatFileParser parser = 
//				new KeggCompoundFlatFileParser(abc);
//		
//		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
//		cpd.setEntry( parser.getEntry());
//		cpd.setName( parser.getName());
//		cpd.setMass( parser.getMass());
//		cpd.setMolWeight( parser.getMolWeight());
//		cpd.setFormula( parser.getFormula());
//		cpd.setRemark( parser.getRemark());
//		cpd.setComment( parser.getComment());
//		cpd.setEnzymes( parser.getEnzymes());
//		cpd.setReactions( parser.getReactions());
//		cpd.setPathways( parser.getPathways());
//		cpd.setCrossReferences( parser.getCrossReferences());
//		
//		assertEquals( 11, cpd.getPathways().size());
//	}
//	
//	@Test
//	public void testC00755() {
//		String abc = null; 
//		try {
//			abc = HttpRequest.get("http://rest.kegg.jp/get/C00755");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		KeggCompoundFlatFileParser parser = 
//				new KeggCompoundFlatFileParser(abc);
//
//		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
//		cpd.setEntry( parser.getEntry());
//		cpd.setName( parser.getName());
//		cpd.setMass( parser.getMass());
//		cpd.setMolWeight( parser.getMolWeight());
//		cpd.setFormula( parser.getFormula());
//		cpd.setRemark( parser.getRemark());
//		cpd.setComment( parser.getComment());
//		cpd.setEnzymes( parser.getEnzymes());
//		cpd.setReactions( parser.getReactions());
//		cpd.setPathways( parser.getPathways());
//		cpd.setCrossReferences( parser.getCrossReferences());
//
//		assertEquals( 7, cpd.getPathways().size());
//	}
//	
//	@Test
//	public void testC01245() {
//		String abc = null; 
//		try {
//			abc = HttpRequest.get("http://rest.kegg.jp/get/C01245");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		KeggCompoundFlatFileParser parser = 
//				new KeggCompoundFlatFileParser(abc);
//
//		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
//		cpd.setEntry( parser.getEntry());
//		cpd.setName( parser.getName());
//		cpd.setMass( parser.getMass());
//		cpd.setMolWeight( parser.getMolWeight());
//		cpd.setFormula( parser.getFormula());
//		cpd.setRemark( parser.getRemark());
//		cpd.setComment( parser.getComment());
//		cpd.setEnzymes( parser.getEnzymes());
//		cpd.setReactions( parser.getReactions());
//		cpd.setPathways( parser.getPathways());
//		cpd.setCrossReferences( parser.getCrossReferences());
//		
//		assertEquals( "C6H15O15P3", cpd.getFormula());
//	}

}
