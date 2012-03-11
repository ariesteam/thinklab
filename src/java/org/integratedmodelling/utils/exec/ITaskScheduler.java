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


public interface ITaskScheduler {
	
	public static interface  Listener {
		
		public void notifyTaskEnqueued(Thread task, int currentlyExecuting, int currentlyScheduled);
		public void notifyTaskFinished(Thread task, int currentlyExecuting, int currentlyScheduled);
		public void notifyTaskStarted(Thread task, int currentlyExecuting, int currentlyScheduled);
	}
	
	public abstract void enqueue(Thread task);

	/**
	 * Start scheduling
	 */
	public abstract void start();

	/**
	 * Stop scheduling, retain any unfinished tasks in queue so a new start() will
	 * pick them up.
	 */
	public abstract void stop();

	public abstract boolean idle();

	public abstract int executing();

	public abstract int scheduled();

	public abstract void addListener(Listener listener);

}