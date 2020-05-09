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
    JScrollPane scrollContactPanel;
//    ContactDB contactDB;
    JList contactList;
    int contactSelected = 0;
    DefaultListModel<Contact> listOfContacts;
    ContactDisplayPanel contactInfoPanel;

    ContactsPanel(JPanel pane) throws IOException {
        super(pane);
        contactDB = new ContactDB();

        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        listOfContacts = contactDB.getListModel();
        contactList = new JList(listOfContacts);
        contactList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(contactList);

//Where output was
        contactInfoPanel = new ContactDisplayPanel();

        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);

        //Display panel is there if I still need it
        rightPanel.add(contactInfoPanel, BorderLayout.NORTH);
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
        contactInfoPanel.setEditable(true);
        edit.setEnabled(true);
        edit.setText("Save");
        editImg = saveImg;
        edit.setIcon(new ImageIcon(editImg));
    }

    public void actionPerformedEdit(ActionEvent e){
        switch(e.getActionCommand()) {
            case "Edit":
                contactInfoPanel.setEditable(true);
                edit.setText("Save");
                editImg = saveImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
            case "Save":
                contactInfoPanel.setEditable(false);
                String [] newText = contactInfoPanel.getTextFields();
                if (contactSelected == 0) {
                    contactSelected = contactDB.addContact(newText[0], newText[1], newText[2], newText[3]);
                    edit.setEnabled(false);
                    delete.setEnabled(false);
//                    contactInfoPanel.setTextEmpty();
                    LogDB.logNewContact(contactSelected);
                } else {
                    contactDB.updateContact(newText[0], newText[1], newText[2], newText[3], contactSelected);
                    LogDB.logSavedContact(contactSelected);

                }
                edit.setText("Edit");
                editImg = tempImg;
                edit.setIcon(new ImageIcon(editImg));

                //Refresh the list of contacts as a change has occurred
                listOfContacts.removeAllElements();
                for(int i = 0; i<contactDB.getListModel().getSize(); i++){
                    listOfContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
                }
                break;
        }
    }



    public void actionPerformedDelete(ActionEvent e){
        contactDB.deleteContact(contactSelected);
        delete.setEnabled(false);
        edit.setEnabled(false);
        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(false);
        LogDB.logDeletedContact(contactSelected);
        listOfContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            listOfContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
        }
    }

    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting() == false) {
            if (contactList.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                contactInfoPanel.setTextEmpty();


            } else {
                //Selection, enable the fire button.
                edit.setEnabled(true);
                delete.setEnabled(true);
                Contact c = (Contact) contactList.getModel().getElementAt(contactList.getSelectedIndex());
                contactInfoPanel.setContact(c);
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