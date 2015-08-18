package pt.uminho.sysbio.biosynthframework.core.data.io;

import java.util.List;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;

@Deprecated
public interface IPageableSource extends ILocalSource {

	public List<GenericReaction> getByPageReactionInformation(int page, int pagesize);
	public List<GenericMetabolite> getByPageMetaboliteInformation(int page, int pagesize);
	public List<GenericEnzyme> getByPageEnzymeInformation(int page, int pagesize);
	public List<GenericReactionPair> getByPagePairInformation(int page, int pagesize);

}
