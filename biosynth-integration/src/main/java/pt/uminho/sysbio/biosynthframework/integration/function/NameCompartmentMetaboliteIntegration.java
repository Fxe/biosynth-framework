package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameCompartmentMetaboliteIntegration implements Function<Set<String>, Set<Set<String>>>{

  private static final Logger logger = LoggerFactory.getLogger(NameCompartmentMetaboliteIntegration.class);

  private Set<String> compartments = new HashSet<>();
  private Map<String, String> spiToName = new HashMap<>();

  public NameCompartmentMetaboliteIntegration(Map<String, String> spiToName, Set<String> compartments) {
    this.compartments.addAll(compartments);
    this.spiToName.putAll(spiToName);
    logger.info("compartments: {}", this.compartments);
  }

  @Override
  public Set<Set<String>> apply(Set<String> t) {
    Map<String, Set<String>> mapping = new HashMap<>();
    Set<String> compartmentsSuffixes = new HashSet<>();
    for (String s : compartments) {
      compartmentsSuffixes.add(String.format("[%s]", s));
    }

    for (String spiEntry : spiToName.keySet()) {
      String s = spiToName.get(spiEntry);
      for (String suffix : compartmentsSuffixes) {
        if (s.endsWith(suffix)) {
          int sindex = s.indexOf(suffix);
          s = s.substring(0, sindex);
          break;
        }
      }

      logger.debug("{} -> {}", spiToName.get(spiEntry), s);

      if (!mapping.containsKey(s)) {
        mapping.put(s, new HashSet<String>());
      }
      mapping.get(s).add(spiEntry);
    }

    logger.info("specie to metabolite: {} -> {}", t.size(), mapping.size());

    return new HashSet<>(mapping.values());
  }
}
