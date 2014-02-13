package edu.uminho.biosynth.core.data.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.components.IntegratedMetabolite;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

public class DefaultMetaboliteIntegrationStrategy implements IIntegrationStrategy<GenericMetabolite, IntegratedMetabolite> {

	protected IKeyGenerator<String> entryGenerator;
	
	public IKeyGenerator<String> getEntryGenerator() { return entryGenerator;}
	public void setEntryGenerator(IKeyGenerator<String> entryGenerator) { this.entryGenerator = entryGenerator;}

	private String collectionToString(Collection<?> colletion) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : colletion) {
			//skip null or empty strings
			if (obj != null && obj.toString().length() > 0) sb.append(obj).append(';');
		}
		String result = sb.toString(); 
		return result.length() < 1 ? null : result;
	}
	
	@Override
	public Collection<IntegratedMetabolite> integrate(Collection<GenericMetabolite> entities) {
		List<IntegratedMetabolite> integrationResult = new ArrayList<> ();
		
		Set<String> name = new HashSet<> ();
		Set<String> description = new HashSet<> ();
		Set<String> formula = new HashSet<> ();
		
		for (GenericMetabolite cpd : entities) {
			name.add(cpd.getName());
			formula.add(cpd.getFormula());
			description.add(cpd.getDescription());
		}
		
		IntegratedMetabolite cpdIntegrated = new IntegratedMetabolite();
		cpdIntegrated.setEntry(entryGenerator.generateKey());
		cpdIntegrated.setName( this.collectionToString(name));
		cpdIntegrated.setDescription( this.collectionToString(description));
		cpdIntegrated.setFormula( this.collectionToString(formula));
		
		integrationResult.add(cpdIntegrated);
		
		return integrationResult;
	}
}
