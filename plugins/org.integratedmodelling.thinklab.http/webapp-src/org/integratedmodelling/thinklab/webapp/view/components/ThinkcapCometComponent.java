package org.integratedmodelling.thinklab.webapp.view.components;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;

/**
 * A ThinkcapComponent instrumented with a COMET pattern, suitable to serve as
 * the root component of a COMET-enabled application. Messages can be sent to
 * components and enqueued for execution asap. An independent polling thread
 * will dequeue messages, ensuring that only the thread that owns the component
 * accesses them.
 * 
 * The default implementation uses send(component, method, parameters) and uses
 * reflection to call methods on the component directly after start() has been
 * called.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinkcapCometComponent extends ThinkcapComponent {

	private static final long serialVersionUID = 2083800725196396310L;
	private int _delay = 200;
	private boolean _ceased = false;
	
	// store the methods in a static map. This is only touched by send(), which
	// is synchronized.
	static HashMap<String,Method> methods = new HashMap<String, Method>();
	
	private class Message {

		Component component;
		String methodId;
		Object[] payload;
		
		public Message(Component c, String n, Object[] p) {
			this.component = c;
			this.methodId = n;
			this.payload = p;
		}
	}

	private LinkedList<Message> _queue = new LinkedList<Message>();
	
	public class PollingThread extends Thread {

		private Desktop _desktop;
		
		PollingThread(Desktop d) {
			_desktop = d;
		}
		
		public void run() {

			try {
				while (!_ceased) {
					try {
						if (_queue.isEmpty()) {
							Threads.sleep(_delay);
						} else {
							Executions.activate(_desktop);
							try {
								dequeueNext();
							} finally {
								Executions.deactivate(_desktop);
							}
						}
					} catch (DesktopUnavailableException ex) {
						throw new ThinklabRuntimeException("internal: "
								+ ex.getMessage());
					} catch (Throwable ex) {
						throw new ThinklabRuntimeException(ex);
					}
				}
			} finally {
				// TODO finalize run - check if we need to do anything else, or
				// have a callback
				if (!_desktop.isServerPushEnabled()) 
					_desktop.enableServerPush(false);
			}
			
			System.out.println("STOPPING POLLING THREAD FUCK");
		}
	}
	
	/**
	 * Start the polling of events. From this point on, the component and its children should
	 * only be controlled through messages.
	 */
	public void start() {
		
		_ceased = false;
		/*
		 * TODO check - if it's in the thread, Execution.getCurrent() will return null and this 
		 * will fail.  
		 */
		if (!getDesktop().isServerPushEnabled()) {
			getDesktop().enableServerPush(true);
		}		
		new PollingThread(getDesktop()).start();
	}
	
	/**
	 * Call this before making modifications to the view through the API.
	 */
	public void stop() {		
		_ceased = true;
	}
	
	private void dequeueNext() throws ThinklabException {
		Message m = _queue.removeFirst();
		try {
			methods.get(m.methodId).invoke(m.component,m.payload);
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
	}
	
	/**
	 * Enqueue a message for a component. The first parameter should be a component, but it can also
	 * be any other object. The second argument is the name of the method we want to invoke on it.
	 * The other args are the arguments to the method. Methods are reflected only once and stored
	 * with name and number of args as key, so overloading won't work unless the number of parameters
	 * differs. I.e., don't be too fancy with this one.
	 * 
	 * @param target
	 * @param message
	 * @throws ThinklabException 
	 */
	public synchronized void send(Component target, String method, Object ... arguments) {
		
		int nargs = (arguments == null ? 0 : arguments.length);
		String mid = method + "_" + nargs;
		
		if (methods.get(mid) == null) {
			
			Class<?> classes[] = new Class<?>[nargs];
			for (int i = 0; i < nargs; i++) {
				classes[i] = arguments[i].getClass();
			}
			Method m  = null;
			try {
				m = target.getClass().getMethod(method,classes);
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
			if (m == null)
				throw new ThinklabRuntimeException(
					"method " + method + " with " +
					nargs + 
					" parameters does not exist in class " +
					target.getClass());
			methods.put(mid, m);
		}
		
		_queue.add(new Message(target, mid, arguments));
	}

}