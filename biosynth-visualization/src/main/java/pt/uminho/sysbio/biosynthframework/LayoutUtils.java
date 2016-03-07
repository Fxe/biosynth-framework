package pt.uminho.sysbio.biosynthframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uminho.biosynth.visualization.escher.EscherCanvas;
import edu.uminho.biosynth.visualization.escher.EscherMap;
import edu.uminho.biosynth.visualization.escher.EscherNode;
import edu.uminho.biosynth.visualization.escher.EscherReaction;
import edu.uminho.biosynth.visualization.escher.EscherReactionSegment;
import pt.uminho.sysbio.biosynthframework.LayoutNode.LayoutNodeType;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout.LayoutEdge;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout.LayoutReaction;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.Reaction;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class LayoutUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(LayoutUtils.class);
  
  public static SubcellularCompartment identityCompartment(String entry) {
    String entry_ = entry;
    SubcellularCompartment cmp = null;
    String suffix = StringUtils.substringAfterLast(entry_, "_");
    switch (suffix) {
      case "c": //cytosol
        cmp = SubcellularCompartment.CYTOSOL;
        break;
      case "e": //exterior
        cmp = SubcellularCompartment.EXTRACELLULAR;
        break;
      case "b": //boundary
        cmp = SubcellularCompartment.BOUNDARY;
        break;
      case "n": //??
        cmp = SubcellularCompartment.NUCLEUS;
        break;
      case "g": //??
        cmp = SubcellularCompartment.GOLGI;
        break;
      case "m": //mitochondria
        cmp = SubcellularCompartment.MITOCHONDRIA;
        break;
      case "x": //??peroxisome/glyoxysome
        cmp = SubcellularCompartment.PEROXISOME;
        break;
      case "r": //endoplasmic reticulum
        cmp = SubcellularCompartment.RETICULUM;
        break;
      case "v": //??
        cmp = SubcellularCompartment.VACUOLE;
        break;
      case "p": //perixome
        cmp = SubcellularCompartment.PERIPLASM;
        break;
      default:
        logger.warn("unknown suffix [{}]", suffix);
        break;
    }
    
    if (cmp == null) {
      logger.warn("Unable to determine compartment for {}", entry);
    }
    return cmp;
  }
  
  public static String stripCompartment(String entry) {
    String entry_ = entry;
    String suffix = StringUtils.substringAfterLast(entry_, "_");
    switch (suffix) {
      case "c": //cytosol
        entry_ = StringUtils.removeEnd(entry_, "_c");
        break;
      case "e": //exterior
        entry_ = StringUtils.removeEnd(entry_, "_e");
        break;
      case "b": //boundary
        entry_ = StringUtils.removeEnd(entry_, "_b");
        break;
      case "n": //??
        entry_ = StringUtils.removeEnd(entry_, "_n");
        break;
      case "g": //??
        entry_ = StringUtils.removeEnd(entry_, "_g");
        break;
      case "m": //mitochondria
        entry_ = StringUtils.removeEnd(entry_, "_m");
        break;
      case "x": //??
        entry_ = StringUtils.removeEnd(entry_, "_x");
        break;
      case "r": //??
        entry_ = StringUtils.removeEnd(entry_, "_r");
        break;
      case "v": //??
        entry_ = StringUtils.removeEnd(entry_, "_v");
        break;
      case "p": //perixome
        entry_ = StringUtils.removeEnd(entry_, "_p");
        break;
      case "l": //lysosome
        entry_ = StringUtils.removeEnd(entry_, "_l");
        break;
      default:
        logger.warn("unknown suffix [{}]", suffix);
        break;
    }
    return entry_;
  }
  
  public static String cleanEntry(String entry) {
    String entry_ = entry.replaceFirst("M_", "");
    entry_ = stripCompartment(entry_);
    
    if (entry_.endsWith("_e")) {
      entry_ = stripCompartment(entry_);
    }
    
    entry_ = entry_.replace("__", "_");
    entry_ = entry_.replace("_APOS_", "'");
    entry_ = entry_.replace("_FSLASH_", "/");
    entry_ = entry_.replace("_LPAREN_", "(");
    entry_ = entry_.replace("_RPAREN_", ")");
    entry_ = entry_.replace("_COMMA_", ",");
    entry_ = entry_.replace("_DASH_", "-");
    entry_ = entry_.replace("_L", "-L");
    entry_ = entry_.replace("_B", "-B");
    entry_ = entry_.replace("_D", "-D");
    entry_ = entry_.replace("_R", "-R");
    entry_ = entry_.replace("_C", "-C");
    entry_ = entry_.replace("_S", "-S");
    entry_ = entry_.replace("_T", "-T");
    entry_ = entry_.replace("_2", "-2");
    entry_ = entry_.replace("-SC", "_SC");
    return entry_;
  }
  
  public static EscherMap loadEscherMap(InputStream is) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    JsonNode node = mapper.readTree(is);
    EscherMap escherMap = mapper.readValue(node.get(0).toString(), 
                                           EscherMap.class);
    
    Map<Long, EscherReaction> reactions = mapper.readValue(
          node.get(1).get("reactions").toString(), 
          new TypeReference<Map<Long, EscherReaction>>() {});
    Map<Long, EscherNode> nodes = mapper.readValue(
        node.get(1).get("nodes").toString(), 
        new TypeReference<Map<Long, EscherNode>>() {});
    EscherCanvas canvas = mapper.readValue(node.get(1).get("canvas").toString(), 
                                           EscherCanvas.class);
    
    escherMap.nodes = nodes;
    escherMap.reactions = reactions;
    escherMap.canvas = canvas;
    
    return escherMap;
  }
  
  public static MetabolicLayout toMetabolicLayout(
      EscherMap map, MetaboliteDao<?> metaboliteDao, ReactionDao<?> reactionDao) {
    MetabolicLayout layout = new MetabolicLayout();
    
    long nodeId = 0;
    long edgeId = 0;
    Map<Long, Long> idMap = new HashMap<> ();
    Set<Long> identifiedRxnNodes = new HashSet<> ();
    for (long id : map.nodes.keySet()) {
      EscherNode node = map.nodes.get(id);
      if (node.bigg_id != null) {
        String cpdEntry = cleanEntry(node.bigg_id);
        Metabolite cpd = metaboliteDao.getMetaboliteByEntry(cpdEntry);

        
        LayoutNode layoutNode = new LayoutNode();
        layoutNode.id = nodeId;
        layoutNode.label = cpdEntry;
        layoutNode.type = LayoutNodeType.SPECIE;
        layoutNode.compartment = LayoutUtils.identityCompartment(node.bigg_id);
        layoutNode.x = node.x;
        layoutNode.y = node.y;
        
        if (cpd == null) {
          logger.warn("Not found: {} - {}", node.bigg_id, cpdEntry);
          layout.nodes.put(layoutNode.id, layoutNode);
        } else {
          layout.addLayoutNode("BiGG", cpdEntry, layoutNode);
        }
        
        nodeId++;
        
        idMap.put(id, layoutNode.id);
        
      }
    }
    
    for (long id : map.reactions.keySet()) {
      EscherReaction erxn = map.reactions.get(id);
      Reaction rxn = reactionDao.getReactionByEntry(erxn.bigg_id);
      Map<String, Set<Long>> reactionNodes = new HashMap<> ();
      
      for (EscherReactionSegment segment : erxn.segments.values()) {

        long fromNodeId = Long.parseLong(segment.from_node_id);
        long toNodeId = Long.parseLong(segment.to_node_id);
        EscherNode fromNode = map.nodes.get(fromNodeId);
        EscherNode toNode = map.nodes.get(toNodeId);
        
        if (!reactionNodes.containsKey(fromNode.node_type)) {
          reactionNodes.put(fromNode.node_type, new HashSet<Long> ());
        }
        if (!reactionNodes.containsKey(toNode.node_type)) {
          reactionNodes.put(toNode.node_type, new HashSet<Long> ());
        }
        
        reactionNodes.get(fromNode.node_type).add(fromNodeId);
        reactionNodes.get(toNode.node_type).add(toNodeId);
      }
      
      
      identifiedRxnNodes.add(id);
      if (reactionNodes.get("midmarker").size() != 1) {
        //midmarker is the rxn marker it self must be 1
        //cry because something is wrong !
      } else {
        long midmarkerId = reactionNodes.get("midmarker").iterator().next();
        EscherNode rxnNode = map.nodes.get(midmarkerId);
        
        LayoutNode primaryNode = new LayoutNode();
        primaryNode.id = nodeId++;
        primaryNode.label = erxn.bigg_id;
        primaryNode.type = LayoutNodeType.REACTION;
        primaryNode.x = rxnNode.x;
        primaryNode.y = rxnNode.y;
        LayoutReaction layoutReaction = new LayoutReaction();
        layoutReaction.primary = primaryNode;
        if (reactionNodes.containsKey("multimarker")) {
          for (long markerId : reactionNodes.get("multimarker")) {
            EscherNode erxnMarkerNode = map.nodes.get(markerId);
            LayoutNode markerNode = new LayoutNode();
            markerNode.id = nodeId++;
            markerNode.label = erxn.bigg_id;
            markerNode.type = LayoutNodeType.REACTION_MARKER;
            markerNode.x = erxnMarkerNode.x;
            markerNode.y = erxnMarkerNode.y;
            layoutReaction.markers.add(markerNode);
            idMap.put(markerId, markerNode.id);
          }
        }
        idMap.put(midmarkerId, primaryNode.id);
        
        
        if (rxn == null) {
          logger.warn("Reaction not found: {}", erxn.bigg_id);
          layout.nodes.put(layoutReaction.primary.id, layoutReaction.primary);
          Set<Long> markers = new HashSet<> ();
          for (LayoutNode n : layoutReaction.markers) {
            markers.add(n.id);
            layout.nodes.put(n.id, n);
          }
        } else {
          layout.addLayoutReaction("BiGG", erxn.bigg_id, layoutReaction);
        }
        
      }
        
      
    }
    
    for (long id : identifiedRxnNodes) {
      EscherReaction erxn = map.reactions.get(id);
      for (EscherReactionSegment segment : erxn.segments.values()) {
        long fromNodeId = Long.parseLong(segment.from_node_id);
        long toNodeId = Long.parseLong(segment.to_node_id);
        try {
          LayoutEdge edge = new LayoutEdge();
          edge.fromNodeId = idMap.get(fromNodeId);
          edge.toNodeId = idMap.get(toNodeId);
          layout.edges.put(edgeId++, edge);
          
        } catch (NullPointerException e) {
          logger.error("{}({}) - {}({}) {} {}", 
              fromNodeId, idMap.containsKey(fromNodeId), 
              toNodeId, idMap.containsKey(toNodeId), 
              map.nodes.get(fromNodeId), map.nodes.get(toNodeId));
        }
      }
    }
    
    return layout;
  }
}
