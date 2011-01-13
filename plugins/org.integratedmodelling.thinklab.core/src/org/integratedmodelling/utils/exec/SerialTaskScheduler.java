package org.integratedmodelling.utils.exec;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

/**
 * A simple scheduler that can be fed tasks and guarantees that at most a given
 * maximum number of them is executed at a time. Also supports listeners to enable
 * notification of task start, end and enqueuing.
 * 
 * @author Ferdinando Villa
 *
 */
public class SerialTaskScheduler extends TaskScheduler {

	int maxConcurrentTasks = 1;
	int _delay = 200;
	
	public interface Task {
		public abstract boolean finished();
	}
	
	public SerialTaskScheduler() {
		super(1);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#start()
	 */
	@Override
	public void start() {
		
		if (_polling != null) {
			stop();
			try {
				Thread.sleep(_delay*4);
			} catch (InterruptedException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		_polling = new TaskThread();
		_stopped = false;
		_polling.start();
		
		// just wait 
		while (_queue.size() > 0) {
			try {
				Thread.sleep(_delay*4);
			} catch (InterruptedException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#stop()
	 */
	@Override
	public void stop() {
		_stopped = true;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#idle()
	 */
	@Override
	public synchronized boolean idle() {
		return _current.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#executing()
	 */
	@Override
	public synchronized int executing() {
		return _current.size();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#scheduled()
	 */
	@Override
	public synchronized int scheduled() {
		return _queue.size();
	}
	
	public int maxTaskCount() {
		return maxConcurrentTasks;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#addListener(org.integratedmodelling.utils.exec.SerialTaskScheduler.Listener)
	 */
	@Override
	public void addListener(Listener listener) {
		_listeners.add(listener);
	}
}
