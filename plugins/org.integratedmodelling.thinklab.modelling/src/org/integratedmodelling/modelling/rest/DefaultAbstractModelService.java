package org.integratedmodelling.modelling.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.model.Scenario;
import org.integratedmodelling.modelling.storage.NetCDFArchive;
import org.integratedmodelling.modelling.storyline.Storyline.Listener;
import org.integratedmodelling.modelling.visualization.ObservationListing;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.Pair;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * 
 * @author ferdinando.villa
 *
 */
public abstract class DefaultAbstractModelService extends DefaultRESTHandler {

	// processing status codes. Status changes are communicated to listener.
	public static final int 
		IDLE = 0, 
		COMPUTING = 1, 
		COMPUTED = 2, 
		ERROR = 3, 
		DISABLED = 4, 
		PENDING = 5;
	
	Scenario  _scenario = null;
	IModel    _model = null;
	IContext  _context = null;
	IConcept  _concept = null;
	IKBox     _kbox = KBoxManager.get();
	boolean   _visualize = false;
	boolean   _dump = false;
	String    _ncout = null;
	
	protected int status = IDLE;
	
	// results
	IContext mresult = null;
	IVisualization visualization = null;
	String dump = null;
	
	public abstract Representation run() throws ThinklabException;
	
	/*
	 * dump context to string, set this.dump to it, and set it in result info
	 */
	public void makeDump() throws ThinklabException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ObservationListing lister = new ObservationListing(mresult);
		lister.dump(ps);
		info(ps.toString());
	}
	
	/*
	 * create visualization from context and communicate URL to recipient
	 */
	public void visualize() {
		
	}
	
	/*
	 * create NC file from context and set it as download with requested filename
	 */
	public void makeNCOutput() throws ThinklabException {

		Pair<File, String> outf = this.getFileName(_ncout, getSession());
		NetCDFArchive out = new NetCDFArchive();
		out.setContext(mresult);
		out.write(outf.getFirst().toString());
		addDownload(outf.getSecond(), _ncout);
	}
	
	public class ModelThread extends Thread {

		IKBox  kbox = null;
		IModel model = null;
		IContext context = null;
		ISession session = null;
		Listener listener = null;
		
		public ModelThread(IKBox kbox, IModel model, IContext context, ISession session, Listener listener) {
			this.kbox = kbox;
			this.model = model;
			this.context = context;
			this.session = session;
			this.listener = listener;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			status = COMPUTING;
			
			boolean errors = false;
			try {

				ModellingPlugin.get().logger().info("computation of " + 
						((Model)model).getNamespace() + "/" + ((Model)model).getId() + 
						" started in " + 
						((Context)context).getNamespace() + "/" + ((Context)context).getId());	
				
				ArrayList<IContextualizationListener> lst = null;
				if (listener instanceof IContextualizationListener) {
					lst = new ArrayList<IContextualizationListener>();
					lst.add((IContextualizationListener)listener);
				}
				
				IQueryResult r = 
					ModelFactory.get().run((Model) this.model, kbox, session, lst, this.context);					
				
				if (r.getTotalResultCount() > 0) {
					IValue res = r.getResult(0, session);
					mresult = (IContext) ((ContextValue)res).getObservationContext();
				}
				
				status = COMPUTED;
				
				if (_visualize) {
					visualize();
				}
				
				if (_dump) {
					makeDump();
				}
				
			} catch (Exception e) {
				
				status = ERROR;
				fail(e.getMessage());
				errors = true;
				
			} finally {
				
				// TODO log user and possibly run time for billing
				ModellingPlugin.get().logger().info(
						"computation of " + model.getName() + " finished" + 
						(errors ? " with errors" : " successfully"));
			}
		}
	}
	
	@Get
	public Representation runInternal() throws ThinklabException {
		
		processArguments();
		return run();
	}

	private void processArguments() throws ThinklabException {

		if (getArgument("model") != null) {
			_model = ModelFactory.get().requireModel(getArgument("model"));			
		}
			
		if (getArgument("context") != null) {
			_context = ModelFactory.get().requireContext(getArgument("context"));
		}
		
		if (getArgument("kbox") != null) {
			_kbox = KBoxManager.get().requireGlobalKBox(getArgument("kbox"));
		}

		if (getArgument("concept") != null) {
			_concept = KnowledgeManager.getConcept(getArgument("concept"));
		}

		if (getArgument("scenario") != null) {

			String sc = getArgument("scenario");
			_scenario = ModelFactory.get().requireScenario(sc);
			if (_model != null) {
				_model = (Model)_model.applyScenario(_scenario);
			}
		}	

		if (getArgument("dump") != null && getArgument("dump").equals("true"))
			_dump = true;
		
		if (getArgument("visualize") != null && getArgument("visualize").equals("true"))
			_dump = true;
		
		if (getArgument("ncoutput") != null)
			_ncout = getArgument("ncoutput");
		
	}

	
}
