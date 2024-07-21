package ExocytosisAnalyzer.GUI;

import java.text.NumberFormat;
import java.util.Locale;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ExocytosisAnalyzer.datasets.Parameters;
import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.detection.MovingLinearRegretionFinder;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import ij.io.FileSaver;
import ij.io.SaveDialog;
import ij.plugin.MontageMaker;
import ij.plugin.Zoom;
import ij.process.ImageProcessor;
import ij.process.ImageConverter;
import net.miginfocom.swing.MigLayout;

public class ResultTableWin extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ResultTable secretionEventList; // result table
	private DefaultTableModel result_dtmS; // result table model
	private ImagePlus image; // film Original
	private ImagePlus output_ips;
	private PlotWindow intensity_Plot_win;
	// private PlotWindow grubbs_Plot_win;
	private PlotWindow diff_Plot_win;
	private PlotWindow RingSimpling_win;
	private PlotWindow gauss_H_win;
	private PlotWindow gauss_V_win;
	private ImagePlus secretion_Film_ips;
	private ImagePlus montage_ips;
	private double tauHisBin = 2;
	private double radiusHisBin = 0;
	private int symbolSize = 4;
	private String symbolType = "Circle";
	private String symbolColor = "Red";
	private JLabel totalEvent;

	public ResultStackWindow preview_win; // results film

	Parameters parameters;
	private int selectedTableRow;
	private int filmZoom;
	private int MontageZoom, MontageCol, MontageGap;
	private boolean MontageLabel;

	//private DecimalFormat format = new DecimalFormat("#0.00#");
	Locale locale = Locale.getDefault();
    private NumberFormat format = NumberFormat.getNumberInstance(locale);
	// public VesicleParametres v_paras; // detection parameters for vesicles
	// public SecretionParameters s_paras; // detection parameters for secretion
	public Vector<Secretion> detected_secretions;
	private Secretion selectedSecretion;

	private String[] columnTitleExocytosis;
	private String[] columnTitleVesicle;

	public ResultTableWin(ImagePlus imp, ResultStackWindow preview_win, Vector<Secretion> input_secretions,
			Parameters paras) {
		this.parameters = paras;
		this.image = imp;
		this.preview_win = preview_win;
		this.output_ips = this.preview_win.sw.getImagePlus();

		this.detected_secretions = input_secretions;
		this.setVisible(true);
		this.setTitle("ExoJ: Detected exocytosis list");
		this.selectedTableRow = 0;
		this.secretion_Film_ips = new ImagePlus("Movie");
		this.montage_ips = new ImagePlus("Montage");
		this.filmZoom = 16;
		this.MontageZoom = 4;
		this.MontageGap = 0;

		int Img_width = detected_secretions.firstElement().film.getWidth();
		this.MontageCol = 600 / (Img_width * MontageZoom);
		this.MontageLabel = false;

		// Result Table Window
		// Initial Table

		columnTitleExocytosis = new String[] { 
				"Ref ", // 0
				"Property ", // 1
				"Begin Frame ", // 2
				"Duration ", // 3
				"Peak Frame ", // 4
				"Position (x,y) ", // 5
				"Max. Displacement (pixels) ", // 6
				"τ (" + parameters.timeUnit + ") ", // 7
				"R² for Decay Est. ", // 8
				"dF/σ (MAD) ", // 9
				"F0 ", //10
				"ΔF ", //11
				"R² for Gaussian fit ", // 12
				"Size (FWHM, " + parameters.pixelUnit + ")" // 13
						
		
		};

		columnTitleVesicle = new String[] { 
				"Ref ", // 0
				"Property ", // 1
				"Frame ", // 2
				"Intensity ", // 3
				"x ", // 4
				"y ", // 5
				"R² for Gaussian fit ", // 6
				"Size (FWHM, " + parameters.pixelUnit + ")" // 7
				
		};

		Object[][] tableData = new Object[detected_secretions.size()][8];
		int i = 0;
		for (Secretion s : detected_secretions) {
			tableData[i] = new Object[] { 
					s.getRef(), 
					s.proprety, 
					s.getStartSlice(), 
					s.getDuration(), 
					s.peakTime,
					s.getStartX() + "," + s.getStartY(), 
					format.format(s.getMaxDisplacement()),
					format.format(s.Decay_tau * parameters.timeInterval),
					format.format(s.Decay_R2),
					format.format(s.getSNR()),
					format.format(s.getF_zero()), 
					format.format(s.getDeltaF()),
					format.format(s.getPeakGaussfitterRsquare2D()),
					format.format(s.getEstimatedPeakSize2D() * parameters.pixelSize)
			};
			i++;
		}

		result_dtmS = new DefaultTableModel(tableData, columnTitleExocytosis);
		
		
		secretionEventList = new ResultTable(result_dtmS);
		FitTableColumns(secretionEventList);
		secretionEventList.getTableHeader().setResizingAllowed(false);
		
		//remove apparent size if not selected
		if ((parameters.useMaxSize == false) && (parameters.useMinSize== false)){
			secretionEventList.removeColumn(secretionEventList.getColumnModel().getColumn(13));
		}
		// Event listener
		MouseAdapter ResultListMouseAdapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				selectedTableRow = secretionEventList.rowAtPoint(e.getPoint());
				selectedSecretion = detected_secretions.elementAt(selectedTableRow);
				if (e.getButton() == MouseEvent.BUTTON3) {
					// add right clicks event
					JPopupMenu popupMenu1 = new JPopupMenu();
					JMenuItem menuItem1 = new JMenuItem("  Peak Intensity (F) Plot");
					JMenuItem menuItem2 = new JMenuItem("  1st order differential (dF) Plot");
					JMenuItem menuItem3 = new JMenuItem("  Size Gaussian Fit");
					JMenuItem menuItem4 = new JMenuItem("  Spatial dynamics (Centered rings)");
					JMenuItem menuItem5 = new JMenuItem("  Movie");
					JMenuItem menuItem6 = new JMenuItem("  Montage");
					menuItem1.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								viewCruvePlot(selectedSecretion);
							}
						}
					});
					menuItem2.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								viewDiffFluoPlot(selectedSecretion);
							}
						}
					});
					menuItem3.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								Secretion s = detected_secretions.elementAt(selectedTableRow);
								Vesicle v = s.secretion_event.elementAt(s.peakTime - s.getStartSlice());
								viewVesicleRadiusFitter(v);
							}
						}
					});
					menuItem4.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								viewRingSimplingCruvePlot(selectedSecretion);
							}
						}
					});
					menuItem5.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								viewFilm(selectedSecretion);
							}
						}
					});
					menuItem6.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e2) {
							if (e2.getButton() == MouseEvent.BUTTON1) {
								viewMontage(selectedSecretion);
							}
						}
					});
					popupMenu1.add(menuItem1);
					popupMenu1.add(menuItem2);
					popupMenu1.add(menuItem3);
					popupMenu1.add(menuItem4);
					popupMenu1.add(menuItem5);
					popupMenu1.add(menuItem6);
					popupMenu1.show(e.getComponent(), e.getX(), e.getY());

					// Set a ROI to localize vesicle in result film

					output_ips.setActivated();
					output_ips.setSlice(selectedSecretion.peakTime);

					int RoiX = selectedSecretion.peakX - (parameters.minSize + 8);
					int RoiY = selectedSecretion.peakY - (parameters.minSize + 8);

					output_ips.setRoi(RoiX, RoiY, parameters.minSize*2 + 16, parameters.minSize*2 + 16);
				}
				// add double clicks event
				if (e.getClickCount() == 2) {
					new EventWin(selectedSecretion);
				}
			}
		};

		// Realtime refreshing plot windows

		ListSelectionListener ResultListSelectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (secretionEventList.getSelectedRow() != -1
						&& secretionEventList.getSelectedRow() < detected_secretions.size()) {
					selectedTableRow = secretionEventList.getSelectedRow();
					Secretion s = detected_secretions.elementAt(selectedTableRow);
					output_ips.setActivated();
					output_ips.setSlice(s.peakTime);

					int RoiX = s.peakX - (parameters.minSize + 8);
					int RoiY = s.peakY - (parameters.minSize + 8);
					output_ips.setRoi(RoiX, RoiY, parameters.minSize*2 + 16, parameters.minSize*2 + 16);

					if (secretion_Film_ips.isVisible())
						viewFilm(s);
					if (montage_ips.isVisible())
						viewMontage(s);
					if (intensity_Plot_win != null)
						if (intensity_Plot_win.isVisible())
							viewCruvePlot(s);
					if (diff_Plot_win != null)
						if (diff_Plot_win.isVisible())
							viewDiffFluoPlot(s);
					if (RingSimpling_win != null)
						if (RingSimpling_win.isVisible())
							viewRingSimplingCruvePlot(s);
					if (gauss_H_win != null && gauss_V_win != null) {
						if (gauss_H_win.isVisible() && gauss_V_win.isVisible()) {
							Vesicle v = s.secretion_event.elementAt(s.peakTime - s.getStartSlice());
							viewVesicleRadiusFitter(v);
						}
					}
				}

			}
		};

		// Analysis buttons

		JButton viewCruvePlotButton = new JButton("Peak Intensity (F) Plot");
		viewCruvePlotButton.addActionListener((ActionEvent e) -> {
			viewCruvePlot(detected_secretions.elementAt(selectedTableRow));
		});

		viewCruvePlotButton.setToolTipText("View intensity Curve and fit curve"); 

		JButton viewGrubbsPlotButton = new JButton("(dF) Plot");
		viewGrubbsPlotButton.addActionListener((ActionEvent e) -> {
			viewDiffFluoPlot(detected_secretions.elementAt(selectedTableRow));
		});
		viewGrubbsPlotButton.setToolTipText("View differential curve and critical threshold");

		JButton viewRadiusFitterButton = new JButton("Size");
		viewRadiusFitterButton.addActionListener((ActionEvent e) -> {
			Secretion s = detected_secretions.elementAt(selectedTableRow);
			Vesicle v = s.secretion_event.elementAt(s.peakTime - s.getStartSlice());
			viewVesicleRadiusFitter(v);
		});
		viewRadiusFitterButton.setToolTipText("View 2D Gaussien fit of apparent radius");

		JButton viewRingSimplingCruveButton = new JButton("Spatial dynamics");
		viewRingSimplingCruveButton.addActionListener((ActionEvent e) -> {
			viewRingSimplingCruvePlot(detected_secretions.elementAt(selectedTableRow));
		});

		viewRingSimplingCruveButton.setToolTipText(
				"View intensity curves simpled (centered rings) from different distances form central pixel. Rings radius increase by 1 pixel");

		JButton viewFilmButton = new JButton("Movie");
		viewFilmButton.addActionListener((ActionEvent e) -> {
			viewFilm(detected_secretions.elementAt(selectedTableRow));
		});

		JButton viewMontageButton = new JButton("Montage");
		viewMontageButton.addActionListener((ActionEvent e) -> {
			viewMontage(detected_secretions.elementAt(selectedTableRow));
		});

		JButton viewTauHisBtn = new JButton("Tau Stat.");
		viewTauHisBtn.addActionListener((ActionEvent e) -> {
			viewTauHis();
		});

		JButton viewRadiusHisBtn = new JButton("Size Stat.");
		viewRadiusHisBtn.addActionListener((ActionEvent e) -> {
			viewSizeHis();
		});

		JButton DistributionMap = new JButton("Distribution Map");
		DistributionMap.addActionListener((ActionEvent e) -> {
			viewHotSpot();
		});
		JButton CountingMap = new JButton("Event Counts");
		CountingMap.addActionListener((ActionEvent e) -> {
			viewTemporalMap();
		});

		// Add button
		JButton AddButton = new JButton("Add");
		AddButton.addActionListener((ActionEvent e) -> {
			addSecretionEvent();
		});

		// Remove button
		JButton RemoveButton = new JButton("Remove");
		RemoveButton.addActionListener((ActionEvent e) -> {
			int rowToRemove = secretionEventList.getSelectedRow();
			if (rowToRemove >= secretionEventList.getRowCount())
				return;
			((DefaultTableModel) secretionEventList.getModel()).removeRow(rowToRemove);
			detected_secretions.removeElementAt(rowToRemove);
			secretionEventList.validate();
			secretionEventList.updateUI();

			Vector<Vesicle> Vesicles = new Vector<Vesicle>();
			for (Secretion s : detected_secretions) {
				for (Vesicle v : s.secretion_event) {
					Vesicles.addElement(v);
				}
			}
			preview_win.setLabels(Vesicles);
			refreshSecretionList();
		});

		// Save button
		JButton SaveButton = new JButton("Export current table");
		SaveButton.addActionListener((ActionEvent e) -> {
			try {

				SaveUtils.saveSecretionList(detected_secretions, parameters);
				// SaveUtils.saveSecretionListExcel(columnTitleS,
				// etected_secretions,pixelSize,timeInterval,pixelUnit,timeUnit,image.getTitle());

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// Save Plot button
		JButton SavePlotButton = new JButton("Export all intensity plots");
		SavePlotButton.addActionListener((ActionEvent e) -> {
			SaveDialog saveFileDialog = new SaveDialog("Export intensity curve and fit", parameters.path,
					parameters.filename);
			for (Secretion s : detected_secretions) {
				saveCurvePlot(s, saveFileDialog.getDirectory(), saveFileDialog.getFileName());
			}

		});
		
		// Export all table button
		JButton ExportAllTableButton = new JButton("Export all table");
		ExportAllTableButton.addActionListener((ActionEvent e) -> {
			
			try {
				SaveUtils.saveAllVesicleList(columnTitleVesicle, detected_secretions, parameters);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			  
		});





		// Setting Menu
		JMenuBar menubar = new JMenuBar();
		JMenu settingMenu = new JMenu("Setting");
		JMenuItem filmSetting = new JMenuItem("Set Movie Zoom");
		JMenuItem MontageSetting = new JMenuItem("Montage Setting");

		filmSetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e1) {
				if (e1.getButton() == MouseEvent.BUTTON1) {
					new FilmSettingWin();
				}
			}
		});

		MontageSetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e1) {
				if (e1.getButton() == MouseEvent.BUTTON1) {
					new MontageSettingWin();
				}
			}
		});

		settingMenu.add(filmSetting);
		settingMenu.add(MontageSetting);

		// Save menu

		JMenu SaveMenu = new JMenu("Save");

		JMenuItem saveSecretionList = new JMenuItem("Exocytosis List");
		saveSecretionList.addActionListener((ActionEvent e) -> {
			try {
				SaveUtils.saveSecretionList(detected_secretions, parameters);
				// SaveUtils.saveSecretionListExcel(columnTitleS, detected_secretions,pixelSize,
				// timeInterval,pixelUnit,timeUnit,image.getTitle());

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		JMenuItem saveSelectedEvent = new JMenuItem("Selected event");
		saveSelectedEvent.addActionListener((ActionEvent e) -> {
			try {
				SaveUtils.saveVesicleList_secretion(selectedSecretion.secretion_event, parameters,
						selectedSecretion.ref_ID);
				/***
				 * SaveUtils.saveVesicleListExcel(columnTitleV,
				 * selectedSecretion.secretion_event, pixelSize, pixelUnit, image.getTitle(),
				 * selectedSecretion.ref_ID);
				 ***/
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		JMenuItem saveFilms = new JMenuItem("Labeled stack");

		saveFilms.addActionListener((ActionEvent e) -> {
			ImagePlus export = image.duplicate();

			for (int j = 1; j <= export.getSlice(); j++) {
				export.setSlice(j);
				export.getProcessor().convertToColorProcessor();
			}
			ImageConverter ic = new ImageConverter(export);
			ic.convertToRGB();
			Vector<Vesicle> Vesicles = new Vector<Vesicle>();

			for (Secretion s : detected_secretions) {
				for (Vesicle v : s.secretion_event) {
					Vesicles.addElement(v);
				}
			}

			export.setColor(GUIWizard.myColor);

			for (Vesicle v : Vesicles) {

				export.setSlice(v.slice);
				export.getProcessor().drawOval((int) Math.round(v.x - (parameters.minSize + 8)),
						(int) Math.round(v.y - (parameters.minSize + 8)),
						(int) Math.round((parameters.minSize * 2 + 16)),
						(int) Math.round((parameters.minSize * 2 + 16)));
			}

			FileSaver saverstack = new FileSaver(export);
			SaveDialog saveFileDialog = new SaveDialog("Save .tif file", image.getTitle() + "_labeled", ".tif");
			String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
			saverstack.saveAsTiffStack(saveFilePatch);
		});

		SaveMenu.add(saveSecretionList);
		SaveMenu.add(saveSelectedEvent);
		SaveMenu.add(saveFilms);

		// View menu

		JMenu ViewMenu = new JMenu("View");

		JMenuItem viewCruvePlotM = new JMenuItem("Peak Intensity (F) Plot");
		viewCruvePlotM.addActionListener((ActionEvent e) -> {
			viewCruvePlot(detected_secretions.elementAt(selectedTableRow));
		});

		viewCruvePlotM.setToolTipText("View intensity curve and fit");

		// JMenuItem viewGrubbsPlotM = new JMenuItem("Grubbs");
		// viewGrubbsPlotM.addActionListener((ActionEvent e) -> {
		// viewGrubbsPlot(detected_secretions.elementAt(selectedTableRow));
		// });
		// viewGrubbsPlotM.setToolTipText("View Grubbs test curve and critical limit");

		JMenuItem viewDiffPlotM = new JMenuItem("dF Curve");
		viewDiffPlotM.addActionListener((ActionEvent e) -> {
			viewDiffFluoPlot(detected_secretions.elementAt(selectedTableRow));
		});
		viewDiffPlotM.setToolTipText("View differential curve and critical limit");

		JMenuItem viewRadiusFitterM = new JMenuItem("Size");
		viewRadiusFitterM.addActionListener((ActionEvent e) -> {
			Secretion s = detected_secretions.elementAt(selectedTableRow);
			Vesicle v = s.secretion_event.elementAt(s.peakTime - s.getStartSlice());
			viewVesicleRadiusFitter(v);
		});
		viewRadiusFitterM.setToolTipText("View the Gaussien fit");

		JMenuItem viewRingSimplingCruveM = new JMenuItem("Spatial dynamics");
		viewRingSimplingCruveM.addActionListener((ActionEvent e) -> {
			viewRingSimplingCruvePlot(detected_secretions.elementAt(selectedTableRow));
		});

		viewRingSimplingCruveM
				.setToolTipText("View intensity curves simpled from different radius ring to central pixel");

		JMenuItem viewFilmM = new JMenuItem("Movie");
		viewFilmM.addActionListener((ActionEvent e) -> {
			viewFilm(detected_secretions.elementAt(selectedTableRow));
		});

		JMenuItem viewMontageM = new JMenuItem("Montage");
		viewMontageM.addActionListener((ActionEvent e) -> {
			viewMontage(detected_secretions.elementAt(selectedTableRow));
		});

		JMenuItem viewTauHisM = new JMenuItem("Tau Hist.");
		viewTauHisM.addActionListener((ActionEvent e) -> {
			viewTauHis();
		});

		JMenuItem viewRadiusHisM = new JMenuItem("Size Hist.");
		viewRadiusHisM.addActionListener((ActionEvent e) -> {
			viewSizeHis();
		});

		JMenuItem viewHotSpotM = new JMenuItem("Distribution map");
		viewHotSpotM.addActionListener((ActionEvent e) -> {
			viewHotSpot();
		});
		JMenuItem viewTMapM = new JMenuItem("Event counts");
		viewTMapM.addActionListener((ActionEvent e) -> {
			viewTemporalMap();
		});

		ViewMenu.add(viewCruvePlotM);
		ViewMenu.add(viewMontageM);
		ViewMenu.add(viewFilmM);
		ViewMenu.add(viewRadiusFitterM);
		ViewMenu.add(viewRadiusHisM);
		ViewMenu.add(viewTauHisM);
		ViewMenu.add(viewDiffPlotM);
		ViewMenu.add(viewRingSimplingCruveM);
		ViewMenu.add(viewHotSpotM);
		ViewMenu.add(viewTMapM);

		// Sort menu

		JMenu SortMenu = new JMenu("Sort");

		JMenuItem SortByRef = new JMenuItem("Sort by Ref. ");
		SortByRef.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_Ref());
			refreshSecretionList();
		});

		JMenuItem SortByProprety = new JMenuItem("Sort by proprety ");
		SortByRef.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_Proprety());
			refreshSecretionList();
		});

		JMenuItem SortBystartFrame = new JMenuItem("Sort by start frame ");
		SortBystartFrame.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_startFrame());
			refreshSecretionList();
		});

		JMenuItem SortByDuration = new JMenuItem("Sort by duration ");
		SortByDuration.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_Duration());
			refreshSecretionList();
		});

		JMenuItem SortBypeakFrame = new JMenuItem("Sort by peak frame ");
		SortBypeakFrame.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_peakFrame());
			refreshSecretionList();
		});

		JMenuItem SortMovDistance = new JMenuItem("Sort by displacement  ");
		SortMovDistance.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_MovDistance());
			refreshSecretionList();
		});

		JMenuItem SortByRadius = new JMenuItem("Sort by size");
		SortByRadius.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_Size());
			refreshSecretionList();
		});

		JMenuItem SortByRadiusR2 = new JMenuItem("Sort by R² for size ");
		SortByRadius.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_RadiusR2());
			refreshSecretionList();
		});

		JMenuItem SortByDecayTau = new JMenuItem("Sort by decay Tau ");
		SortByDecayTau.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_Decay_tau());
			refreshSecretionList();
		});

		JMenuItem SortByDecayR2 = new JMenuItem("Sort by R² for decay ");
		SortByDecayR2.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_decayR2());
			refreshSecretionList();
		});

		JMenuItem SortBySNR = new JMenuItem("Sort by dF/σ ");
		SortBySNR.addActionListener((ActionEvent e) -> {
			Collections.sort(detected_secretions, new comparator_SNR());
			refreshSecretionList();

		});

		SortMenu.add(SortByRef);
		SortMenu.add(SortByProprety);
		SortMenu.add(SortBystartFrame);
		SortMenu.add(SortByDuration);
		SortMenu.add(SortBypeakFrame);
		SortMenu.add(SortMovDistance);
		SortMenu.add(SortByRadius);
		SortMenu.add(SortByRadiusR2);
		SortMenu.add(SortByDecayTau);
		SortMenu.add(SortByDecayR2);
		SortMenu.add(SortBySNR);

		menubar.add(SaveMenu);
		menubar.add(settingMenu);
		menubar.add(ViewMenu);
		menubar.add(SortMenu);

		secretionEventList.addMouseListener(ResultListMouseAdapter);
		secretionEventList.getSelectionModel().addListSelectionListener(ResultListSelectionListener);
		// Sort
		final JTableHeader tableHeader = secretionEventList.getTableHeader();
		tableHeader.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int choose = tableHeader.columnAtPoint(e.getPoint());

				if (choose == 0) {
					Collections.sort(detected_secretions, new comparator_Ref());
					refreshSecretionList();
				}

				if (choose == 1) {
					Collections.sort(detected_secretions, new comparator_Proprety());
					refreshSecretionList();
				}

				if (choose == 2) {
					Collections.sort(detected_secretions, new comparator_startFrame());
					refreshSecretionList();
				}

				if (choose == 3) {
					Collections.sort(detected_secretions, new comparator_Duration());
					refreshSecretionList();
				}
				if (choose == 4) {
					Collections.sort(detected_secretions, new comparator_peakFrame());
					refreshSecretionList();
				}
				if (choose == 6) {
					Collections.sort(detected_secretions, new comparator_MovDistance());
					refreshSecretionList();
				}
				if (choose == 7) {
					Collections.sort(detected_secretions, new comparator_Decay_tau());
					refreshSecretionList();
				}
				if (choose == 8) {
					Collections.sort(detected_secretions, new comparator_decayR2());
					refreshSecretionList();
				}
				if (choose == 9) {
					Collections.sort(detected_secretions, new comparator_SNR());
					refreshSecretionList();
				}
				if (choose == 10) {
					Collections.sort(detected_secretions, new comparator_F_zero());
					refreshSecretionList();
				}
				if (choose == 11) {
					Collections.sort(detected_secretions, new comparator_DeltaF());
					refreshSecretionList();
				}
				if (choose == 12) {
					Collections.sort(detected_secretions, new comparator_RadiusR2());
					refreshSecretionList();
				}
				if (choose == 13) {
					Collections.sort(detected_secretions, new comparator_Size());
					refreshSecretionList();
				}

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		// Initial Window "resultWindows"
		JScrollPane resutWinTablePanel = new JScrollPane(secretionEventList);
		resutWinTablePanel.setSize(new Dimension(1000, 500));

		JPanel resutWinButtonPanel_TOP = new JPanel();
		JPanel resutWinButtonPanel_BOTTOM = new JPanel();

		JPanel resutWinButtonPanel_Infos = new JPanel();
		JPanel resutWinButtonPanel_BottomButton = new JPanel();

		// count event number
		int event_t = 0;
		int event_a = 0;
		int event_m = 0;
		for (Secretion s : detected_secretions) {
			if (s.proprety == "Automatic")
				event_a++;
			if (s.proprety == "Manual")
				event_m++;
			event_t++;
		}

		totalEvent = new JLabel(String.valueOf(event_t) + " events detected, " + String.valueOf(event_a)
				+ " automatically detected events, " + String.valueOf(event_m) + " manually added events.");

		JLabel addSecetionHelp = new JLabel("Set a ROI over the missing event in the result window to add it");

		resutWinButtonPanel_TOP.setSize(new Dimension(600, 100));
		resutWinButtonPanel_TOP.setLayout(new FlowLayout());
		resutWinButtonPanel_TOP.add(viewCruvePlotButton);
		resutWinButtonPanel_TOP.add(viewGrubbsPlotButton);
		resutWinButtonPanel_TOP.add(viewRadiusFitterButton);
		resutWinButtonPanel_TOP.add(viewRingSimplingCruveButton);
		resutWinButtonPanel_TOP.add(viewFilmButton);
		resutWinButtonPanel_TOP.add(viewMontageButton);
		resutWinButtonPanel_TOP.add(DistributionMap);
		resutWinButtonPanel_TOP.add(CountingMap);
		resutWinButtonPanel_TOP.add(viewRadiusHisBtn);
		resutWinButtonPanel_TOP.add(viewTauHisBtn);

		resutWinButtonPanel_Infos.setSize(new Dimension(800, 100));
		resutWinButtonPanel_Infos.add(totalEvent);

		resutWinButtonPanel_BottomButton.setSize(new Dimension(800, 100));
		resutWinButtonPanel_BottomButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		resutWinButtonPanel_BottomButton.add(addSecetionHelp);
		resutWinButtonPanel_BottomButton.add(AddButton);
		resutWinButtonPanel_BottomButton.add(RemoveButton);
		resutWinButtonPanel_BottomButton.add(SaveButton);
		resutWinButtonPanel_BottomButton.add(SavePlotButton);
		resutWinButtonPanel_BottomButton.add(ExportAllTableButton);
		resutWinButtonPanel_BOTTOM.setLayout(new GridLayout(2, 1, 5, 10));
		resutWinButtonPanel_BOTTOM.add(resutWinButtonPanel_Infos);
		resutWinButtonPanel_BOTTOM.add(resutWinButtonPanel_BottomButton);

		// this.setSize(new Dimension(600, 600));
		this.setJMenuBar(menubar);
		this.setLayout(new BorderLayout());
		this.add(resutWinButtonPanel_TOP, BorderLayout.PAGE_START);

		this.add(resutWinTablePanel, BorderLayout.CENTER);

		this.add(resutWinButtonPanel_BOTTOM, BorderLayout.PAGE_END);
		this.pack();
		this.setVisible(true);

		// mouse click event for result film
		output_ips.getCanvas().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = 0;
				Point clickPoint = ((ImageCanvas) e.getSource()).getCursorLoc();
				int currentSlice = output_ips.getCurrentSlice();
				for (int i = 0; i < detected_secretions.size(); i++) {
					if (currentSlice >= detected_secretions.elementAt(i).getStartSlice()
						&& currentSlice <= detected_secretions.elementAt(i).getFinSlice()
						&& Math.abs(clickPoint.x - detected_secretions.elementAt(i).getStartX()) <= (5 + parameters.minSize)
						&& Math.abs(clickPoint.y - detected_secretions.elementAt(i).getStartY()) <= (5 + parameters.minSize)) {
						row = i;
					}
				}

				if (e.getClickCount() == 2) {
					secretionEventList.changeSelection(row, 0, false, false);

				}
			}
		});
	}

	private void viewVesicleRadiusFitter(Vesicle v) {

		double[] hProfile = v.getHorizontalProfile();
		double[] vProfile = v.getVerticalProfile();
		v.get2DGaussFitter();
		double[] Gauss2D_paras = v.Gauss2DParam;
		double[] xdata = new double[v.image_height];
		double[] hFitter = new double[xdata.length];
		double[] vFitter = new double[xdata.length];
		for (int i = 0; i < xdata.length; i++) {
			xdata[i] = i;
			hFitter[i] = Gauss2D_paras[0] + Gauss2D_paras[1]
					* Math.exp(-(Math.pow((xdata[i] - Gauss2D_paras[2]), 2) / (2 * Math.pow(Gauss2D_paras[4], 2))));
			vFitter[i] = Gauss2D_paras[0] + Gauss2D_paras[1]
					* Math.exp(-(Math.pow((xdata[i] - Gauss2D_paras[3]), 2) / (2 * Math.pow(Gauss2D_paras[4], 2))));
		}

		// raw data
		Plot intensityPlotH = new Plot("Horizontal profile", "Pixels", "Intensity");
		intensityPlotH.addPoints(xdata, hProfile, Plot.LINE);
		// fit curve
		intensityPlotH.setColor(Color.RED);
		
		intensityPlotH.addPoints(xdata, hFitter, Plot.LINE);
		intensityPlotH.addLegend("Intensity \n Fit");

		
		intensityPlotH.updateImage();
		if (gauss_H_win == null || gauss_H_win.isClosed())
			gauss_H_win = intensityPlotH.show();
		if (!gauss_H_win.isVisible())
			gauss_H_win.setVisible(true);
		// new PlotWindow(gauss_H_ips,intensityPlotH);
		gauss_H_win.drawPlot(intensityPlotH);
		intensityPlotH.setLimitsToFit(true);
		
		
		Plot intensityPlotV = new Plot("Vertical profile", "Pixels", "Intensity");
		intensityPlotV.addPoints(xdata, vProfile, Plot.LINE);

		// fit curve
		intensityPlotV.setColor(Color.RED);
		intensityPlotV.addPoints(xdata, vFitter, Plot.LINE);
		intensityPlotV.addLegend("Intensity \n  Fit");

		intensityPlotV.updateImage();
		// new PlotWindow(gauss_V_ips,intensityPlotV);
		if (gauss_V_win == null || gauss_V_win.isClosed())
			gauss_V_win = intensityPlotV.show();
		if (!gauss_V_win.isVisible())
			gauss_V_win.setVisible(true);
		gauss_V_win.drawPlot(intensityPlotV);
		intensityPlotV.setLimitsToFit(true);
	}

	private void viewFilm(Secretion aSecretion) {

		if (secretion_Film_ips == null) {
			secretion_Film_ips = new ImagePlus("Movie", aSecretion.film.duplicate());
			secretion_Film_ips.show();
			ImageWindow secretion_Film_win = secretion_Film_ips.getWindow();
			secretion_Film_win.setTitle("Movie");
			int Img_width = secretion_Film_ips.getWidth();
			int Img_height = secretion_Film_ips.getHeight();

			ImageCanvas secretion_ic = secretion_Film_ips.getCanvas();
			Rectangle srcRect = secretion_ic.getSrcRect();

			int srcWidth = srcRect.width;
			int srcHeight = srcRect.height;

			// Set a scale for a better preview film

			secretion_ic.setMagnification(filmZoom);
			int newWidth = Img_width * filmZoom;
			int newHeight = Img_height * filmZoom;
			secretion_Film_win.setSize(newWidth + 200, newHeight + 200);
			secretion_ic.setSize(newWidth, newHeight);
			secretion_ic.setSourceRect(new Rectangle(0, 0, srcWidth, srcHeight));
			secretion_ic.repaint();
			secretion_Film_win.pack();
		} else {
			secretion_Film_ips.setStack(aSecretion.film.duplicate());

			if (!secretion_Film_ips.isVisible())
				secretion_Film_ips.show();
			secretion_Film_ips.setTitle("Movie");
			int Img_width = secretion_Film_ips.getWidth();
			int Img_height = secretion_Film_ips.getHeight();
			ImageWindow secretion_Film_win = secretion_Film_ips.getWindow();
			ImageCanvas secretion_ic = secretion_Film_ips.getCanvas();
			Rectangle srcRect = secretion_ic.getSrcRect();

			int srcWidth = srcRect.width;
			int srcHeight = srcRect.height;

			// Set a scale for a better preview film

			secretion_ic.setMagnification(filmZoom);
			int newWidth = Img_width * filmZoom;
			int newHeight = Img_height * filmZoom;
			secretion_ic.setSize(newWidth, newHeight);
			secretion_Film_win.setSize(newWidth + 200, newHeight + 200);
			secretion_ic.setSourceRect(new Rectangle(0, 0, srcWidth, srcHeight));
			secretion_ic.repaint();
			secretion_Film_win.pack();
		}
	}

	private void viewMontage(Secretion aSecretion) {

		if (montage_ips == null) {
			montage_ips = new ImagePlus("Montage");
			MontageMaker montage = new MontageMaker();
			int rows = aSecretion.film.getSize() / MontageCol;
			if (rows ==0 ) rows = 1;
			montage_ips.setProcessor(montage.makeMontage2(new ImagePlus("Montage", aSecretion.film), MontageCol, rows,
					1, 1, aSecretion.film.getSize(), 1, MontageGap, MontageLabel).getProcessor());

			Button settingBTN = new Button("Setting");
			settingBTN.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						new MontageSettingWin();
					}
				}
			});
			montage_ips.show();
			montage_ips.getWindow().add(settingBTN);
			montage_ips.getWindow().setSize(montage_ips.getWindow().getWidth(),
					montage_ips.getWindow().getHeight() + 60);
			Zoom.set(montage_ips,MontageZoom);
			montage_ips.getWindow().pack();

		} else {
			ImagePlus secretion_ips = new ImagePlus("Movie for montage", aSecretion.film.duplicate());
			MontageMaker montage = new MontageMaker();
			int rows = secretion_ips.getStackSize() / MontageCol;
			if (rows ==0 ) rows = 1;
			ImageProcessor ip = montage.makeMontage2(secretion_ips, MontageCol, rows, 1, 1,
					secretion_ips.getStackSize(), 1, MontageGap, MontageLabel).getProcessor();
			montage_ips.setProcessor(ip);
			

			if (!montage_ips.isVisible()) {
				montage_ips.show();

				Button settingBTN = new Button("Setting");
				settingBTN.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							new MontageSettingWin();
						}
					}
				});
				montage_ips.show();
				montage_ips.getWindow().add(settingBTN);
				montage_ips.getWindow().setSize(montage_ips.getWindow().getWidth(),
						montage_ips.getWindow().getHeight() + 60);
				montage_ips.getWindow().pack();
			}
			montage_ips.setTitle("Montage");
			Zoom.set(montage_ips,MontageZoom);
			montage_ips.updateAndDraw();
		}

	}

	private void viewCruvePlot(Secretion aSecretion) {
		// Preview plot

		double[] x = new double[aSecretion.getDuration()];
		double[] fit_x = new double[aSecretion.getFinSlice() - aSecretion.peakTime + 1];
		double[] fit_y = new double[aSecretion.getFinSlice() - aSecretion.peakTime + 1];

		for (int i = 0; i < x.length; i++) {
			x[i] = aSecretion.getStartSlice() + i;
		}

		for (int i = 0; i < fit_x.length; i++) {
			fit_y[i] = aSecretion.Decay_bk + aSecretion.Decay_peak * Math.exp(-i / aSecretion.Decay_tau);
			fit_x[i] = aSecretion.peakTime + i;
		}

		// raw data
		Plot intensityPlot = new Plot("Peak Intensity (F) and Fit", "Frames", "Intensity");
		intensityPlot.addPoints(x, aSecretion.getCurve(), Plot.CIRCLE);
		// fit curve
		intensityPlot.setColor(Color.RED);
		intensityPlot.addPoints(fit_x, fit_y, Plot.LINE);

		intensityPlot
				.addLegend("Raw intensity \n Fit: y=" + format.format(aSecretion.Decay_bk) + "+" + format.format(aSecretion.Decay_peak)
						+ "*" + "exp(-x/" + format.format(aSecretion.Decay_tau * parameters.timeInterval) + ")");

		if (intensity_Plot_win == null || intensity_Plot_win.isClosed())
			intensity_Plot_win = intensityPlot.show();

		if (!intensity_Plot_win.isVisible())
			intensity_Plot_win.setVisible(true);
		intensity_Plot_win.setTitle("Intensity");
		intensity_Plot_win.drawPlot(intensityPlot);
		intensityPlot.setLimitsToFit(true);

	}

	private void viewDiffFluoPlot(Secretion aSecretion) {

		double[] diff = aSecretion.getDifferential(1);
		Plot dFplot = new Plot("1st order differential (dF) Plot", "Frames", "Differential");
		double[] x = new double[aSecretion.getDuration()];
		for (int i = 0; i < x.length; i++) {
			x[i] = aSecretion.getStartSlice() + i;
		}

		dFplot.setColor(Color.BLACK);
		dFplot.add("line", x, diff);

		double threshold = aSecretion.getMAD() * parameters.min_SNR;
		dFplot.setColor(Color.RED);
		dFplot.drawLine(x[0], threshold, x[x.length - 1], threshold);
		dFplot.drawLine(x[0], -threshold, x[x.length - 1], -threshold);
		dFplot.addText("Threshold", x[0] + 0.2, threshold);

		dFplot.setColor(Color.GREEN);
		dFplot.drawLine(x[0], aSecretion.getMAD(), x[x.length - 1], aSecretion.getMAD());
		dFplot.drawLine(x[0], -aSecretion.getMAD(), x[x.length - 1], -aSecretion.getMAD());
		dFplot.addText("σ", x[0] + 0.2, aSecretion.getMAD());

		if (diff_Plot_win == null || diff_Plot_win.isClosed())
			diff_Plot_win = dFplot.show();

		if (!diff_Plot_win.isVisible())
			diff_Plot_win.setVisible(true);

		diff_Plot_win.setTitle("Differential Fluorescence Intensity");
		diff_Plot_win.drawPlot(dFplot);
		dFplot.setLimitsToFit(true);

	}

	private void viewRingSimplingCruvePlot(Secretion aSecretion) {

		double[] x = new double[aSecretion.getDuration()];
		Vector<double[]> RTCruve = aSecretion.getRingSimplingCurve();
		String legend = new String("Center");

		int cruveNum = RTCruve.size();
		if (cruveNum > 7)
			cruveNum = 7;

		for (int i = 0; i < x.length; i++) {
			x[i] = aSecretion.getStartSlice() + i;
		}

		// Central point
		Plot RingSimpling = new Plot("Spatial dynamics", "Frames", "Intensity");
		RingSimpling.addPoints(x, RTCruve.firstElement(), Plot.LINE);

		// Ring simpling
		for (int i = 1; i < cruveNum; i++) {
			RingSimpling.setColor(new Color( (i - 1) * 40, ((i - 1) * 80)%255, 295 - i * 40) );
			RingSimpling.addPoints(x, RTCruve.elementAt(i), Plot.LINE);
			legend = legend + "	r = " + String.valueOf(i) + " pixels";
		}
		RingSimpling.setColor(Color.BLACK);
		RingSimpling.addLegend(legend);

		if (RingSimpling_win == null || RingSimpling_win.isClosed())
			RingSimpling_win = RingSimpling.show();

		if (!RingSimpling_win.isVisible())
			RingSimpling_win.setVisible(true);

		RingSimpling_win.setTitle("Spatial dynamics");
		RingSimpling_win.drawPlot(RingSimpling);
		RingSimpling.setLimitsToFit(true);
	}

	private void viewHotSpot() {
		Vector<Integer> x_coors = new Vector<Integer>();
		Vector<Integer> y_coors = new Vector<Integer>();

		for (Secretion s : detected_secretions) {
			x_coors.addElement(s.getStartX());
			y_coors.addElement(s.getStartY());
		}

		ImageProcessor HotSpotMap = output_ips.getProcessor().duplicate().convertToRGB();

		switch (symbolColor) {
		case "Red":
			HotSpotMap.setColor(Color.RED);
			break;
		case "Yellow":
			HotSpotMap.setColor(Color.YELLOW);
			break;
		case "Green":
			HotSpotMap.setColor(Color.GREEN);
			break;
		case "Blue":
			HotSpotMap.setColor(Color.BLUE);
			break;
		case "White":
			HotSpotMap.setColor(Color.WHITE);
			break;
		}

		switch (symbolType) {
		case "Dot":
			for (int i = 0; i < x_coors.size(); i++) {
				HotSpotMap.setLineWidth(symbolSize);
				HotSpotMap.drawDot(x_coors.elementAt(i), y_coors.elementAt(i));
			}
			break;
		case "Circle":
			for (int i = 0; i < x_coors.size(); i++) {
				HotSpotMap.setLineWidth(1);
				HotSpotMap.drawOval(x_coors.elementAt(i) - symbolSize, y_coors.elementAt(i) - symbolSize,
						symbolSize * 2, symbolSize * 2);
			}
			break;
		case "Rect":
			for (int i = 0; i < x_coors.size(); i++) {
				HotSpotMap.setLineWidth(1);
				HotSpotMap.drawRect(x_coors.elementAt(i) - symbolSize, y_coors.elementAt(i) - symbolSize,
						symbolSize * 2, symbolSize * 2);
			}
			break;
		}

		ImagePlus output = new ImagePlus("Distribution Map");
		output.setProcessor(HotSpotMap);
		output.show();
		output.getWindow().setTitle("Distribution Map : " + parameters.filename);
		Button settingBTN = new Button("Setting");
		settingBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					new symbolSettingWin();
				}
			}
		});

		Button refreshBTN = new Button("Refresh");
		refreshBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					output.close();
					viewHotSpot();
				}
			}
		});
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(settingBTN);
		buttonPanel.add(refreshBTN);
		output.getWindow().add(buttonPanel);
		output.getWindow().setSize(output.getWindow().getWidth(), output.getWindow().getHeight() + 20);
		output.getWindow().pack();
	}

	private void viewTemporalMap() {

		double[] temporalMap = new double[image.getStack().getSize()];
		double[] time = new double[image.getStack().getSize()];
		for (int i = 1; i <= time.length; i++) {
			time[i - 1] = i;
		}
		for (Secretion s : detected_secretions) {
			temporalMap[s.peakTime - 1]++;
		}

		Plot temporalPlot = new Plot("Event counts", "Frames", "Frequence");
		temporalPlot.addPoints(time, temporalMap, Plot.BAR);
		PlotWindow temporalPlotWin = temporalPlot.show();
		temporalPlotWin.setTitle("Event counts : " + parameters.filename);
	}

	private void viewTauHis() {
		double[] tau = new double[detected_secretions.size()];

		for (int j = 0; j < detected_secretions.size(); j++) {
			tau[j] = detected_secretions.elementAt(j).Decay_tau * parameters.timeInterval;

		}

		Plot his_tau = new Plot("Tau distribution", parameters.timeUnit, "");
		his_tau.addHistogram(tau, tauHisBin);
		PlotWindow his_tau_win = his_tau.show();
		his_tau_win.setTitle("Tau distribution : " + parameters.filename);
		Panel settingPanel = new Panel();
		Button changeBTN = new Button("Change");
		Label Binlabel = new Label("binWidth :");
		Label Binlabel2 = new Label(" (set zero for auto-binning)");
		TextField BinField = new TextField(String.valueOf(tauHisBin), 3);

		changeBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					Plot new_histo = new Plot("Tau distribution", parameters.timeUnit, "");
					tauHisBin = Double.valueOf(BinField.getText());
					new_histo.addHistogram(tau, tauHisBin);
					his_tau_win.drawPlot(new_histo);
					his_tau_win.add(settingPanel);
					his_tau_win.setSize(his_tau_win.getWidth(), his_tau_win.getHeight() + 20);
					his_tau_win.pack();
				}
			}
		});

		settingPanel.setLayout(new FlowLayout());
		settingPanel.add(Binlabel);
		settingPanel.add(BinField);
		settingPanel.add(Binlabel2);
		settingPanel.add(changeBTN);
		his_tau_win.add(settingPanel);
		his_tau_win.setSize(his_tau_win.getWidth(), his_tau_win.getHeight() + 20);
		his_tau_win.pack();

	}

	private void viewSizeHis() {
		double[] estimated_radius = new double[detected_secretions.size()];

		for (int j = 0; j < detected_secretions.size(); j++) {
			estimated_radius[j] = detected_secretions.elementAt(j).getEstimatedPeakSize2D() * parameters.pixelSize;
		}

		Plot his_radius = new Plot("Size distribution", parameters.pixelUnit, "");
		his_radius.addHistogram(estimated_radius, radiusHisBin);
		PlotWindow his_radius_win = his_radius.show();
		his_radius_win.setTitle("Size distribution : " + parameters.filename);
		Panel settingPanel = new Panel();
		Button changeBTN = new Button("Change");
		Label Binlabel = new Label("binWidth :");
		Label Binlabel2 = new Label(" (set zero for auto-binning)");
		TextField BinField = new TextField(String.valueOf(radiusHisBin), 3);

		changeBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					Plot new_histo = new Plot("Size distribution", parameters.timeUnit, "");
					radiusHisBin = Double.valueOf(BinField.getText());
					new_histo.addHistogram(estimated_radius, radiusHisBin);
					his_radius_win.drawPlot(new_histo);
					his_radius_win.add(settingPanel);
					his_radius_win.setSize(his_radius_win.getWidth(), his_radius_win.getHeight() + 20);
					his_radius_win.pack();
				}
			}
		});

		settingPanel.setLayout(new FlowLayout());
		settingPanel.add(Binlabel);
		settingPanel.add(BinField);
		settingPanel.add(Binlabel2);
		settingPanel.add(changeBTN);
		his_radius_win.add(settingPanel);
		his_radius_win.setSize(his_radius_win.getWidth(), his_radius_win.getHeight() + 10);
		his_radius_win.pack();

	};

	public void addSecretionEvent() { // from a ROI to add a secretion event

		if (output_ips.getRoi() != null) {
			int roi_slice = output_ips.getCurrentSlice();
			Rectangle bound = output_ips.getRoi().getBounds();
			ImageProcessor roi_imp = image.getStack().getProcessor(roi_slice).duplicate();

			float[] pixels;
			double max_value = 0.0;

			int max_x = 0;
			int max_y = 0;

			float current_intensity;
			pixels = (float[]) roi_imp.convertToFloatProcessor().getPixels();

			for (int corr_y = bound.y; corr_y < (bound.y + bound.height); corr_y++) {
				for (int corr_x = bound.x; corr_x < (bound.x + bound.width); corr_x++) {
					current_intensity = pixels[corr_y * roi_imp.getWidth() + corr_x];
					if (current_intensity > max_value) {
						max_value = current_intensity;
						max_x = corr_x;
						max_y = corr_y;
					}
				}
			}

			// Generate a full length event using current time point as center

			Vector<Vesicle> event_to_add = new Vector<Vesicle>();
			int current_x = max_x;
			int current_y = max_y;
			for (int i = 0; i < roi_slice; i++) {
				Vesicle vesicle_to_add = new Vesicle(current_x, current_y, Math.round(parameters.minSize/2), roi_slice - i,
						image.getStack());
				vesicle_to_add.pixelSize = parameters.pixelSize;
				vesicle_to_add.pixelSizeUnit = parameters.pixelUnit;
				vesicle_to_add.property = "Manual";
				event_to_add.add(0, vesicle_to_add);
				current_x = vesicle_to_add.x;
				current_y = vesicle_to_add.y;
			}
			current_x = max_x;
			current_y = max_y;
			for (int i = roi_slice + 1; i <= image.getStackSize(); i++) {
				Vesicle vesicle_to_add = new Vesicle(current_x, current_y, Math.round(parameters.minSize/2), i, image.getStack());
				vesicle_to_add.pixelSize = parameters.pixelSize;
				vesicle_to_add.pixelSizeUnit = parameters.pixelUnit;
				vesicle_to_add.property = "Manual";
				event_to_add.add(vesicle_to_add);
				current_x = vesicle_to_add.x;
				current_y = vesicle_to_add.y;
			}

			// Select a windows of 21 frames
			int start = roi_slice - parameters.expand_frames_L;
			int end = roi_slice + 15;

			if (start < 1)
				start = 1;
			if (end > image.getStackSize())
				end = image.getStack().getSize();

			double[] xdata = new double[end - start + 1];
			double[] y1data = new double[end - start + 1];
			int time_corr = start;
			for (int i = 0; i < xdata.length; i++) {
				xdata[i] = time_corr + i;
				y1data[i] = event_to_add.elementAt(i + start - 1).getMaxDen();
			}

			TextField frameBeforePeak = new TextField(String.valueOf(start), 3);
			Label frameBeforePeakLabel = new Label("Start at (Frame):");
			TextField frameAfterPeak = new TextField(String.valueOf(end), 3);
			Label frameAfterPeakLabel = new Label("End:");

			Plot plotForAddSecretion = new Plot("Fluorescent profile", "Frame", "Intensity");
			plotForAddSecretion.addPoints(xdata, y1data, Plot.LINE);
			PlotWindow addSecretionWindow = plotForAddSecretion.show();
			addSecretionWindow.setTitle("Add an event");

			Button addSecretion_OKButton = new Button("Add");
			Button addSecretion_RefreshButton = new Button("Refresh");
			Button addSecretion_cancelButton = new Button("Cancel");

			addSecretion_RefreshButton.addActionListener((ActionEvent e1) -> {

				int start2 = Integer.parseInt(frameBeforePeak.getText());
				int end2 = Integer.parseInt(frameAfterPeak.getText());
				double[] xdata2 = new double[end2 - start2 + 1];
				double[] y1data2 = new double[end2 - start2 + 1];
				int time_corr2 = start2;
				for (int i = 0; i < xdata2.length; i++) {
					xdata2[i] = time_corr2 + i;
					y1data2[i] = event_to_add.elementAt(i + start2 - 1).getMaxDen();
				}
				plotForAddSecretion.replace(0, "line", xdata2, y1data2);
				plotForAddSecretion.setLimitsToFit(true);
			});

			addSecretion_OKButton.addActionListener((ActionEvent c1) -> {
				Vector<Vesicle> vesicle_list = new Vector<Vesicle>();
				// generate secretion

				int userSelectStart = Integer.parseInt(frameBeforePeak.getText());
				int userSelectEnd = Integer.parseInt(frameAfterPeak.getText());

				for (int i = userSelectStart - 1; i < userSelectEnd; i++) {
					vesicle_list.addElement(event_to_add.elementAt(i));
				}
				Secretion secretion_to_add = new Secretion(vesicle_list);
				secretion_to_add.timeInterval = parameters.timeInterval;
				secretion_to_add.timeUnit = parameters.timeUnit;
				MovingLinearRegretionFinder MLR = new MovingLinearRegretionFinder(secretion_to_add.getCurve(),
						secretion_to_add.getTimeCorr(), parameters.minimalPointsForFitter);
				MLR.dofit();

				int peak_position = MLR.findInflection();

				int lastRef = 0;
				secretion_to_add.setFitPointNum(parameters.minimalPointsForFitter);
				secretion_to_add.setPeakTime(peak_position + userSelectStart);
				secretion_to_add.Fit();
				if (!detected_secretions.isEmpty()) {
					lastRef = detected_secretions.lastElement().getRef();
				}
				secretion_to_add.setRef(lastRef + 1);
				secretion_to_add.proprety = "Manual";
				detected_secretions.addElement(secretion_to_add);
				/***
				 * Object rowToAdd[] = new Object[] { detected_secretion.lastElement().getRef(),
				 * detected_secretion.lastElement().proprety,
				 * detected_secretion.lastElement().getStartSlice(),
				 * detected_secretion.lastElement().getDuration(),
				 * detected_secretion.lastElement().peakTime,
				 * detected_secretion.lastElement().getStartX()+ "," +
				 * detected_secretion.lastElement().getStartY(),
				 * format.format(detected_secretion.lastElement().getMaxMovDistance()*
				 * parameters.pixelSize),
				 * format.format(detected_secretion.lastElement().getEstimatedPeakRadius2D() *
				 * parameters.pixelSize),
				 * format.format(detected_secretion.lastElement().getPeakGaussfitterRsquare2D()),
				 * format.format(detected_secretion.lastElement().Decay_tau *
				 * parameters.timeInterval),
				 * format.format(detected_secretion.lastElement().Decay_R2),
				 * format.format(detected_secretion.lastElement().getSNR()),
				 * //detected_secretion.lastElement().functionSimplified };
				 * 
				 * ((DefaultTableModel) secretionEventList.getModel()).addRow(rowToAdd);
				 * secretionEventList.validate(); secretionEventList.updateUI();
				 ***/
				addSecretionWindow.dispose();

				refreshSecretionList();

				// Update label in movie
				Vector<Vesicle> Vesicles = new Vector<Vesicle>();

				for (Secretion s : detected_secretions) {
					for (Vesicle v : s.secretion_event) {
						Vesicles.addElement(v);
					}
				}
				preview_win.setLabels(Vesicles);

			});

			addSecretion_cancelButton.addActionListener((ActionEvent c2) -> {
				addSecretionWindow.dispose();
			});

			Panel addSecretion_buttonPanel = new Panel();

			addSecretion_buttonPanel.setLayout(new FlowLayout());

			addSecretion_buttonPanel.add(frameBeforePeakLabel);
			addSecretion_buttonPanel.add(frameBeforePeak);
			addSecretion_buttonPanel.add(frameAfterPeakLabel);
			addSecretion_buttonPanel.add(frameAfterPeak);
			addSecretion_buttonPanel.add(addSecretion_OKButton);
			addSecretion_buttonPanel.add(addSecretion_RefreshButton);
			addSecretion_buttonPanel.add(addSecretion_cancelButton);

			addSecretionWindow.add(addSecretion_buttonPanel);
			addSecretionWindow.setSize(addSecretionWindow.getWidth(), addSecretionWindow.getHeight() + 10);
			addSecretionWindow.setVisible(true);

		}
	}

	private void refreshSecretionList() {

		if (selectedTableRow > detected_secretions.size())
			selectedTableRow = detected_secretions.size();
		Object[][] tableData = new Object[detected_secretions.size()][8];
		int i = 0;
		for (Secretion s : detected_secretions) {
			tableData[i] = new Object[] { 
					s.getRef(),
					s.proprety,
					s.getStartSlice(),
					s.getDuration(),
					s.peakTime,
					s.getStartX() + "," + s.getStartY(), 
					format.format(s.getMaxDisplacement()),
					format.format(s.Decay_tau * parameters.timeInterval), 
					format.format(s.Decay_R2),
					format.format(s.getSNR()),
					format.format(s.getF_zero()),
					format.format(s.getDeltaF()),
					format.format(s.getPeakGaussfitterRsquare2D()),
					format.format(s.getEstimatedPeakSize2D() * parameters.pixelSize)
					};
			i++;
		}

		result_dtmS = new DefaultTableModel(tableData, columnTitleExocytosis);
		secretionEventList.setModel(result_dtmS);
		secretionEventList.validate();
		secretionEventList.updateUI();
		secretionEventList.changeSelection(selectedTableRow, 0, false, false);
		FitTableColumns(secretionEventList);
		secretionEventList.getTableHeader().setResizingAllowed(false);

		if ((parameters.useMaxSize == false) && (parameters.useMinSize== false))  {
			secretionEventList.removeColumn(secretionEventList.getColumnModel().getColumn(13));
		}

		// count event number
		int event_t = 0;
		int event_a = 0;
		int event_m = 0;
		for (Secretion s : detected_secretions) {
			if (s.proprety == "Automatic")
				event_a++;
			if (s.proprety == "Manual")
				event_m++;
			event_t++;
		}

		totalEvent.setText(String.valueOf(event_t) + " events detected, " + String.valueOf(event_a)
				+ " automatically detected events, " + String.valueOf(event_m) + " manually added events.");
	}

	private void saveCurvePlot(Secretion aSecretion, String path, String name) {
		double[] x = new double[aSecretion.getDuration()];
		double[] fit_x = new double[aSecretion.getFinSlice() - aSecretion.peakTime + 1];
		double[] fit_y = new double[aSecretion.getFinSlice() - aSecretion.peakTime + 1];

		for (int i = 0; i < x.length; i++) {
			x[i] = aSecretion.getStartSlice() + i;
		}

		for (int i = 0; i < fit_x.length; i++) {
			fit_y[i] = aSecretion.Decay_bk + aSecretion.Decay_peak * Math.exp(-i / aSecretion.Decay_tau);
			fit_x[i] = aSecretion.peakTime + i;
		}

		// raw data
		Plot intensityPlot = new Plot("Peak Intensity (F) Plot and Fit", "Frames", "Intensity");
		intensityPlot.addPoints(x, aSecretion.getCurve(), Plot.CIRCLE);
		
		// fit curve
		intensityPlot.setColor(Color.RED);
		intensityPlot.addPoints(fit_x, fit_y, Plot.LINE);

		intensityPlot
		.addLegend("Raw intensity \n Fit: y=" + format.format(aSecretion.Decay_bk) + "+" + format.format(aSecretion.Decay_peak)
				+ "*" + "exp(-x/" + format.format(aSecretion.Decay_tau * parameters.timeInterval) + ")");

		IJ.saveAs(intensityPlot.getImagePlus(), "jpg",
				path + name + "_" + String.valueOf(aSecretion.getRef()) + ".jpg");
	}

	public void FitTableColumns(JTable myTable) {

		JTableHeader header = myTable.getTableHeader();

		int rowCount = myTable.getRowCount();

		Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();

		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();

			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());

			int width = (int) myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable,column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();

			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();

				width = Math.max(width, preferedWidth);

			}

			header.setResizingColumn(column);
			column.setWidth(width + myTable.getIntercellSpacing().width);

		}

	}

	public class comparator_Ref implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getRef() > s2.getRef())
				return 1; // increase
			else if (s1.getRef() < s2.getRef())
				return -1;
			else
				return 0;
		}
	}

	public class comparator_Proprety implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.proprety == "Manual" && s2.proprety == "Automatic")
				return -1; // Manual>Automatic
			else if (s1.proprety == "Automatic" && s2.proprety == "Manual")
				return 1;
			else
				return 0;
		}
	}

	public class comparator_startFrame implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getStartSlice() > s2.getStartSlice())
				return 1; // increase
			else if (s1.getStartSlice() < s2.getStartSlice())
				return -1;
			else
				return 0;
		}
	}

	public class comparator_Duration implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getDuration() > s2.getDuration())
				return -1; // decrease
			else if (s1.getDuration() < s2.getDuration())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_peakFrame implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.peakTime > s2.peakTime)
				return 1; // increase
			else if (s1.peakTime < s2.peakTime)
				return -1;
			else
				return 0;
		}
	}

	public class comparator_decayR2 implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.Decay_R2 > s2.Decay_R2)
				return -1; // decrease
			else if (s1.Decay_R2 < s2.Decay_R2)
				return 1;
			else
				return 0;
		}
	}

	public class comparator_Size implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getEstimatedPeakSize2D() > s2.getEstimatedPeakSize2D())
				return -1; // decrease
			else if (s1.getEstimatedPeakSize2D() < s2.getEstimatedPeakSize2D())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_RadiusR2 implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getPeakGaussfitterRsquare2D() > s2.getPeakGaussfitterRsquare2D())
				return -1; // decrease
			else if (s1.getPeakGaussfitterRsquare2D() < s2.getPeakGaussfitterRsquare2D())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_SNR implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getSNR() > s2.getSNR())
				return -1; // decrease
			else if (s1.getSNR() < s2.getSNR())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_VesicleSize implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getVesicleSize() > s2.getVesicleSize())
				return -1; // decrease
			else if (s1.getVesicleSize() < s2.getVesicleSize())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_Decay_tau implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.Decay_tau > s2.Decay_tau)
				return -1; // decrease
			else if (s1.Decay_tau < s2.Decay_tau)
				return 1;
			else
				return 0;
		}
	}

	public class comparator_MovDistance implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getMaxDisplacement() > s2.getMaxDisplacement())
				return -1; // decrease
			else if (s1.getMaxDisplacement() < s2.getMaxDisplacement())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_F_zero implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getF_zero() > s2.getF_zero())
				return -1; // decrease
			else if (s1.getF_zero() < s2.getF_zero())
				return 1;
			else
				return 0;
		}
	}

	public class comparator_DeltaF implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Secretion s1 = (Secretion) o1;
			Secretion s2 = (Secretion) o2;
			if (s1.getDeltaF() > s2.getDeltaF())
				return -1; // decrease
			else if (s1.getDeltaF() < s2.getDeltaF())
				return 1;
			else
				return 0;
		}
	}

	private class FilmSettingWin extends JFrame {

		private static final long serialVersionUID = 1L;

		FilmSettingWin() {
			this.setTitle("Set movie defaut zoom...");
			this.setLayout(new FlowLayout());
			this.setSize(200, 200);
			this.add(new JLabel("Defaut zoom : "));

			JTextField filmMagField = new JTextField(String.valueOf(filmZoom), 5);
			this.add(filmMagField);
			JButton SetfilmMagButton = new JButton("OK");
			SetfilmMagButton.addActionListener((ActionEvent e2) -> {
				filmZoom = Integer.valueOf(filmMagField.getText());
				this.setVisible(false);
			});
			this.add(SetfilmMagButton);
			this.setVisible(true);
		}

	}

	private class MontageSettingWin extends JFrame {

		private static final long serialVersionUID = 1L;

		MontageSettingWin() {
			this.setLayout(new MigLayout("", "[][]", "[][]"));
			JLabel MontageColLabel = new JLabel("Defaut columns : ");
			JLabel MontageGapLabel = new JLabel("Defaut Gap (pixels) : ");
			JLabel MontageZoomLabel = new JLabel("Defaut zoom : ");
			JLabel MontageLabelLabel = new JLabel("Label: ");
			JTextField MontageColField = new JTextField(String.valueOf(MontageCol), 5);
			JTextField MontageGapField = new JTextField(String.valueOf(MontageGap), 5);
			JTextField MontageZoomField = new JTextField(String.valueOf(MontageZoom), 5);
			JCheckBox MontageLabelBox = new JCheckBox("", MontageLabel);
			this.add(MontageColLabel, "cell 0 0");
			this.add(MontageColField, "cell 1 0");
			this.add(MontageGapLabel, "cell 0 1");
			this.add(MontageGapField, "cell 1 1");
			this.add(MontageZoomLabel, "cell 0 2");
			this.add(MontageZoomField, "cell 1 2");
			this.add(MontageLabelLabel, "cell 0 3");
			this.add(MontageLabelBox, "cell 1 3");

			this.setSize(300, 200);

			JButton SetMontageSettingButton = new JButton("OK");
			SetMontageSettingButton.addActionListener((ActionEvent e2) -> {
				MontageCol = Integer.valueOf(MontageColField.getText());
				MontageGap = Integer.valueOf(MontageGapField.getText());
				MontageZoom = Integer.valueOf(MontageZoomField.getText());
				MontageLabel = MontageLabelBox.isSelected();
				if (montage_ips != null) {
					ImagePlus secretion_ips = new ImagePlus("Secretion images", detected_secretions.elementAt(selectedTableRow).film);
					MontageMaker montage = new MontageMaker();
					int rows = secretion_ips.getStackSize() / MontageCol;
					if (rows ==0 ) rows = 1;
					montage_ips.setProcessor(montage.makeMontage2(secretion_ips, MontageCol, rows, 1, 1,
							secretion_ips.getStackSize(), 1, MontageGap, MontageLabel).getProcessor());
					
					
					Zoom.set(montage_ips,MontageZoom);
					//int newWidth = montage_ips.getWidth() * MontageZoom;
					//int newHeight = montage_ips.getHeight() * MontageZoom;

					//ImageCanvas montage_canvas = montage_ips.getCanvas();
					//montage_canvas.setMagnification(MontageZoom);

					//ImageWindow montage_win = montage_ips.getWindow();
					//montage_win.setSize(newWidth, newHeight);
							
					montage_ips.updateAndDraw();
					;
				}

				this.setVisible(false);
			});
			this.add(SetMontageSettingButton, "cell 1 4");

			this.setVisible(true);
		}

	}

	private class symbolSettingWin extends JFrame {

		private static final long serialVersionUID = 1L;

		symbolSettingWin() {
			this.setTitle("Set distribution map...");
			this.setLayout(new MigLayout("", "[][][]", "[][]"));
			this.setSize(300, 200);
			JLabel symbolSizeLabel = new JLabel("Symbol size : ");
			JLabel symbolSizeLabel2 = new JLabel("(set zero for default)");
			JTextField symbolSizeField = new JTextField(String.valueOf(symbolSize), 6);

			JLabel symbolTypeCollectionLabel = new JLabel("Symbol Type");
			String[] symbolTypeCollection = { "Circle", "Dot", "Rect" };
			JComboBox<String> symbolTypeBox = new JComboBox<String>(symbolTypeCollection);

			JLabel symbolColorCollectionLabel = new JLabel("Symbol Color");
			String[] symbolColorCollection = { "Red", "Yellow", "Green", "Blue", "White" };
			JComboBox<String> symbolColorBox = new JComboBox<String>(symbolColorCollection);

			JButton SetButton = new JButton("OK");
			SetButton.addActionListener((ActionEvent e) -> {
				symbolSize = Integer.valueOf(symbolSizeField.getText());
				symbolType = (String) symbolTypeBox.getSelectedItem();
				symbolColor = (String) symbolColorBox.getSelectedItem();
				this.setVisible(false);

			});

			this.add(symbolSizeLabel, "cell 0 0");
			this.add(symbolSizeField, "cell 1 0");
			this.add(symbolSizeLabel2, "cell 2 0");
			this.add(symbolTypeCollectionLabel, "cell 0 1");
			this.add(symbolTypeBox, "cell 1 1");
			this.add(symbolColorCollectionLabel, "cell 0 2");
			this.add(symbolColorBox, "cell 1 2");
			this.add(SetButton, "cell 0 3");
			this.setVisible(true);
		}

	}

	private class EventWin extends JFrame {

		private static final long serialVersionUID = 1L;
		private VesicleTable selectedEventTable;
		private JScrollPane TablePanel;
		private Secretion selected_secretion;
		private int selected_Vesicle_Row = 0;
		private ImagePlus vesicle_image;

		EventWin(Secretion selected_secretion) {
			this.setTitle("Selected Secretion Event");
			this.selected_secretion = selected_secretion;
			vesicle_image = new ImagePlus("Selectet vesicle");

			Object[][] tableData = new Object[selected_secretion.secretion_event.size()][8];

			int i = 0;
			for (Vesicle v : selected_secretion.secretion_event) {
				tableData[i] = new Object[] { 
						i, 
						v.property, 
						v.slice, 
						v.getMaxDen(),
						v.x,
						v.y,
						format.format(v.EstimateSize2D() * parameters.pixelSize), 
						format.format(v.get2DFitRsqr()) };
				i++;
			}

			DefaultTableModel secretion_dtm = new DefaultTableModel(tableData, columnTitleVesicle);
			selectedEventTable = new VesicleTable(secretion_dtm);

			MouseAdapter eventListMouseAdapter = new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					selected_Vesicle_Row = selectedEventTable.rowAtPoint(e.getPoint());
					if (e.getButton() == MouseEvent.BUTTON3) {
						// add right clicks event
						JPopupMenu popupMenu1 = new JPopupMenu();
						JMenuItem menuItem1 = new JMenuItem("  Valid this vesicle");
						JMenuItem menuItem2 = new JMenuItem("  Invalid this vesicle");
						if (selected_secretion.secretion_event.elementAt(selected_Vesicle_Row).isValid)
							menuItem1.setEnabled(false);
						else
							menuItem2.setEnabled(false);
						menuItem1.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e2) {
								if (e2.getButton() == MouseEvent.BUTTON1) {
									selected_secretion.secretion_event.elementAt(selected_Vesicle_Row).isValid = true;
									secretion_dtm.setValueAt(
											format.format(
													selected_secretion.secretion_event.elementAt(selected_Vesicle_Row)
															.EstimateSize2D() * parameters.pixelSize),
											selected_Vesicle_Row, 6);
									secretion_dtm.setValueAt(
											format.format(selected_secretion.secretion_event
													.elementAt(selected_Vesicle_Row).get2DFitRsqr()),
											selected_Vesicle_Row, 7);
								}
							}
						});
						menuItem2.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e2) {
								if (e2.getButton() == MouseEvent.BUTTON1) {
									selected_secretion.secretion_event.elementAt(selected_Vesicle_Row).isValid = false;
									secretion_dtm.setValueAt(
											format.format(
													selected_secretion.secretion_event.elementAt(selected_Vesicle_Row)
															.EstimateSize2D() * parameters.pixelSize),
											selected_Vesicle_Row, 6);
									secretion_dtm.setValueAt(
											format.format(selected_secretion.secretion_event
													.elementAt(selected_Vesicle_Row).get2DFitRsqr()),
											selected_Vesicle_Row, 7);
								}
							}
						});

						popupMenu1.add(menuItem1);
						popupMenu1.add(menuItem2);
						popupMenu1.show(e.getComponent(), e.getX(), e.getY());
					}

				}
			};
			ListSelectionListener eventListSelectionListener = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					selected_Vesicle_Row = selectedEventTable.getSelectedRow();
					if (selected_Vesicle_Row > -1) {
						Vesicle v = selected_secretion.secretion_event.elementAt(selected_Vesicle_Row);
						output_ips.setActivated();
						output_ips.setSlice(v.slice);
						int Radius = v.radius;
						int RoiX = v.x - (Radius + 8);
						int RoiY = v.y - (Radius + 8);
						output_ips.setRoi(RoiX, RoiY, Radius*2 + 16, Radius*2 + 16);
						if (gauss_H_win != null && gauss_V_win != null) {
							if (gauss_H_win.isVisible() || gauss_V_win.isVisible()) {
								viewVesicleRadiusFitter(v);
							}
						}
						if (vesicle_image.isVisible()) {
							vesicle_image.setProcessor(v.getImage());

							int Img_width = vesicle_image.getWidth();
							int Img_height = vesicle_image.getHeight();

							ImageCanvas secretion_ic = vesicle_image.getCanvas();
							Rectangle srcRect = secretion_ic.getSrcRect();

							int srcWidth = srcRect.width;
							int srcHeight = srcRect.height;

							// Set a scale for a better preview movie
							double mag = 10;

							secretion_ic.setMagnification(mag);
							double newWidth = Img_width * mag;
							double newHeight = Img_height * mag;
							secretion_ic.setSize((int) newWidth, (int) newHeight);
							secretion_ic.setSourceRect(new Rectangle(0, 0, srcWidth, srcHeight));
							secretion_ic.repaint();
						}
					}
				}
			};
			selectedEventTable.addMouseListener(eventListMouseAdapter);
			selectedEventTable.getSelectionModel().addListSelectionListener(eventListSelectionListener);
			TablePanel = new JScrollPane(selectedEventTable);
			TablePanel.setSize(new Dimension(600, 500));

			JButton expendButton_top = new JButton("+");
			expendButton_top.addActionListener((ActionEvent e) -> {
				if (selected_secretion.secretion_event.firstElement().slice > 1) {
					Vesicle v = new Vesicle(selected_secretion.secretion_event.firstElement().x,
							selected_secretion.secretion_event.firstElement().y,
							selected_secretion.secretion_event.firstElement().radius,
							selected_secretion.secretion_event.firstElement().slice - 1, image.getStack(),parameters.fixedExpand);
					v.pixelSize = parameters.pixelSize;
					v.pixelSizeUnit = parameters.pixelUnit;
					v.property = "Custom";
					selected_secretion.addVesicleAt1st(v);
					selected_secretion.setFitPointNum(selected_secretion.min_points_num);
					selected_secretion.setPeakTime(selected_secretion.peakTime);
					selected_secretion.Fit();
					secretion_dtm.insertRow(0,
							new Object[] { 
									v.getRef(),
									v.property, 
									v.slice, 
									v.getMaxDen(), 
									v.x, 
									v.y,
									format.format(v.EstimateSize2D() * parameters.pixelSize),
									format.format(v.get2DFitRsqr()) });
					refreshSecretionList();
				}
			});
			JButton removeButton_top = new JButton("-");
			removeButton_top.addActionListener((ActionEvent e) -> {
				secretion_dtm.removeRow(0);
				selected_secretion.removeVesicleAt1st();
				selected_secretion.setFitPointNum(selected_secretion.min_points_num);
				selected_secretion.setPeakTime(selected_secretion.peakTime);
				selected_secretion.Fit();
				refreshSecretionList();
			});

			JButton expendButton_bottom = new JButton("+");
			expendButton_bottom.addActionListener((ActionEvent e) -> {
				if (selected_secretion.secretion_event.firstElement().slice < image.getStackSize()) {
					Vesicle v = new Vesicle(selected_secretion.secretion_event.lastElement().x,
							selected_secretion.secretion_event.lastElement().y,
							selected_secretion.secretion_event.lastElement().radius,
							selected_secretion.secretion_event.lastElement().slice + 1, image.getStack(),parameters.fixedExpand);
					v.pixelSize = parameters.pixelSize;
					v.pixelSizeUnit = parameters.pixelUnit;
					v.property = "Custom";
					selected_secretion.addVesicle(v);
					selected_secretion.setFitPointNum(selected_secretion.min_points_num);
					selected_secretion.setPeakTime(selected_secretion.peakTime);
					selected_secretion.Fit();
					secretion_dtm.addRow(new Object[] { 
							v.getRef(), 
							v.property, 
							v.slice, 
							v.getMaxDen(),
							v.x, 
							v.y,
							format.format(v.EstimateSize2D() * parameters.pixelSize),
							format.format(v.get2DFitRsqr()) });
					refreshSecretionList();
				}
			});

			JButton removeButton_bottom = new JButton("-");
			removeButton_bottom.addActionListener((ActionEvent e) -> {
				secretion_dtm.removeRow(secretion_dtm.getRowCount() - 1);
				selected_secretion.removeVesicle();
				selected_secretion.setFitPointNum(selected_secretion.min_points_num);
				selected_secretion.setPeakTime(selected_secretion.peakTime);
				selected_secretion.Fit();
				refreshSecretionList();
			});
			
			JCheckBox fixedExpand = new JCheckBox("Fixed position", parameters.useExpandFrames);
			fixedExpand.setSelected(parameters.fixedExpand);
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
			// Intensity button

			/***
			 * JButton cruveButton = new JButton("View intensity cruve");
			 * cruveButton.addActionListener((ActionEvent e) -> {
			 * 
			 * double[] intensity_cruve = new
			 * double[selected_secretion.secretion_event.size()]; double[] time = new
			 * double[selected_secretion.secretion_event.size()]; int j = 0; for (Vesicle v
			 * : selected_secretion.secretion_event) { intensity_cruve[j] = v.getMaxDen();
			 * time[j] = v.slice; j++; } Plot intensity_Plot = new Plot("Fluo", "Frames",
			 * "Intensity"); intensity_Plot.addPoints(time, intensity_cruve, Plot.LINE);
			 * intensity_Plot.show(); });
			 * 
			 ***/
			JButton radiusButton = new JButton("View size curve");
			radiusButton.addActionListener((ActionEvent e) -> {

				double[] radius_cruve = new double[selected_secretion.secretion_event.size()];
				double[] time = new double[selected_secretion.secretion_event.size()];
				int j = 0;
				for (Vesicle v : selected_secretion.secretion_event) {
					radius_cruve[j] = v.EstimateSize2D() * parameters.pixelSize;
					time[j] = v.slice;
					j++;
				}

				Plot radius_Plot = new Plot("Size", "Frames", parameters.pixelUnit);
				radius_Plot.addPoints(time, radius_cruve, Plot.LINE);
				radius_Plot.show();
			});

			JButton selectedRadiusButton = new JButton("View selected vesicle radius");
			selectedRadiusButton.addActionListener((ActionEvent e) -> {
				Vesicle v = selected_secretion.secretion_event.elementAt(selected_Vesicle_Row);
				viewVesicleRadiusFitter(v);

			});
			JButton filmButton = new JButton("View movie");
			filmButton.addActionListener((ActionEvent e) -> {
				viewFilm(this.selected_secretion);
			});
			JButton imageButton = new JButton("View image");
			imageButton.addActionListener((ActionEvent e) -> {
				Vesicle v = selected_secretion.secretion_event.elementAt(selected_Vesicle_Row);
				vesicle_image.setProcessor(v.getImage());
				;
				if (!vesicle_image.isVisible())
					vesicle_image.show();
				int Img_width = vesicle_image.getWidth();
				int Img_height = vesicle_image.getHeight();

				ImageCanvas secretion_ic = vesicle_image.getCanvas();
				Rectangle srcRect = secretion_ic.getSrcRect();

				int srcWidth = srcRect.width;
				int srcHeight = srcRect.height;

				// Set a scale for a better preview movie
				double mag = 10;

				secretion_ic.setMagnification(mag);
				double newWidth = Img_width * mag;
				double newHeight = Img_height * mag;
				secretion_ic.setSize((int) newWidth, (int) newHeight);
				secretion_ic.setSourceRect(new Rectangle(0, 0, srcWidth, srcHeight));
				secretion_ic.repaint();
			});

			JButton SaveButton = new JButton("Save all intensity plots");
			SaveButton.addActionListener((ActionEvent e) -> {
				try {
					SaveUtils.saveVesicleList_secretion(selected_secretion.secretion_event, parameters,
							selected_secretion.ref_ID);
					// SaveUtils.saveVesicleListExcel(columnTitleV,
					// selected_secretion.secretion_event, pixelSize, pixelUnit, image.getTitle(),
					// selected_secretion.ref_ID);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});

			JPanel addRemoveButtonPanelTop = new JPanel();
			addRemoveButtonPanelTop.setSize(new Dimension(100, 100));
			addRemoveButtonPanelTop.setLayout(new FlowLayout());
			addRemoveButtonPanelTop.add(expendButton_top);
			addRemoveButtonPanelTop.add(removeButton_top);
			addRemoveButtonPanelTop.add(fixedExpand);

			JPanel addRemoveButtonPanelBottom = new JPanel();
			addRemoveButtonPanelBottom.setLayout(new FlowLayout());
			// addRemoveButtonPanelBottom.setSize(new Dimension(600, 100));
			addRemoveButtonPanelBottom.add(expendButton_bottom);
			addRemoveButtonPanelBottom.add(removeButton_bottom);

			JPanel ButtonPanelBottom = new JPanel();
			ButtonPanelBottom.setLayout(new FlowLayout());
			// ButtonPanelBottom.add(cruveButton);
			ButtonPanelBottom.add(radiusButton);
			ButtonPanelBottom.add(selectedRadiusButton);
			ButtonPanelBottom.add(filmButton);
			ButtonPanelBottom.add(imageButton);
			ButtonPanelBottom.add(SaveButton);

			JPanel EventWinButtonPanel = new JPanel();
			EventWinButtonPanel.setSize(new Dimension(600, 100));
			EventWinButtonPanel.setLayout(new BorderLayout());
			EventWinButtonPanel.add(addRemoveButtonPanelBottom, BorderLayout.PAGE_START);
			EventWinButtonPanel.add(ButtonPanelBottom, BorderLayout.PAGE_END);

			JPanel TableAndButtonPanel = new JPanel();
			TableAndButtonPanel.setLayout(new BorderLayout());
			TableAndButtonPanel.add(addRemoveButtonPanelTop, BorderLayout.PAGE_START);
			TableAndButtonPanel.add(TablePanel, BorderLayout.CENTER);
			TableAndButtonPanel.add(EventWinButtonPanel, BorderLayout.PAGE_END);

			this.setSize(new Dimension(600, 600));
			this.setLayout(new BorderLayout());
			this.add(TableAndButtonPanel, BorderLayout.CENTER);
			this.add(EventWinButtonPanel, BorderLayout.PAGE_END);
			this.pack();
			this.setVisible(true);
		}

	}

	public class ResultTable extends JTable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ResultTable(Object[][] rows, String[] columns) // Default
		{
			super(rows, columns);
		}

		public ResultTable(DefaultTableModel tableModel) { // DefaultTableModel
			super(tableModel);
		}

		@Override
		public boolean isCellEditable(int row, int column) // unEditable

		{
			return false;
		} 
		
		@Override
	    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

	        Component c = super.prepareRenderer(renderer, row, col);
        	
	        if (col == 7 && detected_secretions.elementAt(row).Decay_tau <= 0) 
	            c.setForeground(Color.RED);
	        else if (col == 13 && detected_secretions.elementAt(row).getEstimatedPeakSize2D() * parameters.pixelSize <= parameters.TheoreticalResolution)
                c.setForeground(Color.RED);
	        else 
	 	        c.setForeground(Color.BLACK);
	       return c;
	       
	    }
	}
			


	public class VesicleTable extends JTable {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public VesicleTable(Object[][] rows, String[] columns) // Default
			{
				super(rows, columns);
			}

			public VesicleTable(DefaultTableModel tableModel) { // DefaultTableModel
				super(tableModel);
			}

			@Override
			public boolean isCellEditable(int row, int column) // unEditable
			{
				return false;
			}

		
		
	}
}
