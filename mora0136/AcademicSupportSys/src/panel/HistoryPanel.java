package panel;

import org.jdatepicker.JDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class HistoryPanel extends TwoPanel {
    JButton reset;
    Image resetImg;
    JPanel filterPanel, sortPanel;
    JLabel filterLabel, dateLabel, fromLabel, toLabel, actionLabel, titleLabel, sortlabel;
    JDatePicker fromDate, toDate;
    JComboBox actionBox, sortBox;
    JTextField titleField;
    JTable results;
    int mainFont = 32;


    HistoryPanel(JPanel pane) throws IOException, SQLException {
        super(pane);

        contextPanel.remove(searchField);
        rightPanel.remove(optionPanel);
        addNew.setText("Apply Filter"); //Repurpose this button for applying the filters selected

        reset = new JButton("Reset Filter");
        resetImg = ImageIO.read(new File("resources/reset.png"));
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,0,0,0);
        c.weightx = 1;
        gridBag.setConstraints(reset, c);
        contextPanel.add(reset);

        filterPanel = new JPanel();
        filterPanel.setLayout(gridBag);

        filterLabel = new JLabel("Filters:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 40, 0, 0);
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(filterLabel, c);
        filterPanel.add(filterLabel);

        dateLabel = new JLabel("By date:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 60, 0, 60);
        c.weighty = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(dateLabel, c);
        filterPanel.add(dateLabel);

        fromLabel = new JLabel("From:");
        c.weighty = 0.15;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 80, 0, 60);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fromLabel, c);
        filterPanel.add(fromLabel);

        fromDate = new JDatePicker();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,0,60);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(fromDate, c);
        filterPanel.add(fromDate);

        toLabel = new JLabel("To:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 80, 0, 60);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(toLabel, c);
        filterPanel.add(toLabel);

        toDate = new JDatePicker();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,0,60);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(toDate, c);
        filterPanel.add(toDate);

        actionLabel = new JLabel("By Action:");
        c.insets = new Insets(20, 60, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(actionLabel, c);
        filterPanel.add(actionLabel);

        actionBox = new JComboBox();
        actionBox.setFont(new Font("Arial", Font.PLAIN, 5));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 0, 0, 60);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(actionBox, c);
        filterPanel.add(actionBox);

        titleLabel = new JLabel("By Title:");
        c.insets = new Insets(20, 60, 0, 60);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(titleLabel, c);
        filterPanel.add(titleLabel);

        titleField = new JTextField();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 0, 0, 60);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(titleField, c);
        filterPanel.add(titleField);

        leftPanel.add(filterPanel, BorderLayout.CENTER);

        //The sort Panel
        sortPanel = new JPanel();
        sortlabel = new JLabel("Sort By:");
        sortPanel.add(sortlabel);
        sortBox = new JComboBox();
        sortPanel.add(sortBox);

        rightPanel.add(sortPanel, BorderLayout.NORTH);


        //The display area of all logs
//        ArrayList<Log> list = new ArrayList();
//        for(int i = 10; i<20; i++){
//            list.add(new Log(i, "12:0"+i, "Upload", "All have the same description"));
//        }

        LocalDate dateFrom = LocalDate.of(2020, 5, 7);
        LocalDate dateTo = LocalDate.now();

        displayPanel.setLayout(gridBag);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(20, 20, 20, 20);
        c.weightx = 1;
        c.weighty = 0;


        for(;dateTo.isAfter(dateFrom); dateTo = dateTo.minusDays(1)){
//            System.out.println(dateTo);
            java.util.List<Log> l = LogDB.getLogsForDay(dateTo.minusDays(1), dateTo);
//            System.out.println(l);
            if(!l.isEmpty()) {
                System.out.println(dateTo);
                JPanel log = new LogPanel(dateTo, l);
                gridBag.setConstraints(log, c);
                displayPanel.add(log);
            }

        }
        JScrollPane n = new JScrollPane(displayPanel);

        rightPanel.add(n, BorderLayout.CENTER);

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
                headingProperties(filterLabel, headerFont+6);
                headingProperties(dateLabel, headerFont);
                headingProperties(fromLabel, headerFont);
                headingProperties(toLabel, headerFont);
                headingProperties(actionLabel, headerFont);
                headingProperties(titleLabel, headerFont);
                textFieldProperties(titleField, bodyFont);

            }
        });

    }

    public void valueChanged(ListSelectionEvent e){
        System.out.println(results.getModel().getValueAt(results.getSelectedRow(), 3));
    }



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

    }
}