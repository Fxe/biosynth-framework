package pt.uminho.sysbio.biosynthframework.chemanalysis.cdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.SMILESWriter;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormat;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MoleculeFormatConverter;

public class CdkMoleculeFormatConverter implements MoleculeFormatConverter {

	@Override
	public String convert(InputStream input, MoleculeFormat in,
			MoleculeFormat out) {
		IAtomContainer container = null;
		
		try {
			switch (in) {
				case SMILES: container = readSmiles(input); break;
				case INCHI:  container = readInchi(input); break;
				default: break;
			}
			System.out.println(container);
			switch (out) {
				case SMILES: return writeSmiles(container).trim();
				default: break;
			}
		
		} catch (CDKException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
		
		return null;
	}
	
	public IAtomContainer readSmiles(InputStream is) throws CDKException {
		return cdkRead(new SMILESReader(is));
	}
	
	public String writeSmiles(IAtomContainer container) throws CDKException {
		return cdkWrite(container, new SMILESWriter());
	}

	public IAtomContainer readInchi(InputStream is) throws CDKException {
		InChIToStructure inChIToStructure;
		try {
			String inchi = StringUtils.join(IOUtils.readLines(is), "");
			IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
			inChIToStructure = InChIGeneratorFactory
					.getInstance()
					.getInChIToStructure(inchi, builder);
		} catch (IOException e) {
			throw new CDKException(e.getMessage());
		}
		
		return inChIToStructure.getAtomContainer();
	}
	
	public IAtomContainer readMol(InputStream is) throws CDKException {
		return cdkRead(new MDLV2000Reader(is));
	}
	
	public IAtomContainer cdkRead(DefaultChemObjectReader reader) throws CDKException {
		IAtomContainer container = null;
		
		try {
			container = reader.read(new AtomContainer());
			reader.close();
		} catch (IOException e) {
			throw new CDKException(e.getMessage());
		}
		
		return container;
	}
	
	public String cdkWrite(IChemObject container, DefaultChemObjectWriter writer) throws CDKException {
		try {
			StringWriter sw = new StringWriter();
			writer.setWriter(sw);
			writer.write(container);
			writer.close();
			return sw.getBuffer().toString();
		} catch (IOException e) {
			throw new CDKException(e.getMessage());
		}
	}
}
