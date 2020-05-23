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

/*
 * The view in which an address book of the contacts saved are shown. This panel inherits from TwoPanel, allowing for
 * the repeating of a common design language, more informatin can be found in twoPanel. It allows for contacts to be
 * added, edited, saved and deleted. A suffix Trie is implemented to assist user's in finding their desired contact.
 * The searching of which is instantaneous due to document Listeners.
 */

public class ContactsPanel extends TwoPanel implements DocumentListener, FocusListener {
    JScrollPane scrollContactPanel;
    JList<Contact> contactList;
    int contactSelected = 0;
    DefaultListModel<Contact> listOfContacts;
    ContactDisplayPanel contactInfoPanel;

    ContactsPanel(JPanel pane){
        super(pane);
        contactDB = new ContactDB();
        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        listOfContacts = contactDB.getListModel();
        contactList = new JList<>(listOfContacts);
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

                if (windowWidth < 1100 || windowHeight < 450) {
                    headerFont = (Integer.min(windowWidth / (1100 / headerFont), windowHeight / (450 / headerFont)));
                }
                searchField.setFont(new Font("Arial", Font.PLAIN, headerFont));
                ComProps.listProperties(contactList, headerFont);
            }
        });
    }

    @Override
    protected void resetAll() {
        contactList.clearSelection();
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
                //Error checking on Email and Phone to ensure correct format
                String[] details = contactInfoPanel.getTextFields();
                if(!contactInfoPanel.isValidEmail()) {
                    JOptionPane.showMessageDialog(this, "the email \""+details[2]+"\" entered was not valid.");
                }else if(!contactInfoPanel.isValidPhone()) {
                    JOptionPane.showMessageDialog(this, "The phone entered was not valid");
                }else{
                    contactInfoPanel.setEditable(false);
                    if (contactSelected == 0) {
                        contactSelected = contactDB.addContact(details[0], details[1], details[2], details[3]);
                        LogDB.logNewContact(contactSelected);
                    } else {
                        contactDB.updateContact(details[0], details[1], details[2], details[3], contactSelected);
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
    }

    /**
     * Any time a contact has been selected and delete is then pressed
     * @param e
     */
    public void actionPerformedDelete(ActionEvent e) {
        if (e.getActionCommand().equals("Delete")) {
            contactDB.deleteContact(contactSelected);
            LogDB.logDeletedContact(contactSelected);
            listOfContacts.removeAllElements();
            for (int i = 0; i < contactDB.getListModel().getSize(); i++) {
                listOfContacts.addElement((Contact) contactDB.getListModel().getElementAt(i));
            }
            //Since contact was deleted and no contact is selected, make edit and delete buttons disabled
            contactInfoPanel.setTextEmpty();
            edit.setEnabled(false);
            delete.setEnabled(false);

        }else if(e.getActionCommand().equals("Cancel")){ // reset contact fields to what it was previously before any changes were made.
            try {
                contactInfoPanel.setContact(contactList.getModel().getElementAt(contactList.getSelectedIndex()));
            }catch(ArrayIndexOutOfBoundsException a){
                contactInfoPanel.setTextEmpty();
            }
        }
        delete.setText("Delete");
        contactInfoPanel.setEditable(false);
        setEditToSave(false);
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
            Contact c = contactList.getModel().getElementAt(contactList.getSelectedIndex());
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
        contactList.setEnabled(listOfContacts.getElementAt(0).getContact_ID() != -1);
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
        }else{
            edit.setText("Edit");
            editImg = tempImg;
        }
        int font = Integer.min(Integer.min(getWidth() /(1300/32), getHeight()/(600/32)), 32);
        ComProps.buttonProperties(edit, editImg, (int)(getHeight() * 0.0625), 50, font, true);
    }
}