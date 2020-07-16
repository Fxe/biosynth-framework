package pt.uminho.sysbio.biosynthframework.io;

public enum FileType {
  TXT("txt"),
  XML("xml"), SBML("sbml"),
  MATLAB("m"),
  XLS("xls"), XLSX("xlsx"), DOC("doc"), DOCX("docx"),
  ZIP("zip");
  
  private String name = null;
  
  private FileType(String alias) {
    name = alias;
  }
  
  public String value() {
    return name;
  }
}
