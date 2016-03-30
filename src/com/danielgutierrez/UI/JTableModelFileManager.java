package com.danielgutierrez.UI;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.danielgutierrez.filesLookUp.FileCached;


class JTableModelFileManager extends AbstractTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Name","File Path","Size",""};
	private List<List<FileCached>> candidateGroup;
    private Object[][] data;
    
    private Color baseColors[] = new Color[]{Color.CYAN,Color.LIGHT_GRAY,Color.MAGENTA};
    private Map<Integer,Color> rowsColor = new HashMap<Integer,Color>();
    private float currentHueColor;
    
    public JTableModelFileManager(List<List<FileCached>> candidateGroup){
    	loadDataTable(candidateGroup);
    }
    
    public void loadDataTable(List<List<FileCached>> candidateGroup){
    	this.candidateGroup = candidateGroup;
    	List<FileCached> dataTemp=new ArrayList<FileCached>();
    	int colIndex = 0;
    	for (List<FileCached> list : candidateGroup) {
			if(list!=null && list.size()>1){
				Color currentColor = nextCurrentColor();
				for (FileCached fileTmp : list) {
					setRowColor(colIndex++,currentColor);
					dataTemp.add(fileTmp);
				}
			}
		}
    	data = new Object[dataTemp.size()][3];
    	
    	for(int i = 0;i<dataTemp.size();i++){
    		data[i] = new Object[]{
    				dataTemp.get(i).getFile().getName(),
    				dataTemp.get(i).getFile().getAbsolutePath(),
    				dataTemp.get(i).getSizeStr(),
    				new Boolean(dataTemp.get(i).isChecked())
    		};
    	}
    	
    }
    public void reloadDataTable(){
    	loadDataTable(candidateGroup);
    	fireTableDataChanged();
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
        //if (col < 2) {
            return false;
        /*} else {
            return true;
        }*/
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
    	return temp.toArray(new Object[0][0]);
    }
    public Color getRowColor(int row){
    	return rowsColor.get(row);
    }
    public void setRowColor(int row,Color color){
    	rowsColor.put(row, color);
    }
    
    private Color nextCurrentColor(){
    	currentHueColor = (float) /*currentHueColor>1?0:currentHueColor+.025f*/Math.random();
    	return Color.getHSBColor(currentHueColor, 0.3f, 0.8f);
    }
    
    static class MyTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JTableModelFileManager model = (JTableModelFileManager) table.getModel();
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(model.getRowColor(row));
            return c;
        }
    }
    
}

