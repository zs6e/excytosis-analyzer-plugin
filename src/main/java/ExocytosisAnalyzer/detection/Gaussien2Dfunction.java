package ExocytosisAnalyzer.detection;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;

public class Gaussien2Dfunction implements ParametricUnivariateFunction {
	/** Parametric function to be fitted. */
	int width;

	final int BG = 0; // background
	final int AP = 1; // amplitude
	final int XC = 2;
	final int YC = 3;
	final int S = 4; // sigma

	public Gaussien2Dfunction(int width) {
		this.width = width;
	}

	public double value(double Corr, double... params) {
		int x = ((int) Corr) % width;
		int y = ((int) Corr) / width;
		double result = gaussian2D(params, x, y);
		return result;
	}

	public double[] gradient(double Corr, double... params) {
		int x = ((int) Corr) % width;
		int y = ((int) Corr) / width;
		double q = gaussian2D(params, x, y) - params[BG];
		double dx = x - params[XC];
		double dy = y - params[YC];
		double[] result = { 1.0, q / params[AP], dx * q / Math.pow(params[S], 2), dy * q / Math.pow(params[S], 2),
				(Math.pow(dx, 2) + Math.pow(dy, 2)) * q / Math.pow(params[S], 3) };
		return result;
	}

	private double gaussian2D(double[] params, int x, int y) {
		// G(x,y) = BG + AP* exp(-((x-xc)^2+(y-yc)^2)/(2 sig^2))
		return params[BG] + params[AP] * Math
				.exp(-(Math.pow((x - params[XC]), 2) + Math.pow((y - params[YC]), 2)) / (2 * Math.pow((params[S]), 2)));

	}

	public void validateParameters(double[] param) throws NullArgumentException, DimensionMismatchException {
		if (param == null) {
			throw new NullArgumentException();
		}
		if (param.length != 5) {
			throw new DimensionMismatchException(param.length, 5);
		}

	}

}
