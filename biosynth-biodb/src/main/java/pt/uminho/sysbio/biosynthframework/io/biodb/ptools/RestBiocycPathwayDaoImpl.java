package pt.uminho.sysbio.biosynthframework.io.biodb.ptools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.AbstractRestfullBiocycDao;

public class RestBiocycPathwayDaoImpl extends AbstractRestfullBiocycDao {
  
  private static Logger logger = LoggerFactory.getLogger(RestBiocycPathwayDaoImpl.class);
  
  public Object getEntry(String entry) {
    if (entry.startsWith(pgdb)) {
      entry = entry.replaceFirst(pgdb.concat(":"), "");
    }

    String restCpdQuery = String.format(xmlGet, pgdb, entry);
    Object o = null;

    logger.debug(String.format("Query: %s", restCpdQuery));
    try {
      String pathEntry = String.format("%s.xml", entry.replaceAll(":", "_"));
      String localPath = getPath("pathway", pathEntry);
      String xmlDoc = null;
      logger.debug(String.format("Local Path: %s", localPath));
//      URL url = new URL("https://biocyc.org/getxml?META:CPD-882");
//      InputStream is = url.openConnection().getInputStream();
//      List<String> lines = IOUtils.readLines(is);
//      System.out.println(lines);
//      System.out.println(restCpdQuery);
      xmlDoc = this.getLocalOrWeb(restCpdQuery, localPath);
      
//      cpd = convert(xmlDoc, localPath);
//      cpd.setVersion(databaseVersion);
    } catch (IOException e) {
      logger.error(String.format("IO - %s", e.getMessage()));
      o = null;
    } catch (JSONException e) {
      logger.error(String.format("PARSE ERROR - %s", e.getMessage()));
      o = null;
    }
    
    return o;
  }
  
  public List<String> getAllEntries() {
    List<String> cpdEntryList = new ArrayList<>();
    try {
      String params = String.format("[x:x<-%s^^%s]", pgdb, "pathways");
      String restXmlQuery = String.format(xmlquery, URLEncoder.encode(params, "UTF-8"));
      String localPath = getPath("query", "pathway.xml");
//      String localPath = String.format("%s/%s/query/", this.getLocalStorage(), pgdb);
//      createFolderIfNotExists(localPath);
//      localPath = localPath.concat("compound.xml");
      String httpResponseString = getLocalOrWeb(restXmlQuery, localPath);
//      System.out.println(httpResponseString);
      JSONObject jsDoc = XML.toJSONObject(httpResponseString);
      JSONArray compoundJsArray = jsDoc.getJSONObject("ptools-xml").getJSONArray("Pathway");
      for (int i = 0; i < compoundJsArray.length(); i++) {
        String entry = compoundJsArray.getJSONObject(i).getString("frameid");
        //              if ( this.entryPrefix.length() > 0) {
        //                  entry = entryPrefix + ":" + entry;
        //              }
        cpdEntryList.add( entry);
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(String.format("UnsupportedEncodingException [%s]", e.getMessage()));
    } catch (JSONException e) {
      logger.error(String.format("JSONException [%s]", e.getMessage()));
    } catch (IOException e) {
      logger.error(String.format("IOException [%s]", e.getMessage()));
    }
    return cpdEntryList;
  }
}
