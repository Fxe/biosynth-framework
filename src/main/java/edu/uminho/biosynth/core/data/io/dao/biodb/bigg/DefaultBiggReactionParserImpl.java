package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.Orientation;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionCrossReferenceEntity;

public class DefaultBiggReactionParserImpl implements BiggReactionParser {
	
	public static String CSV_SEP = "\t";

	private BiggEquationParser equationParser;
	
	public BiggEquationParser getEquationParser() { return equationParser;}
	public void setEquationParser(BiggEquationParser equationParser) { this.equationParser = equationParser;}

	public List<BiggReactionEntity> parseReactions(InputStream inputStream) throws IOException {
		List<BiggReactionEntity> rxnReactions = new ArrayList<> ();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

		String readLine = br.readLine();
//		System.out.println(readLine);
		while ( (readLine = br.readLine()) != null) {
//			System.out.println(readLine);
			
			try {
			
			rxnReactions.add( parseReaction(readLine));
			
			} catch (Exception e) {
				System.out.println(readLine);
				throw e;
			}
			
		}
		
		br.close();
		return rxnReactions;
	}
	
	public BiggReactionEntity parseReaction(String record) {
		/* Example Record
		 * ENTRY  | NAME                                      | SYN | EQUATION                        | COMPARTMENT | ECN | REVERSIBLE   | TRANSLOCATION | ID      | MODEL REF
         * MTCMMT | Methylthiol: coenzyme M methyltransferase |     | [c] : com + dms --> ch4s + mcom | Cytosol     |     | Irreversible | N             | 1800860 | 9
		 */
		String[] values = record.split(CSV_SEP);
		
		BiggReactionEntity rxn = new BiggReactionEntity();
		rxn.setEntry(values[0]);
		rxn.setName(values[1]);
		for (String synonym : values[2].split(";")) {
			if (synonym.trim().length() > 0) rxn.getSynonyms().add( synonym.trim());
		}
		rxn.setEquation(values[3]);
		
		this.equationParser.setEquation(rxn.getEquation());
		this.equationParser.parse();
		rxn.setLeft(this.equationParser.getLeft());
		rxn.setRight(this.equationParser.getRight());
		
		for (String compartment : values[4].split(",")) {
			if (compartment.trim().length() > 0) rxn.getCompartments().add( compartment.trim());
		}
		rxn.setEnzyme(values[5]);
		rxn.setOrientation(values[6].equals("Reversible") ? Orientation.Reversible : Orientation.LeftToRight);
		rxn.setTranslocation(values[7].equals("N") ? false:true);
		rxn.setId(Long.parseLong(values[8]));
		for (String modelIntValue : values[9].split(",")) {
			String modelId = convertToModelCrossReference(Integer.parseInt(modelIntValue));
			if (modelId != null) {
				BiggReactionCrossReferenceEntity xrefModel = new BiggReactionCrossReferenceEntity(
						GenericCrossReference.Type.MODEL, modelId, modelId);
				rxn.addCrossReference(xrefModel);
			}
		}
		rxn.setSource("BIGG");
		
		return rxn;
	}
	
	public String convertToModelCrossReference(int i) {
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
