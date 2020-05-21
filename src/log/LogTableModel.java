package log;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/*
 Defines the logic and style of the Log Table. The key feature here is that only 3 of the four columns are displayed.
 This allows for an object(Contact or Upload) to be associated to a row for easy retrieval.
 */

public class LogTableModel extends AbstractTableModel {

    private final List<Log> list;

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
                return l.getType();
            case 1:
                return l.getAction();
            case 2:
                return l.getData();
            case 3: //This column is not displayed, is strictly here to be able to return the log data.
                return l;
        }
        return null;
    }

    public Log getRow(int rowCount){
        return list.get(rowCount);
    }
}
