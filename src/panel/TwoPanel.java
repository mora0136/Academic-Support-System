package panel;

import contacts.ContactDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

abstract class TwoPanel extends JPanel{
    JPanel cardPane, leftPanel, rightPanel, contextPanel, displayPanel, optionPanel;
    Image backImg, searchImg, addNewImg, editImg, deleteImg, saveImg, tempImg;
    JTextField searchField;
    ContactDB contactDB;
    CardLayout cardLayout;
    JButton edit, delete, addNew, back;
    GridBagLayout gridBag;
    GridBagConstraints c;
    final int mainFont = 32;

    TwoPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        leftPanel = new JPanel();
        rightPanel = new JPanel();
        contextPanel = new JPanel();
        displayPanel = new JPanel();
        optionPanel = new JPanel();
        contactDB = new ContactDB();

        gridBag = new GridBagLayout();
        c = new GridBagConstraints();

        //left and right panels to be added in
        setLayout(new GridLayout(1, 2));

        back = new JButton("Back");
        back.addActionListener(this::actionPerformedBack);
        backImg = ImageIO.read(new File("resources/back.png"));


        searchImg = ImageIO.read(new File("resources/search.png"));

        searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);

        addNew = new JButton("Add New");
        addNew.addActionListener(this::actionPerformedNew);
        addNewImg = ImageIO.read(new File("resources/add.png"));

        edit = new JButton("Edit");
        edit.setEnabled(false);
        edit.addActionListener(this::actionPerformedEdit);
        editImg = ImageIO.read(new File("resources/edit_contact.png"));
        tempImg = editImg;
        saveImg = ImageIO.read(new File("resources/save.png"));

        delete = new JButton("Delete");
        delete.setEnabled(false);
        delete.addActionListener(this::actionPerformedDelete);
        deleteImg = ImageIO.read(new File("resources/delete.png"));

        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        contextPanel.setLayout(gridBag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridBag.setConstraints(back, c);
        contextPanel.add(back);
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 25;
        gridBag.setConstraints(searchField, c);
        contextPanel.add(searchField);

        //Output area when a contact is selected or a new one is to added
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

        optionPanel.setLayout(new GridLayout(1, 2));
        optionPanel.add(edit); optionPanel.add(delete);

        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(addNew, BorderLayout.SOUTH);

        rightPanel.add(optionPanel, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);

        // Details what styles should apply to buttons at the certain size of a window
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

                if (windowWidth < 1300 || windowHeight < 600) {
                    width = (int) (windowHeight * 0.0625);
                    headerFont = (int)(Double.min(windowWidth /(1300/headerFont), windowHeight/(600/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(1300/listFont), windowHeight/(600/listFont)));
                    bodyFont = (int)(Double.min(windowWidth/(1300/bodyFont), windowHeight/(600/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(1300/checkBoxFont), windowHeight/(600/checkBoxFont)));
                }
                    ComProps.buttonProperties(back, backImg, width, height, headerFont, false);
                    ComProps.buttonProperties(addNew, addNewImg, width, height, headerFont, true);
                    ComProps.buttonProperties(edit, editImg, width, height, headerFont, true);
                    ComProps.buttonProperties(delete, deleteImg, width, height, headerFont, true);
                    saveImg = saveImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            }
        });
    }

    /**
     * Details the actions required for a specific button on this panel
     * @param e The Action Event call
     */
    public void actionPerformedBack(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }

    public void actionPerformedNew(ActionEvent e){
        edit.setEnabled(true);
        edit.setText("Save");
        editImg = saveImg;
        edit.setIcon(new ImageIcon(editImg));
    }

    public void actionPerformedEdit(ActionEvent e){
        edit.setText("Save");
        editImg = saveImg;
        edit.setIcon(new ImageIcon(editImg));
    }

    public void actionPerformedSave(ActionEvent e){
        edit.setText("Edit");
        editImg = tempImg;
        edit.setIcon(new ImageIcon(editImg));
    }

    public void actionPerformedDelete(ActionEvent e){
        delete.setEnabled(false);
        edit.setEnabled(false);
    }
}