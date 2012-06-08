package org.integratedmodelling.thinklab.modelling.compiler;

import java.util.ArrayList;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.CElem;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.Dependency;
import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class CompiledModel {

	private ArrayList<ContextMapper> _mappers = new ArrayList<ContextMapper>();
	private ArrayList<Object>        _registers = new ArrayList<Object>();
	private ArrayList<IState>        _states = new ArrayList<IState>();
	private ArrayList<IState>        _observables = new ArrayList<IState>();
	private ArrayList<IAccessor>     _accessors = new ArrayList<IAccessor>();
	
	private ArrayList<Op> _code = new ArrayList<Op>();
	
	private IContext _context;
	private IModel _model;
	
	public CompiledModel(IModel model,
			DirectedGraph<CElem, Dependency> graph, 
			IContext context) throws ThinklabException {
		
		_context = context;
		_model = model;
		
		compile(model, graph, context);
	}

	private void compile(IModel model, DirectedGraph<CElem, Dependency> graph,
			IContext context) throws ThinklabException {
		
		/*
		 * topological sorting of accessors; make accessor array
		 */
		TopologicalOrderIterator<CElem, Dependency> order = 
				new TopologicalOrderIterator<CElem, Dependency>(graph);
		int mapperIdx = -1,
				accessorIdx = -1,
				modelIdx = -1,
				register = -1,
				stateIdx = -1;
		
		while (order.hasNext()) {
				
			CElem ae = order.next();
			IAccessor acc = ae.accessor;
			IModel mod = ae.model;
			IContext ctx = ae.context;
			
			ContextMapper cm = null;
			if (ctx != null) {
				cm =  new ContextMapper(context, ctx);
				if (!cm.isTrivial()) {
					_mappers.add(cm);
					mapperIdx = _mappers.size() - 1;
				} else {
					cm = null;
				}
			}
			
			// if true, we need a context mediation at each step and 
			// a jump address to skip update if the overall state is invisible to
			// this accessor.
			boolean checkCtx = !(cm == null);
			boolean mediates = false;
			boolean isComputing = acc instanceof IComputingAccessor;
							
			_accessors.add(acc);
			
			/*
			 * TODO
			 * start compiling in end condition checks only if the context has listeners worth
			 * checking.
			 */
			
			for (Dependency d : graph.incomingEdgesOf(ae)) {
				CElem src = graph.getEdgeSource(d);
				if (d.isMediation)
					mediates = true;
				
				/*
				 * notify dependency to accessor
				 */
				
				/*
				 * compile in all dependency setting with their name in the model
				 * and previously stored register
				 */
			}
			
			if (model != null) {
				
				for (ISemanticObject<?> o : model.getObservables()) {
					
					/*
					 * create state and a register for each observable
					 */
					IState state = model.getObserver().createState(o, context);
					
					/*
					 * inform the accessor that it will have to compute it and hope
					 * it doesn't throw a fit. Pass the name that we will use as a key
					 * for retrieval. 
					 */
			
					/*
					 * give each observable a register and store it for the dependent
					 * models.
					 */
				}
			}
			if (mediates) {
				
				/*
				 * find the register for the mediated accessor and compile in 
				 * the mediation call; put the result in the same slot 
				 */
				
				/*
				 * if stored, compile op to store from register to state
				 */
					
			} else {
				
				/*
				 * compile the op to compute results
				 */
				
				/*
				 * TODO this should use the outgoing nodes and the ID set into them before instead
				 * of the observable, which should not be compared at every step. It will
				 * also avoid observables that are computed but not used.
				 */
				for (ISemanticObject<?> observable : model.getObservables()) {
						
					/*
					 * compile call to extract value to register
					 */
						
					/* 
					 * compile call to store register to state
					 */
						
				}
					
			}		
			
			if (cm != null) {
				
				/*
				 * resolve jump address for context mediator
				 */
			}
			
		}
		
		/*
		 * compile in END op in case we need to jump to it.
		 */
		
	}
	
	/**
	 * return true if ran to completion
	 * @return
	 */
	public boolean runCode() {

		for (int st = 0; st < _context.getMultiplicity(); st++) {
			
			for (int ip = 0; ip < _code.size(); ip++) {
				switch (_code.get(ip)._op) {
				case STORE:
					break;
				case MEDIATE:
					break;
				case OBSET:
					break;
				case OBGET:
					break;
				case CALL:
					break;
				case CJMP:
					break;
				case CHKEND:
					break;
				case RET:
					return ip == (_code.size() - 1);
				}
			}				
		}
		
		return true;
	}

	
	public IObservation run() {
		
		IObservation ret = null;
		
		if (runCode()) {
			
			/*
			 * reconstruct final observation, assign to ret
			 */
			
		}
		return ret;
	}
	
	enum OPCODE {
		STORE,    // store register to indexed state
		MEDIATE,  // mediate register with accessor and set into same register
		OBSET,    // set dependency for observable from register to accessor into named slot
		OBGET,    // get computed observable state from accessor and set into register
		CALL,     // call compute method
		CJMP,     // check indexed context mapper and jump to named IP if not visible
		CHKEND,   // check external stop condition, update any progress monitors
		RET       // stop
	}
	
	class Op {
		
		OPCODE _op; // Op OP op
		String _name;  // formal name if it applies
		int _state;
		int _accessor;
		int _sreg;
		int _treg;
		int _model;
		int _mapper;
		
		public String toString() {
			return _op.name();
		}
	}


}
