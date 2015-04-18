package rfplugin.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import rfplugin.Activator;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.osgi.service.prefs.Preferences;

public class SampleView extends ViewPart {

	public static final String ID = "rfplugin.views.SampleView";
	private Action runTestAction;
	private Action stopTestAction;
	private Action refreshAction;

	String projectPath = null;
	String pybotPath = null;
	Text filterText = null;
	Filter filter = new Filter();
	TreeContentProvider treeContentProvider;
	private TreeViewer treeViewer;

	public void createPartControl(Composite parent) {
		GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(parentData);

		Display display = Display.getCurrent();
		parent.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		Composite searchMenu = new Composite(parent, SWT.BORDER);
		searchMenu.setLayout(new GridLayout(2, false));
		searchMenu.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label searchLabel = new Label(searchMenu, SWT.LEFT);
		searchLabel.setLayoutData(new GridData(SWT.FILL, GridData.CENTER,
				false, false));
		searchLabel.setText("Search: ");

		filterText = new Text(searchMenu, SWT.FILL);
		filterText.setLayoutData(new GridData(SWT.FILL, GridData.CENTER, true,
				false));

		Composite tableViewerComposite = new Composite(parent, SWT.NONE);
		tableViewerComposite.setLayout(new GridLayout(1, true));
		tableViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));

		Tree tree = new Tree(tableViewerComposite, SWT.FILL | SWT.H_SCROLL
				| SWT.V_SCROLL);

		treeViewer = new TreeViewer(tree);
		treeViewer.setData("GroupType", GroupType.STATUS);
		treeViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		TreeColumn column2 = new TreeColumn(tree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setWidth(50);
		
		treeContentProvider = new TreeContentProvider(treeViewer);
		treeViewer.setContentProvider(treeContentProvider);
		treeViewer.setLabelProvider(new TableLabelProvider(treeViewer));
		treeViewer.setInput(getViewSite());
		treeViewer.addFilter(filter);
		treeViewer.setSorter(new Sorter());
		treeViewer.expandAll();
		
		makeActions();
		setDoubleClickAction();
		createToolbar();
		setSearchListener();

		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int parentWidth = parent.getSize().x;
				int scrollWidth = 42;
				column1.setWidth(parentWidth - column2.getWidth() - scrollWidth);
			}
		});
	}

	private void setSearchListener() {
		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filter.setSeatchTestName(filterText.getText());
				treeViewer.refresh();
				treeViewer.expandAll();
			}
		});
	}

	private void createToolbar() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		((RunTestAction) runTestAction).setStopTestAction(stopTestAction);
        ((StopTestAction) stopTestAction).setRunTestAction(runTestAction);
		
		manager.add(runTestAction);
		manager.add(stopTestAction);
		manager.add(refreshAction);
		manager.add(new SettingsAction(treeViewer));

		Action act = new Action("Group", SWT.DROP_DOWN) {
		};
		act.setMenuCreator(new MenuCreator(treeViewer));
		manager.add(act);
	}

	private void makeActions() {
		runTestAction = new RunTestAction(treeViewer, treeContentProvider);
		stopTestAction = new StopTestAction(treeViewer);
		
		refreshAction = new Action() {
			public void run() {
				treeContentProvider.clearTests();
				treeViewer.refresh();
			}
		};
		refreshAction.setText("Refresh");
	}

	private void setDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				runTestAction.run();
			}
		});
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
}