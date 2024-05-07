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

	public JTextField sizeField_pixel;
	public JTextField maxSizeField_pixel;
	public JTextField SNR_VesicleField;

	public JTextField TheoreticalResolutionField;
	
	public JCheckBox WaveletFilter;
	public JCheckBox lowpass;
	public JCheckBox TheoreticalResolutionCbox;
	

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

		JLabel sizeFieldLabel = new JLabel("Min vesicle apparent size: ");
		JLabel maxSizeFieldLabel = new JLabel("Max vesicle apparent size: ");
		JLabel SNR_VesicleFieldLabel = new JLabel("Dectection threshold: ");
		JLabel useWaveletFilterLabel = new JLabel("Use wavelet filter");
		JLabel showlowpassLabel = new JLabel("Show wavelet lowpass image");

		JLabel TheoreticalResolution  = new JLabel("Theoretical Resolution (if any): ");
		
		
		TheoreticalResolutionCbox= new JCheckBox("", parameters.useTheoreticalResolution);
		WaveletFilter = new JCheckBox("", parameters.WaveletFilter);
		lowpass = new JCheckBox("", parameters.lowpass);
		
		
		pixelSiezField = new JTextField(String.valueOf(parameters.pixelSize), 6);
		IntervalField = new JTextField(String.valueOf(format.format(parameters.timeInterval)), 6);
		IntervalUnit = new JTextField(parameters.timeUnit);
		framerate = new JTextField(String.valueOf(format.format(parameters.framerate)), 6);

		sizeField_pixel = new JTextField(String.valueOf(parameters.minSize), 6);
		maxSizeField_pixel = new JTextField(String.valueOf(parameters.maxSize), 6);
		SNR_VesicleField = new JTextField(String.valueOf(format.format(parameters.SNR_Vesicle)), 6);

		pixelSiezUnitField = new JTextField(parameters.pixelUnit);
		TheoreticalResolutionField = new JTextField(String.valueOf(format.format(parameters.TheoreticalResolution)), 6);
		TheoreticalResolutionField.setEnabled(parameters.useTheoreticalResolution);
		
		JLabel intervalUnitLabel = new JLabel(" sec");
		JLabel framerateUnitLabel = new JLabel(" Hz");
		JLabel SNR_VesicleLabel = new JLabel(" σ (wavelet scale)");
		JLabel pixelsizeUnitlable = new JLabel(parameters.pixelUnit);

		JLabel sizeJLabel = new JLabel(" pixels ("
				+ String.valueOf(format.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");
		JLabel maxSizeJLabel = new JLabel(" pixels ("
				+ String.valueOf(
						format.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
				+ " " + parameters.pixelUnit + ")");

		URL sizeImgURL = VesicleParametresWindows.class.getResource("radius.jpg");
		URL sensitivityImgURL = VesicleParametresWindows.class.getResource("Threshold.jpg");
		sizeFieldLabel
				.setToolTipText("<html><img src=" + sizeImgURL + " width=\"434\" height=\"302\"> <br></html>");
		maxSizeFieldLabel
				.setToolTipText("<html><img src=" + sizeImgURL + " width=\"434\" height=\"302\"> <br></html>");
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
					sizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					maxSizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
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
					sizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					maxSizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
				}
			}
		});

		pixelSiezUnitField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				sizeJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxSizeJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				pixelsizeUnitlable.setText(" " + pixelSiezUnitField.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				sizeJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxSizeJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				parameters.pixelUnit = pixelSiezUnitField.getText();
				sizeJLabel.setText(" pixels ("
						+ String.valueOf(
								format.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
						+ " " + parameters.pixelUnit + ")");
				maxSizeJLabel.setText(" pixels ("
						+ String.valueOf(format
								.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
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

		sizeField_pixel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(! (ch >= '0' && ch <= '9') ){
		            e.consume();
		        }
				try {
					parameters.minSize = Integer.parseInt(sizeField_pixel.getText());
					sizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					
					
				} catch (NumberFormatException arg) {
					sizeJLabel.setText(" Invalid number");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
						parameters.minSize = Integer.parseInt(sizeField_pixel.getText());
						sizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(sizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
						if (parameters.minSize < 1) {
							parameters.minSize = 1;
							sizeField_pixel.setText("1");
						}
						if (parameters.minSize > 25) {
							parameters.minSize = 25;
							sizeField_pixel.setText("25");
						};
						parameters.setScaleList(parameters.minSize,parameters.maxSize);
						
				} catch (NumberFormatException arg) {
					sizeJLabel.setText(" Invalid number");
				}
			}
		});

		maxSizeField_pixel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '0' && ch <= '9') ) {
		            e.consume();
		        }
				try {
					parameters.maxSize = Integer.parseInt(maxSizeField_pixel.getText());
					maxSizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
				} catch (NumberFormatException arg) {
					maxSizeJLabel.setText(" Invalid number");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.maxSize = Integer.parseInt(maxSizeField_pixel.getText());
					maxSizeJLabel.setText(" pixels ("
							+ String.valueOf(format
									.format(Double.parseDouble(maxSizeField_pixel.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit + ")");
					if (parameters.maxSize < parameters.minSize) {
						parameters.maxSize = parameters.minSize;
						sizeField_pixel.setText(String.valueOf(parameters.minSize));
					}
					if (parameters.maxSize > 25) {
						parameters.maxSize = 25;
						maxSizeField_pixel.setText("25");
					};
					parameters.setScaleList(parameters.minSize,parameters.maxSize);
				} catch (NumberFormatException arg) {
					maxSizeJLabel.setText(" Invalid number");
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

		TheoreticalResolutionField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = TheoreticalResolutionField.getText(); 
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
					parameters.TheoreticalResolution = Double.parseDouble(TheoreticalResolutionField.getText());

				} catch (NumberFormatException arg) {

				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.TheoreticalResolution = Double.parseDouble(TheoreticalResolutionField.getText());
				} catch (NumberFormatException arg) {
				}
			}

		});
		
		
		TheoreticalResolutionCbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TheoreticalResolutionCbox.isSelected()) {
					parameters.useTheoreticalResolution = true;
				    TheoreticalResolutionField.setEnabled(true);
				}
				else {
					parameters.useTheoreticalResolution = false;
				    TheoreticalResolutionField.setEnabled(false);
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

		ParametresPanel1.add(sizeFieldLabel, "cell 1 3");
		ParametresPanel1.add(sizeField_pixel, "cell 2 3");
		ParametresPanel1.add(sizeJLabel, "cell 3 3");

		ParametresPanel1.add(maxSizeFieldLabel, "cell 1 4");
		ParametresPanel1.add(maxSizeField_pixel, "cell 2 4");
		ParametresPanel1.add(maxSizeJLabel, "cell 3 4");

		ParametresPanel1.add(SNR_VesicleFieldLabel, "cell 1 5");
		ParametresPanel1.add(SNR_VesicleField, "cell 2 5");
		ParametresPanel1.add(SNR_VesicleLabel, "cell 3 5");

		
		ParametresPanel1.add(TheoreticalResolutionCbox, "cell 0 7");
		ParametresPanel1.add(TheoreticalResolution, "cell 1 7");
		ParametresPanel1.add(TheoreticalResolutionField, "cell 2 7");
		ParametresPanel1.add(pixelsizeUnitlable, "cell 3 7");
		
				
		ParametresPanel1.add(WaveletFilter, "cell 0 8");
		ParametresPanel1.add(useWaveletFilterLabel, "cell 1 8");

		ParametresPanel1.add(lowpass, "cell 0 9");
		ParametresPanel1.add(showlowpassLabel, "cell 1 9");
		
		
		
		/**********************/

		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton restoreButton = new JButton("Restore");

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				SaveDialog saveFileDialog = new SaveDialog("Save parameters", parameters.path, parameters.filename,
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

				sizeField_pixel.setText(String.valueOf(parameters.minSize));
				maxSizeField_pixel.setText(String.valueOf(parameters.maxSize));
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

				sizeField_pixel.setText(String.valueOf(parameters.minSize));
				maxSizeField_pixel.setText(String.valueOf(parameters.maxSize));
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
