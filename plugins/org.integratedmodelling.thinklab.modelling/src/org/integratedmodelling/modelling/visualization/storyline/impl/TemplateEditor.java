package org.integratedmodelling.modelling.visualization.storyline.impl;

import java.util.Properties;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.eclipse.swt.custom.CLabel;

public class TemplateEditor extends ApplicationWindow implements IPresentation {

	private Storyline storyline;

	/**
	 * Create the application window,
	 */
	public TemplateEditor() {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();
	}

	static public void run(Storyline storyline) {
		try {
			TemplateEditor window = new TemplateEditor();
			window.initialize(storyline, null);
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		{
			Composite leftFrame = new Composite(container, SWT.NONE);
			leftFrame.setLayout(new GridLayout(1, false));
			GridData gd_leftFrame = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
			gd_leftFrame.widthHint = 600;
			gd_leftFrame.minimumWidth = 680;
			leftFrame.setLayoutData(gd_leftFrame);
			{
				Composite picBar = new Composite(leftFrame, SWT.NONE);
				picBar.setBackground(SWTResourceManager.getColor(128, 128, 128));
				picBar.setLayout(new GridLayout(1, false));
				GridData gd_picBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
				gd_picBar.heightHint = 33;
				picBar.setLayoutData(gd_picBar);
				picBar.setBounds(0, 0, 64, 64);
				
				CLabel lblNewLabel_1 = new CLabel(picBar, SWT.NONE);
				lblNewLabel_1.setText("New Label");
			}
			{
				Composite image = new Composite(leftFrame, SWT.NONE);
				GridData gd_image = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
				gd_image.minimumWidth = 600;
				gd_image.widthHint = 680;
				gd_image.heightHint = 540;
				image.setLayoutData(gd_image);
				image.setBounds(0, 0, 64, 64);
				
				Label spacer = new Label(image, SWT.NONE);
				spacer.setBackground(SWTResourceManager.getColor(255, 255, 255));
				spacer.setBounds(0, 0, 590, 17);
				
				Label lblNewLabel = new Label(image, SWT.BORDER | SWT.CENTER);
				lblNewLabel.setBackground(SWTResourceManager.getColor(255, 255, 255));
				lblNewLabel.setBounds(0, 16, 590, 489);
				lblNewLabel.setText("New Label");
			}
			{
				Composite composite = new Composite(leftFrame, SWT.NONE);
				composite.setBackground(SWTResourceManager.getColor(128, 128, 128));
				GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
				gd_composite.heightHint = 34;
				composite.setLayoutData(gd_composite);
				composite.setBounds(0, 0, 64, 64);
			}
		}
		
		Composite rightFrame = new Composite(container, SWT.NONE);
		rightFrame.setLayout(new GridLayout(1, false));
		rightFrame.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_1 = new Composite(rightFrame, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_1.heightHint = 28;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setBounds(0, 0, 64, 64);
		
		Composite composite_2 = new Composite(rightFrame, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		composite_2.setBounds(0, 0, 325, 64);
		
		Composite composite_3 = new Composite(rightFrame, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_3.widthHint = 68;
		gd_composite_3.heightHint = 30;
		composite_3.setLayoutData(gd_composite_3);
		composite_3.setBounds(0, 0, 325, 64);
		
		Composite composite_4 = new Composite(rightFrame, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		composite_4.setBounds(0, 0, 325, 64);
		
		Composite composite_5 = new Composite(rightFrame, SWT.NONE);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_5.heightHint = 31;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setBounds(0, 0, 325, 64);
		
		Composite composite_6 = new Composite(rightFrame, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		composite_6.setBounds(0, 0, 325, 64);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the coolbar manager.
	 * @return the coolbar manager
	 */
	@Override
	protected CoolBarManager createCoolBarManager(int style) {
		CoolBarManager coolBarManager = new CoolBarManager(style);
		
		ToolBarManager toolBarManager = new ToolBarManager();
		coolBarManager.add(toolBarManager);
		return coolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(966, 802);
	}

	@Override
	public void render() throws ThinklabException {
		try {
			this.setBlockOnOpen(true);
			this.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			throw new ThinklabException(e);
		}
	}

	@Override
	public void render(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(Storyline storyline, Properties properties) {
		this.storyline = storyline;
	}
}
