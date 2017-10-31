package pt.uminho.sysbio.biosynthframework.chemanalysis.inchi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormat;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormatConverter;

public class JniInchiMoleculeFormatConverter implements MoleculeFormatConverter {

	private final static Logger LOGGER = LoggerFactory.getLogger(JniInchiMoleculeFormatConverter.class);
	
	@Override
	public String convert(InputStream input, MoleculeFormat in,
			MoleculeFormat out, String...params) throws IOException {
		
		if (params.length > 0) {
			LOGGER.warn("Ignored params: {}", StringUtils.join(params, ", "));
		}
		
		switch (in) {
			case InChI: break;
			default: throw new IllegalArgumentException("aww");
		}
		
		switch (out) {
			case InChIKey: break;
			default: throw new IllegalArgumentException("aww");
		}
		
		return generateInchiKey(input);
	}
	
	public String generateInchiKey(InputStream is) throws IOException {
		return JniInchi.getInchiKeyFromInchi(IOUtils.toString(is, Charset.defaultCharset()));
	}

}
