package ExocytosisAnalyzer.datasets;

public class SecretionParametres {
	public int min_frames_num;
	public int min_points_num;
	public int expand_frames;
	public double max_tau, min_tau, max_R, min_R, min_r2, min_SNR;
	
	public SecretionParametres() {
	}
	
	public SecretionParametres(String min_points, String expand, String maxTau, String minTau,String maxR, String minR, String r2, String SNR) {
	    min_points_num = Integer.parseInt(min_points);
	    expand_frames = Integer.parseInt(expand);
		max_tau = Double.parseDouble(maxTau);
		min_tau = Double.parseDouble(minTau);
		max_R = Double.parseDouble(maxR);
		min_R = Double.parseDouble(minR);
		min_r2 = Double.parseDouble(r2);
		min_SNR = Double.parseDouble(SNR);
	}
	public void setMinFrames(String aMin){
		min_frames_num = Integer.parseInt(aMin);
	}
	public void setMinPoints(String aMin){
		min_points_num = Integer.parseInt(aMin);
	}
	public void setExpandFrames(String expand){
		expand_frames = Integer.parseInt(expand);
	}
	public void setMaxTau(String maxTau){
		max_tau = Double.parseDouble(maxTau);
	}
	public void setMinTau(String minTau){
		min_tau = Double.parseDouble(minTau);
	}
	public void setMaxRadius(String maxR){
		max_R = Double.parseDouble(maxR);
	}
	public void setMinRadius(String minR){
		min_R = Double.parseDouble(minR);
	}
	public void setMinR2(String r2){
		min_r2 = Double.parseDouble(r2);
	}
	public void setMinSNR(String SNR){
		min_SNR = Double.parseDouble(SNR);
	}

	
}