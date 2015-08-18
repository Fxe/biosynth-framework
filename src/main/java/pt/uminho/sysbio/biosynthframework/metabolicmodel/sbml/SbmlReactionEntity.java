package pt.uminho.sysbio.biosynthframework.metabolicmodel.sbml;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericReaction;

@Entity
@Table(name="SBML_REACTION")
public class SbmlReactionEntity extends GenericReaction {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_MODEL", nullable=false)
	private SbmlMetabolicModel sbmlMetabolicModel;
	public SbmlMetabolicModel getSbmlMetabolicModel() { return sbmlMetabolicModel;}
	public void setSbmlMetabolicModel(SbmlMetabolicModel sbmlMetabolicModel) { this.sbmlMetabolicModel = sbmlMetabolicModel;}
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pk.sbmlSpecie", cascade={CascadeType.ALL})
	private Set<SbmlReactionProduct> reactionProducts = new HashSet<> ();
//	@MapKey(name="pk.sbmlSpecie.entry")
//	private Map<String, SbmlReactionProduct> reactionProducts = new HashMap<> ();
//	public Map<String, SbmlReactionProduct> getReactionProducts() { return reactionProducts;}
//	public void addReactionProduct(SbmlReactionProduct product) {
//		product.setSbmlReaction(this);
//		this.reactionProducts.put(product.getSbmlSpecie().getEntry(), product);
//		product.getPk().getSbmlSpecie().getEntry()
//	}
//	public void setReactionProducts(
//			Map<String, SbmlReactionProduct> reactionProducts) {
//		for (String specieEntry : reactionProducts.keySet()) {
//			SbmlReactionProduct reactionProduct = reactionProducts.get(specieEntry);
//			reactionProduct.setSbmlReaction(this);
//		}
//		this.reactionProducts = reactionProducts;
//	}
	
	
//	@ManyToMany(cascade={CascadeType.ALL})
//	@JoinTable(name="SBML_REACTION_REACTANTS", 
//		joinColumns={@JoinColumn(name="ID_REACTION")}, 
//		inverseJoinColumns={ @JoinColumn(name="ID_METABOLITE")})
//	Map<String, SbmlMetaboliteSpecieEntity> listOfReactants = new HashMap<> ();
	
	
//	@ManyToMany(cascade={CascadeType.ALL})
//	Map<String, SbmlMetaboliteSpecieEntity> listOfProducts = new HashMap<> ();
}
