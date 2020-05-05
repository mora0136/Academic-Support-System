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
    JPanel cardPane, leftPanel, rightPanel, contextPanel, dataPanel, contactsPanel, servicesPanel, uploadPanel, filePanel, extraPanel, lowerPanel, contactsListPanel, titlePanel, descPanel;
    JScrollPane contactListScroll, addedListScroll;
    JButton backBtn, resetBtn, fileSelectBtn, saveBtn, uploadBtn, selectAll, deselectAll;
    Image backImg, resetImg, fileImg, saveImg, uploadImg;
    JLabel titleLabel, descLabel, fileLabel, typeLabel, dateLabel, uploadLabel, authorsLabel, contactsLabel, addedLabel;
    ContactDB contactDB;
    DefaultListModel<Contact> toAddContacts, addedContacts, tempList;
    JComboBox selectTypeComboBox;
    JTextArea descriptionTextArea;
    JTextField titleField, searchField;
    JDatePanel publishDatePanel;
    JList attachedFileList, toAddContactList, addedContactsList, templateStatement;
    JCheckBox cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit;
    JFileChooser fc;
    int uploadID = 0;

    UploadPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout) pane.getLayout();


        leftPanel = new JPanel();
        rightPanel = new JPanel();
        contextPanel = new JPanel();
        uploadPanel = new JPanel();
        dataPanel = new JPanel();
        filePanel = new JPanel();
        extraPanel = new JPanel();
        lowerPanel = new JPanel();
        titlePanel = new JPanel();
        descPanel = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(new GridLayout(1, 2));
        leftPanel.setLayout(new BorderLayout());
        contextPanel.setLayout(new GridLayout(1, 2));
        dataPanel.setLayout(gridBag);
        lowerPanel.setLayout(new GridLayout(1, 2));
        filePanel.setLayout(gridBag);
        extraPanel.setLayout(gridBag);
        titlePanel.setLayout(gridBag);
        descPanel.setLayout(gridBag);

        rightPanel.setLayout(new BorderLayout());
        uploadPanel.setLayout(new GridLayout(1, 2));

        backImg = ImageIO.read(new File("resources/back.png"));
        resetImg = ImageIO.read(new File("resources/reset.png"));
        saveImg = ImageIO.read(new File("resources/save.png"));
        uploadImg = ImageIO.read(new File("resources/upload.png"));

        backBtn = new JButton("Back");
        backBtn.addActionListener(this::actionPerformed);
        contextPanel.add(backBtn);

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this::actionPerformed);
        contextPanel.add(resetBtn);

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
        descLabel = new JLabel("Description:");
        c.gridheight = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 20, 0, 20);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(descLabel, c);
        descPanel.add(descLabel);

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

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setPreferredSize(new Dimension(1, 1));
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 5, 20);
        gridBag.setConstraints(descriptionTextArea, c);
        descPanel.add(descriptionTextArea);

        //The File Selection section
        fileLabel = new JLabel("File:");
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fileLabel, c);
        filePanel.add(fileLabel);
        fileSelectBtn = new JButton("Select A File...");
        fc = new JFileChooser();
        fileSelectBtn.addActionListener(this::fileSelectedAction);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(fileSelectBtn, c);
        filePanel.add(fileSelectBtn);

        //List to display files and drag pane
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

        //The type DropDown Section
        typeLabel = new JLabel("Type");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(typeLabel, c);
        extraPanel.add(typeLabel);
        String[] types = new String[]{"Article", "Journal", "Paper", "Book", "Research"};
        selectTypeComboBox = new JComboBox(types);
        c.weightx = 10;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(selectTypeComboBox, c);
        extraPanel.add(selectTypeComboBox);

        //The Date Calender Section
        dateLabel = new JLabel("Date:");
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(dateLabel, c);
        extraPanel.add(dateLabel);
//        Calendar cal = new GregorianCalendar();
//        java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());
        publishDatePanel = new JDatePanel();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(publishDatePanel, c);
        extraPanel.add(publishDatePanel);

        lowerPanel.add(filePanel);
        lowerPanel.add(extraPanel);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.1;
        gridBag.setConstraints(titlePanel, c);
        dataPanel.add(titlePanel);
        c.weighty = 0.5;
        gridBag.setConstraints(descPanel, c);
        dataPanel.add(descPanel);
        c.weighty = 0.4;
        gridBag.setConstraints(lowerPanel, c);
        dataPanel.add(lowerPanel);

        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(dataPanel, BorderLayout.CENTER);


        contactsPanel = new JPanel();
        contactsListPanel = new JPanel();
        contactsPanel.setLayout(gridBag);
        contactsListPanel.setLayout(gridBag);

        authorsLabel = new JLabel("Authors:");
        c.insets = new Insets(10, 10, 0, 10);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weighty = 0.1;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridBag.setConstraints(authorsLabel, c);
        contactsPanel.add(authorsLabel);

        searchField = new JTextField("Search...");
        searchField.setForeground(Color.GRAY);
        searchField.addActionListener(this::actionPerformed);
        searchField.getDocument().addDocumentListener((DocumentListener) this);
        searchField.addFocusListener(this);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.9;
        gridBag.setConstraints(searchField, c);
        contactsPanel.add(searchField);

        contactsLabel = new JLabel("Contacts");
        c.insets = new Insets(0, 5, 0, 5);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        gridBag.setConstraints(contactsLabel, c);
        contactsListPanel.add(contactsLabel);

        addedLabel = new JLabel("Added");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.CENTER;
        gridBag.setConstraints(addedLabel, c);
        contactsListPanel.add(addedLabel);

        //The Contact Selector
        contactDB = new ContactDB();
        toAddContacts = contactDB.getListModel();
        addedContacts = new DefaultListModel<Contact>();
        tempList = new DefaultListModel<>();
        for(int i = 0; i<toAddContacts.getSize(); i++){
            tempList.addElement((Contact)toAddContacts.getElementAt(i));
        }

        toAddContactList = new JList(toAddContacts);
        toAddContactList.addListSelectionListener(this::valueChangedToAdd);
        contactListScroll = new JScrollPane(toAddContactList);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(contactListScroll, c);
        contactsListPanel.add(contactListScroll);

        addedContactsList = new JList(addedContacts);
        addedContactsList.addListSelectionListener(this::valueChangedAdded);
        addedListScroll = new JScrollPane(addedContactsList);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(addedListScroll, c);
        contactsListPanel.add(addedListScroll);

        gridBag.setConstraints(contactsListPanel, c);
        contactsPanel.add(contactsListPanel);

        //The services which can be uploaded to
        servicesPanel = new JPanel();
        servicesPanel.setLayout(new GridLayout(5, 3));
        uploadLabel = new JLabel("Upload to...");
        servicesPanel.add(uploadLabel);
        selectAll = new JButton("Select All");
        servicesPanel.add(selectAll);
        deselectAll = new JButton("Deselect All");
        servicesPanel.add(deselectAll);

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

        saveBtn = new JButton("Save");
        saveBtn.addActionListener(this::actionPerformed);
        uploadPanel.add(saveBtn);

        uploadBtn = new JButton("Upload");
        uploadBtn.addActionListener(this::actionPerformed);
        uploadPanel.add(uploadBtn);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(contactsPanel);
        topPanel.add(servicesPanel);

//        rightPanel.add(contactsPanel, BorderLayout.NORTH);
        rightPanel.add(topPanel, BorderLayout.CENTER);
        rightPanel.add(uploadPanel, BorderLayout.SOUTH);
        add(leftPanel);
        add(rightPanel);
//        setTo(6);
        //Still very structure according to the contact Button
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
                listProperties(toAddContactList, listFont);
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

    private void resetAll(){
        selectTypeComboBox.setSelectedIndex(0);
        descriptionTextArea.setText("");
        titleField.setText("");
        searchField.setText("");
        publishDatePanel.getModel().setValue(null); //maybe?
        attachedFileList.setModel(new DefaultListModel());
        toAddContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            toAddContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
        }
        addedContacts.removeAllElements();
        cv.setSelected(false);resGate.setSelected(false);orcid.setSelected(false);
        inst.setSelected(false);publ.setSelected(false);wos.setSelected(false);
        gSch.setSelected(false);linIn.setSelected(false);scopus.setSelected(false);
        pure.setSelected(false);acad.setSelected(false);twit.setSelected(false);
    }

    private void setToExistingUpload(int uploadID){
        this.uploadID = uploadID;
        String sql = "SELECT * FROM uploads WHERE upload_ID = "+ uploadID;
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){

            selectTypeComboBox.setSelectedItem(rs.getString("Type")); // needs to be changed to appropriate
            descriptionTextArea.setText(rs.getString("Description"));
            titleField.setText(rs.getString("Title"));
            searchField.setText("");
            String sqlDate = rs.getString("Date");
            java.util.Date date = new SimpleDateFormat("yyyy-mm-dd").parse(sqlDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            DateModel<Calendar> dateModel = (DateModel<Calendar>) publishDatePanel.getModel();
            dateModel.setValue(calendar);
            cv.setSelected(rs.getBoolean("cv"));resGate.setSelected(rs.getBoolean("resGate"));orcid.setSelected(rs.getBoolean("orcid"));
            inst.setSelected(rs.getBoolean("inst"));publ.setSelected(rs.getBoolean("publ"));wos.setSelected(rs.getBoolean("wos"));
            gSch.setSelected(rs.getBoolean("gSch"));linIn.setSelected(rs.getBoolean("linIn"));scopus.setSelected(rs.getBoolean("scopus"));
            pure.setSelected(rs.getBoolean("pure"));acad.setSelected(rs.getBoolean("acad"));twit.setSelected(rs.getBoolean("twit"));
        }catch(SQLException | ParseException e){

        }

        String sqlSelectAuthors = "SELECT * FROM upload_Authors JOIN contacts ON upload_Authors.contact_ID = contacts.contact_ID WHERE upload_ID = "+ uploadID;
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlSelectAuthors)){
            while(rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                        rs.getString("surname"), rs.getString("email"),
                        rs.getString("phone"));
                addedContacts.addElement(c);
                toAddContacts.removeElement(c);
            }

        }catch(SQLException e){

        }

        String sqlSelectFiles = "SELECT * FROM upload_Files WHERE upload_ID = "+ uploadID;
        DefaultListModel news = (DefaultListModel) attachedFileList.getModel();
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlSelectFiles)){
            while(rs.next()) {
                news.addElement(new File(rs.getString("File")));
            }

        }catch(SQLException e){

        }
    }


    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backBtn){
            cardLayout.show(cardPane, "Home");
        }else if(e.getSource() == resetBtn){
            resetAll();
        }else if(e.getSource() == uploadBtn || e.getSource() == saveBtn){
            //connect to database
            //save to database
            String sqlUpload, sqlAuthors, sqlFiles;
            if(uploadID != 0) {
                sqlUpload = "UPDATE uploads SET Title = ?,Description = ?, type = ?, Date = ?, cv = ?, resGate = ?, orcid = ?, inst = ?, publ = ?, wos = ?, gSch = ?, linIn = ?, scopus = ?, pure = ?, acad = ?, twit = ?, isUploaded = ? WHERE upload_ID = ?";
                sqlAuthors = "UPDATE upload_Authors SET contact_ID = ?, nameUsed = ? WHERE upload_ID = ?";
                sqlFiles = "UPDATE upload_Files SET File = ? WHERE upload_ID = ?";

            }else {
                //Then do a fresh insertion, creating new uploadID
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
                    if(e.getSource() == uploadBtn) {
                        pstmt.setBoolean(17, true);
                    }else{
                        pstmt.setBoolean(17, false);
                    }

                    if(uploadID!=0){
                        pstmt.setInt(18, uploadID);
                    }

                    pstmt.executeUpdate();
                    ResultSet rs = pstmt.getGeneratedKeys();
                    System.out.println("Current uploadID: "+ uploadID);
                    if (rs.next() && uploadID == 0) {
                        System.out.println("in db: "+ rs.getInt(1));
                        uploadID = rs.getInt(1);
                    }

                    for (int i = 0; i < addedContacts.getSize(); i++) {
                        PreparedStatement pstmtAuthors = conn.prepareStatement(sqlAuthors);
                        pstmtAuthors.setInt(1, addedContacts.getElementAt(i).getContact_ID());
                        pstmtAuthors.setBoolean(2, true);
                        pstmtAuthors.setInt(3, uploadID);
                        pstmtAuthors.executeUpdate();
                    }
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

    public void valueChangedToAdd(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (toAddContactList.getSelectedIndex() == -1) {
            } else {
//                for (Object selectedValue : toAddContactList.getSelectedValue()) {
                Contact selectedContact = (Contact)toAddContactList.getSelectedValue();
//                System.out.println(selectedContact);
                addedContacts.addElement(selectedContact);
                toAddContacts.removeElement(selectedContact);
                tempList.removeElement(selectedContact);
            }
        }
    }

    public void valueChangedAdded(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (addedContactsList.getSelectedIndex() == -1) {
            } else {
                Contact selectedContact = (Contact) addedContactsList.getSelectedValue();
//                System.out.println(selectedContact);
                toAddContacts.addElement(selectedContact);
                tempList.addElement(selectedContact);
                addedContacts.removeElement(selectedContact);
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
        toAddContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            if(tempList.contains(contactDB.getListModel().getElementAt(i))){
                toAddContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
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
@SuppressWarnings("serial")
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

class FileRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        File file = (File)value;
        setText( file.getName() );

        return this;
    }
}