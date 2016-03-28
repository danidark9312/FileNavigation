package com.danielgutierrez.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.danielgutierrez.filesLookUp.FileCached;


class JTableModelFileManager extends AbstractTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Name","File Path","Size",""};
                                    
    private Object[][] data;
    
    public JTableModelFileManager(List<List<FileCached>> candidateGroup){
    	List<FileCached> dataTemp=new ArrayList<FileCached>();
    	
    	for (List<FileCached> list : candidateGroup) {
			if(list!=null && list.size()>1){
				for (FileCached fileTmp : list) {
					dataTemp.add(fileTmp);
				}
			}
		}
    	data = new Object[dataTemp.size()][3];
    	
    	for(int i = 0;i<dataTemp.size();i++){
    		data[i] = new Object[]
    		{
    				dataTemp.get(i).getFile().getName(),
    				dataTemp.get(i).getFile().getAbsolutePath(),
    				dataTemp.get(i).getSizeStr(),
    				new Boolean(dataTemp.get(i).isChecked())
    		};
    	}
    	System.out.println("table loaded");
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    public Object[][] getDataChecked(){
    	List<Object[]> temp = new ArrayList<Object[]>();
    	
    	for(int i = 0;i<data.length;i++){
    		if((Boolean)data[i][3]){
    			temp.add(data[i]);
    		}
    	}
    	System.out.println("files checkeds: "+temp.toArray(new Object[0][0]));
    	return temp.toArray(new Object[0][0]);
    }
}