package edu.uminho.biosynth.core.data.io.http;

import java.io.File;
import java.io.IOException;

import edu.uminho.biosynth.util.BioSynthUtilsIO;

public class AbstractHttpLocalSaveDao {
	
//	private String getLocalOrWeb(String restQuery, String localPath) throws IOException {
//		String entryFlatFile = null;
//		
//		String baseDirectory = localFolder.trim().replaceAll("\\\\", "/");
//		if ( !baseDirectory.endsWith("/")) baseDirectory = baseDirectory.concat("/");
//		String dataFileStr = baseDirectory  + entityType + "/" + entry + ".txt";
//		File dataFile = new File(dataFileStr);
//		
//		System.out.println(dataFile);
//		if ( !dataFile.exists()) {
//			entryFlatFile =  HttpRequest.get(restQuery);
//			if (SAVETOCACHE) BioSynthUtilsIO.writeToFile(entryFlatFile, dataFileStr);
//		} else {
//			entryFlatFile = BioSynthUtilsIO.readFromFile(dataFileStr);
//		}
//		
//		return entryFlatFile;
//	}
}
