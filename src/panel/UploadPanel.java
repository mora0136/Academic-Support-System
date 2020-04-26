package panel;

import contacts.ContactList;
import org.jdatepicker.JDatePanel;
import org.jdatepicker.JDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UploadPanel extends JPanel {
    CardLayout cardLayout;
    JPanel cardPane, leftPanel, rightPanel, contextPanel, dataPanel, contactsPanel, servicesPanel, uploadPanel, filePanel, extraPanel, lowerPanel, contactsListPanel;
    JScrollPane contactListScroll, addedListScroll;
    JButton backBtn, resetBtn, fileSelectBtn, saveBtn, uploadBtn;
    Image backImg, resetImg, fileImg, saveImg, uploadImg;
    JLabel titleLabel, descLabel, fileLabel, typeLabel, dateLabel, uploadLabel, authorsLabel, contactsLabel, addedLabel;
    ContactList contactList, addedContacts;
    JComboBox selectTypeComboBox;
    JTextArea descriptionTextArea;
    JTextField titleField, searchField;
    Font heading;
    JDatePanel publishDatePanel;
    JList attachedFileList;

    UploadPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

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
        contextPanel.setLayout(new GridLayout(1,2));
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
        String[] keywords = {"Java", "IntelliJ", "UX", "HCI", "Interactive Computer Systems","Persona", "Grokkability"};
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

        contactList = new ContactList();
        contactListScroll = new JScrollPane(contactList);
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(contactListScroll, c);
        contactsListPanel.add(contactListScroll);

        addedContacts = new ContactList();
        addedListScroll = new JScrollPane(addedContacts);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(addedListScroll, c);
        contactsListPanel.add(addedListScroll);

        gridBag.setConstraints(contactsListPanel, c);
        contactsPanel.add(contactsListPanel);

        saveBtn = new JButton("Save");
        uploadPanel.add(saveBtn);

        uploadBtn = new JButton("Upload");
        uploadPanel.add(uploadBtn);

        rightPanel.add(contactsPanel, BorderLayout.NORTH);
        rightPanel.add(uploadPanel, BorderLayout.SOUTH);
        add(leftPanel);
        add(rightPanel);
        //Still very structure according to the contact Button
        addComponentListener(new ComponentAdapter() {
            public void componentResized (ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int font = 16;

                if(windowWidth <= 600){
                    font = 0;
                }

                if(windowWidth < 800){
                    int width = (int)(windowWidth*0.0625);

                    //Since addNew, edit and delete buttons have an image to the left of text, the font can be displayed longer
                    if(windowWidth < 500) {
                        buttonProperties(backBtn, backImg, width, windowHeight, 0);
                        buttonProperties(resetBtn, resetImg, width, windowHeight, 0);
                        buttonProperties(saveBtn, saveImg, width, windowHeight, 0);
                        buttonProperties(uploadBtn, uploadImg, width, windowHeight, 0);
                    }else{
                        buttonProperties(backBtn, backImg, width, windowHeight, 16);
                        buttonProperties(resetBtn, resetImg, width, windowHeight, 16);
                        buttonProperties(saveBtn, saveImg, width, windowHeight, 16);
                        buttonProperties(uploadBtn, uploadImg, width, windowHeight, 16);
                    }

                }else {
                    buttonProperties(backBtn, backImg, 50, 50, 16);
                    buttonProperties(resetBtn, resetImg, 50, 50, 16);
                    buttonProperties(saveBtn, saveImg, 50, 50, 16);
                    buttonProperties(uploadBtn, uploadImg, 50, 50, 16);
                }
            }
        });
    }

    private void buttonProperties(JButton btn, Image img, int width, int height, int fontSize){
        img = img.getScaledInstance(Integer.min(width, height), -1, Image.SCALE_DEFAULT);
        btn.setIcon(new ImageIcon(img));

        btn.setMargin(new Insets(0, (int)(width*0.25), 0, (int)(width*0.25)));
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setFocusPainted(false);
    }

    public void actionPerformed(ActionEvent e){
        cardLayout.show(cardPane, "Home");
    }
}
