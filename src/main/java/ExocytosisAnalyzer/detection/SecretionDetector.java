package ExocytosisAnalyzer.detection;

import java.util.Vector;



import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.SecretionParametres;
import ExocytosisAnalyzer.datasets.SecretionParametresConfig;
import ExocytosisAnalyzer.datasets.Vesicle;
import ij.ImageStack;


public class SecretionDetector {
	
	ImageStack is;
    private final int num_of_slices;
    private final int frame_prior;
    private final int frame_after;
    private final int min_points_num;
    private int spot_radius;
    Vector<Secretion> detected_vesicle_seqs = new Vector<Secretion>();
    private final double max_tau, min_tau, max_Radius, min_Radius, min_R2, min_SNR;

   

    private SecretionParametresConfig s_paras_config;

	

    
    public SecretionDetector( Vector<Secretion> vesicle_seqs, ImageStack input, SecretionParametres s_paras,  SecretionParametresConfig s_paras_config) {
    	is = input;
    	detected_vesicle_seqs = vesicle_seqs;
    	num_of_slices = is.getSize();
    	spot_radius = vesicle_seqs.firstElement().secretion_event.firstElement().radius;

    	frame_prior = s_paras.expand_frames;
    	frame_after = s_paras.expand_frames;
    	
    	if (s_paras_config.min_points_num) min_points_num = s_paras.min_points_num;
    	else min_points_num = 2;
    	
    	if (s_paras_config.max_tau) max_tau = s_paras.max_tau;
    	else max_tau = num_of_slices;
    		
    	if (s_paras_config.min_tau) min_tau = s_paras.min_tau;
    	else min_tau = 0;
    		
    	if (s_paras_config.max_R) max_Radius = s_paras.max_R;
    	else max_Radius = is.getWidth();
    	
    	if (s_paras_config.min_R) min_Radius = s_paras.min_R;
    	else min_Radius = 0;
    	
    	if (s_paras_config.min_r2) min_R2 = s_paras.min_r2;
    	else min_R2 = 0;
    	
    	if (s_paras_config.min_SNR) min_SNR = s_paras.min_SNR;
    	else min_SNR = 1;
    	
    	this.s_paras_config = s_paras_config;

    }
    
 
    public Vector<Secretion> Detection() {
    	

    	
    	Vector<Secretion> detected_secretion = new Vector<Secretion>();

    	for (int i = 0 ; i < detected_vesicle_seqs.size() ; i++ ) {
        	Secretion current_secretion_event = detected_vesicle_seqs.elementAt(i);
    		int start_s = current_secretion_event.start_slice;
    		int start_x =  current_secretion_event.start_x;
    		int start_y =  current_secretion_event.start_y;
    			
    		int fin_s = current_secretion_event.fin_slice;
    		int fin_x = current_secretion_event.fin_x;
    		int fin_y = current_secretion_event.fin_y;
    			
    		if (s_paras_config.expand_frames) {
        		for (int s = 1 ; s <= frame_prior ; s++ ) {
        			if ((start_s - s)<=0) continue;
        			Vesicle aVesicle = new Vesicle( start_x, start_y, start_s - s, spot_radius, is);
        			current_secretion_event.addVesicleAt1st(aVesicle);
        		}
        		for (int s = 1 ; s <= frame_after ; s++ ) {
        			if ((fin_s + s)>= num_of_slices) continue;
        			Vesicle aVesicle = new Vesicle(fin_x, fin_y, fin_s + s, spot_radius, is);
        			current_secretion_event.addVesicle(aVesicle);
        		}
    		}

    		current_secretion_event.Fit(min_points_num);;
    			
    			
    		if (current_secretion_event.Decay_tau < max_tau && //current_secretion_event.Docking_tau < max_tau &&
    			current_secretion_event.Decay_tau > min_tau && //current_secretion_event.Docking_tau > min_tau &&
    			current_secretion_event.Decay_R2 > min_R2 &&
    			current_secretion_event.getVesicleSize() < max_Radius &&
    			current_secretion_event.getVesicleSize() > min_Radius &&
    			current_secretion_event.getMax()/current_secretion_event.getMin() > min_SNR
    			) {
    				
					current_secretion_event.setRef(i);
					detected_secretion.addElement(current_secretion_event);
    				
    		}
    					
    	}
    	
    	return detected_secretion;	
    	
    }
 


	
	public int getFrameAfter() {
		return frame_after;
	}
	
	public int getFramePrior() {
		return frame_prior;
	}

}
