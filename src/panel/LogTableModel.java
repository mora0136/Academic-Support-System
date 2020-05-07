package panel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LogTableModel extends AbstractTableModel {

    private List<Log> list = new ArrayList<>();

    LogTableModel(List<Log> l){
        list = l;
    }
    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Log l = list.get(rowIndex);
        switch(columnIndex){
            case 0:
                return l.getTime();
            case 1:
                return l.getType();
            case 2:
                return l.getDescription();
            case 3: //This column is not displayed, is strictly here to be able to return the log data.
                return l;
        }
        return null;
    }

    public Log getRow(int rowCount){
        return list.get(rowCount);
    }
}
