package pt.uminho.sysbio.biosynthframework.chemanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

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
	public void test_signature_not_equal_hash() {
		Signature s1 = new Signature("[N]([C]([C]=[O]))");
		Signature s2 = new Signature("[N]([C](=[O][C]))");
		
		int h1 = s1.hashCode();
		int h2 = s2.hashCode();
		
		assertNotEquals(h1, h2);
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
	

}
