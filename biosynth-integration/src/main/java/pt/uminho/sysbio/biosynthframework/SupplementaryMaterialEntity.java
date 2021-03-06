package pt.uminho.sysbio.biosynthframework;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SupplementaryMaterialEntity extends AbstractBiosynthEntity {

  
  private static final Logger logger = LoggerFactory.getLogger(SupplementaryMaterialEntity.class);
  private static final long serialVersionUID = 1L;
  
  protected URL url;
  protected Long size;
  protected String md5;
  protected Boolean literature;
  protected File file;
  protected File folder;
  protected String type;
  protected Set<String> tags = new TreeSet<>();
  
  public List<SupplementaryMaterialEntity> files = new ArrayList<> ();
  
  public URL getUrl() {
    return url;
  }
  public void setUrl(String url) {
    if (!DataUtils.empty(url)) {
      try {
        this.url = new URL(url);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }

  }
  public Long getSize() {
    return size;
  }
  public void setSize(Long size) {
    this.size = size;
  }
  public String getMd5() {
    return md5;
  }
  public void setMd5(String md5) {
    this.md5 = md5;
  }
  public Boolean getLiterature() {
    return literature;
  }
  public void setLiterature(Boolean literature) {
    this.literature = literature;
  }
  public File getFile() {
    return file;
  }
  public void setFile(File file) {
    this.file = file;
  }
  public void setFile(String filename) {
    if (!DataUtils.empty(filename)) {
      if (folder != null) {
        this.file = new File(this.folder.getAbsoluteFile() + "/" + filename);
      } else {
        logger.warn("cannot set file with empty folder");
      }
    }
  }
  public File getFolder() {
    return folder;
  }
  public void setFolder(File folder) {
    this.folder = folder;
  }
  
  
  
  public String getType() { return type;}
  public void setType(String type) { this.type = type; }
  
  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags;}
  public void setTags(String tags) {
    if (!DataUtils.empty(tags)) {
      this.tags.addAll(Arrays.asList(tags.split(";")));
    }
  }
  
  @Override
  public String toString() {
    return String.format("%s %.2f KB", file != null ? file.getName() : "?", size != null ? size/1024.0 : 0.0);
  }
}
