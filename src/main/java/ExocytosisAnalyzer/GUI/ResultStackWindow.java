package ExocytosisAnalyzer.GUI;


import java.awt.Graphics;
import java.util.Vector;


import ExocytosisAnalyzer.datasets.ElementInFrame;
import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.Vesicle;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;

public class ResultStackWindow {

	Vector<ImageLabel> imgLabels;
	StackWindow sw;
	ResultCanvas preview_canvas;

	public ResultStackWindow(ImagePlus imp) {

		preview_canvas = new ResultCanvas(imp);
		sw = new StackWindow(imp, preview_canvas);
		imgLabels = new Vector<ImageLabel>();

	}

	public ResultStackWindow(ImagePlus imp, Vector<Vesicle> Vesicles) {
		
		preview_canvas = new ResultCanvas(imp);
		sw = new StackWindow(imp, preview_canvas);
		imgLabels = new Vector<ImageLabel>();


		for (int i = 0; i < imp.getStackSize(); i++) {
			imgLabels.addElement(new ImageLabel(i + 1));
		}
		for (Vesicle v : Vesicles) {
			imgLabels.elementAt(v.slice - 1).addVesicle(v);
		}
	}


	public void setLabels(Vector<Vesicle> Vesicles) {

		imgLabels = new Vector<ImageLabel>();
		for (int i = 0; i < sw.getImagePlus().getStackSize(); i++) {
			imgLabels.addElement(new ImageLabel(i + 1));
		}

		for (Vesicle v : Vesicles) {
			imgLabels.elementAt(v.slice - 1).addVesicle(v);
		}

	}
	
	public void setLabels2(Vector<Secretion> secretion) {

		imgLabels = new Vector<ImageLabel>();
		for (int i = 0; i < sw.getImagePlus().getStackSize(); i++) {
			imgLabels.addElement(new ImageLabel(i + 1));
		}
		for (Secretion s : secretion) {
			for (Vesicle v : s.secretion_event) {
				imgLabels.elementAt(v.slice - 1).addVesicle(v);
			}

		}
		
	}
	
	public void setLabels3(Vector<ElementInFrame> Elements) {

		if (Elements.size() != sw.getImagePlus().getStackSize()) return;
		
		imgLabels = new Vector<ImageLabel>();
		
		for (int i = 0; i < sw.getImagePlus().getStackSize(); i++) {
			imgLabels.addElement(new ImageLabel(i + 1));
		}

		for (ElementInFrame e : Elements) {
			imgLabels.elementAt(e.Frame_num - 1).Vesicle_to_draw = e.Vesicles;
		}

	}



	public void setTitle(String title) {
		sw.setTitle(title);
	}

	public boolean isVisible() {
		return sw.isVisible();
	}

	public boolean isClosed() {
		return sw.isClosed();
	}

	public void setVisible(boolean b) {
		sw.setVisible(b);
	}

	public class ResultCanvas extends ImageCanvas {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private int radius;

		ResultCanvas(ImagePlus imp) {
			super(imp);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (!imgLabels.isEmpty()) {
				final int slice = this.imp.getCurrentSlice();
				Vector<Vesicle> Vesicles = imgLabels.elementAt(slice - 1).Vesicle_to_draw;
				g.setColor(GUIWizard.myColor);
				double mag = imp.getWindow().getCanvas().getMagnification();

				for (Vesicle v : Vesicles) {
					g.drawOval( (int)Math.round(v.x* mag - (radius + 8) * mag),
								(int)Math.round(v.y* mag - (radius + 8) * mag), 
								(int)Math.round((radius * 2 + 16) * mag),
								(int)Math.round((radius * 2 + 16) * mag) );
				}
			}

		}

	}

	public class ImageLabel {
		public int slice;
		public Vector<Vesicle> Vesicle_to_draw;

		public ImageLabel(int slice) {
			this.Vesicle_to_draw = new Vector<Vesicle>();
			this.slice = slice;
		}

		public ImageLabel(Vector<Vesicle> Vesicles, int slice) {
			this.Vesicle_to_draw = Vesicles;
			this.slice = slice;
		}

		public void setSlice(int n) {
			this.slice = n;
		}

		public void addVesicle(Vesicle aVesicle) {
			Vesicle_to_draw.addElement(aVesicle);
		}
	}
}
