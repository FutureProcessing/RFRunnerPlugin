package rfplugin.action;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.MessageConsoleStream;

import rfplugin.Activator;
import rfplugin.views.ConsoleManager;
import rfplugin.views.Test;

public class StopTestAction extends Action{
	TreeViewer treeViewer;
	Action runTestAction;
	Image stopImage = Activator.getImageDescriptor("icons/stop.gif")
			.createImage();
	ImageDescriptor stopImageDescriptor = ImageDescriptor
			.createFromImage(stopImage);
	
	public StopTestAction(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
		this.setImageDescriptor(stopImageDescriptor);
		this.setEnabled(false);
	}
	
	public void setRunTestAction(Action runTestAction){
		this.runTestAction = runTestAction;
	}
	
	public void run() {
		try {
			ISelection selection = treeViewer.getSelection();
			Object obj = ((IStructuredSelection) selection)
					.getFirstElement();

			if (obj instanceof Test) {
				((Test) obj).setStatus("Not run");
				treeViewer.refresh();
				treeViewer.expandAll();

				Runtime.getRuntime().exec("taskkill /F /IM python.exe");
				MessageConsoleStream out = ConsoleManager
						.getMessageConsoleStream("Console");
				out.println("Test Stop");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		runTestAction.setEnabled(true);
		this.setEnabled(false);
	}
}
