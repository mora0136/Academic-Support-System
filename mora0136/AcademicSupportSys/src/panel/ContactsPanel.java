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

public class ContactsPanel extends TwoPanel implements DocumentListener, FocusListener {
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
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int) (mainFont * (0.75));
                int bodyFont = (int) (mainFont * (0.5));
                int checkBoxFont = (int) (mainFont * (0.75));
                int width = 50;
                int height = 50;

                if (windowWidth < 1100 || windowHeight < 450) {
                    width = (int) (windowHeight / (1100 / 50));
                    headerFont = (int) (Double.min(windowWidth / (1100 / headerFont), windowHeight / (450 / headerFont)));
                    listFont = (int) (Double.min(windowWidth / (1100 / listFont), windowHeight / (450 / listFont)));
                    bodyFont = (int) (Double.min(windowWidth / (1100 / bodyFont), windowHeight / (450 / bodyFont)));
                    checkBoxFont = (int) (Double.min(windowWidth / (1100 / checkBoxFont), windowHeight / (450 / checkBoxFont)));
                }
                searchField.setFont(new Font("Arial", Font.PLAIN, headerFont));
                ComProps.listProperties(contactList, headerFont);
            }
        });
    }

    public void actionPerformedNew(ActionEvent e) {
        contactSelected = 0;
        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(true);
        contactList.setEnabled(false);

        //Alter the edit button to suit the context, in this case change to save;
        setEditToSave(true);
        edit.setEnabled(true);

        //Change delete to suit the context
        delete.setText("Cancel");
        delete.setEnabled(true);

        addNew.setEnabled(false);
    }

    /**
     * A contact has been selected and the edit button has been hit. This button doubles as a save and edit buttons
     * The code in setEditToSave() shows how the icon and text change.
     * @param e
     */
    public void actionPerformedEdit(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Edit":
                contactInfoPanel.setEditable(true);
                contactList.setEnabled(false); // Do not allow for the selection og a different contact
                delete.setText("Cancel"); //Change so it suits the context. Cannot delete from within edit mode
                setEditToSave(true);
                addNew.setEnabled(false);
                break;
            case "Save":
                contactInfoPanel.setEditable(false);
                String[] newText = contactInfoPanel.getTextFields();
                if (contactSelected == 0) {
                    contactSelected = contactDB.addContact(newText[0], newText[1], newText[2], newText[3]);
                    LogDB.logNewContact(contactSelected);
                } else {
                    contactDB.updateContact(newText[0], newText[1], newText[2], newText[3], contactSelected);
                    LogDB.logSavedContact(contactSelected);

                }

                //Refresh the list of contacts as a change has occurred
                listOfContacts.removeAllElements();
                int indexOfCurrentContact = -1; //ensures that the current selection of the new list remains the same as the old one
                for (int i = 0; i < contactDB.getListModel().getSize(); i++) {
                    Contact c = (Contact) contactDB.getListModel().getElementAt(i);
                    if (c.getContact_ID() == contactSelected) {
                        indexOfCurrentContact = i;
                    }
                    listOfContacts.addElement(c);
                }
                contactList.setSelectedIndex(indexOfCurrentContact);
                contactList.setEnabled(true);

                setEditToSave(false);
                edit.setEnabled(false);
                delete.setEnabled(false);
                delete.setText("Delete");
                addNew.setEnabled(true);
                break;
        }
    }

    /**
     * Any time a contact has been selected and delete is then pressed
     * @param e
     */
    public void actionPerformedDelete(ActionEvent e) {
        if (e.getActionCommand() == "Delete") {
            contactDB.deleteContact(contactSelected);
            LogDB.logDeletedContact(contactSelected);
            listOfContacts.removeAllElements();
            for (int i = 0; i < contactDB.getListModel().getSize(); i++) {
                listOfContacts.addElement((Contact) contactDB.getListModel().getElementAt(i));
            }
            contactInfoPanel.setTextEmpty();
        }
        contactInfoPanel.setEditable(false);
        edit.setEnabled(false);
        setEditToSave(false);
        delete.setEnabled(false);
        delete.setText("Delete");
        contactList.setEnabled(true);
        addNew.setEnabled(true);
    }

    /**
     * Any time the selection of the contact changes.
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
        if (contactList.getSelectedIndex() == -1) {
            //No selection, disable fire button.
            contactInfoPanel.setTextEmpty();

        } else {
            //Contact has been selected, enable the action buttons and display contact
            edit.setEnabled(true);
            delete.setEnabled(true);
            Contact c = (Contact) contactList.getModel().getElementAt(contactList.getSelectedIndex());
            contactInfoPanel.setContact(c);
            contactSelected = c.getContact_ID();
        }
    }

    //Any text entry into JTextField will search for the contact desired.
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
    }

    private void search() {
        //Stops the potential for a user to select contact, search, then edit the previously selected contact despite it not being visible.
        contactSelected = 0;
        edit.setEnabled(false);
        delete.setEnabled(false);

        contactDB.searchForContact(searchField.getText());
        listOfContacts.removeAllElements();
        for (int i = 0; i < contactDB.getListModel().getSize(); i++) {
            listOfContacts.addElement((Contact) contactDB.getListModel().getElementAt(i));
        }

        //When no contacts are found, the model will contain a dummy contact with ID -1, in which case disable selection
        if(listOfContacts.getElementAt(0).getContact_ID() == -1){
            contactList.setEnabled(false);
        }else{
            contactList.setEnabled(true);
        }
    }

    /**
     * Remove the Search... and set the text being added to normal
     * @param e
     */
    public void focusGained(FocusEvent e) {
        if(searchField.getText().equals("Search...")) {
            searchField.setText("");
            searchField.setForeground(Color.BLACK);
        }
    }

    /**
     * Allow for the display of the "Search..." text on the text area while not in focus and empty
     * @param e
     */
    public void focusLost(FocusEvent e) {
        if (searchField.getText().isEmpty()) {
            searchField.getDocument().removeDocumentListener(this);
            searchField.setText("Search...");
            searchField.setForeground(Color.GRAY);
            searchField.getDocument().addDocumentListener(this);
        }
    }

    /**
     * Change the edit button to a save button
     * @param isSave true if the button is to shown as save
     */
    private void setEditToSave(boolean isSave) {
        if(isSave){
            edit.setText("Save");
            editImg = saveImg;
            edit.setIcon(new ImageIcon(editImg));
        }else{
            edit.setText("Edit");
            editImg = tempImg;
            edit.setIcon(new ImageIcon(editImg));
        }
    }
}