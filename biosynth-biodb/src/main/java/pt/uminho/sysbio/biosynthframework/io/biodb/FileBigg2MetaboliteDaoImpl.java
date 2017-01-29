package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class FileBigg2MetaboliteDaoImpl implements MetaboliteDao<Bigg2MetaboliteEntity> {
  
  private static final Logger logger = LoggerFactory.getLogger(FileBigg2MetaboliteDaoImpl.class);
  
  private Map<String, Bigg2MetaboliteEntity> cachedData = new HashMap<> ();
  
  @Autowired
  public FileBigg2MetaboliteDaoImpl(Resource bigg2MetaboliteFile) {
    InputStream is = null;
    try {
      is = bigg2MetaboliteFile.getInputStream();
      List<String> lines = IOUtils.readLines(is);
      //bigg_id universal_bigg_id   name    model_list  database_links
      for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i);
        Bigg2MetaboliteEntity cpd = parseBigg2MetaboliteRecord(line);
        cachedData.put(cpd.getEntry(), cpd);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
  }
  
  public static Bigg2MetaboliteEntity parseBigg2MetaboliteRecord(String record) {
    String fields[] = record.split("\t");
    
    String entry = fields[0];
    String universalEntry = fields[1];
    String name = fields[2];
    String modelListStr = fields[3];
    List<Bigg2MetaboliteCrossreferenceEntity> refs = new ArrayList<> ();
    ObjectMapper om = new ObjectMapper();
    try {
      Map<?, ?> data = om.readValue(fields[4], Map.class);
      for (Object k : data.keySet()) {
        String db = k.toString();
        Object a = data.get(k);
//        System.out.println(db);
        if (a instanceof ArrayList) {
          @SuppressWarnings("unchecked")
          ArrayList<Object> arr = ArrayList.class.cast(a);
          for (Object refO : arr) {
            @SuppressWarnings("unchecked")
            Map<String, Object> refData = (Map<String, Object>) refO;
            Bigg2MetaboliteCrossreferenceEntity xref = 
                new Bigg2MetaboliteCrossreferenceEntity(
                    ReferenceType.DATABASE, db, (String) refData.get("id"));
            xref.setLink((String) refData.get("link"));
            refs.add(xref);
          }
//          System.out.println("\t" + arr);
        } else {
          logger.warn("expected ArrayList but found {}", a.getClass().getName());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    Bigg2MetaboliteEntity cpd = new Bigg2MetaboliteEntity();
    cpd.setEntry(entry);
    cpd.setName(name);
    cpd.setUniversalEntry(universalEntry);
    for (String m : modelListStr.split(", ")) {
      cpd.getModelList().add(m.trim());
    }
    cpd.setCrossReferences(refs);

    return cpd;
  }
  
  @Override
  public Bigg2MetaboliteEntity getMetaboliteById(Serializable id) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Bigg2MetaboliteEntity getMetaboliteByEntry(String entry) {
    return this.cachedData.get(entry);
  }

  @Override
  public Bigg2MetaboliteEntity saveMetabolite(Bigg2MetaboliteEntity metabolite) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Not Supported Operation");
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    return new ArrayList<> (cachedData.keySet());
  }

  @Override
  public Serializable save(Bigg2MetaboliteEntity entity) {
    throw new RuntimeException("Not Supported Operation");
  }

}
