package pt.uminho.sysbio.biosynthframework.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class AutoFileReader implements Closeable {
  
  private Closeable closeable = null;
  private InputStreamSet iss = null;
  
  private static final Logger logger = LoggerFactory.getLogger(AutoFileReader.class);
  
  public AutoFileReader(String file, FileType ftype) {
    iss = getStreams(file, ftype);
  }
  
  public InputStreamSet getStreams() {
    return iss;
  }
  
  public InputStreamSet getStreams(String file, FileType ftype) {
    InputStreamSet result = new InputStreamSet();
    if (ftype == null || FileType.AUTO.equals(ftype)) {
      ftype = getFileType(file);
    }
    File f = new File(file);
    
    switch (ftype) {
      case ZIP:
        try {
          ZipContainer container = new ZipContainer(f.getAbsolutePath());
          closeable = container;
          List<ZipRecord> streams = container.getInputStreams();
          for (ZipRecord zr : streams) {
            InputStream is = zr.is;
            String u = file + "/" + zr.name;
            result.streams.put(u, is);
          }
        } catch (ZipException e) {
          logger.warn("not zip {}", f);
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case XML:
        try {
          InputStream is = new FileInputStream(f);
          closeable = is;
          result.streams.put(file, is);
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      default:
        logger.warn("unsupported file type: {}", ftype);
        break;
    }

    return result;
  }
  
  public static FileType getFileType(String file) {
    FileType ftype = FileType.XML;
    File f = new File(file);
    ZipContainer container = null;
    try {
      container = new ZipContainer(f.getAbsolutePath());
      ftype = FileType.ZIP;
    } catch (ZipException e) {
      logger.debug("not zip {}", f);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(container);
    }
    
    return ftype;
  }

  @Override
  public void close() throws IOException {
    if (closeable != null) {
      this.closeable.close();
    }
  }
}
