package pt.uminho.sysbio.biosynth.integration.etl;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggEquationParserImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggReactionParserImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.factory.BiggDaoFactory;

public class TestAnnotationEntityToGraphEntityTransform {

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
	public void test_bigg_reaction() {
		
		CsvBiggReactionDaoImpl daoSrc = 
				new BiggDaoFactory().withFile(new File("D:/home/data/bigg/BiGGreactionList_test.tsv"))
									.withBiggEquationParser(new DefaultBiggEquationParserImpl())
									.withBiggReactionParser(new DefaultBiggReactionParserImpl())
									.buildCsvBiggReactionDao();
		BiggReactionEntity biggReactionEntity = daoSrc.getReactionByEntry("ALOX12R");
		
		AnnotationEntityToGraphEntityTransform<BiggReactionEntity> entityTransform =
				new AnnotationEntityToGraphEntityTransform<BiggReactionEntity>();
		
//		GraphReactionEntity graphReactionEntity = entityTransform.etlTransform(biggReactionEntity);
		
//		System.out.println(graphReactionEntity);
	}

}
