package ExocytosisAnalyzer.datasets;


public class VesicleParametres {

	public int Radius; 
	public int maxRadius;
	public double Percent; 
	public int sensitivity;
	public boolean useWavelet;
	public boolean showLowpass;
	private final int[] availableScale = {1, 3, 7, 13, 25}; //pixel
	public boolean[] scaleList =  {false, false, false, false, false};
	
	
	
	public VesicleParametres() {
		
	}
	
	public VesicleParametres(String aRadius, String aMax, String aPercent, String aSensitivity, boolean wavelet) {
		
	    Radius = Integer.parseInt(aRadius);
	    maxRadius = Integer.parseInt(aMax);
	    Percent = Double.parseDouble(aPercent);
		sensitivity = Integer.parseInt(aSensitivity);
		useWavelet = wavelet;
		
		if (useWavelet) {
			boolean isEnable = false;
		    for(int i = 0; i<5; i++) {
		    	if (availableScale[i]>=Radius*2 && availableScale[i]<=maxRadius*2) {
		    		scaleList[i]=true;
		    		isEnable = true;
		    	}
		    }
		    if (!isEnable) {
		    	for(int i = 0; i<5; i++) {
		    		if (availableScale[i]>=Radius*2) {
		    			scaleList[i-1]=true;
		    			break;
		    		}
		    	}
		    }
		}
	}
	
	public void setRadius(String aRadius){
		 Radius = Integer.parseInt(aRadius);
	}	
		
	public void setMaxRadius(String aMax){
		maxRadius = Integer.parseInt(aMax);
	}	 	
	   
	public void setPercent(String aPercent){
		Percent = Double.parseDouble(aPercent);// 10f; //1-100%
	}   
	    
	public void setSensitivity(String aSensitivity){
		sensitivity = Integer.parseInt(aSensitivity);
	} 

	public void setScaleList() {
		boolean isEnable = false;
		for(int i = 0; i<5; i++) {
		   	if (availableScale[i]>=Radius*2 && availableScale[i]<=maxRadius*2) {
		   		scaleList[i]=true;
		   		isEnable = true;
		   	}
		}
		if (!isEnable) {
		   	for(int i = 0; i<5; i++) {
		   		if (availableScale[i]>=Radius*2) {
		   			scaleList[i-1]=true;
		   			break;
		   		}
		   	}
		}
	}
	
}