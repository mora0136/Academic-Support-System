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

public class LogPanel extends JPanel{
    JLabel day;
    JTable logTable;
    int mainFont = 32;

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
//        day.setFont(new Font("Arial", Font.PLAIN, 32));
        add(day, BorderLayout.NORTH);

        logTable = new JTable(new LogTableModel(log));
        logTable.getSelectionModel().addListSelectionListener(this::valueChanged);
//        logTable.setFont(new Font("Arial", Font.PLAIN, 24));
//        logTable.setRowHeight(30); // very temp please change to more structured approach
//        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//        TableColumnAdjuster tca = new TableColumnAdjuster(logTable);
//        tca.setDynamicAdjustment(true);
//        tca.adjustColumns();
        add(logTable, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int bodyFont = (int)(mainFont*(0.5));
                int checkBoxFont = (int)(mainFont*(0.75));
                int width = 50;
                int height = 50;
                int rowHeight = 30;

                if (windowWidth < 550) {
                    height = (int) (windowWidth / (550/100));
                    rowHeight = (int)(windowWidth/(550/30));
                    headerFont = (int)windowWidth /(550/headerFont);
                    listFont = (int)(windowWidth/(550/listFont));
                    bodyFont = (int)(Double.min(windowWidth/(550/bodyFont), windowHeight/(450/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(550/checkBoxFont), windowHeight/(450/checkBoxFont)));
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
    //Courtest of https://www.codejava.net/java-se/swing/setting-column-width-and-row-height-for-jtable
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
                    JOptionPane.showConfirmDialog(null, upload, "Viewing Contact", JOptionPane.PLAIN_MESSAGE);
                    break;
                case "Contact":
                    //Display the shown contact in a non editable panel
                    l = (Log) tableModel.getValueAt(logTable.getSelectedRow(), 3);
                    Contact c = (Contact) l.getData();
                    ContactDisplayPanel dp = new ContactDisplayPanel(c);
                    dp.setPreferredSize(new Dimension(350, 350));
                    JOptionPane.showConfirmDialog(null, dp, "Viewing Contact", JOptionPane.PLAIN_MESSAGE);
                    break;
            }
        }
    }
}
