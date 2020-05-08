package panel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;

public class EditPanel extends TwoPanel implements DocumentListener, FocusListener {
    JScrollPane scrollContactPanel;
    JList editList;
    DefaultListModel<Upload> editable;
    UploadVerticalOrientation display;

    EditPanel(JPanel pane) throws IOException {
        super(pane);

        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        //Get some type of list from upload where isUploaded is false
        String sql = "SELECT * FROM uploads WHERE isUploaded = false AND isDeleted = false";
        editable = new DefaultListModel<>();
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();) {

            //Retrieve the information found in the Uploads tables
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                editable.addElement(new Upload(rs.getInt("upload_ID"), rs.getString("Title")));
            }
        }catch(SQLException ev){

        }
        editList = new JList(editable);
        editList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(editList);

        //Output area when a contact is selected or a new one is to added
        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
        displayPanel.setLayout(new GridLayout(1,1));
        display = new UploadVerticalOrientation(cardPane);
        displayPanel.add(display);
        JScrollPane displayScroll = new JScrollPane(displayPanel);
        rightPanel.add(displayScroll, BorderLayout.CENTER);

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
                    if(windowWidth < 500) {
                        listProperties(editList, 16);
                    }else{
                        listProperties(editList, 24);
                    }

                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));

                }else{
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    listProperties(editList, 32);
                }
            }
        });
    }

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:data/database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void actionPerformedEdit(ActionEvent e){
        UploadPanel toEdit = null;
        try{
            toEdit = new UploadPanel(cardPane);
        } catch(IOException e1){
            System.out.println(e1);
        }
        toEdit.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
        cardPane.add(toEdit, "Editting");
        cardLayout.show(cardPane, "Editting");
    }

    //Consider moving/marking info rather then deleting?
    public void actionPerformedDelete(ActionEvent e){
        String sqlUploads = "UPDATE uploads SET isDeleted = ? WHERE upload_ID = ?";
//        String sqlAuthors = "DELETE FROM upload_Authors WHERE upload_ID = ?";
//        String sqlFiles = "DELETE FROM upload_Files WHERE upload_ID = ?";

        int uploadID = editable.getElementAt(editList.getSelectedIndex()).getUploadID();

        try (Connection conn = this.connect()) {
            PreparedStatement pstmtUpload = conn.prepareStatement(sqlUploads);
            pstmtUpload.setBoolean(1, true);
            pstmtUpload.setInt(2, uploadID);
            pstmtUpload.executeUpdate();
//            PreparedStatement pstmtAuthor = conn.prepareStatement(sqlAuthors);
//            pstmtAuthor.setInt(1, uploadID);
//            pstmtAuthor.executeUpdate();
//            PreparedStatement pstmtFiles = conn.prepareStatement(sqlFiles);
//            pstmtFiles.setInt(1, uploadID);
//            pstmtFiles.executeUpdate();

        } catch (SQLException ev) {
            System.out.println(ev.getMessage());
        }

        LogDB.logDeletedUpload(uploadID);

        editable.removeElementAt(editList.getSelectedIndex());
        editList.setSelectedIndex(-1);
    }

    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting() == false) {
            if (editList.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                display.resetAll();
            } else {
                //Selection, enable the fire button.
                display.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
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