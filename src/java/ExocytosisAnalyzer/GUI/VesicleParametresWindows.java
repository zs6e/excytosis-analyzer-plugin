package ExocytosisAnalyzer.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.process.FloatProcessor;
import net.miginfocom.swing.MigLayout;

import ExocytosisAnalyzer.datasets.Parameters;
import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.detection.LocalMaximaDetector;
import ExocytosisAnalyzer.detection.MicroWaveletFilter;

public class VesicleParametresWindows extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int image_width, image_height;
	private DecimalFormat format = new DecimalFormat("#0.000#");

	public JTextField IntervalField;
	public JTextField IntervalUnit;
	public JTextField framerate;

	public JTextField pixelSiezField;
	public JTextField pixelSiezUnitField;

	public JTextField radiusField_pixel;
	public JTextField maxRadiusField_pixel;
	public JTextField SNR_VesicleField;

	public JCheckBox WaveletFilter;
	public JCheckBox lowpass;

	private Roi roi;
	public Vector<Vesicle> detected_vesicles;

	public VesicleParametresWindows(Parameters parameters) {
		image_width = parameters.image_width;
		image_height = parameters.image_height;
		roi = new Roi(0, 0, image_width, image_height);

		detected_vesicles = new Vector<Vesicle>();

		// to simplify the calcule, we fix the unit to "second"
		

		/*******************************/

		Border titleBorder1 = BorderFactory.createTitledBorder("Set Parameters for Vesicle Detection");

		JLabel pixelSiezFieldLable = new JLabel("Pixel size: ");
		JLabel IntervalFieldLabel = new JLabel("Frame time interval:");
		JLabel framerateLabel = new JLabel("Frame rate:");

		JLabel radiusFieldLabel = new JLabel("Min vesicle radius: ");
		JLabel maxRadiusFieldLabel = new JLabel("Max vesicle radius: ");
		JLabel SNR_VesicleFieldLabel = new JLabel("Dectection threshold: ");
		JLabel useWaveletFilterLabel = new JLabel("Use wavelet filter");
		JLabel showlowpassLabel = new JLabel("Show wavelet lowpass image");

		WaveletFilter = new JCheckBox("", parameters.WaveletFilter);
		lowpass = new JCheckBox("", parameters.lowpass);

		pixelSiezField = new JTextField(String.valueOf(parameters.pixelSize), 6);
		IntervalField = new JTextField(String.valueOf(format.format(parameters.timeInterval)), 6);
		IntervalUnit = new JTextField(parameters.timeUnit);
		framerate = new JTextField(String.valueOf(format.format(parameters.framerate)), 6);

		radiusField_pixel = new JTextField(String.valueOf(parameters.minRadius), 6);
		maxRadiusField_pixel = new JTextField(String.valueOf(parameters.maxRadius), 6);
		SNR_VesicleField = new JTextField(String.valueOf(format.format(parameters.SNR_Vesicle)), 6);

		pixelSiezUnitField = new JTextField(parameters.pixelUnit);

		JLabel intervalUnitLabel = new JLabel(" sec");
		JLabel framerateUnitLabel = new JLabel(" Hz");
		JLabel SNR_VesicleLabel = new JLabel(" σ (wavelet scale)");

		JLabel radiusJLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel maxRadiusJLabel = new JLabel(" pixels ("
				+ String.valueOf(
						format.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");

		URL radiusImgURL = VesicleParametresWindows.class.getResource("radius.jpg");
		URL sensitivityImgURL = VesicleParametresWindows.class.getResource("Threshold.jpg");
		radiusFieldLabel
				.setToolTipText("<html><img src=" + radiusImgURL + " width=\"434\" height=\"302\"> <br></html>");
		maxRadiusFieldLabel
				.setToolTipText("<html><img src=" + radiusImgURL + " width=\"434\" height=\"302\"> <br></html>");
		SNR_VesicleFieldLabel
				.setToolTipText("<html><img src=" + sensitivityImgURL + " width=\"490\" height=\"434\"> <br></html>");


		// thresholdField.setEnabled(false);
		WaveletFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (WaveletFilter.isSelected()) {
					lowpass.setEnabled(true);
					showlowpassLabel.setEnabled(true);

				} else {
					lowpass.setEnabled(false);
					showlowpassLabel.setEnabled(false);
				}
			}
		});
		

		pixelSiezField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = pixelSiezField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && ch != '.' && ch != ',') {
		            e.consume();
		        } else if("".equals(text) && ch == '.' && ch != ',') {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
		        try {
					parameters.pixelSize = Double.parseDouble(pixelSiezField.getText());
					if (parameters.pixelSize <= 0) {
						parameters.pixelSize = 1;
					}
					radiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					maxRadiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
				}
			
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.pixelSize = Double.parseDouble(pixelSiezField.getText());
					if (parameters.pixelSize <= 0) {
						parameters.pixelSize = 1;
					}
					radiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					maxRadiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
				}
			}
		});

		pixelSiezUnitField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				radiusJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxRadiusJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				radiusJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxRadiusJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				radiusJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxRadiusJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
			}
		});
		

		IntervalField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				String text = IntervalField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && ch != '.' && ch != ',') {
		            e.consume();
		        } else if("".equals(text) && ch == '.' && ch != ',') {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
		        try {
					parameters.timeInterval = Double.parseDouble(IntervalField.getText());
					if (parameters.timeInterval <= 0) {
						parameters.timeInterval = 1;
					}
					parameters.framerate = 1.00 / parameters.pixelSize;
					framerate.setText(String.valueOf(format.format(1.00 / parameters.timeInterval)));
				} catch (NumberFormatException arg) {

				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				try {
					parameters.timeInterval = Double.parseDouble(IntervalField.getText());
					if (parameters.timeInterval <= 0) {
						parameters.timeInterval = 1;
					}
					parameters.framerate = 1.00 / parameters.pixelSize;
					framerate.setText(String.valueOf(format.format(1.00 / parameters.timeInterval)));
				} catch (NumberFormatException arg) {

				}
			}
		});

		framerate.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = framerate.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && ch != '.' && ch != ',') {
		            e.consume();
		        } else if("".equals(text) && ch == '.' && ch != ',') {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }

				try {
					parameters.framerate = Double.parseDouble(framerate.getText());
					if (parameters.framerate <= 0) {
						parameters.framerate = 1;
					}
					parameters.timeInterval = 1.00 / parameters.framerate;
					IntervalField.setText(String.valueOf(format.format(parameters.timeInterval)));
				} catch (NumberFormatException arg) {

				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				try {
					parameters.framerate = Double.parseDouble(framerate.getText());
					if (parameters.framerate <= 0) {
						parameters.framerate = 1;
					}
					parameters.timeInterval = 1.00 / parameters.framerate;
					IntervalField.setText(String.valueOf(format.format(parameters.timeInterval)));
				} catch (NumberFormatException arg) {

				}
			}

		});

		IntervalUnit.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				try {
					parameters.timeUnit = IntervalUnit.getText();

				} catch (NumberFormatException arg) {

				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					parameters.timeUnit = IntervalUnit.getText();
				} catch (NumberFormatException arg) {
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					parameters.timeUnit = IntervalUnit.getText();
				} catch (NumberFormatException arg) {
				}
			}
		});

		radiusField_pixel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(! (ch >= '0' && ch <= '9') ){
		            e.consume();
		        }
				try {
					parameters.minRadius = Integer.parseInt(radiusField_pixel.getText());
					radiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					radiusJLabel.setText(" Invalid number");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.minRadius = Integer.parseInt(radiusField_pixel.getText());
					radiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(radiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
						if (parameters.minRadius < 1) {
							parameters.minRadius = 1;
							radiusField_pixel.setText("1");
						}
						if (parameters.minRadius > 20) {
							parameters.minRadius = 20;
							radiusField_pixel.setText("20");
						};
					
						
				} catch (NumberFormatException arg) {
					radiusJLabel.setText(" Invalid number");
				}
			}
		});

		maxRadiusField_pixel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') ) {
		            e.consume();
		        }
				try {
					parameters.maxRadius = Integer.parseInt(maxRadiusField_pixel.getText());
					maxRadiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					maxRadiusJLabel.setText(" Invalid number");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.maxRadius = Integer.parseInt(maxRadiusField_pixel.getText());
					maxRadiusJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxRadiusField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					if (parameters.maxRadius < parameters.minRadius) {
						parameters.maxRadius = parameters.minRadius + 1;
						radiusField_pixel.setText(String.valueOf(parameters.minRadius + 1));
					}
					if (parameters.maxRadius > 25) {
						parameters.maxRadius = 25;
						radiusField_pixel.setText("25");
					};
				} catch (NumberFormatException arg) {
					maxRadiusJLabel.setText(" Invalid number");
				}
			}
		});

		SNR_VesicleField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = SNR_VesicleField.getText(); 
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') && ch != '.' && ch != ',') {
		            e.consume();
		        } else if("".equals(text) && ch == '.' && ch != ',') {   
		            e.consume();
		        } else if(text.contains(".") || text.contains(",") ){
		            if(ch == '.'||ch == ',' ) {
		                e.consume();
		            }
		        }
				try {
					parameters.SNR_Vesicle = Double.parseDouble(SNR_VesicleField.getText());

				} catch (NumberFormatException arg) {

				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.SNR_Vesicle = Double.parseDouble(SNR_VesicleField.getText());
				} catch (NumberFormatException arg) {
				}
			}

		});

		WaveletFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (WaveletFilter.isSelected()) {
					parameters.WaveletFilter = true;
					SNR_VesicleLabel.setText(" σ (wavelet scale)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Wavelet scale): ");
				
				}
				else {
					parameters.WaveletFilter = false;
					SNR_VesicleLabel.setText(" σ (Intensity)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Intensity): ");
				}

			}
		});

		lowpass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lowpass.isSelected())
					parameters.lowpass = true;
				else
					parameters.lowpass = false;
			}
		});
		
		

		JPanel ParametresPanel1 = new JPanel();
		ParametresPanel1.setBorder(titleBorder1);
		ParametresPanel1.setLayout(new MigLayout("", "[][][][]", "[][]"));

		ParametresPanel1.add(pixelSiezFieldLable, "cell 1 0");
		ParametresPanel1.add(pixelSiezField, "cell 2 0");
		ParametresPanel1.add(pixelSiezUnitField, "cell 3 0");

		ParametresPanel1.add(IntervalFieldLabel, "cell 1 1");
		ParametresPanel1.add(IntervalField, "cell 2 1");
		ParametresPanel1.add(intervalUnitLabel, "cell 3 1");

		ParametresPanel1.add(framerateLabel, "cell 1 2");
		ParametresPanel1.add(framerate, "cell 2 2");
		ParametresPanel1.add(framerateUnitLabel, "cell 3 2");

		ParametresPanel1.add(radiusFieldLabel, "cell 1 3");
		ParametresPanel1.add(radiusField_pixel, "cell 2 3");
		ParametresPanel1.add(radiusJLabel, "cell 3 3");

		ParametresPanel1.add(maxRadiusFieldLabel, "cell 1 4");
		ParametresPanel1.add(maxRadiusField_pixel, "cell 2 4");
		ParametresPanel1.add(maxRadiusJLabel, "cell 3 4");

		ParametresPanel1.add(SNR_VesicleFieldLabel, "cell 1 5");
		ParametresPanel1.add(SNR_VesicleField, "cell 2 5");
		ParametresPanel1.add(SNR_VesicleLabel, "cell 3 5");

		ParametresPanel1.add(WaveletFilter, "cell 0 7");
		ParametresPanel1.add(useWaveletFilterLabel, "cell 1 7");

		ParametresPanel1.add(lowpass, "cell 0 8");
		ParametresPanel1.add(showlowpassLabel, "cell 1 8");
		
		
		
		
		/**********************/

		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton restoreButton = new JButton("Restore");

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				SaveDialog saveFileDialog = new SaveDialog("Save parasmeters", parameters.path, parameters.filename,
						".dat");
				String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
				File file = new File(saveFilePatch);
				Parameters savedparas = parameters;
				savedparas.InitialTrackingParameters();
				savedparas.InitialSecretionParameters();
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

				pixelSiezField.setText(String.valueOf(parameters.pixelSize));
				IntervalField.setText(String.valueOf(parameters.timeInterval));
				IntervalUnit.setText(String.valueOf(parameters.timeUnit));
				framerate.setText(String.valueOf(parameters.framerate));
				pixelSiezUnitField.setText(parameters.pixelUnit);

				radiusField_pixel.setText(String.valueOf(parameters.minRadius));
				maxRadiusField_pixel.setText(String.valueOf(parameters.maxRadius));
				SNR_VesicleField.setText(String.valueOf(parameters.SNR_Vesicle));

				WaveletFilter.setSelected(parameters.WaveletFilter);
				lowpass.setSelected(parameters.lowpass);
				if (WaveletFilter.isSelected()) {
					SNR_VesicleLabel.setText(" σ (wavelet scale)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Wavelet scale): ");
				
				}
				else {
					SNR_VesicleLabel.setText(" σ (Intensity)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Intensity): ");
				}

			}
		});
		restoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parameters.InitialVesicleParametres(ExocytosisAnalyzer.GUI.GUIWizard.origin);

				pixelSiezField.setText(String.valueOf(parameters.pixelSize));
				IntervalField.setText(String.valueOf(parameters.timeInterval));
				IntervalUnit.setText(String.valueOf(parameters.timeUnit));
				pixelSiezUnitField.setText(parameters.pixelUnit);

				radiusField_pixel.setText(String.valueOf(parameters.minRadius));
				maxRadiusField_pixel.setText(String.valueOf(parameters.maxRadius));
				SNR_VesicleField.setText(String.valueOf(parameters.SNR_Vesicle));
				

				WaveletFilter.setSelected(parameters.WaveletFilter);
				lowpass.setSelected(parameters.lowpass);
				if (WaveletFilter.isSelected()) {
					SNR_VesicleLabel.setText(" σ (wavelet scale)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Wavelet scale): ");
				
				}
				else {
					SNR_VesicleLabel.setText(" σ (Intensity)");
					//SNR_VesicleFieldLabel.setText("Dectection threshold (Intensity): ");
				}
				
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

	public void setRoi(Roi aRoi) {
		roi = aRoi;
	}

	public Vector<Vesicle> preview_Detection(ImagePlus in, Parameters parameters) {
		int num_of_slices = in.getStackSize();
		Vector<Vesicle> preview_detected_vesicle = new Vector<Vesicle>();

		MicroWaveletFilter waveletFilter = new MicroWaveletFilter(in, parameters.scaleList, parameters.SNR_Vesicle);
		LocalMaximaDetector myDetector = new LocalMaximaDetector(in, parameters);
		myDetector.setRoi(roi);
		if (WaveletFilter.isSelected()) {
			if (lowpass.isSelected()) {
				float[] lowPass;
				ImageStack LowPassStack = new ImageStack(image_width, image_height);

				for (int slice = 1; slice <= num_of_slices; slice++) {
					lowPass = waveletFilter.filter(in.getStack().getProcessor(slice));
					preview_detected_vesicle = myDetector.FindLocalMaxima(lowPass, slice, 1);
					LowPassStack.addSlice(new FloatProcessor(image_width, image_height, lowPass));

				}
				ImagePlus lowPassImage = new ImagePlus();
				lowPassImage.setStack(LowPassStack);
				lowPassImage.setTitle("Wavelet Low pass filter");
				lowPassImage.show();
			} else {
				float[] lowPass;
				for (int slice = 1; slice <= num_of_slices; slice++) {
					lowPass = waveletFilter.filter(in.getStack().getProcessor(slice));
					preview_detected_vesicle = myDetector.FindLocalMaxima(lowPass, slice, 1);
				}
			}
		} else {
			float[] normalized_float_img;
			for (int slice = 1; slice <= num_of_slices; slice++) {
				normalized_float_img = myDetector.getNormalizedImage(in.getStack().getProcessor(slice));
				final float threshold = myDetector.CalculateThreshold(normalized_float_img);
				preview_detected_vesicle = myDetector.FindLocalMaxima(normalized_float_img, slice, threshold);
				// progress.setValue((int) slice*100/num_of_slices);
			}
		}

		return preview_detected_vesicle;
	}

}
