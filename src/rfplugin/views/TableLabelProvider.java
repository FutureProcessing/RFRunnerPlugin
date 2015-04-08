package rfplugin.views;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import rfplugin.Activator;

class TableLabelProvider implements ITableLabelProvider {
	Set<GroupTest> groupsTests;

	public TableLabelProvider(Set<GroupTest> groupsTests) {
		this.groupsTests = groupsTests;
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
			if (element instanceof GroupTest)
				return String.format("%s (%d of %d)", element.toString(),
						getGroupSize((GroupTest) element), getAllTests());

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

	private int getGroupSize(GroupTest groupName) {
		for (Iterator<GroupTest> it = groupsTests.iterator(); it.hasNext();) {
			GroupTest gt = it.next();
			if (gt.equals(new GroupTest(groupName.groupName))) {
				return gt.tests.size();
			}
		}
		return 0;
	}

	private int getAllTests() {
		int count = 0;
		for (Iterator<GroupTest> it = groupsTests.iterator(); it.hasNext();) {
			GroupTest gt = it.next();
			count += gt.tests.size();
		}
		return count;
	}
}