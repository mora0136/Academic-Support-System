package panel;

import contacts.Contact;
import contacts.ContactDB;
import org.jdatepicker.JDatePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class UploadPanel extends JPanel implements DocumentListener, FocusListener {
    CardLayout cardLayout;
    JPanel cardPane, leftPanel, rightPanel, contextPanel, dataPanel, contactsPanel, servicesPanel, uploadPanel, filePanel, extraPanel, lowerPanel, contactsListPanel;
    JScrollPane contactListScroll, addedListScroll;
    JButton backBtn, resetBtn, fileSelectBtn, saveBtn, uploadBtn, selectAll, deselectAll;
    Image backImg, resetImg, fileImg, saveImg, uploadImg;
    JLabel titleLabel, descLabel, fileLabel, typeLabel, dateLabel, uploadLabel, authorsLabel, contactsLabel, addedLabel;
    ContactDB contactDB;
    DefaultListModel<Contact> toAddContacts, addedContacts, tempList;
    JComboBox selectTypeComboBox;
    JTextArea descriptionTextArea;
    JTextField titleField, searchField;
    Font heading;
    JDatePanel publishDatePanel;
    JList attachedFileList, toAddContactList, addedContactsList;
    ButtonGroup services;
    JCheckBox cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit;

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

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        heading = new Font("Arial", Font.PLAIN, 32);

        setLayout(new GridLayout(1, 2));
        leftPanel.setLayout(new BorderLayout());
        contextPanel.setLayout(new GridLayout(1, 2));
        dataPanel.setLayout(gridBag);
        lowerPanel.setLayout(new GridLayout(1, 2));
        filePanel.setLayout(gridBag);
        extraPanel.setLayout(gridBag);

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
        contextPanel.add(resetBtn);

        c.insets = new Insets(20, 20, 0, 20);

        //The Title Text Section
        titleLabel = new JLabel("Title:");
        titleLabel.setFont(heading);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(titleLabel, c);
        dataPanel.add(titleLabel);
        titleField = new JTextField();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(titleField, c);
        dataPanel.add(titleField);

        //The Description Text Section
        descLabel = new JLabel("Description:");
        descLabel.setFont(heading);
        gridBag.setConstraints(descLabel, c);
        dataPanel.add(descLabel);
        descriptionTextArea = new JTextArea();
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(descriptionTextArea, c);
        dataPanel.add(descriptionTextArea);

        //The File Selection section
        fileLabel = new JLabel("File:");
        fileLabel.setFont(heading);
        c.weighty = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fileLabel, c);
        filePanel.add(fileLabel);
        fileSelectBtn = new JButton("Select A File...");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(fileSelectBtn, c);
        filePanel.add(fileSelectBtn);
        String[] keywords = {"Java", "IntelliJ", "UX", "HCI", "Interactive Computer Systems", "Persona", "Grokkability"};
        attachedFileList = new JList(keywords);
        attachedFileList.setFont(heading);
        c.weighty = 1;
        gridBag.setConstraints(attachedFileList, c);
        filePanel.add(attachedFileList);

        //The type DropDown Section
        typeLabel = new JLabel("Type");
        typeLabel.setFont(heading);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(typeLabel, c);
        extraPanel.add(typeLabel);
        selectTypeComboBox = new JComboBox();
        c.weightx = 10;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(selectTypeComboBox, c);
        extraPanel.add(selectTypeComboBox);

        //The Date Calender Section
        dateLabel = new JLabel("Date:");
        dateLabel.setFont(heading);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(dateLabel, c);
        extraPanel.add(dateLabel);
        publishDatePanel = new JDatePanel();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(publishDatePanel, c);
        extraPanel.add(publishDatePanel);

        lowerPanel.add(filePanel);
        lowerPanel.add(extraPanel);

        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(dataPanel, BorderLayout.CENTER);
        leftPanel.add(lowerPanel, BorderLayout.SOUTH);


        contactsPanel = new JPanel();
        contactsListPanel = new JPanel();
        contactsPanel.setLayout(gridBag);
        contactsListPanel.setLayout(gridBag);

        authorsLabel = new JLabel("Authors:");
        authorsLabel.setFont(heading);
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridBag.setConstraints(authorsLabel, c);
        contactsPanel.add(authorsLabel);

        searchField = new JTextField("Search...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 32));
        searchField.setForeground(Color.GRAY);
        searchField.addActionListener(this::actionPerformed);
        searchField.getDocument().addDocumentListener((DocumentListener) this);
        searchField.addFocusListener(this);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 4;
        gridBag.setConstraints(searchField, c);
        contactsPanel.add(searchField);

        contactsLabel = new JLabel("Contacts");
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.CENTER;
//        c.weightx = 2;
        gridBag.setConstraints(contactsLabel, c);
        contactsListPanel.add(contactsLabel);

        addedLabel = new JLabel("Added");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.CENTER;
//        c.weightx = 2;
        gridBag.setConstraints(addedLabel, c);
        contactsListPanel.add(addedLabel);

        //The Contact Selector
        contactDB = new ContactDB();
        toAddContacts = contactDB.getListModel();
        addedContacts = new DefaultListModel<Contact>();
        tempList = new DefaultListModel<>();
        for(int i = 0; i<toAddContacts.getSize(); i++){
//            System.out.println((Contact)toAddContacts.getElementAt(i));
            tempList.addElement((Contact)toAddContacts.getElementAt(i));
        }

        toAddContactList = new JList(toAddContacts);
        listProperties(toAddContactList);
        toAddContactList.addListSelectionListener(this::valueChangedToAdd);
        contactListScroll = new JScrollPane(toAddContactList);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(contactListScroll, c);
        contactsListPanel.add(contactListScroll);

        addedContactsList = new JList(addedContacts);
        listProperties(addedContactsList);
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
        services = new ButtonGroup();
        uploadLabel = new JLabel("Upload to...");
        uploadLabel.setFont(heading);
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
        services.add(cv);services.add(resGate);services.add(orcid);
        services.add(inst);services.add(publ);services.add(wos);
        services.add(gSch);services.add(linIn);services.add(scopus);
        services.add(pure);services.add(acad);services.add(twit);

        servicesPanel.add(cv);servicesPanel.add(resGate);servicesPanel.add(orcid);
        servicesPanel.add(inst);servicesPanel.add(publ);servicesPanel.add(wos);
        servicesPanel.add(gSch);servicesPanel.add(linIn);servicesPanel.add(scopus);
        servicesPanel.add(pure);servicesPanel.add(acad);servicesPanel.add(twit);

        saveBtn = new JButton("Save");
        uploadPanel.add(saveBtn);

        uploadBtn = new JButton("Upload");
        uploadPanel.add(uploadBtn);

        rightPanel.add(contactsPanel, BorderLayout.NORTH);
        rightPanel.add(servicesPanel, BorderLayout.CENTER);
        rightPanel.add(uploadPanel, BorderLayout.SOUTH);
        add(leftPanel);
        add(rightPanel);
        //Still very structure according to the contact Button
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int font = 16;

                if (windowWidth <= 600) {
                    font = 0;
                }

                if (windowWidth < 800) {
                    int width = (int) (windowWidth * 0.0625);

                    //Since addNew, edit and delete buttons have an image to the left of text, the font can be displayed longer
                    if (windowWidth < 500) {
                        buttonProperties(backBtn, backImg, width, windowHeight, 0);
                        buttonProperties(resetBtn, resetImg, width, windowHeight, 0);
                        buttonProperties(saveBtn, saveImg, width, windowHeight, 0);
                        buttonProperties(uploadBtn, uploadImg, width, windowHeight, 0);
                    } else {
                        buttonProperties(backBtn, backImg, width, windowHeight, 16);
                        buttonProperties(resetBtn, resetImg, width, windowHeight, 16);
                        buttonProperties(saveBtn, saveImg, width, windowHeight, 16);
                        buttonProperties(uploadBtn, uploadImg, width, windowHeight, 16);
                    }

                } else {
                    buttonProperties(backBtn, backImg, 50, 50, 16);
                    buttonProperties(resetBtn, resetImg, 50, 50, 16);
                    buttonProperties(saveBtn, saveImg, 50, 50, 16);
                    buttonProperties(uploadBtn, uploadImg, 50, 50, 16);
                }
            }
        });
    }

    private void listProperties(JList list){
        list.setFont(new Font("Arial", Font.PLAIN, 32));
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

    public void actionPerformed(ActionEvent e) {
        cardLayout.show(cardPane, "Home");
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
//        tempList = new DefaultListModel<>();
//        for(int i = 0; i<toAddContacts.getSize(); i++){
//            System.out.println((Contact)toAddContacts.getElementAt(i));
//            tempList.addElement((Contact)toAddContacts.getElementAt(i));
//        }
//        System.out.println("TEMP LIST "+tempList);
        toAddContacts.removeAllElements();
        for(int i = 0; i<contactDB.getListModel().getSize(); i++){
//            System.out.print((contactDB.getListModel().getElementAt(i)));
//            System.out.println(tempList.getElementAt(i));
            if(tempList.contains(contactDB.getListModel().getElementAt(i))){
//                System.out.println(" Yes");
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

}
