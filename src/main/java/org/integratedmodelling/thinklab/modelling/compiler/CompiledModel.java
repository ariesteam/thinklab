package org.integratedmodelling.thinklab.modelling.compiler;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.CElem;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.Dependency;
import org.integratedmodelling.thinklab.modelling.debug.ModelGraph;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.Observation;
import org.integratedmodelling.utils.NameGenerator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class CompiledModel {

	private ArrayList<ContextMapper> _mappers = new ArrayList<ContextMapper>();
	private ArrayList<IState>        _states = new ArrayList<IState>();
	private ArrayList<IAccessor>     _accessors = new ArrayList<IAccessor>();
	
	private ArrayList<Op> _code = new ArrayList<Op>();
	
	private IContext _context;
	private IModel _model;
	private int _nRegisters;
		
	public CompiledModel(IModel model,
			DirectedGraph<CElem, Dependency> graph, 
			IContext context, int nRegisters) throws ThinklabException {
		
		_context = context;
		_model = model;
		_nRegisters = nRegisters;
		
		/*
		 * compilation may add registers
		 */
		compile(model, graph, context);
		
		/*
		 * TODO remove --debug
		 */
		new ModelGraph(graph).dump(false);

	}

	/*
	 * notify all accessors, create state and compile model workflow into code
	 * for a simple stackless FSM.
	 */
	private void compile(IModel model, DirectedGraph<CElem, Dependency> graph,
			IContext context) throws ThinklabException {
		
		/*
		 * topological sorting of accessors; make accessor array
		 */
		TopologicalOrderIterator<CElem, Dependency> order = 
				new TopologicalOrderIterator<CElem, Dependency>(graph);
		
		int accessorIdx = 0;
		
		while (order.hasNext()) {
				
			CElem ae = order.next();
			IAccessor acc = ae.accessor;
			IContext ctx = ae.context;
			Op checkCtx = null;
			
			ae.order = accessorIdx;
			
			ContextMapper cm = null;
			if (ctx != null) {
				cm =  new ContextMapper(context, ctx);
				if (!cm.isTrivial()) {
					_mappers.add(cm);
					_code.add(checkCtx = Op.CJMP(_mappers.size() - 1));
				} else {
					cm = null;
				}
			}
			
			boolean mediates = false;
			
			_accessors.add(acc);
			
			/*
			 * TODO
			 * compile in end condition checks only if the context has listeners worth
			 * checking.
			 */
			
			/*
			 * set all actual dependencies (may be used by mediators too) ignoring
			 * any mediation input.
			 */
			for (Dependency d : graph.incomingEdgesOf(ae)) {
				if (!d.isMediation) {

					CElem trg = graph.getEdgeTarget(d);
					((IComputingAccessor)(trg.accessor)).notifyDependency(d.observable, d.formalName);

					_code.add(Op.OBSET(accessorIdx, d.register, d.formalName));
				}
			}
			
			/*
			 * sourceReg will be >= 0 iif there is an incoming object to be mediated.
			 * 
			 * If so notify and ensure that we know the source register for the mediation.
			 */
			int sourceReg = -1;
			for (Dependency d : graph.incomingEdgesOf(ae)) {
				if (d.isMediation) {
					
					CElem src = graph.getEdgeSource(d);
					CElem trg = graph.getEdgeTarget(d);
					((IMediatingAccessor)(trg.accessor)).notifyMediatedAccessor(src.accessor);
					sourceReg = d.register;
				}
			}
			
			/*
			 * mediate from register to other register
			 */
			if (sourceReg >= 0) {
				
				/*
				 * determine target register: if anything uses our value, it's that register (can only be
				 * one unless Ken has been really creative). Otherwise, we add a register so we can store 
				 * the state.
				 * 
				 * at the same time, set mediates = true if we are a mediator that gets mediated in turn,
				 * so that we don't store the intermediate state later.
				 */
				int targetReg = -1;
				for (Dependency d : graph.outgoingEdgesOf(ae)) {
					targetReg = d.register;
					if (d.isMediation)
						mediates = true;
				}
				if (targetReg /* still */ < 0) {
					targetReg = _nRegisters ++;
				}
					
				if (acc instanceof IComputingAccessor) {
					/*
					 * mediate and process object
					 */
					_code.add(Op.MCALL(accessorIdx, sourceReg, targetReg));

				} else {
					/*
					 * just mediate
					 */
					_code.add(Op.MEDIATE(accessorIdx, sourceReg, targetReg));
				}
				
				/*
				 * we don't store the state if we are middlemen in a mediation
				 * chain.
				 */
				if (!mediates && ae.model != null) {
					_states.add(ae.model.getObserver().createState(ae.model.getObservables().get(0), context));
					_code.add(Op.STORE(targetReg, _states.size() - 1));
				}
				
			} else {

				/*
				 * if have code, compute it and set output observables. 
				 */
				if (acc instanceof IComputingAccessor) 
					_code.add(Op.CALL(accessorIdx));

				HashSet<Integer> rcomputed  = new HashSet<Integer>();
				HashSet<String> osigs  = new HashSet<String>();
				
				for (Dependency d : graph.outgoingEdgesOf(ae)) {
					
					if (rcomputed.contains(d.register))
						continue;
				
					/*
					 * remember seen observable to avoid storing it later
					 */
					osigs.add(((SemanticObject<?>)(d.observable)).getSignature());
					
					/*
					 * extract to register and store each observable. If not a computing
					 * accessor, should be just a serial accessor with no name, use
					 * simple extract opcode.
					 */
					if (acc instanceof IComputingAccessor) {
						_code.add(Op.OBGET(accessorIdx, d.formalName, d.register));
					} else {
						_code.add(Op.VLGET(accessorIdx, d.register));						
					}
					
					if (ae.model != null) {
						_states.add(ae.model.getObserver().createState(d.observable, context));
						_code.add(Op.STORE(d.register, _states.size() - 1));
					}
					
					rcomputed.add(d.register);
				}
				
				/*
				 * extract and store any explicit observables that we haven't already used as part of
				 * the model dataflow. This can only happen if the accessor is a computing one.
				 */
				if (ae.model != null && ae.accessor instanceof IComputingAccessor) {
					for (ISemanticObject<?> o : ae.model.getObservables()) {
				
						if (osigs.contains(((SemanticObject<?>)o).getSignature()))
							continue;
						
						int targetReg = _nRegisters ++;
						
						/*
						 * give a unique name to the observable, notify to the accessor,
						 * and use that for retrieval of the value.
						 */
						String name = NameGenerator.newName();
						((IComputingAccessor)(ae.accessor)).notifyDependency(o, name);
						_code.add(Op.OBGET(accessorIdx, name, targetReg));
						
						_states.add(ae.model.getObserver().createState(o, context));
						_code.add(Op.STORE(targetReg, _states.size() - 1));
					}
				}

			}

			
			if (checkCtx != null) {
				
				/*
				 * resolve jump address for context mediator if context needs checking.
				 */
				checkCtx._jmpAddr = _code.size();
			}
		
			accessorIdx ++;
		}
		
		/*
		 * compile in RET op in case we need to jump to it. It just means "go to the next
		 * context state".
		 */
		_code.add(Op.NEXT());
				
	}
	
	/**
	 * Run the code and return true if ran to completion, false if interrupted by user action or error.
	 * 
	 * TODO handle stop conditions and exceptions, which are the only situations when this returns false
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	private boolean runCode() throws ThinklabException {

		Object[] registers = new Object[_nRegisters];

		int states = _context.getMultiplicity();
		
		for (int idx = 0; idx < states; idx++) {
			
			for (int ip = 0; ip < _code.size(); ip++) {
				
				Op op = _code.get(ip);
				
				switch (op._op) {
				case STORE:
					((IModifiableState)(_states.get(op._state))).setValue(idx, registers[op._sreg]);
					break;
				case MEDIATE:
					registers[op._treg] = ((IMediatingAccessor)(_accessors.get(op._accessor))).mediate(registers[op._sreg]);
					break;
				case MCALL:
					/*
					 * for now just call mediate() - assume mediators know what to do
					 */
					registers[op._treg] = ((IMediatingAccessor)(_accessors.get(op._accessor))).mediate(registers[op._sreg]);
					break;
				case OBSET:
					((IComputingAccessor)(_accessors.get(op._accessor))).setValue(op._name, registers[op._treg]);
					break;
				case OBGET:
					registers[op._sreg] = ((IComputingAccessor)(_accessors.get(op._accessor))).getValue(op._name);
					break;
				case VLGET:
					registers[op._sreg] = ((ISerialAccessor)(_accessors.get(op._accessor))).getValue(op.getMediatedIndex(idx, _mappers));
					break;
				case CALL:
					((IComputingAccessor)(_accessors.get(op._accessor))).process(op.getMediatedIndex(idx, _mappers));
					break;
				case CJMP:
					/*
					 * TODO
					 */
					break;
				case CHKEND:
					/*
					 * TODO
					 */
					break;
				case NEXT:
					break;
				}
			}
		}
		
		return true;
	}

	public void dumpCode(PrintStream out) {
					
		NumberFormat nf = new DecimalFormat("#000");
		
		int n = 0;
		out.println("ACCESSORS:");
		for (IAccessor acc : _accessors) {
			out.println(nf.format(n++) + " " + acc);
		}
		
		n = 0;
		out.println("\nSTATES:");
		for (IState state : _states) {
			out.println(nf.format(n++) + " " + state);			
		}

		out.println("\nBYTECODE:");
		for (int ip = 0; ip < _code.size(); ip++) {
			
			Op op = _code.get(ip);
			
			switch (op._op) {
			case STORE:
				out.println(nf.format(ip) + ". STORE r" + op._sreg + " -> s" + op._state);
				break;
			case MEDIATE:
				out.println(nf.format(ip) + ". MEDIATE a" + op._accessor + " r" + op._sreg + " -> r" + op._treg);
				break;
			case MCALL:
				out.println(nf.format(ip) + ". MCALL a" + op._accessor + " r" + op._sreg + " -> r" + op._treg);
				break;
			case OBSET:
				out.println(nf.format(ip) + ". OBSET a" + op._accessor + " r" + op._treg + " -> " + op._name);
				break;
			case OBGET:
				out.println(nf.format(ip) + ". OBGET a" + op._accessor + "  " + op._name + " -> r" + op._sreg );
				break;
			case VLGET:
				out.println(nf.format(ip) + ". VLGET a" + op._accessor + "  " + " -> r" + op._sreg );
				break;
			case CALL:
				out.println(nf.format(ip) + ". CALL a" + op._accessor);
				break;
			case CJMP:
				out.println(nf.format(ip) + ". CJUMP m" + op._mapper + " " + op._jmpAddr);
				break;
			case CHKEND:
				out.println(nf.format(ip) + ". CHKEND ");
				break;
			case NEXT:
				out.println(nf.format(ip) + ". NEXT ");
				break;
			}
		}				
	}

	public IObservation run() throws ThinklabException {

		/*
		 * TODO remove or condition to debug
		 */
		dumpCode(System.out);
		
		if (runCode()) {
			
			/*
			 * add states to context
			 */
			for (IState s : _states)
				((Context)_context).addStateUnchecked(s);
			
			/*
			 * reconstruct final observation, assign to ret
			 */
			SemanticObject<?> obs = (SemanticObject<?>) _model.getObservables().get(0);
			for (IState s : _states) {
				SemanticObject<?> sobs = (SemanticObject<?>) s.getObservable();
				if (sobs.getSignature().equals(obs.getSignature())) {
					return s;
				}
			}
			
			return new Observation(obs, _context);
			
		}
		return null;
	}
	
	/*
	 * -------------------------------------------------------------------------------------------
	 * opcodes
	 * -------------------------------------------------------------------------------------------
	 */
	
	enum OPCODE {
		STORE,    // store register to indexed state
		MEDIATE,  // mediate register with accessor and set into other register
		OBSET,    // set dependency for observable from register to accessor into named slot
		OBGET,    // get computed observable state from accessor and set into register
		VLGET,    // get computed observable state from simple serial accessor
		CALL,     // call compute method; prepare outputs for retrieval
		CJMP,     // check indexed context mapper and jump to named IP if not visible
		CHKEND,   // check external stop condition, update any progress monitors
		MCALL,    // mediate register and call any code we have to process it; return output to register
		NEXT      // stop
	}
	
	static class Op {
		
		public int _jmpAddr;
		OPCODE _op; // Op OP op
		String _name;  // formal name if it applies
		int _state;
		int _accessor;
		int _sreg;
		int _treg;
		int _model;
		int _mapper = -1;
		
		protected Op(OPCODE o) {
			_op = o;
		}
		
		public int getMediatedIndex(int idx, List<ContextMapper> mappers) {
			return _mapper < 0 ? 
					idx :
					mappers.get(_mapper).map(idx);
		}

		public String toString() {
			return _op.name();
		}
		
		public static Op STORE(int reg, int state) {
			Op ret = new Op(OPCODE.STORE);
			ret._sreg = reg;
			ret._state = state;
			return ret;
		}
		
		public static Op MEDIATE(int accessor, int sourceRegister, int targetRegister) {
			Op ret = new Op(OPCODE.MEDIATE);
			ret._accessor = accessor;
			ret._sreg = sourceRegister;
			ret._treg = targetRegister;
			return ret;
		}

		public static Op MCALL(int accessor, int sourceRegister, int targetRegister) {
			Op ret = new Op(OPCODE.MCALL);
			ret._accessor = accessor;
			ret._sreg = sourceRegister;
			ret._treg = targetRegister;
			return ret;
		}
		
		public static Op OBSET(int accessor, int register, String formalName) {
			Op ret = new Op(OPCODE.OBSET);
			ret._accessor = accessor;
			ret._treg = register;
			ret._name = formalName;
			return ret;
		}
		
		public static Op OBGET(int accessor, String formalName, int register) {
			Op ret = new Op(OPCODE.OBGET);
			ret._accessor = accessor;
			ret._sreg = register;
			ret._name = formalName;
			return ret;
		}
		
		public static Op VLGET(int accessor, int register) {
			Op ret = new Op(OPCODE.VLGET);
			ret._accessor = accessor;
			ret._sreg = register;
			return ret;
		}
		
		public static Op CALL(int accessor) {
			Op ret = new Op(OPCODE.CALL);
			ret._accessor = accessor;
			return ret;
		}
		
//		public static Op () {
//			Op ret = new Op(OPCODE.CHKEND);
//			return ret;
//		}
		
		public static Op CJMP(int mapper) {
			Op ret = new Op(OPCODE.CJMP);
			ret._mapper = mapper;
			return ret;
		}
		
		public static Op NEXT() {
			Op ret = new Op(OPCODE.NEXT);
			return ret;
		}

	
	}


}
