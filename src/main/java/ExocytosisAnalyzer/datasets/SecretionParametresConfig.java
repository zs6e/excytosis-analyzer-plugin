package ExocytosisAnalyzer.datasets;

public class SecretionParametresConfig {
	public boolean  min_points_num, expand_frames, max_tau, min_tau, max_R, min_R, min_r2, min_SNR;
	public SecretionParametresConfig( boolean min_points, boolean expand, boolean maxTau, boolean minTau,boolean maxR, boolean minR, boolean r2, boolean SNR) {
	    min_points_num = min_points;
	    expand_frames = expand;
		max_tau = maxTau;
		min_tau = minTau;
		max_R = maxR;
		min_R = minR;
		min_r2 = r2;
		min_SNR = SNR;
	}
}