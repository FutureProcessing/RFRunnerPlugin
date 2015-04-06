package rfplugin.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import rfplugin.Activator;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlAdapter;

public class SampleView extends ViewPart {

	public static final String ID = "rfplugin.views.SampleView";
	private Action runTestAction;
	private Action stopTestAction;
	private Action refreshAction;
	private Action settingsAction;

	private List<Test> tests = new ArrayList<Test>();
	String projectPath = null;
	String pybotPath = null;
	Text filterText = null;
	Filter filter = new Filter();

	Group group = Group.STATUS;

	private TreeViewer treeViewer;

	public void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh();
			}
		});
	}

	public class StreamWrapper extends Thread {
		InputStream inputStream = null;
		Test actualRunTest = null;

		StreamWrapper(InputStream inputStream, Test actualRunTest) {
			this.inputStream = inputStream;
			this.actualRunTest = actualRunTest;
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
					int index = tests.indexOf(actualRunTest);
					tests.get(index).setEndTime(System.currentTimeMillis());

					if (line.contains("PASS") || (line.contains("FAIL"))) {
						if (line.contains("PASS")) {
							tests.get(index).setStatus("Passed");
						} else if (line.contains("FAIL")) {
							tests.get(index).setStatus("Failed");
						}
						runTestAction.setEnabled(true);
						stopTestAction.setEnabled(false);
						refresh();
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public boolean listContain(List<Test> list, String query) {
		boolean flag = false;
		for (Test t : list) {
			if (t.getTestName().equals(query)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public void createPartControl(Composite parent) {
		GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(parentData);

		Display display = Display.getCurrent();
		parent.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		Composite searchMenu = new Composite(parent, SWT.BORDER);
		searchMenu.setLayout(new GridLayout(2, false));
		searchMenu.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label searchLabel = new Label(searchMenu, SWT.LEFT);
		searchLabel.setLayoutData(new GridData(SWT.FILL, GridData.CENTER,
				false, false));
		searchLabel.setText("Search: ");

		filterText = new Text(searchMenu, SWT.FILL);
		filterText.setLayoutData(new GridData(SWT.FILL, GridData.CENTER, true,
				false));

		Composite tableViewerComposite = new Composite(parent, SWT.NONE);
		tableViewerComposite.setLayout(new GridLayout(1, true));
		tableViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));

		Tree tree = new Tree(tableViewerComposite, SWT.FILL | SWT.H_SCROLL
				| SWT.V_SCROLL);

		treeViewer = new TreeViewer(tree);
		treeViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		TreeColumn column2 = new TreeColumn(tree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setWidth(50);

		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TableLabelProvider());
		treeViewer.setInput(getViewSite());
		treeViewer.addFilter(filter);
		treeViewer.setSorter(new Sorter());
		treeViewer.expandAll();

		makeActions();
		setDoubleClickAction();
		createToolbar();
		setSearchListener();

		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int parentWidth = parent.getSize().x;
				int scrollWidth = 42;
				column1.setWidth(parentWidth - column2.getWidth() - scrollWidth);
			}
		});
	}

	public Object[] groupTests(String parentElement) {
		List<Test> tmp = new ArrayList<Test>();
		for (Test t : tests) {
			if (group == Group.STATUS) {
				if (t.getStatus().contains(parentElement))
					tmp.add(t);
			}

			if (group == Group.FILE) {
				if (t.getFile().getName().contains(parentElement))
					tmp.add(t);
			}
		}
		return tmp.toArray();
	}

	public class TreeContentProvider implements ITreeContentProvider {
		@Override
		public Object[] getChildren(Object parentElement) {
			treeViewer.expandAll();
			return groupTests((String) parentElement);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Test)
				return false;

			return true;
		}

		private ArrayList<File> searchRobotFiles(String directoryName,
				ArrayList<File> files) {
			File directory = new File(directoryName);
			for (File file : directory.listFiles()) {
				if (file.isFile()
						&& (file.getName().endsWith(".txt") 
						||  file.getName().endsWith(".robot"))) {
					files.add(file);
				} else if (file.isDirectory()) {
					searchRobotFiles(file.getAbsolutePath(), files);
				}
			}
			return files;
		}

		private String getSetting(String file, String options) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				while (line != null) {
					if (line.equals(options)) {
						return br.readLine();
					}
					line = br.readLine();
				}
				br.close();
			} catch (Exception ex) {
			}
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			projectPath = getSetting("settings.dat", "ProjectPath");
			pybotPath = getSetting("settings.dat", "PybotPath");
			ArrayList<File> filesArray;

			if (projectPath != null) {
				try {
					filesArray = searchRobotFiles(projectPath, new ArrayList<File>());
				} catch (Exception ex) {
					return new String[] {};
				}

				if (!filesArray.isEmpty()) {
					for (File currentFile : filesArray) {
						FileReader fileReader = null;

						try {
							fileReader = new FileReader(currentFile);
							BufferedReader bufferedReader = new BufferedReader(fileReader);

							String currentLine;
							final String testCasesStarLineRegEx = "\\*+\\s?Test Cases?\\s?\\**";
							final String testCaseNameRegEx = "^[^\\s|#|*].+";
							
							while ((currentLine = bufferedReader.readLine()) != null) {
								if (Pattern.matches(testCasesStarLineRegEx, currentLine)) {
									while((currentLine = bufferedReader.readLine()) != null) {
										if(currentLine.startsWith("*"))
											break;
										if (Pattern.matches(testCaseNameRegEx, currentLine)) {
											Test test = new Test(currentLine, currentFile, "Not run");
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
					return groupRecords(tests).toArray();
				}
			}
			return new Test[] {};
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Set<String> groupRecords(List<Test> toList) {
		Map<String, Test> uniqueMap = new HashMap<String, Test>();

		for (Test to : toList) {
			if (group == Group.STATUS) {
				if (!uniqueMap.containsKey(to.getStatus())) {
					uniqueMap.put(to.getStatus(), to);
				}
			}
			if (group == Group.FILE) {
				if (!uniqueMap.containsKey(to.getFile().getName())) {
					uniqueMap.put(to.getFile().getName(), to);
				}
			}
		}
		return uniqueMap.keySet();
	}

	class MenuCreator implements IMenuCreator {
		@Override
		public void dispose() {
		}

		@Override
		public Menu getMenu(Control parent) {
			Menu menu = new Menu(parent);
			MenuItem statusItem = new MenuItem(menu, SWT.NONE);
			statusItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					group = Group.STATUS;
					treeViewer.refresh();
					treeViewer.expandAll();
				}
			});
			statusItem.setText("Status");

			MenuItem fileItem = new MenuItem(menu, SWT.NONE);
			fileItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					group = Group.FILE;
					treeViewer.refresh();
					treeViewer.expandAll();
				}
			});
			fileItem.setText("File");
			return menu;
		}

		@Override
		public Menu getMenu(Menu parent) {
			return null;
		}
	}

	class TableLabelProvider implements ITableLabelProvider {
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
						img = Activator.getImageDescriptor(
								"icons/actualRun.gif").createImage();
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
				if (element instanceof String)
					return String.format("%s (%d of %d)", element.toString(),
							groupTests((String) element).length, tests.size());
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
	}

	private void setSearchListener() {
		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filter.setSeatchTestName(filterText.getText());
				treeViewer.refresh();
				treeViewer.expandAll();
			}
		});
	}

	private void createToolbar() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		stopTestAction.setEnabled(false);
		manager.add(runTestAction);
		manager.add(stopTestAction);
		manager.add(refreshAction);
		manager.add(settingsAction);

		Action act = new Action("Group", SWT.DROP_DOWN) {
		};
		act.setMenuCreator(new MenuCreator());
		manager.add(act);
	}

	private void makeActions() {

		// stopTest
		stopTestAction = new Action() {
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
				stopTestAction.setEnabled(false);
			}
		};

		Image stopImage = Activator.getImageDescriptor("icons/stop.gif")
				.createImage();
		ImageDescriptor stopImageDescriptor = ImageDescriptor
				.createFromImage(stopImage);
		stopTestAction.setImageDescriptor(stopImageDescriptor);

		refreshAction = new Action() {
			public void run() {
				tests.clear();
				treeViewer.refresh();
			}
		};
		refreshAction.setText("Refresh");

		settingsAction = new Action() {
			public void run() {
				final JFrame frame = new JFrame("Settings");

				JPanel panel = new JPanel();
				frame.add(panel);

				JPanel projectPathPanel = new JPanel();
				projectPathPanel.setLayout(new FlowLayout());
				panel.add(projectPathPanel);

				JLabel projectPathLabel = new JLabel("Project path",
						JLabel.LEFT);
				projectPathPanel.add(projectPathLabel);
				JTextField textField = new JTextField(30);
				projectPathPanel.add(textField);
				textField.setText(projectPath);

				JButton buttonChoose1 = new JButton("Choose");
				projectPathPanel.add(buttonChoose1);
				buttonChoose1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser
								.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fileChooser.showOpenDialog(null);
						textField.setText(fileChooser.getSelectedFile()
								.toString());
					}
				});

				JPanel pybotPathPanel = new JPanel();
				pybotPathPanel.setLayout(new FlowLayout());
				panel.add(pybotPathPanel);

				JLabel label2 = new JLabel("Pybot path", JLabel.LEFT);
				pybotPathPanel.add(label2);
				final JTextField textField2 = new JTextField(30);
				pybotPathPanel.add(textField2);
				textField2.setText(pybotPath);

				JButton buttonChoose2 = new JButton("Choose");
				pybotPathPanel.add(buttonChoose2);
				buttonChoose2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.showOpenDialog(null);
						textField2.setText(fileChooser.getSelectedFile()
								.toString());
					}
				});

				JButton button = new JButton("OK");
				panel.add(button);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						projectPath = textField.getText();
						pybotPath = textField2.getText();
						try {
							PrintWriter out = new PrintWriter("settings.dat");
							out.println("ProjectPath");
							out.println(projectPath);
							out.println("PybotPath");
							out.println(pybotPath);
							out.close();

							refresh();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						frame.dispose();
					}
				});

				frame.setVisible(true);
				frame.setSize(550, 170);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		};
		settingsAction.setText("Settings");

		runTestAction = new Action() {
			public void run() {
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
								(Test) obj);
						selectedTest.setStatus("Actual run");
						selectedTest.setStartTime(System.currentTimeMillis());
						output.start();

						treeViewer.refresh();
						treeViewer.expandAll();
					} catch (IOException e) {
						e.printStackTrace();
					}

					runTestAction.setEnabled(false);
					stopTestAction.setEnabled(true);
				}
			}
		};

		Image startImgage = Activator.getImageDescriptor("icons/start.gif")
				.createImage();
		ImageDescriptor startImageDescriptor = ImageDescriptor
				.createFromImage(startImgage);
		runTestAction.setImageDescriptor(startImageDescriptor);
	}

	private void setDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				runTestAction.run();
			}
		});
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
}