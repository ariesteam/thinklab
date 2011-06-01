package org.integratedmodelling.thinklab.rest;

import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.utils.exec.TaskScheduler;
import org.restlet.representation.Representation;

public class RESTTaskScheduler extends TaskScheduler {

	public static final String N_TASKS_PROPERTY = "rest.scheduler.ntasks";

	HashMap<Long, Representation> _finished = 
		new HashMap<Long, Representation>();
	HashSet<Long> _enqueued = new HashSet<Long>();
	HashSet<Long> _started = new HashSet<Long>();
	HashMap<String,DefaultRESTHandler> _waiting = 
		new HashMap<String, DefaultRESTHandler>();
	
	public class RESTListener implements Listener {

		@Override
		public void notifyTaskEnqueued(Thread task, int currentlyExecuting,
				int currentlyScheduled) {
			_enqueued.add(task.getId());
		}

		@Override
		public void notifyTaskFinished(Thread task, int currentlyExecuting,
				int currentlyScheduled) {

			Representation result = null;
			if (task instanceof RESTTask) {
				result = ((RESTTask)task).getResult();
			}
			_finished.put(task.getId(), result);
		}

		@Override
		public void notifyTaskStarted(Thread task, int currentlyExecuting,
				int currentlyScheduled) {
			_enqueued.remove(task.getId());
			_started.add(task.getId());
		}
	}
	
	public RESTTaskScheduler(int maxConcurrentTasks) {
		super(maxConcurrentTasks);
		addListener(new RESTListener());
	}
	
	/**
	 * Check if thread with given id has finished.
	 * @param id
	 * @return
	 */
	public synchronized boolean finished(String id) {
		long iid = Long.parseLong(id);
		synchronized (_finished) {
			return _finished.containsKey(iid);
		}
	}

	/**
	 * Only call once after finished(id) returns true. Deletes every trace of the
	 * result after it's called. 
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Representation getResult(String id) {
		long iid = Long.parseLong(id);
		Representation ret = _finished.get(iid);
		synchronized (_started) {
			_started.remove(iid);
		}
		synchronized (_finished) {
			_finished.remove(iid);
		}
		return ret;
	}

	public boolean started(String cmd) {
		synchronized (_started) {
			return _started.contains(Long.parseLong(cmd));
		}
	}

	public boolean enqueued(String cmd) {
		synchronized (_enqueued) {
			return _enqueued.contains(Long.parseLong(cmd));
		}
	}
}
