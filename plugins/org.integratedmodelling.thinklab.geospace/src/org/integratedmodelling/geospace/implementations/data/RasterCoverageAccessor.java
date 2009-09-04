package org.integratedmodelling.geospace.implementations.data;

import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class RasterCoverageAccessor implements IStateAccessor {

	public RasterCoverageAccessor(RasterCoverage coverage) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getValue(Object[] registers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation, IConcept observable,
			int register, IConcept stateType) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
