package pt.uminho.sysbio.biosynthframework.deprecated;

import java.io.Serializable;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.Metabolite;

@Deprecated
public class AbstractGenericMetabolite extends AbstractBiosynthEntity implements Metabolite, Serializable {

	private static final long serialVersionUID = 1L;
	protected String formula;

	public AbstractGenericMetabolite(String id) {
		super(id);
	}

	@Override
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

}
