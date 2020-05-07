package panel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class LogPanel extends JPanel {
    JLabel day;
    JTable logs;

    LogPanel(String date, List log){
        setLayout(new BorderLayout());

        day = new JLabel(date);
        add(day, BorderLayout.NORTH);

        logs = new JTable(new LogTableModel(log));
        logs.getSelectionModel().addListSelectionListener(this::valueChanged);
        add(logs, BorderLayout.CENTER);
    }
    public void valueChanged(ListSelectionEvent e){
        System.out.println(logs.getModel().getValueAt(logs.getSelectedRow(), 3));
    }
}
