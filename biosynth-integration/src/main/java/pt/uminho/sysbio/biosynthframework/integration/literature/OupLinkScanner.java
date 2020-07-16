package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class OupLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {
  
  private static final Logger logger = LoggerFactory.getLogger(OupLinkScanner.class);
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("dataSuppLink");
    if (elements.size() == 1) {
       Elements as = elements.get(0).getElementsByTag("a");
       for (Element a : as) {
         String href = a.attr("href");
         SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
         sup.setUrl(href);
         links.add(sup);
       }
    } else {
      logger.warn("dataSuppLink: {}", elements.size());
    }
    
    return links;
  }
}
