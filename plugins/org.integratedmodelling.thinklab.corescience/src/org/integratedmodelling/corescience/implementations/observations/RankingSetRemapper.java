package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.cmodels.RankingSetRemappingModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.literals.MappedIntSet;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@InstanceImplementation(concept=CoreScience.RANKING_SET_REMAPPER)
public class RankingSetRemapper extends Observation {
	
	public ArrayList<MappedIntSet> mappings = new ArrayList<MappedIntSet>();
	public Double defValue = null;
	
	@Override
	protected IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		return new RankingSetRemappingModel(mappings, defValue);
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
		
		for (IRelationship r : i.getRelationships("measurement:hasMapping")) {
			mappings.add(new MappedIntSet(r.getValue().toString()));
		}
		
		IValue def = i.get("measurement:hasDefaultValue");
		if (def != null)
			defValue = Double.parseDouble(def.toString());
	}

}
