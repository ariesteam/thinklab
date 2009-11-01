package org.integratedmodelling.corescience.contextualization;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceByte;
import org.integratedmodelling.corescience.implementations.datasources.MemClassContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemFloatContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemIntegerContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemLongContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemObjectContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemValueContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IStateValidator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.utils.Ticker;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;


/**
 * A stack machine with registers for mediators, aggregators, parameters, current context and last changed 
 * context dimension.s
 *  
 * @author Ferdinando
 *
 */
public class VMContextualizer<T> {

	private int _pc = 0;
	private int _actregs = 0;
	private int _accregs = 0;
	private int _aggregs = 0;
	private int _parregs = 0;
	private int _storegs = 0;
	private int _valregs = 0;
	private int _vldregs = 0;
	private int _immregs = 0;
	private int _cstords = 0;
	
	public ArrayList<Integer> _code = new ArrayList<Integer>();
	
	public ArrayList<IStateAccessor> _accessors = new ArrayList<IStateAccessor>();
	public ArrayList<IStateValidator> _validators = new ArrayList<IStateValidator>();
	public ArrayList<IValueAggregator<?>> _aggregators = new ArrayList<IValueAggregator<?>>();
	public ArrayList<Object> _immediates = new ArrayList<Object>();
	public ArrayList<IConcept> _observed = new ArrayList<IConcept>();
	public ArrayList<IContextualizedState> _datasources = new ArrayList<IContextualizedState>();
	
	public static class ContextRegister {
		int multiplicity = 1;
		boolean changed = true;
		IValue contextValue = null;
		int currentIndex = 0;
		IConcept dimension = null;
	}
	
	public static class Ins {
		int bytecode;
		String instruction;
		String description;
		Ins(int b, String i, String d) {
			bytecode = b;
			instruction = i;
			description = d;
		}
	}
	
	static final int STACKSIZE = 1024;
	
	
	/*
	 * TODO separate opcodes for push/pop of POD types; POD-specific states;
	 * parameter/constant handling; communication of state size
	 */
	
	static final int JMP = 0;
	static final int AACT = 1;
	static final int AREG = 2;
	static final int APARM = 3;
	static final int AACCS = 4;
	static final int ASTOR = 5;
	static final int PLOAD = 6;
	static final int RPOP = 7;
	static final int SSTORE = 8;
	static final int RSTORE = 9;
	static final int CACCS = 10;
	static final int CVALID = 11;
	static final int IFACT = 12;
	static final int IFCOV = 13;
	static final int INCRC = 14;
	static final int DEACT = 15;
	static final int RETURN = 16;
	static final int ACTIVATE = 17;
	static final int IREG = 18;
	static final int IFCHNG = 19;
	static final int MKSTOR = 20;
	
	// bytecodes
	static public final Ins JMP_I = new Ins(JMP, "JMP", "jump to address");
	static public final Ins AACT_I = new Ins(AACT, "AACT", "allocate new activation register");
	static public final Ins AREG_I = new Ins(AREG, "AREG", "allocate new register of stack type");
	static public final Ins APARM_I = new Ins(APARM, "APARM", "allocate new parameter register (unused)");
	static public final Ins AACCS_I = new Ins(AACCS, "AACCS", "allocate space for given accessor method");
	static public final Ins ASTOR_I = new Ins(ASTOR, "ASTOR", "allocate storage n of given size for observable");
	static public final Ins PLOAD_I = new Ins(PLOAD, "PLOAD", "load parameter on stack (unused)");
	static public final Ins RPOP_I = new Ins(RPOP, "RPOP", "pop to register in address");
	static public final Ins SSTORE_I = new Ins(SSTORE, "SSTORE", "store top of stack into given storage");
	static public final Ins RSTORE_I = new Ins(RSTORE, "RSTORE", "store given register into given storage ");
	static public final Ins CACCS_I = new Ins(CACCS, "CACCS", "call accessor method, push return on stack ");
	static public final Ins CVALID_I = new Ins(CVALID, "CVALID", "call validator, push return on stack");
	static public final Ins IFACT_I = new Ins(IFACT, "IFACT", "jump unless given activation register is true");
	static public final Ins IFCOV_I = new Ins(IFCOV, "IFCOV", "jump unless given aggregator has context covered");
	static public final Ins INCRC_I = new Ins(INCRC, "INCRC", "increment context and jump to addr unless finished");
	static public final Ins DEACT_I = new Ins(DEACT, "DEACT", "set given activation record to false");
	static public final Ins RETURN_I = new Ins(RETURN, "RETURN", "all done, exit");
	static public final Ins ACTIVATE_I = new Ins(ACTIVATE, "ACTIVATE", "activate all registries");
	static public final Ins IREG_I = new Ins(IREG, "IREG", "push immediate value in register");
	static public final Ins IFCHNG_I = new Ins(IFCHNG, "IFCHNG", "jump unless given context register has changed");
	static public final Ins MKSTOR_I = new Ins(MKSTOR, "MKSTOR", "declare custom datasource for storage of given observable");
	
	ContextRegister[] contextRegister = null;
	private IConcept _stackType;
	private boolean needsContextState;

	public VMContextualizer(IConcept stackType) {
		_stackType = stackType;
	}
	
	@SuppressWarnings("unchecked")
	public Map<IConcept, IDataSource<?>> run() throws ThinklabValidationException {
				
		int sp = 0;
				
		/* create registers */
		boolean[] active = new boolean[_actregs];
		
		/*
		 * stack and registers
		 * TODO separate by type and use specific opcodes
		 */
		T[] stack = (T[]) new Object[STACKSIZE];
		T[] regs  = (T[]) new Object[_valregs];
		
		/* states */
		IContextualizedState[] states = 
			new IContextualizedState[_storegs];
		
		/* initialize ticker */
		Ticker ticker = new Ticker();
		for (ContextRegister cr : contextRegister) {
			ticker.addDimension(cr.multiplicity);
		}
				
		/* go */
		for (int pc = 0; pc < _code.size(); pc++) {
			
			int ins = _code.get(pc);
			
			switch (ins >>> 24) {
			case JMP: 
				pc = (ins & 0x00fffff) - 1; 
				break;
			case AACT:
				// unused
				break;
			case AREG:
				// unused
				break;
			case APARM:
				// unused for now
				break;
			case AACCS:
				// unused
				break;
			case ASTOR:
				// TODO add inline size
				int size = _code.get(++pc);
				states[ins & 0x00ffffff] =
					makeState(_stackType, size);
				break;
			case PLOAD:
				// unused
				break;
			case RPOP:
				// pop stack to given register
				regs[ins & 0x00ffffff] = stack[--sp];
				break;
			case SSTORE:
				// store top of stack 
				states[ins & 0x0000ffff].addValue(stack[--sp]);
				break;
			case RSTORE: 
				// store from register
				states[(ins & 0x00ff0000) >> 16].
					addValue(regs[ins & 0x0000ffff]);
				break;
			case CACCS: 
				stack[sp++] = 
					(T) _accessors.get(ins & 0x00ffffff).getValue(regs);
				break;
			case CVALID: 
				stack[sp++] = 
					(T) _validators.get(ins & 0x00ffffff).
						validateData(stack[--sp]);
				break;
			case IFACT:
//				dumpIns(printStream, pc, IFACT_I, ins & 0x00ffffff);
				break;
			case IFCOV:
//				dumpIns(printStream, pc, IFCOV_I, ins & 0x00ffffff);
				break;
			case INCRC: 
				/* if context is fully covered, continue on */
				if (!ticker.expired()) {
					ticker.increment();
					/* jump to encoded start */
					pc = (ins & 0x00fffff) - 1;
				}
				break;
			case DEACT: 
				active[ins & 0x00ffffff] = false;
				break;
			case RETURN: 
				pc = _code.size() + 1;
				break;
			case ACTIVATE: 
				for (int i = 0; i < active.length; i++) 
					active[i] = true;
				break;
			case IREG:
				regs[(ins & 0x00ff0000) >> 16] = 
					(T) _immediates.get(ins & 0x0000ffff);
				break;
			case IFCHNG:
				if (!ticker.hasChanged((ins & 0x00ff0000) >> 16)) {
					pc = (ins & 0x0000ffff);
				}
				break;
			case MKSTOR:
				// transfer custom ds to storage register identified
				states[(ins & 0x00ff0000) >> 16] =
					_datasources.get(ins & 0x0000ffff);
				break;
			}
		}

		/* 
		 * reconstruct state map
		 */
		HashMap<IConcept, IDataSource<?>> ret = new HashMap<IConcept, IDataSource<?>>();
		for (int i = 0; i < _observed.size(); i++) {
			ret.put(_observed.get(i), states[i]);
		}
		
		return ret;
	}

	int makeInst(Ins i) {
		
		if (i.bytecode > 255)
			throw new ThinklabRuntimeException("INTERNAL: compiler needs larger immediate integers in bytecode, tell the PI");

		return i.bytecode << 24;
	}

	int makeInst(Ins i, int address) {

		if (address > 0x0000ffff)
			throw new ThinklabRuntimeException("INTERNAL: compiler needs larger immediate integers in bytecode, tell the PI");

		return (i.bytecode << 24) | (int) address;
	}

	int makeInst(Ins i, int parm1, int parm2) {
		
		if (parm1 > 255 || parm2 > 0xffff)
			throw new ThinklabRuntimeException("INTERNAL: compiler needs larger immediate integers in bytecode, tell the PI");

		return 
			(i.bytecode << 24) | 
			(((int)parm1 << 16 ) | parm2);
	}

	int makeInst(Ins i, int parm1, int parm2, int parm3) {
		
		if (parm1 > 0xff || parm2 > 0xff || parm3 > 0xff)
			throw new ThinklabRuntimeException("INTERNAL: compiler needs larger immediate integers in bytecode, tell the PI");

		return 
			(i.bytecode << 24) |
			((int)parm1 << 16) |
			((int)parm2 << 8) |
			parm1;	
	}

	/**
	 * This is used to create the state datasources for the given type
	 * You're most likely to want to override this one, if any.
	 * 
	 * @param stateType
	 * @return
	 */
	protected IContextualizedState makeState(IConcept stateType, int size) {
		
		IContextualizedState ret = null;
		
		if (stateType.is(KnowledgeManager.Integer()))
			ret = new MemIntegerContextualizedDatasource(stateType, size);
		else if (stateType.is(KnowledgeManager.Long()))
			ret = new MemLongContextualizedDatasource(stateType, size);
		else if (stateType.is(KnowledgeManager.Float()))
			ret = new MemFloatContextualizedDatasource(stateType, size);
		// catch all mixed numbers into doubles - may want to use floats instead
		else if (stateType.is(KnowledgeManager.Double()) || stateType.is(KnowledgeManager.Number()))
			ret = new MemDoubleContextualizedDatasource(stateType, size);
		else if (stateType.is(KnowledgeManager.LiteralValue()))
			ret = new MemValueContextualizedDatasource(stateType, size);
		else if (stateType.equals(KnowledgeManager.Thing()))
			// we have a mixed situation with no common types, can only use an object 
			ret = new MemObjectContextualizedDatasource(stateType, size);
		else {
			/*
			 * if we get here, we have failed to ask the CMs what datasource 
			 * they want.
			 */
			throw new ThinklabRuntimeException(
					"internal error: datasource creation for custom concept " + 
					stateType + 
					"not properly set up");
		}
		return ret;
	}
	
	private int encode(int instruction) {
		_code.add(instruction);
		return _code.size() - 1;
	}
	
	public int registerParameter(T value) {
//		encode(makeInst(AREG_I, _parregs));
		return _parregs++;
	}
	
	public int registerStateAccessor(IStateAccessor accessor) {
		if (accessor == null)
			return -1;
		_accessors.add(accessor);
//		encode(makeInst(AACCS_I, _accregs));
		return _accregs++;
	}

	public int registerStateStorage(IConceptualModel cm, IConcept observable, int size) {
		_observed.add(observable);
		if (cm.getStateType().is(KnowledgeManager.Number())) {
			encode(makeInst(ASTOR_I, _storegs));
			encode(size);
		} else {
			try {
				_datasources.add(cm.createContextualizedStorage(size));
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
			encode(makeInst(MKSTOR_I, _storegs, _cstords++));
			
		}
		return _storegs++;
	}

	public int registerAggregators(IValueAggregator<?> aggregator) {
		if (aggregator == null)
			return -1;
		_aggregators.add(aggregator);
		return _aggregs++;
	}

	public int registerValidator(IStateValidator validator) {
		if (validator == null)
			return -1;
		_validators.add(validator);
		return _vldregs++;
	}

	public int registerValue(Object value) {
		_immediates.add(value);
		return _immregs++;
	}
	
	public void encodeLoadParameter(int p) {
		encode(makeInst(PLOAD_I, p));
	}
	
	public void encodeIncrementContext(int jumpToStart) {
		encode(makeInst(INCRC_I, jumpToStart));
	}

	public void initializeContextRegister(IObservationContext context, boolean needsContextStates) 
		throws ThinklabException {

		/*
		 * TODO if needsContextStates is false, we just need a multidimensional ticker.
		 */
		this.needsContextState = needsContextStates;
		this.contextRegister = new ContextRegister[context.size()];
	
		int i = 0;
		for (IConcept c : context.getDimensions()) {
			
			ContextRegister reg = new ContextRegister();
			
			reg.dimension = c;
			reg.multiplicity = context.getMultiplicity(c);
	
			this.contextRegister[i++] = reg;
		}
	}
	
	public void dump(PrintStream printStream) {
		
		if (contextRegister.length > 0) {
			int i = 0;
			for (ContextRegister cr : contextRegister) {
				printStream.println(
						"Context dimension " + 
						i + 
						": " +
						cr.multiplicity +
						" extents of " +
						cr.dimension);
			}		
		}
		
		printStream.println("Context state " + (needsContextState ? "computed" : "not computed"));
		printStream.println("Code segment size: " + _code.size());
		
		if (_accessors.size() > 0) {
			int i = 0;
			printStream.println("Accessors:");
			for (IStateAccessor acc : _accessors)
				printStream.println(i++ + ": " + acc);
		}

		if (_immediates.size() > 0) {
			int i = 0;
			printStream.println("Immediate values:");
			for (Object imm : _immediates)
				printStream.println(i++ + ": " + imm);
		}
		
		printStream.println("Bytecode:");
		for (int pc = 0; pc < _code.size(); pc ++) {
			
			int ins = _code.get(pc);
			
			switch (ins >>> 24) {
			case JMP: 
				dumpIns(printStream, pc, JMP_I, ins & 0x00ffffff);
				break;
			case AACT:
				// unused 
				dumpIns(printStream, pc, AACT_I, ins & 0x00ffffff);
				break;
			case AREG:
				dumpIns(printStream, pc, AREG_I, ins & 0x00ffffff);
				break;
			case APARM:
				dumpIns(printStream, pc, APARM_I, ins & 0x00ffffff);
				break;
			case AACCS:
				dumpIns(printStream, pc, AACCS_I, ins & 0x00ffffff);
				break;
			case ASTOR:
				int size = _code.get(++pc);
				dumpIns(printStream, pc-1, ASTOR_I, ins & 0x00ffffff, size, _observed.get(ins & 0x00ffffff));
				break;
			case PLOAD:
				dumpIns(printStream, pc, PLOAD_I, ins & 0x00ffffff);
				break;
			case RPOP:
				dumpIns(printStream, pc, RPOP_I, ins & 0x00ffffff);
				break;
			case SSTORE:
				dumpIns(printStream, pc, SSTORE_I, ins & 0x00ffffff);
				break;
			case RSTORE: 
				dumpIns(printStream, pc, RSTORE_I, 
						(ins & 0x00ff0000) >> 16, 
						ins & 0x0000ffff);
				break;
			case CACCS: 
				dumpIns(printStream, pc, CACCS_I, ins & 0x00ffffff);
				break;
			case CVALID: 
				dumpIns(printStream, pc, CVALID_I, ins & 0x00ffffff);
				break;
			case IFACT: 
				dumpIns(printStream, pc, IFACT_I, ins & 0x00ffffff);
				break;
			case IFCOV: 
				dumpIns(printStream, pc, IFCOV_I, ins & 0x00ffffff);
				break;
			case INCRC: 
				dumpIns(printStream, pc, INCRC_I, ins & 0x00ffffff);
				break;
			case DEACT: 
				dumpIns(printStream, pc, DEACT_I, ins & 0x00ffffff);
				break;
			case RETURN: 
				dumpIns(printStream, pc, RETURN_I);
				break;
			case ACTIVATE: 
				dumpIns(printStream, pc, ACTIVATE_I);
				break;
			case IREG: 
				dumpIns(printStream, pc, IREG_I, 
						(ins & 0x00ff0000) >> 16, 
						ins & 0x0000ffff, 
						"[" + _immediates.get(ins & 0x0000ffff) + "]");
				break;
			case IFCHNG: 
				dumpIns(printStream, pc, IFCHNG_I, 
						(ins & 0x00ff0000) >> 16, 
						ins & 0x0000ffff);
				break;
			case MKSTOR: 
				dumpIns(printStream, pc, MKSTOR_I, 
						(ins & 0x00ff0000) >> 16, 
						ins & 0x0000ffff);
				break;
			}
		}
	}

	private void dumpIns(PrintStream writer, int pc, Ins ins, Object ... args) {
		
		writer.print(
				pc + 
				": " +
				ins.instruction);
		
		for (Object o : args) {
			writer.print(" " + o);
		}
				
		writer.println("\t// " + ins.description);
	}

	/**
	 * Return jump address of current program counter, so that a jump will restart execution at 
	 * the next instruction.
	 * 
	 * @return
	 */
	public int getPC() {
		return _code.size();
	}

	/*
	 * Encode a jump instruction to an address to be resolved later using resolveJump
	 */
	public int encodeJump(int jumpAddress) {
		return encode(makeInst(JMP_I, jumpAddress));
	}

	/*
	 * Encode a jump instruction to an address to be resolved later using resolveJump
	 */
	public int encodeJump() {
		return encode(makeInst(JMP_I));
	}

	public int encodeStoreFromStack(int stateId) {
		return encode(makeInst(SSTORE_I, stateId));
	}

	/*
	 * encode the jump address in a jump encoded earlier, using the return value of a previous
	 * encodeJump as first parameter.
	 */
	public void resolveJump(int jumpInstPC, int jumpAddress) {
		int bcode = _code.get(jumpInstPC);
		bcode &= jumpAddress;
		_code.set(jumpInstPC, bcode);
	}

	/**
	 * Request a register for a new parameter of stack type - values that are needed for dependent
	 * observations will be popped to the register returned by this one.
	 * 
	 * @return
	 */
	public int getNewRegister() {
		return _valregs++;
	}


	public void encodeStoreFromRegister(int stateId, int register) {
		encode(makeInst(RSTORE_I, stateId, register));		
	}


	public void encodePopToRegister(int register) {
		encode(makeInst(RPOP_I, register));
	}


	public void encodePushState(int accessorId) {
		encode(makeInst(CACCS_I, accessorId));
	}


	public int getNewActivationRegister() {
		return _actregs++;
	}


	public int encodeActivationCheck(int activationReg) {
		return encode(makeInst(IFACT_I, activationReg, 0));
	}


	public int encodeCoverageCheck(int aggregatorId) {
		return encode(makeInst(IFCOV_I, aggregatorId, 0));
	}

	public int getNextPC() {
		return _pc + 1;
	}


	public void encodeValidation(int validatorId) {
		encode(makeInst(CVALID_I, validatorId));
	}

	/*
	 * encode an instruction that will deactivate the passed register
	 */
	public void encodeDeactivation(int activationReg) {
		encode(makeInst(DEACT_I, activationReg));
	}


	public void encodeReturn() {
		encode(makeInst(RETURN_I));
	}

	/*
	 * store immediate value to register
	 */
	public void encodeRegImmediate(int register, int initialValueId) {
		encode(makeInst(IREG_I, register, initialValueId));
	}

	public void encodeActivateAll() {
		encode(makeInst(ACTIVATE_I));
	}

	public int encodeContextJump(int zi) {
		return encode(makeInst(IFCHNG_I, zi, 0));
	}

	
}
