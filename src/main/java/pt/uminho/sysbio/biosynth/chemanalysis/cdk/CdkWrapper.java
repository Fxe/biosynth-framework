package pt.uminho.sysbio.biosynth.chemanalysis.cdk;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.FormulaReader;

/**
 * 
 * @author Filipe
 *
 */
public class CdkWrapper implements FormulaReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CdkWrapper.class);

	@Override
	public Map<String, Integer> getAtomCountMap(String formula) {
		Map<String, Integer> atomMap = null; 
		try {
			atomMap = new HashMap<> ();
			IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
			IMolecularFormula molecularFormula = MolecularFormulaManipulator
					.getMajorIsotopeMolecularFormula(formula, builder);
			IAtomContainer container = MolecularFormulaManipulator.getAtomContainer(molecularFormula);
			for (IAtom atom : container.atoms()) {
				CollectionUtils.increaseCount(atomMap, atom.getSymbol(), 1);
			}
		} catch (NullPointerException e) {
			LOGGER.error(String.format("NullPointer [%s] - %s", formula, e.getMessage())); 
		} catch (StringIndexOutOfBoundsException e) {
			LOGGER.error(String.format("StringIndexOutOfBounds [%s] - %s", formula, e.getMessage())); 
		} catch (NumberFormatException e) {
			LOGGER.error(String.format("NumberFormat [%s] - %s", formula, e.getMessage())); 
		}
		
		return atomMap;
	}
	
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
			LOGGER.error(String.format("NullPointer [%s] - %s", formula, e.getMessage())); 
		} catch (StringIndexOutOfBoundsException e) {
			LOGGER.error(String.format("StringIndexOutOfBounds [%s] - %s", formula, e.getMessage())); 
		} catch (NumberFormatException e) {
			LOGGER.error(String.format("NumberFormat [%s] - %s", formula, e.getMessage())); 
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
	
	public static void generateSvg(IAtomContainer atomContainer) {
		
//		SVG
//		IAtomContainer molecule;
//		StructureDiagramGenerator a = new StructureDiagramGenerator(atomContainer);
//		a.
		
//		SVGGenerator svgGenerator = new SVGGenerator();
//		StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
//		structureDiagramGenerator.setMolecule(molecule);
//		structureDiagramGenerator.generateCoordinates();
//		
//		AtomContainerRenderer atomContainerRenderer = new AtomContainerRenderer(generators, fontManager)
//		atomContainerRenderer.paintMolecule(molecule, svgGenerator, bounds, resetCenter);
	}

	@Override
	public String convertToIsotopeMolecularFormula(String formula, boolean setOne) {
		return CdkWrapper.toIsotopeMolecularFormula(formula, setOne);
	}
	
	
	public static void main(String[] args) {
		IChemObjectBuilder     builder = SilentChemObjectBuilder.getInstance();
		SmilesParser           sp      = new SmilesParser(builder);
		try {
			IAtomContainer pep = sp.parseSmiles("O=C(O)C(OP(=O)(O)O)=C");
			LOGGER.info("Atoms {}", pep.getAtomCount());
			IAtomContainer mol1 = sp.parseSmiles("CC1=CC(Br)CCC1");
			LOGGER.info("Atoms {}", mol1.getAtomCount());
//			Image
//			IMolecue
			List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
	        generators.add(new BasicSceneGenerator());
	        generators.add(new BasicBondGenerator());
//	        generators.add(new RingPlateGenerator());
	        generators.add(new BasicAtomGenerator());
			AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());
			IAtomContainer triazole = MoleculeFactory.make123Triazole();
	        RendererModel model = renderer.getRenderer2DModel();
			Image image = new BufferedImage(400, 400, BufferedImage.TYPE_4BYTE_ABGR);
	        Graphics2D g = (Graphics2D)image.getGraphics();
	        g.setColor(Color.WHITE);
	        g.fill(new Rectangle2D.Double(0, 0, 400, 400));
	        renderer.paint(triazole, new AWTDrawVisitor(g), 
	                new Rectangle2D.Double(0, 0, 400, 400), true);
	        g.dispose();
	        File file = new File("D:/mol1.png");
	        ImageIO.write((RenderedImage)image, "PNG", file);

		} catch (InvalidSmilesException e) {
			LOGGER.error("IS - {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("IO - {}", e.getMessage());
			e.printStackTrace();
		}
//		System.out.println(new CdkWrapper().getAtomCountMap("CH5O9Mg5.NaCl.Ti(FeHe3)4"));
	}
}
