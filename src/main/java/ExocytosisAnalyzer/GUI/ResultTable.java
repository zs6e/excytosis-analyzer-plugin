package ExocytosisAnalyzer.GUI;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ResultTable extends JTable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */


	public ResultTable(Object[][] rows, String[] columns)    // Default 
	{
		super(rows, columns);
	}
	
	public ResultTable(DefaultTableModel tableModel) {    //// DefaultTableModel
		super(tableModel);
	}
	
	public boolean isCellEditable(int row, int column) //unEditable
	{
		return false;
	}
	
	
}
