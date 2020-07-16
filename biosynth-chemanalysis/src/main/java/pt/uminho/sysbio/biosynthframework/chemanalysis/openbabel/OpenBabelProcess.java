package pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ProcessWorker;

public class OpenBabelProcess {
  private final static Logger logger = LoggerFactory.getLogger(OpenBabelProcess.class);

  public static String DEFAULT_PATH = "obabel.exe";

  public String obabelPath = DEFAULT_PATH;

  public OpenBabelProcess() { }

  public OpenBabelProcess(String obabelPath) {
    this.obabelPath = obabelPath;
  }

  public String[] execute(String stdin) throws IOException {		
    return execute(stdin, new HashMap<String, String> ());
  }

  public String[] execute(String stdin, String ...params) throws IOException {
    Map<String, String> args = new HashMap<> ();
    for (String p : params) {
      args.put(p, null);
    }

    return execute(stdin, args);
  }

  public String[] execute(String stdin, Map<String, String> args) throws IOException {
    List<String> argL = new ArrayList<> ();

    argL.add(this.obabelPath);

    for (String k : args.keySet()) {
      String v = args.get(k);
      argL.add(k);
      if (v != null) argL.add(v);
    }

    ProcessBuilder pb = new ProcessBuilder(argL.toArray(new String[0]));
    logger.debug("Process: {}", StringUtils.join(pb.command(), ' '));
    final Process process = pb.start();
    IOUtils.write(stdin, process.getOutputStream(), Charset.defaultCharset());

    ProcessWorker worker = new ProcessWorker(process);
    Thread thread = new Thread(worker);
    thread.start();
    
    try {
      process.getOutputStream().close();
      thread.join();
    } catch (InterruptedException ex) {
      thread.interrupt();
      ex.printStackTrace();
    } finally {
      process.destroy();
    }

    StringBuilder stdout = new StringBuilder();
    StringBuilder stderr = new StringBuilder();
    stdout.append(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
    stderr.append(IOUtils.toString(process.getErrorStream(), Charset.defaultCharset()));

    String[] output = new String[3];
    output[0] = stdout.toString();
    output[1] = stderr.toString();
    output[2] = StringUtils.join(argL, ' ');

    return output;
  }
}
