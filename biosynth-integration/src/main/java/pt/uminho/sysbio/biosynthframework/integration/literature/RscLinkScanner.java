package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class RscLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    for (Element a : document.getElementsByTag("a")) {
      if (a.text().toLowerCase().contains("supplementary information")) {
        String description = a.text();
        String url = a.attr("href");
//        System.out.println(url);
//        System.out.println(description);
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setDescription(description);
        sup.setUrl(url);
        links.add(sup);
      }

    }
    return links;
  }

}
