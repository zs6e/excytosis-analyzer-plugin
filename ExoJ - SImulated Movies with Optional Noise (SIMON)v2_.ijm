/*Copyright (C) 2024 - LIU Junjun, BUN Philippe

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

print("\\Clear");
setForegroundColor(255, 255, 255); setBackgroundColor(0, 0, 0);
showMessage("Simulated movie", "Two options:\n1. Load a reference stack (e.g. EXO_movie.tif)\n2. Start from a blank image.");

Dialog.createNonBlocking("Simulated movie");
Dialog.setInsets(0, 0, 0); Dialog.addDirectory("Saving folder", "");
Dialog.addCheckbox("Already have a reference time series?", true);
Dialog.setInsets(0, 0, 0); Dialog.addFile("Reference location", "");
Dialog.addCheckbox("Homogeneous illumination?", true);
Dialog.addCheckbox("Adding Gaussian noise?", true);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("How much? (multiple of standard deviation calculated on individual frame", 1);

Dialog.setInsets(10, 0, 0); Dialog.addMessage("---Synthetic fusion event--------------------------", 9);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Time interval? (in s)", 0.2);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Number of Relevant events?", 50);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Number of non-Relevant events?", 50);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Min. radius (in pixel)", 1);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Max. radius (in pixel)", 3);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Min. intensity above local background (multiple)", 0.1);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Max. radius above local background (multiple)", 2);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Min. event duration (in s)", 0.4);
Dialog.setInsets(0, 0, 0); Dialog.addNumber("Max. event duration (in s)", 5);
Dialog.show();

dir = Dialog.getString();
reference_box = Dialog.getCheckbox();
_reference_file = Dialog.getString();
illumination_box = Dialog.getCheckbox();
noise_box = Dialog.getCheckbox();

std_ratio = Dialog.getNumber();
freq = Dialog.getNumber();
relevant_event = Dialog.getNumber(); nonrelevant_event = Dialog.getNumber();
min_size = Dialog.getNumber(); max_size = Dialog.getNumber();
min_intensity = Dialog.getNumber(); max_intensity = Dialog.getNumber();
min_decay = Dialog.getNumber(); max_decay = Dialog.getNumber();


save_folder = dir + "_results/";
if ( File.exists(save_folder) < 1 ) File.makeDirectory( save_folder );
if (reference_box) {
	open(_reference_file);
	name_file = getInfo("image.title"); //name_file = File.nameWithoutExtension;
	w = getWidth(); h = getHeight(); n_frames = nSlices(); 
} else {
	run("Image...");
	w = getWidth(); h = getHeight(); n_frames = nSlices();
	name_file = getInfo("image.title");
}

movie = getImageID();
waitForUser("Confined events", "Two options:\n1. Manual drawing and add to ROI MANAGER\n2. Cell contour segmentation then add to ROI MANAGER.\nPress Ok when done");

/*
 * GENERATE SYNTHETIC DATA
 * NEED A REGION OF INTEREST THAT DELINEATES THE CELL 
 *   1. MANUAL DRAWING (THEN ADD TO ROI MANAGER)
 *or 2. CELL CONTOUR SEGMENTATION USING THE METHOD OF YOUR CHOICE  
 * 
 */

max_area = w*0.1*h*0.1; 
run("Properties...", "channels=1 slices="+n_frames+" frames=1 pixel_width=0.1000000 pixel_height=0.1000000 voxel_depth=1.0000000 frame=["+freq+" sec]");
margin_xy = 0.05*w; // in pixels
margin_t = 0.02*n_frames; // in frame number

nb_events = round(1+random()*(relevant_event-1)); // AT LEAST 1 EVENT
counter = 1; roiManager("Set Color", "orange");
print("--RELEVANT EVENTS--");
print("--Simulation of "+nb_events+" relevant events");
print("------------------------------------");

do {

	intensity_event = min_intensity +random()*(max_intensity-min_intensity);
	decay_event = min_decay +random()*(max_decay-min_decay);
	rad_event = 2*(min_size +random()*(max_size-min_size)) +1;
	r = newArray(3);
	
	for (i=0; i < r.length; i++) {
		r[i] = random(); // from 0 to 1
	}
	pos_x = margin_xy+round(r[0]*(w-(2*margin_xy))); pos_y = margin_xy+round(r[1]*(h-(2*margin_xy))); 
	pos_t = 5+round(r[2]*(n_frames-margin_t)); // starting at least at Frame 5
	
	run("Specify...", "width=18 height=18 x="+pos_x+" y="+pos_y+" slice="+pos_t+" constrain centered");
	getStatistics(area, mean, min, max, std, histogram); local_max = max; local_mean = mean;
	
	run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+pos_t+" oval constrain centered");
	roiManager("Add"); roiManager("Select", newArray(0, counter)); roiManager("AND"); getStatistics(area);
	
	if (area != max_area) {
		roiManager("select", counter);
		control = 0;
		intensity = local_max*(1+intensity_event);
		//intensity = local_mean*(1+intensity_event);

		roiManager("select", counter); run("Add...", "value="+intensity+" slice");
		run("Mean...", "radius=2 slice");
		roiManager("select", counter); getStatistics(area, mean, min, max, std, histogram);
		event_max = max; 
	
		for (i=pos_t+1; i <= n_frames-margin_t; i++) {
			intensity_t = intensity*exp(-(i-pos_t)/(decay_event/freq));
			
			if (event_max+intensity_t >= 1*local_max) {
				run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+i+" oval constrain centered");	
				run("Add...", "value="+intensity_t+" slice");
				run("Mean...", "radius=2 slice");
				control = control+1;
				getStatistics(area, mean, min, max, std, histogram);
				event_max = max;
			}
				
		}
		
	
		
		run("Select None"); roiManager("deselect");
		counter = counter +1;
		print("Event_"+counter-1+"- nb_decaying_frames:("+control+") -Intensity(0): "+intensity+" -size: "+rad_event);	
		
	} else {
		
		roiManager("deselect");
		roiManager("Select", roiManager("count")-1); roiManager("delete");
		run("Select None"); roiManager("deselect");
	}
	
	
} while (counter <= nb_events);
/*
//-==============================================================================================================-
//----------------------------------------------------------------------------------------------------------------
// "NEGATIVE" EVENTS ---------------------------------------------------------------------------------------------
/*
 * Still fluorescent spots, Moving fluorescent spots, No display of exponential decay
 */
//================================================================================================================ 
//================================================================================================================

nb_n_events = round(1+random()*(nonrelevant_event-1));
counter = 1;
print("--NON-RELEVANT EVENTS--");
print("--Simulation of "+nb_n_events+" (+/-) 3 non-relevant events");
print("------------------------------------");

//----------------------------------------------------------------------------------------------------------------
//- ONE FRAME AND THEN DISAPPEARS 
// counter is now at 1
nb_N1_events = 1 +floor(random()*(nb_n_events-1)); // AT LEAST 1 EVENT
print("- Negative 1 frame: "+nb_N1_events+" event(s)");

do {
	intensity_event = min_intensity +random()*(max_intensity-min_intensity);
	decay_event = min_decay +random()*(max_decay-min_decay);
	rad_event = 2*(min_size +random()*(max_size-min_size)) +1;
	
	// GENERATING SPOT
	rn = newArray(4);
	for (in=0; in < rn.length; in++) {
		rn[in] = random; // from 0 to 1
	}
	pos_x = margin_xy+round(rn[0]*(w-(2*margin_xy))); pos_y = margin_xy+round(rn[1]*(h-(2*margin_xy))); 
	pos_t = 5+round(rn[2]*(n_frames-margin_t)); // starting at least at Frame 5

	run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+pos_t+" oval constrain centered");
	getStatistics(area, mean, min, max, std, histogram); local_max = max; local_mean = mean;
	roiManager("Add"); roiManager("Select", newArray(0, nb_events+counter)); roiManager("AND"); getStatistics(area);
	intensity = local_max*(1+intensity_event);
	//intensity = local_mean*(1+intensity_event);
	
	if (area != max_area) {
		roiManager("select", counter); roiManager("Set Color", "white");
		run("Add...", "value="+intensity+" slice");
		run("Mean...", "radius=2 slice");
		run("Select None"); roiManager("deselect");
		counter = counter +1;
		print("Negative event- "+nb_events+counter-1+" -size: "+rad_event+" -intensity: "+intensity);		
	} else {
		roiManager("deselect");
		roiManager("Select", roiManager("count")-1); roiManager("delete");
		run("Select None"); roiManager("deselect");	
	}
} while (counter <=nb_N1_events);

//----------------------------------------------------------------------------------------------------------------
//- STAYS WITH NO DECAY (RANDOM NUMBER OF FRAMES) 
nb_N2_events = 1+ floor(random*(nb_n_events-nb_N1_events)); // AT LEAST 1 EVENT
print("- Negative multiple frames: "+nb_N2_events+" event(s)");

do {
		
	intensity_event = min_intensity +random()*(max_intensity-min_intensity);
	decay_event = min_decay +random()*(max_decay-min_decay);
	rad_event = 2*(min_size +random()*(max_size-min_size)) +1;
	
	// GENERATING SPOT
	rn = newArray(4);
	for (in=0; in < rn.length; in++) {
		rn[in] = random; // from 0 to 1
	}
	pos_x = margin_xy+round(rn[0]*(w-(2*margin_xy))); pos_y = margin_xy+round(rn[1]*(h-(2*margin_xy))); 
	pos_t = 5+round(rn[2]*(n_frames-margin_t)); // starting at least at Frame 5
	
	if (pos_t < (5+n_frames-margin_t)) {
		rn_frames = floor(2+(n_frames/10)*rn[3]);
	} else {
		rn_frames = 4;
	}

	run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+pos_t+" oval constrain centered");
	getStatistics(area, mean, min, max, std, histogram); local_max = max; local_mean = mean;
	roiManager("Add"); roiManager("Select", newArray(0, nb_events+counter)); roiManager("AND"); getStatistics(area);
	intensity = local_max*(1+intensity_event);
	//intensity = local_mean*(1+intensity_event);
	
	if (area != max_area) {
		roiManager("select", counter); roiManager("Set Color", "white");
		run("Add...", "value="+intensity+" slice");
		run("Mean...", "radius=2 slice");
		for (in=pos_t+1; in <= pos_t+1+rn_frames; in++) {
			run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+in+" oval constrain centered");
				
			run("Add...", "value="+intensity+" slice");
			run("Mean...", "radius=2 slice");
		}
		run("Select None"); roiManager("deselect");
		counter = counter +1;
		print("Negative event- "+ nb_events+counter-1+" for "+rn_frames+" frames -size: "+rad_event);	
	} else {
		roiManager("deselect");
		roiManager("Select", roiManager("count")-1); roiManager("delete");
		run("Select None"); roiManager("deselect");	
	}
} while (counter <=nb_N1_events+nb_N2_events);

//----------------------------------------------------------------------------------------------------------------
//- STAYS WITH DECAY (RANDOM NUMBER OF FRAMES)
nb_N3_events = 1+ floor(random*(nb_n_events-nb_N1_events-nb_N2_events)); // AT LEAST 1 EVENT
print("- Decay-like: "+nb_N3_events+" event(s)");

do {

	intensity_event = min_intensity +random()*(max_intensity-min_intensity);
	decay_event = min_decay +random()*(max_decay-min_decay);
	rad_event = 2*(min_size +random()*(max_size-min_size)) +1;
	
	r = newArray(3);
	for (i=0; i < r.length; i++) {
		r[i] = random(); // from 0 to 1
	}
	pos_x = margin_xy+round(r[0]*(w-(2*margin_xy))); pos_y = margin_xy+round(r[1]*(h-(2*margin_xy))); 
	pos_t = 5+round(r[2]*(n_frames-margin_t)); // starting at least at Frame 5
	
	run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+pos_t+" oval constrain centered");
	getStatistics(area, mean, min, max, std, histogram); local_max = max; local_mean = mean;
	roiManager("Add"); roiManager("Select", newArray(0, nb_events+counter)); roiManager("AND"); getStatistics(area);
	intensity = local_max*(1+intensity_event);
	//intensity = local_mean*(1+intensity_event);
	
	if (area != max_area) {
		control = 0;
		roiManager("select", nb_events+counter); roiManager("Set Color", "white");
		run("Add...", "value="+intensity+" slice");
		run("Mean...", "radius=2 slice");
		
		roiManager("select", nb_events+counter); getStatistics(area, mean, min, max, std, histogram);
		event_max = max; 
	
		for (i=pos_t+1; i <= n_frames-margin_t; i++) {
			intensity_t = intensity* (1-abs(1 - sin(random())*cos(random())));
			run("Specify...", "width=18 height=18 x="+pos_x+" y="+pos_y+" slice="+i+" constrain centered");
						
			if (event_max+intensity_t >= 1*local_max) {
				run("Specify...", "width="+rad_event+" height="+rad_event+ " x="+pos_x+" y="+pos_y+" slice="+i+" oval constrain centered");	
				run("Add...", "value="+intensity_t+" slice");
				run("Mean...", "radius=2 slice");
				control = control+1;
				getStatistics(area, mean, min, max, std, histogram);
				event_max = max;		
			}
				
		}
		
	
		
		run("Select None"); roiManager("deselect");
		counter = counter +1;
		print("Event_"+nb_events+counter-1+"- nb_decaying_frames:("+control+") -Intensity(0): "+intensity+" -size: "+rad_event);	
		
	} else {
		
		roiManager("deselect");
		roiManager("Select", roiManager("count")-1); roiManager("delete");
		run("Select None"); roiManager("deselect");
	}
	
	
}while (counter <=nb_N1_events+nb_N2_events+nb_N3_events);

//----------------------------------------------------------------------------------------------------------------
//- MOVING WITH DECAY (RANDOM NUMBER OF FRAMES) 
nb_N4_events = floor(nb_n_events-nb_N1_events-nb_N2_events-nb_N3_events); 
print("- Moving with decay: "+nb_N4_events+" events");

if (nb_N4_events != 0) {
	do {
		intensity_event = min_intensity +random()*(max_intensity-min_intensity);
		decay_event = min_decay +random()*(max_decay-min_decay);
		rad_event = 2*(min_size +random()*(max_size-min_size)) +1;
		// GENERATING SPOT
		rn = newArray(4);
		for (in=0; in < rn.length; in++) {
			rn[in] = random; // from 0 to 1
		}
		pos_x = margin_xy+round(rn[0]*(w-(2*margin_xy))); pos_y = margin_xy+round(rn[1]*(h-(2*margin_xy))); 
		pos_t = 5+round(rn[2]*(n_frames-margin_t)); // starting at least at Frame 5
	
		if (pos_t < (5+n_frames-margin_t)) {
			rn_frames = floor(2+(n_frames/10)*rn[3]);
		} else {
			rn_frames = 4;
		}
	
		run("Specify...", "width="+rad_event+" height="+rad_event+" x="+pos_x+" y="+pos_y+" slice="+pos_t+" oval constrain centered");
		getStatistics(area, mean, min, max, std, histogram); local_max = max; local_mean = mean;
		roiManager("Add"); roiManager("Select", newArray(0, nb_events+counter)); roiManager("AND"); getStatistics(area);
		intensity = local_max*(1+intensity_event);
		//intensity = local_mean*(1+intensity_event);
	
		if (area != max_area) {
			control = 0;
			roiManager("select", nb_events+counter); roiManager("Set Color", "white");
			run("Add...", "value="+intensity+" slice");
			run("Mean...", "radius=2 slice");
			
			roiManager("select", nb_events+counter); getStatistics(area, mean, min, max, std, histogram);
			event_max = max; 
			
			for (in=pos_t+1; in <= pos_t+rn_frames; in++) {
				intensity_t = intensity* (1-abs(1 - sin(random())*cos(random())));

				run("Specify...", "width=18 height=18 x="+pos_x+" y="+pos_y+" slice="+in+" constrain centered");
				getStatistics(area, mean, min, max, std, histogram); local_sd = std; local_background = mean;
			
				if (event_max+intensity_t >= 1*local_background) {
					run("Specify...", "width="+rad_event+" height="+rad_event+ " x="+pos_x+(round(2+2*random()-2*random()))+" y="+pos_y+(round(2+2*random()-2*random()))+" slice="+in+" oval constrain centered");		
					run("Add...", "value="+intensity_t+" slice");
					run("Mean...", "radius=2 slice");
					control = control +1;
					getStatistics(area, mean, min, max, std, histogram);
					event_max = max; 	
				}
							
			}
			run("Select None"); roiManager("deselect");
			counter = counter +1;
			print("Negative event-"+nb_events+counter-1+"- nb_decaying_frames:("+control+") -size: "+rad_event);
		} else {
			roiManager("deselect");
			roiManager("Select", roiManager("count")-1); roiManager("delete");
			run("Select None"); roiManager("deselect");	
		}
	} while (counter <=nb_n_events);
}

// POSITIVE - RELEVANT EVENTS
pos_array = Array.getSequence(nb_events+1);
pos_array = Array.slice(pos_array,1);
roiManager("Select", pos_array);
RoiManager.setGroup(0); RoiManager.setPosition(0); roiManager("Set Color", "orange"); roiManager("Set Line Width", 0);
// NEGATIVE - NON-RELEVANT EVENTS
neg_array = Array.getSequence(roiManager("count"));
neg_array = Array.slice(neg_array,nb_events+1);
roiManager("Select", neg_array);
RoiManager.setGroup(0); RoiManager.setPosition(0); roiManager("Set Color", "white"); roiManager("Set Line Width", 0);
run("Select None"); roiManager("deselect");	

selectWindow("Log"); saveAs("Text", save_folder + name_file +"_Summary");
selectImage(movie); roiManager("Show None"); run("Select None"); roiManager("deselect");
roiManager("save", save_folder + name_file + "Fusion events" + ".zip");
selectImage(movie); roiManager("Show None"); saveAs("tiff", save_folder + name_file +"_Events");

if (illumination_box) {
	newImage("Ramp", "8-bit ramp", w, h, n_frames);
	imageCalculator("Multiply create 32-bit stack", movie,"Ramp"); 
	movie_illumination = getImageID(); saveAs("tiff", save_folder + name_file +"_Events _nonHomogeneous");
	selectWindow("Ramp"); close();
	
}
if (noise_box) {
	run("Select None"); roiManager("deselect")
	for (i=1; i <= n_frames; i++) {
		Stack.setSlice(i); getStatistics(area, mean, min, max, std, histogram);
		run("Add Specified Noise...", "slice standard="+std*std_ratio);
	}
	run("Select None"); roiManager("deselect")
	run("Gaussian Blur...", "sigma=1 stack");
} else {
	run("Select None"); roiManager("deselect")
	run("Gaussian Blur...", "sigma=1 stack");
}

if (illumination_box && noise_box) {
	saveAs("tiff", save_folder + name_file +"_Events _nonHomogeneous_GaussianNoise_"+std_ratio);
}
if (illumination_box == false && noise_box == true) {
	saveAs("tiff", save_folder + name_file +"_Events _GaussianNoise_"+std_ratio);
}
	
