package panel;

import contacts.Contact;
import contacts.ContactDB;
import contacts.ContactDisplayPanel;
import log.LogDB;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ContactsPanel extends TwoPanel implements DocumentListener, FocusListener{
    JScrollPane scrollContactPanel;
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

        contactInfoPanel = new ContactDisplayPanel();

        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);

        rightPanel.add(contactInfoPanel, BorderLayout.NORTH);

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

                if (windowWidth < 1100 || windowHeight < 450) {
                    width = (int) (windowHeight / (1100/50));
                    headerFont = (int)(Double.min(windowWidth /(1100/headerFont), windowHeight/(450/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(1100/listFont), windowHeight/(450/listFont)));
                    bodyFont = (int)(Double.min(windowWidth/(1100/bodyFont), windowHeight/(450/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(1100/checkBoxFont), windowHeight/(450/checkBoxFont)));
                }
                searchField.setFont(new Font("Arial", Font.PLAIN, headerFont));
                ComProps.listProperties(contactList, headerFont);
            }
        });
    }

    public void actionPerformedNew(ActionEvent e){
        contactSelected = 0;
        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(true);
        contactList.setEnabled(false);
        edit.setEnabled(true);
        edit.setText("Save");
        editImg = saveImg;
        edit.setIcon(new ImageIcon(editImg));
        delete.setText("Cancel");
        delete.setEnabled(true);
        addNew.setEnabled(false);
    }

    public void actionPerformedEdit(ActionEvent e){
        switch(e.getActionCommand()) {
            case "Edit":
                contactInfoPanel.setEditable(true);
                contactList.setEnabled(false);
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
                contactList.setEnabled(true);
                edit.setEnabled(false);
                delete.setEnabled(false);
                addNew.setEnabled(true);
                break;
        }
        delete.setText("Delete");
    }



    public void actionPerformedDelete(ActionEvent e){
        if(e.getActionCommand() == "Delete"){
            contactDB.deleteContact(contactSelected);
            LogDB.logDeletedContact(contactSelected);
            listOfContacts.removeAllElements();
            for (int i = 0; i < contactDB.getListModel().getSize(); i++) {
                listOfContacts.addElement((Contact) contactDB.getListModel().getElementAt(i));
            }
        }

        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(false);
        edit.setEnabled(false);
        edit.setText("Edit");
        delete.setEnabled(false);
        delete.setText("Delete");
        contactList.setEnabled(true);
        addNew.setEnabled(true);
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


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(searchImg != null) g.drawImage(searchImg, 0,0,this.getWidth(),this.getHeight(),this);
    }

}