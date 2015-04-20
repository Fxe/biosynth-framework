package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.io.InputStream;

public interface MoleculeFormatConverter {
	public String convert(InputStream input, MoleculeFormat in, MoleculeFormat out);
}
