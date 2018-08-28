package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormat;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormatConverter;
import pt.uminho.sysbio.biosynthframework.cheminformatics.SMARTS;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;

public class SmartsViewAPI implements MoleculeFormatConverter {
  
  
  
  public SmartsViewAPI() {
    // TODO Auto-generated constructor stub
  }
  
  public String toSvg(InputStream is) throws IOException {
    String str = IOUtils.toString(is, Charset.defaultCharset());
    return toSvg(new SMARTS(str));
  }
  
  public String toSvg(SMARTS smarts) {
    try {
      String decode = URLEncoder.encode(smarts.toString(), "UTF-8");
    URI url;
    try {
      url = new URI("http://smartsview.zbh.uni-hamburg.de/auto/svg/1/both/" + decode);
      String data = BiosIOUtils.download(url);
      return data;
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
    }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public String convertSmarts(InputStream input, MoleculeFormat out, String... param) throws IOException {
    switch (out) {
      case SVG: return toSvg(input);
      default:
        break;
      }
    return null;
  }

  @Override
  public String convert(InputStream input, MoleculeFormat in, MoleculeFormat out, String... param) throws IOException {
    switch (in) {
      case RSMARTS: break;
      case SMARTS: return convertSmarts(input, out, param);
      default:
        break;
      }
    return null;
  }
}
