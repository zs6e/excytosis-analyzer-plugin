/**
 * 
 */
package ExocytosisAnalyzer.datasets;

import java.util.Vector;

import ij.ImageStack;
import ij.measure.CurveFitter;

public class Secretion {

	public int ref_ID;
	public String proprety;
	public ImageStack film;
	public int peakX, peakY;
	
	public int peakTime = -1;
	private int peakposition = -1;
	
	public int min_points_num = -1;
	public double timeInterval = 1;
	public String timeUnit = "sec";
	public Vector<Vesicle> secretion_event = new Vector<Vesicle>();
	//public String function;
	//public String functionSimplified;

	public boolean isValid = true;
	public boolean isImmobile = true;

	public double Decay_tau, Docking_tau, Decay_R2, Docking_R2;
	public double Decay_bk, Docking_bk, Decay_peak, Docking_peak;
	private double peak_size = 0;
	private double peak_size_r2 = 0;

	public Secretion() {

	}

	public Secretion(Vector<Vesicle> aEvent) {
		secretion_event = aEvent;
		int num_of_objects = aEvent.size();
		film = new ImageStack(aEvent.firstElement().vesicleImage.getWidth(),
				aEvent.firstElement().vesicleImage.getHeight());
		for (int i = 0; i < num_of_objects; i++) {
			film.addSlice(aEvent.elementAt(i).vesicleImage);
		}


	}

	public void setRef(int aID) {
		ref_ID = aID;
	}

	public int getRef() {
		return ref_ID;
	}
	
	public int getStartSlice() {
		return secretion_event.firstElement().slice;
	}
	public int getFinSlice() {
		
		return secretion_event.lastElement().slice;
	}
	public int getStartX() {
		return secretion_event.firstElement().x;
	}
	public int getStartY() {
		return secretion_event.firstElement().y;
	}
	public int getFinX() {
		return secretion_event.lastElement().x;
	}
	public int getFinY() {
		return secretion_event.lastElement().y;
	}
	
	public void addVesicle(Vesicle aVesicle) {
		secretion_event.addElement(aVesicle);
		film.addSlice(aVesicle.vesicleImage);

	}

	public void addVesicleAt1st(Vesicle aVesicle) {
		secretion_event.add(0, aVesicle);
		film.addSlice(null, aVesicle.vesicleImage, 0);

	}

	public void removeVesicle() {
		secretion_event.remove(secretion_event.size() - 1);
		film.deleteLastSlice();

	}

	public void removeVesicleAt1st() {
		secretion_event.remove(0);
		film.deleteSlice(1);

	}

	public int getDuration() {
		return secretion_event.size();
	}

	public double[] getCurve() {
		double[] fluo_curve = new double[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			fluo_curve[i] = secretion_event.elementAt(i).max_Den;
		}

		return fluo_curve;
	}
	
	public double[] getCurve2() {
		double[] fluo_curve = new double[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			fluo_curve[i] = secretion_event.elementAt(i).max_Den;
		}

		return fluo_curve;
	}

	public double[] getEstimatedPeakCurve() {
		double[] peak = new double[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			peak[i] = secretion_event.elementAt(i).EstimateMaxIntensity2D();
		}
		return peak;
	}

	public Vector<double[]> getRingSimplingCurve() {
		Vector<double[]> RingSimplingCurve = new Vector<double[]>();

		boolean[] mask = new boolean[film.getHeight() * film.getWidth()];
		int index;
		int max_radius = (film.getHeight() - 1) / 2 ;
		for (int ring_radius = 0; ring_radius <= max_radius; ring_radius++) {
			double[] xdata = new double[secretion_event.size()];

			for (int i = -ring_radius; i <= ring_radius; i++) {
				for (int j = -ring_radius; j <= ring_radius; j++) {

					index = (i + max_radius) * film.getHeight() + (j + max_radius);

					if (((i * i + j * j) >= (ring_radius * ring_radius))
							&& ((i * i + j * j) < (ring_radius + 1) * (ring_radius + 1))) {
						mask[index] = true;
					} else {
						mask[index] = false;
					}
				}
			}

			for (int i = 0; i < xdata.length; i++) {
				final short[] pixels = (short[]) film.getProcessor(i + 1).duplicate().getPixels();
				xdata[i] = 0;
				int n = 0;
				for (int j = 0; j < mask.length; j++) {
					if (mask[j]) {
						xdata[i] = xdata[i] + pixels[j];
						n++;
					}
				}
				xdata[i] = xdata[i] / n;
			}
			RingSimplingCurve.addElement(xdata);
		}
		return RingSimplingCurve;
	}

	public double[] getTimeCorr() {

		double[] slice = new double[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			slice[i] = secretion_event.elementAt(i).slice;
		}
		return slice;
	}

	public int[] getTrajectoryX() {
		int[] x_corr = new int[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			x_corr[i] = secretion_event.elementAt(i).x;
		}
		return x_corr;
	}

	public int[] getTrajectoryY() {
		int[] y_corr = new int[secretion_event.size()];

		for (int i = 0; i < secretion_event.size(); i++) {
			y_corr[i] = secretion_event.elementAt(i).y;
		}
		return y_corr;
	}

	public void setPeakTime(int aTime) {
		this.peakTime = aTime;
		this.peakposition = peakTime - secretion_event.firstElement().slice;
	}

	public void setFitPointNum(int aNum) {
		this.min_points_num = aNum;
	}

	/**
	 * @return -1 peak position is at a unreliable position
	 * @return -2 fit parameters are net initialized, please use setPeakTime() and
	 *         setFitPointNum() to set fit parameter
	 *
	 */
	public void Fit() {
		if (min_points_num > 3 && peakposition > 0) {
			double[] xData = this.getTimeCorr();
			double[] yData = this.getCurve();

			//double peak = yData[peakposition];

			double baseline = yData[0];
			for (int i = 0; i < yData.length; i++) {

				if (yData[i] < baseline) {
					baseline = yData[i];
				}
			}

			// make sure the peak and take the last one if there are two identical values
			for (int i = peakposition; i < yData.length - 1; i++) {
				if (yData[i] <= yData[i + 1]) {
					peakposition++;
					peakTime++;
				} else
					break;

			}

			if (xData.length - peakposition >= min_points_num) {
				// minimal points necessary for fitting,
				// position : peak position

				//double[] xData_increase = new double[peakposition + 1];
				//double[] yData_increase = new double[peakposition + 1];
				double[] xData_decrease = new double[yData.length - peakposition];
				double[] yData_decrease = new double[yData.length - peakposition];

				//for (int i = 0; i <= peakposition; i++) { // reverse array
				//	xData_increase[i] = i;
				//	yData_increase[i] = yData[peakposition - i];
			  	//}
				for (int i = 0; i < yData.length - peakposition; i++) {
					xData_decrease[i] = i;
					yData_decrease[i] = yData[i + peakposition];
				}

				// String decay = "y = a + b*exp(-(x/c))";
				// double[] initilParas = { baseline, (peak - baseline), 1 };

				//CurveFitter dockingFitter = new CurveFitter(xData_increase, yData_increase);
				// dockingFitter.doCustomFit(decay, initilParas, false);
				//dockingFitter.doFit(CurveFitter.EXP_WITH_OFFSET); // y = a*exp(-bx) + c
				//double[] dockingParas = dockingFitter.getParams();

				CurveFitter releaseFitter = new CurveFitter(xData_decrease, yData_decrease);
				// releaseFitter.doCustomFit(decay, initilParas, false);
				releaseFitter.doFit(CurveFitter.EXP_WITH_OFFSET);
				double[] releaseParas = releaseFitter.getParams();

				//Docking_tau = 1.0 / dockingParas[1];
				Decay_tau = 1.0 / releaseParas[1];
				Decay_bk = releaseParas[2];
				Decay_peak = releaseParas[0];
				Decay_R2 = releaseFitter.getRSquared();
				peakX = secretion_event.elementAt(peakposition).x;
				peakY = secretion_event.elementAt(peakposition).y;
			} else {
				this.Decay_R2 = -1;
			}
		} else {
			this.Decay_R2 = -2;
		}

	}
	

	public double getVesicleSize() {
		return secretion_event.elementAt(peakTime - secretion_event.firstElement().slice).EstimateSize2D();
	}
	
	public double getF_zero() {
		double[] Curve = this.getCurve();
		return Curve[0];
	}
	public double getMax() {
		double[] Curve = this.getCurve();
		double max_value = Curve[0];

		for (int i = 0; i < Curve.length; i++) {
			if (Curve[i] > max_value) {
				max_value = Curve[i];
			}

		}
		return max_value;
	}

	public double getMin() {
		double[] Curve = this.getCurve();
		double min_value = Curve[0];
		for (int i = 0; i < Curve.length; i++) {
			if (Curve[i] < min_value) {
				min_value = Curve[i];
			}
		}
		return min_value;
	}

	public double getAverage() {
		double[] Curve = this.getCurve();
		double n = Curve.length;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += Curve[i];
		}
		return sum / n;
	}

	public double getSD() {
		double[] Curve = this.getCurve();
		double n = Curve.length;
		double sum = 0;
		double sum2 = 0;
		for (int i = 0; i < n; i++) {
			sum += Curve[i];
			sum2 += Math.pow(Curve[i], 2);
		}
		return Math.sqrt((sum2 - Math.pow(sum, 2) / n) / (n - 1));
	}

	public double[] getMovingAverage(int period) {
		double[] Curve = this.getCurve();
		int N = Curve.length;
		double[] MovingAverage = new double[N];
		double sum;
		double n;
		for (int i = 0; i < N; i++) {
			sum = 0;
			n = period;
			for (int j = 0; j < period; j++) {
				if (i - j < 0) {
					n--;
					continue;
				}
				sum += Curve[i - j];
			}
			MovingAverage[i] = sum / n;
		}
		return MovingAverage;
	}

	public double[] getMovingSD(int period) {
		double[] Curve = this.getCurve();
		int N = Curve.length;
		int n;
		double[] MovingSD = new double[N];
		MovingSD[0] = 0;
		double sum;
		double sum2;
		for (int i = 1; i < N; i++) {
			sum = 0;
			sum2 = 0;
			n = period;
			for (int j = 0; j < period; j++) {
				if (i - j < 0) {
					n--;
					continue;
				}
				sum += Curve[i - j];
				sum2 += Math.pow(Curve[i - j], 2);
			}
			MovingSD[i] = Math.sqrt((sum2 - Math.pow(sum, 2) / n) / (n - 1));
		}
		return MovingSD;
	}

	public double[] getDifferential(int period) {
		double[] Curve = this.getCurve();
		int N = Curve.length;
		double[] Differential = new double[N];
		for (int i = 0; i < N; i++) {
			if (i < period)
				Differential[i] = 0;
			else
				Differential[i] = Curve[i] - Curve[i - period];
		}

		return Differential;
	}



	public double getEstimatedPeakSize2D() {
		if (peak_size == 0) {
			peak_size = secretion_event.elementAt(peakposition).EstimateSize2D();
		}
		return peak_size;

	}

	// public double getMeanGaussfitterRsquare1D() {}
	public double getPeakGaussfitterRsquare2D() {
		if (peak_size_r2 == 0) {
			peak_size_r2 = secretion_event.elementAt(peakposition).get2DFitRsqr();
		}
		return peak_size_r2;
	}

	public double getSNR() {

		double[] curve = this.getDifferential(1);
		double[] residuel = new double[curve.length];
		//List<Double> input = Arrays.stream(curve).boxed().collect(Collectors.toList());
		//List<Double> residuel = new ArrayList<Double>();

		double median = ExocytosisAnalyzer.datasets.FindMedianD.findMedianD(curve);
		
		double max = 0;

		for (int i = 0; i<curve.length;i++) {
			residuel[i] = (Math.abs(curve[i] - median));
			if (residuel[i] > max) 
				max = residuel[i];
		}

		double MAD = ExocytosisAnalyzer.datasets.FindMedianD.findMedianD(residuel) / 0.6745;
		
		if (MAD == 0) return 0;
		

		double max_SNR = max / MAD;

		return max_SNR;
	}

	
	public double getMaxDisplacement() {
		double Distance_2 = 0;
		double max = 0;
		boolean find_first_element = false;
		double x0 = 0;
		double y0 = 0;
		for (Vesicle v : this.secretion_event) {				
			if (v.property == "Automatic") {
				if(find_first_element==false) {
					x0 = v.x;
					y0 = v.y;
					find_first_element = true;
				}
			Distance_2 = Math.pow( v.x-x0, 2) +  Math.pow(  v.y-y0, 2);
			
			if (Distance_2 > max)
				max = Distance_2;
			}
		}	
		return Math.sqrt(Distance_2);
	}

	public double getMAD() {

		double[] curve = this.getDifferential(1);
		double[] residuel = new double[curve.length];
		//List<Double> input = Arrays.stream(curve).boxed().collect(Collectors.toList());
		//List<Double> residuel = new ArrayList<Double>();

		double median = ExocytosisAnalyzer.datasets.FindMedianD.findMedianD(curve);
		
		//double max = 0;

		for (int i = 0; i<curve.length;i++) {
			residuel[i] = (Math.abs(curve[i] - median));
		}

		double MAD = ExocytosisAnalyzer.datasets.FindMedianD.findMedianD(residuel) / 0.6745;

		return MAD;
	}
	
	public double getDeltaF() {
		return this.getMax()-this.getF_zero();
	}

	public Secretion clone() {

		// final int spot_radius = this.secretion_event.elementAt(0).getRadius();
		final int ref = this.ref_ID;
		Vector<Vesicle> aEvent = new Vector<Vesicle>();
		for (int index = 0; index < this.secretion_event.size(); index++) {
			aEvent.addElement(this.secretion_event.elementAt(index));
		}
		Secretion aSecretion = new Secretion(aEvent);
		aSecretion.peakTime = this.peakTime;
		aSecretion.min_points_num = this.min_points_num;
		aSecretion.timeInterval = this.timeInterval;
		aSecretion.timeUnit = this.timeUnit;
		aSecretion.setRef(ref);
		return aSecretion;
	}
	
	

}
