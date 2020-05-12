package log;

import contacts.Contact;
import contacts.ContactDisplayPanel;
import upload.Upload;
import panel.UploadPanelDisabled;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class LogPanel extends JPanel{
    JLabel day;
    JTable logTable;

    public LogPanel(LocalDate date, List log){
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
        day.setFont(new Font("Arial", Font.PLAIN, 32));
        add(day, BorderLayout.NORTH);

        logTable = new JTable(new LogTableModel(log));
        logTable.getSelectionModel().addListSelectionListener(this::valueChanged);
        logTable.setFont(new Font("Arial", Font.PLAIN, 24));
        logTable.setRowHeight(30); // very temp please change to more structured approach
        TableColumnAdjuster tca = new TableColumnAdjuster(logTable);
        tca.adjustColumns();
        add(logTable, BorderLayout.CENTER);
    }
    public void valueChanged(ListSelectionEvent e){
        LogTableModel tableModel = (LogTableModel) logTable.getModel();
        Log l;
        switch((String)tableModel.getValueAt(logTable.getSelectedRow(), 0)){
            case "Upload":
                System.out.println("upload selected");
                UploadPanelDisabled upload = null;
                upload = new UploadPanelDisabled();
                l = (Log)tableModel.getValueAt(logTable.getSelectedRow(), 3);
                Upload u = (Upload)l.getActionType();
                upload.setToExistingUpload(u.getUploadID());
                upload.setPreferredSize(new Dimension(1280, 720));
                JOptionPane.showConfirmDialog(null, upload, "Viewing Contact", JOptionPane.PLAIN_MESSAGE);
                break;
            case "Contact":
                //Display the shown contact in a non editable panel
                l = (Log)tableModel.getValueAt(logTable.getSelectedRow(), 3);
                Contact c = (Contact)l.getActionType();
                ContactDisplayPanel dp = new ContactDisplayPanel(c);
                JOptionPane.showConfirmDialog(null, dp, "Viewing Contact", JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }
}
