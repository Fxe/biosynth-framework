package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public interface BiosDao<T extends AbstractBiosynthEntity> {
  public T getByEntry(String entry);
  public T getById(long id);
  public Long save(T o);
  public boolean delete(T o);
  
  /**
   * List all metabolite id
   * 
   * @return a set that contains all ids
   */
  public Set<Long> getAllIds();
  
  /**
   * List all metabolite entry
   * 
   * @return a set that contains all entries
   */
  public Set<String> getAllEntries();
}
