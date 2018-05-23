package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.InputStreamResource;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;

public class InternalBigg1MetaboliteDaoImpl extends CsvBiggMetaboliteDaoImpl
implements BiosDao<BiggMetaboliteEntity>{
  
  public InternalBigg1MetaboliteDaoImpl() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("bigg1_metabolites.tsv");
    this.setCsvFile(new InputStreamResource(is));
    this.initialize();
  }

  @Override
  public BiggMetaboliteEntity getByEntry(String entry) {
    return this.getMetaboliteByEntry(entry);
  }

  @Override
  public BiggMetaboliteEntity getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(BiggMetaboliteEntity o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(BiggMetaboliteEntity o) {
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
    return new HashSet<>(this.getAllMetaboliteEntries());
  }
}
