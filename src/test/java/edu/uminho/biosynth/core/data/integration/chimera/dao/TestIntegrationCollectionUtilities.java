package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestIntegrationCollectionUtilities {

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
	public void test_resolve_conflicts_no_conflicts() {
		Map<String, Set<String>> c = new HashMap<> ();
		Set<String> s = new HashSet<> ();
		Set<String> d = new HashSet<> ();
		makeCluster("c0", new String[]{"a", "b", "c"}, c);
		makeCluster("c1", new String[]{"d", "e", "f"}, c);
		Map<String, String> o = IntegrationCollectionUtilities.resolveConflicts(c, s, d);
		Map<String, Set<String>> i = IntegrationCollectionUtilities.invertMap(o);
		
		System.out.println(s);
		System.out.println(d);
		System.out.println(o);
		System.out.println(i);
	}
	
	@Test
	public void test_resolve_conflicts_with_conflicts() {
		Map<String, Set<String>> c = new HashMap<> ();
		Set<String> s = new HashSet<> ();
		Set<String> d = new HashSet<> ();
		makeCluster("c0", new String[]{"a", "b", "c"}, c);
		makeCluster("c1", new String[]{"d", "e", "f"}, c);
		makeCluster("c2", new String[]{"a", "g"}, c);
		Map<String, String> o = IntegrationCollectionUtilities.resolveConflicts(c, s, d);
		Map<String, Set<String>> i = IntegrationCollectionUtilities.invertMap(o);
		
		System.out.println(s);
		System.out.println(d);
		System.out.println(o);
		System.out.println(i);
	}

	public static void makeCluster(String c, String[] m, Map<String, Set<String>> cc) {
		cc.put(c, new HashSet<String> (Arrays.asList(m)));
	}
}
