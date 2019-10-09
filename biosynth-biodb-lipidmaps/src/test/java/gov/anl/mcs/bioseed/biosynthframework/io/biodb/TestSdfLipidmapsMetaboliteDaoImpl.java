package gov.anl.mcs.bioseed.biosynthframework.io.biodb;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.io.biodb.SdfLipidmapsMetaboliteDaoImpl;

public class TestSdfLipidmapsMetaboliteDaoImpl {

  @Test
  public void test() {
    File file = new File("G:\\var\\biodb\\lipidmaps/LMSDFDownload12Dec17.zip");
    File file1 = new File("G:\\var\\biodb\\lipidmaps/LMSD_20190711.sdf.zip");
    SdfLipidmapsMetaboliteDaoImpl dao1 = new SdfLipidmapsMetaboliteDaoImpl(file1, "test");
    List<String> e1 = dao1.getAllMetaboliteEntries();

    SdfLipidmapsMetaboliteDaoImpl dao2 = new SdfLipidmapsMetaboliteDaoImpl(file, "test");
    List<String> e2 = dao2.getAllMetaboliteEntries();
    
    System.out.println(e1.size() + " " + e2.size());
    
    fail("Not yet implemented");
  }

}
