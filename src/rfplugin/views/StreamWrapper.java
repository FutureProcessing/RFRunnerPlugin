package rfplugin.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.jface.action.Action;

public class StreamWrapper extends Thread {
	InputStream inputStream = null;
	Test actualRunTest = null;
	TreeViewer treeViewer;
	TreeContentProvider treeContentProvider;
	Action runTestAction;
	Action stopTestAction;

	public StreamWrapper(InputStream inputStream, Test actualRunTest, TreeViewer treeViewer, TreeContentProvider treeContentProvider, Action runTestAction, Action stopTestAction) {
		this.inputStream = inputStream;
		this.actualRunTest = actualRunTest;
		this.treeViewer = treeViewer;
		this.treeContentProvider = treeContentProvider;
		this.runTestAction = runTestAction;
		this.stopTestAction = stopTestAction;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			MessageConsoleStream out = ConsoleManager
					.getMessageConsoleStream("Console");
			while ((line = br.readLine()) != null) {
				out.println(line);
				actualRunTest.setEndTime(System.currentTimeMillis());

				if (line.contains("PASS") || (line.contains("FAIL"))) {
					if (line.contains("PASS")) {
						actualRunTest.setStatus("Passed");
					} else if (line.contains("FAIL")) {
						actualRunTest.setStatus("Failed");
					}
					
					treeContentProvider.updateTest(actualRunTest);
					runTestAction.setEnabled(true);
					stopTestAction.setEnabled(false);
					refresh();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh();
			}
		});
	}
}
