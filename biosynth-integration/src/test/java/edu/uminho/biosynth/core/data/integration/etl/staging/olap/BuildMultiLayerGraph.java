package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.multi.MultiLabelUI;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.MultiLayerGraph;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.util.math.components.OrderedPair;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

public class BuildMultiLayerGraph {

//	public static SessionFactory sessionFactory_stga;
//	private static IGenericDao dao_stga;
//	private static Transaction tx_stga;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory_stga = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_staging_example_pgsql.cfg.xml");
//		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
//
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		sessionFactory_stga.close();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		tx_stga.commit();
//	}
//
//	@Test
//	public void test() {
//		
//		MultiLayerGraph<Serializable, Integer, String> mlg = new MultiLayerGraph<>();
//		String layer;
//		
//		layer = "formula";
//		for (MetaboliteFormulaDim formula : dao_stga.findAll(MetaboliteFormulaDim.class)) {
//			mlg.addVertex(formula.getId(), layer);
//		}
//		
//		layer = "inchi";
//		for (MetaboliteInchiDim inchi : dao_stga.findAll(MetaboliteInchiDim.class)) {
//			mlg.addVertex(inchi.getId(), layer);
//		}
//		
//		layer = "xref";
//		for (MetaboliteXrefDim xref : dao_stga.findAll(MetaboliteXrefDim.class)) {
//			mlg.addVertex(xref.getId(), layer);
//		}
//		
//		layer = "xref_group";
//		for (MetaboliteXrefGroupDim xref_group : dao_stga.findAll(MetaboliteXrefGroupDim.class)) {
//			mlg.addVertex(xref_group.getId(), layer);
//		}
//		
//		layer = "cpd";
//		Integer edgeId = 0;
//		for (MetaboliteStga cpd : dao_stga.findAll(MetaboliteStga.class)) {
//			mlg.addVertex(cpd.getId(), layer);
//			mlg.addEdge(edgeId++, cpd.getId(), "cpd", cpd.getMetaboliteFormulaDim().getId(), "formula");
//			mlg.addEdge(edgeId++, cpd.getId(), "cpd", cpd.getMetaboliteInchiDim().getId(), "inchi");
//			mlg.addEdge(edgeId++, cpd.getId(), "cpd", cpd.getMetaboliteXrefGroupDim().getId(), "xref_group");
////			mlg.addEdge(edgeId, , dst);
//		}
//		
//		System.out.println(mlg);
//	}

}
