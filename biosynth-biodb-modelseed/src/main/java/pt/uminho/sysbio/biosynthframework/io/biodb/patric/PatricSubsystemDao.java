package pt.uminho.sysbio.biosynthframework.io.biodb.patric;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.io.biodb.patric.PatricSubsystemDao.PatricSubsystemResponse.PatricSubsystemDoc;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class PatricSubsystemDao {
  
  private static final Logger logger = LoggerFactory.getLogger(PatricSubsystemDao.class);
  
  public Map<String, Set<String>> subsystemToRoleMap = new HashMap<>();
  public Map<String, Set<String>> roleToSubsystemMap = new HashMap<>();
  
  public static class PatricSubsystemResponse {
    public Map<String, Object> responseHeader = new HashMap<>();
    public SubsystemResponse response;
    
    public static class SubsystemResponse {
      public int numFound;
      public int start;
      public List<PatricSubsystemDoc> docs = new ArrayList<> ();
    }
    
    public static class PatricSubsystemDoc {
      public String subsystem_name;
      public List<String> role_name = new ArrayList<> ();
    }
  }
  
  public static class Builder {
    public PatricSubsystemDao build(Resource roleJson) {
      
      try (InputStream is = roleJson.getInputStream()) {
        PatricSubsystemDao dao = build(is);
        return dao;
      } catch (IOException e) {
        logger.error("IO Error: {}", e.getMessage());
      }
      
      return null;
    }
    
    public PatricSubsystemDao build(InputStream is) throws IOException {
      PatricSubsystemDao dao = new PatricSubsystemDao();

      ObjectMapper om = new ObjectMapper();
      om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
      PatricSubsystemResponse response = om.readValue(is, PatricSubsystemResponse.class);
      for (PatricSubsystemDoc doc : response.response.docs) {
        if (!DataUtils.empty(doc.subsystem_name)) {
          for (String function : doc.role_name) {
            if (!DataUtils.empty(function)) {
              CollectionUtils.insertHS(doc.subsystem_name.trim(), function.trim(), dao.subsystemToRoleMap);
              CollectionUtils.insertHS(function.trim(), doc.subsystem_name.trim(), dao.roleToSubsystemMap);
            } else {
              logger.warn("empty");
            }
          }
        } else {
          logger.warn("empty");
        }
      }

      return dao;
    }
  }
  

}
