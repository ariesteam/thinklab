package org.integratedmodelling.corescience.implementations.cmodels;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.MappedIntSet;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class RankingSetRemappingModel implements IConceptualModel {

	private ArrayList<MappedIntSet> mappings = null;
	private IDataSource<?> ds = null;
	private Double defValue = null;
	
	public RankingSetRemappingModel(ArrayList<MappedIntSet> mappings, Double defValue) {
		this.mappings  = mappings;
		this.defValue = defValue;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		return new RankingSetRemappingAccessor(mappings, ds, defValue);
	}

	@Override
	public IConcept getStateType() {
		/* FIXME this should be an integer, no time to deal with the consequences right now */
		return KnowledgeManager.Double();
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		ds = dataSource;
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
	}

}
