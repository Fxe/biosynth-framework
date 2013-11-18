package edu.uminho.biosynth.core.data.io.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.data.io.ILocalSource;
import edu.uminho.biosynth.core.data.io.IPageableSource;
import edu.uminho.biosynth.core.data.io.IRemoteSource;
import edu.uminho.biosynth.util.EquationParser;

@Deprecated
public class MySQLSource implements ILocalSource, IRemoteSource, IPageableSource {
	
	private final static Logger LOGGER = Logger.getLogger(MySQLSource.class.getName());
	
	public final String reactionTable				= "reaction";
	public final String reactionProductTable		= "reaction_product";
	public final String reactionReactantTable		= "reaction_reactant";
	public final String reactionEnzymeTable			= "reaction_enzyme";
	
	public final String compoundTable				= "compound";
	public final String compoundTableId				= "id_cp";
	
	public final String compoundReactionTable		= "compound_reaction";
	public final String compoundReactionTableIdCpd	= "id_cp";
	public final String compoundReactionTableIdRxn	= "id_rc";
	public final String compoundTagTable			= "compound_tag";
	
	public final String rpairTable					= "rpair";
	public final String rpairTableId				= "id_rp";
	
	public final String rpairReactionTable			= "rpair_reaction";
	public final String rpairReactionTableIdRpr		= "id_rp_rr";
	public final String rpairReactionTableIdRxn		= "id_rc_rr";
	
	public final String rpairRelatedTable			= "rpair_related";
	public final String rpairRelatedTableIdRpr		= "id_rp_main";
	public final String rpairRelatedTableIdRprRel	= "id_rp_rela";
	
	private final String getCompoundBasicInformationSQL = 
			"SELECT name_cp, formula_cp, class_cp, description_cp FROM " + compoundTable + 
			" WHERE " + compoundTableId + " = ?;";
	private final String getCompoundReactionsSQL = 
			"SELECT " + compoundReactionTableIdRxn + " FROM " + compoundReactionTable + 
			" WHERE " + compoundReactionTableIdCpd + " = ?;";
	
	//private final String getReactionPairInformationSQL = "";
	//private final String getReactionPairRelatedReactionsSQL = "SELECT id_rc_rr FROM rpair_reaction WHERE id_rp_rr = ?;";
	//private final String getReactionPairRelatedPairsSQL = "";
	
	public static boolean VERBOSE = false;
	
    private boolean connected_ = false;
    private Connection connection_;
    private String databaseName_;
    private String databaseUser_;
    private String databasePass_;
    private String databasePort_ = "3306";
    private String databaseAddr_ = "localhost";
    private boolean commitAfterSave;
    
    public MySQLSource(Connection connection) {
    	this.connection_ = connection;
    	this.commitAfterSave = true;
    }
    public MySQLSource(String db, String user, String pass) {
        this.databaseName_ = db;
        this.databaseUser_ = user;
        this.databasePass_ = pass;
        this.commitAfterSave = true;
	}
    
    public void commit() throws SQLException {
		this.connection_.commit();
    }
    
    private void close(PreparedStatement stmt) {
    	if ( stmt == null) return;
    	try {
			stmt.close();
		} catch (SQLException sqlEx) {
			System.err.println("SQLEX - " + sqlEx.getMessage());
		}
    }
	
	private GenericReaction getReactionData( String rcID) throws SQLException {
		String sql = "SELECT name_rc, equation_rc, description_rc, orientation_rc  FROM reaction WHERE id_rc = ?;";
		GenericReaction rxn = new GenericReaction();
		rxn.setEntry(rcID);
		PreparedStatement sqlStatement = null;
		
		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcID);
		ResultSet res = sqlStatement.executeQuery();
		boolean empty = true;
		while ( res.next()){
			empty = false;
			rxn.setName( res.getString( 1));
//			rxn.setEquation( res.getString( 2));
			rxn.setDescription( res.getString( 3));
			rxn.setOrientation( res.getInt( 4));
		}
		if (empty) return null;

		close(sqlStatement);
		
		return rxn;
	}
	
	private Map<String, Double> getReactionProducts( String rcID) throws SQLException {
		HashMap<String, Double> products = new HashMap<String, Double>();
		String sql = "SELECT id_cp_p, stoich_p FROM reaction_product WHERE id_rc_p = ?;";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcID);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			products.put( res.getString( 1), res.getDouble( 2));
		}

		close(sqlStatement);
		
		return products;
	}
	private Map<String, Double> getReactionReactants( String rcID) throws SQLException {
		HashMap<String, Double> reactants = new HashMap<String, Double>();
		String sql = "SELECT id_cp_r, stoich_r FROM reaction_reactant WHERE id_rc_r = ?;";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcID);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			reactants.put( res.getString( 1), res.getDouble( 2));
		}

		close(sqlStatement);
		
		return reactants;
	}
	private List<String> getReactionSameAs( String rcID) throws SQLException {
		ArrayList<String> sameas = new ArrayList<String>();
		String sql = "SELECT syn_rc FROM reaction_syn WHERE id_rc_s = ?;";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcID);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			sameas.add( res.getString( 1));
		}

		close(sqlStatement);
		
		return sameas;
	}
	private List<String> getReactionECNumbers( String rcID) throws SQLException {
		ArrayList<String> ecnumbers = new ArrayList<String>();
		String sql = "SELECT id_ec_r FROM reaction_enzyme WHERE id_rc_e = ?;";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcID);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			ecnumbers.add( res.getString( 1));
		}

		close(sqlStatement);
			
		return ecnumbers;
	}
	private Set<String> getReactionRPairs(String rcnID) throws SQLException {
		Set<String> rpairsIDSet = new HashSet<String> ();
		String sql = "SELECT id_rp_r FROM reaction_rpair WHERE id_rc_r = ?;";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setString( 1, rcnID);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			rpairsIDSet.add( res.getString( 1));
		}
		sqlStatement.close();

		close(sqlStatement);

		return rpairsIDSet;
	}

	@Override
	public GenericReaction getReactionInformation(String rxnId) {
		GenericReaction rxn;
		
		try {
		
			rxn = this.getReactionData( rxnId);
			if (rxn == null) return null;
//			rxn.addProducts( this.getReactionProducts( rxnId));
//			rxn.addReactants( this.getReactionReactants( rxnId));
//			rxn.addEnzymes( this.getReactionECNumbers( rxnId));
//			rxn.addRPairs( this.getReactionRPairs( rxnId));
//			rxn.addSameAs( this.getReactionSameAs( rxnId));
			
//			GenericReaction rev_reaction = new GenericReaction(rxn);
//			rev_reaction.swap();
//			rxn.setReverse(rev_reaction);
		} catch (SQLException sqlEx) {
			LOGGER.log(Level.SEVERE, "getReactionInformation " + rxnId);
			return null;
		}
		
		return rxn;
	}

	private Map<String, String> getCompoundBasicInformation( String cpID) {
		String sql = getCompoundBasicInformationSQL;
		Map<String, String> cpInfo = new HashMap<String, String> ();
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, cpID);
			ResultSet res = sqlStatement.executeQuery();
			boolean empty = true;
			while ( res.next()){
				empty = false;
				cpInfo.put("name", res.getString( 1));
				cpInfo.put("formula", res.getString( 2));
				cpInfo.put("metaboliteClass", res.getString( 3));
				cpInfo.put("desc", res.getString( 4));
			}
			if (empty) return null;
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getCompoundName - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return cpInfo;
	}
	private TreeSet<String> getCompoundReactions(String cpID) {
		TreeSet<String> reactions = null;
		String sql = getCompoundReactionsSQL;
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, cpID);
			ResultSet res = sqlStatement.executeQuery();
			reactions = new TreeSet<String>();
			while ( res.next()){
				reactions.add( res.getString( 1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getCompoundReactions - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return reactions;
	}
	
	@Override
	public GenericMetabolite getMetaboliteInformation(String cpID) {
		Map<String, String> cpInfo = getCompoundBasicInformation( cpID);
		if (cpInfo == null) return null;
//		TreeSet<String> reactions = getCompoundReactions( cpID);
		GenericMetabolite cpd = new GenericMetabolite(cpID);
		cpd.setName(cpInfo.get("name"));
		cpd.setFormula(cpInfo.get("formula"));
		cpd.setDescription(cpInfo.get("desc"));
		cpd.setMetaboliteClass(cpInfo.get("metaboliteClass"));
		
		//cpd.setGlycan( cpInfo.get("glycan").equals("0")?false:true);
//		cpd.addReactions(reactions);
		return cpd;
	}

	private GenericReactionPair getReactionPairData( String rprID) {
		String sql = "SELECT name_rp, compound1, compound2, type_rp FROM rpair WHERE id_rp = ?;";
		GenericReactionPair rpr = new GenericReactionPair(rprID);
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, rprID);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				rpr.setName( res.getString(1));
				GenericMetabolite cpd1 = new GenericMetabolite( res.getString(2));
				GenericMetabolite cpd2 = new GenericMetabolite( res.getString(3));
				rpr.setEntry1(cpd1);
				rpr.setEntry2(cpd2);
				rpr.setType( res.getString(4));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getReactionPairData - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return rpr;
	}
	private Set<String> getReactionPairRelatedPairs( String rprID) {
		String sql = "SELECT id_rp_rela FROM rpair_related WHERE id_rp_main = ?;";
		Set<String> relatedPairs = new HashSet<String> ();
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, rprID);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				relatedPairs.add( res.getString(1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getReactionPairRelatedPairs - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return relatedPairs;
	}
	private Set<String> getReactionPairRelatedReactions( String rprID) {
		String sql = "SELECT id_rc_rr FROM rpair_reaction WHERE id_rp_rr = ?;";
		Set<String> relatedReactions = new HashSet<String> ();
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, rprID);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				relatedReactions.add( res.getString(1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getReactionPairRelatedReactions - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return relatedReactions;
	}

	@Override
	public GenericReactionPair getPairInformation(String rprID) {
		GenericReactionPair rpr = this.getReactionPairData(rprID);
		Set<String> relatedPairs = this.getReactionPairRelatedPairs(rprID);
		Set<String> relatedReactions = this.getReactionPairRelatedReactions(rprID);
		rpr.addReactions(relatedReactions);
		rpr.addRelatedPairs(relatedPairs);
		return rpr;
	}
	
	private Map<String, String> getEnzymeOrganims(String ecID) {
		HashMap<String, String> organisms = new HashMap<String, String> ();
		String sql = "SELECT id_og_e, id_gn_e FROM enzymes_genes WHERE id_ec_g = ?;";
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, ecID);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				organisms.put( res.getString(1), res.getString(2));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getEnzymeOrganims - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return organisms;
	}
	private Map<String, String> getEnzymeData( String ecnID) {
		String sql = "SELECT name_ec FROM enzymes WHERE id_ec = ?;";
		Map<String, String> data = new HashMap<String, String> ();
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, ecnID);
			ResultSet res = sqlStatement.executeQuery();
			boolean empty = true;
			while ( res.next()){
				empty = false;
				data.put("name", res.getString( 1));
			}
			if (empty) return null;
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getReactionName - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return data;
	}

	@Override
	public GenericEnzyme getEnzymeInformation(String ecID) {
		Map<String, String> data = getEnzymeData( ecID);
		if (data == null) return null;
		Map<String, String> organisms = getEnzymeOrganims( ecID);

		GenericEnzyme ecn = new GenericEnzyme(ecID);
		ecn.setName( data.get("name"));
		ecn.addOrganimsMap( organisms);
		
		return ecn;
	}
	
	@Override
	public Set<String> getAllReactionIds() {
		Set<String> reaction_ids = new HashSet<String> ();
		String sql = "SELECT id_rc FROM reaction;";
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				reaction_ids.add( res.getString( 1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getAllReactionIDs - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return reaction_ids;
	}
	@Override
	public Set<String> getAllMetabolitesIds() {
		Set<String> compound_ids = new HashSet<String> ();
		String sql = "SELECT id_cp FROM compound;";
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				compound_ids.add( res.getString( 1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getAllCompoundIDs - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return compound_ids;
	}
	@Override
	public Set<String> getAllEnzymeIds() {
		Set<String> enzyme_ids = new HashSet<String> ();
		String sql = "SELECT id_ec FROM enzymes;";
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()){
				enzyme_ids.add( res.getString( 1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getAllCompoundIDs - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return enzyme_ids;
	}
	@Override
	public Set<String> getAllReactionPairIds() {
		Set<String> rprIDs = new HashSet<String> ();
		String sql = "SELECT id_rp FROM rpair;";
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = this.connection_.prepareStatement(sql);
			ResultSet res = sqlStatement.executeQuery();
			while ( res.next()) {
				rprIDs.add( res.getString( 1));
			}
		} catch (SQLException sqlEx) {
			System.err.println("MySQLGatherer::getAllReactionPairs - " + sqlEx.getMessage());
		} finally {
			close(sqlStatement);
		}
		return rprIDs;
	}

	@Override
	public void initialize() {
		if ( VERBOSE && isInitialized()) System.out.println( this + " Already Initialized");
		
		if ( !this.isInitialized()) {
	        try {
	        	if (VERBOSE) System.out.println("Loading driver...");
	            Class.forName("com.mysql.jdbc.Driver");
	            if (VERBOSE) System.out.println("Driver loaded!");
	            
	        	this.connection_ = DriverManager.getConnection("jdbc:mysql://" + this.databaseAddr_ + ":" + this.databasePort_ + "/" + databaseName_ + "?user=" + databaseUser_ + "&password=" + databasePass_);
	            this.connected_ = true;
	            this.connection_.setAutoCommit(false);
	        } catch (ClassNotFoundException e) {
	            throw new RuntimeException("Cannot find the driver in the classpath!", e);
	        } catch(Exception e) {
	        	this.connected_ = false;
	            System.out.println(e.toString());
	        }
		}
	}

	@Override
	public boolean hasCompoundInformation(String cpdId) {
		boolean retVal = false;
		String sql = 
				"SELECT COUNT(*) AS RET FROM compound WHERE compound.id_cp = ?;";
		try  {
			PreparedStatement sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, cpdId);
			ResultSet ret = sqlStatement.executeQuery();
			while ( ret.next()){
				retVal = ret.getInt( 1) == 0 ? false : true;
			}
		} catch (SQLException sqlEx) {
			System.err.println( "LocalDBInterface::hasCompoundInformation - " + sqlEx.getMessage());
		}
		return retVal;
	}
	@Override
	public boolean hasReactionInformation(String rxnId) {
		boolean retVal = false;
		String sql = 
				"SELECT COUNT(*) AS RET FROM reaction WHERE reaction.id_rc = ?;";
		try  {
			PreparedStatement sqlStatement = this.connection_.prepareStatement( sql);
			sqlStatement.setString( 1, rxnId);
			ResultSet ret = sqlStatement.executeQuery();
			while ( ret.next()){
				retVal = ret.getInt( 1) == 0 ? false : true;
			}
		} catch (SQLException sqlEx) {
			System.err.println( "LocalDBInterface::hasCompoundInformation - " + sqlEx.getMessage());
		}
		return retVal;
	}
	@Override
	public boolean hasEnzymeInformation(String ecnId) {
		boolean retVal = false;
		String sql = 
				"SELECT COUNT(*) AS RET FROM enzymes WHERE enzymes.id_ec = ?;";
		try  {
			PreparedStatement sqlStatement = this.connection_.prepareStatement( sql);
			sqlStatement.setString( 1, ecnId);
			ResultSet ret = sqlStatement.executeQuery();
			while ( ret.next()){
				retVal = ret.getInt( 1) == 0 ? false : true;
			}
		} catch (SQLException sqlEx) {
			System.err.println( "LocalDBInterface::hasEnzymeInformation - " + sqlEx.getMessage());
		}
		return retVal;
	}
	@Override
	public boolean hasPairInformation(String rprId) {
		boolean retVal = false;
		String sql = 
				"SELECT COUNT(*) AS RET FROM rpair WHERE rpair.id_rp = ?;";
		try  {
			PreparedStatement sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, rprId);
			ResultSet ret = sqlStatement.executeQuery();
			while ( ret.next()){
				retVal = ret.getInt( 1) == 0 ? false : true;
			}
		} catch (SQLException sqlEx) {
			System.err.println( "LocalDBInterface::hasCompoundInformation - " + sqlEx.getMessage());
		}
		return retVal;
	}
	
	@Override
	public boolean isInitialized() {
		return this.connected_;
	}
	
	private int saveCompoundData(String cpID, String name, String formula, 
			String desc, String source, String metaboliteClass) throws SQLException {
		int retVal = -1;
		String sqlInsert = "INSERT INTO compound ( id_cp, name_cp, formula_cp, class_cp, description_cp, src_cp) VALUES ( ?, ?, ?, ?, ?, ?);";
		PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
		sqlInsertSt.setString( 1, cpID);
		sqlInsertSt.setString( 2, name);
		sqlInsertSt.setString( 3, formula);
		sqlInsertSt.setString( 4, metaboliteClass);
		sqlInsertSt.setString( 5, desc);
		sqlInsertSt.setString( 6, source);
		retVal = sqlInsertSt.executeUpdate();
		
		return retVal;
	}
	private int saveCompoundReactions(String cpID, Set<String> reactionSet) throws SQLException {
		int retVal = -1;
		String sqlInsert = "INSERT INTO compound_reaction ( id_cp, id_rc) VALUES ( ?, ?);";

		PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);

		for (String rxnID : reactionSet) {
			sqlInsertSt.setString( 1, cpID);
			sqlInsertSt.setString( 2, rxnID);
			sqlInsertSt.addBatch();
		}
		int[] updateCounts = sqlInsertSt.executeBatch();
		retVal = updateCounts.length;

		return retVal;
	}

	@Override
	public void saveCompoundInformation(GenericMetabolite cpd) {
		try {
			this.saveCompoundData(cpd.getEntry(), cpd.getName(), cpd.getFormula(), cpd.getDescription(), cpd.getSource(), cpd.getMetaboliteClass());
//			this.saveCompoundReactions( cpd.getEntry(), cpd.getReactionIdSet());
			if (this.commitAfterSave) this.commit();
		} catch (SQLException sqlEx) {
			System.err.println("MySQLSource::saveCompoundInformation - " + cpd.getEntry() + " - " + sqlEx.getMessage());
		}
	}
	
	private int saveReactionData(String rxnId, String name, String equation, String description, String source,
			boolean generic, int orientation) throws SQLException {
		
		int retVal = -1;
		String sqlInsert = "INSERT INTO reaction ( id_rc, name_rc, equation_rc, description_rc, generic_rc, orientation_rc, src_rc) VALUES ( ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
		sqlInsertSt.setString( 1, rxnId);
		sqlInsertSt.setString( 2, name == null ? "" : name);
		sqlInsertSt.setString( 3, equation);
		sqlInsertSt.setString( 4, description);
		sqlInsertSt.setInt( 5, generic ? 1 : 0);
		sqlInsertSt.setInt( 6, orientation);
		sqlInsertSt.setString( 7, source);
		
		retVal = sqlInsertSt.executeUpdate();

		return retVal;
	}
	private int saveReactionEnzymes(String rxnId, Set<String> ecnumbers) throws SQLException {
		int retVal = -1;
		if ( ecnumbers != null) {
			String sqlInsert = "INSERT INTO reaction_enzyme (id_rc_e, id_ec_r) VALUES ( ?, ?);";
			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for (String ecID : ecnumbers) {
				sqlInsertSt.setString( 1, rxnId);
				sqlInsertSt.setString( 2, ecID);
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;
		}
		return retVal;
	}
	private int saveReactionReactants(String rxnId, Map<String, Double> reactants, Map<String, String> genStoich) throws SQLException {
		int retVal = -1;
		if (reactants != null) {
			String sqlInsert = "INSERT INTO reaction_reactant ( id_rc_r, id_cp_r, stoich_r_s, stoich_r) VALUES ( ?, ?, ?, ?);";
			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String cpd : reactants.keySet()) {
				sqlInsertSt.setString( 1, rxnId);
				sqlInsertSt.setString( 2, cpd);
				sqlInsertSt.setString( 3, genStoich.get(cpd));
				sqlInsertSt.setDouble( 4, reactants.get(cpd));
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;
		}
		return retVal;
	}
	private int saveReactionProducts(String rxnId, Map<String, Double> products, Map<String, String> genStoich) throws SQLException {
		int retVal = -1;
		if (products != null) {
			String sqlInsert = "INSERT INTO reaction_product ( id_rc_p, id_cp_p, stoich_p_s, stoich_p) VALUES ( ?, ?, ?, ?);";
			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String cpd : products.keySet()) {
				sqlInsertSt.setString( 1, rxnId);
				sqlInsertSt.setString( 2, cpd);
				sqlInsertSt.setString( 3, genStoich.get(cpd));
				sqlInsertSt.setDouble( 4, products.get(cpd));
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;
		}
		return retVal;
	}
	private int saveReactionSameas(String rxnId, Set<String> sameas) throws SQLException {
		int retVal = -1;
		if ( sameas != null) {
			String sqlInsert = "INSERT INTO reaction_syn ( id_rc_s, syn_rc) VALUES ( ?, ?);";
			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String rxnID : sameas) {
				sqlInsertSt.setString( 1, rxnId);
				sqlInsertSt.setString( 2, rxnID);
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;
		}
		return retVal;
	}
	private int saveReactionRPairs(String rxnID, Set<String> rpairSet) throws SQLException {
		int retVal = -1;
		if ( rpairSet != null) {
			String sqlInsert = "INSERT INTO reaction_rpair ( id_rc_r, id_rp_r) VALUES ( ?, ?);";
			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String rprID : rpairSet) {
				sqlInsertSt.setString( 1, rxnID);
				sqlInsertSt.setString( 2, rprID);
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;
		}
		return retVal;
	}
	
	@Override
	public void saveReactionInformation(GenericReaction rxn) { 
//		EquationParser eqp = new EquationParser(rxn.getEquation());
//		try {
//			this.saveReactionData(rxn.getEntry(), rxn.getName(), rxn.getEquation(), 
//								  rxn.getDescription(), rxn.getSource(), eqp.isVariable(), rxn.getOrientation());
//			this.saveReactionEnzymes(rxn.getEntry(), rxn.getEnzymes());
//			this.saveReactionReactants( rxn.getEntry(), rxn.getLeft(), rxn.getLeftGeneric());
//			this.saveReactionProducts( rxn.getEntry(), rxn.getRight(), rxn.getRightGeneric());
//			this.saveReactionSameas( rxn.getEntry(), rxn.getSameAs());
//			this.saveReactionRPairs( rxn.getEntry(), rxn.getRPairs());
//			if (this.commitAfterSave) this.commit();
//		} catch (SQLException sqlEx) {
//			LOGGER.log(Level.SEVERE, "saveReactionInformation " + rxn.getEntry() + " - " + sqlEx.getMessage());
//		}
	}
	
	private int saveEnzymeData(String ecID, String name, String source) throws SQLException {
		int retVal = -1;
		String sqlInsert = "INSERT INTO enzymes ( id_ec, name_ec, src_ec) VALUES ( ?, ?, ?);";

		PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
		sqlInsertSt.setString( 1, ecID);
		sqlInsertSt.setString( 2, name);
		sqlInsertSt.setString( 3, source);
		retVal = sqlInsertSt.executeUpdate();

		return retVal;
	}
	private int saveEnzymeOrganisms(String ecID, Map<String, String> organims) throws SQLException {
		int retVal = -1;
		if (organims != null) {
			String sqlInsert = "INSERT INTO enzymes_genes (id_ec_g, id_og_e, id_gn_e) VALUES ( ?, ?, ?);";

			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String org : organims.keySet()) {
				sqlInsertSt.setString( 1, ecID);
				sqlInsertSt.setString( 2, org);
				sqlInsertSt.setString( 3, organims.get( org));
				sqlInsertSt.addBatch();
			}
			
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;

		}
		return retVal;
	}

	@Override
	public void saveEnzymeInformation(GenericEnzyme ecn) {
		try {
			saveEnzymeData( ecn.getEntry(), ecn.getName(), ecn.getSource());
			saveEnzymeOrganisms( ecn.getEntry(), ecn.getOrganismMap());
			if (this.commitAfterSave) this.commit();
		} catch (SQLException sqlEx) {
			LOGGER.log(Level.SEVERE, "saveEnzymeInformation " + ecn.getEntry() + " - " + sqlEx.getMessage());
		}

	}
	
	
	private int saveReactionPairData(String rprID, String name, String source, String type, String cpd1, String cpd2) throws SQLException {
		int retVal = -1;
		String sqlInsert = "INSERT INTO rpair ( id_rp, name_rp, compound1, compound2, type_rp, src_rp) VALUES ( ?, ?, ?, ?, ?, ?);";

		PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
		sqlInsertSt.setString( 1, rprID);
		sqlInsertSt.setString( 2, name);
		sqlInsertSt.setString( 3, cpd1);
		sqlInsertSt.setString( 4, cpd2);
		sqlInsertSt.setString( 5, type);
		sqlInsertSt.setString( 6, source);
		retVal = sqlInsertSt.executeUpdate();

		return retVal;
	}
	private int saveReactionPairReactions(String rprID, Set<String> reactions) throws SQLException {
		int retVal = -1;
		if (reactions != null) {
			String sqlInsert = "INSERT INTO rpair_reaction ( id_rp_rr, id_rc_rr) VALUES ( ?, ?);";

			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String rxn : reactions) {
				sqlInsertSt.setString( 1, rprID);
				sqlInsertSt.setString( 2, rxn);
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;

		}
		return retVal;
	}
	private int saveReactionPairRelatedPairs(String rprID, Set<String> related) throws SQLException {
		int retVal = -1;
		if (related != null) {
			String sqlInsert = "INSERT INTO rpair_related ( id_rp_main, id_rp_rela) VALUES ( ?, ?);";

			PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
			for ( String rel_rprID : related) {
				sqlInsertSt.setString( 1, rprID);
				sqlInsertSt.setString( 2, rel_rprID);
				sqlInsertSt.addBatch();
			}
			int[] updateCounts = sqlInsertSt.executeBatch();
			retVal = updateCounts.length;

		}
		return retVal;
	}

	@Override
	public void savePairInformation(GenericReactionPair rpr) {
		
		try {
		
			this.saveReactionPairData(rpr.getEntry(), rpr.getName(), rpr.getSource(), rpr.getType(), 
					rpr.getEntry1().getEntry(), rpr.getEntry2().getEntry());
			this.saveReactionPairReactions( rpr.getEntry(), rpr.getReactions());
			this.saveReactionPairRelatedPairs( rpr.getEntry(), rpr.getRelatedPairs());
			if (this.commitAfterSave) this.commit();
		} catch (SQLException sqlEx) {
			LOGGER.log(Level.SEVERE, "saveEnzymeInformation " + rpr.getEntry() + " - " + sqlEx.getMessage());
		}
	}

	@Override
	public boolean removeReactionInformation(String rxnID) {
		int retVal = 0;
		String sql =
				"DELETE FROM reaction WHERE reaction.id_rc = ?";
		try  {
			PreparedStatement sqlStatement = this.connection_.prepareStatement(sql);
			sqlStatement.setString( 1, rxnID);
			retVal = sqlStatement.executeUpdate();
		} catch (SQLException sqlEx) {
			System.err.println( "LocalDBInterface::removeReactionInformation - " + sqlEx.getMessage());
		}
		return retVal != 0;
	}

	@Override
	public void addMetatagToReaction(String rxnId, String metatag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMetatagsToReaction(String rxnId, Set<String> metatags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMetatagsToReactions(Map<String, Set<String>> metatags) {
		if (metatags != null) {
			String sqlInsert = "INSERT INTO reaction_tag ( id_rc_t, tag_rc) VALUES ( ?, ?);";
			try {
				this.connection_.setAutoCommit(false);
				PreparedStatement sqlInsertSt = connection_.prepareStatement(sqlInsert);
				for ( String rxnId : metatags.keySet()) {
					for ( String tag : metatags.get(rxnId)) {
						sqlInsertSt.setString( 1, rxnId);
						sqlInsertSt.setString( 2, tag);
						sqlInsertSt.addBatch();
					}
				}
				int[] updateCounts = sqlInsertSt.executeBatch();
				int retVal = updateCounts.length;
				System.out.println("Update count:" + retVal);
			} catch (SQLException sqlEx) {
				System.err.println( "MySQLGatherer::addMetatagsToReactions - " + sqlEx.getMessage());
			} finally {
				try {
					this.connection_.setAutoCommit(true);
				} catch ( SQLException sqlEx) {
					System.err.println( "MySQLGatherer::addMetatagsToReactions - finally - " + sqlEx.getMessage());
				}
			}
		} 
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MySQL\t").append( this.connection_).append('\n');
		if ( this.isInitialized()) {
			sb.append("#cpd\t").append( this.getAllMetabolitesIds().size()).append('\n');
			sb.append("#rxn\t").append( this.getAllReactionIds().size()).append('\n');
			sb.append("#ecn\t").append( this.getAllEnzymeIds().size()).append('\n');
			sb.append("#rpr\t").append( this.getAllReactionPairIds().size());
		} else {
			sb.append("Uninitialized");
		}
		return sb.toString();
	}
	
	private Map<String, Map<String, Double>> getByBatchReactionReactants(Collection<String> rxnIdCollection) throws SQLException {
		Map<String, Map<String, Double>> ret = new HashMap<> ();
		StringBuilder parametersPattern = new StringBuilder();
		for (int i = 0; i < rxnIdCollection.size() - 1; i++) {
			parametersPattern.append("?, ");
		}
		parametersPattern.append("?");
		String sql = "SELECT id_rc_r, id_cp_r, stoich_r FROM reaction_reactant WHERE id_rc_r IN (" + parametersPattern.toString() + ");";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		int i = 1;
		for (String rxnId : rxnIdCollection) {
			Map<String, Double> reactants = new HashMap<String, Double>();
			ret.put(rxnId, reactants);
			sqlStatement.setString( i++, rxnId);
		}

		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			ret.get(res.getString( 1)).put( res.getString( 2), res.getDouble( 3));
		}

		close(sqlStatement);
		
		return ret;
	}
	private Map<String, Map<String, Double>> getByBatchReactionProducts(Collection<String> rxnIdCollection) throws SQLException {
		Map<String, Map<String, Double>> ret = new HashMap<> ();
		StringBuilder parametersPattern = new StringBuilder();
		for (int i = 0; i < rxnIdCollection.size() - 1; i++) {
			parametersPattern.append("?, ");
		}
		parametersPattern.append("?");
		
		String sql = "SELECT id_rc_p, id_cp_p, stoich_p FROM reaction_product WHERE id_rc_p IN (" + parametersPattern.toString() + ");";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		int i = 1;
		for (String rxnId : rxnIdCollection) {
			Map<String, Double> products = new HashMap<String, Double>();
			ret.put(rxnId, products);
			sqlStatement.setString( i++, rxnId);
		}

		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			ret.get(res.getString( 1)).put( res.getString( 2), res.getDouble( 3));
		}

		close(sqlStatement);
		
		return ret;
	}
	private Map<String, GenericReaction> getByPageReactionData(int page, int pagesize) throws SQLException {
		String sql = "SELECT id_rc, orientation_rc, src_rc, name_rc, equation_rc, description_rc  FROM reaction LIMIT ? OFFSET ?;";
		Map<String, GenericReaction> pageElement = new HashMap<> ();
		PreparedStatement sqlStatement = null;
		
		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setInt(1, pagesize);
		sqlStatement.setInt(2, pagesize * page);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			String id = res.getString( 1);
			int orientation = res.getInt( 2);
			String source = res.getString( 3);
			String name = res.getString( 4);
			String equation = res.getString( 5);
			String description = res.getString( 6);
			GenericReaction rxn = new GenericReaction();
			rxn.setEntry(id);
			rxn.setDescription(description);
//			rxn.setEquation(equation);
			rxn.setOrientation(orientation);
			rxn.setName(name);
			rxn.setSource(source);
			
			pageElement.put(id, rxn);
		}
	
		close(sqlStatement);
		
		return pageElement;
	}
	
	@Override
	public List<GenericReaction> getByPageReactionInformation(int page, int pagesize) {
		Map<String, GenericReaction> pageElements = null; 
		
		try {
			pageElements = this.getByPageReactionData(page, pagesize);
			Map<String, Map<String, Double>> pageProducts = this.getByBatchReactionProducts(pageElements.keySet());
			Map<String, Map<String, Double>> pageReactants = this.getByBatchReactionReactants(pageElements.keySet());
			
			for (String rxnId : pageElements.keySet()) {
				GenericReaction rxn = pageElements.get(rxnId);
//				rxn.addProducts(pageProducts.get(rxnId));
//				rxn.addReactants(pageReactants.get(rxnId));
			}
		} catch (SQLException sqlEx) {
			LOGGER.log(Level.SEVERE, "SQLERROR ! - " + sqlEx.getMessage());
		}
		
		List<GenericReaction> ret = new ArrayList<> (pageElements.values());
		return ret;
	}

	private Map<String, GenericMetabolite> getByPageMetaboliteData(int page, int pagesize) throws SQLException {
		String sql = "SELECT id_cp, name_cp, formula_cp, class_cp, description_cp  FROM compound LIMIT ? OFFSET ?;";
		Map<String, GenericMetabolite> pageElement = new HashMap<> ();
		PreparedStatement sqlStatement = null;
		
		sqlStatement = this.connection_.prepareStatement(sql);
		sqlStatement.setInt(1, pagesize);
		sqlStatement.setInt(2, pagesize * page);
		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			String id = res.getString( 1);
			String name = res.getString( 2);
			String formula = res.getString( 3);
			String metaboliteClass = res.getString( 4);
			String description = res.getString( 5);
			GenericMetabolite cpd = new GenericMetabolite( id);
			cpd.setDescription(description);
			cpd.setFormula(formula);
			cpd.setMetaboliteClass(metaboliteClass);
			cpd.setName(name);
			
			pageElement.put(id, cpd);
		}
	
		close(sqlStatement);
		
		return pageElement;
	}
	private Map<String, Set<String>> getByBatchMetaboliteReactions(Collection<String> cpdIdCollection) throws SQLException {
		Map<String, Set<String>> cpdReactionMap = new HashMap<> ();
		
		StringBuilder parametersPattern = new StringBuilder();
		for (int i = 0; i < cpdIdCollection.size() - 1; i++) {
			parametersPattern.append("?, ");
		}
		parametersPattern.append("?");
		
		String sql = "SELECT id_cp, id_rc FROM compound_reaction WHERE id_cp IN (" + parametersPattern.toString() + ");";
		PreparedStatement sqlStatement = null;

		sqlStatement = this.connection_.prepareStatement(sql);
		int i = 1;
		for (String cpdId : cpdIdCollection) {
			Set<String> cpdReactionSet = new HashSet<>();
			cpdReactionMap.put(cpdId, cpdReactionSet);
			sqlStatement.setString( i++, cpdId);
		}

		ResultSet res = sqlStatement.executeQuery();
		while ( res.next()){
			cpdReactionMap.get(res.getString( 1)).add( res.getString( 2));
		}

		close(sqlStatement);
		
		return cpdReactionMap;
	}
	
	@Override
	public List<GenericMetabolite> getByPageMetaboliteInformation(int page, int pagesize) {
		Map<String, GenericMetabolite> pageElements = null; 
		
		try {
			pageElements = this.getByPageMetaboliteData(page, pagesize);
			Map<String, Set<String>> cpdReactionMap = this.getByBatchMetaboliteReactions(pageElements.keySet());
			
			for (String cpdId : pageElements.keySet()) {
//				pageElements.get(cpdId).addReactions(cpdReactionMap.get(cpdId));
			}
			// TODO Auto-generated method stub
		} catch (SQLException sqlEx) {
			LOGGER.log(Level.SEVERE, "SQLERROR ! - " + sqlEx.getMessage());
		}
		List<GenericMetabolite> ret = new ArrayList<> (pageElements.values());
		return ret;
	}

	@Override
	public List<GenericEnzyme> getByPageEnzymeInformation(int page, int pagesize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericReactionPair> getByPagePairInformation(int page, int pagesize) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void resetDatabase() {
		//TODO: working on it ! with load prio ...
		//this.connection_.
//		PreparedStatement sqlStatement = null;
//		String sql = BioSynthUtilsIO.readFromFile("./");
//		try {
//			sqlStatement = this.connection_.prepareStatement(sql);
//			sqlStatement.execute();
//		} catch (SQLException ex) {
//			
//		}
	}
	

}
