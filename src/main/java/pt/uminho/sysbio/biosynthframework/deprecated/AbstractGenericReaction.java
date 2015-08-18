package pt.uminho.sysbio.biosynthframework.deprecated;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

@Deprecated
public class AbstractGenericReaction<M extends AbstractGenericMetabolite> extends AbstractBiosynthEntity implements IReactionEntity<M>, Serializable{

	private static final long serialVersionUID = 1L;
	protected Map<M, Double> reactantStoichiometry;
	protected Map<M, Double> productStoichiometry;

	public AbstractGenericReaction(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<M> getReactants() {
		return this.reactantStoichiometry.keySet();
	}
	@Override
	public Set<M> getProducts() {
		return this.productStoichiometry.keySet();
	}

	@Override
	public Map<M, Double> getReactantsStoichiometryMap() {
		return this.reactantStoichiometry;
	}
	@Override
	public Map<M, Double> getProductsStoichiometryMap() {
		return this.productStoichiometry;
	}
	
	
}
