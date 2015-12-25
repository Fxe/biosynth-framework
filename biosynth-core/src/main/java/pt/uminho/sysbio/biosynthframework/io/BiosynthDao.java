package pt.uminho.sysbio.biosynthframework.io;

import java.util.Map;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public interface BiosynthDao<D extends AbstractBiosynthEntity> {
  
  /**
   * Looks up an entity by id. Note that the id is usually a 
   * surrogate key, which means it is bad practice to refer them in
   * this way. Use the entry instead which is a database specified
   * identifier. 
   * 
   * @param id the id of the entity
   * @return the entity with id <code>id</code> if found
   */
  public D findById(long id);
  
  /**
   * Looks up an entity entity by it's entry.
   * 
   * @param entry the entry of the entity
   * @return the entity with entry <code>entry</code> if found
   */
  public D findByEntry(String entry);
  
  /**
   * 
   * @param entity the entity entity to be inserted
   * 
   * @return the generated id (only if the id is set as null otherwise the
   * assigned id is used)
   */
  public Long save(D entity);
  
  public boolean update(D entity);
  
  public boolean delete(D entity);
  
  /**
   * 
   * @return 
   */
  public Map<Long, String> list();
}
