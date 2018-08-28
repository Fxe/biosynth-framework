package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedRole;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;

public class JsonModelSeedRoleDao implements BiosDao<ModelSeedRole> {

  private static final Logger logger = LoggerFactory.getLogger(JsonModelSeedRoleDao.class);
  
  private Map<String, ModelSeedRole> data = new HashMap<> ();
  
  public static class Builder {
    public JsonModelSeedRoleDao build(Resource roleJson) {
      
      try (InputStream is = roleJson.getInputStream()) {
        JsonModelSeedRoleDao dao = build(is);
        return dao;
      } catch (IOException e) {
        logger.error("IO Error: {}", e.getMessage());
      }
      
      return null;
    }
    
    public JsonModelSeedRoleDao build(InputStream roleJson) throws IOException {
      JsonModelSeedRoleDao dao = new JsonModelSeedRoleDao();

      ObjectMapper m = new ObjectMapper();
      m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
      CollectionType ctype = m.getTypeFactory().constructCollectionType(List.class, 
                                                                        ModelSeedRole.class);

      List<ModelSeedRole> roles = m.readValue(roleJson, ctype);
      for (ModelSeedRole role : roles) {
        if (role != null && role.id != null && !role.id.trim().isEmpty()) {
          if (dao.data.put(role.id, role) != null) {
            logger.warn("duplicate ID - {}", role.id);
          }
        } else {
          logger.warn("invalid record - {}", role);
        }
      }

      return dao;
    }
  }
  
  private JsonModelSeedRoleDao() {}
  
  @Deprecated
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

  @Override
  public ModelSeedRole getByEntry(String entry) {
    return this.data.get(entry);
  }

  @Override
  public ModelSeedRole getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(ModelSeedRole o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(ModelSeedRole o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    return new HashSet<>(data.keySet());
  }
}
