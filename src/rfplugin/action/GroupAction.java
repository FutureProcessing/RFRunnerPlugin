package rfplugin.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import rfplugin.views.MenuCreator;

public class GroupAction extends Action {
	TreeViewer treeViewer;
	
	public GroupAction(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		this.setText("Group");
		this.setMenuCreator(new MenuCreator(treeViewer));
	}
}
