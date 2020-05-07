package panel;

import contacts.Contact;
import contacts.ContactDB;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ContactsPanel extends TwoPanel implements DocumentListener, FocusListener{
    JTextField givenNameField, lastNameField, emailField, phoneField;
    JScrollPane scrollContactPanel;
    ContactDB contactDB;
    JList contactList;
    int contactSelected = 0;
    DefaultListModel<Contact> listOfContacts;

    ContactsPanel(JPanel pane) throws IOException {
        super(pane);
        contactDB = new ContactDB();

        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        listOfContacts = contactDB.getListModel();
        contactList = new JList(listOfContacts);
        contactList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(contactList);

        //Output area when a contact is selected or a new one is to added
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        JLabel givenName = new JLabel("Name");
        givenNameField = new JTextField();
        givenNameField.setEditable(false);

        JLabel lastName = new JLabel("Surname");
        lastNameField = new JTextField();
        lastNameField.setEditable(false);

        JLabel email = new JLabel("Email");
        emailField = new JTextField();
        emailField.setEditable(false);

        JLabel phone = new JLabel("Phone");
        phoneField = new JTextField();
        phoneField.setEditable(false);

//        contactList.setOutputPanel(givenNameField, lastNameField, emailField, phoneField, edit, delete);
        displayPanel.add(givenName); displayPanel.add(givenNameField);
        displayPanel.add(lastName); displayPanel.add(lastNameField);
        displayPanel.add(email); displayPanel.add(emailField);
        displayPanel.add(phone); displayPanel.add(phoneField);

        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);

        rightPanel.add(displayPanel, BorderLayout.NORTH);
//        rightPanel.add(optionPanel, BorderLayout.SOUTH);

//        add(leftPanel);
//        add(rightPanel);

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

                    //Since addNew, edit and delete buttons have an image to the left of text, the font can be displayed longer
                    if(windowWidth < 500) {
                        listProperties(contactList, 16);
                    }else{
                        listProperties(contactList, 24);
                    }

                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);

                }else{
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
                    listProperties(contactList, 32);
                }
            }
        });
    }

    public void actionPerformedNew(ActionEvent e){
        contactSelected = 0;
        givenNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        edit.setEnabled(true);
        givenNameField.setEditable(true);
        lastNameField.setEditable(true);
        emailField.setEditable(true);
        phoneField.setEditable(true);
        edit.setText("Save");
        editImg = saveImg;
        edit.setIcon(new ImageIcon(editImg));
    }

    public void actionPerformedEdit(ActionEvent e){
        switch(e.getActionCommand()) {
            case "Edit":
                givenNameField.setEditable(true);
                lastNameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);
                edit.setText("Save");
                editImg = saveImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
            case "Save":
                givenNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);
                if (contactSelected == 0) {
                    contactDB.addContact(givenNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText());
                    edit.setEnabled(false);
                    delete.setEnabled(false);
                    givenNameField.setText("");
                    lastNameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                } else {
                    contactDB.updateContact(givenNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText(), contactSelected);
                }
                edit.setText("Edit");
                editImg = tempImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
        }
    }



    public void actionPerformedDelete(ActionEvent e){
        contactDB.deleteContact(contactSelected);
        delete.setEnabled(false);
        edit.setEnabled(false);
        givenNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        givenNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
    }

    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting() == false) {
            if (contactList.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                givenNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                phoneField.setText("");


            } else {
                //Selection, enable the fire button.
                edit.setEnabled(true);
                delete.setEnabled(true);
                Contact c = (Contact) contactList.getModel().getElementAt(contactList.getSelectedIndex());
                givenNameField.setText(c.getName());
                lastNameField.setText(c.getSurname());
                emailField.setText(c.getEmail());
                phoneField.setText(c.getPhone());
                contactSelected = c.getContact_ID();
            }
        }
    }



    //Any text entry into JTextField will search for the contact desired.
    @Override
    public void insertUpdate(DocumentEvent e) { search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) { search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    private void search(){
        contactDB.searchForContact(searchField.getText());
        listOfContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            listOfContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
        }
        //Older way of doing it is below, above means just editing list not container as well.
//        listOfContacts = contactDB.getListModel();
//        leftPanel.remove(scrollContactPanel);
//        contactList = new JList(contactDB.getListModel());
//
//        System.out.println(contactDB.getListModel());
//
//        listProperties(contactList);
//        contactList.addListSelectionListener(this::valueChanged);
//        scrollContactPanel = new JScrollPane(contactList);
//        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
//        revalidate();
//        repaint();
    }

    //Allow for the display of the "Search..." text on the text area while not in focus
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