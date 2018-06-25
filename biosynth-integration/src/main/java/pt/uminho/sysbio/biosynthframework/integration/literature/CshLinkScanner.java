package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;

public class CshLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  private String base = "https://genome.cshlp.org";
  private String internalLinkClass = "dslink-supplemental-research-data";
  public CshLinkScanner() { }
  
  public CshLinkScanner(String baseUrl) {
    this.base = baseUrl;
  }
  
  public CshLinkScanner(String baseUrl, String linkClass) {
    this.base = baseUrl;
    this.internalLinkClass = linkClass;
  }
  
  public List<SupplementaryMaterialEntity> fetchLinks(String supPage) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(supPage);
    Elements es = document.getElementsByTag("li");
    for (Element e : es) {
      Elements as = e.getElementsByTag("a");
      if (as.size() == 1 && as.get(0).attr("href").contains("/suppl/")) {
        String url = base + as.get(0).attr("href");
        String description = e.text();
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setUrl(url);
        sup.setDescription(description);
        links.add(sup);
      }
    }
    
    return links;
  }
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements es = document.getElementsByClass(internalLinkClass);
    String redirect = null;
    if (es.size() == 1) {
      for (Element e : es) {
        redirect = e.attr("href");
      }
    }

    if (redirect != null) {
      try {
        page = BiosIOUtils.download(base + "/" + redirect);
        links = fetchLinks(page);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    
    return links;
  }
}
