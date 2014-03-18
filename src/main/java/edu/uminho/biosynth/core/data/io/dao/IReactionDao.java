package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericReaction;

public interface IReactionDao<R extends GenericReaction> {

	public R getReactionInformation(Serializable id);
	public R saveReactionInformation(R reaction);
	public List<Serializable> getAllReactionIds();
}
