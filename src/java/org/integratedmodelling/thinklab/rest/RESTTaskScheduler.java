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
package org.integratedmodelling.thinklab.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.collections.Triple;
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
	 * Returns descriptors for each task in the current situation. The triple contains
	 * the ID, the command/service that generated the task, and the status \
	 * (0 = error, 1 = enqueued, 2 = started, 3 = finished, 4 = waiting)
	 * @return
	 */
	public synchronized Collection<Triple<Long, String, Integer>> getTasksDescriptions() {
		
		ArrayList<Triple<Long,String,Integer>> ret = new ArrayList<Triple<Long,String,Integer>>();
		
		return ret;
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
