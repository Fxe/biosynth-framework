package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class SpringerLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  /***
   * Scan links from class <b>c-article-supplementary__item</b> <br>
   * URL inside <b>a</b> element with class <b>print-link</b><br>
   * Example Reconstruction and analysis of a genome-scale metabolic model of the oleaginous fungus Mortierella alpina
   * <a href="https://doi.org/10.1186/s12918-014-0137-8"> https://doi.org/10.1186/s12918-014-0137-8</a>
   * @param document
   * @return
   */
  public List<SupplementaryMaterialEntity> method3(Document document) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Elements elements = document.getElementsByClass("c-article-supplementary__item");
    for (Element a : elements) {
      SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
      Elements urls = a.getElementsByClass("print-link");
      if (urls.size() == 1) {
        Element ref = urls.get(0);
        if (ref.tagName().equals("a")) {
          sup.setUrl(ref.attr("href"));
          sup.setDescription(a.text());
          links.add(sup);
        }
      }
    }
    return links;
  }
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("filename");
    
    if (elements.isEmpty()) {
      elements = document.getElementsByClass("caption-container");
      for (Element container : elements) {
        String href = null;
        String desc = "";
        for (Element a : container.getElementsByTag("a")) {
          String hrefValue = a.attr("href");
          if (hrefValue != null && hrefValue.contains("http")) {
            href = hrefValue;
          }
        }
        for (Element span : container.getElementsByClass("SimplePara")) {
          desc += span.text();
        }
        if (href != null) {
          SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
          sup.setUrl(href);
          sup.setDescription(desc);
          links.add(sup);
        }
      }
    } else {
      for (Element a : elements) {
        if (a.tagName() == "a") {
          String href = a.attr("href");
          SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
          sup.setUrl(href);
          links.add(sup);
        }
      }
    }
    
    if (links.isEmpty()) {
      links = method3(document);
    }
    
    return links;
  }

}
