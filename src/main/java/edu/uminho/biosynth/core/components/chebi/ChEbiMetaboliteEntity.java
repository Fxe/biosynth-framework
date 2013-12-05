package edu.uminho.biosynth.core.components.chebi;

import javax.persistence.Entity;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;

@Entity
@Table(name="CHEBI_METABOLITE")
public class ChEbiMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;

}
