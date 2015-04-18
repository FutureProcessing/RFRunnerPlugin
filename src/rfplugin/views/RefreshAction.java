package rfplugin.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class RefreshAction extends Action {
	TreeViewer treeViewer;
	TreeContentProvider treeContentProvider;
	
	public RefreshAction(TreeViewer treeViewer, TreeContentProvider treeContentProvider){
		this.treeViewer = treeViewer;
		this.treeContentProvider = treeContentProvider;
		this.setText("Refresh");
	}
	
	public void run() {
		treeContentProvider.clearTests();
		treeViewer.refresh();
	}
}
