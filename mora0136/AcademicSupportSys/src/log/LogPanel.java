package log;

import contacts.Contact;
import contacts.ContactDisplayPanel;
import panel.ComProps;
import panel.UploadPanelDisabled;
import upload.Upload;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/*
 LogPanel is the panel structure for a single day of logs. It composes the title/day of the logs, followed by a table
 of the logs. This approach allows for multiple instances of these panels to be used to form a potentially endless
 scrolling Panel inside of HistoryPanel for the visual display of logs. The table utilises a custom Table LogTableModel
 to be able to present the information desired. Any time a Log is chosen from the table. A pop-up window is shown with a
 non-editable view of that Log's associated data(Contact or Upload). This is currently limited to only showing the
 newest data of that Contact or Upload, not the changes at the point of the log. This can be changed by adding more
 tables to the database, but adds complexity and time consuming. This is already beyond the scope of the spec so it was
 decided this was sufficient.
 */

public class LogPanel extends JPanel{
    JLabel day;
    JTable logTable;
    int mainFont = 32;

    public LogPanel(LocalDate date, List log){
        setLayout(new BorderLayout());
        if(DAYS.between(date, LocalDate.now()) == 0){
            day = new JLabel("Today");
        } else if(DAYS.between(date, LocalDate.now()) == 1){
            day = new JLabel("Yesterday");
        }else {
            day = new JLabel(date.format(DateTimeFormatter.ofPattern("EEEE, dd LLLL")));
        }
        add(day, BorderLayout.NORTH);

        logTable = new JTable(new LogTableModel(log));
        logTable.getSelectionModel().addListSelectionListener(this::valueChanged);
        add(logTable, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int rowHeight = 30;

                if (windowWidth < 550) {
                    rowHeight = (int)(windowWidth/(550/30));
                    headerFont = (int)windowWidth /(550/headerFont);
                    listFont = (int)(windowWidth/(550/listFont));
                }
                ComProps.headingProperties(day, headerFont);
                logTable.setFont(new Font("Arial", Font.PLAIN, listFont));
                setJTableColumnsWidth(logTable, getWidth()-50, 20, 20, 80);
                logTable.setRowHeight(rowHeight);
                repaint();
                revalidate();
            }
        });

    }
    //Courtesy of https://www.codejava.net/java-se/swing/setting-column-width-and-row-height-for-jtable
    public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
                                             double... percentages) {
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
        }
    }

    public void valueChanged(ListSelectionEvent e){
        LogTableModel tableModel = (LogTableModel) logTable.getModel();
        Log l;
        if(logTable.getSelectedRow() != -1) {
            switch ((String) tableModel.getValueAt(logTable.getSelectedRow(), 0)) {
                case "Upload":
                    UploadPanelDisabled upload = null;
                    upload = new UploadPanelDisabled();
                    l = (Log) tableModel.getValueAt(logTable.getSelectedRow(), 3);
                    Upload u = (Upload) l.getData();
                    upload.setToExistingUpload(u.getUploadID());
                    upload.setPreferredSize(new Dimension(1280, 720));
                    JOptionPane.showConfirmDialog(null, upload, "Viewing Upload", JOptionPane.PLAIN_MESSAGE);
                    break;
                case "Contact":
                    //Display the shown contact in a non editable panel
                    l = (Log) tableModel.getValueAt(logTable.getSelectedRow(), 3);
                    Contact c = (Contact) l.getData();
                    ContactDisplayPanel dp = new ContactDisplayPanel(c);
                    dp.setMainFont(64);// Because view is smaller then intended use the font size is doubled so it is readable
                    dp.setPreferredSize(new Dimension(350, 350));
                    JOptionPane.showConfirmDialog(null, dp, "Viewing Contact", JOptionPane.PLAIN_MESSAGE);
                    break;
            }
        }
    }
}
