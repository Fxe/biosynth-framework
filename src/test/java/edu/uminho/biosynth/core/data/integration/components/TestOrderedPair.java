package edu.uminho.biosynth.core.data.integration.components;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.util.math.components.OrderedPair;

public class TestOrderedPair {

	@Test
	public void testCreateItem() {
		OrderedPair<String, String> pair1 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("b", "a");
		
		assertEquals("a", pair1.getFirst());
		assertEquals("b", pair1.getSecond());
		assertEquals("b", pair2.getFirst());
		assertEquals("a", pair2.getSecond());
	}
	
	@Test
	public void testEqualsFalse() {
		OrderedPair<String, String> pair1 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("b", "a");
		
		assertEquals(false, pair1.equals(pair2));
	}

	@Test
	public void testEqualsTrue() {
		OrderedPair<String, String> pair0 = new OrderedPair<>("a", null);
		OrderedPair<String, String> pair1 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair3 = new OrderedPair<>("a", null);
		
		assertEquals(true, pair1.equals(pair2));
		assertEquals(true, pair0.equals(pair3));
	}
	
	@Test
	public void testToString() {
		OrderedPair<String, String> pair0 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair1 = new OrderedPair<>(null, "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("a", null);
		OrderedPair<String, String> pair3 = new OrderedPair<>(null, null);

		assertEquals("<a, b>", pair0.toString());
		assertEquals("<null, b>", pair1.toString());
		assertEquals("<a, null>", pair2.toString());
		assertEquals("<null, null>", pair3.toString());
	}

	@Test
	public void testEqualsSetInsertion1() {
		OrderedPair<String, String> pair1 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("b", "a");
		Set<OrderedPair<String, String>> set = new HashSet<> ();
		set.add(pair1);
		set.add(pair2);
		assertEquals(2, set.size());
	}
	
	@Test
	public void testEqualsSetInsertion2() {
		
		OrderedPair<String, String> pair1 = new OrderedPair<>("a", "b");
		OrderedPair<String, String> pair2 = new OrderedPair<>("a", "b");
		Set<OrderedPair<String, String>> set = new HashSet<> ();
		set.add(pair1);
		set.add(pair2);
		assertEquals(1, set.size());
	}
	
	@Test
	public void testEqualsSetInsertion3() throws Exception {
		
		OrderedPair<String, Class<?>> pair1 = new OrderedPair<>();
		OrderedPair<String, Class<?>> pair2 = new OrderedPair<>();
		pair1.setFirst("a");
		pair1.setSecond(Class.forName("java.util.HashSet"));
		Set<OrderedPair<String, Class<?>>> set = new HashSet<> ();
		pair2.setFirst("a");
		pair2.setSecond(Class.forName("java.util.HashSet"));
		set.add(pair1);
		set.add(pair2);
		assertEquals(1, set.size());
	}
	
	@Test
	public void testEqualsSetInsertion4() throws Exception {
		
		OrderedPair<String, Class<?>> pair1 = new OrderedPair<>();
		OrderedPair<String, Class<?>> pair2 = new OrderedPair<>();
		pair1.setFirst("a");
		pair1.setSecond(Class.forName("java.util.HashSet"));
		Set<OrderedPair<String, Class<?>>> set = new HashSet<> ();
		pair2.setFirst("a");
		pair2.setSecond(Class.forName("java.util.ArrayList"));
		set.add(pair1);
		set.add(pair2);
		assertEquals(2, set.size());
	}
	
	@Test
	public void testEqualsSetInsertion5() throws Exception {
		
		OrderedPair<Object, Object> pair1 = new OrderedPair<>();
		OrderedPair<Object, Object> pair2 = new OrderedPair<>();
		pair1.setFirst("a");
		pair1.setSecond(Class.forName("java.util.HashSet"));
		Set<OrderedPair<Object, Object>> set = new HashSet<> ();
		pair2.setFirst(Class.forName("java.util.HashSet"));
		pair2.setSecond("a");
		set.add(pair1);
		set.add(pair2);
		assertEquals(2, set.size());
	}
}
