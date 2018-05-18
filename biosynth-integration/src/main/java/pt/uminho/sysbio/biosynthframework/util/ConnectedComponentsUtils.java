package pt.uminho.sysbio.biosynthframework.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;

public class ConnectedComponentsUtils {
  
  public static<T> Set<T> getElements(ConnectedComponents<T> cc) {
    Set<T> result = new HashSet<> ();
    
    for (Set<T> set: cc) {
      result.addAll(set);
    }
    
    return result;
  }
  
  public static<T> ConnectedComponents<T> filterRemoveBySize(ConnectedComponents<T> ccs, int size) {
    ConnectedComponents<T> result = new ConnectedComponents<>();
    
    for (Set<T> set: ccs) {
      if (set.size() != size) {
        result.add(new HashSet<>(set));
      }
    }
    
    return result;
  }
  
  public static<T> ConnectedComponents<T> filter(ConnectedComponents<T> ccs,
                                                 Set<T> valid, 
                                                 boolean allowSingleTons) {
    ConnectedComponents<T> result = new ConnectedComponents<>();
    
    for (Set<T> set: ccs) {
      Set<T> filter = new HashSet<>(set);
      if (valid != null) {
        filter.retainAll(valid);
      }
      
      if (!filter.isEmpty()) {
        if (filter.size() > 1 || allowSingleTons) {
          result.add(filter);
        }
      }
    }
    
    return result;
  }
  
  /**
   * 
   * @param ccs List of connected components
   * @return a set of elements T found in all CC
   */
  public static<T> Set<T> getSharedElements(
      List<ConnectedComponents<T>> ccs) {
    final Set<T> result = new HashSet<>();
    
    ConnectedComponents<T> cc = ccs.get(0);
//  cc.forEach(s -> result.addAll(s));
    for (Set<T> o : cc) {
      result.addAll(o);
    }
    
    for (int i = 1; i < ccs.size(); i++) {
      cc = ccs.get(i);
      Set<T> elements = new HashSet<>();
//    cc.forEach(s -> elements.addAll(s));
      for (Set<T> o : cc) {
        elements.addAll(o);
      }
      
      Set<T> both = new HashSet<>(
          Sets.intersection(elements, result));
      result.clear();
      result.addAll(both);
    }
    
    return result;
  }
  
  /**
   * ?
   * @param ccs
   * @return A list of CC with all elements in CCS
   */
  public static<T> List<ConnectedComponents<T>> filter(
      List<ConnectedComponents<T>> ccs) {
    
    if (ccs == null || ccs.isEmpty()) {
      return null;
    }
    
    final Set<T> valid = getSharedElements(ccs);
    
//    System.out.println(valid.size());
    
    List<ConnectedComponents<T>> result = new ArrayList<> ();
    for (ConnectedComponents<T> cc : ccs) {
      ConnectedComponents<T> ccValid = new ConnectedComponents<>();
      for (Set<T> s : cc) {
        Set<T> sValid = new HashSet<>(Sets.intersection(s, valid));
        if (!sValid.isEmpty()) {
          ccValid.add(sValid);
        }
      }
      result.add(ccValid);
    }
    
    return result;
  }
  
  public static<T> ConnectedComponents<T> merge(ConnectedComponents<T> a, ConnectedComponents<T> b) {
    ConnectedComponents<T> result = new ConnectedComponents<>();
    
    UndirectedGraph<T, Object> g = new SimpleGraph<>(Object.class);
    
    for (Set<T> cc : a) {
      GraphUtils.addConnectedSet(g, cc);
    }
    for (Set<T> cc : b) {
      GraphUtils.addConnectedSet(g, cc);
    }
    
    List<Set<T>> ccList = GraphUtils.getConnectedComponents(g);
    
    for (Set<T> cc : ccList) {
      result.add(new HashSet<>(cc));
    }
    
    return result;
  }
  
  /**
   * Maps set A to set B
   * 
   * Example: A = ((1, 2, 3))
   * <br>
   * B = ((1, 2))
   * C = ((1, 2, 3) &rarr; &lt; 1 &rarr; (1, 2), 2 &rarr; (1, 2) &gt;)
   * 
   * @param a
   * @param b
   * @return
   */
  public static<T> Map<Set<T>, Map<T, Set<T>>> map(ConnectedComponents<T> a, ConnectedComponents<T> b) {
    Map<Set<T>, Map<T, Set<T>>> result = new HashMap<> ();
    
    for (Set<T> setA : a) {
      result.put(setA, new HashMap<T, Set<T>> ());
      for (T eA : setA) {
        Set<T> targetSet = b.getConnectedComponentOf(eA);
        if (targetSet == null) {
          targetSet = new HashSet<>();
        }

        result.get(setA).put(eA, targetSet);
      }
    }
    
    return result;
  }
  
  public static<T> Map<Integer, Integer> sizeDistribution(ConnectedComponents<T> ccs) {
    Map<Integer, Integer> result = new HashMap<> ();
    
    for (Set<T> set : ccs) {
      CollectionUtils.increaseCount(result, set.size(), 1);
    }
    
    return result;
  }
  
  public static Map<String, Integer> dbDistribution(ConnectedComponents<String> ccs) {
    Map<String, Integer> result = new HashMap<> ();
    
    for (Set<String> set : ccs) {
      for (String s : set) {
        String db_ = s.split("@")[1];
        CollectionUtils.increaseCount(result, db_, 1);
      }
    }
    
    return result;
  }
  
  public static<T> void writeCC(ConnectedComponents<T> cc, String file) {
    StringBuilder sb = new StringBuilder();
    for (Set<T> c : cc) {
      sb.append(StringUtils.join(c, '\t')).append('\n');
    }
    
    try (OutputStream os = new FileOutputStream(file)) {
      IOUtils.write(sb.toString().getBytes(), os);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static ConnectedComponents<String> loadConnectedComponents(String path) {
    ConnectedComponents<String> ccs = new ConnectedComponents<>();
    
    try (InputStream is = new FileInputStream(path)) {
      List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
      for (int i = 1 ; i < lines.size(); i++) {
        String[] s = lines.get(i).split("\t");
        ccs.add(new HashSet<> (Arrays.asList(s)));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ccs;
  }
}
