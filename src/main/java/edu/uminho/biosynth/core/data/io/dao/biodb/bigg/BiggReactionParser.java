package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;

public interface BiggReactionParser {
	public void setEquationParser(BiggEquationParser biggEquationParser);
	public List<BiggReactionEntity> parseReactions(InputStream inputStream) throws IOException;
}
