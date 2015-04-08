package rfplugin.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class Sorter extends ViewerSorter {
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof GroupTest && e2 instanceof GroupTest) {
			GroupTest g1 = (GroupTest) e1;
			GroupTest g2 = (GroupTest) e2;

			if (g1.getGroupName() == "Not run")
				return 1;

			return g1.getGroupName().compareTo(g2.getGroupName());
		}
		return 0;
	}
}