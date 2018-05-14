package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.AbstractRestfulKeggDao;

public class RestKeggGeneDaoImpl extends AbstractRestfulKeggDao<AbstractBiosynthEntity> {
	
	private Map<String, Set<String>> geneToEcMap = new HashMap<> ();
	
	public Set<String> getEcNumber(String orgEntry, String geneEntry) {
		String entry = String.format("%s:%s", orgEntry, geneEntry);
		if (!geneToEcMap.containsKey(entry)) {
			geneToEcMap.putAll( this.getAllGeneIds(orgEntry));
		}
		
		return geneToEcMap.get(entry);
	}
	
	public Map<String, Set<String>> getAllGeneIds(String orgId) {
		Map<String, Set<String>> geneToEcMap = new HashMap<> ();
		String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", orgId);
		String localPath = String.format("%sgenes/%s_list.txt", this.getLocalStorage(), orgId);
		try {
			String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				geneToEcMap.put(values[0], extractEcNumbers(values[1]));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return geneToEcMap;
	}
	
	private Set<String> extractEcNumbers(String str) {
		Set<String> ecSet = new HashSet<> ();
		
		Pattern pattern = Pattern.compile("([0-9-]+\\.[0-9-]+\\.[0-9-]+\\.[0-9-]+)");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			ecSet.add(matcher.group(1));
		}
		
		return ecSet;
	}

  @Override
  public AbstractBiosynthEntity getByEntry(String e) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    // TODO Auto-generated method stub
    return null;
  }
}
