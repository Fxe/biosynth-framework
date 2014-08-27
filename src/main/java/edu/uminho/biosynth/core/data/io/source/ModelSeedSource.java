package edu.uminho.biosynth.core.data.io.source;


@Deprecated
public class ModelSeedSource {
//	
//	private final static Logger LOGGER = Logger.getLogger(ModelSeedSource.class.getName());
//	
//	private String rxnFile;
//	private String cpdFile;
//	
//	private Map<String, SeedMetabolite> cpdMap = new HashMap<> ();
//	
//	public ModelSeedSource(String rxnFile, String cpdFile) {
//		this.rxnFile = rxnFile;
//		this.cpdFile = cpdFile;
//	}
//
//	@Override
//	public GenericReaction getReactionInformation(String rxnId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public GenericMetabolite getMetaboliteInformation(String cpdId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	public SeedMetabolite getMetaboliteInformation2(String cpdId) {
//		return this.cpdMap.get(cpdId);
//	}
//	@Override
//	public GenericEnzyme getEnzymeInformation(String ecnId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public GenericReactionPair getPairInformation(String rprId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Set<String> getAllReactionIds() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Set<String> getAllMetabolitesIds() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Set<String> getAllEnzymeIds() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Set<String> getAllReactionPairIds() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void initialize() {
//		try {
//			String readLine;
//			String header[];
//			
//			BufferedReader cpdReader = new BufferedReader(new FileReader(new File(cpdFile)));
//			
//			header = cpdReader.readLine().split("\t");
//			while ((readLine = cpdReader.readLine()) != null) {
//				String[] data = readLine.split("\t");
//				Double mass;
//				try {
//					mass = Double.valueOf(data[3]);
//				} catch (NumberFormatException nfEx) {
//					LOGGER.log(Level.WARNING, data[0] + " " + data[3] + "::" + nfEx);
//					mass = Double.NaN;
//				}
//				SeedMetabolite cpd = new SeedMetabolite(data[0], data[1], data[2], mass);
//				cpd.setKeggMaps(data[4]);
//				cpd.setKeggRef(data[5]);
//				this.cpdMap.put(cpd.getEntry(), cpd);
//				
//				LOGGER.log(Level.INFO, "Successfully added: " + cpd + " " + cpd.getKeggRef());
//			}
//			
//			cpdReader.close();
//			
//			BufferedReader rxnReader = new BufferedReader(new FileReader(new File(rxnFile)));
//
//			header = rxnReader.readLine().split("\t");
//			while ((readLine = rxnReader.readLine()) != null) {
//				
//			}
//			
//			rxnReader.close();
//		} catch (IOException ioex) {
//			System.err.println(ioex.getMessage());
//		}
//	}
}
