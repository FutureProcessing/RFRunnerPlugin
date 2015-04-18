package rfplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.service.prefs.Preferences;

import rfplugin.model.Group;
import rfplugin.model.Test;

public class TreeContentProvider implements ITreeContentProvider {
	private TreeViewer treeViewer;
	private String projectPath;
	private List<Test> tests = new ArrayList<Test>();
	private Set<Group> groupsTests = new HashSet();
	
	public TreeContentProvider(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		treeViewer.expandAll();
		return ((Group) parentElement).getTests().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Test)
			return false;
		else if (element instanceof Group)
			return true;
		
		return false;
	}

	private ArrayList<File> searchRobotFiles(String directoryName,
			ArrayList<File> files) {
		File directory = new File(directoryName);
		for (File file : directory.listFiles()) {
			if (file.isFile()
					&& (file.getName().endsWith(".txt") || file.getName()
							.endsWith(".robot"))) {
				files.add(file);
			} else if (file.isDirectory()) {
				searchRobotFiles(file.getAbsolutePath(), files);
			}
		}
		return files;
	}

	private void loadPluginSettings() {
		  Preferences prefs = new InstanceScope().getNode("RFPlugin"); 
		  projectPath = prefs.get("ProjectPath", "");
		}

	@Override
	public Object[] getElements(Object inputElement) {
		loadPluginSettings();
		
		ArrayList<File> filesArray;

		if (projectPath != null) {
			try {
				filesArray = searchRobotFiles(projectPath,
						new ArrayList<File>());
			} catch (Exception ex) {
				return new String[] {};
			}

			if (!filesArray.isEmpty()) {
				for (File currentFile : filesArray) {
					FileReader fileReader = null;

					try {
						fileReader = new FileReader(currentFile);
						BufferedReader bufferedReader = new BufferedReader(
								fileReader);

						String currentLine;
						final String testCasesStarLineRegEx = "\\*+\\s?Test Cases?\\s?\\**";
						final String testCaseNameRegEx = "^[^\\s|#|*].+";

						while ((currentLine = bufferedReader.readLine()) != null) {
							if (Pattern.matches(testCasesStarLineRegEx,
									currentLine)) {
								while ((currentLine = bufferedReader
										.readLine()) != null) {
									if (currentLine.startsWith("*"))
										break;
									if (Pattern.matches(testCaseNameRegEx,
											currentLine)) {
										Test test = new Test(currentLine,
												currentFile, "Not run");
										if (!listContain(tests, currentLine)) {
											tests.add(test);
										}
									}
								}
							}
						}
						fileReader.close();
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return groupTests(tests);
			}
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	private Object[] groupTests(List<Test> toList) {	
		groupsTests.clear();
		
		for (Test test : toList) {
			if (treeViewer.getData("GroupType").equals(GroupType.STATUS)) {
				if (!groupsTests.contains(new Group(test.getStatus()))){
					groupsTests.add(new Group(test.getStatus(), test));
				}
				else {
					for (Iterator<Group> it = groupsTests.iterator(); it.hasNext(); ) {
						Group gt = it.next();
				        if (gt.equals(new Group(test.getStatus()))) {
				        	List<Test> testsList = gt.getTests();
				        	testsList.add(test);
				        	gt.setTests(testsList);
				        	break;
				        }
				    }
				}
			}
			
			else if (treeViewer.getData("GroupType").equals(GroupType.FILE)) {
				if (!groupsTests.contains(new Group(test.getFile().getName()))){
					groupsTests.add(new Group(test.getFile().getName(), test));
				}
				else {
					for (Iterator<Group> it = groupsTests.iterator(); it.hasNext(); ) {
						Group gt = it.next();
				        if (gt.equals(new Group(test.getFile().getName()))) {
				        	List<Test> testsList = gt.getTests();
				        	testsList.add(test);
				        	gt.setTests(testsList);
				        	break;
				        }
				    }
				}
			}
		}
		return groupsTests.toArray();
	}
	
	private boolean listContain(List<Test> list, String query) {
		boolean flag = false;
		for (Test t : list) {
			if (t.getTestName().equals(query)) {
				flag = true;
				break;
			}
		}
		return flag;
	}	
	
	public void updateTest(Test test){
		for (Test t : tests){
			if (t.getTestName().equals(test.getTestName())){
				t = test;
			}
		}
	}
	
	public void clearTests(){
		groupsTests.clear();
		tests.clear();
	}
}
