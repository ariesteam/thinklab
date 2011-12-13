//package org.integratedmodelling.modelling.agents;
//
//import org.ascape.model.Agent;
//import org.ascape.model.rule.Rule;
//import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
//
//import clojure.lang.IFn;
//
//public class ThinkRule extends Rule {
//
//	private static final long serialVersionUID = -6779499732710522578L;
//	private IFn _body;
//
//	public ThinkRule(String s, IFn body) {
//		super(s);
//		this._body = body;
//	}
//	
//	@Override
//	public void execute(Agent arg0) {
//		try {
//			_body.invoke(arg0);
//		} catch (Exception e) {
//			throw new ThinklabRuntimeException(e);
//		}
//	}
//
//}
