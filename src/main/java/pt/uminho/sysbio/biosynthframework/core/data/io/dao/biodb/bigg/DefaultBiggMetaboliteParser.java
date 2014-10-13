package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class DefaultBiggMetaboliteParser {
	
	public static String CSV_SEP = "\t";
	
	public static List<BiggMetaboliteEntity> parseMetabolites(InputStream inputStream) throws IOException {
		List<BiggMetaboliteEntity> mnxMetabolites = new ArrayList<> ();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		
		String readLine = br.readLine();
//		System.out.println(readLine);
		while ( (readLine = br.readLine()) != null) {
			mnxMetabolites.add( parseMetabolite(readLine));
		}
		
		br.close();
		return mnxMetabolites;
	}
	
	public static BiggMetaboliteEntity parseMetabolite(String record) {
		/* Example Record
		 * ENTRY  | Name      | FORMULA | CHARGE | COMPARTMENT                                              | KEGG   | CAS      | ID    | MODEL REF
		 * ala-D  | D-Alanine | C3H7NO2 | 0      | Peroxisome, Extra-organism, Cytosol, Periplasm, Lysosome | C00133 | 338-69-2 | 33977 | 1,10,2,3,4,5,7	
		 */
		String[] values = record.split(CSV_SEP);
		BiggMetaboliteEntity cpd = new BiggMetaboliteEntity();
		cpd.setEntry( values[0]);
		cpd.setName( values[1]);
		cpd.setFormula( values[2]);
		cpd.setCharge(Integer.parseInt(values[3]));
		for (String compartment : values[4].split(",")) {
			if (compartment.trim().length() > 0) cpd.getCompartments().add( compartment.trim());
		}
		if (values[5].trim().length() > 0) {
			BiggMetaboliteCrossreferenceEntity xrefKegg = new BiggMetaboliteCrossreferenceEntity(
					GenericCrossReference.Type.DATABASE, "KEGG", values[5]);
			cpd.addCrossReference(xrefKegg);
		}
		if (values[6].trim().length() > 0) {
			BiggMetaboliteCrossreferenceEntity xrefCas = new BiggMetaboliteCrossreferenceEntity(
					GenericCrossReference.Type.DATABASE, "CAS", values[6]);
			cpd.addCrossReference(xrefCas);			
		}

		cpd.setId(Long.parseLong( values[7]));
		cpd.setSource("BIGG");
		for (String modelIntValue : values[8].split(",")) {
			String modelId = convertToModelCrossReference(Integer.parseInt(modelIntValue));
			if (modelId != null) {
				BiggMetaboliteCrossreferenceEntity xrefModel = new BiggMetaboliteCrossreferenceEntity(
						GenericCrossReference.Type.MODEL, modelId, modelId);
				cpd.addCrossReference(xrefModel);
			}
		}
		return cpd;
	}
	
	private static String convertToModelCrossReference(int i) {
		/* INT - Model
		 * 1 - E. coli iJR904
		 * 2 - H. sapiens Recon 1
		 * 3 - H. pylori iIT341
		 * 4 - P. putida iJN746
		 * 5 - E. coli iAF1260
		 * 6 - S. cerevisiae iND750
		 * 7 - S. aureus iSB619
		 * 8 - E. coli textbook #????
		 * 9 - M. barkeri iAF692
		 * 10- M. tuberculosis iNJ661
		 */
		String model = null;;
		switch (i) {
			case 1 : model = "iJR904";	break;
			case 2 : model = "Recon 1";	break;
			case 3 : model = "iIT341";	break;
			case 4 : model = "iJR904";	break;
			case 5 : model = "iAF1260";	break;
			case 6 : model = "iND750";	break;
			case 7 : model = "iSB619";	break;
			case 9 : model = "iAF692";	break;
			case 10: model = "iNJ661";	break;
			default:
				model = null;
				break;
		}
		
		return model;
	}
}
