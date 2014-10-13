package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultGraphImpl;

public class TestDefaultGraph {

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
	public void testEmptyGraph() {
		BinaryGraph<Integer, Integer> graph = new DefaultGraphImpl<>();
		
		assertEquals(0, graph.size());
		assertEquals(0, graph.order());
	}

	@Test
	public void testGraphWithSingleVertex() {
		BinaryGraph<Integer, Integer> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		assertEquals(0, graph.size());
		assertEquals(1, graph.order());
	}
	
	@Test
	public void testGraphWithManyVertexex() {
		BinaryGraph<Integer, Integer> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(1);
		graph.addVertex(4);
		graph.addVertex(2);
		graph.addVertex(1);
		assertEquals(0, graph.size());
		assertEquals(4, graph.order());
	}
	
	@Test
	public void testGraphWithSingleEdge() {
		BinaryGraph<Integer, Character> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(6);
		graph.addVertex(2);
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		assertEquals(1, graph.size());
		assertEquals(3, graph.order());
	}
	
	@Test
	public void testGraphWithManyEdge() {
		BinaryGraph<Integer, Character> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(6);
		graph.addVertex(2);
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		assertEquals(3, graph.size());
		assertEquals(3, graph.order());
	}
	
	@Test
	public void testGraphWithCollisionEdge() {
		BinaryGraph<Integer, Character> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(6);
		graph.addVertex(2);
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		assertEquals(3, graph.size());
		assertEquals(3, graph.order());
	}
	
	@Test
	public void testGraphWithBidirectionalEdge() {
		BinaryGraph<Integer, Character> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(2);
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 5));
		assertEquals(2, graph.size());
		assertEquals(2, graph.order());
	}
	
	@Test
	public void testGraphWithAllEdge() {
		BinaryGraph<Integer, Character> graph = new DefaultGraphImpl<>();
		graph.addVertex(5);
		graph.addVertex(6);
		graph.addVertex(2);
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		assertEquals(4, graph.size());
		assertEquals(3, graph.order());
	}
	
	@Test
	public void testGraphMergeSingleVertex() {
		BinaryGraph<Integer, Character> graph1 = new DefaultGraphImpl<>();
		graph1.addVertex(5);
		graph1.addVertex(6);
		graph1.addVertex(2);
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		
		BinaryGraph<Integer, Character> graph2 = new DefaultGraphImpl<>();
		graph2.addVertex(10);
		
		graph1.addAll(graph2);
		
		assertEquals(4, graph1.size());
		assertEquals(4, graph1.order());
	}
	
	@Test
	public void testEmptyGraphMergeManyCollisionVertex() {
		BinaryGraph<Integer, Character> graph1 = new DefaultGraphImpl<>();
		graph1.addVertex(5);
		graph1.addVertex(6);
		graph1.addVertex(2);
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		
		BinaryGraph<Integer, Character> graph2 = new DefaultGraphImpl<>();
		graph2.addVertex(2);
		graph2.addVertex(10);
		graph2.addVertex(100);
		
		graph1.addAll(graph2);
		
		assertEquals(4, graph1.size());
		assertEquals(5, graph1.order());
	}
	
	@Test
	public void testEmptyGraphMergeSingleEdge() {
		BinaryGraph<Integer, Character> graph1 = new DefaultGraphImpl<>();
		graph1.addVertex(5);
		graph1.addVertex(6);
		graph1.addVertex(2);
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		
		BinaryGraph<Integer, Character> graph2 = new DefaultGraphImpl<>();
		graph2.addVertex(10);
		graph2.addVertex(2);
		graph2.addEdge(new DefaultBinaryEdge<Character, Integer>('K', 5, 2));
		
		graph1.addAll(graph2);
		
		assertEquals(5, graph1.size());
		assertEquals(4, graph1.order());
	}
	
	@Test
	public void testEmptyGraphMergeManyEdgeCollision() {
		BinaryGraph<Integer, Character> graph1 = new DefaultGraphImpl<>();
		graph1.addVertex(5);
		graph1.addVertex(6);
		graph1.addVertex(2);
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		
		BinaryGraph<Integer, Character> graph2 = new DefaultGraphImpl<>();
		graph2.addVertex(2);
		graph2.addVertex(5);
		graph2.addVertex(10);
		graph2.addVertex(100);
		graph2.addVertex(1000);
		graph2.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph2.addEdge(new DefaultBinaryEdge<Character, Integer>('i', 2, 10));
		graph2.addEdge(new DefaultBinaryEdge<Character, Integer>('j', 2, 100));
		
		graph1.addAll(graph2);
		
		assertEquals(6, graph1.size());
		assertEquals(6, graph1.order());
	}
	
	@Test
	public void testEmptyGraphMergeSelf() {
		BinaryGraph<Integer, Character> graph1 = new DefaultGraphImpl<>();
		graph1.addVertex(5);
		graph1.addVertex(6);
		graph1.addVertex(2);
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('b', 2, 6));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('c', 6, 5));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('a', 5, 2));
		graph1.addEdge(new DefaultBinaryEdge<Character, Integer>('A', 2, 5));
		
		graph1.addAll(graph1);
		
		assertEquals(4, graph1.size());
		assertEquals(3, graph1.order());
	}
}
