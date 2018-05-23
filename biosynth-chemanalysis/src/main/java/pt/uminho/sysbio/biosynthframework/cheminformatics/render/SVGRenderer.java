package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SVGRenderer {
  
  public void convertMolToSvg(InputStream is, OutputStream os) throws IOException;
  public String convertMolToSvg(String mol);
  public String convertMolToSvg(File mol);
  public void convertMolToSvgFile(File molIn, File svgOut);
}
