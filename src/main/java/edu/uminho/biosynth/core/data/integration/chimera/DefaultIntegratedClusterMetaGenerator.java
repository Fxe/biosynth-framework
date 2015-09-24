package edu.uminho.biosynth.core.data.integration.chimera;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.IntegrationMessageLevel;

public class DefaultIntegratedClusterMetaGenerator implements IntegratedClusterMetaGenerator{

	@Override
	public List<IntegratedClusterMeta> generateMeta(
			IntegratedMetaboliteEntity integratedMetaboliteEntity) {
		
		List<IntegratedClusterMeta> integratedClusterMetas = new ArrayList<> ();
		
		if (integratedMetaboliteEntity.getIsoFormulas().isEmpty()) {
			Set<String> formulas = new HashSet<> (integratedMetaboliteEntity.getFormulas().values());
			
			if (formulas.size() > 1) {
				IntegratedClusterMeta integratedClusterMeta = new IntegratedClusterMeta();
				integratedClusterMeta.setMessage("Multiple Formulas (unparseable)");
				integratedClusterMeta.setLevel(IntegrationMessageLevel.WARNING);
				integratedClusterMeta.setMetaType(MetaMessageCode.MULTIPLE_FORMULAS_UNPARSEABLE);
				integratedClusterMetas.add(integratedClusterMeta);
			}
		} else {
			Set<String> isoFormulas = new HashSet<> (integratedMetaboliteEntity.getIsoFormulas().values());
			
			if (isoFormulas.size() > 1) {
				IntegratedClusterMeta integratedClusterMeta = new IntegratedClusterMeta();
				integratedClusterMeta.setMessage("Multiple Formulas");
				integratedClusterMeta.setLevel(IntegrationMessageLevel.WARNING);
				integratedClusterMeta.setMetaType(MetaMessageCode.MULTIPLE_FORMULAS);
				integratedClusterMetas.add(integratedClusterMeta);
			}
		}

		
		Set<String> inchies = new HashSet<> (integratedMetaboliteEntity.getInchis().values());
		if (inchies.size() > 1) {
			IntegratedClusterMeta integratedClusterMeta = new IntegratedClusterMeta();
			integratedClusterMeta.setMessage("Multiple InChI Strings");
			integratedClusterMeta.setLevel(IntegrationMessageLevel.WARNING);
			integratedClusterMeta.setMetaType(MetaMessageCode.MULTIPLE_INCHIS);
			integratedClusterMetas.add(integratedClusterMeta);
		}
		
		return integratedClusterMetas;
	}

}
