package panel;

import org.jdatepicker.JDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class HistoryPanel extends TwoPanel implements FocusListener{
    JButton reset, fromButton, toButton;
    Image resetImg;
    JPanel filterPanel, sortPanel;
    JLabel filterLabel, dateLabel, fromLabel, toLabel, typeLabel, actionLabel, titleLabel, sortlabel;
    JDatePicker fromDate, toDate;
    JComboBox typeBox, actionBox, sortBox;
    JTextField titleField;
    JTable results;
    JFormattedTextField fromText, toText;
    JScrollPane logScroll;
    int mainFont = 32;

    HistoryPanel(JPanel pane) throws IOException, SQLException {
        super(pane);

        contextPanel.remove(searchField);
        rightPanel.remove(optionPanel);
        addNew.setText("Apply Filter"); //Repurpose this button for applying the filters selected

        reset = new JButton("Reset Filter");
        resetImg = ImageIO.read(new File("resources/reset.png"));
        reset.addActionListener(this::actionPerformedReset);
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,0,0,0);
        c.weightx = 1;
        gridBag.setConstraints(reset, c);
        contextPanel.add(reset);

        filterPanel = new JPanel();
        filterPanel.setLayout(gridBag);

        filterLabel = new JLabel("Filters:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 40, 20, 0);
        c.weighty = 0.15;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(filterLabel, c);
        filterPanel.add(filterLabel);

        dateLabel = new JLabel("By date:");
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 60, 0, 30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(dateLabel, c);
        filterPanel.add(dateLabel);

        fromLabel = new JLabel("From:");
        c.weighty = 0.15;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 80, 0, 0);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fromLabel, c);
        filterPanel.add(fromLabel);

        fromDate = new JDatePicker();
        fromText = fromDate.getFormattedTextField();
        fromButton = fromDate.getButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 25;
        c.insets = new Insets(0,0,0,30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(fromDate, c);
        filterPanel.add(fromDate);

        toLabel = new JLabel("To:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.insets = new Insets(0, 80, 0, 0);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(toLabel, c);
        filterPanel.add(toLabel);

        toDate = new JDatePicker();
        toText = toDate.getFormattedTextField();
        toButton = toDate.getButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,0,30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(toDate, c);
        filterPanel.add(toDate);

        typeLabel = new JLabel("By Type:");
        c.weightx = 0;
        c.insets = new Insets(20, 60, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(typeLabel, c);
        filterPanel.add(typeLabel);

        typeBox = new JComboBox(new String[]{"Upload", "Contact"});
        typeBox.setSelectedIndex(-1);
        typeBox.addActionListener(this::actionPerformedType);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 0, 0, 30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(typeBox, c);
        filterPanel.add(typeBox);

        actionLabel = new JLabel("By Action:");
        actionLabel.setEnabled(false);
        c.insets = new Insets(20, 60, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(actionLabel, c);
        filterPanel.add(actionLabel);

        actionBox = new JComboBox();
        actionBox.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 0, 0, 30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(actionBox, c);
        filterPanel.add(actionBox);

        titleLabel = new JLabel("By Title:");
        c.insets = new Insets(20, 60, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(titleLabel, c);
        filterPanel.add(titleLabel);

        titleField = new JTextField("Enter Search Query...");
        titleField.setForeground(Color.GRAY);
        titleField.addFocusListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 0, 0, 30);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(titleField, c);
        filterPanel.add(titleField);

        leftPanel.add(filterPanel, BorderLayout.CENTER);

        //The sort Panel
        sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortlabel = new JLabel("Sort By:");
        sortPanel.add(sortlabel);
        sortBox = new JComboBox(new String[]{"Date<ASC>", "Date<DESC>"});
        sortPanel.add(sortBox);
        sortPanel.setAlignmentY(JPanel.RIGHT_ALIGNMENT);

        rightPanel.add(sortPanel, BorderLayout.NORTH);


        //The display area of all logs
//        ArrayList<Log> list = new ArrayList();
//        for(int i = 10; i<20; i++){
//            list.add(new Log(i, "12:0"+i, "Upload", "All have the same description"));
//        }

//        LocalDate dateFrom = LocalDate.of(2020, 4, 7);
//        LocalDate dateTo = LocalDate.now();
//
//        displayPanel.setLayout(gridBag);
//        c.fill = GridBagConstraints.BOTH;
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.insets = new Insets(20, 20, 20, 20);
//        c.weightx = 1;
//        c.weighty = 0;
//
//
//        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
////            System.out.println(dateTo);
//            java.util.List<Log> l = LogDB.getLogsForDay(dateTo.minusDays(1), dateTo);
////            System.out.println(l);
//            if(!l.isEmpty()) {
//                JPanel log = new LogPanel(dateTo, l);
//                gridBag.setConstraints(log, c);
//                displayPanel.add(log);
//            }
//
//        }

        displayPanel.setLayout(gridBag);
        displayLogs();
        logScroll = new JScrollPane(displayPanel);

        rightPanel.add(logScroll, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int bodyFont = (int)(mainFont*(0.5));
                int checkBoxFont = (int)(mainFont*(0.75));
                int width = 50;
                int height = 50;

                if (windowWidth < 1300 || windowHeight < 600) {
                    width = (int) (windowHeight * 0.0625);
                    headerFont = (int)(Double.min(windowWidth /(1300/headerFont), windowHeight/(600/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(1300/listFont), windowHeight/(600/listFont)));
                    bodyFont = (int)(Double.min(windowWidth/(1300/bodyFont), windowHeight/(600/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(1300/checkBoxFont), windowHeight/(600/checkBoxFont)));
                }

                buttonProperties(back, backImg, width, height, headerFont, true);
                buttonProperties(reset, resetImg, width, height, headerFont, true);
                buttonProperties(fromButton, null, width, height, listFont, false);
                buttonProperties(toButton, null, width, height, listFont, false);
                headingProperties(filterLabel, headerFont+6);
                headingProperties(dateLabel, headerFont);
                headingProperties(fromLabel, headerFont);
                headingProperties(toLabel, headerFont);
                headingProperties(typeLabel, headerFont);
                headingProperties(actionLabel, headerFont);
                headingProperties(titleLabel, headerFont);
                headingProperties(sortlabel, listFont);
                textFieldProperties(titleField, listFont);
                fromText.setFont(new Font("Arial", Font.PLAIN, listFont));
                toText.setFont(new Font("Arial", Font.PLAIN, listFont));
                typeBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                actionBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                sortBox.setFont(new Font("Arial", Font.PLAIN, bodyFont));

            }
        });

    }

//    public void valueChanged(ListSelectionEvent e){
//        switch((String)results.getModel().getValueAt(results.getSelectedRow(), 2)){
//            case "Upload":
//                System.out.println("upload selected");
//                break;
//            case "Contact":
//                System.out.println("Contact selected");
//                break;
//        }
//        System.out.println(results.getModel().getValueAt(results.getSelectedRow(), 3));
//    }



    private void headingProperties(JLabel label, int fontSize){
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
    }

    private void textFieldProperties(JTextField field, int fontSize){
        field.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    private void textAreaProperties(JTextArea area, int fontSize){
        area.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    //Apply Filter Button action
    public void actionPerformedNew(ActionEvent e){
        displayPanel.removeAll();
        LocalDate dateFrom, dateTo;
        if(fromDate.getModel().isSelected()) {
            Calendar calFrom = (Calendar) (fromDate.getModel().getValue());
            Instant instFrom = calFrom.getTime().toInstant();
            dateFrom = instFrom.atZone(ZoneId.systemDefault()).toLocalDate();
        }else {
            dateFrom = LocalDate.of(2020, 1, 1);
        }

        if(toDate.getModel().isSelected()) {
            Calendar calTo = (Calendar) (toDate.getModel().getValue());
            Instant instTo = calTo.getTime().toInstant();
            dateTo = instTo.atZone(ZoneId.systemDefault()).toLocalDate();
        }else{
            dateTo = LocalDate.now();
        }

        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
            java.util.List<Log> l;
            if(typeBox.getSelectedIndex() != -1){
                if(actionBox.getSelectedIndex() != -1){
                    l = LogDB.getLogsForDayWithTypeActionFilter(dateTo.minusDays(1), dateTo, (String)typeBox.getSelectedItem(), (String)actionBox.getSelectedItem());
                }else{
                    l = LogDB.getLogsForDayWithTypeFilter(dateTo.minusDays(1), dateTo, (String)typeBox.getSelectedItem());
                }
            }else{
                l = LogDB.getLogsForDay(dateTo.minusDays(1), dateTo);
            }
            if(!l.isEmpty()) {
                JPanel log = new LogPanel(dateTo, l);
                gridBag.setConstraints(log, c);
                displayPanel.add(log);
            }

        }
        repaint();
        revalidate();
    }

    public void actionPerformedType(ActionEvent e){
        if(typeBox.getSelectedIndex() != -1){
            actionBox.setEnabled(true);
            actionLabel.setEnabled(true);
            if(typeBox.getSelectedIndex() == 0){ //Upload
                actionBox.setModel(new DefaultComboBoxModel(new String[]{"Uploaded", "Saved", "Deleted"}));
            }else if(typeBox.getSelectedIndex() == 1){ //Contact
                actionBox.setModel(new DefaultComboBoxModel(new String[]{"Added", "Saved", "Deleted"}));
            }
            actionBox.setSelectedIndex(-1);
        }else{
            actionBox.setModel(new DefaultComboBoxModel());
            actionBox.setEnabled(false);
            actionLabel.setEnabled(false);
        }
    }

    public void actionPerformedReset(ActionEvent e){
        displayPanel.removeAll();
        fromDate.getModel().setSelected(false);
        toDate.getModel().setSelected(false);
        typeBox.setSelectedIndex(-1);
        actionBox.setModel(new DefaultComboBoxModel());
        actionBox.setEnabled(false);
        actionLabel.setEnabled(false);
        titleField.setText("");
        displayLogs();
    }

    public void displayLogs(){
        LocalDate dateFrom = LocalDate.of(2020, 4, 7);
        LocalDate dateTo = LocalDate.now();

        displayPanel.setLayout(gridBag);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 1;
        c.weighty = 0;

        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
            java.util.List<Log> l = LogDB.getLogsForDay(dateTo.minusDays(1), dateTo);
            if(!l.isEmpty()) {
                JPanel log = new LogPanel(dateTo, l);
                gridBag.setConstraints(log, c);
                displayPanel.add(log);
            }

        }
    }

    //Allow for the display of the "Search..." text on the text area while not in focus
    public void focusGained(FocusEvent e) {
        titleField.setText("");
        titleField.setForeground(Color.BLACK);
    }
    public void focusLost(FocusEvent e) {
        if(titleField.getText().isEmpty()){
            titleField.setText("Enter Search Query...");
            titleField.setForeground(Color.GRAY);
        }
    }
}