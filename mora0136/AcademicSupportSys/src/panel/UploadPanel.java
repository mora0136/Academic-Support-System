package panel;

import contacts.Contact;
import contacts.ContactDB;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadPanel extends JPanel implements DocumentListener, FocusListener {
    CardLayout cardLayout;
    JPanel cardPane, leftPanel, rightPanel, backResetPanel, uploadDetailsLeftPanel, contactsPanel, servicesPanel, saveUploadPanel, filePanel, typeDatePanel, fileTypeDatePanel, contactsListPanel, titlePanel, descPanel;
    JScrollPane contactListScroll, addedListScroll;
    JButton backBtn, resetBtn, fileSelectBtn, saveBtn, uploadBtn, selectAll, deselectAll;
    Image backImg, resetImg, fileImg, saveImg, uploadImg;
    JLabel titleLabel, descLabel, fileLabel, typeLabel, dateLabel, uploadLabel, authorsLabel, contactsLabel, addedLabel;
    ContactDB contactDB;
    DefaultListModel<Contact> displayedContacts, addedContacts, notAddedContacts;
    JComboBox selectTypeComboBox;
    JTextArea descriptionTextArea;
    JTextField titleField, searchField;
    JDatePanel publishDatePanel;
    JList attachedFileList, notAddedContactList, addedContactsList, templateStatement;
    JCheckBox cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit;
    JFileChooser fc;
    int uploadID = 0;

    UploadPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout) pane.getLayout();

        //The following defines the panels being used on the left Panel
        leftPanel = new JPanel();
        backResetPanel = new JPanel(); //Contains the back and reset buttons
        saveUploadPanel = new JPanel(); //Contains the upload and save buttons
        uploadDetailsLeftPanel = new JPanel(); //Contains the LowerPanel, titlePanel and descPanel
        filePanel = new JPanel(); //Contains the File label, btn and list
        typeDatePanel = new JPanel(); //Contains the Type and Date
        fileTypeDatePanel = new JPanel(); //Contains the filePanel and typeDatePanel
        titlePanel = new JPanel(); //Contains the Title label and field
        descPanel = new JPanel(); //Contains the description label, area and list

        //griBag layout and constraint that will be used to manipulate panels and components
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Define the layouts for each Panel
        setLayout(new GridLayout(1, 2)); //Defines the left right sides of the display
        leftPanel.setLayout(new BorderLayout());
        backResetPanel.setLayout(new GridLayout(1, 2));
        uploadDetailsLeftPanel.setLayout(gridBag);
        fileTypeDatePanel.setLayout(new GridLayout(1, 2));
        filePanel.setLayout(gridBag);
        typeDatePanel.setLayout(gridBag);
        titlePanel.setLayout(gridBag);
        descPanel.setLayout(gridBag);
        saveUploadPanel.setLayout(new GridLayout(1, 2));

        //Common images that need to be loaded in for buttons etc.
        backImg = ImageIO.read(new File("resources/back.png"));
        resetImg = ImageIO.read(new File("resources/reset.png"));
        saveImg = ImageIO.read(new File("resources/save.png"));
        uploadImg = ImageIO.read(new File("resources/upload.png"));

        //Define the back button
        backBtn = new JButton("Back");
        backBtn.addActionListener(this::actionPerformed);
        backResetPanel.add(backBtn);
        //Define the Reset Button
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this::actionPerformed);
        backResetPanel.add(resetBtn);

        //A generic inset that will apply until next changed
        c.insets = new Insets(0, 20, 0, 20);

        //The Title Text Section
        titleLabel = new JLabel("Title:");
        gridBag.setConstraints(titleLabel, c);
        titlePanel.add(titleLabel);
        titleField = new JTextField();
        c.weightx = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(titleField, c);
        titlePanel.add(titleField);


        //The Description Text Section
        //Defining the Label
        descLabel = new JLabel("Description:");
        c.gridheight = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 20, 0, 20);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(descLabel, c);
        descPanel.add(descLabel);
        //Defining the template statements
        String[] str = new String[]{"Hello", "testing", "wowsy"};
        templateStatement = new JList(str);
        templateStatement.setPreferredSize(new Dimension(1, 1));
        c.gridwidth = 1;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 20, 5, 5);
        gridBag.setConstraints(templateStatement, c);
        descPanel.add(templateStatement);
        //Defining the textArea
        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setPreferredSize(new Dimension(1, 1));
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 5, 20);
        gridBag.setConstraints(descriptionTextArea, c);
        descPanel.add(descriptionTextArea);

        //The File Selection section
        //Define the File Label
        fileLabel = new JLabel("File:");
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fileLabel, c);
        filePanel.add(fileLabel);
        //Define the File select btn
        fileSelectBtn = new JButton("Select A File...");
        fc = new JFileChooser();
        fileSelectBtn.addActionListener(this::fileSelectedAction);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(fileSelectBtn, c);
        filePanel.add(fileSelectBtn);
        //Define a list to display files and drag pane
        DefaultListModel<File> tempFileList = new DefaultListModel<>();
        attachedFileList = new JList(tempFileList);
        attachedFileList.setCellRenderer(new FileRenderer());
        attachedFileList.setFont(new Font("Arial", Font.PLAIN, 16));
        attachedFileList.setTransferHandler(new FileListTransferHandler(attachedFileList));
        attachedFileList.setDropMode(DropMode.INSERT);
        c.weighty = 0.9;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(attachedFileList, c);
        filePanel.add(attachedFileList);

        //The type DropDown Section(e.g Article, Book, Journal)
        //Define the type Label
        typeLabel = new JLabel("Type");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(typeLabel, c);
        typeDatePanel.add(typeLabel);
        //Define the comboBox containg the possible types
        String[] types = new String[]{"Article", "Journal", "Paper", "Book", "Research"};
        selectTypeComboBox = new JComboBox(types);
        c.weightx = 10;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(selectTypeComboBox, c);
        typeDatePanel.add(selectTypeComboBox);

        //The Date Calender Section
        //Define the Date Label
        dateLabel = new JLabel("Date:");
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(dateLabel, c);
        typeDatePanel.add(dateLabel);
        //Define the Date Panel which is an external feature found from:https://github.com/JDatePicker/JDatePicker
        publishDatePanel = new JDatePanel();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(publishDatePanel, c);
        typeDatePanel.add(publishDatePanel);

        //Add the File and type and Date to one panel, organised in a 1row*2column grid
        fileTypeDatePanel.add(filePanel);
        fileTypeDatePanel.add(typeDatePanel);

        //Add each panel with specific proportions to a panel to contain the centre of the leftPanel
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.1;
        gridBag.setConstraints(titlePanel, c);
        uploadDetailsLeftPanel.add(titlePanel);
        c.weighty = 0.5;
        gridBag.setConstraints(descPanel, c);
        uploadDetailsLeftPanel.add(descPanel);
        c.weighty = 0.4;
        gridBag.setConstraints(fileTypeDatePanel, c);
        uploadDetailsLeftPanel.add(fileTypeDatePanel);

        leftPanel.add(backResetPanel, BorderLayout.NORTH);
        leftPanel.add(uploadDetailsLeftPanel, BorderLayout.CENTER);


        //The following defines the Panels to be used on the right side
        rightPanel = new JPanel();
        contactsPanel = new JPanel(); //Contains the Contact Label and search Field
        contactsListPanel = new JPanel(); //Contains the two labels and lists containg added and not added contacts
        servicesPanel = new JPanel(); //Contains the Label, buttons and checkBoxes for what service to upload to
        JPanel contactsServicesPanel = new JPanel(); //Contains the contacts and services to take up the centre of right panel

        rightPanel.setLayout(new BorderLayout());
        contactsPanel.setLayout(gridBag);
        contactsListPanel.setLayout(gridBag);
        servicesPanel.setLayout(new GridLayout(5, 3));
        contactsServicesPanel.setLayout(new GridLayout(2, 1));

        //The Contacts Panel
        //Defining the Author Label
        authorsLabel = new JLabel("Authors:");
        c.insets = new Insets(10, 10, 0, 10);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weighty = 0.1;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridBag.setConstraints(authorsLabel, c);
        contactsPanel.add(authorsLabel);
        //Defining the searchField
        searchField = new JTextField("Search...");
        searchField.setForeground(Color.GRAY);
        searchField.addActionListener(this::actionPerformed);
        searchField.getDocument().addDocumentListener((DocumentListener) this);
        searchField.addFocusListener(this);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.9;
        gridBag.setConstraints(searchField, c);
        contactsPanel.add(searchField);

        //The contactsListPanel
        //Defining the ContactsLabel
        contactsLabel = new JLabel("Contacts");
        c.insets = new Insets(0, 5, 0, 5);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        gridBag.setConstraints(contactsLabel, c);
        contactsListPanel.add(contactsLabel);
        //Defining the added Label
        addedLabel = new JLabel("Added");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.CENTER;
        gridBag.setConstraints(addedLabel, c);
        contactsListPanel.add(addedLabel);
        //The Contact Selector lists
        //instantiate each relevant object
        contactDB = new ContactDB();
        displayedContacts = contactDB.getListModel(); //The displayed list which changes according to the search results
        addedContacts = new DefaultListModel<>(); //The contacts that have been added to the upload
        notAddedContacts = new DefaultListModel<>(); //Stores a list in the background of the full list of contacts that could be added, this can be used to search for
        for(int i = 0; i< displayedContacts.getSize(); i++){ // simply copying the values to the other
            notAddedContacts.addElement(displayedContacts.getElementAt(i));
        }
        //Defining the not added contact List and adding a Scroll Pane
        notAddedContactList = new JList(displayedContacts);
        notAddedContactList.addListSelectionListener(this::valueChangedToAdd);
        contactListScroll = new JScrollPane(notAddedContactList);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(contactListScroll, c);
        contactsListPanel.add(contactListScroll);
        //Defining the added Contact List and adding a scroll pane
        addedContactsList = new JList(addedContacts);
        addedContactsList.addListSelectionListener(this::valueChangedAdded);
        addedListScroll = new JScrollPane(addedContactsList);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(addedListScroll, c);
        contactsListPanel.add(addedListScroll);

        gridBag.setConstraints(contactsListPanel, c);
        contactsPanel.add(contactsListPanel);

        //The components that are contained in servicesPanel
        uploadLabel = new JLabel("Upload to...");
        servicesPanel.add(uploadLabel);
        selectAll = new JButton("Select All");
        servicesPanel.add(selectAll);
        deselectAll = new JButton("Deselect All");
        servicesPanel.add(deselectAll);
        //The CheckBoxes that are added to the services Panel
        cv = new JCheckBox("Personal C.V.");
        resGate = new JCheckBox("Research Gate");
        orcid = new JCheckBox("ORCID");
        inst = new JCheckBox("Institue Page");
        publ = new JCheckBox("Publons");
        wos = new JCheckBox("World of Research");
        gSch = new JCheckBox("Google Scholar");
        linIn = new JCheckBox("LinkedIn");
        scopus = new JCheckBox("Scopus");
        pure = new JCheckBox("Pure");
        acad = new JCheckBox("Academia");
        twit = new JCheckBox("Twitter");
        servicesPanel.add(cv);servicesPanel.add(resGate);servicesPanel.add(orcid);
        servicesPanel.add(inst);servicesPanel.add(publ);servicesPanel.add(wos);
        servicesPanel.add(gSch);servicesPanel.add(linIn);servicesPanel.add(scopus);
        servicesPanel.add(pure);servicesPanel.add(acad);servicesPanel.add(twit);

        //Adding to the contactsServicesPanel
        contactsServicesPanel.add(contactsPanel);
        contactsServicesPanel.add(servicesPanel);

        //The saveUploadPanel
        //Defining Save btn
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(this::actionPerformed);
        saveUploadPanel.add(saveBtn);
        //Defining Upload btn
        uploadBtn = new JButton("Upload");
        uploadBtn.addActionListener(this::actionPerformed);
        saveUploadPanel.add(uploadBtn);

        //Adding to the rightPanel
        rightPanel.add(contactsServicesPanel, BorderLayout.CENTER);
        rightPanel.add(saveUploadPanel, BorderLayout.SOUTH);

        //Adding to the window
        add(leftPanel);
        add(rightPanel);

        //The following defines what should happen to a component when the window is resized.
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = 32;
                int listFont = 24;
                int bodyFont = 16;
                int checkBoxFont = 24;
                int width = 50;
                int height = 50;

                if (windowWidth < 1300 || windowHeight < 600) {
                    width = (int) (windowHeight * 0.0625);
                    headerFont = (int)(Double.min(windowWidth /(1300/headerFont), windowHeight/(600/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(1300/listFont), windowHeight/(600/listFont)));
                    bodyFont = (int)(Double.min(windowWidth/(1300/bodyFont), windowHeight/(600/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(1300/checkBoxFont), windowHeight/(600/checkBoxFont)));
                }

                buttonProperties(backBtn, backImg, width, height, headerFont);
                buttonProperties(resetBtn, resetImg, width, height, headerFont);
                buttonProperties(saveBtn, saveImg, width, height, headerFont);
                buttonProperties(uploadBtn, uploadImg, width, height, headerFont);

                listProperties(attachedFileList, listFont);
                listProperties(notAddedContactList, listFont);
                listProperties(addedContactsList, listFont);
                listProperties(templateStatement, bodyFont);

                headingProperties(titleLabel, headerFont);
                headingProperties(descLabel, headerFont);
                headingProperties(fileLabel, headerFont);
                headingProperties(typeLabel, headerFont);
                headingProperties(dateLabel, headerFont);
                headingProperties(uploadLabel, headerFont);
                headingProperties(authorsLabel, headerFont);
                headingProperties(contactsLabel, headerFont-4);
                headingProperties(addedLabel, headerFont-4);

                checkBoxProperties(cv, checkBoxFont);
                checkBoxProperties(resGate, checkBoxFont);
                checkBoxProperties(orcid, checkBoxFont);
                checkBoxProperties(inst, checkBoxFont);
                checkBoxProperties(publ, checkBoxFont);
                checkBoxProperties(wos, checkBoxFont);
                checkBoxProperties(gSch, checkBoxFont);
                checkBoxProperties(linIn, checkBoxFont);
                checkBoxProperties(scopus, checkBoxFont);
                checkBoxProperties(pure, checkBoxFont);
                checkBoxProperties(acad, checkBoxFont);
                checkBoxProperties(twit, checkBoxFont);

                textFieldProperties(titleField, headerFont);
                textAreaProperties(descriptionTextArea, bodyFont);
                textFieldProperties(searchField, headerFont);

                fileSelectBtn.setFont(new Font("Arial", Font.PLAIN, bodyFont));
                selectAll.setFont(new Font("Arial", Font.PLAIN, headerFont));
                deselectAll.setFont(new Font("Arial", Font.PLAIN, headerFont));
            }
        });
    }


    private void headingProperties(JLabel label, int fontSize){
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
    }

    private void textFieldProperties(JTextField field, int fontSize){
        field.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    private void textAreaProperties(JTextArea area, int fontSize){
        area.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }


    private void listProperties(JList list, int fontSize){
        list.setFont(new Font("Arial", Font.PLAIN, fontSize));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void buttonProperties(JButton btn, Image img, int width, int height, int fontSize) {
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        btn.setMargin(new Insets(0, (int) (width * 0.25), 0, (int) (width * 0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setFocusPainted(false);
    }

    private void checkBoxProperties(JCheckBox box, int fontSize){
        box.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    /**
     * Sets all components in the window to their default values
     */
    private void resetAll(){
        selectTypeComboBox.setSelectedIndex(0);
        descriptionTextArea.setText("");
        titleField.setText("");
        searchField.setText("");
        publishDatePanel.getModel().setValue(null); //maybe?
        attachedFileList.setModel(new DefaultListModel());
        displayedContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            displayedContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
        }
        addedContacts.removeAllElements();
        cv.setSelected(false);resGate.setSelected(false);orcid.setSelected(false);
        inst.setSelected(false);publ.setSelected(false);wos.setSelected(false);
        gSch.setSelected(false);linIn.setSelected(false);scopus.setSelected(false);
        pure.setSelected(false);acad.setSelected(false);twit.setSelected(false);
    }

    /**
     * Sets all the components in the window to a state as ssaved in the database for the corresponding uplaod_ID
     * @param uploadID The uploadID of the upload to be viewed/set
     */
    private void setToExistingUpload(int uploadID){
        this.uploadID = uploadID;
        String sql = "SELECT * FROM uploads " +
                     "WHERE upload_ID = "+ uploadID;

        String sqlSelectAuthors = "SELECT * FROM upload_Authors " +
                                  "JOIN contacts ON upload_Authors.contact_ID = contacts.contact_ID " +
                                  "WHERE upload_ID = "+ uploadID;

        String sqlSelectFiles = "SELECT * FROM upload_Files " +
                                "WHERE upload_ID = "+ uploadID;

        //Try to make a connection to the database
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();){

            //Retrieve the information found in the Uploads tables
            ResultSet rs = stmt.executeQuery(sql);
            selectTypeComboBox.setSelectedItem(rs.getString("Type"));
            descriptionTextArea.setText(rs.getString("Description"));
            titleField.setText(rs.getString("Title"));
            searchField.setText("");

            //Setting the date, since java.util.Date and java.sql.Date is finicky, just utilised it as a string
            String sqlDate = rs.getString("Date");
            java.util.Date date = new SimpleDateFormat("yyyy-mm-dd").parse(sqlDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            DateModel<Calendar> dateModel = (DateModel<Calendar>) publishDatePanel.getModel();
            dateModel.setValue(calendar);

            //Setting the services
            cv.setSelected(rs.getBoolean("cv"));resGate.setSelected(rs.getBoolean("resGate"));orcid.setSelected(rs.getBoolean("orcid"));
            inst.setSelected(rs.getBoolean("inst"));publ.setSelected(rs.getBoolean("publ"));wos.setSelected(rs.getBoolean("wos"));
            gSch.setSelected(rs.getBoolean("gSch"));linIn.setSelected(rs.getBoolean("linIn"));scopus.setSelected(rs.getBoolean("scopus"));
            pure.setSelected(rs.getBoolean("pure"));acad.setSelected(rs.getBoolean("acad"));twit.setSelected(rs.getBoolean("twit"));

            //Retrieve the information found in the upload_Authors table
            rs = stmt.executeQuery(sqlSelectAuthors);
            while(rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                        rs.getString("surname"), rs.getString("email"),
                        rs.getString("phone"));
                addedContacts.addElement(c);
                displayedContacts.removeElement(c);
            }
            //Retrieve the information found in the upload_Files table.
            DefaultListModel news = (DefaultListModel) attachedFileList.getModel();
            rs = stmt.executeQuery(sqlSelectFiles);
            while(rs.next()) {
                news.addElement(new File(rs.getString("File")));
            }

        }catch(SQLException | ParseException e){

        }
    }


    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backBtn){
            cardLayout.show(cardPane, "Home");
        }else if(e.getSource() == resetBtn){
            resetAll();
        }else if(e.getSource() == uploadBtn || e.getSource() == saveBtn){
            /*
            Upload and save essentially have the same information they need to convey, the only difference being that by
            uploading, the isUpload field will be set to true, and can no longer be selected to edit. However there are
            different SQL statements required if the current upload is new or an existing one. This is identified in the
            first if statement.
             */
            String sqlUpload, sqlAuthors, sqlFiles;
            if(uploadID != 0) { //If an uploadID is specified, then it already exists in db and thus only need to update
                sqlUpload = "UPDATE uploads SET Title = ?,Description = ?, type = ?, Date = ?, cv = ?, resGate = ?, orcid = ?, inst = ?, publ = ?, wos = ?, gSch = ?, linIn = ?, scopus = ?, pure = ?, acad = ?, twit = ?, isUploaded = ? WHERE upload_ID = ?";
                sqlAuthors = "UPDATE upload_Authors SET contact_ID = ?, nameUsed = ? WHERE upload_ID = ?";
                sqlFiles = "UPDATE upload_Files SET File = ? WHERE upload_ID = ?";

            }else {//A fresh insertion, creating new uploadID that will be used to associate with authors and files
                sqlUpload = "INSERT INTO uploads(Title,Description, type, Date, cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit, isUploaded) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                sqlAuthors = "INSERT INTO upload_Authors(contact_ID, nameUsed, upload_ID) VALUES(?,?,?)";
                sqlFiles = "INSERT INTO upload_Files(File, upload_ID) VALUES(?,?)";
            }
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sqlUpload, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, titleField.getText());
                    pstmt.setString(2, descriptionTextArea.getText());
                    pstmt.setString(3, String.valueOf(selectTypeComboBox.getSelectedItem()));
                    DateModel date = publishDatePanel.getModel();
                    pstmt.setString(4, date.getYear()+"-"+ date.getMonth() +"-"+ date.getDay());
                    pstmt.setBoolean(5, cv.isSelected());pstmt.setBoolean(6, resGate.isSelected());pstmt.setBoolean(7, orcid.isSelected());
                    pstmt.setBoolean(8, inst.isSelected());pstmt.setBoolean(9, publ.isSelected());pstmt.setBoolean(10, wos.isSelected());
                    pstmt.setBoolean(11, gSch.isSelected());pstmt.setBoolean(12, linIn.isSelected());pstmt.setBoolean(13, scopus.isSelected());
                    pstmt.setBoolean(14, pure.isSelected());pstmt.setBoolean(15, acad.isSelected());pstmt.setBoolean(16, twit.isSelected());
                    //isUpload column defines if it has been uploaded or saved, so if the upload btn is pressed, then true
                    if(e.getSource() == uploadBtn) {
                        pstmt.setBoolean(17, true);
                    }else{
                        pstmt.setBoolean(17, false);
                    }
                    //If the upload already exists then the update requires a primary key, uploadID
                    if(uploadID!=0){
                        pstmt.setInt(18, uploadID);
                    }

                    pstmt.executeUpdate(); //Execute the statment
                    ResultSet rs = pstmt.getGeneratedKeys(); //generated keys has the row that was entered
                    if (rs.next() && uploadID == 0) { //if we are working with an upload that didn't exist before
                        uploadID = rs.getInt("upload_ID"); //Allows for authors and files to be associated with upload
                    }

                    //add in each contact into upload_Authors, associating it with the upload.
                    for (int i = 0; i < addedContacts.getSize(); i++) {
                        PreparedStatement pstmtAuthors = conn.prepareStatement(sqlAuthors);
                        pstmtAuthors.setInt(1, addedContacts.getElementAt(i).getContact_ID());
                        pstmtAuthors.setBoolean(2, true);
                        pstmtAuthors.setInt(3, uploadID);
                        pstmtAuthors.executeUpdate();
                    }
                    //add in each file into upload_Files, associating it with the upload.
                    for (int i = 0; i < attachedFileList.getModel().getSize(); i++) {
                        PreparedStatement pstmtFiles = conn.prepareStatement(sqlFiles);
                        pstmtFiles.setString(1, attachedFileList.getModel().getElementAt(i).toString());
                        pstmtFiles.setInt(2, uploadID);
                        pstmtFiles.executeUpdate();
                    }
                } catch (SQLException et) {
                    System.out.println(et.getMessage());
                }

        }

    }
    /**
     *Initiate a connection the database being used
     */
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

    /**
     * Defines what to do when a Contact is clicked on in the list of potential contacts to add
     * @param e The event
     */
    public void valueChangedToAdd(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (notAddedContactList.getSelectedIndex() == -1) {
            } else {
                Contact selectedContact = (Contact) notAddedContactList.getSelectedValue();
                addedContacts.addElement(selectedContact);
                displayedContacts.removeElement(selectedContact);
                notAddedContacts.removeElement(selectedContact);
            }
        }
    }

    /**
     * Defines what to do when a contact is clicked on in the added contacts
     * @param e The event
     */
    public void valueChangedAdded(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (addedContactsList.getSelectedIndex() == -1) {
            } else {
                Contact selectedContact = (Contact) addedContactsList.getSelectedValue();
//                System.out.println(selectedContact);
                displayedContacts.addElement(selectedContact);
                notAddedContacts.addElement(selectedContact);
                addedContacts.removeElement(selectedContact);
            }
        }
    }

    //Defines what should be done any time text is entered into the SearchField
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
        displayedContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            if(notAddedContacts.contains(contactDB.getListModel().getElementAt(i))){
                displayedContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
            }
        }
    }

    //Clear whatever was in the textField previously
    public void focusGained(FocusEvent e) {
        searchField.setText("");
        searchField.setForeground(Color.BLACK);
    }
    //Allow for the display of the "Search..." text on the text area while not in focus and empty
    public void focusLost(FocusEvent e) {
        if(searchField.getText().isEmpty()){
            searchField.getDocument().removeDocumentListener(this);
            searchField.setText("Search...");
            searchField.setForeground(Color.GRAY);
            searchField.getDocument().addDocumentListener(this);
        }
    }
    //Select a file via the fileSelectButton
    public void fileSelectedAction(ActionEvent e){
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try {
                File file = fc.getSelectedFile();
                DefaultListModel l = (DefaultListModel)attachedFileList.getModel();
                l.addElement(file);
            }catch(Exception e1){};
        }else{
        }
    }

}

/**
 * Allows for the Drag and Drop mechanics to work.
 */
class FileListTransferHandler extends TransferHandler {
    private JList list;

    FileListTransferHandler(JList list){
        this.list = list;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            return false;
        }
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        Transferable t = support.getTransferable();
        try {
            java.util.List files = (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
            DefaultListModel model = (DefaultListModel) list.getModel();
            for (int i = 0; i < files.size(); i++) {
                model.addElement(files.get(i));
            }
            } catch(UnsupportedFlavorException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
            return true;
        }
    }

/**
 * Defines how to display a File for each cell in the JList
 */
class FileRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        File file = (File)value;
        setText( file.getName() );

        return this;
    }
}