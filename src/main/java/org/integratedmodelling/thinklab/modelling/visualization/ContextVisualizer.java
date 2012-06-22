package org.integratedmodelling.thinklab.modelling.visualization;

import java.util.ArrayList;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

public class ContextVisualizer extends ApplicationWindow {

	/*
	 * the object shown after show() has been called.
	 */
	ContextVisualizer _window;
	
	/**
	 * The contexts we're visualizing
	 */
	ArrayList<IContext> _contexts = new ArrayList<IContext>();
	private Table table;
	
	/**
	 * Create the application window.
	 */
	public ContextVisualizer() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		{
			TreeViewer treeViewer = new TreeViewer(container, SWT.BORDER);
			Tree tree = treeViewer.getTree();
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		}
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		}
		{
			TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
			table = tableViewer.getTable();
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		}

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
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
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
	 * Launch the application.
	 * @param args
	 */
	public void show() {
		try {
			_window = new ContextVisualizer();
			_window.setBlockOnOpen(true);
			_window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Current context");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
