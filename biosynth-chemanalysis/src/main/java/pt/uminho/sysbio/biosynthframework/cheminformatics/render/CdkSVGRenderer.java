package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;

public class CdkSVGRenderer extends AbstractSVGRenderer {
  
  @Override
  public void convertMolToSvg(InputStream is, OutputStream os) throws IOException {
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
  
      DepictionGenerator depictionGenerator = new DepictionGenerator()
          .withSize(75, 75).withAtomColors();
      depictionGenerator.depict(container).writeTo("svg", os);
      reader.close();
    } catch (CDKException e) {
      throw new IOException("CDK: " + e.getMessage());
    }
  }




}
