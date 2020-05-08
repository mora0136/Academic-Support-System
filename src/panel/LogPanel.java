package panel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class LogPanel extends JPanel {
    JLabel day;
    JTable logs;

    LogPanel(LocalDate date, List log){
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new BorderLayout());
        if(DAYS.between(date, LocalDate.now()) == 0){
            day = new JLabel("Today");
        } else if(DAYS.between(date, LocalDate.now()) == 1){
            day = new JLabel("Yesterday");
        }else {
            day = new JLabel(date.format(DateTimeFormatter.ofPattern("EEEE, dd LLLL")));
        }
//        c.fill = GridBagConstraints.BOTH;
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        gb.setConstraints(day, c);
        day.setFont(new Font("Arial", Font.PLAIN, 32));
        add(day, BorderLayout.NORTH);

        logs = new JTable(new LogTableModel(log));
//        c.fill = GridBagConstraints.BOTH;
//        c.insets = new Insets(5, 20, 5, 20);
//        gb.setConstraints(logs, c);
        logs.getSelectionModel().addListSelectionListener(this::valueChanged);
        logs.setFont(new Font("Arial", Font.PLAIN, 24));
        logs.setRowHeight(30); // very temp please change to more structured approach
        add(logs, BorderLayout.CENTER);
    }
    public void valueChanged(ListSelectionEvent e){
        System.out.println(logs.getModel().getValueAt(logs.getSelectedRow(), 3));
    }
}
