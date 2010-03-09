package org.integratedmodelling.utils.exec;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

/**
 * A simple scheduler that can be fed with tasks and guarantees that at most a given
 * maximum number sof them is executed at a time. 
 * 
 * @author Ferdinando Villa
 *
 */
public class TaskScheduler {

	int maxConcurrentTasks = 1;
	int _delay = 200;
	
	volatile private boolean _stopped = true;
	
	public abstract class Task extends Thread {
		public abstract boolean finished();
	}
	
	private class TaskThread extends Thread {

		@Override
		public void run() {
			try {
				while (!_stopped) {
					try {
						if (_queue.isEmpty()) {
							sleep(_delay);
						} else {
							try {
								checkNext();
							} finally {
							}
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
			for (Task t : _current) {
				if (t.finished()) {
					_current.remove(t);
				}
			}
			
			/*
			 * start as many tasks as we can afford
			 */
			if (_queue.size() > 0) {
				while (_current.size() < maxConcurrentTasks) {
					try {
						Task t = _queue.remove();
						t.start();
						_current.add(t);
					} catch (Exception e) {
						throw new ThinklabInternalErrorException(e);
					}
				}	
			}
		}
	}
	
	ConcurrentLinkedQueue<Task> _queue = new ConcurrentLinkedQueue<Task>();
	ConcurrentLinkedQueue<Task> _current = new ConcurrentLinkedQueue<Task>();
	private TaskThread _polling = null;
	
	public TaskScheduler(int maxConcurrentTasks) {
		this.maxConcurrentTasks = maxConcurrentTasks;
	}

	public void enqueue(Task task) {
		_queue.add(task);
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

	public boolean idle() {
		return _current.isEmpty();
	}
	
	public int executing() {
		return _current.size();
	}
	
	public int scheduled() {
		return _queue.size();
	}
}
