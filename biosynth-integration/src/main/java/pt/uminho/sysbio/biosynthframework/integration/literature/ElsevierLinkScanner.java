package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class ElsevierLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  private static final Logger logger = LoggerFactory.getLogger(ElsevierLinkScanner.class);
  
//  private final WebDriver driver;
  
  public ElsevierLinkScanner(String phantomjs) {
    System.setProperty("webdriver.gecko.driver", "/opt/webdriver/geckodriver/0.21/geckodriver.exe");
    System.setProperty("phantomjs.binary.path",  "/opt/webdriver/phantomjs/2.11/bin/phantomjs.exe");
//    driver = new PhantomJSDriver();
  }
  
  public String loadPage(String url) {
    final WebDriver driver = new PhantomJSDriver();
    driver.navigate().to(url);
    
    logger.info("{}", driver.getCurrentUrl());
    String source = driver.getPageSource();
    
    driver.quit();
    return source;
  }
  
  public String decode1(Document document) {
    String url = null;
    try {
      for (Element e : document.getElementsByTag("meta")) {
        if (e.hasAttr("HTTP-EQUIV") && "REFRESH".equals(e.attr("HTTP-EQUIV"))) {
          String content = e.attr("content");
          String[] p = content.split("'");
          url = URLDecoder.decode(p[1], "UTF-8");
          System.out.println(url);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return url;
  }
  
  public String decode2(Document document) {
    String url = null;
    try {
      Element inputRedirect = document.getElementById("redirectURL");
      String redirect = inputRedirect.attr("value");
      url = URLDecoder.decode(redirect, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return url;
  }
  
  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    logger.debug("!!!!!!!!!!!");
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    String url = decode2(document);
    logger.info(url);
    
//    Element inputRedirect = document.getElementById("redirectURL");
    
    System.out.println();
    try {
      page = loadPage(url);
      

//        FileOutputStream fos = new FileOutputStream("/tmp/test2.html");
//        IOUtils.copy(new ByteArrayInputStream(page.getBytes()), fos);
//        fos.close();
      
      
      return parse(page);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return links;
  }
  
  public List<SupplementaryMaterialEntity> parse(String page) {
    logger.debug("!!!!!!!!!!!");
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
//    System.out.println(document);
    Elements es = document.getElementsByClass("download-link");
    for (Element link : es) {
      logger.info("{}", link);
//      Elements link = e.getElementsByClass("download-link");
//      if (link.size() == 1) {
        String href =link.attr("href");
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setUrl(href);
        links.add(sup);
//      }
    }
    
    if (links.isEmpty()) {
      logger.info("{}", page);
    }
    
    return links;
  }
}
