package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGeneEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenesDaoImpl;

public class TestRestKeggGeneDaoImpl {
	
	protected static String folder = "/home/rafael/Documents/work/publicDatabases/Kegg/crawler";
	protected static RestKeggGenesDaoImpl rest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		rest = new RestKeggGenesDaoImpl();
//		rest.setLocalStorage(folder);
//		rest.createFolder();
	}
	
	@Test
	public void test1(){
//		KeggGeneEntity geneEntity = rest.getGeneByEntry("reh:H16_A2182");
////		KeggGeneEntity geneEntity = rest.getGeneByEntry("eco:b3862");
//		
//		System.out.println("Entry: " + geneEntity.getEntry());
//		System.out.println("NUC SEQ:\n" + geneEntity.getNucleotidesSeq());
//		System.out.println("AA SEQ:\n" + geneEntity.getAminoacidsSeq());
//		System.out.println(geneEntity.getPropertyValues("DBLINKS"));
//		System.out.println("Modules:");
//		if(geneEntity.getModules()!=null)
//			for(String e : geneEntity.getModules())
//				System.out.println(e);
//		System.out.println("Orthology:");
//		if(geneEntity.getOrthologs()!=null)
//			for(String e : geneEntity.getOrthologs())
//				System.out.println(e);
//		System.out.println("Paths:");
//		if(geneEntity.getPathways()!=null)
//			for(String e : geneEntity.getPathways())
//				System.out.println(e);
//		System.out.println("ECs:");
//		if(geneEntity.getEcNumbers()!=null)
//			for(String e : geneEntity.getEcNumbers())
//				System.out.println(e);
	}

}
