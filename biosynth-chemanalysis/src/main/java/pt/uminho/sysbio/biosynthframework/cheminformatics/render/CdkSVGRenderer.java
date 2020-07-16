package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class CdkSVGRenderer extends AbstractSVGRenderer {
  
  @Override
  public void convertMolToSvg(InputStream is, OutputStream os) throws IOException {
    generateSvg(is, os);
  }
  
  public void convertSmiToSvg(InputStream is, OutputStream os) throws IOException {
    generateSvg(is, os);
  }
  
  public static void renderSvg(File molFile, File svgFile) throws IOException {
    OutputStream os = new FileOutputStream(svgFile);
    InputStream is = new FileInputStream(molFile);
    
    generateSvg(is, os);
    
    is.close();
    os.close();
  }
  
  public static void generateSvg(InputStream is, OutputStream os) throws IOException {
    try {
      IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
      MDLV2000Reader reader = new MDLV2000Reader(is);
      IAtomContainer container = reader.read(builder.newInstance(IAtomContainer.class));
      generateSvg(container, os);
      reader.close();
    } catch (CDKException e) {
      throw new IOException("CDK: " + e.getMessage());
    }
  }
  
  public static void generateSmiSvg(InputStream is, OutputStream os) throws IOException {
    try {
      SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
      String smi = IOUtils.readLines(is, Charset.defaultCharset()).get(0);
      IAtomContainer container = sp.parseSmiles(smi);
      StructureDiagramGenerator generator = new StructureDiagramGenerator(container);
      generator.generateCoordinates();
      container = generator.getMolecule();
      generateSvg(container, os);
    } catch (CDKException e) {
      throw new IOException("CDK: " + e.getMessage());
    }
  }

  public static void generateSvg(IAtomContainer container, OutputStream os) throws CDKException, IOException {
    DepictionGenerator depictionGenerator = new DepictionGenerator()
        .withSize(75, 75).withAtomColors();
    depictionGenerator.depict(container).writeTo("svg", os);
  }


}
