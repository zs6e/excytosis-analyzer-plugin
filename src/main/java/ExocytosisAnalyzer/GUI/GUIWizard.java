package ExocytosisAnalyzer.GUI;

import java.awt.BorderLayout;

import java.awt.CardLayout;
import java.awt.Color;
//import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;

import ExocytosisAnalyzer.datasets.ElementInFrame;
import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.Parameters;
import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.detection.DeltaMovie;
import ExocytosisAnalyzer.detection.Grubbs;
import ExocytosisAnalyzer.detection.LocalMaximaDetector;
import ExocytosisAnalyzer.detection.MicroWaveletFilter;
import ExocytosisAnalyzer.detection.MovingLinearRegretionFinder;
import ExocytosisAnalyzer.detection.PhotolechingCorrection;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.FloatProcessor;

public class GUIWizard extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel cardPanel;
	private CardLayout card;
	private VesicleParametresWindows vesicleWindows;
	private TrackingWindows trackingWindows;
	private SecretionParametresWindows secretionWindows;
	private JButton previewButton;
	private JButton nextButton;
	private JButton backButton;
	private ResultStackWindow preview_stack;
	private ResultStackWindow preview_dF_stack;
	
	private ActionListener detecteVesicle;
	private ActionListener previewVesicle;
	private ActionListener previewTracking;
	private ActionListener Tracking;
	private ActionListener showSecretion;
	private ActionListener previewSecretion;
	private ActionListener backToVesicle;
	private ActionListener backToTracking;

	
	
	public ImagePlus imp;
	public static  ImagePlus origin;
	public ImagePlus dF;
	public ImagePlus absdF;
	
	private final int image_height, image_width, num_of_slices;
	private Parameters parameters;
	//private JPanel progressPanel;
	private ProgressMonitor progress;
	private Vector<ElementInFrame> elements;
	private Vector<Secretion> detected_events;
	private Vector<Secretion> detected_secretions;
	private JComboBox<String> colorList;
	public static Color myColor = Color.RED;
	
	
	private JCheckBox photobleachingCorrection;
	private JLabel photobleachingCorrectionLabel;
	private JCheckBox deltamovie;
	private JLabel showdeltaMovieLabel;
	
	public GUIWizard(ImagePlus input) {
				
		this.setTitle("ExoJ: Event Detection");
		origin = input;
		imp = input.duplicate();
		imp.show();
		parameters = new Parameters(input);
		parameters.InitialVesicleParametres(input);
		parameters.InitialTrackingParameters();
		parameters.InitialSecretionParameters();
		elements = new Vector<ElementInFrame>();
		image_width = imp.getWidth();
		image_height = imp.getHeight();
		num_of_slices = imp.getStackSize();



		vesicleWindows = new VesicleParametresWindows(parameters);
		trackingWindows = new TrackingWindows(parameters);
		secretionWindows = new SecretionParametresWindows(imp, parameters);
		
		card = new CardLayout(5, 5);
		cardPanel = new JPanel(card);
		cardPanel.add(vesicleWindows,"p1");
		cardPanel.add(trackingWindows,"p2");
		cardPanel.add(secretionWindows,"p3");
		
		
		photobleachingCorrection = new JCheckBox ("");
		photobleachingCorrectionLabel = new JLabel("Correct Photobleaching (may create artifacts!)");
		
		deltamovie = new JCheckBox("");
		showdeltaMovieLabel = new JLabel("Transform to dF movie");
		
		
		detected_events = new Vector<Secretion>();
		detected_secretions = new Vector<Secretion>();


		previewSecretion = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (preview_stack.isVisible())
					preview_stack.setVisible(false);
				if (preview_stack.isClosed())
					preview_stack = new ResultStackWindow(imp.duplicate());

				preview_stack.setTitle("Secretion Preview");
				new SecretionDetectorThread(detected_events, detected_secretions).start();
				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
			}
		};

		showSecretion = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (preview_stack.isVisible())
					preview_stack.setVisible(false);
				if (preview_stack.isClosed())
					preview_stack = new ResultStackWindow(imp.duplicate());

				// preview_imp = imp.duplicate();
				preview_stack.setTitle("Secretion Preview");
				progress = new ProgressMonitor(secretionWindows,"","Exocytosis identification in progress...",0, 100);
				new SecretionDetectorThread(detected_events, detected_secretions).start();
				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
			}
		};

		previewTracking = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (preview_stack.isVisible())
					preview_stack.setVisible(false);
				if (preview_stack.isClosed())
					preview_stack = new ResultStackWindow(imp.duplicate());

				preview_stack.setTitle("Tracking Preview");
				new trackingThread(detected_events, true, parameters).start();
				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
			}
		};

		Tracking = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (preview_stack.isVisible())
					preview_stack.setVisible(false);
				if (preview_stack.isClosed())
					preview_stack = new ResultStackWindow(imp.duplicate());

				preview_stack.setTitle("Tracking Preview");
				progress = new ProgressMonitor(trackingWindows,"","Tracking in progress...",0, 100);
				new trackingThread(detected_events, false, parameters).start();

				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
				card.show(cardPanel,"p3");
				previewButton.removeActionListener(previewTracking);
				nextButton.removeActionListener(Tracking);
				backButton.removeActionListener(backToVesicle);
				backButton.addActionListener(backToTracking);
				previewButton.addActionListener(previewSecretion);
				nextButton.addActionListener(showSecretion);
			}
		};

		previewVesicle = new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				ImagePlus preview_imp;
				ResultStackWindow preview_win;
				
				ImagePlus preview_dF_imp;
				ResultStackWindow preview_dF_win;
				ImagePlus preview_absdF_imp;
							
			
				if (deltamovie.isSelected()) {
					int currentslice = ij.WindowManager.getCurrentImage().getCurrentSlice();
					dF.setSlice(currentslice);
					absdF.setSlice(currentslice);
					imp.setSlice(currentslice);
					preview_dF_imp = new ImagePlus("Detected spots", dF.getProcessor());
					preview_absdF_imp = new ImagePlus("Detected spots", absdF.getProcessor());
					preview_dF_win = new ResultStackWindow(preview_dF_imp);
					preview_imp = new ImagePlus("Detected spots", imp.getProcessor());
					preview_win = new ResultStackWindow(preview_imp);
					if (parameters.WaveletFilter) {
						preview_win.setTitle("Detected spots : Wavelet SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
						preview_dF_win.setTitle("Detected spots in dF : Wavelet SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
					}
					else {
						preview_win.setTitle("Detected spots : Local maxima SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
						preview_dF_win.setTitle("Detected spots in dF : Local maxima SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
					}
					if (imp.getRoi() == null)
						vesicleWindows.setRoi(new Roi(0, 0, image_width, image_height));
					else
						vesicleWindows.setRoi(imp.getRoi());

					Vector<Vesicle> preview_detection_vesicles = vesicleWindows.preview_Detection(preview_absdF_imp, parameters);
					preview_dF_win.setLabels(preview_detection_vesicles);
					preview_dF_win.setVisible(true);
					preview_win.setLabels(preview_detection_vesicles);
					preview_win.setVisible(true);
				} else {
					preview_imp = new ImagePlus("Detected spots", imp.getProcessor());
					preview_win = new ResultStackWindow(preview_imp);
					if (parameters.WaveletFilter) {
						preview_win.setTitle("Detected spots : Wavelet SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
					}
					else {
						preview_win.setTitle("Detected spots : Local maxima SNR = " + String.valueOf(parameters.SNR_Vesicle) + " σ" );
					}
					if (imp.getRoi() == null)
						vesicleWindows.setRoi(new Roi(0, 0, image_width, image_height));
					else
						vesicleWindows.setRoi(imp.getRoi());

					Vector<Vesicle> preview_detection_vesicles = vesicleWindows.preview_Detection(preview_imp, parameters);
					preview_win.setLabels(preview_detection_vesicles);
					preview_win.setVisible(true);
				}
				
			}

			
		};

		detecteVesicle = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				progress = new ProgressMonitor(vesicleWindows,"","Detection in progress...",0, 100);
				new VesicleDetectThread().start();
				previewButton.setEnabled(false);
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
				
				photobleachingCorrection.setVisible(false);
				photobleachingCorrectionLabel.setVisible(false);
				deltamovie.setVisible(false);
				showdeltaMovieLabel.setVisible(false);
				
				
				card.show(cardPanel,"p2");
				previewButton.removeActionListener(previewVesicle);
				nextButton.removeActionListener(detecteVesicle);
			
				nextButton.setText("Next");
				previewButton.addActionListener(previewTracking);
				nextButton.addActionListener(Tracking);
				backButton.addActionListener(backToVesicle);
				if (parameters.deltamovie)
					TrackingWindows.setMinimalEventSizeField(1);
				else 
					TrackingWindows.setMinimalEventSizeField(2);
				
			}
		};		
		backToVesicle = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				previewButton.removeActionListener(previewTracking);
                previewButton.addActionListener(previewVesicle);
                
				nextButton.removeActionListener(Tracking);
				nextButton.addActionListener(detecteVesicle);
				nextButton.setText("Run");
				

				card.show(cardPanel,"p1");
				backButton.removeActionListener(backToVesicle);
				backButton.setEnabled(false);
				
				photobleachingCorrection.setVisible(true);
				photobleachingCorrectionLabel.setVisible(true);
				deltamovie.setVisible(true);
				showdeltaMovieLabel.setVisible(true);
			}
		};
		backToTracking = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previewButton.removeActionListener(previewSecretion);
                previewButton.addActionListener(previewTracking);
                
				nextButton.removeActionListener(showSecretion);
				nextButton.setText("Next");
				nextButton.addActionListener(Tracking);
				card.show(cardPanel,"p2");
				backButton.removeActionListener(backToTracking);
				backButton.addActionListener(backToVesicle);
				
				
			}
		};

		
		photobleachingCorrection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (deltamovie.isSelected()) {
					 RestoreImage();
					 deltamovie.setSelected(false); 
				}
				if (photobleachingCorrection.isSelected()) 
					 BleachingCorrection();
				 else
					 RestoreImage();
			}

		});
		
		
		deltamovie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (deltamovie.isSelected()) {
					absdF = DeltaMovie.getAbsDeltaMovie(imp);
					dF = DeltaMovie.getDeltaMovie(imp);
					dF.show();
					parameters.deltamovie = true;
					parameters.minimalEventSize = 1;
				} else {
					absdF.close();
					dF.close();
					parameters.deltamovie = false;
					parameters.minimalEventSize = 2;
				}
			}
		});
		
		
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		
		
		JPanel bouttonPanel = new JPanel();
		
		
		previewButton = new JButton("Preview");
		nextButton = new JButton("Run");
		backButton = new JButton("Back");
		
		previewButton.addActionListener(previewVesicle);
		nextButton.addActionListener(detecteVesicle);
		backButton.setEnabled(false);
		
		bouttonPanel.add(backButton);
		bouttonPanel.add(previewButton);
		bouttonPanel.add(nextButton);
		
		//progressPanel.add(progress);
		//bouttonPanel.add(progress);		
		
		
		JLabel colorText = new JLabel("Label color:");
		String[] symbolColorCollection = {"Red","Yellow","Green","Blue","White"};
		
		colorList = new JComboBox<String>(symbolColorCollection);
		
		colorList.addActionListener((e)->{
			updateColor();
		});
		
		JPanel colorPanel = new JPanel();
		colorPanel.add(colorText); 
		colorPanel.add(colorList); 
		
		
		JPanel correctionPanel = new JPanel();
		correctionPanel.add(photobleachingCorrection);
		correctionPanel.add(photobleachingCorrectionLabel);
		correctionPanel.add(deltamovie);
		correctionPanel.add(showdeltaMovieLabel);

		
		
		bottomPanel.add(correctionPanel,BorderLayout.NORTH);
		bottomPanel.add(bouttonPanel,BorderLayout.CENTER);
		bottomPanel.add(colorPanel,BorderLayout.EAST);
		
		
		this.add(cardPanel);
		this.add(bottomPanel, BorderLayout.SOUTH);
		//progress.setVisible(false);
		this.setSize(650, 600);
		this.setVisible(true);
	}
	
	private void updateColor()
	{
		String symbolColor = (String)colorList.getSelectedItem();
		
		switch (symbolColor){
		case "Red": 
			myColor = Color.RED;
			break;
		case "Yellow": 
			myColor = Color.YELLOW;
			break;
		case "Green": 
			myColor = Color.GREEN;
			break;
		case "Blue": 
			myColor = Color.BLUE;
			break;
		case "White": 
			myColor = Color.WHITE;
			break;	
		}
	}

	private class VesicleDetectThread extends Thread {

		private MicroWaveletFilter waveletFilter;
		private LocalMaximaDetector myDetector;

		public VesicleDetectThread() {
			
			if (deltamovie.isSelected()) {
				waveletFilter = new MicroWaveletFilter(absdF, parameters.scaleList, parameters.SNR_Vesicle);
				myDetector = new LocalMaximaDetector(absdF, parameters);
			} else {
				waveletFilter = new MicroWaveletFilter(imp, parameters.scaleList, parameters.SNR_Vesicle);
				myDetector = new LocalMaximaDetector(imp, parameters);
			}
			
			if (imp.getRoi() == null)
				myDetector.setRoi(new Roi(0, 0, image_width, image_height));
			else
				myDetector.setRoi(imp.getRoi());
		}

		public void run() {
			//progress.setVisible(true);
			elements.removeAllElements();
			ElementInFrame element;

			if (vesicleWindows.WaveletFilter.isSelected()) {
				if (vesicleWindows.lowpass.isSelected()) {
					float[] lowPass;
					ImageStack LowPassStack = new ImageStack(image_width, image_height);

					for (int slice = 1; slice <= num_of_slices; slice++) {
						element = new ElementInFrame();
						if (deltamovie.isSelected()) 
							lowPass = waveletFilter.filter(absdF.getStack().getProcessor(slice));
						else
							lowPass = waveletFilter.filter(imp.getStack().getProcessor(slice));
						element.Vesicles = myDetector.FindLocalMaxima(lowPass, slice, 1);
						element.Frame_num = slice;
						elements.add(element);
						FloatProcessor newslice = new FloatProcessor(image_width, image_height, lowPass);
						newslice.invertLut();
						LowPassStack.addSlice(newslice);
						progress.setProgress(slice * 100 / num_of_slices);
					}
					ImagePlus lowPassImage = new ImagePlus("Wavelet Low-pass Image",LowPassStack);
					//lowPassImage.setStack(LowPassStack);
					//lowPassImage.setTitle("Wavelet Low-pass Image");
				   
					lowPassImage.show();


				
					
					
				} else {
					float[] lowPass;
					for (int slice = 1; slice <= num_of_slices; slice++) {
						element = new ElementInFrame();
						if (deltamovie.isSelected()) 
							lowPass = waveletFilter.filter(absdF.getStack().getProcessor(slice));
						else
							lowPass = waveletFilter.filter(imp.getStack().getProcessor(slice));
						element.Vesicles = myDetector.FindLocalMaxima(lowPass, slice, 1);
						element.Frame_num = slice;
						elements.add(element);
						progress.setProgress(slice * 100 / num_of_slices);
					}
				}
			} else {
				float[] normalized_float_img;
				for (int slice = 1; slice <= num_of_slices; slice++) {
					element = new ElementInFrame();
					if (deltamovie.isSelected()) 
						normalized_float_img = myDetector.getNormalizedImage(absdF.getStack().getProcessor(slice));
					else
						normalized_float_img = myDetector.getNormalizedImage(imp.getStack().getProcessor(slice));
					final float threshold = myDetector.CalculateThreshold(normalized_float_img);
					element.Vesicles = myDetector.FindLocalMaxima(normalized_float_img, slice, threshold);
					element.Frame_num = slice;
					elements.add(element);
					progress.setProgress(slice * 100 / num_of_slices);
				}
			}
			progress.close();
			boolean noResult = true;
			for (ElementInFrame e : elements) {
				if (!e.Vesicles.isEmpty())
					noResult = false;
			}

			if (!noResult) {

				preview_stack = new ResultStackWindow(imp.duplicate());
				preview_stack.setTitle("Detected events");
				preview_stack.setLabels3(elements);
				if (deltamovie.isSelected()) {
					preview_dF_stack = new ResultStackWindow(dF.duplicate());
					preview_dF_stack.setTitle("Detected events");
					preview_dF_stack.setLabels3(elements);
				}
				
			} else {
				IJ.showMessage("No event detected");
			}
			nextButton.setEnabled(true);
			previewButton.setEnabled(true);
			backButton.setEnabled(true);

		}
	}

	private class trackingThread extends Thread {
		private final int search_radius, TemporalSearchDepth, minimalEventSize;
		private Vector<Secretion> _detected_events;
		boolean preview;

		public trackingThread(Vector<Secretion> detected_events, boolean preview, Parameters para) {
			search_radius = para.SpatialsearchRadius;
			TemporalSearchDepth = para.TemporalSearchDepth;
			minimalEventSize = para.minimalEventSize;
			this._detected_events = detected_events;
			this._detected_events.removeAllElements();
			this.preview = preview;
			//Spot tracking,
            //Search in XY : search_radius (in pixels)
			//Search in Z : "TimeSearchDepth" (in frames)
		}

		public void run() {
			Vesicle current_vesicle;
			Vesicle next_vesicle;
			//progress.setVisible(true);
			//progress.setValue(0);
			final int num_of_frame = elements.size();
			int ref = 1;
			
			
			for (int i = 0; i < num_of_frame; i++) {
				elements.elementAt(i).removeLink();
			}
			for (int i = 0; i < num_of_frame; i++) {
				for (int j = 0; j < elements.elementAt(i).Vesicles.size(); j++) {
					Vector<Vesicle> vesicle_sequence = new Vector<Vesicle>();
					boolean isNewEvent = true;
					boolean hasNext;
					if (elements.elementAt(i).Vesicles.elementAt(j).isLinked)
						continue;
					current_vesicle = elements.elementAt(i).Vesicles.elementAt(j);
					vesicle_sequence.addElement(current_vesicle);
					do {
						hasNext = false;
						if (TemporalSearchDepth == 1) {
							int l = -1;
							double min_distance = search_radius;
							double distance;
							// current_vesicle.slice slice start at 1, not 0. current_vesicle.slice = next
							// frames of current_vesicle
							if (current_vesicle.slice >= num_of_frame)
								break;
							for (int k = 0; k < elements.elementAt(current_vesicle.slice).Vesicles.size(); k++) {
								next_vesicle = elements.elementAt(current_vesicle.slice).Vesicles.elementAt(k);
								if (!next_vesicle.isValid)
									continue;
								if (next_vesicle.isLinked)
									continue;
								distance = current_vesicle.getDistance(next_vesicle);
								if (distance < min_distance) {
									min_distance = distance;
									l = k;
								}

							}
							if (l != -1) {
								if (isNewEvent) {
									elements.elementAt(i).Vesicles.elementAt(j).isLinked = true;
									isNewEvent = false;
								}
								elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l).isLinked = true;
								vesicle_sequence
										.addElement(elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l));
								current_vesicle = elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l);
								hasNext = true;
							}
						} else {
							for (int deep = 0; deep < TemporalSearchDepth; deep++) {
								// current_vesicle.slice slice start at 1, not 0
								if (current_vesicle.slice + deep >= num_of_frame)
									break;
								int l = -1;
								double min_distance = search_radius;
								double distance;

								for (int k = 0; k < elements.elementAt(current_vesicle.slice).Vesicles.size(); k++) {
									next_vesicle = elements.elementAt(current_vesicle.slice).Vesicles.elementAt(k);

									if (!next_vesicle.isValid)
										continue;
									if (next_vesicle.isLinked)
										continue;
									distance = current_vesicle.getDistance(next_vesicle);
									if (distance < min_distance) {
										min_distance = distance;
										l = k;
									}
								}

								if (l != -1) {
									if (isNewEvent) {
										elements.elementAt(i).Vesicles.elementAt(j).isLinked = true;
										isNewEvent = false;
									}
									elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l).isLinked = true;
									vesicle_sequence.addElement(
											elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l));
									current_vesicle = elements.elementAt(current_vesicle.slice).Vesicles.elementAt(l);
									hasNext = true;
								}

								if (hasNext)
									break;
								else {
									Vesicle temporal_vesicle = new Vesicle(current_vesicle.x, current_vesicle.y,
											current_vesicle.radius, current_vesicle.slice + 1);
									temporal_vesicle.property = "Gap";
									temporal_vesicle.isValid = false;
									temporal_vesicle.pixelSize = parameters.pixelSize;
									temporal_vesicle.pixelSizeUnit = parameters.pixelUnit;
									vesicle_sequence.addElement(temporal_vesicle);

									// Just a temporary vesicle to fill the gap, not valid

									current_vesicle = temporal_vesicle;
								}
								if (deep == TemporalSearchDepth - 1) {
									for (int remove = 0; remove < TemporalSearchDepth; remove++) {
										vesicle_sequence.removeElementAt(vesicle_sequence.size() - 1);
									}
								}
							}
						}

					} while (hasNext);
					int detectedVesicleNum = 0;
					for (Vesicle v : vesicle_sequence) {
						if (v.property == "Automatic")
							detectedVesicleNum++;
					}

					if (detectedVesicleNum >= minimalEventSize) {
						
						

						for (int k = 0; k < vesicle_sequence.size(); k++) {
							vesicle_sequence.elementAt(k).setImage(imp.getStack());
						}

						_detected_events.addElement(new Secretion(vesicle_sequence));
						_detected_events.lastElement().setRef(ref);
						ref++;
					}

				}
				progress.setProgress(i * 100 / num_of_frame);
			}
			progress.close();
			
			// Put all vesucles in a Vecter
			Vector<Vesicle> Vesicles = new Vector<Vesicle>();
			for (Secretion s : _detected_events) {
				for (Vesicle v : s.secretion_event) {
					Vesicles.addElement(v);
				}
			}

			if (!_detected_events.isEmpty()) {
				
				preview_stack.setLabels(Vesicles);
				preview_stack.setVisible(true);
				
				if (parameters.deltamovie) {
					preview_dF_stack.setLabels(Vesicles);
					preview_dF_stack.setVisible(true);
				}


			} else {
				IJ.showMessage("No event detected");
			}
			if (parameters.showlist) {
				TrackingTableWin tracking_results_win = new TrackingTableWin(imp, preview_stack,_detected_events, parameters);
				tracking_results_win.setVisible(true);
			}

			if (preview) {
				previewButton.setEnabled(true);
				nextButton.setEnabled(true);
				backButton.setEnabled(true);
				
			}
			else {
				previewButton.setEnabled(true);
				nextButton.setEnabled(true);
				nextButton.setText("Run");
				backButton.setEnabled(true);

				
			}

		}
	}

	private class SecretionDetectorThread extends Thread {

		private int min_points_num;
		//private int spot_radius; 
	    private double min_tau;
		private Vector<Secretion> _detected_events, _detected_events_clone, _detected_secretions;


		public SecretionDetectorThread(Vector<Secretion> detected_events, Vector<Secretion> detected_secretions) {

			this._detected_secretions = detected_secretions;
			this._detected_secretions.removeAllElements();
			this._detected_events = detected_events;
			//spot_radius = detected_events.firstElement().secretion_event.firstElement().radius;

			if (parameters.useMinTauFrames)
				min_tau = parameters.min_tau;
			else
				min_tau = 0;

			if (parameters.useMinimalPointsForFitter)
				min_points_num = parameters.minimalPointsForFitter;
			else
				min_points_num = 4;

		}

		public void run() {
			//progress.setVisible(true);

			// make a clone....
			_detected_events_clone = new Vector<Secretion>();
			for (Secretion event : _detected_events) {
				_detected_events_clone.addElement(event.clone());
			}
			
			int ref = 1;

			for (int i = 0; i < _detected_events_clone.size(); i++) {
				Secretion current_secretion_event = _detected_events_clone.elementAt(i);

				int start_slice = current_secretion_event.getStartSlice();
				int start_x = current_secretion_event.getStartX();
				int start_y = current_secretion_event.getStartY();

				int fin_slice = current_secretion_event.getFinSlice();
				int fin_x = current_secretion_event.getFinX();
				int fin_y = current_secretion_event.getFinY();

				if (parameters.useExpandFrames) {
					for (int s = 1; s <= parameters.expand_frames_L; s++) {
						if ((start_slice - s) < 1)
							continue;
						Vesicle aVesicle = new Vesicle(start_x, start_y, current_secretion_event.secretion_event.firstElement().radius, start_slice - s, imp.getStack(), parameters.fixedExpand);
						aVesicle.isValid = false;
						aVesicle.property = "Expanded";
						aVesicle.pixelSize = parameters.pixelSize;
						aVesicle.pixelSizeUnit = parameters.pixelUnit;
						current_secretion_event.addVesicleAt1st(aVesicle);
					}
					for (int s = 1; s <= parameters.expand_frames_R; s++) {
						if ((fin_slice + s) > num_of_slices)
							continue;
						Vesicle aVesicle = new Vesicle(fin_x, fin_y, current_secretion_event.secretion_event.firstElement().radius, fin_slice + s, imp.getStack(), parameters.fixedExpand);
						aVesicle.isValid = false;
						aVesicle.property = "Expanded";
						aVesicle.pixelSize = parameters.pixelSize;
						aVesicle.pixelSizeUnit = parameters.pixelUnit;
						current_secretion_event.addVesicle(aVesicle);
					}
				}
				double[] yData = current_secretion_event.getCurve();
				double[] diff = current_secretion_event.getDifferential(1);
				double[] xData = current_secretion_event.getTimeCorr();

				// MannKendall mk = new MannKendall(diff);
				// Vector<Double> k2 = mk.test();
				// if (k2.isEmpty()) continue;

				if (parameters.useGrubbs) {
					boolean hasAbnormal;
					Grubbs SHESD = new Grubbs(diff);
					SHESD.alpha = Double.parseDouble(parameters.Grubbs_Alpha);
					hasAbnormal = SHESD.test();
					int k1 = SHESD.getK();

					if (!hasAbnormal)
						continue;
					if (k1 < parameters.expand_frames_L || k1 > diff.length - parameters.expand_frames_R)
						continue;
				}
				// Min dF
				if (parameters.useMinSNR && current_secretion_event.getSNR() < parameters.min_SNR)
					continue;
				// Max displasmsnet
				if (parameters.useMaxDisplacement && current_secretion_event.getMaxDisplacement() > parameters.MaxDisplacement)
					continue;


				MovingLinearRegretionFinder MLR = new MovingLinearRegretionFinder(yData, xData, min_points_num);
				MLR.dofit();
				int peak_position = MLR.findInflection();
				current_secretion_event.setFitPointNum(min_points_num);
				current_secretion_event.setPeakTime(peak_position + current_secretion_event.getStartSlice());
				current_secretion_event.Fit();

				// Max tau
				if (parameters.useMaxTauFrames) {
					if (peak_position < parameters.expand_frames_L)
						continue;
					if (current_secretion_event.Decay_tau > parameters.max_tau)
						continue;

				}
				// Min tau
				// Min tauif (current_secretion_event.Decay_tau <= 0)
				//	continue;
				if (parameters.useMinTauFrames) {
					if (peak_position < parameters.expand_frames_L)
						continue;

					if (current_secretion_event.Decay_tau < min_tau)
						continue;
					// current_secretion_event.Docking_tau > min_tau

				}
				// Min R2 for decay
				
				if (parameters.useMinDecayFitterR2 && current_secretion_event.Decay_R2 < parameters.min_decay_fitter_r2)
					continue;

				final AtomicInteger atomic_i = new AtomicInteger(-1);
				final Vector<Thread> threadsVector = new Vector<Thread>();
				for (int thread_counter = 0; thread_counter < 4; thread_counter++) {
					threadsVector
							.add(new identifyVesicleWithGaussThread(current_secretion_event.secretion_event, atomic_i));
				}
				for (final Thread t : threadsVector) {
					t.start();
				}
				for (final Thread t : threadsVector) {
					try {
						t.join();
					} catch (final InterruptedException ie) {
						// hahaha
					}
				}
				// Min R2 for radius
				
				if (parameters.useGaussienfitter2DR2
						&& current_secretion_event.getPeakGaussfitterRsquare2D() < parameters.min_gaussienfitter2DR2)
					continue;
				//Radius
				if (parameters.useMaxSize
						&& current_secretion_event.getEstimatedPeakSize2D() > parameters.max_estimated_size)
					continue;
				if (parameters.useMinSize
						&& current_secretion_event.getEstimatedPeakSize2D() < parameters.min_estimated_size)
					continue;

				current_secretion_event.setRef(ref);
				current_secretion_event.proprety = "Automatic";
				current_secretion_event.timeInterval = parameters.timeInterval;
				current_secretion_event.timeUnit = parameters.timeUnit;
				_detected_secretions.addElement(current_secretion_event);
				
				ref++;
				progress.setProgress(i * 100 / _detected_events_clone.size());
			}
			progress.close();

			Vector<Vesicle> Vesicles = new Vector<Vesicle>();
			for (Secretion s : _detected_secretions) {
				for (Vesicle v : s.secretion_event) {
					Vesicles.addElement(v);
				}
			}

			if (!Vesicles.isEmpty()) {
				new ResultTableWin(imp, preview_stack, _detected_secretions, parameters);
				preview_stack.setLabels(Vesicles);
				preview_stack.setVisible(true);
				if (parameters.deltamovie) {
					preview_dF_stack.setLabels(Vesicles);
					preview_dF_stack.setVisible(true);
				}
				
				
			} else {
				IJ.showMessage("No event detected");
			}
			previewButton.setEnabled(true);
			nextButton.setEnabled(true);
			backButton.setEnabled(true);

		}
	}

	private class identifyVesicleWithGaussThread extends Thread {
		private final Vector<Vesicle> in;
		private final AtomicInteger atomic_i;

		identifyVesicleWithGaussThread(Vector<Vesicle> in, AtomicInteger atomic_i) {
			this.in = in;
			this.atomic_i = atomic_i;
		}

		@Override
		public void run() {
			int i;
			while ((i = atomic_i.incrementAndGet()) < in.size()) {
				if (in.elementAt(i).isValid)
					in.elementAt(i).get2DGaussFitter();
			}
		}
	}
	
	private void BleachingCorrection() {
		
		PhotolechingCorrection.MeshBleachingCorrection(imp);
		imp.updateAndDraw();
	}

	private void RestoreImage() {
		imp.setImage(origin.duplicate()); 
		imp.updateAndDraw();
	}


}
