package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class CreateDb {

//	static final String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db/";
//	private static GraphDatabaseService service;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		service = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		service.shutdown();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void test() {
//
//			
//			
//		try ( Transaction tx = service.beginTx() )
//		{
//			service.schema()
//		            .constraintFor( DynamicLabel.label( "InChI" ) )
//		            .assertPropertyIsUnique( "inchi" )
//		            .create();
//			service.schema()
//			        .constraintFor( DynamicLabel.label( "Compound" ) )
//			        .assertPropertyIsUnique( "textKey" )
//			        .create();
//			service.schema()
//			        .constraintFor( DynamicLabel.label( "Compound" ) )
//			        .assertPropertyIsUnique( "numericKey" )
//			        .create();
//			service.schema()
//			        .constraintFor( DynamicLabel.label( "Formula" ) )
//			        .assertPropertyIsUnique( "formula" )
//			        .create();
//			service.schema()
//			        .constraintFor( DynamicLabel.label( "Name" ) )
//			        .assertPropertyIsUnique( "name" )
//			        .create();
//		    tx.success();
//		    tx.close();
//		}
//		
//	}

}
