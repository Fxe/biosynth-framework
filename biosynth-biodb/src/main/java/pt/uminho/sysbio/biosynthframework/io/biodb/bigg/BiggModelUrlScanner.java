package pt.uminho.sysbio.biosynthframework.io.biodb.bigg;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;

public class BiggModelUrlScanner {
  
  public String base = "http://bigg.ucsd.edu";
  
  
  /**
   * 
   * @param modelEntry
   * @return Tuple with PMID and URL
   */
  public Tuple2<String> scan(String modelEntry) {
    String url = null;
    String pmid = null;
    
    try {
      String page = BiosIOUtils.download("http://bigg.ucsd.edu/models/" + modelEntry);
      Document document = Jsoup.parse(page);

      for (Element e : document.getElementsByTag("a")) {
        if ("uncompressed".equals(e.text())) {
          url = base + "/" + e.attr("href");
        }
        if (e.hasAttr("href") && e.attr("href").contains("http://www.ncbi.nlm.nih.gov/pubmed/")) {
          pmid = e.attr("href").substring("http://www.ncbi.nlm.nih.gov/pubmed/".length());
        }
      }
      
//      System.out.println(pmid + " " + url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return new Tuple2<String>(pmid, url);
  }
}
