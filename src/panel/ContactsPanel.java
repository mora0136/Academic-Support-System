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
 * the repeating of a common design language, more information can be found in twoPanel. It allows for contacts to be
 * added, edited, saved and deleted. A suffix Trie is implemented to assist user's in finding their desired contact.
 * The searching of which is instantaneous due to document Listeners.
 */

public class ContactsPanel extends TwoPanel implements DocumentListener, FocusListener {
    JScrollPane scrollContactPanel;
    JList<Contact> contactList;
    int contactSelected = 0;
    DefaultListModel<Contact> listOfContacts;
    ContactDisplayPanel contactInfoPanel;

    ContactsPanel(JPanel pane) {
        super(pane);
        contactDB = new ContactDB();
        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);
        searchField.setToolTipText("Search for a Contacts Name");

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
        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(false);
        contactList.clearSelection();
        contactList.setEnabled(true);
        setSaveToEdit();
        edit.setEnabled(false);
        setCancelToDelete();
        delete.setEnabled(false);
        addNew.setEnabled(true);
    }

    public void actionPerformedNew(ActionEvent e) {
        contactSelected = 0;
        contactInfoPanel.setTextEmpty();
        contactInfoPanel.setEditable(true);
        contactList.setEnabled(false);

        //Alter the edit button to suit the context, in this case change to save;
        setEditToSave();
        edit.setEnabled(true);

        //Change delete to suit the context
        setDeleteToCancel();
        delete.setEnabled(true);

        addNew.setEnabled(false);
    }

    /**
     * A contact has been selected and the edit button has been hit. This button doubles as a save and edit button
     * @param e
     */
    public void actionPerformedEdit(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Edit":
                contactInfoPanel.setEditable(true);
                contactList.setEnabled(false); // Do not allow for the selection of a different contact
                setDeleteToCancel();
                setEditToSave();
                addNew.setEnabled(false);
                break;
            case "Save":
                //Error checking on Email and Phone to ensure correct format before entering into database
                String[] details = contactInfoPanel.getTextFields();
                if (details[0].isBlank() || details[1].isBlank()) {
                    JOptionPane.showMessageDialog(this, "No name was given, please enter a name.");
                } else if (!contactInfoPanel.isValidEmail()) {
                    JOptionPane.showMessageDialog(this, "the email \"" + details[2] + "\" entered was not valid.");
                } else if (!contactInfoPanel.isValidPhone()) {
                    JOptionPane.showMessageDialog(this, "The phone entered was not valid");
                } else {
                    contactInfoPanel.setEditable(false);
                    if (contactSelected == 0) {
                        contactSelected = contactDB.addContact(details[0], details[1], details[2], details[3]);
                        LogDB.logAddedContact(contactSelected);
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

                    setSaveToEdit();
                    setCancelToDelete();

                    edit.setEnabled(false);
                    delete.setEnabled(false);
                    addNew.setEnabled(true);
                    break;
                }
        }
    }

    /**
     * Any time a contact has been selected and delete is then pressed.
     * This button doubles as a cancel and a delete button.
     * @param e
     */
    public void actionPerformedDelete(ActionEvent e) {
        if (e.getActionCommand().equals("Delete")) {
            int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this contact?", "Are you sure?", JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.NO_OPTION) {
                return;
            }
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

        } else if (e.getActionCommand().equals("Cancel")) { // reset contact fields to what it was previously before any changes were made.
            try {
                contactInfoPanel.setContact(contactList.getModel().getElementAt(contactList.getSelectedIndex()));
            } catch (ArrayIndexOutOfBoundsException a) {
                //There was no previously selected contact, so set the fields and buttons appropriately
                contactInfoPanel.setTextEmpty();
                delete.setEnabled(false);
                edit.setEnabled(false);
            }
        }

        contactInfoPanel.setEditable(false);
        setCancelToDelete();
        setSaveToEdit();
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

    //Any text entry into searchField will search for the contact desired.
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
     * Remove the Search... and set the text being added to normal properties
     * @param e
     */
    public void focusGained(FocusEvent e) {
        if (searchField.getText().equals("Search...")) {
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
            //Need to remove the document listener since it acts as if a search is being initiated and screws with the list results
            searchField.getDocument().removeDocumentListener(this);
            searchField.setText("Search...");
            searchField.setForeground(Color.GRAY);
            searchField.getDocument().addDocumentListener(this);
        }
    }


    /**
     * Change the edit button to a save button
     */
    private void setEditToSave() {
        edit.setText("Save");
        editImg = saveImg;
        edit.setMnemonic('s');
        int font = Integer.min(Integer.min(getWidth() / (1300 / 32), getHeight() / (600 / 32)), 32);
        ComProps.buttonProperties(edit, editImg, (int) (getHeight() * 0.0625), 50, font, true);
    }

    /**
     * Change the save button to an edit button
     */
    private void setSaveToEdit() {
        edit.setText("Edit");
        editImg = tempImg;
        edit.setMnemonic('e');
        int font = Integer.min(Integer.min(getWidth() / (1300 / 32), getHeight() / (600 / 32)), 32);
        ComProps.buttonProperties(edit, editImg, (int) (getHeight() * 0.0625), 50, font, true);
    }

    /**
     * Change the delete button to a cancel button
     */
    private void setDeleteToCancel() {
        delete.setText("Cancel");
        delete.setMnemonic('c');
    }

    /**
     * Change the cancel button to a delete button
     */
    private void setCancelToDelete() {
        delete.setText("Delete");
        delete.setMnemonic('d');
    }
}