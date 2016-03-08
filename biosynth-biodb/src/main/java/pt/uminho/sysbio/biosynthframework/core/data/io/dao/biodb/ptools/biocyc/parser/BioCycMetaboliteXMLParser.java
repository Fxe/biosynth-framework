package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.IGenericMetaboliteParser;

public class BioCycMetaboliteXMLParser extends AbstractBioCycXMLParser 
implements IGenericMetaboliteParser {

  private final static Logger logger = LoggerFactory.getLogger(BioCycMetaboliteXMLParser.class);

  private final JSONObject base;
  private final BioCycEntityType entityType;

  public BioCycMetaboliteXMLParser(String xmlDocument) throws JSONException, IOException {
    super(xmlDocument);

    this.parseContent();

    JSONObject jsMetabolite = null;
    if (this.content.getJSONObject("ptools-xml").has("Compound")) {
      jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("Compound");
      this.entityType = BioCycEntityType.Compound;
    } else if ( this.content.getJSONObject("ptools-xml").has("Protein")) {
      jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("Protein");
      this.entityType = BioCycEntityType.Protein;
    } else if ( this.content.getJSONObject("ptools-xml").has("RNA")) {
      jsMetabolite = this.content.getJSONObject("ptools-xml").getJSONObject("RNA");
      this.entityType = BioCycEntityType.RNA;
    } else {
      this.entityType = BioCycEntityType.ERROR;
      throw new IOException("Invalid metabolite xml document " + this.content.getJSONObject("ptools-xml").toString());
    }

    this.base = jsMetabolite;

  }

  public boolean isValid() {
    if (this.base == null) return false;
    return this.base != null || !this.base.has("Error");
  }

  public List<String> getReactions() throws JSONException {
    List<String> rxnIdList = null;
    JSONArray rxnJSArray = null;


    rxnIdList = new ArrayList<String>();

    if (base.has("appears-in-right-side-of")) {
      Object rightSide = base.getJSONObject("appears-in-right-side-of").get("Reaction");
      if (rightSide instanceof JSONArray) {
        rxnJSArray = (JSONArray) rightSide;
      } else {
        rxnJSArray = new JSONArray();
        rxnJSArray.put(rightSide);
      }
      for (int i = 0; i < rxnJSArray.length(); i++) {
        rxnIdList.add(rxnJSArray.getJSONObject(i).getString("orgid") + ":" +  rxnJSArray.getJSONObject(i).getString("frameid"));
      }
    }

    if (base.has("appears-in-left-side-of")) {
      Object leftSide = base.getJSONObject("appears-in-left-side-of").get("Reaction");
      if (leftSide instanceof JSONArray) {
        rxnJSArray = (JSONArray) leftSide;
      } else {
        rxnJSArray = new JSONArray();
        rxnJSArray.put(leftSide);
      }
      for (int i = 0; i < rxnJSArray.length(); i++) {
        rxnIdList.add(rxnJSArray.getJSONObject(i).getString("orgid") + ":" +  rxnJSArray.getJSONObject(i).getString("frameid"));
        //System.out.println(rxnJSArray.getJSONObject(i).getString("frameid"));
      }
    }

    return rxnIdList;
  }

  public String getFrameId() {
    return this.base.getString("frameid");
  }

  public String getEntry() throws JSONException {
    return this.base.getString("ID");
  }

  public String getSource() throws JSONException {
    return this.base.getString("orgid");

  }

  public String getName() throws JSONException {
    String commonName;
    if (this.base.has("common-name")) {
      commonName = this.base.getJSONObject("common-name").getString("content");
    } else {
      commonName = this.getEntry();
    }

    return commonName;
  }

  public String getFormula() throws JSONException {
    String formula = null;

    switch (entityType) {
    case Compound:
      if (this.base.has("cml")) {
        formula = this.base.getJSONObject("cml")
            .getJSONObject("molecule").getJSONObject("formula").getString("concise");
        formula = formula.replaceAll(" ", "");
      } else {
        return null;
      }
      break;
    default:
      logger.warn(String.format("getFormula Not parseable type - %s", entityType));
      break;
    }
    if (formula == null) return null;

    return formula.isEmpty() ? null : formula;
  }

  public String getInchi() throws JSONException {
    String inchi = null;

    if (this.base.has("inchi")) {
      inchi = this.base.getJSONObject("inchi").getString("content");
      //			inchi = inchi.replace("InChI=", "");
    }

    return inchi;
  }

  public Integer getCharge() {
    try {
      Integer charge = null;

      switch (entityType) {
      case Compound:
        if (this.base.has("cml")) {
          charge = this.base.getJSONObject("cml").getJSONObject("molecule").getInt("formalCharge");
        }
        break;
      default:
        logger.warn(String.format("getCharge Not parseable type - %s", entityType));
        break;
      }

      return charge;
    } catch (JSONException ex) {
      logger.error("JSONException" + ex.getMessage());
      return null;
    }
  }

  public String getSmiles() {
    try {
      //			System.out.println(this.base.getJSONObject("cml").getJSONObject("molecule").get("string"));
      String smiles = "";

      switch (entityType) {
      case Compound:
        if (this.base.has("cml")) {
          Object jsStringObj = this.base.getJSONObject("cml")
              .getJSONObject("molecule").get("string");
          JSONArray jsArrayString;
          if (jsStringObj instanceof JSONArray) {
            jsArrayString = (JSONArray) jsStringObj;
          } else {
            jsArrayString = new JSONArray();
            jsArrayString.put(jsStringObj);
          }
          for (int i = 0; i < jsArrayString.length(); i++) {
            JSONObject jsArrObj = jsArrayString.getJSONObject(i);
            if (jsArrObj.getString("title").equals("smiles")) {
              smiles = jsArrObj.getString("content");
            }
          }


          smiles = smiles.replaceAll(" ", "");
        }
        break;
      default:
        break;
      }
      if (smiles.trim().isEmpty()) return null;

      return smiles;
    } catch (JSONException ex) {
      logger.error("JSONException" + ex.getMessage() + " => " + this.getEntry());
      return null;
    }
  }

  public String getEntityClass() {
    return this.entityType.toString();
  }

  public String getComment() {
    String comment = null;

    try {
      if (this.base.has("comment")) {
        comment = "";
        JSONArray jsArrayComment = this.getObjectAsArray(this.base, "comment");
        for (int i = 0; i < jsArrayComment.length(); i++) {
          JSONObject commentJs = jsArrayComment.getJSONObject(i);
          if (commentJs.has("content")) {
            comment.concat(commentJs.getString("content"));
          }
        }
      }
      return comment;
    } catch (JSONException e) {
      logger.error("JSONException " + e.getMessage() + " => " + this.getEntry());
      return null;
    }
  }

  public String getSpecies() {
    try {
      String ret = "";
      try {
        ret = this.base.getJSONObject("species").getJSONObject("Organism").getString("frameid");
      } catch (Exception e) {
        logger.error("EXCEPTION ! " + this.xmlDocument);
        throw e;
      }
      return ret;
    } catch (JSONException ex) {
      logger.error("JSONException");
      return null;
    }
  }


  public String getRemark() {
    try {
      String keggLink = "";
      if ( this.base.has("dblink") ) {
        JSONArray jsonArray = toJSONArray(this.base.get("dblink"));
        for (int i = 0; i < jsonArray.length(); i++) {
          if ( jsonArray.getJSONObject(i).getString("dblink-db").equals("LIGAND-CPD")) {
            keggLink = jsonArray.getJSONObject(i).get("dblink-oid").toString();
          }
        }
      }
      return keggLink;
    } catch (JSONException ex) {
      logger.error("JSONException");
      return null;
    }
  }

  private static JSONArray toJSONArray(Object obj) {
    if ( obj instanceof JSONArray) return (JSONArray) obj ;
    JSONArray jsArray = new JSONArray();
    jsArray.put(obj);

    return jsArray;
  }

  public Double getMolWeight() {
    try {
      Double molWeight = null;
      if (this.base.has("molecular-weight")) {
        molWeight = this.base.getJSONObject("molecular-weight").getDouble("content");
      }

      return molWeight;
    } catch (JSONException e) {
      logger.error("JSONException" + e.getMessage());
      return null;
    }
  }

  public Double getCmlMolWeight() {
    try {
      Double cmlMolWeight = null;

      switch (entityType) {
      case Compound: //Must have cml -> molecule -> float
        if (this.base.has("cml") && this.base.getJSONObject("cml").has("molecule")
            && this.base.getJSONObject("cml").getJSONObject("molecule").has("float") ) {
          cmlMolWeight = this.base.getJSONObject("cml")
              .getJSONObject("molecule").getJSONObject("float").getDouble("content");
        }
        break;
      default:
        logger.warn(String.format("getCmlMolWeight Not parseable type - %s", entityType));
        break;
      }

      return cmlMolWeight;
    } catch (JSONException ex) {
      logger.error("JSONException" + ex.getMessage());
      return null;
    }
  }

  public Double getGibbs() {
    try {
      Double gibbs = null;
      if (this.base.has("gibbs-0")) {
        gibbs = this.base.getJSONObject("gibbs-0").getDouble("content");
      }

      return gibbs;
    } catch (JSONException e) {
      logger.error("JSONException" + e.getMessage());
      return null;
    }
  }

  public List<String> getSynonym() {
    try {
      List<String> synonyms = new ArrayList<> ();
      if (this.base.has("synonym")) {
        JSONArray synJsArray = null;
        Object synObj = this.base.get("synonym");
        if (synObj instanceof JSONArray) {
          synJsArray = (JSONArray) synObj;
        } else {
          synJsArray = new JSONArray();
          synJsArray.put(synObj);
        }
        for (int i = 0; i < synJsArray.length(); i++) {
          synonyms.add( synJsArray.getJSONObject(i).getString("content"));
        }
      }

      return synonyms;
    } catch (JSONException e) {
      logger.error("JSONException" + e.getMessage());
      return null;
    }
  }

  public List<BioCycMetaboliteCrossreferenceEntity> getCrossReferences() {
    try {
      List<BioCycMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
      if (this.base.has("dblink")) {
        JSONArray dblinkJsArray = null;
        Object dblinkObj = this.base.get("dblink");
        if (dblinkObj instanceof JSONArray) {
          dblinkJsArray = (JSONArray) dblinkObj;
        } else {
          dblinkJsArray = new JSONArray();
          dblinkJsArray.put(dblinkObj);
        }
        for (int i = 0; i < dblinkJsArray.length(); i++) {
          JSONObject dblinkJsObj = dblinkJsArray.getJSONObject(i);
          BioCycMetaboliteCrossreferenceEntity crossReference = new BioCycMetaboliteCrossreferenceEntity();
          crossReference.setType(ReferenceType.DATABASE);
          if (dblinkJsObj.has("dblink-db"))
            crossReference.setRef(dblinkJsObj.getString("dblink-db"));
          if (dblinkJsObj.has("dblink-oid"))
            crossReference.setValue(dblinkJsObj.get("dblink-oid").toString());
          if (dblinkJsObj.has("unification"))
            crossReference.setRelationship(dblinkJsObj.getString("unification"));
          if (dblinkJsObj.has("dblink-URL"))
            crossReference.setUrl(dblinkJsObj.getString("dblink-URL"));
          crossReferences.add(crossReference);
          //					System.out.println(dblinkJsObj);
          //					crossReferences.add( synJsArray.getJSONObject(i).getString("content"));
        }
      }

      return crossReferences;
    } catch (JSONException e) {
      logger.error("JSONException" + e.getMessage() + " => " + this.getEntry());
      return null;
    }
  }

  public List<String> getParents() {
    List<String> res = new ArrayList<> ();
    if (this.base.has("parent")) {
      JSONArray parentJsArray = this.getObjectAsArray(this.base, "parent");
      for (int i = 0; i < parentJsArray.length(); i++) {
        Object arrayIndexObj = parentJsArray.get(i);
        if (arrayIndexObj instanceof JSONObject) {
          JSONObject parentJsObj = (JSONObject) arrayIndexObj;
          if (parentJsObj.has("Compound")) {
            res.add(parentJsObj.getJSONObject("Compound").getString("orgid") + ":" + parentJsObj.getJSONObject("Compound").getString("frameid"));
          } else if (parentJsObj.has("Protein")) {
            res.add(parentJsObj.getJSONObject("Protein").getString("orgid") + ":" + parentJsObj.getJSONObject("Protein").getString("frameid"));
          } else if (parentJsObj.has("RNA")) {
            res.add(parentJsObj.getJSONObject("RNA").getString("orgid") + ":" + parentJsObj.getJSONObject("RNA").getString("frameid"));
          } else {
            System.out.println("PARENT ERROR !");
            System.out.println(parentJsArray);
            System.exit(0);
          }
        } else {
          //					JSONObject js = new JSONObject();
          res.add(parentJsArray.getString(i));
        }
      }
    }
    return res;
  }

  public List<String> getInstanses() {
    List<String> res = new ArrayList<> ();
    if (this.base.has("instance")) {
      JSONArray instanceJsArray = null;
      Object instanceObj = this.base.get("instance");
      if (instanceObj instanceof JSONArray) {
        instanceJsArray = (JSONArray) instanceObj;
      } else {
        instanceJsArray = new JSONArray();
        instanceJsArray.put(instanceObj);
      }
      for (int i = 0; i < instanceJsArray.length(); i++) {
        JSONObject instanceJsObj = instanceJsArray.getJSONObject(i);
        if (instanceJsObj.has("Compound")) {
          res.add(instanceJsObj.getJSONObject("Compound").getString("orgid") + ":" + instanceJsObj.getJSONObject("Compound").getString("frameid"));
        } else if (instanceJsObj.has("Protein")) {
          res.add(instanceJsObj.getJSONObject("Protein").getString("orgid") + ":" + instanceJsObj.getJSONObject("Protein").getString("frameid"));
        } else if (instanceJsObj.has("RNA")) {
          res.add(instanceJsObj.getJSONObject("RNA").getString("orgid") + ":" + instanceJsObj.getJSONObject("RNA").getString("frameid"));
        } else {
          System.out.println("PARENT ERROR !");
          System.out.println(instanceJsObj);
          System.exit(0);
        }
      }
    }
    return res;
  }

  public List<String> getSubclasses() {
    List<String> res = new ArrayList<> ();
    if (this.base.has("subclass")) {
      JSONArray subclassJsArray = null;
      Object subclassObj = this.base.get("subclass");
      if (subclassObj instanceof JSONArray) {
        subclassJsArray = (JSONArray) subclassObj;
      } else {
        subclassJsArray = new JSONArray();
        subclassJsArray.put(subclassObj);
      }
      for (int i = 0; i < subclassJsArray.length(); i++) {
        JSONObject subclassJsObj = subclassJsArray.getJSONObject(i);
        if (subclassJsObj.has("Compound")) {
          res.add(subclassJsObj.getJSONObject("Compound").getString("orgid") + ":" + subclassJsObj.getJSONObject("Compound").getString("frameid"));
        } else if (subclassJsObj.has("Protein")) {
          res.add(subclassJsObj.getJSONObject("Protein").getString("orgid") + ":" + subclassJsObj.getJSONObject("Protein").getString("frameid"));
        } else if (subclassJsObj.has("RNA")) {
          res.add(subclassJsObj.getJSONObject("RNA").getString("orgid") + ":" + subclassJsObj.getJSONObject("RNA").getString("frameid"));
        } else {
          System.out.println("PARENT ERROR !");
          System.out.println(subclassJsObj);
          System.exit(0);
        }
      }
    }
    return res;
  }

  public List<String> getEnzymes() {
    // TODO Auto-generated method stub
    return null;
  }

  private JSONArray getObjectAsArray(JSONObject base, String key) {
    JSONArray jsArray = null;
    Object jsObject = base.get(key);
    if (jsObject instanceof JSONArray) {
      jsArray = (JSONArray) jsObject;
    } else {
      jsArray = new JSONArray();
      jsArray.put(jsObject);
    }
    return jsArray;
  }
}
