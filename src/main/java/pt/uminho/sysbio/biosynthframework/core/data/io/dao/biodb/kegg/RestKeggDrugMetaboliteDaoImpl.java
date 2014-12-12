package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggDrugMetaboliteFlatFileParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

public class RestKeggDrugMetaboliteDaoImpl
extends AbstractRestfulKeggDao implements MetaboliteDao<KeggDrugMetaboliteEntity>{

	private static final Logger LOGGER = LoggerFactory.getLogger(RestKeggDrugMetaboliteDaoImpl.class);
	private static final String restDrQuery = "http://rest.kegg.jp/get/dr:%s";
	private static final String restDrMolQuery = "http://rest.kegg.jp/get/dr:%s/mol";

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		List<Serializable> cpdIds = new ArrayList<>();
		String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "dr");
		String localPath = this.getLocalStorage() + "query" + "/drug.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
//				dr:D10517\tCrisantaspase (JAN)
				String[] values = httpResponseLine[i].split("\\t");
//				remove dr:
				cpdIds.add(values[0].substring(3));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpdIds;
	}

	@Override
	public Serializable save(KeggDrugMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeggDrugMetaboliteEntity getMetaboliteById(Serializable id) {
		String restDrQuery = String.format(RestKeggDrugMetaboliteDaoImpl.restDrQuery, id);
		String restDrMolQuery = String.format(RestKeggDrugMetaboliteDaoImpl.restDrMolQuery, id);
		
		String localPath = this.getLocalStorage()  + "dr" + "/" + id;
		
		KeggDrugMetaboliteEntity cpd = null;
		
		String drFlatFile = null;
		String drMolFile = null;
		try {
			drFlatFile = getLocalOrWeb(restDrQuery, localPath + ".txt");
			if (drFlatFile == null) return null;
			drMolFile = getLocalOrWeb(restDrMolQuery, localPath + ".mol");
			
			if (drMolFile == null) {
				BioSynthUtilsIO.writeToFile("null", localPath + ".mol");
			}
			
//			System.out.println(drFlatFile);

		
			KeggDrugMetaboliteFlatFileParser parser = new KeggDrugMetaboliteFlatFileParser(drFlatFile);
//		System.out.println(parser.getTabs());
			cpd = new KeggDrugMetaboliteEntity();
			
			cpd.setEntry(parser.getEntry());
			cpd.setName(parser.getName());
			cpd.setFormula(parser.getFormula());
			cpd.setMass(parser.getMass());
			cpd.setMolWeight(parser.getMolWeight());
			cpd.setActivity(parser.getActivity());
			cpd.setTarget(parser.getTarget());
			cpd.setMetabolism(parser.getMetabolism());
			cpd.setRemark(parser.getRemark());
			cpd.setComment(parser.getComment());
			if (drMolFile != null && !drMolFile.isEmpty()) {
				cpd.setMol2d(drMolFile);
			}
			cpd.setCrossReferences(parser.getCrossReferences());
			cpd.setProduct(parser.getProduct());
			cpd.setSequence(parser.getSequence());
			cpd.setDrugSource(parser.getSource());
			cpd.setStrMap(parser.getStructureMap());
			cpd.setOtherMap(parser.getOtherMap());
			cpd.setComponent(parser.getComponent());
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
//			LOGGER.debug(e.getStackTrace());
			return null;
		}
		return cpd;
	}
	
	public String getMetaboliteFlatFile(Serializable id) {
		String restDrQuery = String.format(RestKeggDrugMetaboliteDaoImpl.restDrQuery, id);
		String localPath = this.getLocalStorage()  + "dr" + "/" + id;
		
		String drFlatFile = null;
		try {
			drFlatFile = getLocalOrWeb(restDrQuery, localPath + ".txt");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return drFlatFile;
	}

	@Override
	public KeggDrugMetaboliteEntity saveMetabolite(
			KeggDrugMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public KeggDrugMetaboliteEntity getMetaboliteByEntry(String entry) {
		return this.getMetaboliteById(entry);
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> cpdIds = new ArrayList<>();
		String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "dr");
		String localPath = this.getLocalStorage() + "query" + "/drug.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
//				dr:D10517\tCrisantaspase (JAN)
				String[] values = httpResponseLine[i].split("\\t");
//				remove dr:
				cpdIds.add(values[0].substring(3));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpdIds;
	}

}
