package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggPathwayEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenericEntityFlatFileParser;

public class RestKeggPathwaysDaoImpl
extends AbstractRestfulKeggDao {

	public static boolean DELAY_ON_IO_ERROR = false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestKeggPathwaysDaoImpl.class);
	private static final String restPathwayQuery = "http://rest.kegg.jp/get/%s";
	
	

	public KeggPathwayEntity getEntry(String entry) {
		String restPathsQuery = String.format(RestKeggPathwaysDaoImpl.restPathwayQuery, entry);
		String localPath = getPathFolder() + entry ;
		KeggPathwayEntity p = null;
		
		try {
			LOGGER.info(restPathsQuery);
			LOGGER.info(localPath);
			String pathFlatFile = this.getLocalOrWeb(restPathsQuery, localPath + ".txt");
			p = KeggGenericEntityFlatFileParser.parse(KeggPathwayEntity.class, pathFlatFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return p;
	}

	public Set<String> getAllEntries() {
		Set<String> rnIds = new HashSet<>();
		String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "path");
		String localPath = this.getLocalStorage() + "query" + "/pathways.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				rnIds.add(values[0].substring(5));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return rnIds;
	}
	
	public String getPathFolder(){
		return this.getLocalStorage()  + "pathways" + "/";
	}
	
	public void createFolder(){
		File f = new File(getPathFolder());
		f.mkdirs();
	}
}
