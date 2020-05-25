package panel;

import contacts.Contact;
import contacts.ContactDB;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePanel;
import template.Template;
import template.TemplateDB;
import template.TemplatePanel;
import upload.Upload;
import upload.UploadDB;

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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/*
 * This is by far the most complex Panel of all classes. It is not suggested you start here if you are wishing to first
 * understand the workings of this project. UploadPanel has the most user input of all panels and the layout of the
 * information per mockups were very incompatible with the Layout Managers of java. This then required a lot of JPanel
 * objects to contain sometimes one Component. Utilising GridBag Layout is powerful but overwhelming in how it can
 * clutter code. The structure of this class is first, insantiating all fields on the left side of the windows, and
 * then instantiating the right side of the window. Button listeners are given their own methods that take an Action
 * Event. Some other classes are contained within and are exclusively used inside of the UploadPanel. Further
 * information can be found where they exist.
 */

public class UploadPanel extends JPanel implements DocumentListener, FocusListener{
    CardLayout cardLayout;
    JPanel cardPane, leftPanel, rightPanel, backResetPanel, uploadDetailsLeftPanel, contactsPanel, servicesPanel,
            saveUploadPanel, filePanel, typeDatePanel, fileTypeDatePanel, contactsListPanel, titlePanel, descPanel;
    JScrollPane contactListScroll, addedListScroll;
    JButton backBtn, resetBtn, fileSelectBtn, saveBtn, uploadBtn, selectAll, deselectAll;
    Image backImg, resetImg, saveImg, uploadImg;
    JLabel titleLabel, descLabel, fileLabel, typeLabel, dateLabel, uploadLabel, authorsLabel, contactsLabel, addedLabel;
    DefaultListModel<Contact> displayedContacts, addedContacts, notAddedContacts;
    DefaultListModel<Template> templates;
    JComboBox selectTypeComboBox;
    JTextArea descriptionTextArea;
    JTextField titleField, searchField;
    JDatePanel publishDatePanel;
    JList attachedFileList, notAddedContactList, addedContactsList, templateStatement;
    JCheckBox cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit;
    JFileChooser fc;
    int mainFont = 32;
    ContactDB contactDB;
    Upload currentUpload = new Upload(); // If the upload already exists, it is stored in here so that the DB can update
    JSplitPane sp;
    boolean isEditView = false;

    public UploadPanel(JPanel pane){
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
        try {
            backImg = ImageIO.read(new File("resources/back.png"));
            resetImg = ImageIO.read(new File("resources/reset.png"));
            saveImg = ImageIO.read(new File("resources/save.png"));
            uploadImg = ImageIO.read(new File("resources/upload.png"));
        }catch(IOException e){
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Image files failed to load. No images will be available", "", JOptionPane.WARNING_MESSAGE);
            backImg = null;
            resetImg = null;
            saveImg = null;
            uploadImg = null;
            System.out.println(e);
        }

        //Define the back button
        backBtn = new JButton("Back");
        backBtn.addActionListener(this::actionPerformedBack);
        backBtn.setMnemonic('b');
        backResetPanel.add(backBtn);
        //Define the Reset Button
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this::actionPerformedReset);
        resetBtn.setMnemonic('r');
        backResetPanel.add(resetBtn);

        //A generic inset that will apply until next changed
        c.insets = new Insets(0, 5, 0, 5);

        //The Title Text Section
        titleLabel = new JLabel("Title:");
        gridBag.setConstraints(titleLabel, c);
        titlePanel.add(titleLabel);
        titleField = new JTextField();
        titleField.setColumns(15);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(titleField, c);
        titlePanel.add(titleField);


        //The Description Text Section
        //Defining the Label
        descLabel = new JLabel("Description:");
        c.gridheight = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 5, 0, 5);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(descLabel, c);
        descPanel.add(descLabel);
        //Defining the template statements
        templates = new DefaultListModel<>();
        DefaultListModel t = TemplateDB.getTemplates();
        for(int i = 0; i < t.getSize(); i++){
            templates.addElement((Template) t.get(i));
        }
        templates.add(0, new Template(-1, "Add/Edit Templates"));
        templateStatement = new JList(templates);
        templateStatement.setCellRenderer(new templateCellRenderer());
        templateStatement.setMinimumSize(new Dimension(1, 1));
        templateStatement.addListSelectionListener(this::valueChangedTemplate);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(templateStatement, c);
        //Defining the Description textArea
        descriptionTextArea = new JTextArea(1, 1);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionTextArea);
        gridBag.setConstraints(descriptionScroll, c);
        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.setOneTouchExpandable(true);
        gridBag.setConstraints(sp, c);
        sp.add(templateStatement);
        sp.add(descriptionScroll);
        descPanel.add(sp);

        //The File Selection section
        //Define the File Label
        fileLabel = new JLabel("File:");
        c.weighty = 0.1;
        c.insets = new Insets(0, 5, 5, 5);
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
        tempFileList.addElement(null);
        attachedFileList = new JList(tempFileList);
        attachedFileList.setCellRenderer(new FileRenderer());
        attachedFileList.setTransferHandler(new FileListTransferHandler(attachedFileList));
        attachedFileList.setDropMode(DropMode.INSERT);
        attachedFileList.getSelectionModel().addListSelectionListener(this::valueChangedFile);
        JScrollPane fileScroll = new JScrollPane(attachedFileList);
        c.weighty = 0.9;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(fileScroll, c);
        filePanel.add(fileScroll);

        //The type DropDown Section(e.g Article, Book, Journal)
        //Define the type Label
        typeLabel = new JLabel("Type:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.25;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(typeLabel, c);
        typeDatePanel.add(typeLabel);
        //Define the comboBox containing the possible types
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
        publishDatePanel = new JDatePanel(new java.util.Date());
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(publishDatePanel, c);
        typeDatePanel.add(publishDatePanel);

        //Add the File and typeDate to one panel, organised in a 1row*2column grid
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
        contactsListPanel = new JPanel(); //Contains the two labels and lists containing added and not added contacts
        servicesPanel = new JPanel(); //Contains the Label, buttons and checkBoxes for what service to upload to
        JPanel contactsServicesPanel = new JPanel(); //Contains the contacts and services to take up the centre of right panel

        rightPanel.setLayout(new BorderLayout());
        contactsPanel.setLayout(gridBag);
        contactsListPanel.setLayout(new SpringLayout());
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
        searchField.getDocument().addDocumentListener((DocumentListener) this);
        searchField.addFocusListener(this);
        searchField.setToolTipText("Search for a Contact to Add");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.9;
        gridBag.setConstraints(searchField, c);
        contactsPanel.add(searchField);

        //The contactsListPanel
        //Defining the ContactsLabel
        contactsLabel = new JLabel("Contacts");
        contactsListPanel.setLayout(new SpringLayout());
        contactsListPanel.add(contactsLabel);
        //Defining the added Label
        addedLabel = new JLabel("Added");
        contactsListPanel.add(addedLabel);
        //The Contact Selector lists instantiation
        contactDB = new ContactDB();
        displayedContacts = contactDB.getListModel(); //The displayed list which changes according to the search results
        addedContacts = new DefaultListModel<>(); //The contacts that have been added to the upload
        notAddedContacts = new DefaultListModel<>(); //Stores a list in the background of the full list of contacts that could be added, this can be used to search for
        for(int i = 0; i< displayedContacts.getSize(); i++){ // simply copying the values to the other
            notAddedContacts.addElement(displayedContacts.getElementAt(i));
        }
        //Defining the not added contact List and adding a Scroll Pane
        notAddedContactList = new JList(displayedContacts);
        notAddedContactList.setFixedCellWidth(1); // fixed width ensures the space is filled evenly between both lists despite anything that it contains
        notAddedContactList.addListSelectionListener(this::valueChangedToAdd);
        contactListScroll = new JScrollPane(notAddedContactList);
        contactsListPanel.add(contactListScroll);
        //Defining the added Contact List and adding a scroll pane
        addedContactsList = new JList(addedContacts);
        addedContactsList.setFixedCellWidth(1);
        addedContactsList.addListSelectionListener(this::valueChangedAdded);
        addedListScroll = new JScrollPane(addedContactsList);
        contactsListPanel.add(addedListScroll);
        //Apply a grid to the contacts list
        SpringUtilities.makeCompactGrid(contactsListPanel,
                2, 2, //rows, cols
                0, 0,        //initX, initY
                5, 5);
        //Add List Panel to the contacts Panel
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(contactsListPanel, c);
        contactsPanel.add(contactsListPanel);

        //The components that are contained in servicesPanel
        uploadLabel = new JLabel("Upload to...");
        servicesPanel.add(uploadLabel);
        selectAll = new JButton("Select All");
        selectAll.addActionListener(this::actionPerformedSelect);
        servicesPanel.add(selectAll);
        deselectAll = new JButton("Deselect All");
        deselectAll.addActionListener(this::actionPerformedSelect);
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
        saveBtn.addActionListener(this::actionPerformedUpdateDB);
        saveBtn.setMnemonic('s');
        saveUploadPanel.add(saveBtn);
        //Defining Upload btn
        uploadBtn = new JButton("Upload");
        uploadBtn.addActionListener(this::actionPerformedUpdateDB);
        uploadBtn.setMnemonic('u');
        saveUploadPanel.add(uploadBtn);

        //Adding to the rightPanel
        rightPanel.add(contactsServicesPanel, BorderLayout.CENTER);
        rightPanel.add(saveUploadPanel, BorderLayout.SOUTH);

        //Adding to the window
        add(leftPanel);
        add(rightPanel);

        //The following defines what should happen to a component when the window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int bodyFont = (int)(mainFont*(0.6));
                int checkBoxFont = (int)(mainFont*(0.75));
                int width = 50;
                int height = 50;

                if (windowWidth < 1300 || windowHeight < 600) {
                    width = (int) (windowHeight * 0.0625);
                    headerFont = (Integer.min(windowWidth /(1300/headerFont), windowHeight/(600/headerFont)));
                    listFont = (Integer.min(windowWidth/(1300/listFont), windowHeight/(600/listFont)));
                    bodyFont = (Integer.min(windowWidth/(1300/bodyFont), windowHeight/(600/bodyFont)));
                    checkBoxFont = (Integer.min(windowWidth/(1300/checkBoxFont), windowHeight/(600/checkBoxFont)));
                }

                ComProps.buttonProperties(backBtn, backImg, width, height, headerFont, true);
                ComProps.buttonProperties(resetBtn, resetImg, width, height, headerFont, true);
                ComProps.buttonProperties(saveBtn, saveImg, width, height, headerFont, true);
                ComProps.buttonProperties(uploadBtn, uploadImg, width, height, headerFont, true);

                ComProps.listProperties(attachedFileList, listFont);
                ComProps.listProperties(notAddedContactList, listFont);
                ComProps.listProperties(addedContactsList, listFont);
                ComProps.listProperties(templateStatement, bodyFont);

                ComProps.headingProperties(titleLabel, headerFont);
                ComProps.headingProperties(descLabel, headerFont);
                ComProps.headingProperties(fileLabel, headerFont);
                ComProps.headingProperties(typeLabel, headerFont);
                ComProps.headingProperties(dateLabel, headerFont);
                ComProps.headingProperties(uploadLabel, headerFont);
                ComProps.headingProperties(authorsLabel, headerFont);
                ComProps.headingProperties(contactsLabel, headerFont-4);
                ComProps.headingProperties(addedLabel, headerFont-4);

                ComProps.checkBoxProperties(cv, checkBoxFont);
                ComProps.checkBoxProperties(resGate, checkBoxFont);
                ComProps.checkBoxProperties(orcid, checkBoxFont);
                ComProps.checkBoxProperties(inst, checkBoxFont);
                ComProps.checkBoxProperties(publ, checkBoxFont);
                ComProps.checkBoxProperties(wos, checkBoxFont);
                ComProps.checkBoxProperties(gSch, checkBoxFont);
                ComProps.checkBoxProperties(linIn, checkBoxFont);
                ComProps.checkBoxProperties(scopus, checkBoxFont);
                ComProps.checkBoxProperties(pure, checkBoxFont);
                ComProps.checkBoxProperties(acad, checkBoxFont);
                ComProps.checkBoxProperties(twit, checkBoxFont);

                ComProps.textFieldProperties(titleField, headerFont);
                ComProps.textAreaProperties(descriptionTextArea, bodyFont);
                ComProps.textFieldProperties(searchField, headerFont);

                selectTypeComboBox.setFont(new Font("Arial", Font.PLAIN, listFont));
                fileSelectBtn.setFont(new Font("Arial", Font.PLAIN, bodyFont));
                selectAll.setFont(new Font("Arial", Font.PLAIN, headerFont));
                deselectAll.setFont(new Font("Arial", Font.PLAIN, headerFont));
            }
        });
    }

    /**
     * Sets all components in the window to their default values
     */
    public void resetAll(){
        currentUpload = new Upload();
        selectTypeComboBox.setSelectedIndex(0);
        descriptionTextArea.setText("");
        titleField.setText("");
        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);

        DateModel d = publishDatePanel.getModel();
        d.setValue(new Date(System.currentTimeMillis()));

        DefaultListModel f = (DefaultListModel) attachedFileList.getModel();
        if(this instanceof UploadPanelDisabled && f.getSize() > 0) { //The fileList in disabled view contains files at index 0 we want to remove
            f.removeAllElements();
        }else if(f.getSize() > 1){ //Boundary checking to ensure aren't removing range not in bounds.
            f.removeRange(1, f.getSize()-1); //Don't include first element, as this is the tip to drag file
        }

        addedContacts.removeAllElements();
        displayedContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            displayedContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
        }

        cv.setSelected(false);resGate.setSelected(false);orcid.setSelected(false);
        inst.setSelected(false);publ.setSelected(false);wos.setSelected(false);
        gSch.setSelected(false);linIn.setSelected(false);scopus.setSelected(false);
        pure.setSelected(false);acad.setSelected(false);twit.setSelected(false);
    }

    /**
     * Sets all the components in the window to a state as saved in the database for the corresponding uplaod_ID
     * @param uploadID The uploadID of the upload to be viewed/set
     */
    public void setToExistingUpload(int uploadID){
        resetAll();
        isEditView = true;
        currentUpload = UploadDB.getUpload(uploadID);
        titleField.setText(currentUpload.getTitle());
        descriptionTextArea.setText(currentUpload.getDescription());
        selectTypeComboBox.setSelectedItem(currentUpload.getType());
        for (int i = 0; i < currentUpload.getAttachedFiles().getSize(); i++) {
            ((DefaultListModel<File>) attachedFileList.getModel()).addElement((File) currentUpload.getAttachedFiles().getElementAt(i));
        }


        LocalDate date = currentUpload.getDate();
        publishDatePanel.getModel().setDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        for(int i = 0; i< currentUpload.getAddedContacts().getSize(); i++){
            addedContacts.addElement((Contact) currentUpload.getAddedContacts().getElementAt(i));
            displayedContacts.removeElement(currentUpload.getAddedContacts().getElementAt(i));
        }
        cv.setSelected(currentUpload.isCv());resGate.setSelected(currentUpload.isResGate());orcid.setSelected(currentUpload.isOrcid());
        inst.setSelected(currentUpload.isInst());publ.setSelected(currentUpload.isPubl());wos.setSelected(currentUpload.isWos());
        gSch.setSelected(currentUpload.isgSch());linIn.setSelected(currentUpload.isLinIn());scopus.setSelected(currentUpload.isScopus());
        pure.setSelected(currentUpload.isPure());acad.setSelected(currentUpload.isAcad());twit.setSelected(currentUpload.isTwit());

    }


    public void actionPerformedBack(ActionEvent e){
        if(!isEditView) {
            int n = JOptionPane.showConfirmDialog(this,
                    "NOTE: All unsaved data will be erased\nWould you like to return to the Home Panel?",
                    "Continue Back?",
                    JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.OK_OPTION){
                resetAll();
                cardLayout.show(cardPane, "Home");
            }
        }else{
            cardLayout.show(cardPane, "Edit");
        }
    }

    public void actionPerformedReset(ActionEvent e){
        resetAll();
    }

    public void actionPerformedUpdateDB(ActionEvent e) {
        titleField.setBorder(UIManager.getBorder("TextField.border"));
        descriptionTextArea.setBorder(UIManager.getBorder("TextField.border"));
        if(e.getSource() == uploadBtn){
            if(titleField.getText().isBlank()){
                JOptionPane.showMessageDialog(this, "Please Enter a Title");
                titleField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                return; // exit the upload action because fields still required
            }else if(descriptionTextArea.getText().isBlank()){
                JOptionPane.showMessageDialog(this, "Please Enter a Description");
                descriptionTextArea.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                return;
            }else if(!(cv.isSelected() || resGate.isSelected() || orcid.isSelected() || inst.isSelected() ||
                    publ.isSelected() || wos.isSelected() || gSch.isSelected() || linIn.isSelected() ||
                    scopus.isSelected() || pure.isSelected() || acad.isSelected() || twit.isSelected())){
                JOptionPane.showMessageDialog(this, "Please Enter at least one service to upload to");
                return;
            }
            //Confirm the selection made was correct
            int n = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to upload?\nUploaded data cannot be changed and is permanent.",
                    "Continue Upload?",
                    JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION){
                return;
            }

        }else if (e.getSource() == saveBtn){
            if(titleField.getText().isBlank()){
                JOptionPane.showMessageDialog(this, "Please Enter a Title");
                titleField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                return; // exit the save action because fields still required
            }
        }

        currentUpload.setTitle(titleField.getText());
        currentUpload.setDescription(descriptionTextArea.getText());
        DefaultListModel f = new DefaultListModel<>();
        // removes the null at the front of the list
        for(int i = 1; i < attachedFileList.getModel().getSize(); i++){
            f.addElement(attachedFileList.getModel().getElementAt(i));
        }
        currentUpload.setAttachedFiles(f);
        currentUpload.setType((String) selectTypeComboBox.getSelectedItem());
        if(publishDatePanel.getModel().isSelected()) {
            Date date = (Date) (publishDatePanel.getModel().getValue());
            Instant instant = date.toInstant();
            currentUpload.setDate(instant.atZone(ZoneId.systemDefault()).toLocalDate());
        }else{
            //default to todays date
            currentUpload.setDate(LocalDate.now());
        }
        currentUpload.setAddedContacts(addedContacts);
        currentUpload.setCv(cv.isSelected());currentUpload.setResGate(resGate.isSelected());currentUpload.setOrcid(orcid.isSelected());
        currentUpload.setInst(inst.isSelected());currentUpload.setPubl(publ.isSelected());currentUpload.setWos(wos.isSelected());
        currentUpload.setgSch(gSch.isSelected());currentUpload.setLinIn(linIn.isSelected());currentUpload.setScopus(scopus.isSelected());
        currentUpload.setPure(pure.isSelected());currentUpload.setAcad(acad.isSelected());currentUpload.setTwit(twit.isSelected());

        int uploadID = UploadDB.addExistingUpload(currentUpload, e.getSource() == uploadBtn, currentUpload.getUploadID() != 0);
        currentUpload.setUploadID(uploadID);
        if(e.getSource() == uploadBtn){
            cardLayout.show(cardPane, "Home");
            JOptionPane.showConfirmDialog(null, "\'"+currentUpload.getTitle()+"\' was successfully Upload!", "Successful Upload", JOptionPane.PLAIN_MESSAGE);
            resetAll();
        }else{
            JOptionPane.showConfirmDialog(null, "\'"+currentUpload.getTitle()+"\' was successfully saved!", "Successfully Saved", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void actionPerformedSelect(ActionEvent e){
        if(e.getSource() == selectAll){
            cv.setSelected(true);resGate.setSelected(true);orcid.setSelected(true);
            inst.setSelected(true);publ.setSelected(true);wos.setSelected(true);
            gSch.setSelected(true);linIn.setSelected(true);scopus.setSelected(true);
            pure.setSelected(true);acad.setSelected(true);twit.setSelected(true);
        }else{
            cv.setSelected(false);resGate.setSelected(false);orcid.setSelected(false);
            inst.setSelected(false);publ.setSelected(false);wos.setSelected(false);
            gSch.setSelected(false);linIn.setSelected(false);scopus.setSelected(false);
            pure.setSelected(false);acad.setSelected(false);twit.setSelected(false);
        }
    }

    public void valueChangedTemplate(ListSelectionEvent e) {
        if(templateStatement.isSelectionEmpty()){
            descriptionTextArea.requestFocusInWindow();
        }else {
            if(templates.getElementAt(templateStatement.getSelectedIndex()).getTemplateID() == -1){
                JOptionPane.showConfirmDialog(null, new TemplatePanel(), "Edit Templates", JOptionPane.PLAIN_MESSAGE);
                templates.removeAllElements();
                DefaultListModel temp = TemplateDB.getTemplates();
                for(int i = 0; i < temp.getSize(); i++){
                    templates.addElement((Template) temp.get(i));
                }
                templates.add(0, new Template(-1, "Add/Edit Templates"));
            }else {
                String keyword = templateStatement.getSelectedValue().toString();
                descriptionTextArea.append(keyword);
                templateStatement.clearSelection();
            }
        }
    }

    public void valueChangedFile(ListSelectionEvent e) {
        if(attachedFileList.getSelectedIndex() > 0) {
            String[] options = {"Back", "Remove File"};
            int result = JOptionPane.showOptionDialog(null, new FileOptionPanel((File) attachedFileList.getSelectedValue()), "File Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                //remove from the list was selected so do this
                DefaultListModel l = (DefaultListModel) attachedFileList.getModel();
                l.remove(attachedFileList.getSelectedIndex());
            }
        }
    }

    /**
     * Defines what to do when a Contact is clicked on in the list of potential contacts to add
     * @param e The event
     */
    public void valueChangedToAdd(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
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
        if (!e.getValueIsAdjusting()) {
            if (addedContactsList.getSelectedIndex() != -1) {
                Contact selectedContact = (Contact) addedContactsList.getSelectedValue();
                displayedContacts.addElement(selectedContact);
                notAddedContacts.addElement(selectedContact);
                addedContacts.removeElement(selectedContact);
            }
        }
    }


    //The Properties of the search contacts field when adding text and focus is moved.
    //Defines what should be done any time text is entered into the SearchField
    @Override
    public void insertUpdate(DocumentEvent e) { search(); }
    @Override
    public void removeUpdate(DocumentEvent e) { search(); }
    @Override
    public void changedUpdate(DocumentEvent e) { }
    private void search(){
        contactDB.searchForContact(searchField.getText());
        displayedContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
            if(notAddedContacts.contains(contactDB.getListModel().getElementAt(i))){
                displayedContacts.addElement((Contact)contactDB.getListModel().getElementAt(i));
            }
        }
    }
    //Clear whatever was in the search textField previously
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
            }catch(Exception e1){
                JOptionPane.showMessageDialog(this, e,"Warning!", JOptionPane.WARNING_MESSAGE);
            };
        }else{
        }
    }
}

/**
 * Allows for the Drag and Drop mechanics of the file list to work by utilising fileListFlavors.
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
        } catch(UnsupportedFlavorException | IOException e){
            e.printStackTrace();
        }
        return true;
    }
}

/**
 * Defines how to display a File for each cell in the JList. Any row in this list that is null will be converted to a
 * info message. This displays an image icon and text informing the user to drag a file into the list. This will ass the
 * file to the list. This null row cannot be edited. It also ensures a file in the list shows it's fileName
 */
class FileRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value == null) { // Any element that is null is considered as a placeholder for the drag file info row
            try {
                super.getListCellRendererComponent(list, value, index, false, false);
                Image addFile = ImageIO.read(new File("resources/addFile.png"));
                //Unfortunately I could not find a way to scale this image according to the size of the window.
                //This means at the smallest view it takes up more space then would be liked.
                Image i = addFile.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
                setIcon(new ImageIcon(i));
                setText("Drag a file to attach");
                setToolTipText("Drag a file to attach");
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.RIGHT);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e,"Warning!", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            File file = (File)value;
            setText( file.getName() );
        }

        return this;
    }
}


/*
 * The template List has a initial row that acts as a dummy to launch the add/edit template view. To give this element
 * a unique look, a cell renderer changes the background of that single element and it's properties to better convey
 * importance to the user
 */
class templateCellRenderer extends DefaultListCellRenderer{
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
        setToolTipText(this.getText());
        if(index == 0){
            setFont(new Font("Arial", Font.BOLD, this.getFont().getSize()+2));
            setBackground(Color.LIGHT_GRAY);
        }
        return this;
    }
}