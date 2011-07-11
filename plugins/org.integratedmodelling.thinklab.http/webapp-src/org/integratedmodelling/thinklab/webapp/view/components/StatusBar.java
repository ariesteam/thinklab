package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.Hashtable;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

public class StatusBar extends Div {

	// messages
	public final static String SET = "set";
	
	private static final long serialVersionUID = -6653953855887408421L;

	String statusIcon = null;
	String message = null;
	String statusCode = null;
	ZK.ZKComponent menuDiv = null;
	String messageDivSclass = null;
	String messageSclass = null;
	
	
	/*
	 * contains a map of code -> (message, icon)
	 */
	Hashtable<String, Pair<String, String>> statusCodes = null;
	
	public StatusBar(Hashtable<String, Pair<String, String>> codes) {
		statusCodes = codes;
	}
	
	public void initialize() {
		set("idle");
	}
	
	public void setMenu(ZK.ZKComponent component) {
		menuDiv = component;
		set("idle");
	}
	
	protected Component getMenu() {
		return menuDiv == null ? null : menuDiv.get();
	}
	
	public void setMessageClass(String scl) {
		messageSclass = scl;
	}

	public void setMessageDivClass(String scl) {
		messageDivSclass = scl;
	}
	
	public void set(String state) {
		
		setState(state);
		ZK.resetComponent(this);
		appendChild(
			ZK.hbox(
				ZK.image(statusIcon),
				ZK.label(message)
					.align("left").sclass(messageSclass).width("100%"),
				ZK.div(menuDiv).align("right")
			).width("100%").sclass(messageDivSclass).get());
	}
	
	private void setState(String state) {
		
		Pair<String, String> p = statusCodes.get(state);
		
		message = p == null ? "Unknown state" : p.getFirst();
		statusIcon = p == null ? 
				"" : // TODO put in a question mark thingy
				p.getSecond();
		
	}


}
