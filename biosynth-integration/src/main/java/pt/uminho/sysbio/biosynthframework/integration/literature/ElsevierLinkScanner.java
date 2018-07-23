package pt.uminho.sysbio.biosynthframework.integration.literature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class ElsevierLinkScanner {

  private final WebDriver driver;
  
  public ElsevierLinkScanner(String phantomjs) {
    System.setProperty("webdriver.gecko.driver", "/opt/webdriver/geckodriver/0.21/geckodriver.exe");
    System.setProperty("phantomjs.binary.path",  "/opt/webdriver/phantomjs/2.11/bin/phantomjs.exe");
    driver = new PhantomJSDriver();
  }
  
  public String loadPage(String url) {
    driver.navigate().to(url);
    String source = driver.getPageSource();
    return source;
  }
}
