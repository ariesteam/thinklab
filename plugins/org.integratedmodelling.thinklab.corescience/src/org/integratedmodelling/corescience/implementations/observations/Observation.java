package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

@InstanceImplementation(concept = "observation:Observation")
public class Observation implements IObservation, IInstanceImplementation {
	
	private String formalName = null;
	
	/*
	 * these can be both objects implementations or literals, coming from OWL,
	 * so we store the value and convert on usage. Observation structures built
	 * internally (e.g. from literals) will have these as null, and must provide
	 * their own DS and CM.
	 */
	private IValue dataSourceHolder = null;

	private IDataSource<?> dataSource = null;
	protected IInstance observable = null;
	protected IInstance observation = null;
	protected IInstance dataSourceValue = null;
	protected IObservation[] dependencies = new IObservation[0];
	protected Topology[] extentDependencies = new Topology[0];
	protected IObservation[] nonExtentDependencies = new IObservation[0];
	protected IObservation[] sameExtentAntecedents = new IObservation[0];
	protected IObservation[] antecedents = new IObservation[0];
	protected IObservation mediatedObservation = null;
	protected IObservation mediatorObservation = null;

	private HashMap<String, Object> metadata = new HashMap<String, Object>();
	
	public IDataSource<?> getDataSource()  {

		if (dataSource == null && dataSourceHolder != null) {

			if (dataSourceHolder.isObjectReference())
				try {
					dataSource = (IDataSource<?>) dataSourceHolder
							.asObjectReference().getObject().getImplementation();
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
			else
				dataSource = (IDataSource<?>) dataSourceHolder;
		}
		return dataSource;
	}

	@Override
	public IObservation getMediatedObservation() {
		return mediatedObservation;
	}

	@Override
	public IObservation[] getDependencies() {
		return nonExtentDependencies;
	}

	@Override
	public Topology[] getTopologies() {
		return extentDependencies;
	}

	@Override
	public IInstance getObservable() {
		return observable;
	}

	public IConcept getObservationClass() {
		return observation.getDirectType();
	}

	public IInstance getObservationInstance() {
		return observation;
	}
	
	public String toString() {
		return "[" + this.observation.getDirectType() + ": "
				+ this.observable.getLocalName() + " ("
				+ this.observable.getType() + ")]";
	}

	@Override
	public IObservation getMediatorObservation() {
		return mediatorObservation;
	}

	@Override
	public boolean isMediated() {
		return mediatorObservation != null;
	}

	@Override
	public boolean isMediator() {
		return mediatedObservation != null;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		/*
		 * this one is easy
		 */
		observation = i;

		ArrayList<IObservation> dep = new ArrayList<IObservation>();
		ArrayList<IObservation> ext = new ArrayList<IObservation>();
		ArrayList<IObservation> nxt = new ArrayList<IObservation>();
		ArrayList<IObservation> sea = new ArrayList<IObservation>();
		ArrayList<IObservation> ant = new ArrayList<IObservation>();

		IValue fn = i.get(CoreScience.HAS_FORMAL_NAME);
		if (fn != null)
			formalName = fn.toString();
		
		/*
		 * locate and store various related for efficiency. This method is
		 * faster than getting piece by piece.
		 */
		for (IRelationship r : i.getRelationships()) {

			/* again, for speed */
			if (!r.isClassification()) {
				if (observable == null
						&& r.getProperty().is(CoreScience.HAS_OBSERVABLE)) {
					observable = r.getValue().asObjectReference().getObject();
				} else if (dataSourceHolder == null
						&& r.getProperty().is(CoreScience.HAS_DATASOURCE)) {
					dataSourceHolder = r.getValue();
				} else if (r.getProperty().is(CoreScience.DEPENDS_ON)) {

					dep.add((IObservation) r.getValue().asObjectReference()
							.getObject().getImplementation());

					if (r.getProperty().is(CoreScience.HAS_EXTENT)) {
						ext.add((IObservation) r.getValue().asObjectReference()
								.getObject().getImplementation());
					} else {
						nxt.add((IObservation) r.getValue().asObjectReference()
								.getObject().getImplementation());
					}

					if (r.getProperty().is(CoreScience.MEDIATES_OBSERVATION)) {
						mediatedObservation = (IObservation) r.getValue()
								.asObjectReference().getObject()
								.getImplementation();
					}

				} else if (r.getProperty().is(CoreScience.DERIVED_FROM)) {
					
					ant.add((IObservation) r.getValue().asObjectReference()
							.getObject().getImplementation());
					
					if (r.getProperty().is(CoreScience.HAS_SAME_CONTEXT_ANTECEDENT)) {
						sea.add((IObservation) r.getValue().asObjectReference()
								.getObject().getImplementation());
					}
				}
			}
		}

		if (dep.size() > 0)
			dependencies = dep.toArray(dependencies);

		if (ext.size() > 0)
			extentDependencies = ext.toArray(extentDependencies);

		if (nxt.size() > 0)
			nonExtentDependencies = nxt.toArray(nonExtentDependencies);

		if (sea.size() > 0)
			sameExtentAntecedents = sea.toArray(sameExtentAntecedents);
		
		if (ant.size() > 0)
			antecedents = ant.toArray(antecedents);
		
		/*
		 * if we are mediating something and we have our own observable, we must
		 * be punished. This may be questionable in general, but that's our
		 * definition of mediation, and it works great in contextualization.
		 */
		if (mediatedObservation != null && observable != null)
			throw new ThinklabValidationException(
					"mediator observations should not declare an observable: " + 
					observable.getDirectType());

		/*
		 * ensure we know the observable if we're mediating another obs and we
		 * don't have our own observable.
		 */
		IObservation mobs = mediatedObservation;
		while (observable == null && mobs != null) {
			observable = mediatedObservation.getObservable();
			mobs = mobs.getMediatedObservation();
		}

		/*
		 * if we STILL have no observable, we're in trouble. Observables cannot
		 * be null.
		 */
		if (observable == null)
			throw new ThinklabValidationException("observation "
					+ i.getLocalName() + " has no observable");

		if (mediatedObservation != null) {
			((Observation) mediatedObservation).mediatorObservation = this;
		}

	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
		
		/*
		 * if we had no datasource, have the derived obs create one if
		 * appropriate.
		 */
		if (dataSource == null)
			dataSource = createMissingDatasource();
		
	}
	
	protected IDataSource<?> createMissingDatasource() throws ThinklabException {
		return null;
	}
	
	@Override
	public Polylist conceptualize() throws ThinklabException {

		return Polylist.list(
				CoreScience.OBSERVATION,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
						(getObservable() instanceof IConceptualizable) ? 
								((IConceptualizable)getObservable()).conceptualize() :
								getObservable().toList(null)));
	}

	@Override
	public IConcept getObservableClass() {
		return observable.getDirectType();
	}

	@Override
	public IObservation[] getAntecedents() {
		return sameExtentAntecedents;
	}

	public String getFormalName() {
		return formalName;
	}
	
	public void setDatasource(IDataSource<?> ds) {
		this.dataSource = ds;
	}

    public IObservation getExtent(IConcept extentObservable) {
            IObservation ret = null;
            for (IObservation ext : extentDependencies) {
            	if (ext.getObservableClass().is(extentObservable)) {
            		ret = ext;
            		break;
                 }
            }
            return ret;
    }
    
    @Override
    public boolean equals(Object obj) {
            return (obj instanceof Observation) ? observation
                            .equals(((Observation) obj).observation) : false;
    }
    
    @Override
    public int hashCode() {
            return observation.hashCode();
    }

	@Override
	public HashMap<String, Object> getMetadata() {
		return this.metadata ;
	}
}
