package rfplugin.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class Filter extends ViewerFilter {
	String searchTestName = "";
	
	public void setSeatchTestName(String searchTestName){
		this.searchTestName = searchTestName;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement,
			Object element) {

		StructuredViewer sviewer = (StructuredViewer) viewer;
		ITreeContentProvider provider = (ITreeContentProvider) sviewer
				.getContentProvider();
		
		if (element instanceof Group ) {
			for (Object child: provider.getChildren(element)) {
				if (select(viewer, element, child)) 
					return true; 
			}
		}
		else {
			Test test = (Test)element;
			if (test.getTestName().contains(searchTestName)) {
				return true;
			}
		}
		return false;
	}
}
