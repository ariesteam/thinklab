package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IContextDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObservationDefinition;

@Concept(NS.CONTEXT)
public class Context extends ModelObject<Context> implements IContextDefinition {

	ArrayList<IObservation> _observations = new ArrayList<IObservation>();
	
	public void addObservation(Observation o) {
		_observations.add(o);
	}
	
	public List<IObservation> getObservations() {
		return _observations;
	}

	@Override
	public int getMultiplicity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IContext intersection(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext union(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void listen(IListener... listeners) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addObservation(IObservationDefinition observation) {
		_observations.add(observation);
	}

	@Override
	public List<IExtent> getExtents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMultiplicity(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IExtent getExtent(IConcept observable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCovered(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IState getState(IConcept observable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void merge(IObservation observation) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IExtent getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IState> getStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext collapse(IConcept dimension) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context demote() {
		return this;
	}

	@Override
	public void addObservationGeneratorFunction(IFunctionDefinition function) {
		// TODO Auto-generated method stub
		
	}

}
