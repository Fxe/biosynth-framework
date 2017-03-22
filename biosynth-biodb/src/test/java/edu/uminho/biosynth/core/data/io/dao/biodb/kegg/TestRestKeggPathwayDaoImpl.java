package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggPathwayEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggPathwaysDaoImpl;

public class TestRestKeggPathwayDaoImpl {
	
	protected static String folder = "/home/rafael/Documents/work/publicDatabases/Kegg/crawler";
	protected static RestKeggPathwaysDaoImpl rest;
	protected static String[] paths = new String[]{
			"bte00010",
			"bte00020",
			"bte00030",
			"bte00190",
			"bte00910",
			"bte00250",
			"bte00330",
			"bte00270",
			"bte00260",
			"bte00340",
			"bte00290",
			"bte00300",
			"bte00400",
			"bte00071",
			"bte00061"
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		rest = new RestKeggPathwaysDaoImpl();
//		rest.setLocalStorage(folder);
//		rest.setSaveLocalStorage(true);
//		rest.setUseLocalStorage(true);
//		rest.createFolder();
	}
	
//	@Test
	public void test1(){
		KeggPathwayEntity p = rest.getEntry("bte00330");
		System.out.println(p.toString());
	}
	
//	@Test
	public void testAllEntries(){
		Set<String> ps = rest.getAllEntries();
		int i=0, total=ps.size();
		Set<String> pathsWithStepsWithNoEc = new TreeSet<>();
		Set<String> pathsWithStepsWithNoKog = new TreeSet<>();
		for(String p : ps)
		{
			KeggPathwayEntity path = rest.getEntry(p);
			System.out.println(++i + " / " + total + " " + path.getEntry());
			
			Map<String, Set<String>> geneStepEcs = path.getGeneStepEcs();
			Map<String, Set<String>> geneStepOrths = path.getGeneStepOrthologs();
			
			if(geneStepEcs!=null)
				for(String gene : geneStepEcs.keySet())
					if(!geneStepOrths.containsKey(gene))
						pathsWithStepsWithNoKog.add(p);
			
			if(geneStepOrths!=null)
				for(String gene : geneStepOrths.keySet())
					if(!geneStepEcs.containsKey(gene))
						pathsWithStepsWithNoEc.add(p);
		}
		
		System.out.println("\n\n\npathsWithStepsWithNoEc");
		for(String p : pathsWithStepsWithNoEc)
			System.out.println(p);
		System.out.println(pathsWithStepsWithNoEc.size());
		
		System.out.println("\n\n\npathsWithStepsWithNoKog");
		for(String p : pathsWithStepsWithNoKog)
			System.out.println(p);
		System.out.println(pathsWithStepsWithNoKog.size());
		
		
		System.out.println("\n\n\n\n\n\n");
		for(String p : ps)
			System.out.println(p);
	}
	
	
//	@Test
	public void testPaths(){
		int i=0, total=paths.length;
		Set<String> pathsWithStepsWithNoEc = new TreeSet<>();
		Set<String> pathsWithStepsWithNoKog = new TreeSet<>();
		
		for(String p : paths)
		{
			KeggPathwayEntity path = rest.getEntry(p);
			System.out.println(++i + " / " + total + " " + path.getEntry());
			
			Map<String, Set<String>> geneStepEcs = path.getGeneStepEcs();
			Map<String, Set<String>> geneStepOrths = path.getGeneStepOrthologs();
			
			if(geneStepEcs!=null)
				for(String gene : geneStepEcs.keySet())
					if(!geneStepOrths.containsKey(gene))
						pathsWithStepsWithNoKog.add(p);
			
			if(geneStepOrths!=null)
				for(String gene : geneStepOrths.keySet())
					if(!geneStepEcs.containsKey(gene))
						pathsWithStepsWithNoEc.add(p);
			
			
			System.out.println(path);
		}
		
		System.out.println("\n\n\npathsWithStepsWithNoEc");
		for(String p : pathsWithStepsWithNoEc)
			System.out.println(p);
		System.out.println(pathsWithStepsWithNoEc.size());
		
		System.out.println("\n\n\npathsWithStepsWithNoKog");
		for(String p : pathsWithStepsWithNoKog)
			System.out.println(p);
		System.out.println(pathsWithStepsWithNoKog.size());
		
	}
	
	
	
	
	
}
