package rfplugin.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import rfplugin.Activator;
import rfplugin.model.Group;
import rfplugin.model.Test;

class TableLabelProvider implements ITableLabelProvider {
	private TreeViewer treeViewer;
	private Group group;
	
	public TableLabelProvider(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (element instanceof Test) {
				Image img;
				Test t = (Test) element;
				String status = t.getStatus();

				if (status.equals("Passed")) {
					img = Activator.getImageDescriptor("icons/pass.gif")
							.createImage();
					return img;
				}

				if (status.equals("Failed")) {
					img = Activator.getImageDescriptor("icons/fail.gif")
							.createImage();
					return img;
				}

				if (status.equals("Actual run")) {
					img = Activator.getImageDescriptor("icons/actualRun.gif")
							.createImage();
					return img;
				}

				img = Activator.getImageDescriptor("icons/notrun.gif")
						.createImage();
				return img;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (element instanceof Test)
				return ((Test) element).getTestName();
			if (element instanceof Group){
				this.group = (Group) element;
				return String.format("%s (%d of %d)", element.toString(),
						getGroupSize((Group) element), getAllTests());
			}

		case 1:
			if (element instanceof Test) {

				Test t = (Test) element;
				if (t.getStatus() == "Passed" || t.getStatus() == "Failed") {
					long time = (t.getEndTime() - t.getStartTime()) / 1000;
					return Long.toString(time) + " s";
				}
				return "-";
			}
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	private int getGroupSize(Group groupName) {
		return groupName.getTests().size();
	}

	private int getAllTests() {
		int count = 0;
		TreeItem[] items = treeViewer.getTree().getItems();
	
		for(TreeItem item : items)
	    {
			count += item.getItemCount();
	    }
		return count;
	}
}