package pt.uminho.sysbio.biosynthframework.chemanalysis.cdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormat;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormatConverter;
import pt.uminho.sysbio.biosynthframework.cheminformatics.render.CdkSVGRenderer;

public class CdkMoleculeFormatConverter implements MoleculeFormatConverter {

  private final static Logger logger = LoggerFactory.getLogger(CdkMoleculeFormatConverter.class);

  @Override
  public String convert(InputStream input, 
                        MoleculeFormat in,
                        MoleculeFormat out, 
                        String...params) throws IOException {
    
    if (MoleculeFormat.MDLMolFile.equals(in) &&
        MoleculeFormat.SVG.equals(out)) {
      CdkSVGRenderer renderer = new CdkSVGRenderer();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      renderer.convertMolToSvg(input, os);
      return os.toString();
    }
    
    IAtomContainerSet containerSet = null;
    IAtomContainer container = null;

    try {
      switch (in) {
      case SMILES: containerSet = readSmiles(input); break;
      case InChI:  containerSet = readInchi(input); break;
      case MDLMolFile: containerSet = readMol(input); break;
      default: throw new IllegalArgumentException("Unsupported input format " + in);
      }

      container = containerSet.getAtomContainer(0);

      for (String param : params) {
        switch (param) {
        case "-d":
          logger.debug("AtomContainerManipulator.removeHydrogens");
          container = AtomContainerManipulator.removeHydrogens(container);
          break;
          //					case "-h": new CDKHydrogenAdder();  //FAIL !
        default: logger.warn("Ignored param: {}", param); break;
        }

      }

      switch (out) {
      case SMILES: return writeSmiles(container).trim();
      case MDLMolFile: return writeMol(container);
      case InChI: return writeInchi(container);
      default: throw new IllegalArgumentException("Unsupported output format " + out);
      }

    } catch (CDKException e) {
      throw new IOException(e.getMessage());
    }
  }

  public IAtomContainerSet readSmiles(InputStream is) throws CDKException {
    return cdkRead(new SMILESReader(is));
  }

  public String writeSmiles(IAtomContainer container) throws CDKException {
    return cdkWrite(container, new SMILESWriter());
  }

  public String writeInchi(IAtomContainer container) throws CDKException {
    return CdkWrapper.convertToInchi(container).getLeft();

  }

  public String writeMol(IAtomContainer container) throws CDKException {
    StructureDiagramGenerator structureDiagramGenerator = 
        new StructureDiagramGenerator(container);
    structureDiagramGenerator.generateCoordinates();
    MDLV2000Writer writer = new MDLV2000Writer();
    return cdkWrite(container, writer);
  }

  public IAtomContainerSet readInchi(InputStream is) throws CDKException {
    InChIToStructure inChIToStructure;
    try {
      String inchi = StringUtils.join(IOUtils.readLines(is, Charset.defaultCharset()), "");
      IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
      inChIToStructure = InChIGeneratorFactory
          .getInstance()
          .getInChIToStructure(inchi, builder);
    } catch (IOException e) {
      throw new CDKException(e.getMessage());
    }

    IAtomContainer container = inChIToStructure.getAtomContainer();
    IAtomContainerSet containerSet = new AtomContainerSet();
    containerSet.addAtomContainer(container);
    return containerSet;
  }

  public IAtomContainerSet readMol(InputStream is) throws CDKException {
    return cdkRead(new MDLV2000Reader(is));
  }

  public IAtomContainerSet cdkRead(DefaultChemObjectReader reader) throws CDKException {
    IAtomContainerSet containerSet = null;

    try {
      containerSet = reader.read(new AtomContainerSet());
      reader.close();
    } catch (IOException e) {
      throw new CDKException(e.getMessage());
    }

    //		for (IAtomContainer container : containerSet.atomContainers()) {
    //			
    //		}

    return containerSet;
  }

  public String cdkWrite(IChemObject container, DefaultChemObjectWriter writer) throws CDKException {
    try {
      StringWriter sw = new StringWriter();
      writer.setWriter(sw);
      writer.write(container);
      writer.close();
      return sw.getBuffer().toString();
    } catch (IOException e) {
      throw new CDKException(e.getMessage());
    }
  }
}
