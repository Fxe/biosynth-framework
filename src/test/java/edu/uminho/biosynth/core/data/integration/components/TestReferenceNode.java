package edu.uminho.biosynth.core.data.integration.components;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;

public class TestReferenceNode {

	@Test
	public void testCreateNullObject() {
		ReferenceNode node = new ReferenceNode(null, null);
		assertEquals(null, node.getEntry());
		assertEquals(null, node.getEntityType());
	}

	@Test
	public void testCreateReferenceNode() {
		ReferenceNode node = new ReferenceNode(null, null);
		node.setEntry("C00001");
		node.setEntityType(KeggMetaboliteEntity.class);
		node.addIdServicePair(123456, "KEGG Live Service");
		assertEquals("C00001", node.getEntry());
		assertEquals(KeggMetaboliteEntity.class, node.getEntityType());
	}
	
	@Test
	public void testEqualsTrue() throws Exception {
		ReferenceNode node0 = new ReferenceNode(null, null);
		ReferenceNode node1 = new ReferenceNode(null, null);
		ReferenceNode node2 = new ReferenceNode(null, null);
		node2.setEntityType(Class.forName("java.util.HashSet"));
		ReferenceNode node3 = new ReferenceNode(null, null);
		node3.setEntityType(Class.forName("java.util.HashSet"));
		
		assertEquals(true, node0.equals(node1));
		assertEquals(true, node1.equals(node0));
		assertEquals(true, node2.equals(node3));
		assertEquals(true, node3.equals(node2));
	}
	
	@Test
	public void testSetContainsTrue() {
		ReferenceNode node0 = new ReferenceNode(null, null);
		node0.setEntry("C00001");
		node0.setEntityType(KeggMetaboliteEntity.class);
		node0.addIdServicePair(123456, "KEGG Live Service");
		
		Set<ReferenceNode> set = new HashSet<> ();
		set.add(node0);
		
		ReferenceNode node1 = new ReferenceNode(null, null);
		node1.setEntry("C00001");
		node1.setEntityType(KeggMetaboliteEntity.class);
		node1.addIdServicePair(45645, "KEGG Static Service");
		
		assertEquals(true, set.contains(node0));
		assertEquals(true, set.contains(node1));
	}
}
