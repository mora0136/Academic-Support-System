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
import java.io.IOException;
import java.util.ArrayList;

public class EditPanel extends TwoPanel implements DocumentListener, FocusListener {
    JScrollPane scrollContactPanel;
    JList editList;
    DefaultListModel<Upload> editable;
    UploadVerticalOrientation displayUpload;
    SuffixTrie sf;

    EditPanel(JPanel pane) throws IOException {
        super(pane);

        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        //Get some type of list from upload where isUploaded is false
        editable = UploadDB.getAllEditableUploads();
        sf = new SuffixTrie();
        for(int i = 0; i < editable.getSize(); i++){
            sf.insert(editable.getElementAt(i).toString(), editable.getElementAt(i));
        }
        editList = new JList(editable);
        editList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(editList);

        //Output area when a contact is selected or a new one is to added
        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
        displayUpload = new UploadVerticalOrientation();
        JScrollPane displayScroll = new JScrollPane(displayUpload);
        rightPanel.add(displayScroll, BorderLayout.CENTER);

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
                    searchField.setFont(new Font("Arial", Font.PLAIN, headerFont));
                    ComProps.listProperties(editList, headerFont);
            }
        });
    }

    public void actionPerformedEdit(ActionEvent e){
        UploadPanel toEdit = new UploadPanel(cardPane);
        toEdit.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
        toEdit.backBtn.setText("Cancel");
        cardPane.add(toEdit, "Editting");
        cardLayout.show(cardPane, "Editting");
    }

    //Consider moving/marking info rather then deleting?
    public void actionPerformedDelete(ActionEvent e){
        UploadDB.deleteUpload((Upload) editList.getSelectedValue());

        LogDB.logDeletedUpload(((Upload) editList.getSelectedValue()).getUploadID());

        editable.removeElementAt(editList.getSelectedIndex());
        editList.setSelectedIndex(-1);
    }

    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting() == false) {
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
    public void insertUpdate(DocumentEvent e) { search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) { search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    private void search(){
        //Search for the Title
        if(searchField.getText().length() == 0 || searchField.equals("Search...")) {
            editable = UploadDB.getAllEditableUploads();
            editList.setModel(editable);
        }else{
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
                JLabel noContent = new JLabel("Contact not Found");
                noContent.setFont(new Font("Arial", Font.PLAIN, 16));
            }
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