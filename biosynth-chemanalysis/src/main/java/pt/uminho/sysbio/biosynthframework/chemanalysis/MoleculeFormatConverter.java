package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.io.IOException;
import java.io.InputStream;

public interface MoleculeFormatConverter {
  public String convert(InputStream input, MoleculeFormat in, MoleculeFormat out, String...param) throws IOException;
}
