package rfplugin.views;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuCreator implements IMenuCreator {
	TreeViewer treeViewer;
	
	public MenuCreator(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		MenuItem statusItem = new MenuItem(menu, SWT.NONE);
		statusItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeViewer.setData("GroupType", GroupType.STATUS);
				treeViewer.refresh();
				treeViewer.expandAll();
				treeViewer.refresh();
			}
		});
		statusItem.setText("Status");

		MenuItem fileItem = new MenuItem(menu, SWT.NONE);
		fileItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeViewer.setData("GroupType", GroupType.FILE);
				treeViewer.refresh();
				treeViewer.expandAll();
				treeViewer.refresh();
			}
		});
		fileItem.setText("File");
		return menu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
}