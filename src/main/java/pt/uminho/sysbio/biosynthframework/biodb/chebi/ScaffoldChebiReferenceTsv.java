package pt.uminho.sysbio.biosynthframework.biodb.chebi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteReferenceEntity;

public class ScaffoldChebiReferenceTsv {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ScaffoldChebiReferenceTsv.class);
	private final String SEP = "\t";
	private SessionFactory sessionFactory;
	private File file;
	
	public ScaffoldChebiReferenceTsv(SessionFactory sessionFactory, File referenceFile) {
		this.sessionFactory = sessionFactory;
		this.file = referenceFile;
	}
	
	public<T> T getOrNull(T[] array, int index) {
		if (index < array.length && index >= 0) {
			return array[index];
		}
		
		return null;
	}
	
	public void scaffold() throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = br.readLine();
		
		int i = 0;
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		while ((line = br.readLine()) != null) {
			ChebiDumpMetaboliteReferenceEntity entity = new ChebiDumpMetaboliteReferenceEntity();
			String[] columns = line.split(SEP);
			
			try {
			
				Long compoundId = Long.parseLong(columns[0]);
				String referenceId = getOrNull(columns, 1);
				String referenceDbName = getOrNull(columns, 2);
				String locationInRef = getOrNull(columns, 3);
				String referenceName = getOrNull(columns, 4);
				entity.setLocationInRef(locationInRef);
				entity.setReferenceDbName(referenceDbName);
				entity.setReferenceName(referenceName);
				entity.setReferenceId(referenceId);
				Object o = sessionFactory.getCurrentSession().get(ChebiDumpMetaboliteEntity.class, compoundId);
				ChebiDumpMetaboliteEntity cpd = ChebiDumpMetaboliteEntity.class.cast(o);
				entity.setId(i);
				entity.setChebiDumpMetaboliteEntity(cpd);
				sessionFactory.getCurrentSession().save(entity);
			} catch (IndexOutOfBoundsException e) {
				LOGGER.error("Index error for line: " + line);
				br.close();
				throw e;
			} catch (RuntimeException e) {
				LOGGER.error("Runtime exception: " + line);
			}

			i++;
			
			if ((i % 1000) == 0) {
				LOGGER.debug("@: " + i);
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
			
			
			
		}
		
		tx.commit();
		
		fis.close();
		br.close();
	}
}
