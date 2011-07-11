package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.utils.NameGenerator;

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
	protected IObservation[] contingencies = new IObservation[0];
	protected Topology[] extentDependencies = new Topology[0];
	protected IObservation[] nonExtentDependencies = new IObservation[0];
	protected IObservation mediatedObservation = null;
	protected IObservation mediatorObservation = null;
	private boolean acceptsNodata = true;
	protected boolean acceptsDiscontinuousTopologies = true;
	
	// public so that getField can find it
	public Metadata metadata = new Metadata();
	public Metadata additionalMetadata = null;
	
	// used to sort contingencies if this is in a merger obs
	public Integer contingencyOrder = 0;

	// if this isn't null, there was a state for us in the context, and
	// we don't compute anything.
	private IState predefinedState = null;

	// if this is a dependency of a stateful merger, we need to know so we 
	// look mediated and our state isn't stored, to ensure unique observables
	// in the final context.
	private boolean isMerged = false;
	
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
		String name = this.observable.getLocalName();
		if (NameGenerator.isGenerated(name))
			name = "";
		return "[" + this.observation.getDirectType() + ": "
				+ name + " ("
				+ this.observable.getType() + ")]";
	}

	@Override
	public IObservation getMediatorObservation() {
		return mediatorObservation;
	}

	@Override
	public boolean isMediated() {
		return mediatorObservation != null || isMerged;
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
		acceptsNodata = setAcceptsContextExtrapolation();
		
		ArrayList<IObservation> dep = new ArrayList<IObservation>();
		ArrayList<IObservation> con = new ArrayList<IObservation>();
		ArrayList<IObservation> ext = new ArrayList<IObservation>();
		ArrayList<IObservation> nxt = new ArrayList<IObservation>();

		IValue fn = i.get(CoreScience.HAS_FORMAL_NAME);
		if (fn != null)
			formalName = fn.toString();
		
		/*
		 * locate and store various related for efficiency. This method is
		 * faster than getting piece by piece.
		 */
		for (IRelationship r : i.getRelationships()) {

			// save metadata properties so we can recognize the observation 
			if (r.getProperty().getConceptSpace().equals("metadata")) {
				metadata.put(r.getProperty().toString(), r.getValue().toString());
			}
			
			/* again, for speed */
			if (!r.isClassification()) {
				if (observable == null
						&& r.getProperty().is(CoreScience.HAS_OBSERVABLE)) {
					observable = r.getValue().asObjectReference().getObject();
				} else if (dataSourceHolder == null
						&& r.getProperty().is(CoreScience.HAS_DATASOURCE)) {
					dataSourceHolder = r.getValue();
				} else if (r.getProperty().is(CoreScience.CONTINGENT_TO)) {

					con.add((IObservation) r.getValue().asObjectReference()
							.getObject().getImplementation());
					
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
				} 
			}
		}

		if (dep.size() > 0)
			dependencies = dep.toArray(dependencies);
		
		if (con.size() > 0)
			contingencies = dep.toArray(contingencies);

		if (ext.size() > 0)
			extentDependencies = ext.toArray(extentDependencies);

		if (nxt.size() > 0)
			nonExtentDependencies = nxt.toArray(nonExtentDependencies);
		
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

		/*
		 * if we have been given metadata through reflection, start with those.
		 */
		if (additionalMetadata != null) {
			metadata.merge(additionalMetadata);
			// give the GC a chance
			additionalMetadata = null;
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

	public Metadata getMetadata() {
		return this.metadata ;
	}

	@Override
	public IObservation[] getContingencies() {
		return contingencies;
	}

	/**
	 * This is called by ObservationContext.validate() to expose the obs to the 
	 * overall context. Redefine as needed to validate dependencies on specific
	 * context representations.
	 * 
	 * @param ctx
	 */
	public void validateOverallContext(IObservationContext ctx) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean acceptsContextExtrapolation() {
		return this.acceptsNodata;
	}

	@Override
	public boolean acceptsDiscontinuousTopologies() {
		return this.acceptsDiscontinuousTopologies;
	}
	
	/**
	 * Redefine this to inform the system of whether contextualizing beyond the
	 * dependencies' stated context is acceptable or not. The default is true, which
	 * may mean lots of wrongness if used unknowingly. If this is false, anything
	 * that contains such observation as dependency will be shrunk to the intersection
	 * with its context. Currently this may break models with contingencies.
	 * 
	 * @return
	 */
	protected boolean setAcceptsContextExtrapolation() {
		return true;
	}

	/*
	 * USED BY THE COMPILER ONLY - this sets a predefined state in this obs,
	 * so that it gets used as is instead of recomputing the whole thing.
	 */
	public void setPredefinedState(IState state) {
		this.predefinedState = state;
	}
	
	/*
	 * USED BY THE COMPILER ONLY
	 */
	public IState getPredefinedState() {
		return predefinedState;
	}

	public void setMerged(boolean b) {
		isMerged  = b;
	}

	/**
	 * This is a callback used by the compiler - if there's anything that needs done
	 * and depends on the context before getStateType() and createState() get called, 
	 * put it here.
	 * 
	 * @param context
	 * @param session
	 * @throws ThinklabException 
	 */
	public void preContextualization(ObservationContext context,
			ISession session) throws ThinklabException {
	}
}
