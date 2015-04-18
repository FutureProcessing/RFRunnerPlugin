package rfplugin.action;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.service.prefs.Preferences;

import rfplugin.Activator;
import rfplugin.model.Test;
import rfplugin.views.ConsoleManager;
import rfplugin.views.StreamWrapper;
import rfplugin.views.TreeContentProvider;

public class RunTestAction extends Action {
	TreeViewer treeViewer;
	Action stopTestAction;
	String pybotPath;
	TreeContentProvider treeContentProvider;
	Image startImgage = Activator.getImageDescriptor("icons/start.gif")
			.createImage();
	ImageDescriptor startImageDescriptor = ImageDescriptor
			.createFromImage(startImgage);
	
	public RunTestAction(TreeViewer treeViewer, TreeContentProvider treeContentProvider){
		this.treeViewer = treeViewer;
		this.treeContentProvider = treeContentProvider;
		this.setImageDescriptor(startImageDescriptor);
	}
	
	public void setStopTestAction(Action stopTestAction){
		this.stopTestAction = stopTestAction;
	}
	
	public void run() {
		loadPluginSettings();
		
		MessageConsoleStream out = ConsoleManager
				.getMessageConsoleStream("Console");
		out.println("");

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection)
				.getFirstElement();

		if (obj instanceof Test) {
			Runtime rt = Runtime.getRuntime();
			StreamWrapper output;

			try {
				Test selectedTest = (Test) obj;
				String testName = selectedTest.getTestName();
				String filePath = selectedTest.getFile().getPath();
				String pybotCommand = new StringBuilder(pybotPath)
						.append(" --test ").append('"')
						.append(testName).append('"').append(' ')
						.append(filePath).toString();
				Process proc = rt.exec(pybotCommand);
				output = new StreamWrapper(proc.getInputStream(),
						(Test) obj, treeViewer, treeContentProvider, this, stopTestAction);
				selectedTest.setStatus("Actual run");
				selectedTest.setStartTime(System.currentTimeMillis());
				output.start();

				treeViewer.refresh();
				treeViewer.expandAll();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.setEnabled(false);
			stopTestAction.setEnabled(true);
		}
	}
	
	private void loadPluginSettings() {
		Preferences prefs = new InstanceScope().getNode("RFPlugin");
		this.pybotPath = prefs.get("PybotPath", "");
	}
}
