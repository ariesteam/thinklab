package org.integratedmodelling.modelling.random;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.implementations.datasources.MemObjectContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.corescience.ClassificationModel;
import org.integratedmodelling.modelling.corescience.ObservationModel;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.modelling.implementations.observations.BayesianTransformer;
import org.integratedmodelling.modelling.interfaces.IContextOptional;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.ITrainableModel;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.modelling.model.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.riskwiz.bn.BayesianFactory;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

public class BayesianModel extends DefaultStatefulAbstractModel implements IContextOptional, ITrainableModel {

	public BayesianModel(String namespace) {
		super(namespace);
	}

	String source = null;
	String algorithm = null;
	ArrayList<IConcept> keepers = new ArrayList<IConcept>();
	ArrayList<IConcept> required = new ArrayList<IConcept>();
	IModel resultModel = null;
	
	@Override
	protected void copy(DefaultAbstractModel model) {
		super.copy(model);
		algorithm = ((BayesianModel)model).algorithm;
		keepers = ((BayesianModel)model).keepers;
		source = ((BayesianModel)model).source;
		resultModel = ((BayesianModel)model).resultModel;
		required = ((BayesianModel)model).required;
	}

	@Override
	public String toString() {
		return ("bayesian");
	}
	
	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {
		
		if (keyword.equals(":import")) {
			this.source = argument.toString();
		} else if (keyword.equals(":algorithm")) {
			this.algorithm = argument.toString();
		} else if (keyword.equals(":keep")) {
			
			Collection<?> p = (Collection<?>) argument;
			for (Object c : p)
				keepers.add(ModelFactory.annotateConcept(namespace, c.toString()));

		} else if (keyword.equals(":required")) {
			
			Collection<?> p = (Collection<?>) argument;
			for (Object c : p)
				required.add(ModelFactory.annotateConcept(namespace, c.toString()));

		} else if (keyword.equals(":result")) {
			
			resultModel = (IModel)argument;
			this.metadata.putAll(((DefaultAbstractModel)resultModel).getMetadata());
			
		} else super.applyClause(keyword, argument);
			
	}

	@Override
	public boolean isStateful() {
		return resultModel != null;
	}
	
	@Override
	public void addObservedModel(IModel model) {
		
		if (! (((Model)model).getDefinition() instanceof ClassificationModel)) {
			throw new ThinklabRuntimeException(
					"bayesian node " + model.getName() + 
					" should be a classification");
		}

		// anything that is specifically modeled becomes a keeper automatically
		keepers.add(((DefaultAbstractModel)((Model)model).getDefinition()).getObservableClass());
		super.addObservedModel(model);
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		super.validateMediatedModel(model);
	}


	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getConfigurableClone() {
		
		BayesianModel ret = new BayesianModel(namespace);
		ret.algorithm = algorithm;
		ret.required  = required;
		ret.keepers = keepers;
		ret.resultModel = resultModel;
		ret.source = source;
		ret.copy(this);
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags) throws ThinklabException {

		ArrayList<Object> arr = new ArrayList<Object>();
		IndirectObservation resultObservation = null;
		
		if (resultModel != null) {
			Polylist ls = 
				((Model)resultModel).getDefinition().buildDefinition(kbox, session, context, flags);
			resultObservation = 
				(IndirectObservation) ObservationFactory.getObservation(session.createObject(ls));	
		}
		
		arr.add("modeltypes:BayesianTransformer");
		
		if (!isMediating())
			arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, this.observableSpecs));
				
		if (source != null)
			arr.add(Polylist.list(BayesianTransformer.HAS_NETWORK_SOURCE, source));

		if (algorithm != null)
			arr.add(Polylist.list(BayesianTransformer.HAS_BAYESIAN_ALGORITHM, algorithm));

		for (int i = 0; i < keepers.size(); i++) {
			arr.add(Polylist.list(
						ModelFactory.RETAINS_STATES, 
						keepers.get(i).toString()));
			
		}
		
		for (int i = 0; i < required.size(); i++) {
			arr.add(Polylist.list(
						ModelFactory.REQUIRES_STATES, 
						required.get(i).toString()));
		}
				
		/*
		 * communicate how to model specific nodes that had their
		 * model specified by passing a prototype observation.
		 * 
		 * FIXME: this will create observations that will not be resolved, so when the
		 * models are mediating, they won't have their mediated counterpart so they must
		 * include an observable. The whole observable transmission strategy should be revised.
		 * For now we add the stupid flags and pass it to buildDefinition, but the function
		 * shouldn't need any flags.
		 */
		for (IModel c : observed) {
			arr.add(Polylist.list(
					BayesianTransformer.HAS_PROTOTYPE_MODEL,
					((Model)c).getDefinition().buildDefinition(kbox, session, null, FORCE_OBSERVABLE)));
		}

		Polylist ret = Polylist.PolylistFromArrayList(arr);

		ret = ObservationFactory.addReflectedField(ret, "observed", observed);
		
		if (resultObservation != null) {
			ret = ObservationFactory.addReflectedField(ret, "outputObservation", resultObservation);
		}
		
		return addDefaultFields(ret);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel train(IKBox kbox, ISession session, Object... params)
			throws ThinklabException {

		// TODO parameterize
		String method = "EM";
		
		IModel ret = null;
		
		/*
		 * open the model and build a list of all the observables. Count the 
		 * leaves (from model dependencies) and the total. If total - leaves > 0, we can train.
		 */
		 IBayesianNetwork bn = BayesianFactory.get().createBayesianNetwork(source);
		 ArrayList<IConcept> observers = new ArrayList<IConcept>();
		 
		 HashSet<IConcept> outputs = new HashSet<IConcept>();
		 HashSet<IConcept> inputs = new HashSet<IConcept>();
		 
		 for (String c : bn.getAllNodeIds()) {
			 IConcept obs = 
					KnowledgeManager.getConcept(getObservableClass().getConceptSpace() + ":" + c);
			 if (findDependencyFor(obs) == null) {
				 outputs.add(obs);
			 } else {
				 inputs.add(obs);
			 }
			 observers.add(obs);
		 }
		
		 if (outputs.size() == 0)
			 return null;
		 
		/*
		 * collect context and training directory from parameters; complain if
		 * not found.
		 */
		IContext context = null;
		File trainingDir = null;
		for (Object p : params) {
			if (p instanceof IContext) {
				context = (IContext)p;
			} else if (p instanceof File) {
				trainingDir = (File)p;
			}
		}
		
		
		/*
		 * build and contextualize an ID of all the dependencies and observables
		 * using the correspondent models.
		 */
		ObservationModel idnt = new ObservationModel(this.getNamespace());
		idnt.setObservable(this.getObservableClass());
		
		for (IConcept c : observers) {
			IModel m = findDependencyFor(c);
			if (m == null) {
				
				/*
				 * look it up in the observed
				 */
				for (IModel o : observed) {
					if (o.getObservableClass().equals(c)) {
						m = o;
						break;
					}
				}
			}
			if (m != null) {
				idnt.addDependentModel(m);
			}
		}
		
		session.print("computing available evidence... ");
		IQueryResult r = 
				ModelFactory.get().run(new Model(idnt), kbox, session, null, context);
			
		if (r.getTotalResultCount() > 0) {
			
			IValue res = r.getResult(0, session);
			IContext result = ((ContextValue)res).getObservationContext();
			session.print("done.\nCreating training dataset... ");
			PrintWriter out = null;
			
			File trainData = new File(trainingDir + 
					File.separator +
					"traindata.txt");

			File trainedModel = new File(trainingDir + File.separator + 
					MiscUtilities.getFileName(MiscUtilities.resolveUrlToFile(source).toString()));
			
			/*
			 * create training datafile
			 */
			try {
				out = 
					new PrintWriter(
							new FileOutputStream(trainData, true));
			} catch (FileNotFoundException e) {
				throw new ThinklabIOException(e);
			}
			
			/*
			 * remove non-classification dependencies and check what's left
			 */
			for (IConcept c : observers) {
				
				IState state = result.getState(c);
				
				if (! (state instanceof MemObjectContextualizedDatasource) &&
					! (state instanceof CategoricalDistributionDatasource)) {
					outputs.remove(c);
					inputs.remove(c);
				} else {
					// throw away the result, but instantiate all metadata
					Metadata.getImageData(state);
				}
			}
			
			if (outputs.isEmpty()) {
				session.print("no usable evidence for any of the outputs. Exiting.");
				return null;
			}
			if (inputs.isEmpty()) {
				session.print("no usable evidence for any of the inputs. Exiting.");
				return null;
			}
			
			/*
			 * write out headers
			 */
			ArrayList<IConcept> states = new ArrayList<IConcept>();
			boolean first = true;
			for (IConcept o : outputs) {
				out.print((first ? "" : "\t") + o.getLocalName());
				first = false;
				states.add(o);
			}
			for (IConcept o : inputs) {
				out.print((first ? "" : "\t") + o.getLocalName());				
				first = false;
				states.add(o);
			}
			out.println();
			
			for (int i = 0; i < result.getMultiplicity(); i++) {
				/*
				 * only write row if there is at least one output non-nil and 
				 * one input observation. 
				 */
				first = true;
				for (IConcept o : states) {
					IState state = result.getState(o);
					IConcept val = null;
					Object v = state.getValue(i);
					if (v instanceof IConcept) {
						val = (IConcept)v;
					}
					out.print((first ? "" : "\t") + (val == null ? "*" : val.getLocalName()));
					first = false;
				}
				out.println();
			}
			
			out.close();
			session.print("done.\nTraining... ");
			
			/*
			 * create BN - for now only supported if imported
			 */
			if (source == null) {
				throw new ThinklabValidationException(
						"model contains no source URL for the bayesian model: only imported models are supported");
			}
			
			bn = BayesianFactory.get().createBayesianNetwork(source.toString());
			IBayesianNetwork trainedBN = bn.train(trainData, method);
			session.print("done.\nWriting out trained model... ");
			trainedBN.write(trainedModel);
			
			session.print("all done.");
			
		} else {
			session.print("no results. Exiting.");
			return null;
		}
		
		return ret;
	
	}

	@Override
	public void applyTraining(String trainedInstanceID) {
		// TODO Auto-generated method stub
		
	}

	
	
}
