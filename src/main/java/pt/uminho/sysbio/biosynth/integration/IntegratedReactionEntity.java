package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.Map;

public class IntegratedReactionEntity {
	
	private Long id;
	private String entry;
	private String description;
	private String source;
	
	private Map<Long, CompositeProxyId> sourcesMap = new HashMap<> ();
	
	private Map<Long, String> equation = new HashMap<> ();
	
	private Map<Long, ?> left = new HashMap<> ();
	private Map<Long, ?> right = new HashMap<> ();
	
	
}
