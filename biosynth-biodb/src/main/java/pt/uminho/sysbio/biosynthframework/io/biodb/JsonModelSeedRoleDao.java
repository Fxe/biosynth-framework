package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedRole;

public class JsonModelSeedRoleDao {

  private static final Logger logger = LoggerFactory.getLogger(JsonModelSeedRoleDao.class);
  
  public Map<String, ModelSeedRole> data = new HashMap<> ();
  
  public JsonModelSeedRoleDao(Resource roleJson) {
    ObjectMapper m = new ObjectMapper();
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    CollectionType ctype = m.getTypeFactory()
                            .constructCollectionType(List.class, 
                                                     ModelSeedRole.class);
    
    try {
      List<ModelSeedRole> roles = m.readValue(roleJson.getInputStream(), ctype);
      for (ModelSeedRole role : roles) {
        if (role != null && role.id != null && !role.id.trim().isEmpty()) {
          if (data.put(role.id, role) != null) {
            logger.warn("duplicate ID - {}", role.id);
          }
        } else {
          logger.warn("invalid record - {}", role);
        }
      }
    } catch (IOException e) {
      logger.error("IO Error: {}", e.getMessage());
    }
  }
}
