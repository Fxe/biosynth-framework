package pt.uminho.sysbio.biosynthframework.chemanalysis;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestReactionSignatureSet {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void test_signature_set_equal() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		
//		System.out.println(sgs1.hashCode() + " " + sgs2.hashCode());
		
		assertTrue(sgs1.equals(sgs2));
	}

	@Test
	public void test_signature_set_not_equal() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 2.0);
		
//		System.out.println(sgs1.hashCode() + " " + sgs2.hashCode());
		
		assertFalse(sgs1.equals(sgs2));
	}
	
	@Test
	public void test_signature_set_hash_equal_1() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		
		assertTrue(sgs1.hashCode() == sgs2.hashCode());
	}
	
	@Test
	public void test_signature_set_hash_equal_2() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		
		assertTrue(sgs1.hashCode() == sgs2.hashCode());
	}
	
	@Test
	public void test_signature_set_hash_equal_3() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		
		assertTrue(sgs1.hashCode() == sgs2.hashCode());
	}
	
	@Test
	public void test_signature_set_hash_equal_4() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs1.getLeftSignatureMap().put(new Signature("[N]([C])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[O](=[C])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[N]([C])"), 1.0);
		
		assertTrue(sgs1.hashCode() == sgs2.hashCode());
	}
	
	@Test
	public void test_signature_set_hash_not_equal_1() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 2.0);
		
		
		assertFalse(sgs1.hashCode() == sgs2.hashCode());
	}
	
	@Test
	public void test_signature_set_hash_not_equal_2() {
		ReactionSignature sgs1 = new ReactionSignature();
		ReactionSignature sgs2 = new ReactionSignature();
		sgs1.getLeftSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs1.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 1.0);
		sgs2.getLeftSignatureMap().put(new Signature("[C]([C][O]=[O])"), 1.0);
		sgs2.getRightSignatureMap().put(new Signature("[C](=[O]=[O])"), 2.0);
		
		
		assertFalse(sgs1.hashCode() == sgs2.hashCode());
	}

}
