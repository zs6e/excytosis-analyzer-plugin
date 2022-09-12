/*
    ExoJ is an Image/Fiji plugin to automate the detection and the analysis of exocytosis in fluorescent time series
    Copyright (C) 2022 - LIU Junjun, BUN Philippe

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ExocytosisAnalyzer.GUI.GUIWizard;




public class Exocytosis_analyzer implements PlugIn {
	openImageWin open_image_win;
	JLabel activeImageTitle;
	Object[][] tableData;
	imageListTable image_list;
	int selectedTableRow;
	@Override
	public void run(String arg) {
		
		//LocalDate date = LocalDate.now();
		//if (date.getYear()<=2022 && date.getMonthValue()<=7 )
			open_image_win = new openImageWin();
		
	}

	// -- Main method --

	/** Tests our command. */
	public static void main(String[] args) throws Exception {
		
		Class<?> clazz = Exocytosis_analyzer.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
		System.setProperty("plugins.dir", pluginsDir);
		
		// Launch ImageJ
		new ImageJ();

		// load the image
		ImagePlus image;
       	OpenDialog wo=new OpenDialog("");
        String we=wo.getPath();
		if (we !=null) {
			image = new ImagePlus(we);;
			image.show();
		}
       
        
       
		// Launch the command.
        IJ.runPlugIn(clazz.getName(), "");
	}
	
	public class openImageWin extends JFrame{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		openImageWin(){	
				
			JPanel upperPan = new JPanel();
			JPanel lowerPan = new JPanel();

			
			//JLabel OpenImageLabel = new JLabel("Open image :");

			
			final String[] columnTitle = { "#", "Image Title" };
			
			if (WindowManager.getCurrentImage() == null) {
				activeImageTitle = new JLabel("No image");
				tableData = new Object[1][2];
				tableData[0] = new Object[] {1,"No image"};
				
			}else {
				activeImageTitle = new JLabel(WindowManager.getCurrentImage().getTitle());
				String[] opened_images_list = WindowManager.getImageTitles();				
				tableData = new Object[opened_images_list.length][2];

				int i = 0;
				for (String s : opened_images_list) {
					tableData[i] = new Object[] {i+1, s};
					i++;
				}
			}

			DefaultTableModel image_list_dtm = new DefaultTableModel(tableData, columnTitle);
			image_list = new imageListTable(image_list_dtm);
			JScrollPane listTablePanel = new JScrollPane(image_list);

			
			ListSelectionListener imageListSelectionListener = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (image_list.getSelectedRow() != -1
							&& WindowManager.getImageTitles() != null
							&& image_list.getSelectedRow() < WindowManager.getImageTitles().length) {
						selectedTableRow = image_list.getSelectedRow();
						
						WindowManager.setCurrentWindow((ImageWindow) WindowManager.getWindow(WindowManager.getImageTitles()[selectedTableRow]));
						activeImageTitle.setText(WindowManager.getImageTitles()[selectedTableRow]);
					}
				}
			};
			image_list.getSelectionModel().addListSelectionListener(imageListSelectionListener);
			

			JButton openBTN = new JButton("Open");		
			MouseAdapter open =	new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					
					
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (WindowManager.getCurrentImage() == null) {
							IJ.error("No image");
							return;
						}
						ImagePlus imp = WindowManager.getCurrentImage();
						int type = imp.getType();
						if (type != ImagePlus.GRAY8 && type != ImagePlus.GRAY16 && type != ImagePlus.GRAY32) {
							IJ.error("Only process 8-bit, 16-bit or 32-bit image.");
							return;
						}
						//Roi roi = imp.getRoi();
						//if (roi!=null) {
						//	ImagePlus input = imp.duplicate();
						//	input.setRoi(roi);
						//	input.crop();
						//	input.show();
						//	new GUIWizard(input);
						//}
						else {
							new GUIWizard(imp);
						}
						
					}
				}	
			};
			openBTN.addMouseListener(open);
			


			
			JButton CloseBTN = new JButton("Close");		
			MouseAdapter close = new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					
					
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (WindowManager.getCurrentImage() == null) {
							IJ.error("No image");
							return;
						}
						WindowManager.getCurrentImage().close();
						refreshImageList();

					}
				}	
			};
			CloseBTN.addMouseListener(close);
			
			
			
			JButton RefreshBTN = new JButton("Refresh");
			MouseAdapter Refresh = new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						refreshImageList();
					}
				}	
			};
			RefreshBTN.addMouseListener(Refresh);
			
			MouseAdapter activeWinMon = new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (WindowManager.getCurrentImage() == null) {
							activeImageTitle.setText("No image");
						}else {
							activeImageTitle.setText(WindowManager.getCurrentImage().getTitle());
						}
					}
				}	
			};
			

			upperPan.setLayout(new FlowLayout());
			//upperPan.add(OpenImageLabel);
			//upperPan.add(activeImageTitle);
			//upperPan.setBorder(titleBorder);
			lowerPan.setLayout(new FlowLayout());
			lowerPan.add(openBTN);
			lowerPan.add(RefreshBTN);
			lowerPan.add(CloseBTN);
			
			
			image_list.setPreferredScrollableViewportSize(new Dimension(440,170)); 
			image_list.getColumn("#").setMaxWidth(10); 
			listTablePanel.setSize(450, 220);
			
			JPanel mainPan = new JPanel(new BorderLayout());
			
						
			mainPan.add(upperPan,BorderLayout.NORTH);
			mainPan.add(listTablePanel,BorderLayout.CENTER);
			this.setTitle("ExoJ: List of available files");
			this.setLayout(new FlowLayout());
			this.addMouseListener(activeWinMon);
			this.add(mainPan);
			this.add(lowerPan);
			this.setSize(480, 350);
			this.setVisible(true);
		}
	}
	private void refreshImageList() {
		final String[] columnTitle = { "#", "Image Title" };

		
		if (WindowManager.getCurrentImage() == null) {
			selectedTableRow = 0;
			activeImageTitle = new JLabel("No image");
			tableData = new Object[1][2];
			tableData[0] = new Object[] {1,"No image"};
			
		}else {
			String[] opened_images_list = WindowManager.getImageTitles();
			if (selectedTableRow > opened_images_list.length)
				selectedTableRow = opened_images_list.length;
			
			activeImageTitle = new JLabel(WindowManager.getCurrentImage().getTitle());
						
			tableData = new Object[opened_images_list.length][2];

			int i = 0;
			for (String s : opened_images_list) {
				tableData[i] = new Object[] {i+1, s};
				i++;
			}
		}
		
		
		DefaultTableModel image_list_dtm = new DefaultTableModel(tableData, columnTitle);

		image_list.setModel(image_list_dtm);
		image_list.validate();
		image_list.updateUI();
		image_list.changeSelection(selectedTableRow, 0, false, false);
		image_list.getColumn("#").setMaxWidth(10); 
	}
	public class imageListTable extends JTable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public imageListTable(Object[][] rows, String[] columns) // Default
		{
			super(rows, columns);
		}

		public imageListTable(DefaultTableModel tableModel) { // DefaultTableModel
			super(tableModel);
		}

		@Override
		public boolean isCellEditable(int row, int column) // unEditable
		{
			return false;
		}

	}



}
