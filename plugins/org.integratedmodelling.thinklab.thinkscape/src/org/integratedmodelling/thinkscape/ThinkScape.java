package org.integratedmodelling.thinkscape;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.growl.RenderGrowl;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.policy.RTPolicy;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinkscape.listeners.ThinkscapeSessionListener;

public class ThinkScape extends JFrame implements RTPolicy {

	private static final long serialVersionUID = -1487172704905257002L;
	static String appName = "ThinkScape v 0.02";
	private static Logger log = null;
	private ISession session;

	public ThinkScape(ISession session) {
		super();
		this.session = session;
		setOntoTitle(null);
		log = Logger.getLogger(this.getClass());
	}

	public void showThinkscape() throws ThinklabException {
		
		RenderGrowl renderPolicy = new RenderGrowl();
		final KRPolicyThinkLab krPolicy = new KRPolicyThinkLab(session);
		final ThinkScapeGUI guiWindow = new ThinkScapeGUI();

		ApplicationFrame.createApplicationFrame(this, guiWindow,
				krPolicy, renderPolicy, null);
		guiWindow.setBrowseMenuContainer(new CustomMenuContainer());

		try {
			ApplicationFrame.getApplicationFrame().documentBase = new File(".")
					.toURL();
		} catch (Exception e) {
		}

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				System.exit(0);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				guiWindow.getDisplay().getVisualization().run("layout");
			}

			public void windowDeactivated(WindowEvent e) {
				guiWindow.getDisplay().getVisualization().cancel("layout");
			}
		});
		
		/* register session listener manually */
		session.addListener(new ThinkscapeSessionListener());
		
		this.getContentPane().setLayout(new java.awt.BorderLayout());
		this.getContentPane().add(guiWindow,
				java.awt.BorderLayout.CENTER);
		this.setSize(900, 600);
		this.setVisible(true);
	}

	/**
	 * Get the main logger object, whose output can be controlled through properties.
	 * @return
	 */
	public static Logger logger() {
		return log;
	}

	public void setOntoTitle(String name) {
		if (name == null) {
			name = "untitled";
		}
		setTitle(appName + " [" + name + "]");
	}

	@Override
	public void initializeGrOWL() {
		// TODO Auto-generated method stub
		
	}

}
