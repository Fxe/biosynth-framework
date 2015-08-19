package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggModuleEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggModuleDaoImpl;

public class TestRestKeggModuleDaoImpl {
	
	protected static String folder = "/home/rafael/Documents/work/publicDatabases/Kegg/crawler";
	protected static RestKeggModuleDaoImpl rest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rest = new RestKeggModuleDaoImpl();
		rest.setLocalStorage(folder);
		rest.setSaveLocalStorage(true);
		rest.setUseLocalStorage(true);
		rest.createFolder();
	}
	
	@Test
	public void test1(){
		KeggModuleEntity moduleEntity = rest.getModuleByEntry("M00016");
		System.out.println(moduleEntity.getEntry());
		System.out.println("Paths:");
		for(String e : moduleEntity.getPathways())
			System.out.println(e);
		System.out.println("Orthologs:");
		for(String e : moduleEntity.getOrthologs())
			System.out.println(e);
		System.out.println("Compounds:");
		for(String e : moduleEntity.getCompounds())
			System.out.println(e);
		System.out.println("Reactions:");
		for(String e : moduleEntity.getReactions())
			System.out.println(e);
	}

}
