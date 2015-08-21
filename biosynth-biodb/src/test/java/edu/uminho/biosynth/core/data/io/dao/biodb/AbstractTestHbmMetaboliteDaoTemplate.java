package edu.uminho.biosynth.core.data.io.dao.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Rule;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.data.test.mother.AbstractHbmObjectMother;
import pt.uminho.sysbio.data.test.rule.SessionFactoryRule;

public abstract class AbstractTestHbmMetaboliteDaoTemplate<M extends GenericMetabolite> {

	@Rule
	public final SessionFactoryRule sessionFactoryRule;
	
	public AbstractHbmObjectMother<M> hbmObjectMother;
	public MetaboliteDao<M> metaboliteDao;
	
	public String entry;
	public Long id;
	
	public SessionFactory getSessionFactory() {
		return sessionFactoryRule.getSessionFactory();
	}
	
	public AbstractTestHbmMetaboliteDaoTemplate(SessionFactoryRule sessionFactoryRule) {
		this.sessionFactoryRule = sessionFactoryRule;
	}
	
	@Test
	public void testGetAllMetaboliteIds() {
		
		hbmObjectMother.instance();
		
		List<Serializable> entryIds = metaboliteDao.getAllMetaboliteIds();
		
		assertNotNull(entryIds);
		assertEquals(1, entryIds.size());
	}

	@Test
	public void testGetAllMetaboliteEntries() {
		
		hbmObjectMother.instance();
		
		List<String> entryStrings = metaboliteDao.getAllMetaboliteEntries();
		
		assertNotNull(entryStrings);
		assertEquals(1, entryStrings.size());
	}
	
	@Test
	public void testGetAllMetaboliteEntriesEmpty() {	
		List<String> entryStrings = metaboliteDao.getAllMetaboliteEntries();
		
		assertNotNull(entryStrings);
		assertEquals(0, entryStrings.size());
	}

	@Test
	public void testSaveMetaboliteSuccess() {
		
		M cpd = this.hbmObjectMother.instanceNonPersist();
		metaboliteDao.saveMetabolite(cpd);
		
		M cpdEntry = metaboliteDao.getMetaboliteByEntry(cpd.getEntry());
		assertNotNull(cpdEntry);
		M cpdId = metaboliteDao.getMetaboliteById(cpd.getId());
		assertNotNull(cpdId);
	}
	
	@Test
	public void testSaveMetaboliteFail() {
		
		M cpd = this.hbmObjectMother.instanceNonPersist();
		cpd.setEntry("");
		cpd.setId(null);
		metaboliteDao.saveMetabolite(cpd);
	}
	
	@Test
	public void testGetMetaboliteByEntrySuccess() {
		hbmObjectMother.instance();
		
		M cpd = metaboliteDao.getMetaboliteByEntry(entry);
		
		assertNotNull(cpd);
	}
	
//	@Test
//	public void testGetMetaboliteByIdSuccess() {
//		M instance = hbmObjectMother.instance();
//		Long id = this.id == null ? instance.getId():this.id;
//		M cpd = metaboliteDao.getMetaboliteById(id);
//		
//		assertNotNull(cpd);
//	}
	
	@Test
	public void testGetMetaboliteByEntryFail() {
		M cpd = metaboliteDao.getMetaboliteByEntry("abcdef123456gg");

		assertNull(cpd);
	}
	
	@Test
	public void testGetMetaboliteByIdFail() {
		M cpd = metaboliteDao.getMetaboliteById(999999L);
		
		assertNull(cpd);
	}
}
