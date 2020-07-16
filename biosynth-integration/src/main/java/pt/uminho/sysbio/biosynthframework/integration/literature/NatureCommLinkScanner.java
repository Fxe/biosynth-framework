package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class NatureCommLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("c-article-supplementary__item");
    for (Element element : elements) {
      Elements as = element.getElementsByTag("a");
      for (Element a : as) {
        String href = a.attr("href");
        String description = a.text();
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setUrl(href);
        sup.setDescription(description);
        links.add(sup);
      }
    }
    
    return links;
  }
}
