package ExocytosisAnalyzer.GUI;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

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
	public JCheckBox fixedExpand;
	public JCheckBox useMaxTauFrames;
	public JCheckBox useMinTauFrames;
	public JCheckBox useMaxSize;
	public JCheckBox useMinSize;
	public JCheckBox useMaxDisplacement;
	public JCheckBox useMinDecayFitterR2;
	public JCheckBox useMinSNR;
	public JCheckBox useGaussienfitter2DR2;

	JTextField minimalPointsForFitterField;
	JTextField expandFramesField_L;
	JTextField expandFramesField_R;

	JTextField MaxTauFramesField;
	JTextField MinTauFramesField;
	JTextField MaxSizeField;
	JTextField MinSizeField;
	JTextField MaxDisplacementField;
	JTextField MinDecayFitterR2Field;

	JTextField MinSNRField;
	JTextField gaussienfitterR2Field;

	ImagePlus image; // film Original
	//private DecimalFormat format = new DecimalFormat("#0.00#");
	
	// Addition from Shengyan Xu
	Locale locale = Locale.getDefault();
    private NumberFormat format = NumberFormat.getNumberInstance(locale);
	
	// public Vector<Secretion> detected_secretions;

	public SecretionParametresWindows(ImagePlus imp, Parameters parameters) {


	

		Border titleBorder1 = BorderFactory.createTitledBorder("Event Sorting and Analysis");
		this.setBorder(titleBorder1);


		image = imp;

		useMinimalPointsForFitter = new JCheckBox("", parameters.useMinimalPointsForFitter);
		useExpandFrames = new JCheckBox("", parameters.useExpandFrames);
		fixedExpand = new JCheckBox("Fixed position", parameters.useExpandFrames);
		useMaxTauFrames = new JCheckBox("", parameters.useMaxTauFrames);
		useMinTauFrames = new JCheckBox("", parameters.useMinTauFrames);
		useMaxSize = new JCheckBox("", parameters.useMaxSize);
		useMinSize = new JCheckBox("", parameters.useMinSize);
		useMaxDisplacement = new JCheckBox("", parameters.useMaxDisplacement);

		useMinDecayFitterR2 = new JCheckBox("", parameters.useMinDecayFitterR2);
		useMinSNR = new JCheckBox("", parameters.useMinSNR);

		useGaussienfitter2DR2 = new JCheckBox("", parameters.useGaussienfitter2DR2);

		minimalPointsForFitterField = new JTextField(String.valueOf(parameters.minimalPointsForFitter), 5);
		expandFramesField_L = new JTextField(String.valueOf(parameters.expand_frames_L), 5);
		expandFramesField_R = new JTextField(String.valueOf(parameters.expand_frames_R), 5);
		MinSNRField = new JTextField(String.valueOf(format.format(parameters.min_SNR)), 5);
		MaxTauFramesField = new JTextField(String.valueOf(format.format(parameters.max_tau)), 5);
		MinTauFramesField = new JTextField(String.valueOf(format.format(parameters.min_tau)), 5);
		MaxSizeField = new JTextField(String.valueOf(format.format(parameters.max_estimated_size)), 5);
		MinSizeField = new JTextField(String.valueOf(format.format(parameters.min_estimated_size)), 5);
		MaxDisplacementField = new JTextField(String.valueOf(format.format(parameters.MaxDisplacement)), 5);
		MinDecayFitterR2Field = new JTextField(String.valueOf(format.format(parameters.min_decay_fitter_r2)), 5);
		gaussienfitterR2Field = new JTextField(String.valueOf(format.format(parameters.min_gaussienfitter2DR2)), 5);

		
		
		
		minimalPointsForFitterField.setEnabled(parameters.useMinimalPointsForFitter);
		expandFramesField_L.setEnabled(parameters.useExpandFrames);
		expandFramesField_R.setEnabled(parameters.useExpandFrames);
		MinSNRField.setEnabled(parameters.useMinSNR);
		MaxTauFramesField.setEnabled(parameters.useMaxTauFrames);
		MinTauFramesField.setEnabled(parameters.useMinTauFrames);
		MaxSizeField.setEnabled(parameters.useMaxSize);
		MinSizeField.setEnabled(parameters.useMinSize);
		MaxDisplacementField.setEnabled(parameters.useMaxDisplacement);
		MinDecayFitterR2Field.setEnabled(parameters.useMinDecayFitterR2);
		gaussienfitterR2Field.setEnabled(parameters.useGaussienfitter2DR2);



		// texts of descriptions

		
		
		
		JLabel minimalPointsForFitterFieldLabel = new JLabel("Min. points for fitting procedure:");
		JLabel expandFramesFieldLabel = new JLabel("Expanding frames (pre/post peak):" );
		JLabel MinSNRFieldLabel = new JLabel("Detection threshold:");
		JLabel MaxTauFramesFieldLabel = new JLabel("Upper Decay limit:");
		JLabel MinTauFramesFieldLabel = new JLabel("Lower Decay limit:");
		JLabel MaxSizeFieldLabel = new JLabel("Upper apparent size limit:");
		JLabel MinSizeFieldLabel = new JLabel("Lower apparent size limit:");
		JLabel MaxDisplacementFieldLabel = new JLabel("Max. displacement:");
		JLabel MinR2FieldLabel = new JLabel("Min. R² (Decay fitting procedure):");
		JLabel gaussienfitter2DR2Label = new JLabel("Min. R² (Gaussian fitting procedure):");

		URL expandImgURL = TrackingWindows.class.getResource("expand.jpg");
		URL deltaFImgURL = TrackingWindows.class.getResource("deltaF.jpg");
		expandFramesFieldLabel
				.setToolTipText("<html><img src=" + expandImgURL + " width=\"573\" height=\"322\"> <br></html>");
		MinSNRFieldLabel
				.setToolTipText("<html><img src=" + deltaFImgURL + " width=\"614\" height=\"394\"> <br></html>");

		JLabel minimalPointsForFitterLabel = new JLabel(" points");


		JLabel MaxTauFramesLabel = new JLabel(" frames ("
				+ String.valueOf(
						format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
				+ " " + parameters.timeUnit + ")");
		JLabel MinTauFramesLabel = new JLabel(" frames ("
				+ String.valueOf(
						format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
				+ " " + parameters.timeUnit + ")");

		JLabel MaxSizeLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(MaxSizeField.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel MinSizeLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(MinSizeField.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel MaxDisplacementLabel = new JLabel(" pixels");
		JLabel MinR2Label = new JLabel(" ");
		JLabel MinGaussR2Label = new JLabel(" ");
		JLabel MinSNRLabel = new JLabel(" dF/σ (intensity)");
		
		
		
		if (useMinSNR.isSelected()) {
			MinSNRLabel.setText(" dF/σ (intensity)");
		} else {
			MinSNRLabel.setText(" Deactivated");
		}
		
		if ((useMaxTauFrames.isSelected() || useMinTauFrames.isSelected())) {
			MaxTauFramesLabel.setText(" frames ("
					+ String.valueOf(
							format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
					+ " " + parameters.timeUnit + ")");
			MinTauFramesLabel.setText(" frames ("
					+ String.valueOf(
							format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
					+ " " + parameters.timeUnit + ")");
		} else {
			MaxTauFramesLabel.setText(" No upper limit");
			MinTauFramesLabel.setText(" No lower limit");
		}
		
		if ((useMaxSize.isSelected() || useMinSize.isSelected())) {
			MaxSizeLabel.setText(" pixels ("
					+ String.valueOf(format.format(Double.parseDouble(MaxSizeField.getText()) * parameters.pixelSize))
					+ " " + parameters.pixelUnit + ")");
			MinSizeLabel.setText(" pixels ("
					+ String.valueOf(format.format(Double.parseDouble(MinSizeField.getText()) * parameters.pixelSize))
					+ " " + parameters.pixelUnit + ")");
		} else {
			MaxSizeLabel.setText(" No upper limit");
			MinSizeLabel.setText(" No lower limit");
		}

		if (useMaxDisplacement.isSelected()) {
			MaxDisplacementLabel.setText(" pixels");
		} else {
			MaxDisplacementLabel.setText(" Deactivated");
		}

		if (useMinDecayFitterR2.isSelected()) {
			MinR2Label.setText(" ");
		} else {
			MinR2Label.setText(" Deactivated");
		}
		if (useGaussienfitter2DR2.isSelected()) {
			MinGaussR2Label.setText(" ");
		} else {
			MinGaussR2Label.setText(" Deactivated");
		}

		
		
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
		fixedExpand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fixedExpand.isSelected()) {
					parameters.fixedExpand = true;
				} else {
					parameters.fixedExpand = false;

				}
			}
		});
		useMinSNR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinSNR.isSelected()) {
					parameters.useMinSNR = true;
					MinSNRField.setEnabled(true);
					MinSNRLabel.setText(" dF/σ (intensity)");
				} else {
					MinSNRField.setEnabled(false);
					parameters.useMinSNR = false;
					MinSNRLabel.setText(" Deactivated");
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
				if ((useMaxTauFrames.isSelected() || useMinTauFrames.isSelected())) {
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} else {
					MaxTauFramesLabel.setText(" No upper limit");
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
				if ((useMaxTauFrames.isSelected() || useMinTauFrames.isSelected())) {
					
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} else {
				
					MinTauFramesLabel.setText(" No lower limit");
				}
			}
		});

		useMaxSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMaxSize.isSelected()) {
					parameters.useMaxSize = true;
					MaxSizeField.setEnabled(true);
				} else {
					MaxSizeField.setEnabled(false);
					parameters.useMaxSize = false;
				}
				if ((useMaxSize.isSelected() || useMinSize.isSelected())) {
					MaxSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MaxSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					
				} else {
					MaxSizeLabel.setText(" No upper limit");
					
				}
				
				
				
			}
		});

		useMinSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinSize.isSelected()) {
					parameters.useMinSize = true;
					MinSizeField.setEnabled(true);
				} else {
					MinSizeField.setEnabled(false);
					parameters.useMinSize = false;
				}
				if ((useMaxSize.isSelected() || useMinSize.isSelected())) {
					
					MinSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MinSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} else {
				
					MinSizeLabel.setText(" No lower limit");
				}
			}
		});
		
		useMaxDisplacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMaxDisplacement.isSelected()) {
					parameters.useMaxDisplacement = true;
					MaxDisplacementField.setEnabled(true);
					MaxDisplacementLabel.setText(" pixels");
				} else {
					MaxDisplacementField.setEnabled(false);
					parameters.useMaxDisplacement = false;
					MaxDisplacementLabel.setText(" Deactivated");
				}
			}
		});

		useMinDecayFitterR2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useMinDecayFitterR2.isSelected()) {
					parameters.useMinDecayFitterR2 = true;
					MinDecayFitterR2Field.setEnabled(true);
					MinR2Label.setText(" ");
				} else {
					MinDecayFitterR2Field.setEnabled(false);
					parameters.useMinDecayFitterR2 = false;
					MinR2Label.setText(" Deactivated");
				}
			}
		});

		useGaussienfitter2DR2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useGaussienfitter2DR2.isSelected()) {
					parameters.useGaussienfitter2DR2 = true;
					gaussienfitterR2Field.setEnabled(true);
					MinGaussR2Label.setText(" ");
				} else {
					gaussienfitterR2Field.setEnabled(false);
					parameters.useGaussienfitter2DR2 = false;
					MinGaussR2Label.setText(" Deactivated");
				}
			}
		});
		
		
		
		

//---------------------------------------------------
		
		
		

		minimalPointsForFitterField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

		        char ch = e.getKeyChar(); 
		        if(!(ch >= '1' && ch <= '9') ) {
		            e.consume();
		        } 
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
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
		expandFramesField_L.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') ) {
		            e.consume();
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		expandFramesField_L.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_L = Integer.parseInt(expandFramesField_L.getText());
					//expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					//expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_L = Integer.parseInt(expandFramesField_L.getText());
					//expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					//expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		expandFramesField_R.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') ) {
		            e.consume();
		        } 
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		expandFramesField_R.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_R = Integer.parseInt(expandFramesField_R.getText());
					//expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					//expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.expand_frames_R = Integer.parseInt(expandFramesField_R.getText());
					//expandFramesLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					//expandFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MinSNRField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MinSNRField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MinSNRField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinSNRField.getText());
		        	parameters.min_SNR =number.doubleValue();
					MinSNRLabel.setText(" σ (Intensity)");
				} catch (NumberFormatException | ParseException arg) {
					MinSNRLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinSNRField.getText());
		        	parameters.min_SNR =number.doubleValue();
					MinSNRLabel.setText(" σ (Intensity)");
				} catch (NumberFormatException | ParseException arg) {
					MinSNRLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MaxTauFramesField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MaxTauFramesField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MaxTauFramesField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {					
					Number number = format.parse(MaxTauFramesField.getText());
		        	parameters.max_tau =number.doubleValue();
					
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format( parameters.max_tau * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MaxTauFramesLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MaxTauFramesField.getText());
		        	parameters.max_tau =number.doubleValue();
					
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format( parameters.max_tau * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MaxTauFramesLabel.setText(" Invalid number");
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MinTauFramesField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MinTauFramesField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MinTauFramesField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinTauFramesField.getText());
		        	parameters.min_tau =number.doubleValue();
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(parameters.min_tau * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MinTauFramesLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinTauFramesField.getText());
		        	parameters.min_tau =number.doubleValue();
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(format.format(parameters.min_tau * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MinTauFramesLabel.setText(" Invalid number");
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MaxSizeField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MaxSizeField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MaxSizeField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MaxSizeField.getText());
		        	parameters.max_estimated_size =number.doubleValue();
					MaxSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(parameters.max_estimated_size * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MaxSizeLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MaxSizeField.getText());
		        	parameters.max_estimated_size =number.doubleValue();
					MaxSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(parameters.max_estimated_size * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MaxSizeLabel.setText(" Invalid number");
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MinSizeField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MinSizeField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MinSizeField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
	
				try {
					Number number = format.parse(MinSizeField.getText());
		        	parameters.min_estimated_size = number.doubleValue();
					MinSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(parameters.min_estimated_size * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MinSizeLabel.setText(" Invalid number");
				}

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinSizeField.getText());
		        	parameters.min_estimated_size = number.doubleValue();
					MinSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(parameters.min_estimated_size * parameters.pixelSize)) + " "
							+ parameters.pixelUnit + ")");
				} catch (NumberFormatException | ParseException arg) {
					MinSizeLabel.setText(" Invalid number");
				
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MaxDisplacementField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MaxDisplacementField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MaxDisplacementField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					
					Number number = format.parse(MaxDisplacementField.getText());
		        	parameters.MaxDisplacement = number.doubleValue();

					MaxDisplacementLabel.setText(" pixels");
				} catch (NumberFormatException | ParseException arg) {
					MaxDisplacementLabel.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					
					Number number = format.parse(MaxDisplacementField.getText());
		        	parameters.MaxDisplacement = number.doubleValue();

					MaxDisplacementLabel.setText(" pixels");
				} catch (NumberFormatException | ParseException arg) {
					MaxDisplacementLabel.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		MinDecayFitterR2Field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = MinDecayFitterR2Field.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		MinDecayFitterR2Field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinDecayFitterR2Field.getText());
		        	parameters.min_decay_fitter_r2 = number.doubleValue();
					MinR2Label.setText(" ");
				} catch (NumberFormatException | ParseException arg) {
					MinR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(MinDecayFitterR2Field.getText());
		        	parameters.min_decay_fitter_r2 = number.doubleValue();
					MinR2Label.setText(" ");
				} catch (NumberFormatException | ParseException arg) {
					MinR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}
		});
		gaussienfitterR2Field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = gaussienfitterR2Field.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && (ch != '.' && ch != ',')) {
		            e.consume();
		        } else if("".equals(text) && (ch == '.' || ch == ',')) {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		gaussienfitterR2Field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(gaussienfitterR2Field.getText());
		        	parameters.min_gaussienfitter2DR2 = number.doubleValue();
					MinGaussR2Label.setText(" ");
				} catch (NumberFormatException | ParseException arg) {
					MinGaussR2Label.setText(" Invalid number");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					Number number = format.parse(gaussienfitterR2Field.getText());
		        	parameters.min_gaussienfitter2DR2 = number.doubleValue();
					MinGaussR2Label.setText(" ");
				} catch (NumberFormatException | ParseException arg) {
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
				SaveDialog saveFileDialog = new SaveDialog("Save parameters", parameters.path, parameters.filename, ".dat");
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
				fixedExpand.setSelected(parameters.fixedExpand);
				useMinSNR.setSelected(parameters.useMinSNR);
				useMaxTauFrames.setSelected(parameters.useMaxTauFrames);
				useMinTauFrames.setSelected(parameters.useMinTauFrames);
				useMaxSize.setSelected(parameters.useMaxSize);
				useMinSize.setSelected(parameters.useMinSize);
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
				MaxSizeField.setEnabled(parameters.useMaxSize);
				MinSizeField.setEnabled(parameters.useMinSize);
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
				MaxSizeField.setText(String.valueOf(format.format(parameters.max_estimated_size)));
				MinSizeField.setText(String.valueOf(format.format(parameters.min_estimated_size)));
				MaxDisplacementField.setText(String.valueOf(format.format(parameters.MaxDisplacement)));
				MinDecayFitterR2Field.setText(String.valueOf(format.format(parameters.min_decay_fitter_r2)));
				gaussienfitterR2Field.setText(String.valueOf(format.format(parameters.min_gaussienfitter2DR2)));
				
				if (useMinSNR.isSelected()) {
					MinSNRLabel.setText(" dF/σ (intensity)");
				} else {
					MinSNRLabel.setText(" No filtration of dF/σ (intensity)");
				}
				
				if ((useMaxTauFrames.isSelected() || useMinTauFrames.isSelected())) {
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} else {
					MaxTauFramesLabel.setText(" No filtration of Decay time");
					MinTauFramesLabel.setText(" No filtration of Decay time");
				}
				
				if ((useMaxSize.isSelected() || useMinSize.isSelected())) {
					MaxSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MaxSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					MinSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MinSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} else {
					MaxSizeLabel.setText(" No estimation of FWHM (apparent size)");
					MinSizeLabel.setText(" No estimation of FWHM (apparent size)");
				}

				if (useMaxDisplacement.isSelected()) {
					MaxDisplacementLabel.setText(" pixels");
				} else {
					MaxDisplacementLabel.setText(" No filtration of Max. Displacement");
				}

				if (useMinDecayFitterR2.isSelected()) {
					MinR2Label.setText(" ");
				} else {
					MinR2Label.setText(" No filtration of Decay fit R²");
				}
				if (useGaussienfitter2DR2.isSelected()) {
					MinGaussR2Label.setText(" ");
				} else {
					MinGaussR2Label.setText(" No filtration of morphology fit R²");
				}

				

			}
		});
		restoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				parameters.InitialSecretionParameters();

				useMinimalPointsForFitter.setSelected(parameters.useMinimalPointsForFitter);
				useExpandFrames.setSelected(parameters.useExpandFrames);
				fixedExpand.setSelected(parameters.fixedExpand);
				useMinSNR.setSelected(parameters.useMinSNR);
				useMaxTauFrames.setSelected(parameters.useMaxTauFrames);
				useMinTauFrames.setSelected(parameters.useMinTauFrames);
				useMaxSize.setSelected(parameters.useMaxSize);
				useMinSize.setSelected(parameters.useMinSize);
				useMaxDisplacement.setSelected(parameters.useMaxDisplacement);
				useMinDecayFitterR2.setSelected(parameters.useMinDecayFitterR2);
				useGaussienfitter2DR2.setSelected(parameters.useGaussienfitter2DR2);

				minimalPointsForFitterField.setEnabled(parameters.useMinimalPointsForFitter);
				expandFramesField_L.setEnabled(parameters.useExpandFrames);
				expandFramesField_R.setEnabled(parameters.useExpandFrames);
				MinSNRField.setEnabled(parameters.useMinSNR);
				MaxTauFramesField.setEnabled(parameters.useMaxTauFrames);
				MinTauFramesField.setEnabled(parameters.useMinTauFrames);
				MaxSizeField.setEnabled(parameters.useMaxSize);
				MinSizeField.setEnabled(parameters.useMinSize);
				MaxDisplacementField.setEnabled(parameters.useMaxDisplacement);
				MinDecayFitterR2Field.setEnabled(parameters.useMinDecayFitterR2);
				gaussienfitterR2Field.setEnabled(parameters.useGaussienfitter2DR2);

				minimalPointsForFitterField.setText(String.valueOf(parameters.minimalPointsForFitter));
				expandFramesField_L.setText(String.valueOf(parameters.expand_frames_L));
				expandFramesField_R.setText(String.valueOf(parameters.expand_frames_R));
				MinSNRField.setText(String.valueOf(format.format(parameters.min_SNR)));
				MaxTauFramesField.setText(String.valueOf(format.format(parameters.max_tau)));
				MinTauFramesField.setText(String.valueOf(format.format(parameters.min_tau)));
				MaxSizeField.setText(String.valueOf(format.format(parameters.max_estimated_size)));
				MinSizeField.setText(String.valueOf(format.format(parameters.min_estimated_size)));
				MaxDisplacementField.setText(String.valueOf(format.format(parameters.MaxDisplacement)));
				MinDecayFitterR2Field.setText(String.valueOf(format.format(parameters.min_decay_fitter_r2)));
				gaussienfitterR2Field.setText(String.valueOf(format.format(parameters.min_gaussienfitter2DR2)));
				
				if (useMinSNR.isSelected()) {
					MinSNRLabel.setText(" dF/σ (intensity)");
				} else {
					MinSNRLabel.setText(" No filtration of dF/σ (intensity)");
				}
				
				if ((useMaxTauFrames.isSelected() || useMinTauFrames.isSelected())) {
					MaxTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MaxTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
					MinTauFramesLabel.setText(" frames ("
							+ String.valueOf(
									format.format(Double.parseDouble(MinTauFramesField.getText()) * parameters.timeInterval))
							+ " " + parameters.timeUnit + ")");
				} else {
					MaxTauFramesLabel.setText(" No filtration of Decay time");
					MinTauFramesLabel.setText(" No filtration of Decay time");
				}
				
				if ((useMaxSize.isSelected() || useMinSize.isSelected())) {
					MaxSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MaxSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					MinSizeLabel.setText(" pixels ("
							+ String.valueOf(format.format(Double.parseDouble(MinSizeField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} else {
					MaxSizeLabel.setText(" No estimation of FWHM (apparent size)");
					MinSizeLabel.setText(" No estimation of FWHM (apparent size)");
				}

				if (useMaxDisplacement.isSelected()) {
					MaxDisplacementLabel.setText(" pixels");
				} else {
					MaxDisplacementLabel.setText(" No filtration of Max. Displacement");
				}

				if (useMinDecayFitterR2.isSelected()) {
					MinR2Label.setText(" ");
				} else {
					MinR2Label.setText(" No filtration of Decay fit R²");
				}
				if (useGaussienfitter2DR2.isSelected()) {
					MinGaussR2Label.setText(" ");
				} else {
					MinGaussR2Label.setText(" No filtration of morphology fit R²");
				}

				

			}
		});
		
		
		Panel ParametresPanel1 = new Panel();
		ParametresPanel1.setLayout(new MigLayout("", "[][][][]", "[][]"));

		ParametresPanel1.add(useMinimalPointsForFitter, "cell 0 1");
		ParametresPanel1.add(minimalPointsForFitterFieldLabel, "cell 1 1");
		ParametresPanel1.add(minimalPointsForFitterField, "cell 2 1");
		ParametresPanel1.add(minimalPointsForFitterLabel, "cell 4 1");
		
		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 2");
		
		ParametresPanel1.add(useExpandFrames, "cell 0 3");
		ParametresPanel1.add(expandFramesFieldLabel, "cell 1 3");
		ParametresPanel1.add(expandFramesField_L, "cell 2 3");
		ParametresPanel1.add(expandFramesField_R, "cell 3 3");
		ParametresPanel1.add(fixedExpand, "cell 4 3");
		
		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 4");
		
		ParametresPanel1.add(useMinSNR, "cell 0 5");
		ParametresPanel1.add(MinSNRFieldLabel, "cell 1 5");
		ParametresPanel1.add(MinSNRField, "cell 2 5");
		ParametresPanel1.add(MinSNRLabel, "cell 4 5");

		
		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 6");
		
		ParametresPanel1.add(useMaxTauFrames, "cell 0 7");
		ParametresPanel1.add(MaxTauFramesFieldLabel, "cell 1 7");
		ParametresPanel1.add(MaxTauFramesField, "cell 2 7");
		ParametresPanel1.add(MaxTauFramesLabel, "cell 4 7");

		ParametresPanel1.add(useMinTauFrames, "cell 0 8");
		ParametresPanel1.add(MinTauFramesFieldLabel, "cell 1 8");
		ParametresPanel1.add(MinTauFramesField, "cell 2 8");
		ParametresPanel1.add(MinTauFramesLabel, "cell 4 8");
		
		
		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 9");

		ParametresPanel1.add(useMaxSize, "cell 0 10");
		ParametresPanel1.add(MaxSizeFieldLabel, "cell 1 10");
		ParametresPanel1.add(MaxSizeField, "cell 2 10");
		ParametresPanel1.add(MaxSizeLabel, "cell 4 10");

		ParametresPanel1.add(useMinSize, "cell 0 11");
		ParametresPanel1.add(MinSizeFieldLabel, "cell 1 11");
		ParametresPanel1.add(MinSizeField, "cell 2 11");
		ParametresPanel1.add(MinSizeLabel, "cell 4 11");
		
		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 12");

		ParametresPanel1.add(useMaxDisplacement, "cell 0 13");
		ParametresPanel1.add(MaxDisplacementFieldLabel, "cell 1 13");
		ParametresPanel1.add(MaxDisplacementField, "cell 2 13");
		ParametresPanel1.add(MaxDisplacementLabel, "cell 4 13");

		ParametresPanel1.add(new JLabel("----------------------------"),"cell 1 14");
		
		ParametresPanel1.add(useMinDecayFitterR2, "cell 0 15");
		ParametresPanel1.add(MinR2FieldLabel, "cell 1 15");
		ParametresPanel1.add(MinDecayFitterR2Field, "cell 2 15");
		ParametresPanel1.add(MinR2Label, "cell 4 15");
		
		ParametresPanel1.add(useGaussienfitter2DR2, "cell 0 16");
		ParametresPanel1.add(gaussienfitter2DR2Label, "cell 1 16");
		ParametresPanel1.add(gaussienfitterR2Field, "cell 2 16");
		ParametresPanel1.add(MinGaussR2Label, "cell 4 16");
		
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
