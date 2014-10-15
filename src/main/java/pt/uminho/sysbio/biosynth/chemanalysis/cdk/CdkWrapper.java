package pt.uminho.sysbio.biosynth.chemanalysis.cdk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.DefaultCaret;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.renderer.AbstractRenderer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.jchempaint.renderer.Renderer;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.font.IFontManager;
import org.openscience.jchempaint.renderer.generators.AtomContainerBoundsGenerator;
import org.openscience.jchempaint.renderer.generators.AtomContainerTitleGenerator;
import org.openscience.jchempaint.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.generators.IGenerator;
import org.openscience.jchempaint.renderer.visitor.SVGGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.FormulaConverter;

/**
 * 
 * @author Filipe
 *
 */
public class CdkWrapper implements FormulaConverter {
	
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
	public static String toIsotopeMolecularFormula(String formula, boolean setOne) {
		
		try {
		
			IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
			IMolecularFormula molecularFormula = MolecularFormulaManipulator
					.getMajorIsotopeMolecularFormula(formula, builder);
	
			String ret = MolecularFormulaManipulator.getString(molecularFormula, setOne);
			
			return ret;
		
		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException " + e.getMessage()); 
		} catch (StringIndexOutOfBoundsException e) {
			LOGGER.error("StringIndexOutOfBoundsException " + e.getMessage()); 
		} catch (NumberFormatException e) {
			LOGGER.error("NumberFormatException " + e.getMessage()); 
		}

		return null;
	}
	
	public static IAtomContainer readMol2d(String mol2d) throws IOException, CDKException {
		IAtomContainer atomContainer = null;

		MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new ByteArrayInputStream(mol2d.getBytes()));
		atomContainer = mdlv2000Reader.read(new AtomContainer());
		mdlv2000Reader.close();
		
		return atomContainer;
	}
	
	public static String convertToUniqueSmiles(IAtomContainer atomContainer) throws CDKException {
		return SmilesGenerator.unique().create(atomContainer);
	}
	
	public static String convertToAbsoluteSmiles(IAtomContainer atomContainer) throws CDKException {
		return SmilesGenerator.absolute().create(atomContainer);
	}
	
	public static String convertToGenericSmiles(IAtomContainer atomContainer) throws CDKException {
		return SmilesGenerator.generic().create(atomContainer);
	}
	
	public static String convertToIsomericSmiles(IAtomContainer atomContainer) throws CDKException {
		return SmilesGenerator.isomeric().create(atomContainer);
	}
	
	public static Pair<String, String> convertToInchi(IAtomContainer atomContainer) throws CDKException {
		Pair<String, String> inchiTuple = null;
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
		InChIGenerator generator = factory.getInChIGenerator(atomContainer);
		
		String inchi = generator.getInchi();
		String inchiKey = null; 
		
		if (inchi != null) {
			inchiKey = generator.getInchiKey();
			inchiTuple = new ImmutablePair<> (inchi, inchiKey);
		}
		
		return inchiTuple;
	}
	
	public static Pair<String, String> convertMol2dToInChI(String mol2d) {
		Pair<String, String> inchiTuple = null;

		try {
			inchiTuple = convertToInchi(readMol2d(mol2d));

		} catch (IOException | CDKException e) {
			LOGGER.error(e.getMessage());
		}
		
		return inchiTuple;
	}
	
	public static String generateSvg(IAtomContainer atomContainer) {
		
//		SVG
//		IAtomContainer molecule;
//		StructureDiagramGenerator a = new StructureDiagramGenerator(atomContainer);
//		a.
//		Intermediat
		List<IGenerator> o = new ArrayList<> ();
//		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
		BasicBondGenerator bond = new BasicBondGenerator(); o.add(bond);
//		System.out.println(2.4d);
//		bond.setOverrideColor(Color.BLACK);
//		o.add(new org.openscience.jchempaint.renderer.generators.BasicAtomGenerator());
		
//		o.add(new org.openscience.jchempaint.renderer.generators.AtomNumberGenerator());
//		o.add(new AtomContainerBoundsGenerator());
//		o.add(new AtomContainerTitleGenerator());
		IFontManager fontManager = new AWTFontManager();
		Renderer renderer = new Renderer(o, fontManager, false);
		
		org.openscience.cdk.renderer.ChemModelRenderer modelRenderer;
//		renderer.p
		org.openscience.cdk.renderer.RendererModel rendererModel;
		
		AbstractRenderer<?> abstractRenderer;
//		I
		org.openscience.jchempaint.renderer.IRenderer r;
//		org.openscience.jchempaint.renderer.visitor.
		IChemModel iChemModel;
//		r.
//		abstractRenderer.
		SVGGenerator svgGenerator = new SVGGenerator();
		renderer.paintMolecule(atomContainer, svgGenerator, new Rectangle(40, 40), true );
//		svgGenerator
//		rendererModel.
//		svgGenerator.
//		renderer.pa
		
//		StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
//		structureDiagramGenerator.setMolecule(molecule);
//		structureDiagramGenerator.generateCoordinates();
//		
//		AtomContainerRenderer atomContainerRenderer = new AtomContainerRenderer(generators, fontManager)
//		atomContainerRenderer.paintMolecule(molecule, svgGenerator, bounds, resetCenter);
		
		return svgGenerator.getResult().replace(',', '.');
	}

	@Override
	public String convertToIsotopeMolecularFormula(String formula, boolean setOne) {
		return CdkWrapper.toIsotopeMolecularFormula(formula, setOne);
	}
}
