/* APKLoader is a Swing GUI for common ADB commands.
 * Commands include: install, upgrade (install -r), and test (install -t).
 * The GUI allows the user to select the path to their ADB and APK file and
 * and to connect to a device. The paths will be remembered the next time
 * the user launches the application.
 * Author: Cale McCammon
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;

public class APKLoader extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JTextField adbPathText, apkFileText, outputText;
	private JLabel adbPathLabel, apkFileLabel, outputLabel;
	private JButton adbButton, apkButton, loadAPKButton, connectButton;
	private JRadioButton installNewRadio, upgradeRadio, testRadio;
	private ButtonGroup radioGroup;
	private JPanel mainPanel, adbPanel, apkPanel, buttonPanel, outputPanel, radioPanel;
	private TitledBorder outputBorder;
	private FileDialog fileDialog;
	private Preferences userPrefs = Preferences.userNodeForPackage(APKLoader.class);
	private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	final static String adbFile = "adb.exe";
	private String adbPath, apkPath, apkFile;
	
	public APKLoader() {
		super("APKLoader");
		setPreferredSize(new Dimension(400, 200));
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1));
		setTitle("APKLoader");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		adbPathLabel = new JLabel("ADB Path");
		adbPathText = new JTextField(20);
		adbPathText.setEditable(false);
		adbButton = new JButton("Browse");
		adbButton.addActionListener(this);
		apkFileLabel = new JLabel("APK File");
		apkFileText = new JTextField(20);
		apkFileText.setEditable(false);
		installNewRadio = new JRadioButton("New Install");
		installNewRadio.setSelected(true);
		upgradeRadio = new JRadioButton("Upgrade/Replace");
		testRadio = new JRadioButton("Install Test Build");
		radioGroup = new ButtonGroup();
		radioGroup.add(installNewRadio);
		radioGroup.add(upgradeRadio);
		radioGroup.add(testRadio);
		apkButton = new JButton("Browse");
		apkButton.addActionListener(this);
		loadAPKButton = new JButton("Load APK");
		loadAPKButton.addActionListener(this);
		connectButton = new JButton("Connect Device");
		connectButton.addActionListener(this);
		outputLabel = new JLabel("Log");
		outputText = new JTextField(30);
		outputText.setEditable(false);
		
		adbPanel = new JPanel();
		adbPanel.add(adbPathLabel);
		adbPanel.add(adbPathText);
		adbPanel.add(adbButton);
		mainPanel.add(adbPanel);
		
		apkPanel = new JPanel();
		apkPanel.add(apkFileLabel);
		apkPanel.add(apkFileText);
		apkPanel.add(apkButton);
		mainPanel.add(apkPanel);
		
		radioPanel = new JPanel();
		radioPanel.add(installNewRadio);
		radioPanel.add(upgradeRadio);
		radioPanel.add(testRadio);
		mainPanel.add(radioPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.add(loadAPKButton);
		buttonPanel.add(connectButton);
		mainPanel.add(buttonPanel);
		outputPanel = new JPanel();
		outputPanel.add(outputLabel);
		outputPanel.setBorder(outputBorder);
		outputPanel.add(outputText);
		mainPanel.add(outputPanel);

		fileDialog = new FileDialog(this, "Choose a file", FileDialog.LOAD);
		
		adbPath = userPrefs.get("adbPath", null);
		apkFile = userPrefs.get("apkFile", null);
		apkPath = userPrefs.get("apkPath", null);
		
		adbPathText.setText(adbPath);
		apkFileText.setText(apkFile);
		
		add(mainPanel);
		pack();
	}
	
	public void actionPerformed(ActionEvent event) {
		//Opens file dialog for user to set ADB path. Saves the path to the user's preferences.
		if(event.getSource() == adbButton) {
			fileDialog.setDirectory(System.getProperty("user.home"));
			fileDialog.setFile("*adb.exe");
			fileDialog.setVisible(true);
			String directory = fileDialog.getDirectory();
			if(directory == null) {
				return;
			} else {
				adbPathText.setText(directory + adbFile);
				userPrefs.put("adbPath", directory + adbFile);
				adbPath = userPrefs.get("adbPath", directory + adbFile);
			}
		}
		//Open file dialog for user to select APK. Saves the directory and file to user's preferences.
		if(event.getSource() == apkButton) {
			fileDialog.setDirectory(System.getProperty("user.home") + System.getProperty("file.separator") + "Downloads");
			fileDialog.setFile("*.apk");
			fileDialog.setVisible(true);
			String file = fileDialog.getFile();
			if(file == null) {
				return;
			} else {
				String directory = fileDialog.getDirectory();
				userPrefs.put("apkPath", directory);
				userPrefs.put("apkFile", file);
				apkFileText.setText(file);
				apkPath = userPrefs.get("apkPath", directory);
				apkFile = userPrefs.get("apkFile", file);
			}
		}
		
		if(event.getSource() == loadAPKButton) {
		    setCursor(waitCursor);
		    //If no APK is selected, then show an error.
			if(apkFileText.getText().equals("")) {
				outputText.setText("Failure: No APK selected.");
			//If no ADB is selected, then show an error.
			} else if (adbPathText.getText().equals("")) {
				outputText.setText("Failure: No ADB selected.");
			} else {
				ProcessBuilder pb = null;
				Process p;
				String apkFullPath = apkPath + apkFile;
				try {
					//If new Install Radio is selected then pass install command and APK full path to ADB.
					if(installNewRadio.isSelected()) {
						pb = new ProcessBuilder(adbPathText.getText(), "install", apkFullPath);
						System.out.println(apkFullPath);
					//If Upgrade Radio is selected then pass install -r command and APK full path to ADB.
					} else if (upgradeRadio.isSelected()) {
						pb = new ProcessBuilder(adbPathText.getText(), "install", "-r", apkFullPath);
					//If Test Radio is selected, then pass install -t command and APK full path to ADB.
					} else if (testRadio.isSelected()) {
						pb = new ProcessBuilder(adbPathText.getText(), "install", "-t", apkFullPath);
					}
					p = pb.start();
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String readline;
					String input;
					System.out.println("Installing " + apkFullPath);
					//Input stream for handling installs from older Android devices.
					while((input = inputReader.readLine()) != null) {
						System.out.println(input);
						if (input.contains("Failure")) {
							String readlineSubstring = input.substring(input.indexOf("[") + 1, input.indexOf("]"));
							outputText.setText("Failure: " + readlineSubstring);
						} else if(input.contains("Success")) {
							outputText.setText("Sucess: APK installed.");
						} else if (input.contains("no devices/emulators found")) {
							outputText.setText("Failure: No devices.");
							p.destroy();
						} else if(input.contains("more than one device")) {
							outputText.setText("Failure: More than one device.");
							p.destroy();
						}
					}
					//Error stream for handling installs on newere Android devices.
					while ((readline = reader.readLine()) != null) {
						System.out.println(readline);
						if(readline.contains("Success")) {
							outputText.setText("Sucess: APK installed.");
							p.destroy();
						} else if (readline.contains("no devices")) {
							outputText.setText("Failure: No devices.");
							p.destroy();
						} else if(readline.contains("more than one device")) {
							outputText.setText("Failure: More than one device.");
							p.destroy();
						} else if (readline.contains("Failure")) {
							String readlineSubstring = readline.substring(readline.indexOf("[") + 1, readline.indexOf("]"));
							outputText.setText("Failure: " + readlineSubstring);
							p.destroy();
						} else {
							outputText.setText("Failure: Something went wrong.");
							p.destroy();
						}
					}
				} catch (IOException e) {
					outputText.setText("Failure: ADB or APK not found.");
					e.printStackTrace();
				}
			}
			setCursor(defaultCursor);
		}
		
		//Check if device is connected.
		if(event.getSource() == connectButton) {
			ProcessBuilder pb = new ProcessBuilder(adbPathText.getText(), "devices");
			Process p;
			try {
				p = pb.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String readline;
				ArrayList<String> output = new ArrayList<String>();
				//Show whether connection is sueccessful or not from the input stream.
				while ((readline = reader.readLine()) != null) {
					output.add(readline);
					if(output.size() >= 2) {
						if(output.get(1).contains("device")) {
							outputText.setText("Success: Device connected.");
							p.waitFor();
						} else {
							outputText.setText("Failure: Device not found.");
							p.destroy();
						}
					}
				}
			} catch (IOException e) {
				outputText.setText("Failure: ADB not found.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main (String args []) {
		APKLoader window = new APKLoader();
		window.setVisible(true);
	}
}
