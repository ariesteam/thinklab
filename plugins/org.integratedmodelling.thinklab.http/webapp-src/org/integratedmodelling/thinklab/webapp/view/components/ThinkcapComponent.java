package org.integratedmodelling.thinklab.webapp.view.components;

import org.apache.commons.collections.MultiHashMap;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

/**
 * Portlets are created within panelchildren ZUL components. A portal layout is automatically
 * created in the mother window.  
 * @author Ferdinando
 *
 */
public  abstract class ThinkcapComponent extends Window implements AfterCompose {

	private static final long serialVersionUID = 4106491385632197295L;
	MultiHashMap statemap = new MultiHashMap();
	String state = null;
	
	protected void setup() {
		initialize();
	}
	
	/**
	 * This callback is called when the component is first initialized , after the
	 * session and (possibly) the model have been defined but before events and
	 * variables have been wired. This is the right place to use setContent() to 
	 * define the component.
	 */
	abstract public void initialize();
	
	/**
	 * This is called after initialize() is called and after all components are wired. Default
	 * does nothing. 
	 */
	public void postInitialize() {
	}
	
	public void append(ZK.ZKComponent component) {
		this.appendChild(component.get());
	}

	@Override
	public void afterCompose() {

		/*
		 * this serves both as the component generating events and as the handler catching them
		 */
		initialize();
		ZK.setupEventListeners(this, this);
		ZK.wireFieldsToComponents(this);
		postInitialize();
	}

	/**
	 * Quickly return the named child component. Supposedly used in a safe
	 * context, so throws a runtime exception in case of failure.
	 * 
	 * FIXME understand why getFellow() should work the same way but doesn't.
	 * 
	 * @param s
	 * @return
	 */
	protected Component get(String s) {
		
		Component ret = ZK.getComponentById(this, s);
		
		if (ret == null)
			throw new ThinklabRuntimeException("child component " + s + " not found");

		return ret;
	}
	
	/**
	 * Quickly remove all children.
	 */
	public void clear() {
		ZK.resetComponent(this);
	}
	
	/*
	 * define this one to remove those components that are not children when clear() is called.
	 */
	public void deleteSubcomponents() {
		
	}
	
	/**
	 * Shortcut to use the ZK helper class directly.
	 * 
	 * @param component
	 */
	public void setContent(ZK.ZKComponent component) {
		this.appendChild(component.get());
	}
	
	public void setContent(HtmlBasedComponent component) {
		this.appendChild(component);
	}
	
	/**
	 * Create an alert modal window, wait for OK. Window will be styled using
	 * thinkcap_alert class.
	 * 
	 * @param message
	 * @param img
	 */
	public void alert(String message) {
		
		Window win = (Window) ZK.window(
			ZK.vbox(
				ZK.separator(false).height(24),
				ZK.text(message).fillx().align("center").fillx(),
				ZK.button("Ok").
					listener("onClick", new EventListener() {
						
						@Override
						public void onEvent(Event arg0) throws Exception {
							arg0.getTarget().getParent().getParent().detach();
						}
					}),
				ZK.separator(false).height(24)
				)
			).hide().sclass("thinkcap_alert").get();
		
		appendChild(win);
		win.setZindex(10000);
		win.doHighlighted();
		
	}
}
