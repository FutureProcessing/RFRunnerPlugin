package rfplugin.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class SampleView extends ViewPart {
	public static final String ID = "rfplugin.views.SampleView";
	private Action runTestAction;
	private Action stopTestAction;
	private TreeViewer treeViewer;
	private TreeContentProvider treeContentProvider;
	private Text filterText = null;
	private Filter filter = new Filter();
	
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
		manager.add(new RefreshAction(treeViewer, treeContentProvider));
		manager.add(new SettingsAction(treeViewer));
		manager.add(new GroupAction(treeViewer));
	}

	private void makeActions() {
		runTestAction = new RunTestAction(treeViewer, treeContentProvider);
		stopTestAction = new StopTestAction(treeViewer);
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