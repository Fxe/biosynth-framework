package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class XmlStreamProfiller {
  
  public Map<String, Integer> tagCounter = new HashMap<> ();
  public Map<String, Set<String>> namespaces = new HashMap<> ();
  
  public Map<String, Set<String>> getSharedTags() {
    Map<String, Set<String>> result = new HashMap<> ();
    for (String k : namespaces.keySet()) {
      if (namespaces.get(k).size() > 1) {
        result.put(k, namespaces.get(k));
      }
    }
    return result;
  }
  
  
  public Set<String> getNamespaces() {
    Set<String> result = new HashSet<> ();
    for (String k : namespaces.keySet()) {
      result.addAll(namespaces.get(k));
    }
    return result;
  }
  
  public void scan(InputStream is) throws IOException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    try {
//      Map<String, Integer> tagCounter = new HashMap<> ();
//      Map<String, Set<String>> namespaces = new HashMap<> ();
      
      XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(is);
      while (xmlEventReader.hasNext()) {
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        if (xmlEvent.isStartDocument()) {
          
        } else if (xmlEvent.isEndDocument()) {
          
        } else if (xmlEvent.isStartElement()) {
          StartElement element = xmlEvent.asStartElement();
          String logalPart = element.getName().getLocalPart();
          String namespace = element.getName().getNamespaceURI();
          
          CollectionUtils.increaseCount(tagCounter, logalPart, 1);
          if (!namespaces.containsKey(logalPart)) {
            namespaces.put(logalPart, new HashSet<String> ());
          }
          namespaces.get(logalPart).add(namespace);
          
        } else if (xmlEvent.isEndElement()) {
          EndElement element = xmlEvent.asEndElement();
          String logalPart = element.getName().getLocalPart();
          String namespace = element.getName().getNamespaceURI();
//          System.out.println(logalPart + " " + namespace);
        }
      }
      
      
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }
}
