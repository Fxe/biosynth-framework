package pt.uminho.sysbio.biosynthframework.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.DatasetFactory;

public class FileDatasetDao {
  
  private static final Logger logger = LoggerFactory.getLogger(FileDatasetDao.class);
  
  private final File file;
  private final Dataset<String, String, Object> dataset;
  
  public FileDatasetDao(File file) {
    this.file = file;
    if (file.exists()) {
      logger.debug("loaded from [{}]", this.file);
      dataset = new DatasetFactory().buildFromFile(this.file);
    } else {
      logger.debug("blank dataset");
      dataset = new Dataset<>();
    }
  }
  
  public Dataset<String, String, Object> getDataset() {
    return dataset;
  }

  public void save() {
    String data = DataUtils.getTableStr(dataset.dataset, "key");
    try {
      FileWriter fw = new FileWriter(file);
      IOUtils.write(data.getBytes(), fw, Charset.defaultCharset());
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
