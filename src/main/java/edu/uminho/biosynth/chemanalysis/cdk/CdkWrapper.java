package edu.uminho.biosynth.chemanalysis.cdk;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDK;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import edu.uminho.biosynth.program.InchiToCan;

/**
 * 
 * @author Filipe
 *
 */
public class CdkWrapper {
	
	private static final Logger LOGGER = Logger.getLogger(CdkWrapper.class);

	/**
	 * Wraps the CDK MolecularFormulaManipulator getString method. Uses the 
	 * {@link org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator#getMajorIsotopeMolecularFormula(String, IChemObjectBuilder) 
	 * getMajorIsotopeMolecularFormula} to build an molecular formula data structure. 
	 * Returns the string representation of the molecule formula. Based on Hill System.
	 * 
	 * @param formula the molecular formula as string
	 * @param setOne True, when must be set the value 1 for elements with one atom
	 * @return Returns the string representation of the molecule formula. Based on Hill System.
	 * @see {@link org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator#getString(IMolecularFormula, boolean)}
	 */
	public static String convertToIsotopeMolecularFormula(String formula, boolean setOne) {
		
		try {
		
			IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
			IMolecularFormula molecularFormula = MolecularFormulaManipulator
					.getMajorIsotopeMolecularFormula(formula, builder);
	
			String ret = MolecularFormulaManipulator.getString(molecularFormula, setOne);
			
			return ret;
		
		} catch (NullPointerException e) {
			LOGGER.error(e.getMessage()); 
		} catch (StringIndexOutOfBoundsException e) {
			LOGGER.error(e.getMessage()); 
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage()); 
		}

		return null;
	}
	
	public static String molToInChI(String mol2d) {
//		InChIGeneratorFactory factory = new InChIGeneratorFactory().
//		InChIToStructure a = new InChIToStructure(i)
//		Mol2Reader mol2Reader = new Mol2Reader(null);
//		mol2Reader.
//		CDK
		return null;
	}
	
	public static void generateSvg() {
//		IAtomContainer molecule;
//		
//		SVGGenerator svgGenerator = new SVGGenerator();
//		StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
//		structureDiagramGenerator.setMolecule(molecule);
//		structureDiagramGenerator.generateCoordinates();
//		
//		AtomContainerRenderer atomContainerRenderer = new AtomContainerRenderer(generators, fontManager)
//		atomContainerRenderer.paintMolecule(molecule, svgGenerator, bounds, resetCenter);
	}
}
