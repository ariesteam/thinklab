package org.integratedmodelling.thinklab.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.http.ThinkWeb;
import org.integratedmodelling.thinklab.http.ThinklabHttpdPlugin;
import org.integratedmodelling.thinklab.http.ThinklabWebPlugin;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualConcept;
import org.integratedmodelling.thinklab.webapp.view.components.Ribbon;
import org.integratedmodelling.thinklab.webapp.view.components.Sidebar;
import org.integratedmodelling.thinklab.webapp.view.components.StatusBar;
import org.integratedmodelling.thinklab.webapp.view.components.ThinkcapComponent;
import org.integratedmodelling.thinklab.webapp.view.components.ToggleComponent;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Path;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.api.InputElement;

/**
 * Simplify API creation of ZK components. Ugly class, but with this one, who 
 * needs ZUL and the silly lang-addon.xml.
 * 
 * @author Ferdinando Villa
 *
 */
public class ZK {

	/*
	 * this is a configured prefix for relative <prefix>/* URLs, to allow
	 * swapping a prefix to access proxied resources. Bound to <prefix>.url.prefix
	 * configuration variables.
	 */
	static HashMap<String,String> _up = null;

	static public interface RowRenderer {
		public void render(Object content, Row row);
	}
	
	static public class ZKComponent {
		
		protected HtmlBasedComponent c = null;
		
		public ZKComponent(HtmlBasedComponent c) {
			this.c = c;
		}
		
		private void addStyle(String s) {
			
			String style = c.getStyle();
			if (style == null)
				style = "";
			else if (!style.trim().equals(""))
				style += "; ";
			style += s;
			c.setStyle(style);
		}
		
		private void removeStyle(String s) {
			
			String style = c.getStyle();
			if (style != null && !style.trim().equals(""))
				style.replaceAll(s, "");
			c.setStyle(style);
		}
		
		public HtmlBasedComponent get() {
			return c;
		}
		
		public ZKComponent style(String s) {
			if (c != null) c.setStyle(s);
			return this;
		}
		
		public ZKComponent listener(String event, EventListener listener) {
			if (c != null && listener != null)
				c.addEventListener(event, listener);
			return this;
		}
		
		public ZKComponent enable(boolean s) {
			if (c != null) {
				if (c instanceof InputElement) {
					((InputElement)c).setDisabled(!s);
				} else if (c instanceof Button) {
					((Button)c).setDisabled(!s);
				} else if (c instanceof Toolbarbutton) {
					((Toolbarbutton)c).setDisabled(!s);
				}
			}
			return this;
		}
		
		public ZKComponent sclass(String s) {
			if (c != null && s != null) c.setSclass(s);
			return this;
		}
		
		public ZKComponent fillx() {
			if (c != null) c.setWidth("100%");
			return this;
		}
		
		public ZKComponent filly() {
			if (c != null) c.setHeight("100%");
			return this;
		}
		
		public ZKComponent fill() {
			if (c != null) {
				c.setWidth("100%");
				c.setHeight("100%");
			}
			return this;
		}

		public ZKComponent margins(int x) {
			if (c != null)
				addStyle("margin: " + x + "px");
			return this;
		}
		
		public ZKComponent lmargin(int x) {
			if (c != null)
				addStyle("margin-left: " + x + "px");
			return this;
		}
		
		public ZKComponent rmargin(int x) {
			if (c != null)
				addStyle("margin-right: " + x + "px");
			return this;
		}

		public ZKComponent tmargin(int x) {
			if (c != null)
				addStyle("margin-top: " + x + "px");
			return this;
		}

		public ZKComponent bmargin(int x) {
			if (c != null)
				addStyle("margin-bottom: " + x + "px");
			return this;
		}
		
		public ZKComponent border() {
			if (c != null)
				addStyle("border: 1px");
			return this;
		}
		
		public ZKComponent tborder() {
			if (c != null)
				addStyle("border-top: 1px");
			return this;
		}
		
		public ZKComponent bborder() {
			if (c != null)
				addStyle("border-bottom: 1px");
			return this;
		}
		
		public ZKComponent lborder() {
			if (c != null)
				addStyle("border-left: 1px");
			return this;
		}
		
		public ZKComponent rborder() {
			if (c != null)
				addStyle("border-right: 1px");
			return this;
		}
		
		public ZKComponent width(String s) {
			if (c != null) c.setWidth(s);
			return this;
		}
		
		public ZKComponent width(int s) {
			if (c != null) c.setWidth(s+"px");
			return this;
		}
		
		public ZKComponent height(String s) {
			if (c != null) c.setHeight(s);
			return this;
		}
		
		public ZKComponent height(int s) {
			if (c != null) c.setHeight(s+ "px");
			return this;
		}
		
		public ZKComponent hide() {
			if (c != null) c.setVisible(false);
			return this;
		}
		
		public ZKComponent show() {
			if (c != null) c.setVisible(true);
			return this;
		}
		
		public ZKComponent id(String s) {
			if (c != null) c.setId(s);
			return this;
		}
		
		public ZKComponent tooltip(String s) {
			if (c != null) c.setTooltiptext(s);
			return this;
		}
		
		public ZKComponent mold(String s) {
			if (c != null) c.setMold(s);
			return this;
		}
		
		public ZKComponent zclass(String s) {
			if (c != null) c.setZclass(s);
			return this;
		}
		
		/**
		 * turn the cursor into a pointer when hovering over
		 * @param s
		 * @return
		 */
		public ZKComponent hand() {
			if (c != null) addStyle("cursor: pointer");
			return this;
		}
		
		/**
		 * turn the cursor into a pointer if the passed bool is true. Looks silly but
		 * it's useful to make this conditional in the functional style component definition.
		 * 
		 * @param doit
		 * @return
		 */
		public ZKComponent hand(boolean doit) {
			if (c != null && doit) addStyle("cursor: pointer");
			return this;
		}
		
		public ZKComponent align(String s) {
			
			if (c != null) {
				if (c instanceof Div)
					((Div)c).setAlign(s);
				else {
					// wrap it in a div and set alignment
					Div div = new Div();
					div.appendChild(c);
					div.setAlign(s);
					c = div;
				}
			}
			
			return this;
		}
		
		public ZKComponent lalign() {
			return align("left");
		}
		
		public ZKComponent ralign() {
			return align("rigth");
		}
		
		public ZKComponent calign() {
			return align("center");
		}
		
		public ZKComponent selected(int n) {
			
			if (c != null) {
				if (c instanceof Listbox) {
					((Listbox)c).setSelectedIndex(n);					
				} else if (c instanceof Ribbon) {
					((Ribbon)c).select(n);
				}
				// TODO other selectables
			}
			return this;
		}
		
		public ZKComponent value(String string) {
			
			if (c != null) {
				if (c instanceof Textbox) {
					((Textbox)c).setValue(string);
				} else if (c instanceof Combobox) {
					((Combobox)c).setValue(string);
				} /* TODO anything else that needs a value */
			}
			return this;
		}
		
		public ZKComponent spacing(int n) {
			if (c != null && c instanceof Box) {
				((Box)c).setSpacing(n + "px");
			} 
			return this;
		}

		public ZKComponent scroll() {
			
			if (c != null && c instanceof Window) {
				((Window)c).setContentStyle("overflow: auto");
			}
			return this;
		}

		public ZKComponent color(String string) {

			if (c != null)
				addStyle("color: " + string + ";");
			return this;
		}
		
		public ZKComponent bgcolor(String string) {

			if (c != null)
				addStyle("background-color: " + string + ";");
			return this;
		}

		public ZKComponent zindex(int i) {
			if (c != null)
				c.setZindex(i);
			return this;
		}
	}

	public static class ToggleComponentP extends ZKComponent {
		protected ToggleComponentP(HtmlBasedComponent c) {
			super(c);
		}
		
		public ToggleComponentP open(boolean b) {
			if (c != null) ((ToggleComponent)c).setOpen(b);
			return this;
		}

		public ZKComponent labelStyle(String style) {
			if (c != null) ((ToggleComponent)c).setLabelStyle(style);
			return this;
		}
	}
	
	public static class StatusbarP extends ZKComponent {
		
		protected StatusbarP(HtmlBasedComponent c) {
			super(c);
		}
		
		public StatusbarP state(String b) {
			if (c != null) ((StatusBar)c).set(b);
			return this;
		}

		public StatusbarP messagesclass(String style) {
			if (c != null) ((StatusBar)c).setMessageClass(style);
			return this;
		}

		public StatusbarP messagedivsclass(String style) {
			if (c != null) ((StatusBar)c).setMessageDivClass(style);
			return this;
		}
	}
	
	public static class TreeComponent extends ZKComponent {

		protected TreeComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public TreeComponent vflex() {
			if (c != null) ((Tree)c).setVflex(true);
			return this;
		}
		
		public TreeComponent checkbox() {
			if (c != null) {
				((Tree)c).setCheckmark(true);
				((Tree)c).setMultiple(true);
			}
			return this;
		}

		public TreeComponent checkradio() {
			if (c != null) {
				((Tree)c).setCheckmark(true);
				((Tree)c).setMultiple(false);
			}
			return this;
		}

		public TreeComponent model(TreeModel layerTree) {
			if (c != null) {
				((Tree)c).setModel(layerTree);
			}
			return this;
		}
		
		public TreeComponent renderer(TreeitemRenderer renderer) {
			if (c != null) {
				((Tree)c).setTreeitemRenderer(renderer);
			}
			return this;
		}
	}
	
	public static class SidebarComponent extends ZKComponent {

		protected SidebarComponent(HtmlBasedComponent c) {
			super(c);
		}
		
		public SidebarComponent headersclass(String style) {
			if (c != null && style != null && !style.isEmpty())
				((Sidebar)c).setHeaderSclass(style);
			return this;
		}

		public SidebarComponent labelsclass(String styleEnabled, String styleDisabled) {
			if (c != null && styleEnabled != null && !styleEnabled.isEmpty())
				((Sidebar)c).setLabelSclass(styleEnabled, styleDisabled);
			return this;
		}
		
		public SidebarComponent headersize(int n) {
			if (c != null )
				((Sidebar)c).setHeaderSize(n);
			return this;
		}
	}
	
	public static class TreeNodeComponent extends ZKComponent {

		protected TreeNodeComponent(HtmlBasedComponent c) {
			super(c);
		}
	
	}
	
	public static class TextBoxComponent extends ZKComponent {

		protected TextBoxComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public TextBoxComponent listener(EventListener listener) {
			if (c != null && listener != null) {
				c.addEventListener("onOK", listener);
				c.addEventListener("onChange", listener);
			}
			return this;
		}
	}
	
	public static class ComboComponent extends ZKComponent {

		protected ComboComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public ComboComponent listmodel(ListModel model) {
			if (c != null && model != null) {
				((Combobox)c).setAutodrop(true);
				((Combobox)c).setModel(model);
			}
			return this;
		}
		
		public ComboComponent listener(EventListener listener) {
			if (c != null && listener != null) {
				c.addEventListener("onOK", listener);
				c.addEventListener("onChange", listener);
			}
			return this;
		}
	}
	
	public static class BoxComponent extends ZKComponent {

		protected BoxComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public BoxComponent widths(String w) {
			if (c != null) ((Box)c).setWidths(w);
			return this;
		}
		
		public BoxComponent valign(String v) {
			if (c != null) ((Box)c).setAlign(v);
			return this;
		}
	}
	
	public static class GridComponent extends ZKComponent {

		protected GridComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public GridComponent paging(int rows) {
			if (c != null) {
				((Grid)c).setMold("paging");
				((Grid)c).setPageSize(rows);
			}
			return this;
		}
		
		public GridComponent fixedLayout() {
			if (c != null) {
				((Grid)c).setFixedLayout(true);
			}
			return this;
		}
	}
	
	public static class ListboxComponent extends ZKComponent {

		protected ListboxComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public ListboxComponent multiple(boolean b) {
			if (c != null) ((Listbox)c).setMultiple(true);
			return this;
		}
		
		public ListboxComponent checkmark(boolean b) {
			if (c != null) ((Listbox)c).setCheckmark(true);
			return this;
		}
		
		public ListboxComponent fixedLayout(boolean b) {
			if (c != null) ((Listbox)c).setFixedLayout(true);
			return this;
		}
		
		public ListboxComponent nrows(int pgsz) {
			if (c != null) 
				((Listbox)c).setRows(pgsz);
			return this;
		}
		
		public ListboxComponent selected(int n) {
			if (c != null)
				((Listbox)c).setSelectedIndex(n);
			return this;
		}
	}
	
	public static class SplitterComponent extends ZKComponent {

		protected SplitterComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public SplitterComponent collapse(String collapse) {
			if (c != null) ((Splitter)c).setCollapse(collapse);
			return this;
		}
	}
	
	public static class ItemComponent extends ZKComponent {

		protected ItemComponent(HtmlBasedComponent c) {
			super(c);
		}
	
		public ItemComponent selected(boolean on) {
			if (c != null) ((Listitem)c).setSelected(on);
			return this;
		}
		
		public ItemComponent select() {
			if (c != null) ((Listitem)c).setSelected(true);
			return this;
		}
		
		public ItemComponent unselect() {
			if (c != null) ((Listitem)c).setSelected(false);
			return this;
		}
	}
	/**
	 * Make a grid with the passed content. Passed parameters should only be
	 * the result of column(), columns(), row(), or rows(). Constraints about the
	 * parameters to be passed should be obvious to the not-entirely-idiotic.
	 * 
	 * @param comps
	 * @return
	 */
	public static GridComponent grid(ZKComponent ... comps) {
		
		Grid ret = new Grid();
		Columns columns = null;
		Rows rows = null;
		
		for (ZKComponent c : comps) {
			
			if (c.get() instanceof Rows)
				rows = (Rows) c.get();
			else if (c.get() instanceof Columns)
				columns = (Columns)c.get();
			else if (c.get() instanceof Row || c.get() instanceof Group) {
				if (rows == null)
					rows = new Rows();
				rows.appendChild(c.get());
			} else if (c.get() instanceof Column) {
				if (columns == null)
					columns = new Columns();
				columns.appendChild(c.get());
			}
		}
		
		if (columns != null)
			ret.appendChild(columns);
		
		if (rows != null)
			ret.appendChild(rows);
		
		return new GridComponent(ret);
	}
	
	/**
	 * A set of grid columns, all the default width
	 * 
	 * @param objs
	 * @return
	 */
	public static ZKComponent columns(String ...objs) {
		
		Columns c = new Columns();
		
		for (String s : objs) {
			c.appendChild(new Column(s));
		}
		
		return new ZKComponent(c);
	}
	
	/**
	 * An individual grid column, so we can specify the width, class etc.
	 * 
	 * @param s
	 * @return
	 */
	public static ZKComponent column(String s) {
		return new ZKComponent(new Column(s));
	}
	
	public static ZKComponent row(ZKComponent ...objs) {

		Row row = new Row();
		for (ZKComponent c : objs)
			if (c != null && c.get() != null)
				row.appendChild(c.get());
		
		return new ZKComponent(row);
	}

	public static ZKComponent group(String title) {
		
		Group b = new Group(title);
		return new ZKComponent(b);
	}

	public static ZKComponent slider() {
		Slider b = new Slider();
		return new ZKComponent(b);
	}
	
	
	public static ToggleComponentP toggle(String s, ZKComponent ... objs) {

		ToggleComponent com = new ToggleComponent(s);
		for (ZKComponent c : objs)
			if (c != null && c.get() != null)
				com.addToggledComponent(c.get());
		
		return new ToggleComponentP(com);
	}

	public static ZKComponent rows(Collection<?> data, RowRenderer renderer) {
		Rows rows = new Rows();
		for (Object d : data) {
			Row row = new Row();
			renderer.render(d, row);
			rows.appendChild(row);
		}
		return new ZKComponent(rows);
	}
	
	/**
	 * Make an accordion tabbox. Alternate String and ZKComponent parameters, for title and
	 * content of each tab.
	 * 
	 * @param objs
	 * @return
	 */
	static public ZKComponent accordion(Object ... objs) {
		
		Tabbox tbox = new Tabbox();
		
		tbox.setMold("accordion-lite");
		
		Tabs tabs = new Tabs();
		Tabpanels tabp = new Tabpanels();
		
		for (int i = 0; i < objs.length; i++) {
			
			String s = (String) objs[i++];
			ZKComponent z = (ZKComponent) objs[i];
			
			Tab tab = new Tab(s);
			tabs.appendChild(tab);
			
			Tabpanel tap = new Tabpanel();
			tap.appendChild(z.get());
			tabp.appendChild(tap);

		}
		
		tbox.appendChild(tabs);
		tbox.appendChild(tabp);
		
		return new ZKComponent(tbox);
	}
	
	/**
	 * Make a tabbox. Alternate String and ZKComponent parameters, for title and
	 * content of each tab.
	 * 
	 * @param objs
	 * @return
	 */
	static public ZKComponent tabbox(Object ... objs) {
		
		Tabbox tbox = new Tabbox();
		
		Tabs tabs = new Tabs();
		Tabpanels tabp = new Tabpanels();
		
		for (int i = 0; i < objs.length; i++) {
			
			String s = (String) objs[i++];
			ZKComponent z = (ZKComponent) objs[i];
			
			Tab tab = new Tab(s);
			tabs.appendChild(tab);
			
			Tabpanel tap = new Tabpanel();
			tap.appendChild(z.get());
			tabp.appendChild(tap);
		}
		
		tbox.appendChild(tabs);
		tbox.appendChild(tabp);
		
		return new ZKComponent(tbox);
	}
	
	/**
	 * make a tree for the passed graph, ignoring the root element; use the
	 * concept ID as the ID of each treeitem, and the label + image for the
	 * treecell. Will become more flexible later.
	 * 
	 * @param graph
	 * @return
	 */
	static public TreeComponent tree(TreeNodeComponent ... nodes) {
		
		Tree tree = new Tree();

		// TODO
		
		return new TreeComponent(tree);
	}
	
	static public TreeNodeComponent node(Object content) {
		
		Treeitem node = new Treeitem();
		
		// TODO
		
		return new TreeNodeComponent(node);
	}

	static public TreeNodeComponent node(Object content, TreeNodeComponent ... nodes) {
		
		Treeitem node = new Treeitem();
		
		// TODO
		
		return new TreeNodeComponent(node);
	}
	
	static public TreeNodeComponent node(String label, Object content) {
		
		Treeitem node = new Treeitem();
		
		// TODO
		
		return new TreeNodeComponent(node);
	}

	static public TreeNodeComponent node(String label, Object content, TreeNodeComponent ... nodes) {
		
		Treeitem node = new Treeitem();
		
		// TODO
		
		return new TreeNodeComponent(node);
	}
	
	/**
	 * make a tree for the passed graph, ignoring the root element; use the
	 * concept ID as the ID of each treeitem, and the label + image for the
	 * treecell. Will become more flexible later.
	 * 
	 * @param graph
	 * @return
	 */
	static public TreeComponent ktree(IConcept ... root) {
		
		Tree tree = new Tree();
		tree.appendChild(makeKTreeChildren(null, root));
		
		return new TreeComponent(tree);
	}
	
	private static Treechildren makeKTreeChildren(Set<IConcept> refs, IConcept ... root) {
	
		// treechildren (treeitem  (treerow (treecell*)) treechildren) 
		Treechildren children = new Treechildren();
		
		for (IConcept c : root) {
			
			Treeitem item = makeKTreeItem(c, refs);
			if (item != null) {
				item.setOpen(false);
				children.appendChild(item);
			}
		}
		
		return children;
	}

	private static Treeitem makeKTreeItem(IConcept c, Set<IConcept> refs) {
		
		VisualConcept vc = TypeManager.get().getVisualConcept(c);
		
		if (refs == null)
			refs = new HashSet<IConcept>();
		
		if (refs.contains(c))
			return null;
		
		refs.add(c);
		
		Treeitem item = new Treeitem();
		item.setId(c.toString());
		item.setTooltiptext("Concept " + vc.getName() + " in ontology " + vc.getConceptSpace());
		Treerow row = new Treerow();
		Treecell cell = new Treecell();
		
		cell.setLabel(vc.getName());
		cell.setImage(vc.getTypeIcon(null));
		
		Collection<IConcept> childr = c.getChildren();
		if (childr.size() > 0) {
			item.appendChild(
					makeKTreeChildren(refs, childr.toArray(new IConcept[childr.size()])));
		}
		
		row.appendChild(cell);
		item.appendChild(row);
		
		return item;
	}

	static public ZKComponent c(HtmlBasedComponent cc) {
		return new ZKComponent(cc);
	}
	
	static public BoxComponent hbox() {
		return new BoxComponent(new Hbox());
	}
	
	static public StatusbarP statusbar(Hashtable<String, Pair<String, String>> codes) {
		StatusBar bar = new StatusBar(codes);
		return new StatusbarP(bar);
	}

	static public StatusbarP statusbar(
			Hashtable<String, Pair<String, String>> codes,
			ZKComponent menu) {
		StatusBar bar = new StatusBar(codes);
		bar.setMenu(menu);
		return new StatusbarP(bar);
	}
	
	static public BoxComponent hbox(ZKComponent ... comps) {
		
		Component[] cc = new Component[comps == null ? 0 : countNotNulls(comps)];
		int i = 0;
		if (comps != null) {
			for (ZKComponent c : comps)
				if (c != null && c.get() != null) 
					cc[i++] = c.get();
		}
		return new BoxComponent(new Hbox(cc));
	}
	
	private static int countNotNulls(ZKComponent[] comps) {
		int cnt = 0;
		for (ZKComponent c : comps)
			if (c != null && c.get() != null)
				cnt++;
		return cnt;
	}

	static public BoxComponent vbox() {
		return new BoxComponent(new Vbox());
	}
	
	static public BoxComponent vbox(ZKComponent ... comps) {
		
		Component[] cc = new Component[countNotNulls(comps)];
		int i = 0;
		for (ZKComponent c : comps)
			if (c != null && c.get() != null)
				cc[i++] = c.get();
		
		Vbox w = new Vbox(cc);

		return new BoxComponent(w);
	}
	
	static public ZKComponent window() {

		Window w = new Window();

		return new ZKComponent(w);
	}
	
	static public ZKComponent div() {

		Div w = new Div();


		return new ZKComponent(w);
	}
	
	static public ZKComponent window(ZKComponent ... comps) {

		Window w = new Window();

		for (ZKComponent c : comps)
			if (c != null && c.get() != null) w.appendChild(c.get());
		return new ZKComponent(w);
	}
	
	static public ZKComponent div(ZKComponent ... comps) {

		Div w = new Div();
		for (ZKComponent c : comps)
			if (c != null && c.get() != null) w.appendChild(c.get());
		return new ZKComponent(w);
	}
	
	static public ZKComponent label(String label) {
		return new ZKComponent(new Label(label));
	}
	
	static public ZKComponent checkbox(boolean checked) {
		Checkbox chk = new Checkbox();
		chk.setChecked(checked);
		return new ZKComponent(chk);
	}
	
	static public ZKComponent image(String image) {
		return new ZKComponent(new Image(fixUrl(image)));
	}
	
	static public ZKComponent separator(boolean bar) {
		Separator s = new Separator();
		s.setBar(bar);
		return new ZKComponent(s);
	}

	static public ZKComponent separator() {
		Separator s = new Separator();
		s.setBar(false);
		return new ZKComponent(s);
	}

	static public ZKComponent bar() {
		Separator s = new Separator();
		s.setBar(true);
		return new ZKComponent(s);
	}
	
	static public ZKComponent spacer() {
		Separator s = new Separator();
		s.setBar(false);
		return new ZKComponent(s);
	}
	
	static public ZKComponent ribbon(int width, int height, int itemWidth, ZKComponent ... components) {
		Ribbon s = new Ribbon(width, height, itemWidth, components);
		return new ZKComponent(s);
	}
	
	static public ZKComponent spacer(int n) {
		Separator s = new Separator();
		s.setBar(false);
		s.setWidth(n+"px");
		s.setHeight(n+"px");
		return new ZKComponent(s);
	} 
	
	static public ZKComponent hspacer(int n) {
		Separator s = new Separator();
		s.setBar(false);
		s.setWidth(n+"px");
		return new ZKComponent(s);
	}
	
	static public ZKComponent vspacer(int n) {
		Separator s = new Separator();
		s.setBar(false);
		s.setHeight(n+"px");
		return new ZKComponent(s);
	}
	
	static public ZKComponent linkbutton(String label) {
		Toolbarbutton b = new Toolbarbutton();
		b.setLabel(label);
		return new ZKComponent(b);
	}

	static public ZKComponent linkbutton(String label, String link) {
		
		if (label == null)
			return new ZKComponent(null);
		
		Toolbarbutton b = new Toolbarbutton();
		b.setLabel(label);
		b.setHref(link);
		return new ZKComponent(b);
	}

	static public ZKComponent linkbutton(String label, String link, String target) {
		
		if (label == null)
			return new ZKComponent(null);
		
		Toolbarbutton b = new Toolbarbutton();
		b.setLabel(label);
		b.setHref(link);
		b.setTarget(target);
		return new ZKComponent(b);
	}

	static public ZKComponent imagebutton(String image, String link) {

		if (image == null)
			return new ZKComponent(null);
		
		Toolbarbutton b = new Toolbarbutton();
		b.setImage(fixUrl(image));
		b.setHref(link);
		return new ZKComponent(b);
	}

	static public ZKComponent imagebutton(String image, String link, String target) {

		if (image == null)
			return new ZKComponent(null);
		
		Toolbarbutton b = new Toolbarbutton();
		b.setImage(fixUrl(image));
		b.setHref(link);
		b.setTarget(target);
		return new ZKComponent(b);
	}
	
	static public ZKComponent imagebutton(String image) {
		
		if (image == null)
			return new ZKComponent(null);

		Toolbarbutton b = new Toolbarbutton();
		b.setImage(fixUrl(image));
		return new ZKComponent(b);
	}

	public static String fixUrl(String url) {
		
		if (url == null)
			return null;
		
		if (_up == null) {
			
			_up = new HashMap<String, String>();
			
			Properties prop = ThinklabHttpdPlugin.get().getProperties();
			
			for (Object p : prop.keySet()) {
				if (p.toString().endsWith(".url.prefix")) {
					String prefix = Path.getFirst(p.toString(), ".");
					_up.put(prefix, prop.getProperty(p.toString()));
				}
			}
		}
		
		for (String prf : _up.keySet()) {
			if (url.startsWith("/" + prf)) {
				url = "/" + _up.get(prf) + url.substring(prf.length() + 1);
				break;
			}			
		}
		
		return url;
	}

	static public ComboComponent combobox(String ... itemlabels) {
		Combobox b = new Combobox();
		for (String s : itemlabels) {
			b.appendChild(new Comboitem(s));
		}
		return new ComboComponent(b);
	}
	
	static public ZKComponent intbox() {
		return new ZKComponent(new Intbox());
	}

	static public TextBoxComponent textbox() {
		return new TextBoxComponent(new Textbox());
	}

	static public ZKComponent textbox(String initialValue) {
		Textbox t = new Textbox();
		t.setValue(initialValue);
		return new ZKComponent(t);
	}

	static public ZKComponent datebox() {
		return new ZKComponent(new Datebox());
	}

	public static ZKComponent button(String label) {
		Button b = new Button(label);
		return new ZKComponent(b);
	}

	public static void setupEventListeners(Component comp, final Object listener) {

	       Method[] methods = listener.getClass().getDeclaredMethods();
	        for (final Method m : methods) {
	            String methodName = m.getName();
	            int dollarIndex = methodName.indexOf("$");
	            if (dollarIndex > 0) {
	                Class<?> params[] = m.getParameterTypes();
	                if (params.length == 1 && params[0].isAssignableFrom(Event.class)) {
	                    String compId = methodName.substring(dollarIndex + 1);
	                    Component fellow = ZK.getComponentById(comp, compId);
	                    if (fellow != null) {
	                    	
	                        String eventName = methodName.substring(0, dollarIndex);
	                        fellow.addEventListener(eventName, new EventListener() {

	                            public void onEvent(Event event) throws Exception {
	                                m.invoke(listener, event);
	                            }
	                        });
	                    }
	                }
	            }
	        }
	}
	
	public static void wireFieldsToComponents(Component comp) {

	       Field[] fields = comp.getClass().getDeclaredFields();
	        for (final Field f : fields) {
	            String fieldName = f.getName();

	            Component fellow = ZK.getComponentById(comp, fieldName);
	            if (fellow != null) {
	            	try {
	            		f.setAccessible(true);
	            		f.set(comp, fellow);
					} catch (Exception e) {
						throw new ThinklabRuntimeException(e);
					}
	            }
	        }
	}

	public static ZKComponent groupbox(ZKComponent ... comps) {
		Groupbox b = new Groupbox();
		for (ZKComponent c : comps)
			if (c.get() != null) b.appendChild(c.get());
		return new ZKComponent(b);
	}

	public static ZKComponent groupbox(String title, ZKComponent ... comps) {
		
		Groupbox b = new Groupbox();

		Caption c = new Caption();
		c.setLabel(title);
		b.appendChild(c);

		if (comps != null)
			for (ZKComponent cc : comps)
				if (cc != null && cc.get() != null) 
					b.appendChild(cc.get());
		
		return new ZKComponent(b);
	}
	
	public static SplitterComponent splitter() {
		
		Splitter splitter = new Splitter();
		return new SplitterComponent(splitter);
	}

	public static ZKComponent text(String string) {
		
		Div div = new Div();
		Text text = new Text(string);
		div.appendChild(text);
		return new ZKComponent(div);
	}

	public static ListboxComponent listbox(ZKComponent ... comps) {
		Listbox lb = new Listbox();
		
		for (ZKComponent cc : comps)
			if (cc.get() != null) lb.appendChild(cc.get());
		
		return new ListboxComponent(lb);
	}

	public static ZKComponent listheader(String string) {
		Listheader lh = new Listheader(string);
		return new ZKComponent(lh);
	}

	public static ZKComponent listhead(ZKComponent ... comps) {
		
		Listhead b = new Listhead();

		for (ZKComponent cc : comps)
			if (cc.get() != null) b.appendChild(cc.get());
		
		return new ZKComponent(b);
	}

	public static ZKComponent listcell(String label) {
		
		Listcell b = new Listcell(label);
		return new ZKComponent(b);
	}
	
	public static ItemComponent listitem(String label, Object value) {
		
		Listitem b = new Listitem(label, value);
		return new ItemComponent(b);
	}


	public static ItemComponent listitem(ZKComponent ... comps) {
		
		Listitem b = new Listitem();

		for (ZKComponent cc : comps)
			if (cc.get() != null) b.appendChild(cc.get());
		
		return new ItemComponent(b);
	}

	public static SidebarComponent sidebar(int w, int h) {

		Sidebar s = new Sidebar();
		s.setWidth(w+"px");
		s.setMaxHeight(h);
		
		return new SidebarComponent(s);
	}

	/**
	 * Find a component by user ID. Seems absent in ZK.
	 * 
	 * @param root
	 * @param id
	 * @return the component with the specified ID, or null.
	 */
	public static Component getComponentById(Component root, String id) {
	
		Component ret = null;
	
		if (root.getId().equals(id))
			return root;
		
		for (Object o : root.getChildren()) {
			if ( (ret = getComponentById((Component)o, id)) != null)
				break;
		}
		
		return ret;
	}

	/**
	 * Detach all children of a component
	 * 
	 * @param root
	 */
	public static void resetComponent(Component root) {
		
		ArrayList<Component> cp = new ArrayList<Component>();
		
		if (root instanceof ThinkcapComponent)
			((ThinkcapComponent)root).deleteSubcomponents();
		
		for (Object c : root.getChildren()) {
			cp.add((Component)c);
		}
		
		for (Component c : cp)
			c.detach();
	}

	/**
	 * Create a jar containing the passed lang-addon.xml and the contents of any components/
	 * directory, put it in the web server's WEB-INF/lib directory.
	 * @throws ThinkcapException 
	 */
	public static void createComponentJar(ThinklabWebPlugin plugin) throws ThinklabException {
	
		File zklang = new File(plugin.getLoadDirectory() + File.separator + "lang-addon.xml");
		
		if (!zklang.exists())
			return;
		
		plugin.logger().info(
				"publishing ZK resources");
		
		File outfile = new File(
				ThinkWeb.get().getWebSpace() + 
				File.separator + 
				"WEB-INF" +
				File.separator +
				"lib" + 
				File.separator +
				ThinklabWebPlugin.getPluginName(plugin.getDescriptor().getId()) +
				"z.jar");
		
		FileOutputStream fos = null;
		JarOutputStream jos = null;
		
		try {
			fos = new FileOutputStream(outfile);
		
			// stupid manifest		
			Manifest manifest = new Manifest();
			Attributes manifestAttr = manifest.getMainAttributes();
			manifestAttr.putValue("Manifest-Version", "1.0");
		
			jos = new JarOutputStream(fos, manifest);
			
			jos.putNextEntry(new JarEntry("metainfo/"));
			jos.putNextEntry(new JarEntry("metainfo/zk/"));
	
			FileInputStream in = new FileInputStream(zklang);
			byte[] buf = new byte[4096];
			
			jos.putNextEntry(new JarEntry("metainfo/zk/lang-addon.xml"));      
	
			int len;
			while ((len = in.read(buf)) > 0) {
			   jos.write(buf, 0, len);
			}
	
			jos.closeEntry();
			in.close();
			
			File zkcomp = 
				new File(plugin.getLoadDirectory() + File.separator + "components");
	
			if (zkcomp.exists() && zkcomp.isDirectory()) {
				
				jos.putNextEntry(new JarEntry("components/"));
				
				for (File cmp :  zkcomp.listFiles()) {
	
					/*
					 * TODO check if we want to include something other than 
					 * .zul files.
					 */
					if (!cmp.isDirectory() && !cmp.isHidden() && cmp.toString().endsWith(".zul")) {
						
						String bname = MiscUtilities.getFileName(cmp.toString());
						jos.putNextEntry(new JarEntry("component/" + bname));
	
						in = new FileInputStream(cmp);
						while ((len = in.read(buf)) > 0) {
						   jos.write(buf, 0, len);
						}
	
						jos.closeEntry();
						in.close();
					}
				}
			}
			
			jos.close();
			
		} catch (Exception e) {
			throw new ThinklabException(e);
		} finally {
	
		}
	}

}
