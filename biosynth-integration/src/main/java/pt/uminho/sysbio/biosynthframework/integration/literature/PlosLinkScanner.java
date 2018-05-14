package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PlosLinkScanner implements Function<String, List<String>> {

  @Override
  public List<String> apply(String page) {
    List<String> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("supplementary-material");
    for (Element e : elements) {
      Elements doi = e.getElementsByClass("siDoi");
      if (doi.size() == 1 && doi.get(0).getElementsByTag("a").size() == 1) {
        Element a = doi.get(0).getElementsByTag("a").get(0);
        String href = a.attr("href");
        links.add(href);
      }
    }
    
    return links;
  }
}
