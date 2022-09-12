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

import ExocytosisAnalyzer.datasets.Parameters;
import ExocytosisAnalyzer.datasets.Secretion;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import net.miginfocom.swing.MigLayout;

public class TrackingWindows extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	
	private JTextField SpatialSearchRadiusField;
	private JTextField TemporalSearchDepthField;
	private JTextField minimalEventSizeField;
	private DecimalFormat format = new DecimalFormat("#0.00#");
	private JCheckBox Showlist = new JCheckBox("Show Tracking list", false);
	
	//private VesicleParametres v_paras;
	//public Vector<Secretion> detected_events;

	public TrackingWindows(Parameters parameters) {
		
		if (!parameters.loadCustomParameters)
			parameters.InitialTrackingParameters();
		
		Border titleBorder1 = BorderFactory.createTitledBorder("Spot Tracking (vesicle trajectory)");
		this.setBorder(titleBorder1);
		
		//v_paras = paras;
		//detected_events = new Vector<Secretion>();
		JLabel SpatialSearchRadiusFieldLabel = new JLabel("Spatial searching range: ");
		JLabel TimeSearchDepthFieldLabel = new JLabel("Temporal searching depth (Gap closing): ");
		JLabel minimalEventSizeFieldLabel = new JLabel("Minimal event size: ");

		
		SpatialSearchRadiusField = new JTextField(String.valueOf(parameters.SpatialsearchRadius), 5);
		TemporalSearchDepthField = new JTextField(String.valueOf(parameters.TemporalSearchDepth), 5);
		minimalEventSizeField = new JTextField(String.valueOf(parameters.minimalEventSize), 5);
		
		JLabel radiusJLabel = new JLabel(
				" " + String.valueOf(format.format(Double.parseDouble(SpatialSearchRadiusField.getText()) * parameters.pixelSize)) + " "
						+ parameters.pixelUnit);
		JLabel TemporalSearchDepthLabel = new JLabel(" frames");
		JLabel minimalEventSizeLabel = new JLabel(" frames");

		JPanel ParametresPanel = new JPanel();
		
		URL spatialImgURL = TrackingWindows.class.getResource("spatial.jpg");
		URL temporalImgURL = TrackingWindows.class.getResource("temporal.jpg");
		
		SpatialSearchRadiusFieldLabel.setToolTipText("<html><img src=" +spatialImgURL+ " width=\"294\" height=\"167\"> <br></html>");
		TimeSearchDepthFieldLabel.setToolTipText("<html><img src=" +temporalImgURL+ " width=\"497\" height=\"228\"> <br></html>");
		
		ParametresPanel.setLayout(new MigLayout("", "[][][]", "[][]"));

		ParametresPanel.add(SpatialSearchRadiusFieldLabel, "cell 0 0");
		ParametresPanel.add(SpatialSearchRadiusField, "cell 1 0");
		ParametresPanel.add(radiusJLabel, "cell 2 0");

		ParametresPanel.add(TimeSearchDepthFieldLabel, "cell 0 1");
		ParametresPanel.add(TemporalSearchDepthField, "cell 1 1");
		ParametresPanel.add(TemporalSearchDepthLabel, "cell 2 1");

		ParametresPanel.add(minimalEventSizeFieldLabel, "cell 0 2");
		ParametresPanel.add(minimalEventSizeField, "cell 1 2");
		ParametresPanel.add(minimalEventSizeLabel, "cell 2 2");
		ParametresPanel.add(Showlist, "cell 0 3");

		SpatialSearchRadiusField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '1' && ch <= '9') ) {
		            e.consume();
		        }
				try {
					parameters.SpatialsearchRadius = Integer.parseInt(SpatialSearchRadiusField.getText());
					radiusJLabel.setText(" "
							+ String.valueOf(format.format(Double.parseDouble(SpatialSearchRadiusField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit);
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
					parameters.SpatialsearchRadius = Integer.parseInt(SpatialSearchRadiusField.getText());
					radiusJLabel.setText(" "
							+ String.valueOf(format.format(Double.parseDouble(SpatialSearchRadiusField.getText()) * parameters.pixelSize))
							+ " " + parameters.pixelUnit);
				} catch (NumberFormatException arg) {
					radiusJLabel.setText(" Invalid number");
				}
			}

		});
		
		TemporalSearchDepthField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '1' && ch <= '9') ) {
		            e.consume();
		        }
				try {
					parameters.TemporalSearchDepth = Integer.parseInt(TemporalSearchDepthField.getText());
					TemporalSearchDepthLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					TemporalSearchDepthLabel.setText(" Invalid number");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.TemporalSearchDepth = Integer.parseInt(TemporalSearchDepthField.getText());
					TemporalSearchDepthLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					TemporalSearchDepthLabel.setText(" Invalid number");
				}
			}
		});
		minimalEventSizeField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
		        char ch = e.getKeyChar(); 
		        if(!(ch >= '1' && ch <= '9') ) {
		        	 e.consume();
		        }
				try {
					parameters.minimalEventSize = Integer.parseInt(minimalEventSizeField.getText());
					minimalEventSizeLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					minimalEventSizeLabel.setText(" Invalid number");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					parameters.minimalEventSize = Integer.parseInt(minimalEventSizeField.getText());
					minimalEventSizeLabel.setText(" frames");
				} catch (NumberFormatException arg) {
					minimalEventSizeLabel.setText(" Invalid number");
				}
			}

		});
		
		Showlist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Showlist.isSelected())
					parameters.showlist = true;
				else
					parameters.showlist = false;
			}
		});
		
		
		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton restoreButton = new JButton("Restore");
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SaveDialog saveFileDialog = new SaveDialog("Save parasmeters", parameters.path ,parameters.filename, ".dat");
				String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
				if (saveFilePatch == null ) return;
				File file =new File(saveFilePatch);
				Parameters savedparas = parameters;
				savedparas.InitialSecretionParameters();
			    FileOutputStream out;
			        try {
			            out = new FileOutputStream(file);
			            ObjectOutputStream objOut=new ObjectOutputStream(out);
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
				if (openFilePatch == null ) return;
				
				File file =new File(openFilePatch);
				FileInputStream in;
		        try {
		            in = new FileInputStream(file);
		            ObjectInputStream objIn=new ObjectInputStream(in);
		            parameters.copy( (Parameters) objIn.readObject()) ;
		            objIn.close();
		            parameters.loadCustomParameters = true;
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (ClassNotFoundException e1) {
		            e1.printStackTrace();
		        }
		    	SpatialSearchRadiusField.setText(String.valueOf(parameters.SpatialsearchRadius));
		    	TemporalSearchDepthField.setText(String.valueOf(parameters.TemporalSearchDepth));
		    	minimalEventSizeField.setText(String.valueOf(parameters.minimalEventSize));
		    	Showlist.setSelected(parameters.showlist);
		    	
		    
			}
		});
		restoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parameters.InitialTrackingParameters();
		    	SpatialSearchRadiusField.setText(String.valueOf(parameters.SpatialsearchRadius));
		    	TemporalSearchDepthField.setText(String.valueOf(parameters.TemporalSearchDepth));
		    	minimalEventSizeField.setText(String.valueOf(parameters.minimalEventSize));
		    	Showlist.setSelected(parameters.showlist);
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
		this.add(ParametresPanel,BorderLayout.NORTH);
		this.add(ParametresPanel2,BorderLayout.SOUTH);
		
	}


}
