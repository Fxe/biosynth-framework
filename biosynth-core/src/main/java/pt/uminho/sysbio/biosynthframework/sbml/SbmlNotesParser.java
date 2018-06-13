package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SbmlNotesParser {
  
  public static enum Fields {
    FORMULA,
    CHARGE,
    SMILES,
    EC_NUMBER,
    SUBSYSTEM,
    GENE_ASSOCIATION,
  }
  
  private static final Logger logger = LoggerFactory.getLogger(SbmlNotesParser.class);
  
  public List<XmlMessage> messages = new ArrayList<> ();
  public Map<String, String> fields = new HashMap<> ();
  private String html;
  private Set<String> texts = new HashSet<>();
  private final Set<String> bad = new HashSet<>();
  
  private final List<Tuple2<String>> data = new ArrayList<> ();
  
  public SbmlNotesParser() {
    this.fields.putAll(getDefaults());
  }
  
  public SbmlNotesParser(String html) {
    this.fields.putAll(getDefaults2());
    this.html = html;
    this.parse(html);
  }
  
  public List<Tuple2<String>> getData() { return data;}
  public Set<String> getBad() { return bad;}

  public static void processTexts(Elements es, Set<String> texts) {
    for (Element e : es) {
//      System.out.println("!" + e + " " + e.text());
//      System.out.println("----------");
      if (!DataUtils.empty(e.ownText())) {
        texts.add(e.ownText().trim());
      }
      processTexts(e.children(), texts);
    }
  }
  
  public void parse(String html) {
    Document document = Jsoup.parse(html);
    processTexts(document.children(), texts);
//    System.out.println(document);
    for (String text : texts) {
      if (StringUtils.countMatches(text, ":") == 1) {
        String[] p = text.split(":");
        if (p.length > 1) {
          String mapping = fields.get(p[0].trim().toUpperCase());
          if (mapping != null) {
            data.add(new Tuple2<String>(mapping, p[1].trim()));
          } else {
            bad.add(text);
          }
        }
      } else {
        bad.add(text);
      }
    }
  }
  
  public void parse() {
    this.parse(html);
  }
  
  public static Map<String, String> getDefaults2() {
    Map<String, String> fields = new HashMap<> ();
    fields.put("FORMULA", Fields.FORMULA.toString());
    fields.put("SMILES", Fields.SMILES.toString());
    fields.put("CHARGE", Fields.CHARGE.toString());
    
//    fields.put("EQUATION", "equation");
    fields.put("GPR_ASSOCIATION", Fields.GENE_ASSOCIATION.toString());
    fields.put("GENE_ASSOCIATION", Fields.GENE_ASSOCIATION.toString());
    fields.put("GENE ASSOCIATION", Fields.GENE_ASSOCIATION.toString());
//    fields.put("GENE_LIST", "gene_list");
    
//    fields.put("GENE_NAME", "protein_association");
//    fields.put("PROTEIN_ASSOCIATION", "protein_association");
    fields.put("SUBSYSTEM", Fields.SUBSYSTEM.toString());
//    fields.put("PROTEIN_CLASS", "protein_class");
//    fields.put("CONFIDENCE LEVEL", "confidence_level");
    fields.put("EC NUMBER", Fields.EC_NUMBER.toString());
    fields.put("EC_NUMBER", Fields.EC_NUMBER.toString());
    fields.put("EC", Fields.EC_NUMBER.toString());
    
//    fields.put("PROTEIN_NAME", "reaction_name");
    
    
//    fields.put("AUTHORS", "authors");
//    fields.put("COMPARTMENT", "compartment");
//    fields.put("KEGG ID", "kegg");
//    fields.put("KEGG_RID", "kegg");
//    fields.put("PUBCHEM ID", "pubchem");
//    fields.put("CHEBI ID", "chebi");
    
    return fields;
  }
  
  public static Map<String, String> getDefaults() {
    Map<String, String> fields = new HashMap<> ();
    fields.put("FORMULA:", "formula");
    fields.put("SMILES:", "smiles");
    
    fields.put("CHARGE:", "charge");
    fields.put("EQUATION:", "equation");
    fields.put("GPR_ASSOCIATION:", "gene_association");
    fields.put("GENE_ASSOCIATION:", "gene_association");
    fields.put("GENE ASSOCIATION:", "gene_association");
    fields.put("GENE_LIST:", "gene_list");
    
    fields.put("GENE_NAME:", "protein_association");
    fields.put("PROTEIN_ASSOCIATION:", "protein_association");
    fields.put("SUBSYSTEM:", "subsystem");
    fields.put("PROTEIN_CLASS:", "protein_class");
    fields.put("CONFIDENCE LEVEL:", "confidence_level");
    fields.put("EC NUMBER:", "ec_number");
    fields.put("EC_NUMBER:", "ec_number");
    fields.put("EC_Number:", "ec_number");
    
    fields.put("PROTEIN_NAME:", "reaction_name");
    
    fields.put("EC:", "ec_number");
    fields.put("AUTHORS:", "authors");
    fields.put("COMPARTMENT:", "compartment");
    fields.put("KEGG ID:", "kegg");
    fields.put("KEGG_RID:", "kegg");
    fields.put("PUBCHEM ID:", "pubchem");
    fields.put("CHEBI ID:", "chebi");
    
    return fields;
  }

  public static String extractString(String string) {
    String[] split = string.replace("</p>", "").split(":");
    if (split.length > 1) {
      List<String> tokens = new ArrayList<> ();
      for (int i = 1; i < split.length; i++) {
        tokens.add(split[i]);
      }
      String data = StringUtils.join(tokens, ':').trim();
      if (!data.isEmpty()) {
        return data;
      }
    }
    
    return null;
  }
  
  public static List<String> parseNotes(String xmlNotes) {
    List<String> result = new ArrayList<> ();
    
    if (DataUtils.empty(xmlNotes)) {
      return result;
    }
    
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    try {
      XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
          new ByteArrayInputStream(xmlNotes.getBytes()));
      while (xmlEventReader.hasNext()) {
        XMLEvent event = xmlEventReader.nextEvent();
        if (event.isCharacters()) {
          result.add(event.asCharacters().getData());
        }
      }
    } catch (XMLStreamException e) {
      e.printStackTrace(); 
    }
    
    return result;
  }
  
  @Deprecated
  public Map<String, String> parseNotes(List<String> notes) {
    Map<String, String> data = new HashMap<> ();

    
    for (String line : notes) {
      if (line.replaceAll(" ", "").equals("<p></p>")) {
        logger.debug("skipped empty paragraph");
      } else {
        boolean detected = false;
        for (String field : fields.keySet()) {
          if (line.toUpperCase().contains(field)) {
            detected = true;
            String str = extractString(line);
            if (str != null) {
              if (data.put(fields.get(field), str) != null) {
                messages.add(new XmlMessage(null, 
                    MessageCategory.NULL, MessageType.ERROR, "duplicate field ec_number %s -> %s", fields.get(field), field));
                logger.debug("duplicate field {} -> {}", fields.get(field), field);
              }
            }
          }
        }
        
        if (!detected) {
          messages.add(new XmlMessage(null, 
              MessageCategory.NULL, MessageType.ERROR, "Unknown field %s", line));
          logger.debug("Unknown field: {}", line);
        }
      }

    }
    
    return data;
  }
  
  public Map<String, Set<String>> parseNotes2(List<String> notes) {
    Map<String, Set<String>> data = new HashMap<> ();

    
    for (String line : notes) {
      if (line.replaceAll(" ", "").equals("<p></p>")) {
        logger.debug("skipped empty paragraph");
      } else {
        boolean detected = false;
        for (String field : fields.keySet()) {
          if (line.toUpperCase().contains(field)) {
            detected = true;
            String str = extractString(line);
            if (str != null) {
              if (!data.containsKey(fields.get(field))) {
                data.put(fields.get(field), new HashSet<String> ());
              }
              data.get(fields.get(field)).add(str);
//              if (data.put(fields.get(field), str) != null) {
////                messages.add(new XmlMessage(null, MessageType.ERROR, "duplicate field ec_number %s -> %s", fields.get(field), field));
////                logger.debug("duplicate field {} -> {}", fields.get(field), field);
//              }
            }
          }
        }
        
        if (!detected) {
          messages.add(new XmlMessage(null, 
              MessageCategory.NULL, MessageType.ERROR, "Unknown field %s", line));
          logger.debug("Unknown field: {}", line);
        }
      }

    }
    
    return data;
  }
}
