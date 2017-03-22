package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggKOEntity extends KeggEntity{
    
    protected Set<String> genes;
    protected Set<String> modules;
    protected Set<String> pathways;
    protected Set<String> ecNumbers;
    public Set<Pair<String, String>> g = new HashSet<> ();
    
    public void addGene(String gene){
        if(genes==null)
            genes = new HashSet<>();
        genes.add(gene);
    }
    
    public void addGenes(Collection<String> gs){
        if(genes==null)
            genes = new HashSet<>();
        genes.addAll(gs);
    }
    
    public void addModule(String module){
        if(modules==null)
            modules = new HashSet<>();
        modules.add(module);
    }

    public void addPathway(String pathway){
        if(pathways==null)
            pathways = new HashSet<>();
        pathways.add(pathway);
    }
    
    public void addEcNumbers(Collection<String> ecs){
        if(ecNumbers==null)
            ecNumbers = new HashSet<>();
        ecNumbers.addAll(ecs);
    }
    
    public String getEntryFromValue(String value){
        Pattern p = Pattern.compile(KeggTokens.ORTHOLOG_REGEXP);
        Matcher m = p.matcher(value);
        return m.find() ? m.group(1) : null;
    }
    
    
    public Set<String> getGenes() {
        return genes;
    }
    public void setGenes(Set<String> genes) {
        this.genes = genes;
    }
    public Set<String> getModules() {
        return modules;
    }
    public void setModules(Set<String> modules) {
        this.modules = modules;
    }
    public Set<String> getPathways() {
        return pathways;
    }
    public void setPathways(Set<String> pathways) {
        this.pathways = pathways;
    }
    public Set<String> getEcNumbers() {
        return ecNumbers;
    }
    public void setEcNumbers(Set<String> ecNumbers) {
        this.ecNumbers = ecNumbers;
    }
    

    @Override
    public void addProperty(String key, String value) {
        Object addedValue = null;
        
        if(key.equals(KeggTokens.ENTRY))
        {
            addedValue = getEntryFromValue(value);
            if(addedValue!=null)
                entry = (String) addedValue;
        }
        else if(key.equals(KeggTokens.DEFINITION))
        {
            addedValue = getEcNumbersFromDefinition(value);
            if(addedValue!=null)
                addEcNumbers((Set<String>) addedValue);
        }
        else if(key.equals(KeggTokens.GENES))
        {
            addedValue = getGenesFromValue(value);
            if(addedValue!=null)
                addGenes((Set<String>) addedValue);
        }
        else if(key.equals(KeggTokens.MODULE))
        {
            addedValue = getModuleFromValue(value);
            if(addedValue!=null)
                addModule((String) addedValue);
        }
        else if(key.equals(KeggTokens.PATHWAY))
        {
            addedValue = getPathwayFromValue(value);
            if(addedValue!=null)
                addPathway((String) addedValue);
        }
        if(addedValue==null)
            super.addProperty(key, value);
    }
}