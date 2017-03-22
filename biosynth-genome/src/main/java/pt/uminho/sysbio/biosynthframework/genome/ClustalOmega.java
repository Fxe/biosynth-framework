package pt.uminho.sysbio.biosynthframework.genome;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClustalOmega {
  
  private static Logger logger = LoggerFactory.getLogger(ClustalOmega.class);
  
  String binPath = ".";
  
  public ClustalOmega() { }
  
  public ClustalOmega(String path) {
    this.binPath = path;
  }

  public String[] execute(String stdin, String...args) throws IOException {

    logger.debug(StringUtils.join(args, ' '));
    logger.trace(stdin);

    StringBuilder stdout = new StringBuilder();
    StringBuilder stderr = new StringBuilder();

    ProcessBuilder processBuilder = new ProcessBuilder(args);
    final Process process = processBuilder.start();

    IOUtils.write(stdin, process.getOutputStream());
    process.getOutputStream().close();

    stdout.append(IOUtils.toString(process.getInputStream()));
    stderr.append(IOUtils.toString(process.getErrorStream()));

    String[] output = new String[2];
    output[0] = stdout.toString();
    output[1] = stderr.toString();

    logger.trace(output[0]);
    logger.trace(output[1]);

    return output;
  }
  
  public String malign(String sequences) throws IOException {
    String[] output = execute(sequences, binPath + "clustalo", "--infile=-");
    return output[0];
  }
}
