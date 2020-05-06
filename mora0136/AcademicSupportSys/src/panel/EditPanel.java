package panel;

import contacts.ContactDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class EditPanel extends JPanel implements DocumentListener, FocusListener {
    JPanel cardPane, leftPanel, rightPanel, contextPanel, displayPanel, optionPanel;
    Image backImg, searchImg, editImg, deleteImg;
    JScrollPane scrollContactPanel;
    JTextField searchField;
    ContactDB contactDB;
    CardLayout cardLayout;
    JButton edit, delete;
    JList editList;
    DefaultListModel<Upload> editable;
    UploadEditPanel display;

    EditPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        leftPanel = new JPanel();
        rightPanel = new JPanel();
        contextPanel = new JPanel();
        displayPanel = new JPanel();
        optionPanel = new JPanel();
        contactDB = new ContactDB();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //left and right panels to be added in
        setLayout(new GridLayout(1, 2));

        JButton back = new JButton("Back");
        back.addActionListener(this::actionPerformed);
        backImg = ImageIO.read(new File("resources/back.png"));

        searchImg = ImageIO.read(new File("resources/search.png"));

        searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);
        searchField.addActionListener(this::actionPerformed);
        searchField.getDocument().addDocumentListener(this);
        searchField.addFocusListener(this);

        edit = new JButton("Edit");
        edit.setEnabled(false);
        edit.addActionListener(this::actionPerformed);
        editImg = ImageIO.read(new File("resources/edit_contact.png"));

        delete = new JButton("Delete");
        delete.setEnabled(false);
        delete.addActionListener(this::actionPerformed);
        deleteImg = ImageIO.read(new File("resources/delete.png"));

        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        contextPanel.setLayout(gridBag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridBag.setConstraints(back, c);
        contextPanel.add(back);
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 25;
        gridBag.setConstraints(searchField, c);
        contextPanel.add(searchField);

        //Get some type of list from upload where isUploaded is false
        String sql = "SELECT * FROM uploads WHERE isUploaded = false";
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
//        listProperties(contactList, 32);
        editList.addListSelectionListener(this::valueChanged);
        scrollContactPanel = new JScrollPane(editList);

        //Output area when a contact is selected or a new one is to added

        optionPanel.setLayout(new GridLayout(1, 2));
        optionPanel.add(edit); optionPanel.add(delete);

        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
        displayPanel.setLayout(new GridLayout(1,1));
        display = new UploadEditPanel(cardPane);
        displayPanel.add(display);
        JScrollPane displayScroll = new JScrollPane(displayPanel);
        rightPanel.add(displayScroll, BorderLayout.CENTER);
        rightPanel.add(optionPanel, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);

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
                    buttonProperties(back, backImg, width, windowHeight, font, false);

                    //Since addNew, edit and delete buttons have an image to the left of text, the font can be displayed longer
                    if(windowWidth < 500) {
                        buttonProperties(edit, editImg, width, windowHeight, 0, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 0, true);
                        listProperties(editList, 16);
                    }else{
                        buttonProperties(edit, editImg, width, windowHeight, 16, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 16, true);
                        listProperties(editList, 24);
                    }

                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));

                }else{
                    buttonProperties(back, backImg, 50, 50, font, false);
                    buttonProperties(edit, editImg, 50, 50, font, true);
                    buttonProperties(delete, deleteImg, 50, 50, font, true);
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

    private void listProperties(JList list, int fontSize){
        list.setFont(new Font("Arial", Font.PLAIN, fontSize));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Details the standard design layout of a button inside this display
     * @param btn The button to be edited
     * @param img The associated image with the button
     * @param width The desired width of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param height The desired height of the image(Image aspect ratio is retained so smallest of width and height is used)
     * @param fontSize The font size desired on the button(0 disabled the text from being viewable)
     * @param IconLeft Is true if the desired position of the text is to the right of the image
     */
    private void buttonProperties(JButton btn, Image img, int width, int height, int fontSize, boolean IconLeft){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        if(!IconLeft){
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
        }

        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setFocusPainted(false);
    }

    /**
     * Details the actions required for a specific button on this panel
     * @param e The Action Event call
     */
    public void actionPerformed(ActionEvent e){
        System.out.println("Action performed");
        switch(e.getActionCommand()) {
            case "Back":
                cardLayout.show(cardPane, "Home");
                break;
            case "Edit":
                //Send to uploadPanel with setTo(uploadID)
                UploadPanel toEdit = null;
                try{
                    toEdit = new UploadPanel(cardPane);
                } catch(IOException e1){
                    System.out.println(e1);
                }
                toEdit.setToExistingUpload(editable.getElementAt(editList.getSelectedIndex()).getUploadID());
                cardPane.add(toEdit, "Editting");
                cardLayout.show(cardPane, "Editting");
                break;
            case "Delete":
                //Delete from database
                String sqlUploads = "DELETE FROM uploads WHERE upload_ID = ?";
                String sqlAuthors = "DELETE FROM upload_Authors WHERE upload_ID = ?";
                String sqlFiles = "DELETE FROM upload_Files WHERE upload_ID = ?";

                try (Connection conn = this.connect()) {
                    PreparedStatement pstmtUpload = conn.prepareStatement(sqlUploads);
                    pstmtUpload.setInt(1, editable.getElementAt(editList.getSelectedIndex()).getUploadID());
                    pstmtUpload.executeUpdate();
                    PreparedStatement pstmtAuthor = conn.prepareStatement(sqlAuthors);
                    pstmtAuthor.setInt(1, editable.getElementAt(editList.getSelectedIndex()).getUploadID());
                    pstmtAuthor.executeUpdate();
                    PreparedStatement pstmtFiles = conn.prepareStatement(sqlFiles);
                    pstmtFiles.setInt(1, editable.getElementAt(editList.getSelectedIndex()).getUploadID());
                    pstmtFiles.executeUpdate();

                } catch (SQLException ev) {
                    System.out.println(ev.getMessage());
                }

                editable.removeElementAt(editList.getSelectedIndex());
                editList.setSelectedIndex(-1);
                break;
        }
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