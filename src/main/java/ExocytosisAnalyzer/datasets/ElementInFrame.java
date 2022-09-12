package ExocytosisAnalyzer.datasets;

import java.util.Vector;

public class ElementInFrame {
	public int Frame_num;
	public Vector<Vesicle> Vesicles ;
	public Vector<Integer> secretion_x ;
	public Vector<Integer> secretion_y ;
	public ElementInFrame(){	
		Vesicles = new Vector<Vesicle>();
		secretion_x = new Vector<Integer>();
		secretion_y = new Vector<Integer>();
	}
	public void removeLink() {
		for (int i = 0; i < Vesicles.size(); i++) {
			Vesicles.elementAt(i).isLinked = false;
		}
	}

}
