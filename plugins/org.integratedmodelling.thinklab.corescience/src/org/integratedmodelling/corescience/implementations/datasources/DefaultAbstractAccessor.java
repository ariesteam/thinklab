package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Adds a few useful methods to navigate the complexities of multidimensional contexts.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultAbstractAccessor implements IStateAccessor {

	protected MultidimensionalCursor cursor = null;
	protected IObservationContext overallContext = null;
	protected IState state = null;
	protected IObservationContext ownContext = null;
	protected ContextMapper cmapper = null;

	@Override
	public void notifyState(IState dds, IObservationContext overallContext,
			IObservationContext ownContext) throws ThinklabException {

		this.state = dds;
		this.ownContext = ownContext;
		this.overallContext = overallContext;
		
		if (dds == null) 
			this.state = createMissingState(ownContext.getObservation(), ownContext);
		
		cursor       = ContextMapper.getCursor(ownContext);
		this.cmapper = new ContextMapper(overallContext, ownContext);
	}

	/**
	 * Called only when the compiler decides that it doesn't need to store the states we 
	 * produce. If we need them to keep history, we can create private storage here.
	 * 
	 * @param observation
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	protected IState createMissingState(IObservation observation, IObservationContext context) throws ThinklabException {
		return null;
	}

	protected int getOffsetFromOverallIndex(int index) {
		return cmapper.getIndex(index);
	}
	
	protected Object getHistoryState(int[] offsets) {
		return null;
	}
}
