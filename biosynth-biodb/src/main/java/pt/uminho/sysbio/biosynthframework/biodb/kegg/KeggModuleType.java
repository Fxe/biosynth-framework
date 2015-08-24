package pt.uminho.sysbio.biosynthframework.biodb.kegg;

public enum KeggModuleType {

	Pathway("pathway"),
	Complex("complex"),
	FuncSet("funcset"),
	Signature("signature");
	
	
	
	protected String name;
	
	private KeggModuleType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	static public KeggModuleType getType(String name){
		System.out.println(">>>>>>>> " + name);
		String n = name.toLowerCase();
		for(KeggModuleType t : values())
			if(t.getName().equals(n))
				return t;
		return null;
	}
	
}
