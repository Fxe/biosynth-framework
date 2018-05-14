package pt.uminho.sysbio.biosynthframework.util;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipContainer implements Closeable, AutoCloseable {
  
  public static class ZipRecord implements Closeable, AutoCloseable {
    public String name;
    public long size;
    public long compressedSize;
    public int method;
    public InputStream is;
    
    @Override
    public void close() throws IOException {
      this.is.close();
    }
    
    @Override
    public String toString() {
      return String.format("ZipRecord[%s]", name);
    }
  }

  private final InputStream is; //file input stream
  private final ZipInputStream zis; //zip file manipulator
  private final ZipFile zf; //zip file pointer
  
  public ZipContainer(String path) throws IOException {
    this.is = new FileInputStream(path);
    this.zis = new ZipInputStream(is);
    this.zf = new ZipFile(path);
  }
  
  public List<ZipRecord> getInputStreams() throws IOException {
    List<ZipRecord> streams = new ArrayList<> ();
    ZipEntry ze = null;
    while ((ze = zis.getNextEntry()) != null) {
      ZipRecord record = new ZipRecord();
      record.name = ze.getName();
      record.size = ze.getSize();
      record.compressedSize = ze.getCompressedSize();
      record.method = ze.getMethod();
      record.is = zf.getInputStream(ze);
      
      streams.add(record);
    }
    
    return streams;
  }
  
  @Override
  public void close() throws IOException {
    this.zf.close();
    this.zis.close();
    this.is.close();
  }
}
