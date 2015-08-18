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

import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyRelationEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyVertexEntity;

public class ScaffoldChebiOntologyTsv {
	private final static Logger LOGGER = LoggerFactory.getLogger(ScaffoldChebiOntologyTsv.class);
	private final String SEP = "\t";
	private SessionFactory sessionFactory;
	private File ontologyFile;
	private File relationFile;
	private File verticeFile;
	
	public ScaffoldChebiOntologyTsv(SessionFactory sessionFactory, 
			File ontologyFile, 
			File relationFile, 
			File verticeFile) {
		this.sessionFactory = sessionFactory;
		this.ontologyFile = ontologyFile;
		this.relationFile = relationFile;
		this.verticeFile = verticeFile;
	}
	
	public void scaffold() throws FileNotFoundException, IOException {
		scaffoldOntology();
		scaffoldVertice();
		scaffoldRelation();
	}
	
	public void scaffoldOntology() throws IOException {
		FileInputStream fis = new FileInputStream(ontologyFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = br.readLine();
		
		int i = 0;
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(SEP);
			Long id = Long.parseLong(columns[0]);
			String title = columns[1];
			ChebiDumpOntologyEntity ontology = new ChebiDumpOntologyEntity();
			ontology.setId(id);
			ontology.setTitle(title);
			
			sessionFactory.getCurrentSession().save(ontology);
			
			i++;
			if ((i % 1000) == 0) {
				LOGGER.info("@: " + i);
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
		
		fis.close();
		br.close();
	}
	
	public void scaffoldVertice() throws IOException {
		FileInputStream fis = new FileInputStream(verticeFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = br.readLine();
		
		int i = 0;
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(SEP);
			Long id = Long.parseLong(columns[0]);
			Long compound_child_id = Long.parseLong(columns[1]);
			Long ontology_id = Long.parseLong(columns[2]);
			String vertice_ref = columns[3];
			ChebiDumpOntologyEntity ontology = ChebiDumpOntologyEntity.class.cast(
					sessionFactory.getCurrentSession().get(ChebiDumpOntologyEntity.class, ontology_id));
			
			ChebiDumpOntologyVertexEntity vertex = new ChebiDumpOntologyVertexEntity();
			vertex.setId(id);
			vertex.setCompoundId(compound_child_id);
			vertex.setChebiDumpOntologyEntity(ontology);
			vertex.setVerticeRef(vertice_ref);
			
			sessionFactory.getCurrentSession().save(vertex);
			
			i++;
			if ((i % 1000) == 0) {
				LOGGER.info("@: " + i);
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
		
		fis.close();
		br.close();
	}
	
	public void scaffoldRelation() throws IOException {
		FileInputStream fis = new FileInputStream(relationFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = br.readLine();
		
		int i = 0;
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(SEP);
			Long id = Long.parseLong(columns[0]);
			String type = columns[1];
			Long init_id = Long.parseLong(columns[2]);
			Long final_id = Long.parseLong(columns[3]);
			String status = columns[4];
			ChebiDumpOntologyVertexEntity from = ChebiDumpOntologyVertexEntity.class.cast(
					sessionFactory.getCurrentSession().get(ChebiDumpOntologyVertexEntity.class, init_id));
			ChebiDumpOntologyVertexEntity to = ChebiDumpOntologyVertexEntity.class.cast(
					sessionFactory.getCurrentSession().get(ChebiDumpOntologyVertexEntity.class, final_id));
			ChebiDumpOntologyRelationEntity relation = new ChebiDumpOntologyRelationEntity();
			relation.setId(id);
			relation.setFrom(from);
			relation.setTo(to);
			relation.setType(type);
			relation.setStatus(status);
			sessionFactory.getCurrentSession().save(relation);
			i++;
			if ((i % 1000) == 0) {
				LOGGER.info("@: " + i);
				tx.commit();
				tx = sessionFactory.getCurrentSession().beginTransaction();
			}
		}
		
		tx.commit();
		
		fis.close();
		br.close();
	}
}
