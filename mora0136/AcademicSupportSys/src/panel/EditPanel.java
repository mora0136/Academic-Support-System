package panel;

import log.LogDB;
import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;
import upload.Upload;
import upload.UploadDB;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
 * Any existing/not uploaded uploads are shown within this Panel. It inherits from TwoPanel for a common design language
 * and displays a view of the selected upload on the right of the screen. As with ContactPanel, a suffix trie is used to
 * allow for efficient searching of the edit List
 */

public class EditPanel extends TwoPanel implements DocumentListener, FocusListener {
    JScrollPane scrollContactPanel;
    JList<Upload> editList;
    DefaultListModel<Upload> editable;
    UploadVerticalOrientation displayUpload;
    SuffixTrie sf;

    EditPanel(JPanel pane) {
        super(pane);

        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);
        searchField.setToolTipText("Search for a Saved Upload");

        //Get some type of list from upload where isUploaded is false
        editable = new DefaultListModel<>();
        editList = new JList<>(editable);
        editList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(editList);

        //Output area when a contact is selected or a new one is to added
        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
        displayUpload = new UploadVerticalOrientation();
        JScrollPane displayScroll = new JScrollPane(displayUpload);
        rightPanel.add(displayScroll, BorderLayout.CENTER);

        // Details what styles should apply to buttons at the certain size of a window
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                resetAll();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;

                if (windowWidth < 1300 || windowHeight < 600) {
                    headerFont = (int) (Integer.min(windowWidth / (1300 / headerFont), windowHeight / (600 / headerFont)));
                }
                searchField.setFont(new Font("Arial", Font.PLAIN, headerFont));
                ComProps.listProperties(editList, headerFont);
            }
        });
    }

    @Override
    protected void resetAll() {
        displayUpload.resetAll();
        editList.clearSelection();
        DefaultListModel l = (DefaultListModel) editList.getModel();
        l.removeAllElements();
        populateListToEdit();
        edit.setEnabled(false);
        delete.setEnabled(false);
    }

    public void actionPerformedNew(ActionEvent e) {
        resetAll();
        cardLayout.show(cardPane, "Upload");
    }

    public void actionPerformedEdit(ActionEvent e) {
        UploadPanel toEdit = new UploadPanel(cardPane);
        toEdit.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
        toEdit.backBtn.setText("Cancel");
        toEdit.backBtn.setMnemonic('c');
        cardPane.add(toEdit, "Editing");
        cardLayout.show(cardPane, "Editing");
    }

    public void actionPerformedDelete(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this Upload?", "Are you sure?", JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.NO_OPTION) {
            return;
        }
        UploadDB.deleteUpload(editList.getSelectedValue());
        LogDB.logDeletedUpload((editList.getSelectedValue()).getUploadID());
        editable.removeElementAt(editList.getSelectedIndex());
        editList.clearSelection();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (editList.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                displayUpload.resetAll();
            } else {
                //Selection, enable the fire button.
                displayUpload.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
                edit.setEnabled(true);
                delete.setEnabled(true);
            }
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
        editList.setEnabled(true);
        //Search for the Title
        if (searchField.getText().length() == 0 || searchField.getText().equals("Search...")) {
            editable = UploadDB.getAllEditableUploads();
            editList.setModel(editable);
        } else {
            ArrayList<SuffixIndex> startIndexes;
            editable.removeAllElements();
            SuffixTrieNode sn = sf.get(searchField.getText());
            if (sn != null) {
                startIndexes = sn.getData().getStartIndexes();
                for (SuffixIndex s : startIndexes) {
                    //Prevents duplicate contacts being displayed as a node may contain many subStrings of itself
                    if (!editable.contains(s.getObj())) {
                        editable.addElement((Upload) s.getObj());
                    }
                }

            } else {
                //This is where an error message should be returned as nothing found.
                editable.addElement(new Upload(-1, "No Editable Uploads Found"));
                editList.setEnabled(false);
            }
        }
    }

    //Allow for the display of the "Search..." text on the text area while not in focus
    public void focusGained(FocusEvent e) {
        searchField.setText("");
        searchField.setForeground(Color.BLACK);
    }

    public void focusLost(FocusEvent e) {
        if (searchField.getText().isEmpty()) {
            searchField.getDocument().removeDocumentListener(this);
            searchField.setText("Search...");
            searchField.setForeground(Color.GRAY);
            searchField.getDocument().addDocumentListener(this);
        }
    }

    private void populateListToEdit() {
        DefaultListModel<Upload> temp = UploadDB.getAllEditableUploads();
        sf = new SuffixTrie();
        for (int i = 0; i < temp.size(); i++) {
            editable.addElement(temp.getElementAt(i));
            sf.insert(editable.getElementAt(i).toString(), editable.getElementAt(i));
        }
    }
}