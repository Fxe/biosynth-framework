package pt.uminho.sysbio.biosynthframework.integration.literature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.LiteratureEntity;
import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class TestLiteratureManagerService {

  private static String base = 
      "/home/fliu/OneDrive - Universidade do Porto/home/fliu/workspace/bios/models";
  private static LiteratureManagerService service;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    Resource path = new FileSystemResource(base);
    service = new LiteratureManagerService(path);
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

//  @Test
  public void testDownload() {
    try {
      URI uri = new URI("https://onlinelibrary.wiley.com/action/downloadSupplement?doi=10.1002%2Fbiot.201200266&attachmentId=187219262");
      File folder = new File("D:\\tmp\\biodb");
      File result = LiteratureManagerService.download(uri, null, folder);
      assertNotNull(result);
      System.out.println(result);
    } catch (IOException | URISyntaxException e) {
      fail(e.getMessage());
    }
  }
  
//  @Test
  public void testGetSupplementaryMaterial1() {
    LiteratureEntity entity = service.getLiteratureByEntry("iAM388");
    List<SupplementaryMaterialEntity> sups = service.getSupplementaryMaterial(entity);
    assertNotNull(sups);
    assertEquals(9, sups.size());
    System.out.println(sups);
  }
  
  //@Test
  public void testGetSupplementaryMaterial2() {
    //  LiteratureEntity entity = service.getLiteratureByEntry("23420771");
    //  SupplementaryMaterialEntity sm = 
    //      service.getSupplementaryMaterial(entity, "https://onlinelibrary.wiley.com/action/downloadSupplement?doi=10.1002%2Fbiot.201200266&attachmentId=187219262");
    //  service.fetchSupplementaryMaterial(entity);
    //  System.out.println(entity.getFolder());
  }
  
//  @Test
  public void testFetchSupplementaryMaterialUrls() {
    //22044676  10.1186/1471-2164-12-535    BMC genomics    BMC Genomics
    LiteratureEntity entity = service.getLiteratureByEntry("iAM388");
    List<String> urls = service.fetchSupplementaryMaterialUrls(entity);
    assertNotNull(urls);
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM1_ESM.XLS"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM2_ESM.XLSX"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM3_ESM.XLS"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM4_ESM.XLS"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM5_ESM.XLSX"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM6_ESM.pdf"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM7_ESM.ppt"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM8_ESM.pdf"));
    assertTrue(urls.contains("https://static-content.springer.com/esm/art%3A10.1186%2F1471-2164-12-535/MediaObjects/12864_2011_3727_MOESM9_ESM.pdf"));
  }

  @Test
  public void testListSupplementaryMaterials() {
    LiteratureEntity entity = service.getLiteratureByEntry("iAM388");
    Map<String, SupplementaryMaterialEntity> sup1 = new HashMap<>();
    Map<String, SupplementaryMaterialEntity> sup2 = new HashMap<>();
    for (SupplementaryMaterialEntity s : entity.getSupplementaryMaterials()) {
      sup1.put(s.getEntry(), s);
    }
    List<SupplementaryMaterialEntity> sup = service.listSupplementaryMaterials(entity);
    for (SupplementaryMaterialEntity s : sup) {
      sup2.put(s.getEntry(), s);
    }
    
    for (String k : sup2.keySet()) {
      SupplementaryMaterialEntity s1 = sup1.get(k);
      SupplementaryMaterialEntity s2 = sup2.get(k);
      if (s1 != null && s2 != null) {
        assertEquals(s1.getMd5(), s2.getMd5());
        assertEquals(s1.getFile(), s2.getFile());
        assertEquals(s1.getEntry(), s2.getEntry());
        assertEquals(s1.getSize(), s2.getSize());
        System.out.println(s1.getUrl());
        System.out.println(s2.getUrl());
//        assertEquals(s1.getUrl().toString(), s2.getUrl().toString());
      } else {
        System.out.println(k);
      }
    }
  }
}
