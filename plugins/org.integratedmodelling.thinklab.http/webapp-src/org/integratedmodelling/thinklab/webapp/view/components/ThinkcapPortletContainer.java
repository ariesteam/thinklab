package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabWebModel;
import org.integratedmodelling.thinklab.http.ThinklabWebSession;
import org.integratedmodelling.thinklab.webapp.view.LayoutDescriptor;
import org.integratedmodelling.thinklab.webapp.view.PortletDescriptor;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;

/**
 * Portlet containers are initialized based on their ID according to plugin extension 
 * points, and are automatically persisted according to authentication information. All
 * an application needs to create is the definition of one or more portlet containers
 * in plugin.xml. The <portal> component in thinklab uses the ID attribute to retrieve
 * the layout defined by the application, and automatically looks up a server-side persisted
 * layout for it if one exists.
 * 
 * @author Ferdinando
 *
 */
public class ThinkcapPortletContainer extends Portallayout {

	private static final long serialVersionUID = 4716284670456663417L;
	private String layout = null;
	private ArrayList<HtmlBasedComponent> portlets = new ArrayList<HtmlBasedComponent>();
	ThinklabWebSession session = null;
	ThinklabWebModel model = null;
	
	public String getLayout() {
		return this.layout;
	}
	
	public void setLayout(String layout) {
		this.layout = layout;
	}
	
	public ThinkcapPortletContainer() throws ThinklabPluginException {
		
		/*
		 * set the ThinkcapSession and the model if any
		 */
		session = ThinkWeb.getThinkcapSession(Sessions.getCurrent());
		model = (ThinklabWebModel) session.getUserModel();
        
		/*
		 * check if we have an authenticated session
		 */
        
        setup();
		
	}
	
	public void setup() throws ThinklabPluginException {
		
		if (layout != null) {
			
			LayoutDescriptor ld = /* Thinkcap.get().getLayoutDescriptor(layout)*/ null;
			
			if (ld == null)
				throw new ThinklabRuntimeException("layout " + layout + " indicated for portal is not defined");
			
			/*
			 * create all columns
			 * TODO this uses the base config - no persisted layout is read.
			 */
			for (LayoutDescriptor.Column c : ld.columns) {
				
				Portalchildren pc = new Portalchildren();
				pc.setWidth(c.width);

				for (LayoutDescriptor.PortletD pd : c.portlets) {

					PortletDescriptor pdesc  = /* Thinkcap.get().getPortletDescriptor(pd.id) */null;

					if (pdesc == null)
						throw new ThinklabRuntimeException("portlet " + layout + " indicated for portal is not defined");

					HtmlBasedComponent ppd = null;

					/* 
					 * get a regular iframe if there is a URL, or a thinkcap portlet if
					 * we have a class for it
					 */
					if (pdesc.url != null) {

						ppd = new Iframe(pdesc.url);
						
					} else {
						
						ppd = (HtmlBasedComponent) pdesc.registeringPlugin.createInstance(pdesc.view);

					}
					
					if (ppd instanceof ThinkcapComponent) {
						
//						if (pd.state != null)
//							((ThinkcapComponent) ppd).setState(pd.state);

					}
					
					if (ppd instanceof Panel) {
						
						if (pd.title != null)
							((Panel) ppd).setTitle(pd.title);

						((Panel)ppd).setClosable(pd.closable);
						((Panel)ppd).setCollapsible(pd.collapsible);
						((Panel)ppd).setOpen(pd.open);
						((Panel)ppd).setMovable(pd.moveable);
						
						pc.appendChild(ppd);
						
					} else {
						
						Panel panel = new Panel();
						Panelchildren pnc = new Panelchildren();
						pnc.appendChild(ppd);
						panel.appendChild(pnc);

						if (pd.title != null)
							panel.setTitle(pd.title);

						panel.setClosable(pd.closable);
						panel.setCollapsible(pd.collapsible);
						panel.setOpen(pd.open);
						panel.setMovable(pd.moveable);

						
						pc.appendChild(panel);
					}
					
					portlets.add(ppd);
				}
				
				this.appendChild(pc);
			}
			
			/*
			 * TODO
			 * restore portlets if layout has persisted configuration
			 */
						
		}
		
	}
}
