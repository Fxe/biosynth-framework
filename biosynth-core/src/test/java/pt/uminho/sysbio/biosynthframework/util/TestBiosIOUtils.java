package pt.uminho.sysbio.biosynthframework.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.io.FileType;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class TestBiosIOUtils {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test1() {
    FileType t = BiosIOUtils.detectType(new File("D:\\home\\fliu\\OneDrive - Universidade do Porto\\home\\fliu\\workspace\\bios\\models\\iWV1314\\12864_2008_1438_MOESM1_ESM.fas"));
    System.out.println(t);
    fail("Not yet implemented");
  }

  @Test
  public void test2() {
    FileType t = BiosIOUtils.detectType(new File("D:\\home\\fliu\\OneDrive - Universidade do Porto\\home\\fliu\\workspace\\bios\\models\\21208457\\12864_2010_3148_MOESM7_ESM.XLS"));
    System.out.println(t);
    fail("Not yet implemented");
  }
  
  @Test
  public void test3() {
    FileType t = BiosIOUtils.detectType(new File("D:\\home\\fliu\\OneDrive - Universidade do Porto\\home\\fliu\\workspace\\bios\\models\\21208457\\12864_2010_3148_MOESM6_ESM.XML"));
    System.out.println(t);
    fail("Not yet implemented");
  }
  
  @Test
  public void test4() {
    File f = new File("D:\\home\\fliu\\OneDrive - Universidade do Porto\\home\\fliu\\workspace\\bios\\models\\iJW145\\inline-supplementary-material-5.zip");
    FileType t = BiosIOUtils.detectType(f);
    System.out.println(t);
    
    List<String> filesss = new ArrayList<> ();
    if (f.getName().toLowerCase().endsWith(".zip")) {
      int files = 0;
      boolean sbml = false;
      try (ZipContainer zip = new ZipContainer(f.getAbsolutePath())) {
        for (ZipRecord zr : zip.getInputStreams()) {
          files++;
          filesss.add(zr.name);
          try (InputStream is = zr.is) {
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw, Charset.defaultCharset());
            String data = sw.toString();
            sbml = sbml || BiosIOUtils.isSbml(data);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      System.out.println(filesss);
      if (files == 1 && sbml) {
        System.out.println(f + " sbml !");
      }
    }
    fail("Not yet implemented");
  }
}
