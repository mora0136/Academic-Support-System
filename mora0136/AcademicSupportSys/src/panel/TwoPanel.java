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
                int font = 16;

                if(windowWidth <= 600){
                    font = 0;
                }

                if(windowWidth < 800){
                    int width = (int)(windowWidth*0.0625);
                    buttonProperties(back, backImg, width, windowHeight, font, false);

                    //Since addNew, edit and delete buttons have an image to the left of text, the font can be displayed longer
                    if(windowWidth < 500) {
                        buttonProperties(addNew, addNewImg, width, windowHeight, 0, true);
                        buttonProperties(edit, editImg, width, windowHeight, 0, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 0, true);
                    }else{
                        buttonProperties(addNew, addNewImg, width, windowHeight, 16, true);
                        buttonProperties(edit, editImg, width, windowHeight, 16, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 16, true);
                    }
                    saveImg = saveImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);

                }else{
                    buttonProperties(back, backImg, 50, 50, font, false);
                    buttonProperties(addNew, addNewImg, 50, 50, font, true);
                    buttonProperties(edit, editImg, 50, 50, font, true);
                    buttonProperties(delete, deleteImg, 50, 50, font, true);
//                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
//                    listProperties(contactList, 32);
                }
            }
        });
    }

    public void listProperties(JList list, int fontSize){
        list.setFont(new Font("Arial", Font.PLAIN, fontSize));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Details the standard design layout of a button inside this display
     * @param btn The button to be edited
     * @param img The associated image with the button
     * @param width The desired width of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param height The desired height of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param fontSize The font size desired on the button(0 disabled the text from being viewable)
     * @param IconLeft Is true if the desired position of the text is to the right of the image
     */
    public void buttonProperties(JButton btn, Image img, int width, int height, int fontSize, boolean IconLeft){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        if(!IconLeft){
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
        }else{
            btn.setVerticalTextPosition(SwingConstants.CENTER);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setFocusPainted(false);
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