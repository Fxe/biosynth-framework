package pt.uminho.sysbio.biosynthframework.biodb.kegg.crawller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggECNumberDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.biodb.kegg.RestKeggGenomeDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.biodb.kegg.RestKeggModuleDaoImpl;

import com.google.common.collect.Lists;

@Deprecated
public class KeggCrawller {

	
	public static Collection<Runnable> generateRunnable(String folder, int partition){
		
//		TODO: put this options in a file;
		boolean crawllgenomes = true;
		boolean crawllmodules = true;
		boolean crawllECs = true;
		boolean crawllKOs = true;
		
		
		Set<Runnable> runs = new LinkedHashSet<Runnable>();
		int i = 0;
		if(crawllgenomes){
			RestKeggGenomeDaoImpl rest = new RestKeggGenomeDaoImpl();
			rest.setLocalStorage(folder);
			rest.setSaveLocalStorage(true);
			rest.setUseLocalStorage(true);
//			rest.createFolder();
			Set<String> genomes = rest.getAllEntries();
			for(String genome : genomes){
				keggGenomeAndGenesRunnable crawler = new keggGenomeAndGenesRunnable(i,folder, genome);
				runs.add(crawler);
				i++;
			}
		}
		
		
		if(crawllKOs){
			RestKeggKOsDaoImpl rest = new RestKeggKOsDaoImpl();
			rest.setLocalStorage(folder);
			rest.setSaveLocalStorage(true);
			rest.setUseLocalStorage(true);
			rest.createFolder();
			
			Set<String> entries = rest.getAllKOEntries();
			List<List<String>> parts = Lists.partition(new ArrayList<String>(entries), entries.size()/partition);
			for(List<String> p : parts){
				KeggKORunnable crawler = new KeggKORunnable(folder, p);
				runs.add(crawler);
			}
		}
		
		if(crawllECs){
			RestKeggECNumberDaoImpl rest = new RestKeggECNumberDaoImpl();
			rest.setLocalStorage(folder);
			rest.setSaveLocalStorage(true);
			rest.setUseLocalStorage(true);
//			rest.createFolder();
			
			Set<String> entries = rest.getAllEntries();
			List<List<String>> parts = Lists.partition(new ArrayList<String>(entries), entries.size()/partition);
			for(List<String> p : parts){
				KeggECNumbersRunnable crawler = new KeggECNumbersRunnable(folder, p);
				runs.add(crawler);
			}
		}
		
		if(crawllmodules){
			RestKeggModuleDaoImpl rest = new RestKeggModuleDaoImpl();
			rest.setLocalStorage(folder);
			rest.setSaveLocalStorage(true);
			rest.setUseLocalStorage(true);
//			rest.createFolder();
			
			Set<String> entries = rest.getAllEntries();
			List<List<String>> parts = Lists.partition(new ArrayList<String>(entries), entries.size()/partition);
			for(List<String> p : parts){
				keggModuleRunnable crawler = new keggModuleRunnable(folder, p);
				runs.add(crawler);
			}
		}
		
		return runs;
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		if(args.length != 3){
			System.out.println("Argumentns:\n1- number of Threads\n2- number of repetitions\n3 - folder to collect info");
			return ;
		}
		
		
		int threads = Integer.parseInt(args[0]);
		int repetitions = Integer.parseInt(args[1]);
		String folder = args[2];
		new File(folder+"/query/").mkdirs();
		
		
		for(int i =0; i < repetitions; i++){
		
			ExecutorService executor = Executors.newFixedThreadPool(threads);
			
			Collection<Runnable> work = generateRunnable(folder, threads);
			
			List<Future<?>> list = new ArrayList<>();
			for(Runnable r : work){
				list.add(executor.submit(r));
			}
			
			
			executor.awaitTermination(30, TimeUnit.DAYS);
		}
	}
}
