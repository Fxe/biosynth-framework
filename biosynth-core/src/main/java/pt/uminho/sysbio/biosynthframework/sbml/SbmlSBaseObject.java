package pt.uminho.sysbio.biosynthframework.sbml;

public class SbmlSBaseObject extends XmlObject {
  
  /**
   * The <b>notes</b> element
   */
  protected String notes;
  
  /**
   * The <b>annotation</b> element
   */
  protected String annotation;

  public String getNotes() { return notes;}
  public void setNotes(String notes) { this.notes = notes;}
  
  public String getAnnotation() { return annotation;}
  public void setAnnotation(String annotation) { this.annotation = annotation;}
}
