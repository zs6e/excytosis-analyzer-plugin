/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package ExocytosisAnalyzer;


import java.io.File;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ExocytosisAnalyzer.GUI.My_Gui;
import ij.ImagePlus;
import io.scif.img.ImgOpener;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;




/** An ImageJ2 command . */
@Plugin(type = Command.class,menuPath = "Plugins>Exocytosis analyzer")
public class Exocytosis_analyzer implements Command {

	// -- Parameters --

	@Parameter
	private ImagePlus imp;


	// -- Command methods --

	@Override
	public void run() {
		My_Gui MyWindows = new My_Gui(imp);
		MyWindows.new VesicleParametresWindows();
		// Set the image's title to the specified value.
		
	}





	// -- Main method --

	/** Tests our command. */
	public static void main(final String... args) throws Exception {
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// open file
		final File file = ij.ui().chooseFile(null, "open");

		// load the image
		ImgOpener opener = new ImgOpener();
		Img<RealType> image= (Img<RealType> ) opener.openImgs(file.getPath()).get( 0 );;
		
        ij.ui().show(image);


		// Launch the command.
		ij.command().run(Exocytosis_analyzer.class, true);
	}





}
