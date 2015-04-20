package pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormat;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormatConverter;

public class OpenBabelMoleculeFormatConverter implements MoleculeFormatConverter {

	private final static Logger LOGGER = LoggerFactory.getLogger(OpenBabelMoleculeFormatConverter.class);
	
	private OpenBabelProcess obb;
	
	public OpenBabelMoleculeFormatConverter(String path) {
		OpenBabelProcess.OBABEL_PATH = path;
		obb = new OpenBabelProcess();
	}
	
	@Override
	public String convert(InputStream input, MoleculeFormat in,
			MoleculeFormat out) {
		String i = "-i".concat(babelFormatArg(in)); 
		String o = "-o".concat(babelFormatArg(out));
		
		try {
			String stdin = IOUtils.toString(input);
			String[] res = obb.execute(stdin, i, o);
			LOGGER.debug("stderr: {}", res[1]);
			return res[0];
		} catch (IOException e) {
			LOGGER.warn(e.getMessage());
			return null;
		}
	}
	
	public String babelFormatArg(MoleculeFormat format) {
		switch (format) {
			case SMILES: return "smi";
			case INCHI: return "inchi";
			case MOL2D: return "mol";
			case SVG: return "svg";
			default: return null;
		}
	}

}
