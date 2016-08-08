package pt.uminho.sysbio.biosynthframework.visualization.escher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.visualization.escher.EscherMap;
import pt.uminho.sysbio.biosynthframework.LayoutUtils;

public class TestLoadEscher {

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
  public void testLoadMap() {
    String path = "/var/escher/1-0-0/5/maps/Saccharomyces cerevisiae/iMM904.Central carbon metabolism.json";
    File escherMapFile = new File(path);
    InputStream is = null;
    try {
      is = new FileInputStream(escherMapFile);
      EscherMap escherMap = LayoutUtils.loadEscherMap(is);
      assertEquals("iMM904.Central carbon metabolism", escherMap.map_name);
      assertEquals("Yeast central carbon metabolism\nLast Modified Fri Jan 09 2015 19:50:57 GMT-0800 (PST)", escherMap.map_description);
      assertEquals("78086bfdab8ac8a8150cf4cd5dada037", escherMap.map_id);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  @Test
  public void testLoadModel() {
    String path = "/var/escher/1-0-0/5/models/Escherichia coli/e_coli_core.json";
    File escherMapFile = new File(path);
    InputStream is = null;
    try {
      is = new FileInputStream(escherMapFile);
      EscherModel model = LayoutUtils.loadEscherModel(is);
      System.out.println(model.genes.get(0));
      System.out.println(model.metabolites.get(0).notes);
      System.out.println(model.metabolites.get(1).notes);
      System.out.println(model.reactions.get(0).notes);
      //      for (EscherModelReaction r : model.reactions) {
      //        System.out.println(r.id + " " + r.objective_coefficient);
      //      }
      assertNotNull(model);
      assertEquals(1.0, model.version, 0.0);
      assertEquals("e_coli_core", model.id);
      Map<String, String> compartments = new HashMap<> ();
      compartments.put("c", "cytosol");
      compartments.put("e", "extracellular space");
      assertEquals(compartments, model.compartments);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  @Test
  public void testWriteModel() {
    EscherModel model1 = null;
    {
      String path = "/var/escher/1-0-0/5/models/Escherichia coli/e_coli_core.json";
      File escherMapFile = new File(path);
      InputStream is = null;
      try {
        is = new FileInputStream(escherMapFile);
        model1 = LayoutUtils.loadEscherModel(is);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeQuietly(is);
      }
    }
    String path = "/super_sapien.json";
    EscherModel model = new EscherModel();
    model.compartments.put("G", "G comp");
    model.compartments.putAll(model1.compartments);
    model.id = "super_sapien";
    model.version = 1;
    EscherModelReaction r0 = model1.reactions.get(0);
    r0.notes.clear();
    System.out.println(r0.metabolites);
    model.reactions.add(r0);
    EscherModelReaction r1 = model1.reactions.get(1);
    r1.metabolites.clear();
    r1.metabolites.put("acald_c", 1.0);
    r1.metabolites.put("pyr_c", -100.0);
    r1.metabolites.put("omg_c", -100.0);
    model.reactions.add(r1);
    
    EscherModelReaction raa = new EscherModelReaction(); // model1.reactions.get(10);
    raa.notes.clear();
    raa.id = "AFUS";
    raa.name = "AFUS";
    raa.subsystem = "A system";
    raa.lower_bound = -1000d;
    raa.upper_bound = 1000d;
    raa.metabolites.clear();
    raa.metabolites.put("aa_c", 2.0);
    raa.metabolites.put("a_c", -1.0);
    System.out.println("rule " + raa.gene_reaction_rule);
    System.out.println("rule " + raa.objective_coefficient);
    model.reactions.add(raa);
    Map<String, EscherModelMetabolite> cpdDb = new HashMap<> ();
    cpdDb.put("omg_c", new EscherModelMetabolite("omg_c", "OH MY GOD", "OMg", "c", 10));
    cpdDb.put("a_c", new EscherModelMetabolite("a_c",  "A" , "Li", "c", 0));
    cpdDb.put("aa_c", new EscherModelMetabolite("aa_c", "AA", "Li2", "G", 0));
    for (EscherModelReaction r : model.reactions) {
      for (String m : r.metabolites.keySet()) {
        EscherModelMetabolite metabolite = null;
        if (!cpdDb.containsKey(m)) {
          metabolite = model1.getMetabolite(m);
          metabolite.notes.clear();
          cpdDb.put(m, metabolite);
//          System.out.println(metabolite.id + " -> " + metabolite.compartment);
        } else {
          metabolite = cpdDb.get(m);
        }
        System.out.println(m);
        
        //metabolite.id = "cpd_" +p++;
        
        model.metabolites.add(metabolite);
      }
    }

    System.out.println(model.metabolites);
    //    model.metabolites.add();
    //    model.metabolites.add();


    //    r0.metabolites.put("a", 2.0);
    //    r0.metabolites.put("aa", -1.0);

    LayoutUtils.writeModel(model, path);
    //    InputStream is = null;
    //    try {
    //      is = new FileInputStream(escherMapFile);
    //      EscherModel model = LayoutUtils.loadEscherModel(is);
    //      System.out.println(model.genes.get(0));
    //      System.out.println(model.metabolites.get(0));
    //      System.out.println(model.reactions.get(0));
    ////      for (EscherModelReaction r : model.reactions) {
    ////        System.out.println(r.id + " " + r.objective_coefficient);
    ////      }
    //      assertNotNull(model);
    //      assertEquals(1.0, model.version, 0.0);
    //      assertEquals("e_coli_core", model.id);
    //      Map<String, String> compartments = new HashMap<> ();
    //      compartments.put("c", "cytosol");
    //      compartments.put("e", "extracellular space");
    //      assertEquals(compartments, model.compartments);
    //
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //    } finally {
    //      IOUtils.closeQuietly(is);
    //    }
  }
}
