package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class WileyLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {
  
  private final String base = "https://onlinelibrary.wiley.com";
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("support-info__table");
    if (elements.size() == 1) {
      Element container = elements.get(0);
      for (Element a : container.getElementsByTag("a")) {
        String href = a.attr("href");
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setUrl(base + href);
        links.add(sup);
      }
    }
    
    return links;
  }
}
