package rfplugin.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SettingsAction extends Action {
	TreeViewer treeViewer;
	String projectPath;
	String pybotPath;

	public SettingsAction(TreeViewer treeViewer) {
		this.setText("Settings");
		this.treeViewer = treeViewer;
	}

	public void run() {
		loadPluginSettings();

		final JFrame frame = new JFrame("Settings");

		JPanel panel = new JPanel();
		frame.add(panel);

		JPanel projectPathPanel = new JPanel();
		projectPathPanel.setLayout(new FlowLayout());
		panel.add(projectPathPanel);

		JLabel projectPathLabel = new JLabel("Project path", JLabel.LEFT);
		projectPathPanel.add(projectPathLabel);
		JTextField textField = new JTextField(30);
		projectPathPanel.add(textField);
		textField.setText(projectPath);

		JButton buttonChoose1 = new JButton("Choose");
		projectPathPanel.add(buttonChoose1);
		buttonChoose1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.showOpenDialog(null);
				textField.setText(fileChooser.getSelectedFile().toString());
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
				textField2.setText(fileChooser.getSelectedFile().toString());
			}
		});

		JButton button = new JButton("OK");
		panel.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				projectPath = textField.getText();
				pybotPath = textField2.getText();

				new Thread(new Runnable() {
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								treeViewer.refresh();
							}
						});
					}
				}).start();

				savePluginSettings();
				frame.dispose();
			}
		});

		frame.setVisible(true);
		frame.setSize(550, 170);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void savePluginSettings() {
		Preferences prefs = InstanceScope.INSTANCE.getNode("RFPlugin");

		prefs.put("ProjectPath", projectPath);
		prefs.put("PybotPath", pybotPath);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void loadPluginSettings() {
		Preferences prefs = new InstanceScope().getNode("RFPlugin");
		this.projectPath = prefs.get("ProjectPath", "");
		this.pybotPath = prefs.get("PybotPath", "");
	}
}
