package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGenomeEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenericEntityFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenomeFlatFileParser;

public class RestKeggGenomeDaoImpl
extends AbstractRestfulKeggDao {

	
	public static boolean DELAY_ON_IO_ERROR = false;
	private static final Logger LOGGER = LoggerFactory.getLogger(RestKeggGenomeDaoImpl.class);
	private static final String restRxnQuery = "http://rest.kegg.jp/get/gn:%s";
	


	public KeggGenomeEntity getGenomeByEntry(String entry) {
		String restRxnQuery = String.format(RestKeggGenomeDaoImpl.restRxnQuery, entry);
		
		String localPath = getPathFolder()+ entry;
		KeggGenomeEntity genome = new KeggGenomeEntity();
		String rnFlatFile = null; 
		try {
			LOGGER.debug(restRxnQuery);
			LOGGER.debug(localPath);
			rnFlatFile = this.getLocalOrWeb(restRxnQuery, localPath +".txt");
			
			LOGGER.debug("{}", rnFlatFile.getBytes().length);
			
			KeggGenomeFlatFileParser parser = new KeggGenomeFlatFileParser(rnFlatFile);
			genome.setEntry(parser.getEntry());
			genome.setDefinition(parser.getDefinition());
			genome.setTaxonomy(parser.getTaxonomy());
			
		} catch (IOException e) {
//			String genomeFlatFile = this.getLocalOrWeb(restRxnQuery, localPath +".txt");
//			genome = KeggGenericEntityFlatFileParser.parse(KeggGenomeEntity.class, genomeFlatFile);
		} catch (Exception e) {

			LOGGER.error(e.getMessage());
		}
		return genome;
	}


	public Set<String> getAllGenomeEntries() {
		Set<String> rnIds = new HashSet<>();
		String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "genome");
		String localPath = this.getLocalStorage() + "query" + "/genome.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				rnIds.add(values[0].substring(7));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return rnIds;
	}

	public String getPathFolder(){
		return this.getLocalStorage()  + "genome" + "/";
	}
	
	public void createFolder(){
		File f = new File(getPathFolder());
		f.mkdirs();
	}
	
	
	public static void main(String[] args) {
		RestKeggGenomeDaoImpl rest = new RestKeggGenomeDaoImpl();
		rest.getGenomeByEntry("T02080");
		System.out.println(rest.getAllGenomeEntries());
		
	}
}
