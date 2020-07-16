package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class MdpiMetabolitesScanner implements Function<String, List<SupplementaryMaterialEntity>> {
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("content__container content__container__combined-for-large");
    for (Element element : elements) {
      Element e = element.getElementById("suppl_id");
      boolean isSupplementaryMaterial = e != null;
      
      if (isSupplementaryMaterial) {
        Elements as = element.getElementsByTag("a");
        for (Element a : as) {
          String href = a.attr("href");
          if (href != null && href.trim().length() > 0) {
            if (!href.startsWith("https://www.mdpi.com/")) {
              href = "https://www.mdpi.com/" + href;
            }
            String description = a.text();
            SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
            sup.setUrl(href);
            sup.setDescription(description);
            links.add(sup);
          }
        }         
      }
    }
    
    return links;
  }
}
