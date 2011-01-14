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