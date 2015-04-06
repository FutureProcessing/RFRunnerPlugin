package rfplugin.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class Sorter extends ViewerSorter {
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof String && e2 instanceof String) {
			String g1 = (String) e1;
			String g2 = (String) e2;

			if (g1 == "Not run")
				return 1;

			return g1.compareTo(g2);
		}
		return 0;
	}
}