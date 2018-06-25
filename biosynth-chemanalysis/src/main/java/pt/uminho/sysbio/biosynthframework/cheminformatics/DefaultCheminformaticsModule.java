package pt.uminho.sysbio.biosynthframework.cheminformatics;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkMoleculeFormatConverter;
import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkWrapper;
import pt.uminho.sysbio.biosynthframework.chemanalysis.inchi.JniInchiMoleculeFormatConverter;
import pt.uminho.sysbio.biosynthframework.chemanalysis.opsin.OpsinWrapper;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class DefaultCheminformaticsModule implements CheminformaticsModule {
  
  private static final Logger logger = LoggerFactory.getLogger(DefaultCheminformaticsModule.class);
  
  private CdkWrapper cdk;
  private SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
  private JniInchiMoleculeFormatConverter inchiConverter;
  
  public String getUniversalSmiles(IAtomContainer ac) {
    SmilesGenerator sg = new SmilesGenerator(SmiFlavor.UniversalSmiles);
    try {
      String result = sg.create(ac);
      return result;
    } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | CDKException e) {
      return null;
    }
  }
  
  public String getSmiles(IAtomContainer ac) {
    SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Default);
    try {
      String result = sg.create(ac);
      return result;
    } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | CDKException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String nameToInchi(String name) {
    return OpsinWrapper.iupacToInchi(name);
  }

  @Override
  public String molToInchi(String mol) {
    String inchi = null;
    try {
      IAtomContainer ac = CdkWrapper.readMol2d(mol);
      InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
      InChIGenerator ig = factory.getInChIGenerator(ac);
      inchi = ig.getInchi();
    } catch (IOException | CDKException e) {
      e.printStackTrace();
    }
    return inchi;
  }
  
  @Override
  public String molToSmiles(String mol) {
    String smiles = null;
    try {
      IAtomContainer ac = CdkWrapper.readMol2d(mol);
      SmilesGenerator sg = new SmilesGenerator(SmiFlavor.UniversalSmiles);
      smiles = sg.create(ac);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return smiles;
  }

  @Override
  public Map<String, Integer> molToAtomMap(String mol) {
    Map<String, Integer> atomCount = null;
    try {
      IAtomContainer ac = CdkWrapper.readMol2d(mol);
      addImplicitHydrogens(ac);
      atomCount = getAtomCount(ac, cdk);
    } catch (IOException | CDKException e) {
      e.printStackTrace();
    }
    return atomCount;
  }
  
  @Override
  public String atomMapToFormula(Map<String, Integer> atomMap) {
    return cdk.convertToIsotopeMolecularFormula(getFormulaFromAtomMap(atomMap), false);
  }
  
  @Override
  public Map<String, Integer> resolveFormula(Set<String> formulas) {
    List<Map<String, Integer>> fmaps = new ArrayList<> ();
    for (String f : formulas) {
      Map<String, Integer> map = cdk.getAtomCountMap(f);
      fmaps.add(map);
    }
    
    if (fmaps.size() == 1) {
      return fmaps.iterator().next();
    }
    
    return null;
  }
  
  public static String getFormulaFromAtomMap(Map<String, Integer> atomMap) {
    return Joiner.on("").withKeyValueSeparator("").join(atomMap);
  }
  
  public static void addImplicitHydrogens(IAtomContainer iac) throws CDKException {
    
    CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(iac.getBuilder());
    CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(iac.getBuilder());
    for (IAtom atom : iac.atoms()) {
      IAtomType type = matcher.findMatchingAtomType(iac, atom);
      AtomTypeManipulator.configure(atom, type);
      adder.addImplicitHydrogens(iac, atom);
      AtomContainerManipulator.convertImplicitToExplicitHydrogens(iac);
    }
  }
  
  public static void addImplicitHydrogens(IAtomContainerSet iacs) throws CDKException {
    for (IAtomContainer iac : iacs.atomContainers()) {
      addImplicitHydrogens(iac);
    }
  }
  
  public static Map<String, Integer> getAtomCount(IAtomContainer iac, CdkWrapper cdk) throws CDKException {
    return cdk.getAtomCountMap(iac);
  }
  
  public static Map<String, Integer> getAtomCount(IAtomContainerSet iacs, CdkWrapper cdk) throws CDKException {
    Map<String, Integer> atomCount = new HashMap<> ();
    
    for (IAtomContainer iac : iacs.atomContainers()) {
      Map<String, Integer> p = cdk.getAtomCountMap(iac);
      for (String a : p.keySet()) {
        CollectionUtils.increaseCount(atomCount, a, p.get(a));
      }
    }
    
    return atomCount;
  }

  @Override
  public String smilesToCannonical(String smi) {
    String can = null;
   
    try {
      IAtomContainer ac = sp.parseSmiles(smi);
      SmilesGenerator sg = new SmilesGenerator(SmiFlavor.UniversalSmiles);
      can = sg.create(ac);
    } catch (Exception e) {
      logger.warn("{}: {}", smi, e.getMessage());
    }
    
    return can;
  }

  @Override
  public Map<String, Integer> smilesToAtomMap(String smi) {
    Map<String, Integer> atomCount = null;
    try {
      IAtomContainer ac = sp.parseSmiles(smi);
      atomCount = containerToAtomMap(ac);
    } catch (CDKException e) {
      e.printStackTrace();
    }
    return atomCount;
  }
  
  @Override
  public String smilesToInchi(String smi) {
    String inchi = null;
    try {
      IAtomContainer ac = sp.parseSmiles(smi);
      addImplicitHydrogens(ac);
      InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
      InChIGenerator ig = factory.getInChIGenerator(ac);
      inchi = ig.getInchi();
    } catch (CDKException e) {
      e.printStackTrace();
    }

    return inchi;
  }
  
  public Map<String, Integer> containerToAtomMap(IAtomContainer ac) {
    Map<String, Integer> atomCount = null;
    try {
      addImplicitHydrogens(ac);
      atomCount = getAtomCount(ac, cdk);
    } catch (CDKException e) {
      e.printStackTrace();
    }
    
    return atomCount;
  }

  @Override
  public String inchiToSmiles(String inchi) {
    String smiles = null;
    try {
      IAtomContainerSet containerSet = new CdkMoleculeFormatConverter().readInchi(
          new ByteArrayInputStream(inchi.getBytes()));
      if (containerSet.getAtomContainerCount() != 1) {
        
      } else {
        IAtomContainer ac = containerSet.atomContainers().iterator().next();
        SmilesGenerator sg = new SmilesGenerator(SmiFlavor.UniversalSmiles);
        smiles = sg.create(ac);
        if (smiles == null) {
          System.out.println("failed to generate universal smiles " + inchi);
          smiles = getSmiles(ac);
        }
        if (smiles == null) {
          System.out.println("failed to generate smiles " + inchi);
        }
      }
    } catch (CDKException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String inchiToInchiKey(String inchi) {
    String inchiKey = null;
    try {
      inchiKey = inchiConverter.generateInchiKey(new ByteArrayInputStream(inchi.getBytes()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return inchiKey;
  }

  @Override
  public Map<String, Integer> inchiToAtomMap(String inchi) {
    Map<String, Integer> atomCount = null;
    try {
      IAtomContainerSet containerSet = new CdkMoleculeFormatConverter().readInchi(
          new ByteArrayInputStream(inchi.getBytes()));
      addImplicitHydrogens(containerSet);
      atomCount = getAtomCount(containerSet, cdk);
    } catch (CDKException e) {
      e.printStackTrace();
    }
    return atomCount;
  }



}
