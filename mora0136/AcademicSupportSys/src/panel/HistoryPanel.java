package panel;

import log.Log;
import log.LogDB;
import log.LogPanel;
import org.jdatepicker.JDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;


/*
 * HistoryPanel inherits from TwoPanel for a common design language. This panel will initially display all logs stored
 * inside of the logs database. Filters allow for a refining in the logs displayed. The Logs themselves are stored in a
 * LogPanel. A logPanel is used for a single day of logs and contains the date as a header and a JTable to list the
 * information. From here a log can be selected to view more info.
 */
public class HistoryPanel extends TwoPanel implements FocusListener{
    JButton reset, fromButton, toButton;
    Image resetImg;
    JPanel filterPanel;
    JLabel filterLabel, dateLabel, fromLabel, toLabel, typeLabel, actionLabel;
    JDatePicker fromDate, toDate;
    JComboBox typeBox, actionBox;
    JTextField titleField;
    JFormattedTextField fromText, toText;
    JScrollPane logScroll;
    int mainFont = 32;

    HistoryPanel(JPanel pane){
        super(pane);

        //Changing some of the properties of TwoPanel to suit context better.
        contextPanel.remove(searchField);
        rightPanel.remove(optionPanel);
        addNew.setText("Apply Filter");

        reset = new JButton("Reset Filter");
        try{
            resetImg = ImageIO.read(new File("resources/reset.png"));
        }catch(IOException e){
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Image files failed to load. No images will be available", "", JOptionPane.WARNING_MESSAGE);
            resetImg = null;
        }
        reset.addActionListener(this::actionPerformedReset);
        reset.setMnemonic('r');
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
        leftPanel.add(filterPanel, BorderLayout.CENTER);

        displayPanel.setLayout(gridBag);
        logScroll = new JScrollPane(displayPanel);
        logScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.add(logScroll, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                resetAll();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int width = 50;
                int height = 50;

                if (windowWidth < 1300 || windowHeight < 600) {
                    width = (int) (windowHeight * 0.0625);
                    headerFont = (int)(Integer.min(windowWidth /(1300/headerFont), windowHeight/(600/headerFont)));
                    listFont = (int)(Integer.min(windowWidth/(1300/listFont), windowHeight/(600/listFont)));
                }

                ComProps.buttonProperties(back, backImg, width, height, headerFont, true);
                ComProps.buttonProperties(reset, resetImg, width, height, headerFont, true);
                ComProps.buttonProperties(fromButton, null, width, height, listFont, false);
                ComProps.buttonProperties(toButton, null, width, height, listFont, false);
                ComProps.headingProperties(filterLabel, headerFont+6);
                ComProps.headingProperties(dateLabel, headerFont);
                ComProps.headingProperties(fromLabel, headerFont);
                ComProps.headingProperties(toLabel, headerFont);
                ComProps.headingProperties(typeLabel, headerFont);
                ComProps.headingProperties(actionLabel, headerFont);
                fromText.setFont(new Font("Arial", Font.PLAIN, listFont));
                toText.setFont(new Font("Arial", Font.PLAIN, listFont));
                typeBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                actionBox.setFont(new Font("Arial", Font.PLAIN, listFont));

            }
        });

    }

    @Override
    protected void resetAll() {
        displayPanel.removeAll();
        fromDate.getModel().setSelected(false);
        toDate.getModel().setSelected(false);
        typeBox.setSelectedIndex(-1);
        actionBox.setModel(new DefaultComboBoxModel());
        actionBox.setEnabled(false);
        actionLabel.setEnabled(false);
        displayLogs();
    }

    //Apply Filter Button action
    public void actionPerformedNew(ActionEvent e){
        displayLogs();
    }

    //Once a type is selected, instantiate the appropriate action selection options.
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
        resetAll();
    }

    public void displayLogs(){
        displayPanel.removeAll();
        displayPanel.setLayout(gridBag);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 1;
        c.weighty = 0;

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

        for(;dateTo.isAfter(dateFrom.minusDays(1)); dateTo = dateTo.minusDays(1)){
            java.util.List<Log> l;
            if(typeBox.getSelectedIndex() != -1){
                if(actionBox.getSelectedIndex() != -1){
                    l = LogDB.getLogsForDayWithTypeActionFilter(dateTo, (String)typeBox.getSelectedItem(), (String)actionBox.getSelectedItem());
                }else{
                    l = LogDB.getLogsForDayWithTypeFilter(dateTo, (String)typeBox.getSelectedItem());
                }
            }else{
                l = LogDB.getLogsForDay(dateTo);
            }
            if(!l.isEmpty()) {
                JPanel log = new LogPanel(dateTo, l);
                gridBag.setConstraints(log, c);
                displayPanel.add(log);
            }

        }
        revalidate();
        repaint();
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