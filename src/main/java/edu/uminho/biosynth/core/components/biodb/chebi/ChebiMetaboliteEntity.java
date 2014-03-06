package edu.uminho.biosynth.core.components.biodb.chebi;

import javax.persistence.Entity;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;

@Entity
@Table(name="CHEBI_METABOLITE")
public class ChebiMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;

}
