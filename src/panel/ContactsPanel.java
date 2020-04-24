package panel;

import contacts.ContactList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ContactsPanel extends JPanel implements DocumentListener, FocusListener{
    CardLayout cardLayout;
    JPanel cardPane, left, right, leftTop, rightTop, rightBottom, panelMiddle;
    JScrollPane scrollMiddle;
    Image backImg, searchImg, addNewImg, editImg, deleteImg, saveImg, tempImg;
    JTextField searchField;
    ContactList contactList;
    JTextField givenNameField, lastNameField, emailField, phoneField;
    JButton edit;

    ContactsPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        left = new JPanel();
        right = new JPanel();
        leftTop = new JPanel();
        panelMiddle = new JPanel();
        rightTop = new JPanel();
        rightBottom = new JPanel();

        contactList = new ContactList();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Set the frame to 1x2 grid(left and right Panels)
        setLayout(new GridLayout(1, 2));

        JButton back = new JButton("Back");
        back.addActionListener(this::actionPerformed);
        backImg = ImageIO.read(new File("resources/back.png"));
//        back.addActionListener(this::actionPerformed);


        searchImg = ImageIO.read(new File("resources/search.png"));

        searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);
        searchField.addActionListener(this::actionPerformed);
        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

//        JButton search = new JButton("Search");
//        search.addActionListener(this::actionPerformed);
//        search.setMnemonic(KeyEvent.VK_ENTER); // still needs to worked on
//        searchImg = ImageIO.read(new File("resources/search.png"));
        //Action Event needed, either make exclusive to search or could auto do it as entering text

        JButton addNew = new JButton("Add New");
        addNew.addActionListener(this::actionPerformed);
        addNewImg = ImageIO.read(new File("resources/add.png"));

        edit = new JButton("Edit");
        edit.addActionListener(this::actionPerformed);
        editImg = ImageIO.read(new File("resources/edit_contact.png"));
        tempImg = editImg;
        saveImg = ImageIO.read(new File("resources/save.png"));

        JButton delete = new JButton("Delete");
        delete.addActionListener(this::actionPerformed);
        deleteImg = ImageIO.read(new File("resources/delete.png"));

        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        leftTop.setLayout(gridBag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridBag.setConstraints(back, c);
        leftTop.add(back);
        c.insets = new Insets(10, 10, 10, 0);
        c.weightx = 25;
        gridBag.setConstraints(searchField, c);
        leftTop.add(searchField);
        c.insets = new Insets(10, 0, 10, 10);
        c.weightx = 2;
//        gridBag.setConstraints(search, c);
//        leftTop.add(search);
        leftTop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        //Add contacts to leftMiddle scroll view
        contactList.setLayout(new BoxLayout(contactList, BoxLayout.Y_AXIS));
        scrollMiddle = new JScrollPane(contactList);

        //Output area
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.Y_AXIS));
        JLabel givenName = new JLabel("Name");
        givenNameField = new JTextField();
        JLabel lastName = new JLabel("Surname");
        lastNameField = new JTextField();
        JLabel email = new JLabel("Email");
        emailField = new JTextField();
        JLabel phone = new JLabel("Phone");
        phoneField = new JTextField();
        //Associate the desired output location of the information contained within contactlist.
        //Allows for buttons press to influence other panel.
        contactList.setOutputPanel(givenNameField, lastNameField, emailField, phoneField);
        givenNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        rightTop.add(givenName);
        rightTop.add(givenNameField);
        rightTop.add(lastName);
        rightTop.add(lastNameField);
        rightTop.add(email);
        rightTop.add(emailField);
        rightTop.add(phone);
        rightTop.add(phoneField);


        rightBottom.setLayout(new GridLayout(1, 2));
        rightBottom.add(edit);
        rightBottom.add(delete);

        left.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        left.add(leftTop, BorderLayout.NORTH);
        left.add(scrollMiddle, BorderLayout.CENTER);
        left.add(addNew, BorderLayout.SOUTH);

        right.add(rightTop, BorderLayout.NORTH);
        right.add(rightBottom, BorderLayout.SOUTH);

        add(left);
        add(right);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                GridBagConstraints c2 = new GridBagConstraints();
                int font = 16;

                if(windowWidth <= 600){
                    font = 0;
                }

                if(windowWidth < 800){
                    int width = (int)(windowWidth*0.0625);
                    buttonProperties(back, backImg, width, width, font, false);
//                    buttonProperties(search, searchImg, width, width, font, false);
                    if(windowWidth < 500) {
                        buttonProperties(addNew, addNewImg, width, width, 0, true);
                        buttonProperties(edit, editImg, width, width, 0, true);
                        buttonProperties(delete, deleteImg, width, width, 0, true);
                    }else{
                        buttonProperties(addNew, addNewImg, width, width, 16, true);
                        buttonProperties(edit, editImg, width, width, 16, true);
                        buttonProperties(delete, deleteImg, width, width, 16, true);
                    }
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(Integer.min(width, width), -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(Integer.min(width, width), -1, Image.SCALE_DEFAULT);
                }else{
                    buttonProperties(back, backImg, 50, 50, font, false);
//                    buttonProperties(search, searchImg, 50, 50, font, false);
                    buttonProperties(addNew, addNewImg, 50, 50, font, true);
                    buttonProperties(edit, editImg, 50, 50, font, true);
                    buttonProperties(delete, deleteImg, 50, 50, font, true);
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(Integer.min(50, 50), -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(Integer.min(50, 50), -1, Image.SCALE_DEFAULT);
                }
            }
        });
    }
    private void buttonProperties(JButton btn, Image img, int width, int height, int font, boolean IconLeft){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));
        if(!IconLeft){
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
        }
        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, font));
        btn.setFocusPainted(false);
//        gridBag.setConstraints(btn,c);
    }


    public void actionPerformed(ActionEvent e){
        System.out.println("Action performed");
        switch(e.getActionCommand()) {
            case "Back":
                cardLayout.show(cardPane, "Home");
                break;
            case "Add New":
                break;
            case "Edit":
                givenNameField.setEditable(true);
                lastNameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);
                edit.setText("Save");
//                tempImg = editImg;
                editImg = saveImg;
                edit.setIcon(new ImageIcon(editImg));
                break;

            case "Save":
                givenNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);
                edit.setText("Edit");
                editImg = tempImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
            case "Delete":
                break;
        }
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
//                search();
    }

    private void search(){
//                if(searchField.getText() != "Search...") {
        contactList.searchForContact(searchField.getText());
//                }
    }

    //Note that whatever was in the box previously is erased
    public void focusGained(FocusEvent e) {
        searchField.setText("");
        searchField.setForeground(Color.BLACK);
    }
    public void focusLost(FocusEvent e) {
        if(searchField.getText().isEmpty()){
            searchField.getDocument().removeDocumentListener(this);
            searchField.setText("Search...");
            searchField.setForeground(Color.GRAY);
            searchField.getDocument().addDocumentListener(this);
        }
    }
}