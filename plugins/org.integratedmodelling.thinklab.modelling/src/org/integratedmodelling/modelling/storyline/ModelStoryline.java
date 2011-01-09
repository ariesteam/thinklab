package org.integratedmodelling.modelling.storyline;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.exec.TaskScheduler;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

public class ModelStoryline extends Storyline {

	private static final long serialVersionUID = -561462173124000708L;
	
	IModel        model;
	IConcept      observable;
	boolean       isCovered = false;
	/*
	 * the set of all possible models that can compute this storyline in specified
	 * contexts. Read from the storyline file. We can only run one at a time.
	 */
	ArrayList<Pair<IModel,IContext>> models = new ArrayList<Pair<IModel,IContext>>();
	
	// processing status
	public static final int IDLE = 0, COMPUTING = 1, COMPUTED = 2, ERROR = 3, DISABLED = 4, PENDING = 5;
	
	//
	public int status = DISABLED;

	private IVisualization visualization;
	private TaskScheduler scheduler;
	private IKBox kbox;

	
	public interface Listener {
		public void onStatusChange(int original, int newstatus);
	}
	/**
	 * The thread that actually does the modeling work.
	 * 
	 * @author Ferdinando
	 *
	 */
	public class ModelThread extends TaskScheduler.Task {

		IKBox  kbox = null;
		ISession session = null;
		Listener listener = null;
		
		private volatile boolean isComputing;
		
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
				listener.onStatusChange(IDLE, COMPUTING);
			
			isComputing = true;			
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
				getVisualization().initialize(getContext(), template().getProperties());
				getVisualization().visualize();
				
				status = COMPUTED;
				if (listener != null)
					listener.onStatusChange(COMPUTING, COMPUTED);

				
			} catch (Exception e) {
				
				e.printStackTrace();
				isComputing = false;
				status = ERROR;
				if (listener != null)
					listener.onStatusChange(COMPUTING, ERROR);
				ModellingPlugin.get().logger().error(e.getMessage());
				errors = true;
				
			} finally {
				
				// TODO log user and possibly run time for billing
				ModellingPlugin.get().logger().info(
						"computation of " + model.getName() + " finished" + 
						(errors ? " with errors" : " successfully"));
			}
			
			isComputing = false;
		}

		@Override
		public boolean finished() {
			return !isComputing;
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
	 * Set the context and return whether any of our models covers it. If this returns false,
	 * the storyline should not be computed.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	public boolean setContext(IContext context) throws ThinklabException {

		this.context = context;

		// this is probably unnecessary as we create new storylines when we set the
		// context, but just in case we change logics later, force recalculation of
		// the coverage.
		coverageComputed = false;
		this.coverage = null;
		
		/*
		 * ensure the template is loaded and the model list has been read. 
		 * FIXME this call is not very nice.
		 */
		template();
		
		/*
		 * if we have no model/context pairs, we can run anywhere.
		 */
		if (models.size() == 0) {
			status = IDLE;
			return (isCovered = true);			
		}
		
		/*
		 * check that model coverage is intersecting context if there is one. If there
		 * isn't, the model is assumed global.
		 */
		for (Pair<IModel, IContext> mc : models) {
			
			if (context.intersects(mc.getSecond())) {
				this.model = mc.getFirst();
				status = IDLE;
				return (isCovered = true);
			}
		}
		
		return false;
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
	
	public ModelStoryline(IConcept observable) {
		
		this.observable = observable;		
	}

	public IModel getModel() {
		return this.model;
	}

	public void compute(int viewportX, int viewportY,
			String dir, String urlp, 
			Listener listener) throws ThinklabException {
				
		if (this.session == null)
			this.session = new Session();
		
		status = PENDING;
		
		ModelThread process = 
			new ModelThread(this.kbox, session, listener);
			
		if (process != null) {
			this.scheduler.enqueue(process);
		}
	}

	@Override
	public IConcept getObservable() {
		return observable;
	}

	public IVisualization getVisualization() throws ThinklabException {
		
		if (this.visualization == null) {
			/*
			 * find the class we want to use for visualization. Look up
			 * the hierarchy of storylines to check if any of the templates
			 * specifies it. If not, use a standard FileVisualization.
			 */
			String vclass = findProperty("visualization-class");
			if (vclass != null) {
				Class<?> cls = null;
				try {
					cls = Class.forName(vclass);
					this.visualization = (IVisualization) cls.newInstance();
					
				} catch (Exception e) {
					throw new ThinklabRuntimeException(e);
				}
			} else {
				this.visualization = new FileVisualization();
			}
		}
		return this.visualization;
	}

	@Override
	protected void processTemplate(PresentationTemplate template) {

		if (template.getCustomNodes() != null) {
			
			for (Node node : template.getCustomNodes()) {
				if (node.getNodeName().equals("model")) {
					String m = XMLDocument.getAttributeValue(node, "id");
					String c = XMLDocument.getAttributeValue(node, "context");
				
					try {
						IModel   mod = ModelFactory.get().requireModel(m);
						IContext con = ModelFactory.get().requireContext(c);
					
						this.models.add(new Pair<IModel, IContext>(mod,con));
						
					} catch (ThinklabException e) {
						throw new ThinklabRuntimeException(e);
					}
				}
			}
		}

	}
	
	public ArrayList<Pair<IModel, IContext>> getModelCoverage() {
		return null;
	}

	public int getStatus() {
		return status;
	}
}
