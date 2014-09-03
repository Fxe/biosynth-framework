package pt.uminho.sysbio.biosynth.chemanalysis.cdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Filipe
 *
 */
public class CdkWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CdkWrapper.class);

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
	
	public static String convertMol2dToInChI(String mol2d) {
		String inchi = null;
		try {
			MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new ByteArrayInputStream(mol2d.getBytes()));
			AtomContainer atomContainer = mdlv2000Reader.read(new AtomContainer());
			mdlv2000Reader.close();
	
			InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
			inchi = factory.getInChIGenerator(atomContainer).getInchi();
		} catch (IOException | CDKException e) {
			LOGGER.error(e.getMessage());
		}
		return inchi;
	}
	
	public static void generateSvg() {
//		SVG
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
