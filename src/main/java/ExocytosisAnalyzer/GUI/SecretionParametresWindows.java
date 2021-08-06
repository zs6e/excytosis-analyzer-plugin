package ExocytosisAnalyzer.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ExocytosisAnalyzer.datasets.Parameters;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import net.miginfocom.swing.MigLayout;

public class SecretionParametresWindows extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JCheckBox useMinimalPointsForFitter;
	public JCheckBox useExpandFrames;
	public JCheckBox useMaxTauFrames;
	public JCheckBox useMinTauFrames;
	public JCheckBox useMaxRadius;
	public JCheckBox useMinRadius;
	public JCheckBox useMaxDisplacement;
	public JCheckBox useMinDecayFitterR2;
	public JCheckBox useMinSNR;
	public JCheckBox useGaussienfitter2DR2;

	JTextField minimalPointsForFitterField;
	JTextField expandFramesField_L;
	JTextField expandFramesField_R;

	JTextField MaxTauFramesField;
	JTextField MinTauFramesField;
	JTextField MaxRadiusField;
	JTextField MinRadiusField;
	JTextField MaxDisplacementField;
	JTextField MinDecayFitterR2Field;

	JTextField MinSNRField;
	JTextField gaussienfitterR2Field;

	ImagePlus image; // film Original
	private DecimalFormat format = new DecimalFormat("#0.00#");
	
	// public Vector<Secretion> detected_secretions;

	public SecretionParametresWindows(ImagePlus imp, Parameters parameters) {


		if (!parameters.loadCustomParameters)
			parameters.InitialSecretionParameters();

		Border titleBorder1 = BorderFactory.createTitledBorder("Event Sorting and Analysis");
		this.setBorder(titleBorder1);

		image = imp;

		useMinimalPointsForFitter = new JCheckBox("", true);
		useExpandFrames = new JCheckBox("", true);

		useMaxTauFrames = new JCheckBox("", false);
		useMinTauFrames = new JCheckBox("", false);
		useMaxRadius = new JCheckBox("", false);
		useMinRadius = new JCheckBox("", false);
		useMaxDisplacement = new JCheckBox("", false);

		useMinDecayFitterR2 = new JCheckBox("", true);
		useMinSNR = new JCheckBox("", true);

		useGaussienfitter2DR2 = new JCheckBox("", true);

		minimalPointsForFitterField = new JTextField(String.valueOf(parameters.minimalPointsForFitter), 5);
		expandFramesField_L = new JTextField(String.valueOf(parameters.expand_frames_L), 5);
		expandFramesField_R = new JTextField(String.valueOf(parameters.expand_frames_R), 5);
		MinSNRField = new JTextField(String.valueOf(parameters.min_SNR), 5);

		MaxTauFramesField = new JTextField(String.valueOf(parameters.max_tau), 5);
		MinTauFramesField = new JTextField(String.valueOf(parameters.min_tau), 5);
		MaxRadiusField = new JTextField(String.valueOf(parameters.max_estimated_radius), 5);
		MinRadiusField = new JTextField(String.valueOf(parameters.min_estimated_radius), 5);
		MaxDisplacementField = new JTextField(String.valueOf(parameters.MaxDisplacement), 5);

		MinDecayFitterR2Field = new JTextField(String.valueOf(parameters.min_decay_fitter_r2), 5);

		gaussienfitterR2Field = new JTextField(String.valueOf(parameters.min_gaussienfitter2DR2), 5);

		minimalPointsForFitterField.setEnabled(parameters.useMinimalPointsForFitter);
		expandFramesField_L.setEnabled(parameters.useExpandFrames);
		expandFramesField_R.setEnabled(parameters.useExpandFrames);
		MinSNRField.setEnabled(parameters.useMinSNR);

		MaxTauFramesField.setEnabled(parameters.useMaxTauFrames);
		MinTauFramesField.setEnabled(parameters.useMinTauFrames);
		MaxRadiusField.setEnabled(parameters.useMaxRadius);
		MinRadiusField.setEnabled(parameters.useMinRadius);
		MaxDisplacementField.setEnabled(parameters.useMaxDisplacement);

		MinDecayFitterR2Field.setEnabled(parameters.useMinDecayFitterR2);

		gaussienfitterR2Field.setEnabled(parameters.useGaussienfitter2DR2);

		// Checkbox selection

		useMinimalPointsForFitter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinimalPointsForFitter.isSelected()) {
					parameters.useMinimalPointsForFitter = true;
					minimalPointsForFitterField.setEnabled(true);
				} else {
					minimalPointsForFitterField.setEnabled(false);
					parameters.useMinimalPointsForFitter = false;
				}
			}
		});

		useExpandFrames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useExpandFrames.isSelected()) {
					parameters.useExpandFrames = true;
					expandFramesField_L.setEnabled(true);
					expandFramesField_R.setEnabled(true);
				} else {
					expandFramesField_L.setEnabled(false);
					expandFramesField_R.setEnabled(false);
					parameters.useExpandFrames = false;

				}
			}
		});

		useMinSNR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinSNR.isSelected()) {
					parameters.useMinSNR = true;
					MinSNRField.setEnabled(true);
				} else {
					MinSNRField.setEnabled(false);
					parameters.useMinSNR = false;
				}
			}
		});

		useMaxTauFrames.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent e) {
				if (useMaxTauFrames.isSelected()) {
					parameters.useMaxTauFrames = true;
					MaxTauFramesField.setEnabled(true);
				} else {
					MaxTauFramesField.setEnabled(false);
					parameters.useMaxTauFrames = false;
				}
			}
		});

		useMinTauFrames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinTauFrames.isSelected()) {
					parameters.useMinTauFrames = true;
					MinTauFramesField.setEnabled(true);
				} else {
					MinTauFramesField.setEnabled(false);
					parameters.useMinTauFrames = false;
				}
			}
		});

		useMaxRadius.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMaxRadius.isSelected()) {
					parameters.useMaxRadius = true;
					MaxRadiusField.setEnabled(true);
				} else {
					MaxRadiusField.setEnabled(false);
					parameters.useMaxRadius = false;
				}
			}
		});

		useMinRadius.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinRadius.isSelected()) {
					parameters.useMinRadius = true;
					MinRadiusField.setEnabled(true);
				} else {
					MinRadiusField.setEnabled(false);
					parameters.useMinRadius = false;
				}
			}
		});
		useMaxDisplacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMaxDisplacement.isSelected()) {
					parameters.useMaxDisplacement = true;
					MaxDisplacementField.setEnabled(true);
				} else {
					MaxDisplacementField.setEnabled(false);
					parameters.useMaxDisplacement = false;
				}
			}
		});

		useMinDecayFitterR2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinDecayFitterR2.isSelected()) {
					parameters.useMinDecayFitterR2 = true;
					MinDecayFitterR2Field.setEnabled(true);
				} else {
					MinDecayFitterR2Field.setEnabled(false);
					parameters.useMinDecayFitterR2 = false;
				}
			}
		});

		useGaussienfitter2DR2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useGaussienfitter2DR2.isSelected()) {
					parameters.useGaussienfitter2DR2 = true;
					gaussienfitterR2Field.setEnabled(true);
				} else {
					gaussienfitterR2Field.setEnabled(false);
					parameters.useGaussienfitter2DR2 = false;
				}
			}
		});

		// texts of descriptions

		JLabel minimalPointsForFitterFieldLabel = new JLabel("Min. points for fitting procedure:");
		JLabel expandFramesFieldLabel = new JLabel("Extended frames (pre/post peak):");
		JLabel MinSNRFieldLabel = new JLabel("Detection threshold (dF/σ):");
		JLabel MaxTauFramesFieldLabel = new JLabel("Upper Decay limit:");
		JLabel MinTauFramesFieldLabel = new JLabel("Lower Decay limit:");
		JLabel MaxRadiusFieldLabel = new JLabel("Upper estimated radius limit:");
		JLabel MinRadiusFieldLabel = new JLabel("Lower estimated radius limit:");
		JLabel MaxDisplacementFieldLabel = new JLabel("Max. displacement:");
		JLabel MinR2FieldLabel = new JLabel("Min. R² (Decay fitting procedure):");
		JLabel gaussienfitter2DR2Label = new JLabel("Min. R² (Est. radius fitting procedure):");

		URL expandImgURL = TrackingWindows.class.getResource("expand.jpg");
		URL deltaFImgURL = TrackingWindows.class.getResource("deltaF.jpg");
		expandFramesFieldLabel
				.setToolTipText("<html><img src=" + expandImgURL + " width=\"573\" height=\"322\"> <br></html>");
		MinSNRFieldLabel
				.setToolTipText("<html><img src=" + deltaFImgURL + " width=\"614\" height=\"394\"> <br></html>");

		JLabel minimalPointsForFitterLabel = new JLabel(" points");
		JLabel expandFramesLabel = new JLabel(" frames");

		JLabel MaxTauFramesLabel = new JLabel(" frames ("
				+ String.valueOf(
						format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
				+ " " + parameters.timeUnit + ")");
		JLabel MinTauFramesLabel = new JLabel(" frames ("
				+ String.valueOf(
						format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
				+ " " + parameters.timeUnit + ")");

		JLabel MaxRadiusLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(MaxRadiusField.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel MinRadiusLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(MinRadiusField.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel MaxDisplacementLabel = new JLabel(" pixels");
		JLabel MinR2Label = new JLabel(" ");
		JLabel MinGaussR2Label = new JLabel(" ");
		JLabel MinSNRLabel = new JLabel(" σ (MAD)");

		Panel ParametresPanel1 = new Panel();
		ParametresPanel1.setLayout(new MigLayout("", "[][][][]", "[][]"));

		ParametresPanel1.add(useMinimalPointsForFitter, "cell 0 1");
		ParametresPanel1.add(minimalPointsForFitterFieldLabel, "cell 1 1");
		ParametresPanel1.add(minimalPointsForFitterField, "cell 2 1");
		ParametresPanel1.add(minimalPointsForFitterLabel, "cell 3 1");

		ParametresPanel1.add(useExpandFrames, "cell 0 2");
		ParametresPanel1.add(expandFramesFieldLabel, "cell 1 2");
		ParametresPanel1.add(expandFramesField_L, "cell 2 2");
		ParametresPanel1.add(expandFramesField_R, "cell 3 2");
		//ParametresPanel1.add(expandFramesLabel, "cell 4 2");

		ParametresPanel1.add(useMinSNR, "cell 0 3");
		ParametresPanel1.add(MinSNRFieldLabel, "cell 1 3");
		ParametresPanel1.add(MinSNRField, "cell 2 3");
		ParametresPanel1.add(MinSNRLabel, "cell 3 3");

		ParametresPanel1.add(useMaxTauFrames, "cell 0 4");
		ParametresPanel1.add(MaxTauFramesFieldLabel, "cell 1 4");
		ParametresPanel1.add(MaxTauFramesField, "cell 2 4");
		ParametresPanel1.add(MaxTauFramesLabel, "cell 3 4");

		ParametresPanel1.add(useMinTauFrames, "cell 0 5");
		ParametresPanel1.add(MinTauFramesFieldLabel, "cell 1 5");
		ParametresPanel1.add(MinTauFramesField, "cell 2 5");
		ParametresPanel1.add(MinTauFramesLabel, "cell 3 5");

		ParametresPanel1.add(useMaxRadius, "cell 0 6");
		ParametresPanel1.add(MaxRadiusFieldLabel, "cell 1 6");
		ParametresPanel1.add(MaxRadiusField, "cell 2 6");
		ParametresPanel1.add(MaxRadiusLabel, "cell 3 6");

		ParametresPanel1.add(useMinRadius, "cell 0 7");
		ParametresPanel1.add(MinRadiusFieldLabel, "cell 1 7");
		ParametresPanel1.add(MinRadiusField, "cell 2 7");
		ParametresPanel1.add(MinRadiusLabel, "cell 3 7");

		ParametresPanel1.add(useMaxDisplacement, "cell 0 8");
		ParametresPanel1.add(MaxDisplacementFieldLabel, "cell 1 8");
		ParametresPanel1.add(MaxDisplacementField, "cell 2 8");
		ParametresPanel1.add(MaxDisplacementLabel, "cell 3 8");

		ParametresPanel1.add(useMinDecayFitterR2, "cell 0 9");
		ParametresPanel1.add(MinR2FieldLabel, "cell 1 9");
		ParametresPanel1.add(MinDecayFitterR2Field, "cell 2 9");
		ParametresPanel1.add(MinR2Label, "cell 3 9");

		ParametresPanel1.add(useGaussienfitter2DR2, "cell 0 10");
		ParametresPanel1.add(gaussienfitter2DR2Label, "cell 1 10");
		ParametresPanel1.add(gaussienfitterR2Field, "cell 2 10");
		ParametresPanel1.add(MinGaussR2Label, "cell 3 10");

		minimalPointsForFitterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.minimalPointsForFitter = Integer.parseInt(minimalPointsForFitterField.getText());
					minimalPointsForFitterLabel.setText(" points");
				} catch (NumberFormatException arg) {
					minimalPointsForFitterLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.minimalPointsForFitter = Integer.parseInt(minimalPointsForFitterField.getText());
					minimalPointsForFitterLabel.setText(" points");
				} catch (NumberFormatException arg) {
					minimalPointsForFitterLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		expandFramesField_L.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_L = Integer.parseInt(expandFramesField_L.getText());
					expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_L = Integer.parseInt(expandFramesField_L.getText());
					expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		expandFramesField_R.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_R = Integer.parseInt(expandFramesField_R.getText());
					expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_R = Integer.parseInt(expandFramesField_R.getText());
					expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MinSNRField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.min_SNR = Double.parseDouble(MinSNRField.getText());
					MinSNRLabel.setText(" σ (MAD)");
				} catch (NumberFormatException arg) {
					MinSNRLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.min_SNR = Double.parseDouble(MinSNRField.getText());
					MinSNRLabel.setText(" σ (MAD)");
				} catch (NumberFormatException arg) {
					MinSNRLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MaxTauFramesField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.max_tau = Double.parseDouble(MaxTauFramesField.getText());
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException arg) {
					MaxTauFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.max_tau = Double.parseDouble(MaxTauFramesField.getText());
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException arg) {
					MaxTauFramesLabel.setText(" Invalid number");
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MinTauFramesField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.min_tau = Double.parseDouble(MinTauFramesField.getText());
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException arg) {
					MinTauFramesLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.min_tau = Double.parseDouble(MinTauFramesField.getText());
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException arg) {
					MinTauFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MaxRadiusField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.max_estimated_radius = Double.parseDouble(MaxRadiusField.getText());
					MaxRadiusLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MaxRadiusField.getText()) * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					MaxRadiusLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.max_estimated_radius = Double.parseDouble(MaxRadiusField.getText());
					MaxRadiusLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MaxRadiusField.getText()) * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					MaxRadiusLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MinRadiusField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				parameters.min_estimated_radius = Double.parseDouble(MinRadiusField.getText());
				try {
					MinRadiusLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MinRadiusField.getText()) * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					MinRadiusLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.min_estimated_radius = Double.parseDouble(MinRadiusField.getText());
					MinRadiusLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MinRadiusField.getText()) * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					MinRadiusLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MaxDisplacementField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.MaxDisplacement = Double.parseDouble(MaxDisplacementField.getText());
					MaxDisplacementLabel.setText(" pixels");
				} catch (NumberFormatException arg) {
					MaxDisplacementLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.MaxDisplacement = Double.parseDouble(MaxDisplacementField.getText());
					MaxDisplacementLabel.setText(" pixels");
				} catch (NumberFormatException arg) {
					MaxDisplacementLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		MinDecayFitterR2Field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.min_decay_fitter_r2 = Double.parseDouble(MinDecayFitterR2Field.getText());
					MinR2Label.setText(" ");
				} catch (NumberFormatException arg) {
					MinR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.min_decay_fitter_r2 = Double.parseDouble(MinDecayFitterR2Field.getText());
					MinR2Label.setText(" ");
				} catch (NumberFormatException arg) {
					MinR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		gaussienfitterR2Field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.min_gaussienfitter2DR2 = Double.parseDouble(gaussienfitterR2Field.getText());
					MinGaussR2Label.setText(" ");
				} catch (NumberFormatException arg) {
					MinGaussR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.min_gaussienfitter2DR2 = Double.parseDouble(gaussienfitterR2Field.getText());
					MinGaussR2Label.setText(" ");
				} catch (NumberFormatException arg) {
					MinGaussR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});

		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton restoreButton = new JButton("Restore");

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SaveDialog saveFileDialog = new SaveDialog("Save parasmeters", parameters.path, parameters.filename, ".dat");
				String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
				if (saveFilePatch != null) {
					File file = new File(saveFilePatch);
					Parameters savedparas = parameters;
					FileOutputStream out;
					try {
						out = new FileOutputStream(file);
						ObjectOutputStream objOut = new ObjectOutputStream(out);
						objOut.writeObject(savedparas);
						objOut.flush();
						objOut.close();

					} catch (IOException e1) {

						e1.printStackTrace();
					}

				}
			}
		});

		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				OpenDialog openFileDialog = new OpenDialog("Load parameters", parameters.path, parameters.filename);
				String openFilePatch = openFileDialog.getPath();
				if (openFilePatch == null)
					return;

				File file = new File(openFilePatch);
				FileInputStream in;
				try {
					in = new FileInputStream(file);
					ObjectInputStream objIn = new ObjectInputStream(in);
					parameters.copy((Parameters) objIn.readObject());
					objIn.close();
					parameters.loadCustomParameters = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				// checkboxs
				useMinimalPointsForFitter.setSelected(parameters.useMinimalPointsForFitter);
				useExpandFrames.setSelected(parameters.useExpandFrames);
				useMinSNR.setSelected(parameters.useMinSNR);
				useMaxTauFrames.setSelected(parameters.useMaxTauFrames);
				useMinTauFrames.setSelected(parameters.useMinTauFrames);
				useMaxRadius.setSelected(parameters.useMaxRadius);
				useMinRadius.setSelected(parameters.useMinRadius);
				useMaxDisplacement.setSelected(parameters.useMaxDisplacement);
				useMinDecayFitterR2.setSelected(parameters.useMinDecayFitterR2);
				useGaussienfitter2DR2.setSelected(parameters.useGaussienfitter2DR2);
				
				
				
				

				// fields
				
				minimalPointsForFitterField.setEnabled(parameters.useMinimalPointsForFitter);
				expandFramesField_L.setEnabled(parameters.useExpandFrames);
				expandFramesField_R.setEnabled(parameters.useExpandFrames);
				MaxTauFramesField.setEnabled(parameters.useMaxTauFrames);
				MinTauFramesField.setEnabled(parameters.useMinTauFrames);
				MinSNRField.setEnabled(parameters.useMinSNR);
				MaxRadiusField.setEnabled(parameters.useMaxRadius);
				MinRadiusField.setEnabled(parameters.useMinRadius);
				MaxDisplacementField.setEnabled(parameters.useMaxDisplacement);
				MinDecayFitterR2Field.setEnabled(parameters.useMinDecayFitterR2);
				gaussienfitterR2Field.setEnabled(parameters.useGaussienfitter2DR2);

				// Values
				minimalPointsForFitterField.setText(String.valueOf(parameters.minimalPointsForFitter));
				expandFramesField_L.setText(String.valueOf(parameters.expand_frames_L));
				expandFramesField_R.setText(String.valueOf(parameters.expand_frames_R));
				MinSNRField.setText(String.valueOf(format.format(parameters.min_SNR)));
				MaxTauFramesField.setText(String.valueOf(format.format(parameters.max_tau)));
				MinTauFramesField.setText(String.valueOf(format.format(parameters.min_tau)));
				MaxRadiusField.setText(String.valueOf(format.format(parameters.max_estimated_radius)));
				MinRadiusField.setText(String.valueOf(format.format(parameters.min_estimated_radius)));
				MaxDisplacementField.setText(String.valueOf(format.format(parameters.MaxDisplacement)));
				MinDecayFitterR2Field.setText(String.valueOf(format.format(parameters.min_decay_fitter_r2)));
				gaussienfitterR2Field.setText(String.valueOf(format.format(parameters.min_gaussienfitter2DR2)));

			}
		});
		restoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parameters.InitialSecretionParameters();

				useMinimalPointsForFitter.setSelected(parameters.useMinimalPointsForFitter);
				useExpandFrames.setSelected(parameters.useExpandFrames);
				useMinSNR.setSelected(parameters.useMinSNR);
				useMaxTauFrames.setSelected(parameters.useMaxTauFrames);
				useMinTauFrames.setSelected(parameters.useMinTauFrames);
				useMaxRadius.setSelected(parameters.useMaxRadius);
				useMinRadius.setSelected(parameters.useMinRadius);
				useMaxDisplacement.setSelected(parameters.useMaxDisplacement);
				useMinDecayFitterR2.setSelected(parameters.useMinDecayFitterR2);
				useGaussienfitter2DR2.setSelected(parameters.useGaussienfitter2DR2);

				minimalPointsForFitterField.setEnabled(parameters.useMinimalPointsForFitter);
				expandFramesField_L.setEnabled(parameters.useExpandFrames);
				expandFramesField_R.setEnabled(parameters.useExpandFrames);
				MinSNRField.setEnabled(parameters.useMinSNR);
				MaxTauFramesField.setEnabled(parameters.useMaxTauFrames);
				MinTauFramesField.setEnabled(parameters.useMinTauFrames);
				MaxRadiusField.setEnabled(parameters.useMaxRadius);
				MinRadiusField.setEnabled(parameters.useMinRadius);
				MaxDisplacementField.setEnabled(parameters.useMaxDisplacement);
				MinDecayFitterR2Field.setEnabled(parameters.useMinDecayFitterR2);
				gaussienfitterR2Field.setEnabled(parameters.useGaussienfitter2DR2);

				minimalPointsForFitterField.setText(String.valueOf(parameters.minimalPointsForFitter));
				expandFramesField_L.setText(String.valueOf(parameters.expand_frames_L));
				expandFramesField_R.setText(String.valueOf(parameters.expand_frames_R));
				MinSNRField.setText(String.valueOf(format.format(parameters.min_SNR)));
				MaxTauFramesField.setText(String.valueOf(format.format(parameters.max_tau)));
				MinTauFramesField.setText(String.valueOf(format.format(parameters.min_tau)));
				MaxRadiusField.setText(String.valueOf(format.format(parameters.max_estimated_radius)));
				MinRadiusField.setText(String.valueOf(format.format(parameters.min_estimated_radius)));
				MaxDisplacementField.setText(String.valueOf(format.format(parameters.MaxDisplacement)));
				MinDecayFitterR2Field.setText(String.valueOf(format.format(parameters.min_decay_fitter_r2)));
				gaussienfitterR2Field.setText(String.valueOf(format.format(parameters.min_gaussienfitter2DR2)));

			}
		});
		JPanel ParametresPanel2 = new JPanel();
		Border titleBorder2 = BorderFactory.createTitledBorder("Parameters");
		ParametresPanel2.setBorder(titleBorder2);
		ParametresPanel2.setLayout(new FlowLayout());
		ParametresPanel2.add(saveButton);
		ParametresPanel2.add(loadButton);
		ParametresPanel2.add(restoreButton);

		this.setLayout(new BorderLayout());
		this.add(ParametresPanel1, BorderLayout.NORTH);
		this.add(ParametresPanel2, BorderLayout.SOUTH);

	}

}
