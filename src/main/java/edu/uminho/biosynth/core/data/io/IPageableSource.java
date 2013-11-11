package edu.uminho.biosynth.core.data.io;

import java.util.List;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;

public interface IPageableSource extends ILocalSource {

	public List<GenericReaction> getByPageReactionInformation(int page, int pagesize);
	public List<GenericMetabolite> getByPageMetaboliteInformation(int page, int pagesize);
	public List<GenericEnzyme> getByPageEnzymeInformation(int page, int pagesize);
	public List<GenericReactionPair> getByPagePairInformation(int page, int pagesize);

}
