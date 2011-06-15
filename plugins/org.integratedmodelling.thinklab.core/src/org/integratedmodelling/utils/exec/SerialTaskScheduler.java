package org.integratedmodelling.utils.exec;

import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

/**
 * This one will simply run all the tasks when started, waiting for them to finish, 
 * and must be used in a single user context.
 * 
 * @author Ferdinando Villa
 *
 */
public class SerialTaskScheduler extends TaskScheduler {

	public SerialTaskScheduler() {
		super(1);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.utils.exec.ITaskScheduler#start()
	 */
	@Override
	public void start() {

		for (Thread t : _queue) {
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		_queue.clear();
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
