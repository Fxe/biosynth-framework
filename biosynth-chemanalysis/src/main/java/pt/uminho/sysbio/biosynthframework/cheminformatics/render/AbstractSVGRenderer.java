package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractSVGRenderer implements SVGRenderer {
  
  @Override
  public abstract void convertMolToSvg(InputStream is, 
                                       OutputStream os) throws IOException;
  
  @Override
  public String convertMolToSvg(String mol) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      convertMolToSvg(new ByteArrayInputStream(mol.getBytes()), os);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return os.toString();
  }

  @Override
  public String convertMolToSvg(File mol) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    
    try (InputStream is = new FileInputStream(mol)) {
      convertMolToSvg(is, os);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return os.toString();
  }

  @Override
  public void convertMolToSvgFile(File mol, File svgOut) {
    try (InputStream is = new FileInputStream(mol); 
         OutputStream os = new FileOutputStream(svgOut)){
      this.convertMolToSvg(is, os);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
