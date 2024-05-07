package ExocytosisAnalyzer.detection;

import java.util.Collection;

import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

public class VesicleFitter extends AbstractCurveFitter {
	private int width;
	private double[] initialGuess;
	private int maxIter;
	private final Gaussien2Dfunction FUNCTION;
	public double[] params;
	public double rSquared;
	public double[] fData;

	public VesicleFitter(int width, double[] initialGuess, int maxIter) {
		this.width = width;
		this.initialGuess = initialGuess;
		this.maxIter = maxIter;
		FUNCTION = new Gaussien2Dfunction(width);
	}

	public static VesicleFitter create(int width) {
		return new VesicleFitter(width, null, Integer.MAX_VALUE);
	}

	public VesicleFitter withStartPoint(int width, double[] newStart) {
		return new VesicleFitter(width, newStart.clone(), maxIter);
	}

	public VesicleFitter withMaxIterations(int width, int newMaxIter) {
		return new VesicleFitter(width, initialGuess, newMaxIter);
	}

	@Override
	protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
		// Prepare least-squares problem.
		final int len = observations.size();
		final double[] target = new double[len];
		final double[] weights = new double[len];
		int i = 0;
		for (WeightedObservedPoint obs : observations) {
			target[i] = obs.getY();
			weights[i] = obs.getWeight();
			++i;
		}
		final AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(
				FUNCTION, observations);
		return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(maxIter).start(initialGuess)
				.target(target).weight(new DiagonalMatrix(weights))
				.model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
	}

	@Override
	public double[] fit(Collection<WeightedObservedPoint> points) {

		params = super.fit(points);

		final int BG = 0; // background
		final int AP = 1; // amplitude
		final int XC = 2;
		final int YC = 3;
		final int S = 4; // sigma

		int numPoints = points.size();
		double[] yData = new double[numPoints];
		double[] xData = new double[numPoints];
		double[] Weight = new double[numPoints];
		fData = new double[numPoints];
		double sumY = 0;
		double sumY2 = 0;
		double SSres = 0;
		int numWeightedPoints = 0;
		int xposition, yposition;
		int i = 0;
		for (WeightedObservedPoint obs : points) {
			yData[i] = obs.getY();
			xData[i] = obs.getX();
			Weight[i] = obs.getWeight();
			xposition = i % width;
			yposition = i / width;
			fData[i] = params[BG] + params[AP]
					* Math.exp(-(Math.pow((xposition - params[XC]), 2) + Math.pow((yposition - params[YC]), 2))
							/ (2 * Math.pow((params[S]), 2)));
			if (Weight[i] == 1)
				++numWeightedPoints;
			sumY += yData[i] * Weight[i];
			sumY2 += Math.pow(yData[i] * Weight[i], 2);
			SSres += Math.pow(((yData[i] - fData[i]) * Weight[i]), 2);
			++i;
		}

		double SSt = sumY2 - sumY * sumY / numWeightedPoints;

		rSquared = 0;
		if (SSt > 0.0)
			rSquared = 1.0 - SSres / SSt;

		return params;

	}

}
