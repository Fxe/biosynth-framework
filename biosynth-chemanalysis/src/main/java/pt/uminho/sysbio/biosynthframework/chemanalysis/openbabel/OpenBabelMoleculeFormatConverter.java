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
		OpenBabelProcess.DEFAULT_PATH = path;
		obb = new OpenBabelProcess();
	}
	
	@Override
	public String convert(InputStream input, MoleculeFormat in,
			MoleculeFormat out, String...params) {
		String inputArg = "-i".concat(babelFormatArg(in)); 
		String outputArg = "-o".concat(babelFormatArg(out));
		
		String[] args = new String[params.length + 2];
		
		for (int a = 0; a < params.length; a++) {
			args[a] = params[a];
		}
		args[args.length - 1] = outputArg;
		args[args.length - 2] = inputArg;
		
		try {
			String stdin = IOUtils.toString(input);
			String[] res = obb.execute(stdin, args);
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
			case InChI: return "inchi";
			case MDLMolFile: return "mol";
			case SVG: return "svg";
			default: return null;
		}
	}

}
