package pt.uminho.sysbio.biosynth.integration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

/**
 * 
 * @author Filipe Liu
 *
 */
public interface BiodbService {
  public String getEntryById(long id);
  public Long getIdByEntryAndDatabase(String entry, String database);
  public String getNamePropertyById(long id);
  public String getFormulaPropertyById(long id);
  public Map<Long, Double> getStoichiometry(long id);
  public List<Pair<Long, Double>> getRawStoichiometry(long id);
  public Set<Long> getIdsByStoichiometry(Map<Long, Double> stoich);
  
  public Set<Long> getIdsByDatabaseAndType(String database, String type);
  
  public String getDatabaseById(long id);
  
  public Long getSpecieCompartmentId(long spiId);
  
  /**
   * Returns integrated {@link SubcellularCompartment SubcellularCompartment}
   * from specie Id. Returns <tt>UNKNOWN</tt> if compartment is not integrated.
   * Returns overridden compartment if found.
   * @param cmpId ID of compartment
   * @return
   */
  public SubcellularCompartment getCompartmentSubcellularLocation(long cmpId);
  
  /**
   * Returns integrated {@link SubcellularCompartment SubcellularCompartment}
   * from specie Id. Returns <tt>UNKNOWN</tt> if compartment is not integrated.
   * Returns overridden compartment if found.
   * @param spiId ID of specie
   * @return
   */
  public SubcellularCompartment getSpecieSubcellularLocation(long spiId);
  
  /**
   * Returns references from entities
   * @param id
   * @return
   */
  public Set<Long> getReferencesBy(long id);
  
  
  public Pair<Set<Long>, Set<Long>> getReactionsByCpd(long cpdId);
  
  
  /**
   * Checks if ID is integrated
   * @param id entity ID
   * @return
   */
  public boolean isIntegrated(long id);
  
  public Long getIntegratedId(long id);
  
  public Set<Long> getMembersByCtrId(long id);
  
  public void setIntegrationId(long itgId);
  
  public long getIntegrationId();
  
  /**
   * Transform ID collection into aliases
   * @param ids of the entities
   * @return
   */
  public List<String> getAliasFromIds(Collection<Long> ids);
  
  /**
   * Expand references
   * @param cpdIdSet
   * @param itgId
   * @return
   */
  public Map<Set<Long>, Set<Long>> expandReferences(Set<Long> cpdIdSet, long itgId);
  
  /**
   * Collect species that are annotated by the compounds
   * @param cpdIdSet
   * @return a map organized by model and species
   */
  public Map<Long, Set<Long>> getSpeciesFromReferences(Set<Long> cpdIdSet);
  
  /**
   * 
   * @param spiId
   * @param itgId
   * @return
   */
  public Set<Long> getSpecieUnification(long spiId, long itgId);
  
  public Map<Long, Long> getMetaboliteUnificationMap();
  public Map<Long, Long> getReactionUnificationMap();
  public Map<Long, Long> getModelSpecieUnificationMap();
  public Map<Long, Long> getModelReactionUnificationMap();
}
