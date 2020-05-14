package panel;

import log.Log;
import log.LogDB;
import log.LogPanel;
import org.jdatepicker.JDatePicker;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

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
    SuffixTrie st;

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

        sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

//        titleLabel = new JLabel("By Title:");
////        c.insets = new Insets(20, 60, 0, 0);
////        c.fill = GridBagConstraints.HORIZONTAL;
////        c.gridwidth = GridBagConstraints.RELATIVE;
////        gridBag.setConstraints(titleLabel, c);
//        sortPanel.add(titleLabel);

//        titleField = new JTextField("Enter Search Query...", 15);//Need to adjust columns
//        titleField.setForeground(Color.GRAY);
//        titleField.addFocusListener(this);
////        c.fill = GridBagConstraints.HORIZONTAL;
////        c.insets = new Insets(20, 0, 0, 30);
////        c.gridwidth = GridBagConstraints.REMAINDER;
////        gridBag.setConstraints(titleField, c);
//        sortPanel.add(titleField);

        leftPanel.add(filterPanel, BorderLayout.CENTER);

        //The sort Panel
        sortlabel = new JLabel("Sort By:");
        sortPanel.add(sortlabel);
        sortBox = new JComboBox(new String[]{"Date<ASC>", "Date<DESC>"});
        sortBox.addActionListener(this::actionPerformedSort);
        sortPanel.add(sortBox);
        sortPanel.setAlignmentY(JPanel.RIGHT_ALIGNMENT);

        rightPanel.add(sortPanel, BorderLayout.NORTH);

        displayPanel.setLayout(gridBag);
        displayLogs();
        logScroll = new JScrollPane(displayPanel);
        logScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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
//                ComProps.headingProperties(titleLabel, headerFont);
                ComProps.headingProperties(sortlabel, listFont);
//                ComProps.textFieldProperties(titleField, listFont);
                fromText.setFont(new Font("Arial", Font.PLAIN, listFont));
                toText.setFont(new Font("Arial", Font.PLAIN, listFont));
                typeBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                actionBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                sortBox.setFont(new Font("Arial", Font.PLAIN, bodyFont));

            }
        });

    }

    //Apply Filter Button action
    public void actionPerformedNew(ActionEvent e){
        displayLogs();
//        displayPanel.removeAll();
//        LocalDate dateFrom, dateTo;
//        if(fromDate.getModel().isSelected()) {
//            Calendar calFrom = (Calendar) (fromDate.getModel().getValue());
//            Instant instFrom = calFrom.getTime().toInstant();
//            dateFrom = instFrom.atZone(ZoneId.systemDefault()).toLocalDate();
//        }else {
//            dateFrom = LocalDate.of(2020, 1, 1);
//        }
//
//        if(toDate.getModel().isSelected()) {
//            Calendar calTo = (Calendar) (toDate.getModel().getValue());
//            Instant instTo = calTo.getTime().toInstant();
//            dateTo = instTo.atZone(ZoneId.systemDefault()).toLocalDate();
//        }else{
//            dateTo = LocalDate.now();
//        }
//
//        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
//            java.util.List<Log> l;
//            if(typeBox.getSelectedIndex() != -1){
//                if(actionBox.getSelectedIndex() != -1){
//                    l = LogDB.getLogsForDayWithTypeActionFilter(dateTo.minusDays(1), dateTo, (String)typeBox.getSelectedItem(), (String)actionBox.getSelectedItem());
//                }else{
//                    l = LogDB.getLogsForDayWithTypeFilter(dateTo.minusDays(1), dateTo, (String)typeBox.getSelectedItem());
//                }
//            }else{
//                l = LogDB.getLogsForDay(dateTo.minusDays(1), dateTo);
////                l.sort(Comparator.comparing(Log::getDate));
//            }
//            if(!l.isEmpty()) {
//                JPanel log = new LogPanel(dateTo, l);
//                gridBag.setConstraints(log, c);
//                displayPanel.add(log);
//            }
//
//        }
//        searchLogs();
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
        displayPanel.removeAll();
        fromDate.getModel().setSelected(false);
        toDate.getModel().setSelected(false);
        typeBox.setSelectedIndex(-1);
        actionBox.setModel(new DefaultComboBoxModel());
        actionBox.setEnabled(false);
        actionLabel.setEnabled(false);
//        titleField.setText("");
        displayLogs();
    }

    public void displayLogs(){
        st = new SuffixTrie();
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
//                l.sort(Comparator.comparing(Log::getDate));
            }
            if(!l.isEmpty()) {
                for(Log li : l){
                    st.insert((String) li.getData().toString(), li);
                }
                JPanel log = new LogPanel(dateTo, l);
                gridBag.setConstraints(log, c);
                displayPanel.add(log);
            }
            repaint();
            revalidate();

        }

//        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
//            java.util.List<Log> l = LogDB.getLogsForDay(dateTo, dateTo);
//            if(!l.isEmpty()) {
//                for(Log li : l){
//                    st.insert((String) li.getData().toString(), li);
//                }
//                JPanel log = new LogPanel(dateTo, l);
//                gridBag.setConstraints(log, c);
//                displayPanel.add(log);
//            }
//
//        }
    }

    //No implementation, is kind of hard to do with current setup.
    public void actionPerformedSort(ActionEvent e){
    }

    public void searchLogs(){
//        if(titleField.getText().length() == 0 || titleField.equals("Enter Search Query...")) {
////            editable = UploadDB.getAllEditableUploads();
////            editList.setModel(editable);
//            displayLogs();
//            System.out.println("displayLogs");
//        }else {
//            for (Component c : displayPanel.getComponents()) {
//                ArrayList<SuffixIndex> startIndexes;
//                SuffixTrieNode sn = st.get(titleField.getText());
//                if (sn != null) {
//                    startIndexes = sn.getData().getStartIndexes();
//                    for (SuffixIndex s : startIndexes) {
//                        //Prevents duplicate contacts being displayed as a node may contain many subStrings of itself
//                        if (!editable.contains(s.getObj())) {
//                            editable.addElement((Upload) s.getObj());
//                        }
//                    }
//
//                } else {
//                    //This is where an error message should be returned as nothing found.
//                    JLabel noContent = new JLabel("Contact not Found");
//                    noContent.setFont(new Font("Arial", Font.PLAIN, 16));
//                }
//            }
//        }
        System.out.println(st.get(titleField.getText()));
        SuffixTrieNode sn = st.get(titleField.getText());
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