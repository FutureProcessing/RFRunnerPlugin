package rfplugin.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import rfplugin.model.Group;

public class Sorter extends ViewerSorter {
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof Group && e2 instanceof Group) {
			Group g1 = (Group) e1;
			Group g2 = (Group) e2;

			if (g1.getGroupName() == "Not run")
				return 1;

			return g1.getGroupName().compareTo(g2.getGroupName());
		}
		return 0;
	}
}