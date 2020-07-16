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

public class MolSysBioLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  private static final Logger logger = LoggerFactory.getLogger(MolSysBioLinkScanner.class);
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements es = document.getElementsByClass("supplementary-material");
    for (Element e : es) {
      if ("supplementary-material".equals(e.className())) {
        List<Element> hrefs = new ArrayList<> ();
        String description = "";
        for (Element child : e.children()) {
          description += child.text().replaceAll("\n", "");
          Elements hrefs_ = child.getElementsByTag("a");
          for (Element a : hrefs_) {
            hrefs.add(a);
          }
        }
        if (hrefs.size() == 1) {
          SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
          sup.setUrl(hrefs.get(0).attr("href").trim());
          sup.setDescription(description);
          links.add(sup);
        } else {
          logger.warn("unable to extract link");
        }      
      }
    }
    
    return links;
  }

}
