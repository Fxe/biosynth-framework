package pt.uminho.sysbio.biosynthframework;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.visualization.escher.EscherMap;

public class TestLayoutUtils {

  public static final String[] maps = {
//    "D:/var/biomodels/layout/escher/e_coli_core.Core metabolism.json", "ec_core", "60001",
//    "D:/var/biomodels/layout/escher/iJO1366.Central metabolism.json", "iJO1366_central", "10001",
//    "D:/var/biomodels/layout/escher/iJO1366.Fatty acid beta-oxidation.json", "iJO1366_fa_beta", "10002",
//    "D:/var/biomodels/layout/escher/iJO1366.Fatty acid biosynthesis (saturated).json", "iJO1366_fa_synth", "10003",
//    "D:/var/biomodels/layout/escher/iJO1366.Nucleotide metabolism.json", "iJO1366_nuc", "10004",
//    "D:/var/biomodels/layout/escher/iMM904.Central carbon metabolism.json", "iMM904_central", "20001",
//      
//    "D:/var/biomodels/layout/escher/RECON1.Carbohydrate metabolism.json", "RECON1_carb", "80001",
//      
//    "D:/var/biomodels/layout/escher/fliu/iMM904.sterol_final.json", "iMM904_sterol", "101",
//    "D:/var/biomodels/layout/escher/fliu/iMM904.phospho_bio_final.json", "iMM904_phospho", "201",
    "D:/var/biomodels/layout/escher/fliu/iMM904.complex_etoh_final.json", "iMM904_complex", "202",
  };
  
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
  public void test() {
//    for (int i = 0; i < maps.length; i+=3) {
//      String path = maps[i];
//      File escherMapFile = new File(path);
//    
//      try {
//        InputStream is = new FileInputStream(escherMapFile);
//        EscherMap escherMap = LayoutUtils.loadEscherMap(is);
//        System.out.println(escherMap.map_name);
//        System.out.println(escherMap.nodes.keySet().size());
//        System.out.println(escherMap.reactions.keySet().size());
//        System.out.println(escherMap.canvas);
//
//        MetabolicLayout layout = LayoutUtils.toMetabolicLayout(
//            escherMap, null, null);
//        
//        System.out.println(layout.nodes.size());
//        System.out.println(layout.metabolites.get("BiGG"));
//        System.out.println(layout.reactions.get("BiGG"));
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
  }
}
