package pt.uminho.sysbio.biosynthframework.chemanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.util.DigestUtils;

public class TestSignatures {

  @Test
  public void test_signature_equal_hash() {
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C]([C]=[O]))");
    
    int h1 = s1.hashCode();
    int h2 = s2.hashCode();
    
    assertEquals(h1, h2);
  }
  
  @Test
  public void test_signature_equal_hash_2() {
    Signature s1 = new Signature("0-42L");
    Signature s2 = new Signature("0-43-");
    
    int h1 = s1.hashCode();
    int h2 = s2.hashCode();
    
    assertEquals(h1, h2);
  }

  @Test
  public void test_signature_not_equal_hash() {
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C](=[O][C]))");
    
    int h1 = s1.hashCode();
    int h2 = s2.hashCode();
    
    assertNotEquals(h1, h2);
  }
  
  
  @Test
  public void test_signature_not_equal_hash_2() {
    Signature s1 = new Signature("0-42L");
    Signature s2 = new Signature("0-43-");
    
    long h1 = s1.hash();
    long h2 = s2.hash();
    
    assertEquals(h1, h2);
  }
  
  @Test
  public void test_signature_equal_compare() {
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C]([C]=[O]))");
    
    assertTrue(s1.equals(s2));
  }

  @Test
  public void test_signature_not_equal_compare() {
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C](=[O][C]))");
    
    assertFalse(s1.equals(s2));
  }
  
  @Test
  public void test_signature_collision_set() {
    Set<Signature> set = new HashSet<> ();
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C]([C]=[O]))");
    set.add(s1);
    set.add(s2);
    
    assertEquals(1, set.size());
  }

  @Test
  public void test_signature_not_collision_set() {
    Set<Signature> set = new HashSet<> ();
    Signature s1 = new Signature("[N]([C]([C]=[O]))");
    Signature s2 = new Signature("[N]([C](=[O][C]))");
    set.add(s1);
    set.add(s2);
    
    assertEquals(2, set.size());
  }
  
  @Test
  public void test_signature_contains() {
    Set<Signature> set1 = new HashSet<> ();
    Set<Signature> set2 = new HashSet<> ();
    set1.add(new Signature("[N]([C]([C]=[O]))"));
    set1.add(new Signature("[N]([C](=[O][C]))"));
    set2.add(new Signature("[N]([C]([C]=[O]))"));
    
    assertTrue(set1.containsAll(set2));
  }
  
  /**
   * 1.0  [C]([C]([C])[C]([C])) 0
1.0 [C]([C]([C])[C](=[N][O])) 0
1.0 [C]([C]([C])[N])  0
1.0 [C]([C]([C][O])[C]([C][O][O])[O]) 0
1.0 [C]([C]([C])=[N][O])  0
1.0 [N]([C]([C])) 0
1.0 [N](=[C]([C][O])) 0
2.0 [C]([C]([C][O])[C]([C][O])[O])  0
1.0 [O]([C]([C][C])[C]([C][C][O]))  0
1.0 [C]([C]([O])[C]([C][O])[O]([C]))  0
3.0 [O]([C]([C][C]))  0
1.0 [C]([C]([C])[C]([N])) 0
1.0 [C]([C]([C][O])[O]) 0
1.0 [O]([C]([C][C][O])) 0
1.0 [C]([C]([O])[C]([C][O])[O][O]([C])) 0
1.0 [O]([C]([C]=[N])) 0
1.0 [C]([C]([C][O][O])[O])  0
2.0 [O]([C]([C])) 0
   */
  @Test
  public void test_molecular_signature_not_equals() {
//    String[] msig1Array = new String[] {
//        "1", "[C]([C]([C])[O]=[O])",
//        "1", "[C]([C]([C])[C]([C]))",
//        "1", "[C]([C]([C])[N])",
//        "1", "[O](=[C]([C][O]))",
//        "1", "[O]([C]([C][C])[C]([C][O]))",
//        "1", "[O]([C]([C][O]))",
//        "1", "[N]([C]([C]))",
//        "1", "[O]([C]([C]=[O]))",
//        "2", "[C]([C]([C][O])[C]([C][O])[O])",
//        "1", "[C]([C]([C])[C]([O]=[O]))",
//        "1", "[C]([C]([C][O])[C]([O][O])[N])",
//        "1", "[C]([C]([O])[C]([C][O])[O]([C]))",
//        "3", "[O]([C]([C][C]))",
//        "1", "[N]([C]([C][C]))",
//        "1", "[C]([C]([C])[C]([N]))",
//        "1", "[C]([C]([C][O])[O])",
//        "1", "[C]([C]([C][N])[O][O]([C]))",
//        "1", "[C]([C]([C][N])[C]([C][O])[O])",
//        "1", "[O]([C]([C]))",
//        };
//    String[] msig2Array = new String[] {
//        "1", "[C]([C]([C])[O]=[O])",
//        "3", "[C]([C]([C])[C]([C]))",
//        "1", "[C]([C]([C])[N])",
//        "1", "[O](=[C]([C][O]))",
//        "1", "[O]([C]([C][C])[C]([C][O]))",
//        "1", "[O]([C]([C][O]))",
//        "1", "[N]([C]([C]))",
//        "1", "[O]([C]([C]=[O]))",
//        "1", "[C]([C]([C][O])[C]([C][O])[O])",
//        "1", "[C]([C]([C])[C]([O]=[O]))",
//        "1", "[C]([C]([C][O])[C]([O][O])[N])",
//        "1", "[C]([C]([O])[C]([C][O])[O]([C]))",
//        "2", "[O]([C]([C][C]))",
//        "1", "[N]([C]([C][C]))",
//        "1", "[C]([C]([C])[C]([N]))",
//        "1", "[C]([C]([C][O])[O])",
//        "1", "[C]([C]([C][N])[O][O]([C]))",
//        "1", "[C]([C]([C][N])[C]([C][O])[O])",
//        "1", "[O]([C]([C]))",
//        };
    String[] msig1Array = new String[] {
        "1", "[C]([C]([C])[C]([C]))",
        "2", "[C]([C]([C][O])[C]([C][O])[O])",
        "3", "[O]([C]([C][C]))",
        };
    String[] msig2Array = new String[] {
        "3", "[C]([C]([C])[C]([C]))",
        "1", "[C]([C]([C][O])[C]([C][O])[O])",
        "2", "[O]([C]([C][C]))",
        };
    MolecularSignature msig1 = new MolecularSignature();
    for (int i = 0; i < msig1Array.length; i += 2) {
      double val = Double.parseDouble(msig1Array[i]);
      String signatureStr = msig1Array[i + 1];
      msig1.getSignatureMap().put(new Signature(signatureStr), val);
    }
    
    MolecularSignature msig2 = new MolecularSignature();
    for (int i = 0; i < msig2Array.length; i += 2) {
      double val = Double.parseDouble(msig2Array[i]);
      String signatureStr = msig2Array[i + 1];
      msig2.getSignatureMap().put(new Signature(signatureStr), val);
    }
    
    System.out.println(SignatureUtils.toString(msig1));
    System.out.println(SignatureUtils.toString(msig2));
    System.out.println(SignatureUtils.getFormulaMap(msig1));
    System.out.println(SignatureUtils.getFormulaMap(msig2));
    System.out.println(DigestUtils.hex(msig1.hash()));
    System.out.println(DigestUtils.hex(msig2.hash()));
  }
  
  @Test
  public void test_molecular_signature_not_equals_2() {
    String[] msig1Array = new String[] {
        "1", "[A]",
        "2", "[B]",
        "3", "[C]",
        };
    String[] msig2Array = new String[] {
        "3", "[A]",
        "1", "[B]",
        "2", "[C]",
        };
    MolecularSignature msig1 = new MolecularSignature();
    for (int i = 0; i < msig1Array.length; i += 2) {
      double val = Double.parseDouble(msig1Array[i]);
      String signatureStr = msig1Array[i + 1];
      msig1.getSignatureMap().put(new Signature(signatureStr), val);
    }
    
    MolecularSignature msig2 = new MolecularSignature();
    for (int i = 0; i < msig2Array.length; i += 2) {
      double val = Double.parseDouble(msig2Array[i]);
      String signatureStr = msig2Array[i + 1];
      msig2.getSignatureMap().put(new Signature(signatureStr), val);
    }
    
    System.out.println(SignatureUtils.toString(msig1));
    System.out.println(SignatureUtils.toString(msig2));
    System.out.println(SignatureUtils.getFormulaMap(msig1));
    System.out.println(SignatureUtils.getFormulaMap(msig2));
    System.out.println(DigestUtils.hex(msig1.hash()));
    System.out.println(DigestUtils.hex(msig2.hash()));
  }
}
