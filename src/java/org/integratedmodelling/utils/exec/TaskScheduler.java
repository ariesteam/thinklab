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

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.rest.RESTTask;

/**
 * A simple scheduler that can be fed tasks and guarantees that at most a given
 * maximum number of them is executed at a time. Also supports listeners to enable
 * notification of task start, end and enqueuing.
 * 
 * @author Ferdinando Villa
 *
 */
public class TaskScheduler implements ITaskScheduler {

	int maxConcurrentTasks = 1;
	int _delay = 200;
	
	volatile private boolean _stopped = true;
	public ArrayList<Listener> _listeners = new ArrayList<Listener>();

	protected class TaskThread extends Thread {

		@Override
		public void run() {
			try {
				while (!_stopped) {
					try {
						sleep(_delay);
						try {
							checkNext();
						} catch (Exception e) {
							throw new ThinklabRuntimeException(e);
						} finally {
						}
					} catch (Throwable ex) {
						throw new ThinklabRuntimeException(ex);
					}
				}
			} finally {
			}
		}
		
		private void checkNext() throws ThinklabException {
						
			/*
			 * check if we have anything to remove
			 */
			for (Thread t : _current) {

				if (!t.isAlive() || (t instanceof RESTTask && ((RESTTask)t).isFinished())) {
					_current.remove(t);
					for (Listener l : _listeners) {
						l.notifyTaskFinished(t, _current.size(), _queue.size());
					}
				}
			}
			
			/*
			 * start as many tasks as we can afford
			 */
			if (_queue.size() > 0) {
				while (_current.size() < maxConcurrentTasks && _queue.size() > 0) {
					try {
						if (_queue.size() > 0) {
							Thread t = _queue.remove();
							_current.add(t);
							t.start();
							for (Listener l : _listeners) {
								l.notifyTaskStarted(t, _current.size(), _queue.size());
							}
						}
					} catch (Exception e) {
						throw new ThinklabInternalErrorException(e);
					}
				}	
			}
		}
	}
	
	protected ConcurrentLinkedQueue<Thread> _queue = new ConcurrentLinkedQueue<Thread>();
	protected ConcurrentLinkedQueue<Thread> _current = new ConcurrentLinkedQueue<Thread>();
	protected TaskThread _polling = null;
	
	public TaskScheduler(int maxConcurrentTasks) {
		this.maxConcurrentTasks = maxConcurrentTasks;
	}

	public void enqueue(Thread task) {
		_queue.add(task);
		for (Listener l : _listeners) {
			l.notifyTaskEnqueued(task, _current.size(), _queue.size());
		}
	}

	/**
	 * Start scheduling
	 */
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
	}
	
	/**
	 * Stop scheduling, retain any unfinished tasks in queue so a new start() will
	 * pick them up.
	 */
	public void stop() {
		_stopped = true;
	}

	public synchronized boolean idle() {
		return _current.isEmpty();
	}
	
	public synchronized int executing() {
		return _current.size();
	}
	
	public synchronized int scheduled() {
		return _queue.size();
	}
	
	public int maxTaskCount() {
		return maxConcurrentTasks;
	}
	
	public void addListener(Listener listener) {
		_listeners.add(listener);
	}

}
