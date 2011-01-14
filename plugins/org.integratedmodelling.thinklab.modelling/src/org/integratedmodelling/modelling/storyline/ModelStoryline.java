package org.integratedmodelling.modelling.storyline;

import java.util.List;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.exec.ITaskScheduler;

public class ModelStoryline extends Storyline {

	private static final long serialVersionUID = -561462173124000708L;
	
	IModel        model;
	IConcept      observable;
	boolean       isCovered = false;
	
	/**
	 * The thread that actually does the modeling work.
	 * 
	 * @author Ferdinando
	 *
	 */
	public class ModelThread extends Thread {

		IKBox  kbox = null;
		ISession session = null;
		Listener listener = null;
		
		public ModelThread(IKBox kbox, ISession session, Listener listener) {
			this.kbox = kbox;
			this.session = session;
			this.listener = listener;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			status = COMPUTING;
			if (listener != null)
				listener.onStatusChange(ModelStoryline.this, IDLE, COMPUTING);
			
			boolean errors = false;
			
			/*
			 * launch modeling, catch any exceptions and set status and interface if
			 * any shit happens
			 */			
			try {

				ModellingPlugin.get().logger().info("computation of " + model + " started");					
				IQueryResult r = 
					ModelFactory.get().run((Model) model, kbox, session, null, context);					
				
				if (r.getTotalResultCount() > 0) {
					
					IValue res = r.getResult(0, session);
					context = (IContext) ((ContextValue)res).getObservationContext();

				}
				
				/*
				 * create visualization and notify the browsers
				 */
				if (listener != null) {
					IVisualization vis = listener.createVisualization(model, getContext());
					vis.initialize(getContext(), template.getProperties());
					vis.visualize();
				}
				
				status = COMPUTED;
				if (listener != null)
					listener.onStatusChange(ModelStoryline.this, COMPUTING, COMPUTED);

				
			} catch (Exception e) {
				
				e.printStackTrace();
				status = ERROR;
				if (listener != null)
					listener.onStatusChange(ModelStoryline.this, COMPUTING, ERROR);
				ModellingPlugin.get().logger().error(e.getMessage());
				errors = true;
				
			} finally {
				
				// TODO log user and possibly run time for billing
				ModellingPlugin.get().logger().info(
						"computation of " + model.getName() + " finished" + 
						(errors ? " with errors" : " successfully"));
			}
		}
	}
	
	@Override
	public ShapeValue getCoverage() throws ThinklabException {
		
		if (!coverageComputed) {
			
			for (Pair<IModel, IContext> mc : models) {
				
				IExtent e = mc.getSecond().getSpace();
				if (e instanceof ArealExtent) {
					ShapeValue sh = ((ArealExtent)e).getShape();
					
					if (context != null) {
						ArealExtent ce = (ArealExtent) context.getSpace();
						sh = sh.intersection(ce.getShape());
					}
					if (this.coverage == null)
						this.coverage = sh;
					else
						this.coverage = this.coverage.union(sh);
				}
			}
			coverageComputed = true;
		}
		
		return this.coverage;
	}

	/**
	 * Set the context and check whether any of our models covers it. 
	 * 
	 * @param context
	 * @return 
	 * @return
	 * @throws ThinklabException
	 * @Override
	 */
	public void setContext(IContext context) throws ThinklabException {

		super.setContext(context);
		
		// this is probably unnecessary as we create new storylines when we set the
		// context, but just in case we change logics later, force recalculation of
		// the coverage.
		coverageComputed = false;
		this.coverage = null;
		
		/*
		 * if we have no model/context pairs, we can run anywhere.
		 */
		if (models.size() == 0) {
			status = IDLE;
			isCovered = true;			
			return;
		}
		
		/*
		 * check that model coverage is intersecting context if there is one. If there
		 * isn't, the model is assumed global.
		 */
		for (Pair<IModel, IContext> mc : models) {
			
			if (context.intersects(mc.getSecond())) {
				this.model = mc.getFirst();
				status = IDLE;
				isCovered = true;
				return;
			}
		}
	}
	
	/**
	 * Returns whether the storyline is covered in its
	 * assigned context. If there's no context it's not
	 * covered.
	 * 
	 * @return
	 */
	public boolean isCovered() {
		
		if (context == null) {
			return false;
		}
		
		return isCovered;
	}
	
	public ModelStoryline(StorylineTemplate template) {
		super(template);
		status = DISABLED;
	}

	public IModel getModel() {
		return this.model;
	}

	@Override
	public void compute(Listener listener) throws ThinklabException {
			
		if (!isCovered()) {
			return;
		}
		
		this.session = listener.getSession();
		
		status = PENDING;
		
		ModelThread process = 
			new ModelThread(KBoxManager.get(), session, listener);
			
		if (process != null) {
			listener.getScheduler().enqueue(process);
		}
	}

	@Override
	protected void processTemplate(StorylineTemplate template) {

		if (template.getModelSpecifications() != null) {
			
			List<String> mspecs  = template.getModelSpecifications();
			
			for (int i = 0; i < mspecs.size(); i += 2) {

				String m = mspecs.get(i);
				String c = mspecs.get(i+1);
				
				try {
					
					IModel   mod = ModelFactory.get().requireModel(m);
					IContext con = null;
					if (c != null)
						con = ModelFactory.get().requireContext(c);
					this.models.add(new Pair<IModel, IContext>(mod,con));
						
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + models.size() + " models]";
	}
}
