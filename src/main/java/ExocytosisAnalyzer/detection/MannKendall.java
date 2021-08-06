package ExocytosisAnalyzer.detection;

import java.awt.Color;
import java.util.Vector;

import ij.gui.Plot;

public class MannKendall {
	double[] yData, yData_rev, xData;
	int N;
	Vector<Double> K;
	double[] UFk,UBkT;

	public MannKendall(double[] signal, double[] time) {
		yData = signal;
		xData = time;

		N = yData.length;
		yData_rev = new double[N];
		for (int i = 0; i < N; i++) {
			yData_rev[i] = signal[N - i - 1];
		}
		UFk = new double[N];
		UBkT = new double[N];
		K  = new Vector<Double>();

	}
	public MannKendall(double[] signal) {
		yData = signal;


		N = yData.length;
		xData = new double[N];
		yData_rev = new double[N];
		for (int i = 0; i < N; i++) {
			yData_rev[i] = signal[N - i - 1];
			xData[i] = i;
		}
		UFk = new double[N];
		UBkT = new double[N];
		K  = new Vector<Double>();

	}

	public Vector<Double> test() {
		
		
		double s, s_rev, n;
		double Sk, Exp_Sk, Var_Sk, Sk_rev, Exp_Sk_rev, Var_Sk_rev;
		double[] UBk, diff;

		s = 0.0;
      	s_rev = 0.0;
		
		UBk = new double[N];
		diff = new double[N];
		
		UFk[0]=0.0;
		UBk[0]=0.0;



		for (int i = 2; i <= N; i++) {
			for (int j = 1; j < i; j++) {
				if (yData[i-1] > yData[j-1])
					s++;
			}
			Sk = s;
			n = i;
			
	        Exp_Sk = n*(n+1.0)/4.0 ;
	        Var_Sk = n*(n-1.0)*(2.0*n+5.0)/72.0;
	        UFk[i-1] = (Sk-Exp_Sk)/Math.sqrt(Var_Sk);

		}
		

		
		for (int i = 2; i <= N; i++) {
			for (int j = 1; j < i; j++) {
				if (yData_rev[i-1] > yData_rev[j-1])
					s_rev++;
			}
			Sk_rev = s_rev;
			n = i;
	        Exp_Sk_rev = n*(n+1.0)/4.0 ;
			Var_Sk_rev = n*(n-1.0)*(2.0*n+5.0)/72.0;
	        UBk[i-1] = (Sk_rev-Exp_Sk_rev)/Math.sqrt(Var_Sk_rev);
	        
	        

		}

        for(int i=0; i<N; i++){
        	UBkT[i] = -UBk[N - i - 1];
        	diff[i] = UFk[i] - UBkT[i];
        }

        for (int i = 1; i < N; i++) {
        	if (diff[i-1]*diff[i]<0)
        		K.add(xData[i]);
        }
        return K;
	}
	public Plot getPlot() {
	
		
		Plot MK = new Plot("Mann-Kendall","frame","UFk - UBk");
		MK.setColor(Color.RED);
		MK.add("line", xData, UFk);
		MK.setColor(Color.BLUE);
		MK.add("line", xData, UBkT);
		return MK;
	}
}
