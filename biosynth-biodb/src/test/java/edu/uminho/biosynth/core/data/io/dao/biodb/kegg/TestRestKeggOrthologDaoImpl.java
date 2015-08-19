package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggKOEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;

public class TestRestKeggOrthologDaoImpl {
	
	protected static String folder = "/home/rafael/Documents/work/publicDatabases/Kegg/crawler";
	protected static RestKeggKOsDaoImpl rest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rest = new RestKeggKOsDaoImpl();
		rest.setLocalStorage(folder);
		rest.setSaveLocalStorage(true);
		rest.setUseLocalStorage(true);
		rest.createFolder();
	}
	
	@Test
	public void test1(){
		KeggKOEntity koEntity = rest.getKOByEntry("K12524");
		System.out.println(koEntity.getEntry());
		System.out.println("Pathways:");
		for(String g : koEntity.getPathways())
			System.out.println(g);
		System.out.println("Modules:");
		for(String g : koEntity.getModules())
			System.out.println(g);
		System.out.println("Genes:");
		for(String g : koEntity.getGenes())
			System.out.println(g);
	}

}
