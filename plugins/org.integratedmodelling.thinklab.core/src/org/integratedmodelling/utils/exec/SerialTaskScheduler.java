/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.utils.exec;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;

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
