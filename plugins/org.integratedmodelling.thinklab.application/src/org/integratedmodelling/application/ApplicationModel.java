package org.integratedmodelling.application;

import java.util.Stack;

/**
 * A container for the state of an application. It contains a stack of objects where application
 * steps can push a result object, and callbacks to react to any change in state.
 * @author Ferdinando Villa
 *
 */
public abstract class ApplicationModel  {

	 Stack<Object> stack = new Stack<Object>();
	 
	 void push(Object o) {
		 if (acceptInsertion(o)) {
			 stack.push(o);
			 onObjectInserted(o);
		 }
	 }
	 
	 
	 void pop() {
		 
		Object o = stack.pop();
		
		if (acceptRemoval(o)) {
			 onObjectRemoved(o);
		} else {
			stack.push(o);
		}
	 }

	protected abstract boolean acceptInsertion(Object o);

	protected abstract void onObjectInserted(Object o);
	
	protected abstract boolean acceptRemoval(Object o);

	protected abstract void onObjectRemoved(Object o);
}
