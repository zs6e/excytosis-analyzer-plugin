package ExocytosisAnalyzer.GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import ExocytosisAnalyzer.datasets.SecretionParametres;
import ExocytosisAnalyzer.datasets.SecretionParametresConfig;
import ExocytosisAnalyzer.datasets.VesicleParametres;
import ExocytosisAnalyzer.detection.LocalMaximaDetector;
import ExocytosisAnalyzer.detection.SecretionDetector;
import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.datasets.Secretion;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.gui.Plot;
import ij.io.SaveDialog;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import net.miginfocom.swing.MigLayout;

public class My_Gui {
	// image property members

	ImagePlus image;  //film Original
	private ImagePlus output_ips;  //results film
	private Calibration imageCalibration;
	private double pixelSize;
	private String pixelUnit;
	private double timeInterval;
	private String timeUnit;
	private DecimalFormat format = new DecimalFormat("#.0#");
	private VesicleParametres v_paras; //detection parameters for vesicles
	private SecretionParametres s_paras; //detection parameters for secretion
	private SecretionParametresConfig s_paras_config;
		
	//detection result 
	private Vector<Vesicle> detected_vesicles = new Vector<Vesicle>();
	protected Vector<Secretion> detected_vesicle_seqs; 
	private Vector<Secretion> detected_vesicle_seqs_clone = new Vector<Secretion>();
	//private Vector<Secretion> detected_secretion = new Vector<Secretion>();
	private DefaultTableModel result_dtm; //result table model
	private ResultTable secretionEventList; //result table
		
	//for adding a secretion
	private int[] x_corr_to_add; 
	private int[] y_corr_to_add; 
	
	
	public My_Gui(ImagePlus imp) {
		//read film and its propriety
		image = imp;
		imageCalibration = imp.getCalibration();
		pixelSize = imageCalibration.pixelWidth;
		if (pixelSize == 0) pixelSize = 1;
		pixelUnit = imageCalibration.getUnits();
		timeInterval = imageCalibration.frameInterval;
		if (timeInterval == 0) timeInterval = 1;
		timeUnit = imageCalibration.getTimeUnit();
	}
		

	public class VesicleParametresWindows {

		public VesicleParametresWindows() {
 
		    JFrame parametresWindows=new JFrame("Vesicle parametres");
				
		    JLabel pixelSiezFieldLable = new JLabel("Pixel size: ");
		    JLabel radiusFieldLabel = new JLabel("Minimal Vesicule Radius (1-25 pixels): ");
			JLabel maxRadiusFieldLabel = new JLabel("Maximal Vesicule Radius (1-25 pixels):");
			JLabel thresholdFieldLabel = new JLabel("Threshold intensity (0-100%):");
			JLabel sensitivityFieldLabel = new JLabel("Sensitivity (1-200):");   
			JCheckBox WaveletFilter = new JCheckBox("Use MicroWavelet Filter",true);
			JCheckBox lowpass = new JCheckBox("Show lowpass image",false);
			
			JTextField pixelSiezField = new JTextField(String.valueOf(pixelSize), 5);
		    JTextField radiusField_pixel = new JTextField("3", 5);
		    JTextField maxRadiusField_pixel = new JTextField("5", 5);
		    JTextField thresholdField = new JTextField("0.5", 5);
		    JTextField sensitivityField = new JTextField("50", 5);
		    
		    JTextField pixelSiezLable = new JTextField(pixelUnit);
		    JLabel radiusJLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
		    JLabel maxRadiusJLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
		    JLabel thresholdJLabel = new JLabel(" Invalid if use Wavelet");
		    JLabel sensitivityJLabel = new JLabel(" Wavelet sensitivity");
			
			
			Panel ParametresPanel1= new Panel();
			ParametresPanel1.setLayout(new MigLayout("", "[][][]", "[][]")); 
			
			ParametresPanel1.add(pixelSiezFieldLable, "cell 0 0");
			ParametresPanel1.add(pixelSiezField, "cell 1 0");
			ParametresPanel1.add(pixelSiezLable, "cell 2 0");
			
			ParametresPanel1.add(radiusFieldLabel, "cell 0 1");
			ParametresPanel1.add(radiusField_pixel, "cell 1 1");
			ParametresPanel1.add(radiusJLabel, "cell 2 1");
			
			ParametresPanel1.add(maxRadiusFieldLabel, "cell 0 2");
			ParametresPanel1.add(maxRadiusField_pixel, "cell 1 2");
			ParametresPanel1.add(maxRadiusJLabel, "cell 2 2");
			
			ParametresPanel1.add(thresholdFieldLabel, "cell 0 3");
			ParametresPanel1.add(thresholdField, "cell 1 3");
			ParametresPanel1.add(thresholdJLabel, "cell 2 3");
			
			ParametresPanel1.add(sensitivityFieldLabel, "cell 0 4");
			ParametresPanel1.add(sensitivityField, "cell 1 4");
			ParametresPanel1.add(sensitivityJLabel, "cell 2 4");
			
			ParametresPanel1.add(WaveletFilter, "cell 0 5");
			ParametresPanel1.add(lowpass, "cell 0 6");
			
			
			pixelSiezField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						pixelSize = Double.parseDouble(pixelSiezField.getText());
						radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
						maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
					}catch (NumberFormatException arg) {
						pixelSiezField.setText(" Invalid number");
					}
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						pixelSize = Double.parseDouble(pixelSiezField.getText());	
						radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
						maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
					}catch (NumberFormatException arg) {
						pixelSiezField.setText(" Invalid number");
					}
				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
	        });
			
			pixelSiezLable.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					pixelUnit = pixelSiezLable.getText();
					radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
					maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);

				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					pixelUnit = pixelSiezLable.getText();
					radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);
					maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);

				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
	        });
			
			radiusField_pixel.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);	
					}catch (NumberFormatException arg) {
						radiusJLabel.setText(" Invalid number");
					}
	            	
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						radiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(radiusField_pixel.getText())*pixelSize))+" "+pixelUnit);	
					}catch (NumberFormatException arg) {
						radiusJLabel.setText(" Invalid number");
					}
				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
	        });

			maxRadiusField_pixel.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);	
					}catch (NumberFormatException arg) {
						maxRadiusJLabel.setText(" Invalid number");
					}
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						maxRadiusJLabel.setText(" "+String.valueOf(format.format(Double.parseDouble(maxRadiusField_pixel.getText())*pixelSize))+" "+pixelUnit);	
					}catch (NumberFormatException arg) {
						maxRadiusJLabel.setText(" Invalid number");
					}
					
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
		
				}
			});
	
			Panel buttonPanel = new Panel();
			buttonPanel.setLayout(new FlowLayout());
			Button previewButton = new Button("preview");
			previewButton.addActionListener((ActionEvent e)->{
			    String aRadius = radiusField_pixel.getText();
			    String aMaxRadius = maxRadiusField_pixel.getText();
			    String aPercent = thresholdField.getText();// 10f; //1-100%
			    String aSensitivity = sensitivityField.getText();
			    boolean Wavelet = WaveletFilter.isSelected();
			    v_paras = new VesicleParametres(aRadius,  aMaxRadius,  aPercent,  aSensitivity,  Wavelet); 
			    v_paras.showLowpass=lowpass.isSelected();
    			preview();
			});
			Button NextButton = new Button("Next");
			NextButton.addActionListener((ActionEvent e)->{
			    String aRadius = radiusField_pixel.getText();
			    String aMaxRadius = maxRadiusField_pixel.getText();
			    String aPercent = thresholdField.getText();// 10f; //1-100%
			    String aSensitivity = sensitivityField.getText();
			    boolean Wavelet = WaveletFilter.isSelected();
			    v_paras = new VesicleParametres(aRadius,  aMaxRadius,  aPercent,  aSensitivity,  Wavelet);
			    v_paras.showLowpass=lowpass.isSelected();
  			    startDectection(); 
  			    parametresWindows.dispose();
  			    new SecretionParametresWindows();
			});
			buttonPanel.add(previewButton);
			buttonPanel.add(NextButton);	

			parametresWindows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			parametresWindows.setResizable(false);
			parametresWindows.setSize(500,280);
			parametresWindows.setLayout(new FlowLayout());
			parametresWindows.add(ParametresPanel1);
			parametresWindows.add(buttonPanel);
			parametresWindows.setVisible(true);
		}
		
		

	}
	public class SecretionParametresWindows {
		public SecretionParametresWindows() {
			
			JFrame parametresWindows=new JFrame("Exocytosis parametres");
			
		
			JCheckBox useMinimalPointsForFitter = new JCheckBox("",true);
			JCheckBox useExpandFrames = new JCheckBox("",true);
			JCheckBox useMaxTauFrames = new JCheckBox("",true);
			JCheckBox useMinTauFrames = new JCheckBox("",true);
			JCheckBox useMaxRadius = new JCheckBox("",false);
			JCheckBox useMinRadius = new JCheckBox("",false);
			JCheckBox useMinR2 = new JCheckBox("",true);
			JCheckBox useMinSNR = new JCheckBox("",false);

			
		    JTextField InervalField = new JTextField(String.valueOf(timeInterval), 5);
		    JTextField minimalPointsForFitterField = new JTextField("3", 5);
		    JTextField expandFramesField = new JTextField("4", 5);
		    JTextField MaxTauFramesField = new JTextField("20", 5);
		    JTextField MinTauFramesField = new JTextField("1", 5);
		    JTextField MaxRadiusField = new JTextField(String.valueOf(v_paras.maxRadius), 5);
		    JTextField MinRadiusField = new JTextField(String.valueOf(v_paras.Radius), 5);
		    JTextField MinR2Field = new JTextField("0.90", 5);
		    JTextField MinSNRField = new JTextField("1.5", 5);
		    
		    JLabel InervalFieldLabel = new JLabel("Slice time interval:");
		    JLabel minimalPointsForFitterFieldLabel = new JLabel("Minimal Points For Fitter:");
		    JLabel expandFramesFieldLabel = new JLabel("Expand Frames:");
		    JLabel MaxTauFramesFieldLabel = new JLabel("MaxTau (frams):");
		    JLabel MinTauFramesFieldLabel = new JLabel("MinTau (frams):");
		    JLabel MaxRadiusFieldLabel = new JLabel("Max Estimated R:");
		    JLabel MinRadiusFieldLabel = new JLabel("Min Estimated R:");
		    JLabel MinR2FieldLabel = new JLabel("Min R2:");
		    JLabel MinSNRFieldLabel = new JLabel("Min SNR:");
		    
		    JTextField Inerval = new JTextField(String.valueOf(timeUnit), 5);
		    JLabel minimalPointsForFitterLabel = new JLabel(" Points");
		    JLabel expandFramesLabel = new JLabel(" Frames");
		    JLabel MaxTauFramesLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(MaxTauFramesField.getText())*timeInterval))+" "+timeUnit);
		    JLabel MinTauFramesLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(MinTauFramesField.getText())*timeInterval))+" "+timeUnit);
		    JLabel MaxRadiusLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(MaxRadiusField.getText())*pixelSize))+" "+pixelUnit);
		    JLabel MinRadiusLabel = new JLabel(" "+String.valueOf(format.format(Double.parseDouble(MinRadiusField.getText())*pixelSize))+" "+pixelUnit);
		    JLabel MinR2Label = new JLabel(" ");
		    JLabel MinSNRLabel = new JLabel(" Folds");
		    
		    
			Panel ParametresPanel1 = new Panel();
			ParametresPanel1.setLayout(new MigLayout("", "[][][][]", "[][]")); 
			
			ParametresPanel1.add(InervalFieldLabel, "cell 1 0");
			ParametresPanel1.add(InervalField, "cell 2 0");
			ParametresPanel1.add(Inerval, "cell 3 0");
			
			ParametresPanel1.add(useMinimalPointsForFitter, "cell 0 1");
			ParametresPanel1.add(minimalPointsForFitterFieldLabel, "cell 1 1");
			ParametresPanel1.add(minimalPointsForFitterField, "cell 2 1");
			ParametresPanel1.add(minimalPointsForFitterLabel, "cell 3 1");
			
			ParametresPanel1.add(useExpandFrames, "cell 0 2");
			ParametresPanel1.add(expandFramesFieldLabel, "cell 1 2");
			ParametresPanel1.add(expandFramesField, "cell 2 2");
			ParametresPanel1.add(expandFramesLabel, "cell 3 2");
			
			ParametresPanel1.add(useMaxTauFrames, "cell 0 3");
			ParametresPanel1.add(MaxTauFramesFieldLabel, "cell 1 3");
			ParametresPanel1.add(MaxTauFramesField, "cell 2 3");
			ParametresPanel1.add(MaxTauFramesLabel, "cell 3 3");
			
			ParametresPanel1.add(useMinTauFrames, "cell 0 4");
			ParametresPanel1.add(MinTauFramesFieldLabel, "cell 1 4");
			ParametresPanel1.add(MinTauFramesField, "cell 2 4");
			ParametresPanel1.add(MinTauFramesLabel, "cell 3 4");
			
			ParametresPanel1.add(useMaxRadius, "cell 0 5");
			ParametresPanel1.add(MaxRadiusFieldLabel, "cell 1 5");
			ParametresPanel1.add(MaxRadiusField, "cell 2 5");
			ParametresPanel1.add(MaxRadiusLabel, "cell 3 5");
			
			ParametresPanel1.add(useMinRadius, "cell 0 6");
			ParametresPanel1.add(MinRadiusFieldLabel, "cell 1 6");
			ParametresPanel1.add(MinRadiusField, "cell 2 6");
			ParametresPanel1.add(MinRadiusLabel, "cell 3 6");
			
			ParametresPanel1.add(useMinR2, "cell 0 7");
			ParametresPanel1.add(MinR2FieldLabel, "cell 1 7");
			ParametresPanel1.add(MinR2Field, "cell 2 7");
			ParametresPanel1.add(MinR2Label, "cell 3 7");
			
			ParametresPanel1.add(useMinSNR, "cell 0 8");
			ParametresPanel1.add(MinSNRFieldLabel, "cell 1 8");
			ParametresPanel1.add(MinSNRField, "cell 2 8");
			ParametresPanel1.add(MinSNRLabel, "cell 3 8");
		    
		    
		    
		    InervalField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						timeInterval = Double.parseDouble(InervalField.getText());
						MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);
						MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
					}catch (NumberFormatException arg) {
						InervalField.setText(" Invalid number");
					}
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						timeInterval = Double.parseDouble(InervalField.getText());	
						MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);
						MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
					}catch (NumberFormatException arg) {
						InervalField.setText(" Invalid number");
					}
				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
	        });
			
		    Inerval.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					timeUnit = Inerval.getText();
					MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);
					MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					timeUnit = Inerval.getText();
					MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);
					MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
	        });
		    
		    MaxTauFramesField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);	
					} catch (NumberFormatException arg) {
						MaxTauFramesLabel.setText(" Invalid number");
					}
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						MaxTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MaxTauFramesField.getText())*timeInterval)+" "+timeUnit);	
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
						MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
					}catch (NumberFormatException arg) {
						MinTauFramesLabel.setText(" Invalid number");
					}
						
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						MinTauFramesLabel.setText(" "+String.valueOf(Double.parseDouble(MinTauFramesField.getText())*timeInterval)+" "+timeUnit);
					}catch (NumberFormatException arg) {
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
								MaxRadiusLabel.setText(" "+String.valueOf(Double.parseDouble(MaxRadiusField.getText())*pixelSize)+" "+pixelUnit);
							}catch (NumberFormatException arg) {
								MaxRadiusLabel.setText(" Invalid number");
							}
								
			            }
						@Override
						public void insertUpdate(DocumentEvent e) {
							try {
								MaxRadiusLabel.setText(" "+String.valueOf(Double.parseDouble(MaxRadiusField.getText())*pixelSize)+" "+pixelUnit);
							}catch (NumberFormatException arg) {
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
					try {
						MinRadiusLabel.setText(" "+String.valueOf(Double.parseDouble(MinRadiusField.getText())*pixelSize)+" "+pixelUnit);
					}catch (NumberFormatException arg) {
						MinRadiusLabel.setText(" Invalid number");
					}
						
	            }
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						MinRadiusLabel.setText(" "+String.valueOf(Double.parseDouble(MinRadiusField.getText())*pixelSize)+" "+pixelUnit);
					}catch (NumberFormatException arg) {
						MinRadiusLabel.setText(" Invalid number");
					}
				}
				@Override
				public void removeUpdate(DocumentEvent e) {

				}
			});
		    
	   
			Panel buttonPanel = new Panel();
			buttonPanel.setLayout(new FlowLayout());
			Button previewButton = new Button("preview");
			previewButton.addActionListener((ActionEvent e)->{

			    String min_points_num = minimalPointsForFitterField.getText();
			    String expand_frames = expandFramesField.getText();
			    String maxTau = MaxTauFramesField.getText();
			    String minTau = MinTauFramesField.getText();
			    String maxR = MaxRadiusField.getText();
			    String minR = MinRadiusField.getText();
			    String r2 = MinR2Field.getText();
			    String SNR = MinSNRField.getText();
			    
			    s_paras = new SecretionParametres(min_points_num, expand_frames, maxTau, minTau, maxR, minR, r2, SNR);
			    s_paras_config = new SecretionParametresConfig(
							    		useMinimalPointsForFitter.isSelected(), 
							    		useExpandFrames.isSelected() ,
							    		useMaxTauFrames.isSelected() ,
							    		useMinTauFrames.isSelected() ,
							    		useMaxRadius.isSelected() ,
							    		useMinRadius.isSelected() ,
							    		useMinR2.isSelected() ,
							    		useMinSNR.isSelected() 
							    	);
			    output_ips.close();
			    Vector<Secretion> detected_secretion = findSecretion();
			    showResuteTable(detected_secretion);

			});
			
			Button NextButton = new Button("Finish");
			NextButton.addActionListener((ActionEvent e)->{

			    String min_points_num = minimalPointsForFitterField.getText();
			    String expand_frames = expandFramesField.getText();
			    String maxTau = MaxTauFramesField.getText();
			    String minTau = MinTauFramesField.getText();
			    String maxR = MaxRadiusField.getText();
			    String minR = MinRadiusField.getText();
			    String r2 = MinR2Field.getText();
			    String SNR = MinSNRField.getText();
			    s_paras = new SecretionParametres(min_points_num, expand_frames, maxTau, minTau, maxR, minR, r2, SNR);
			    s_paras_config = new SecretionParametresConfig(		    		
							    		useMinimalPointsForFitter.isSelected(), 
							    		useExpandFrames.isSelected() ,
							    		useMaxTauFrames.isSelected() ,
							    		useMinTauFrames.isSelected() ,
							    		useMaxRadius.isSelected() ,
							    		useMinRadius.isSelected() ,
							    		useMinR2.isSelected() ,
							    		useMinSNR.isSelected() 
							    	);
			    output_ips.close();
			    Vector<Secretion> detected_secretion = findSecretion();
			    parametresWindows.dispose();
			    showResuteTable(detected_secretion);
			});		

			buttonPanel.add(previewButton);
			buttonPanel.add(NextButton);	
					
			parametresWindows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			parametresWindows.setResizable(false);
			parametresWindows.setSize(350,350);
			parametresWindows.setLayout(new FlowLayout());
			parametresWindows.add(ParametresPanel1);

			parametresWindows.add(buttonPanel);
		    parametresWindows.setVisible(true);

			
		}

	}

	public void preview() {

		ImageProcessor current_processor = image.getProcessor().duplicate().convertToRGB();
		ImageStack previewstack = new ImageStack(image.getWidth(), image.getHeight());
		previewstack.addSlice(image.getProcessor());
		
	    LocalMaximaDetector preview_Detector = new LocalMaximaDetector(previewstack, v_paras);  
		
	    Vector<Vesicle> preview_detected_Vesicle = preview_Detector.Detection();
	    current_processor.setColor(Color.RED);
		if (!preview_detected_Vesicle.isEmpty()) {
		
			for (int i = 0; i < preview_detected_Vesicle.size(); i++) {

				 current_processor.drawOval(preview_detected_Vesicle.elementAt(i).x - (v_paras.Radius+10)/2, 
				        		       preview_detected_Vesicle.elementAt(i).y - (v_paras.Radius+10)/2, 
				        		       v_paras.Radius+10, 
				        		       v_paras.Radius+10);
			}
		}
		
		ImagePlus preview_imp = new ImagePlus("Preview detection", current_processor);
		preview_imp.setDisplayMode(IJ.COLOR);
		preview_imp.show();
		if (v_paras.showLowpass) {
			ImagePlus lowPassImage = new ImagePlus();
			lowPassImage.setStack(preview_Detector.lowPassStack);
			lowPassImage.show();
		}
		
	}
       
	public void startDectection() {

	    LocalMaximaDetector myDetector = new LocalMaximaDetector(image.getStack(),v_paras);
	    
	    detected_vesicles = myDetector.Detection();
		detected_vesicle_seqs = myDetector.tracking();
		
	    if(!detected_vesicles.isEmpty()) { 
	    	output_ips = image.duplicate();
	    	ImageProcessor current_ip;

	    	for (int i = 0; i < detected_vesicles.size(); i++) {
	    		current_ip = output_ips.getStack().getProcessor(detected_vesicles.elementAt(i).slice);
	    		current_ip.setColor(Color.WHITE);
	    		current_ip.drawOval(detected_vesicles.elementAt(i).x - (v_paras.Radius+10)/2, 
	    				detected_vesicles.elementAt(i).y - (v_paras.Radius+10)/2, 
				        		       v_paras.Radius+10, 
				        		       v_paras.Radius+10);
	    	}
	    	output_ips.show();
	    }
		if (v_paras.showLowpass) {
			ImagePlus lowPassImage = new ImagePlus();
			lowPassImage.setStack(myDetector.lowPassStack);
			lowPassImage.show();
		}
	}
	private Vector<Secretion> findSecretion() {
	        	
    	// Do a clone.... 
		detected_vesicle_seqs_clone.removeAllElements();
    	for (int i = 0 ; i < detected_vesicle_seqs.size() ; i++ ) {
    		Secretion seq_clone = new Secretion();
    		seq_clone = detected_vesicle_seqs.elementAt(i).clone();
    		detected_vesicle_seqs_clone.addElement(seq_clone);
    	}
    			
		SecretionDetector mySecretionDetector = new SecretionDetector(detected_vesicle_seqs_clone, image.getStack(), s_paras, s_paras_config);
		Vector<Secretion> detected_secretion = new Vector<Secretion>();
		detected_secretion = mySecretionDetector.Detection();
		int Radius = v_paras.Radius;
		output_ips = image.duplicate();
		ImageProcessor current_ip;

		for (int i = 0; i < detected_secretion.size(); i++) {
				
			Secretion current_secretion = detected_secretion.elementAt(i);
			int size = current_secretion.getDuration();
							
			for (int j = 0; j < size ; j++) {

				output_ips.setSlice(current_secretion.secretion_event.elementAt(j).slice);
				current_ip = output_ips.getProcessor();
				current_ip.convertToRGB();
				current_ip.setColor(Color.WHITE);

				current_ip.drawOval(current_secretion.secretion_event.elementAt(j).x - (Radius+10)/2, 
										current_secretion.secretion_event.elementAt(j).y - (Radius+10)/2, 
										Radius+10, 
										Radius+10);
			}
		
        }
		output_ips.show();	
		return detected_secretion;
	}
	
	private void showResuteTable(Vector<Secretion> detected_secretion) {
		//Result Table Window
		//Initial Table

		final String[] columnTitle = {"Ref" , "Start slice" , "Fin slice", "Start x", "Start y", "Estimated Radius" ,"Tau", "R2"};
		Object[][] tableData =  new Object[detected_secretion.size()][8];
		for (int i = 0; i < detected_secretion.size(); i++) {
			tableData[i]=new Object[] {
			       detected_secretion.elementAt(i).getRef(),
			       detected_secretion.elementAt(i).start_slice,
			       detected_secretion.elementAt(i).fin_slice,
			       detected_secretion.elementAt(i).start_x,
			       detected_secretion.elementAt(i).start_y,
			       detected_secretion.elementAt(i).getVesicleSize()*pixelSize,
			       detected_secretion.elementAt(i).Decay_tau*timeInterval,
			       detected_secretion.elementAt(i).Decay_R2,
			       };
		}
		
		result_dtm = new DefaultTableModel(tableData,columnTitle);
		secretionEventList = new ResultTable(result_dtm);

		secretionEventList.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int selectedTableRow =((JTable) e.getSource()).rowAtPoint(e.getPoint());
            	Secretion selectedSecretion = detected_secretion.elementAt(selectedTableRow);
				if (e.getButton() == MouseEvent.BUTTON3) {
					// add right clicks event
					JPopupMenu popupMenu1 = new JPopupMenu();
					JMenuItem menuItem1 = new JMenuItem("  View cruve fitter");
					JMenuItem menuItem2 = new JMenuItem("  View radius fitter");
					JMenuItem menuItem3 = new JMenuItem("  View rings simpling cruve");
					JMenuItem menuItem4 = new JMenuItem("  View film");
					menuItem1.addMouseListener(new MouseAdapter() { 
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {								
								viewCruvePlot(selectedSecretion);
							}
						}
					});
					menuItem2.addMouseListener(new MouseAdapter() { 
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {								
								viewRadiusFitter(selectedSecretion);
							}
						}
					});
					menuItem3.addMouseListener(new MouseAdapter() { 
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {								
								viewRingSimplingCruvePlot(selectedSecretion);
							}
						}
					});
					menuItem4.addMouseListener(new MouseAdapter() { 
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {								
			                	viewFilm(selectedSecretion); 
							}
						}
					});
					popupMenu1.add(menuItem1);
					popupMenu1.add(menuItem2);
					popupMenu1.add(menuItem3);
					popupMenu1.add(menuItem4);
					popupMenu1.show(e.getComponent(), e.getX(), e.getY());
					
		    		// Set a ROI to localize vesicle in result film
	        		
	        		output_ips.setActivated();
	        		output_ips.setSlice(selectedSecretion.peakTime);
	        		
	        		int Radius = v_paras.Radius;
	        		int RoiX = selectedSecretion.peakX - (Radius+16)/2;
	        		int RoiY = selectedSecretion.peakY - (Radius+16)/2;
	        		
	        		output_ips.setRoi(RoiX, RoiY, Radius+16, Radius+16);
					
				}
				// add double clicks event
                if (e.getClickCount() == 2){  
                	viewFilm(selectedSecretion);   		   
    		        viewCruvePlot(selectedSecretion);
    	    		// Set a ROI to localize vesicle in result film
            		
            		output_ips.setActivated();
            		output_ips.setSlice(selectedSecretion.peakTime);
            		
            		int Radius = v_paras.Radius;
            		int RoiX = selectedSecretion.peakX - (Radius+16)/2;
            		int RoiY = selectedSecretion.peakY - (Radius+16)/2;
            		
            		output_ips.setRoi(RoiX, RoiY, Radius+16, Radius+16);
    				
                }
    
			}
			 
		});
		
		
		//Add button
		Button AddButton = new Button("Add");
		AddButton.addActionListener((ActionEvent e)->{
			addSecretionEvent(detected_secretion) ;
		});
		
		//Remove button
		Button RemoveButton = new Button("Remove");
		RemoveButton.addActionListener((ActionEvent e)->{
			int rowToRemove = secretionEventList.getSelectedRow();
			((DefaultTableModel) secretionEventList.getModel()).removeRow(rowToRemove);
			detected_secretion.removeElementAt(rowToRemove);
			secretionEventList.validate();
			secretionEventList.updateUI();
		   
		});
		
		//Save button
		Button SaveButton = new Button("Save");
		SaveButton.addActionListener((ActionEvent e)->{
				try {
					saveResult(detected_secretion) ;
				} catch (IOException e1) {
					e1.printStackTrace();
				}		
		});
		
		
		//Initial Window "resultWindows"
		JScrollPane resutWinTablePanel = new JScrollPane(secretionEventList);
		resutWinTablePanel.setSize(new Dimension(600,500));

		Panel resutWinButtonPanel = new Panel();
		JLabel addSecetionHelp = new JLabel("Set a ROI over the missing secretion in the result film"); 
		resutWinButtonPanel.setSize(new Dimension(600,100));
		resutWinButtonPanel.setLayout(new FlowLayout());
		resutWinButtonPanel.add(addSecetionHelp);
		resutWinButtonPanel.add(AddButton);
		resutWinButtonPanel.add(RemoveButton);
		resutWinButtonPanel.add(SaveButton);
		
		JFrame resultWindows = new JFrame("Detected secretion evenet");
		resultWindows.setSize(new Dimension(600,600));
		resultWindows.setLayout(new BorderLayout());
		resultWindows.add(resutWinTablePanel,BorderLayout.CENTER);
		resultWindows.add(resutWinButtonPanel,BorderLayout.PAGE_END);
		resultWindows.pack();
		resultWindows.setVisible(true);
		
		//mouse click event for result film
    	output_ips.getCanvas().addMouseListener(new MouseAdapter() {
    		public void mouseReleased(MouseEvent e) {
    			 if (e.getClickCount() == 2){
    				Point clickPoint = ((ImageCanvas) e.getSource()).getCursorLoc();
    				int currentSlice = output_ips.getSlice();
    				
    				for (int i = 0; i < detected_secretion.size(); i++) {
    					if  (	currentSlice >= detected_secretion.elementAt(i).start_slice && 
    							currentSlice <=  detected_secretion.elementAt(i).fin_slice &&
    							Math.abs(clickPoint.x - detected_secretion.elementAt(i).start_x) <= 4* v_paras.Radius &&
    							Math.abs(clickPoint.y - detected_secretion.elementAt(i).start_y) <= 4* v_paras.Radius) {
    						secretionEventList.changeSelection(i, 0, false, false);
    						viewCruvePlot(detected_secretion.elementAt(i));
    						
    					};
    				}
    			 }
    		}		
    	});
	}
	private void addSecretionEvent(Vector<Secretion> detected_secretion) { //from a ROI to "input secretion"
	
		if(output_ips.getRoi() != null) {
			int roi_slice = output_ips.getSlice();
			Rectangle bound = output_ips.getRoi().getBounds();
			ImageStack roi_is = output_ips.duplicate().getStack();
			ImageProcessor roi_ip = output_ips.duplicate().getProcessor();
			
			JFrame addSecretionWindow = new JFrame("Add a secretion");
			Panel addSecretion_buttonPanel = new Panel();
			Panel addSecretion_PlotPanel = new Panel();
	
	
		    
			short[] pixels =(short[]) roi_ip.getPixels();
			double max_value = pixels[0];
			double min_value = pixels[0];
		    for(int i=1;i < pixels.length;i++){
		    	if(pixels[i] > max_value){
		    		max_value = (double) pixels[i];
		    	}
		    	if(pixels[i] < min_value){
		    		min_value = (double) pixels[i]; 
		    	}
		    } 
			
			
			double[] local_max = new double[roi_is.getSize()];
			double[] local_min = new double[roi_is.getSize()];
			x_corr_to_add = new int[roi_is.getSize()]; 
			y_corr_to_add = new int[roi_is.getSize()]; 
			int x_corr = 0;
			int y_corr = 0;
	
			for (int i = 1; i <= roi_is.getSize(); i++) {
	
				pixels =(short[]) roi_is.getProcessor(i).getPixels();
				max_value = pixels[0];
				min_value = pixels[0];
			    for(int j=1;j < pixels.length;j++){
			    	if(pixels[j] > max_value){
			    		max_value =(double) pixels[j];
			    		x_corr = j;
			    		y_corr = j;
			    	}
			    	if(pixels[j] < min_value){
			    		min_value =(double) pixels[j]; 
		
			    	}
			    } 
			    local_max[i-1] = max_value;
			    local_min[i-1] = min_value;
				x_corr_to_add[i-1] = x_corr % roi_is.getWidth() +  bound.x;
				y_corr_to_add[i-1]= (int) (Math.floor(y_corr/roi_is.getWidth()) + bound.y);
	
			}
			
			int start = roi_slice - 10;
			int end = roi_slice + 10;
			
			if (start < 1) start =1;
			if (end > roi_is.getSize()) end = roi_is.getSize();
			
			double[] xdata = new double[end-start];
			double[] y1data = new double[end-start];
			double[] y2data = new double[end-start];
			int time_corr = start;
			for (int i = 0; i < xdata.length; i++) {					
				xdata[i] =  time_corr+i;
				y1data[i] = local_max[time_corr+i];
				y2data[i] = local_min[time_corr+i];
			}
			
		    TextField frameBeforePeak = new TextField(String.valueOf(start), 10);	    
		    JLabel frameBeforePeakLabel = new JLabel("Start at (Frame):");
		    TextField frameAfterPeak = new TextField(String.valueOf(end), 10);	    
		    JLabel frameAfterPeakLabel = new JLabel("End:");
		    
			
			Plot plotForAddSecretion = new Plot("Fluo", "Frams", "Intensity",xdata ,y1data );
			plotForAddSecretion.setColor(Color.RED);
			plotForAddSecretion.addPoints(xdata, y2data, Plot.LINE);
			plotForAddSecretion.updateImage();
			ImageCanvas plotImg = new ImageCanvas(plotForAddSecretion.getImagePlus());
			addSecretion_PlotPanel.add(plotImg);
			
				    
		    
		    Button addSecretion_OKButton = new Button("OK");
			addSecretion_OKButton.addActionListener((ActionEvent c1)->{				
				Vector<Vesicle> vesicle_list = new Vector<Vesicle>();
				//generate secretion
				int userSelectStart = Integer.parseInt(frameBeforePeak.getText());
				int userSelectEnd = Integer.parseInt(frameAfterPeak.getText());
				int Radius = detected_secretion.elementAt(0).secretion_event.elementAt(0).radius;
				
				for (int i = userSelectStart; i <= userSelectEnd; i++) {
						Vesicle vesicle_to_add = new Vesicle(x_corr_to_add[i-1], y_corr_to_add[i-1], i, Radius, image.getStack());
						vesicle_list.addElement(vesicle_to_add);
				} 
				Secretion secretion_to_add = new Secretion(vesicle_list);
				
				
				secretion_to_add.Fit(s_paras.min_points_num);
				int lastRef=detected_secretion.lastElement().getRef();
				detected_secretion.addElement(secretion_to_add);
				detected_secretion.lastElement().setRef(lastRef+1);
				
				Object rowToAdd[] = new Object[] {detected_secretion.lastElement().getRef(),
						detected_secretion.lastElement().start_slice,
						detected_secretion.lastElement().fin_slice,
						detected_secretion.lastElement().start_x,
						detected_secretion.lastElement().start_y,
						detected_secretion.lastElement().getVesicleSize(),
						detected_secretion.lastElement().Decay_tau,
						detected_secretion.lastElement().Decay_R2,};
				
				((DefaultTableModel) secretionEventList.getModel()).addRow(rowToAdd);
				secretionEventList.validate();
				secretionEventList.updateUI();
				addSecretionWindow.dispose();
			});
			
			Button addSecretion_cancelButton = new Button("Cancel");
			addSecretion_cancelButton.addActionListener((ActionEvent c2)->{	
				addSecretionWindow.dispose();
			});
			
	
			addSecretion_buttonPanel.setLayout(new FlowLayout());
	
			addSecretion_buttonPanel.add(frameBeforePeakLabel);
			addSecretion_buttonPanel.add(frameBeforePeak);
			addSecretion_buttonPanel.add(frameAfterPeakLabel);
			addSecretion_buttonPanel.add(frameAfterPeak);
			addSecretion_buttonPanel.add(addSecretion_OKButton);
			addSecretion_buttonPanel.add(addSecretion_cancelButton);
	
	
			addSecretionWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addSecretionWindow.setResizable(false);
			addSecretionWindow.setSize(600,400);
			addSecretionWindow.setLayout(new FlowLayout());
			addSecretionWindow.add(addSecretion_PlotPanel);
			addSecretionWindow.add(addSecretion_buttonPanel);
			addSecretionWindow.setVisible(true);	
			
	
		}
	
	}
	private void viewRadiusFitter(Secretion aSecretion) {
		Vesicle selectedVesicle = aSecretion.secretion_event.elementAt(aSecretion.peakTime - aSecretion.start_slice);
		double[] hProfile = selectedVesicle.getHorizontalProfile();
		double[] hParas = selectedVesicle.getHorizontalGaussFitter();
		
		double[] vProfile = selectedVesicle.getVerticalProfile();
		double[] vParas =selectedVesicle.getVerticalGaussFitter();
		
    	double[] xdata = new double[4*selectedVesicle.radius+1];
    	double j = -2*selectedVesicle.radius;
    	for (int i=0; i<xdata.length; i++) {
    		xdata[i]= j;
    		j++;
    	}
    	
    	double[] hFitter = new double[xdata.length]; 
    	for (int i=0; i<xdata.length; i++) {
    		hFitter[i] = hParas[0] + (hParas[1]-hParas[0]) * Math.exp( -( Math.pow( (xdata[i]-hParas[2]) , 2) / (2* Math.pow(hParas[3],2))  )   );
    		j++;
    	}
    	
    	double[] vFitter = new double[xdata.length];
    	for (int i=0; i<xdata.length; i++) {
    		vFitter[i] = vParas[0] + (vParas[1]-vParas[0]) * Math.exp( -( Math.pow( (xdata[i]-vParas[2]) , 2) / (2* Math.pow(vParas[3],2))  )   );
    		j++;
    	}
    	
		//raw data
		Plot intensityPlotH = new Plot("Vesicle", "Horizontal", "Intensity",xdata , hProfile );
		intensityPlotH.show();
		//fit curve
		intensityPlotH.setColor(Color.RED);
		intensityPlotH.addPoints(xdata, hFitter, Plot.LINE);	
    	
		Plot intensityPlotV = new Plot("Vesicle", "Horizontal", "Intensity",xdata , vProfile );
		intensityPlotV.show();
		//fit curve
		intensityPlotV.setColor(Color.RED);
		intensityPlotV.addPoints(xdata, vFitter, Plot.LINE);

	}

	private void viewFilm(Secretion aSecretion) {
		ImagePlus secretionFilm = new ImagePlus(); 
		secretionFilm.setStack(aSecretion.film);    				
		secretionFilm.show();
        int Img_width = secretionFilm.getWidth();
        int Img_height = secretionFilm.getHeight();

        ImageCanvas secretion_ic = secretionFilm.getCanvas();
        Rectangle srcRect = secretion_ic.getSrcRect();

        int srcWidth = srcRect.width;
        int srcHeight = srcRect.height;
        
        // Set a scale for a better preview film 
        
        double mag = 4;
        
        secretion_ic.setMagnification(mag);
        double newWidth = Img_width*mag;
        double newHeight = Img_height*mag;
        secretion_ic.setSize((int)newWidth, (int)newHeight);
        secretion_ic.setSourceRect(new Rectangle(0, 0, srcWidth, srcHeight));
        secretion_ic.repaint();
	}
	private void viewCruvePlot(Secretion aSecretion) {
    	// Preview plot 

		
		double[] x = new double[aSecretion.getDuration()];
		double[] fit_x = new double[aSecretion.fin_slice - aSecretion.peakTime +1];
		double[] fit_y = new double[aSecretion.fin_slice - aSecretion.peakTime +1];
		
		for (int i = 0; i < x.length; i++ ) {		    					
			x[i]=aSecretion.start_slice + i;
		}
		
		for (int i = 0; i < fit_x.length; i++ ) {
			fit_y[i] = aSecretion.Decay_bk + aSecretion.Decay_peak*Math.exp(-i/aSecretion.Decay_tau);
			fit_x[i] = aSecretion.peakTime + i;
		}
		
		
		//raw data
		Plot intensityPlot = new Plot("Fluo", "Frams", "Intensity",x , aSecretion.getCurve() );
		intensityPlot.show();
		//fit curve
		intensityPlot.setColor(Color.RED);
		intensityPlot.addPoints(fit_x, fit_y, Plot.LINE);	
		

	}
	
	private void viewRingSimplingCruvePlot(Secretion aSecretion) {
    	// Preview plot 

		
		double[] x = new double[aSecretion.getDuration()];
		Vector<double[]> RTCruve =  aSecretion.getRingSimplingCurve();
		
		int cruveNum = RTCruve.size();
		if (cruveNum > 6) cruveNum =6;
		
		for (int i = 0; i < x.length; i++ ) {		    					
			x[i]=aSecretion.start_slice + i;
		}
		
		//Central point
		Plot intensityPlot = new Plot("Fluo", "Frams", "Intensity",x ,RTCruve.firstElement());
		intensityPlot.show();
		
		//Ring simpling 
		for (int i = 1; i < cruveNum; i++ ) {		    					
			intensityPlot.setColor(new Color(0,(i-1)*40,255));
			intensityPlot.addPoints(x, RTCruve.elementAt(i), Plot.LINE);	
		}
		
		

	}

	public void saveResult( Vector<Secretion> detected_secretion ) throws IOException {
		
		SaveDialog saveFileDialog = new SaveDialog("save file", "result",".csv");
        String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
        

        final String separator = ","; 
        StringBuffer result = new StringBuffer();
                
        File outputfile = new File(saveFilePatch);
        outputfile.createNewFile();
		FileWriter mywriter = new FileWriter(outputfile);

		mywriter.write("ID");
		mywriter.write(separator);
		mywriter.write("start_slice");
		mywriter.write(separator);
		mywriter.write("fin_slice");
		mywriter.write(separator);
		mywriter.write("start_x");
		mywriter.write(separator);
		mywriter.write("start_y");
		mywriter.write(separator);
		mywriter.write("Curve");
		mywriter.write(separator);
		mywriter.write("Tau");
		mywriter.write(separator);
		mywriter.write("Estimate R");
		mywriter.write('\n');

        for (int i = 0; i < detected_secretion.size(); i++) {
	
	        result.append(Integer.toString(detected_secretion.elementAt(i).getRef()));
	        result.append(separator);
	        result.append(Integer.toString(detected_secretion.elementAt(i).start_slice));
	        result.append(separator);
	        result.append(Integer.toString(detected_secretion.elementAt(i).fin_slice));
	        result.append(separator);
	        result.append(Integer.toString(detected_secretion.elementAt(i).start_x));
	        result.append(separator);
	        result.append(Integer.toString(detected_secretion.elementAt(i).start_y));
	        result.append(separator);
	        result.append(Arrays.toString(detected_secretion.elementAt(i).getCurve()));
	        result.append(separator);
	        result.append(Double.toString(detected_secretion.elementAt(i).Decay_tau));
	        result.append(separator);
	        result.append(Double.toString(detected_secretion.elementAt(i).getVesicleSize()));
	        result.append("\n");

	        
        }

        mywriter.write(result.toString());
        mywriter.flush();
        mywriter.close();
	}
	
}
