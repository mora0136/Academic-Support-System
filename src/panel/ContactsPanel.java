package panel;

import contacts.ContactList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ContactsPanel extends JPanel implements DocumentListener, FocusListener{
    JPanel cardPane, leftPanel, rightPanel, contextPanel, displayPanel, optionPanel;
    Image backImg, searchImg, addNewImg, editImg, deleteImg, saveImg, tempImg;
    JTextField givenNameField, lastNameField, emailField, phoneField;
    JScrollPane scrollContactPanel;
    JTextField searchField;
    ContactList contactList;
    CardLayout cardLayout;
    JButton edit, delete;

    ContactsPanel(JPanel pane) throws IOException {
        this.cardPane = pane;
        this.cardLayout = (CardLayout)pane.getLayout();

        leftPanel = new JPanel();
        rightPanel = new JPanel();
        contextPanel = new JPanel();
        displayPanel = new JPanel();
        optionPanel = new JPanel();
        contactList = new ContactList();

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

        JButton addNew = new JButton("Add New");
        addNew.addActionListener(this::actionPerformed);
        addNewImg = ImageIO.read(new File("resources/add.png"));

        edit = new JButton("Edit");
        edit.setEnabled(false);
        edit.addActionListener(this::actionPerformed);
        editImg = ImageIO.read(new File("resources/edit_contact.png"));
        tempImg = editImg;
        saveImg = ImageIO.read(new File("resources/save.png"));

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

        //Allow the contact list to have a scrollable list
        contactList.setLayout(new BoxLayout(contactList, BoxLayout.Y_AXIS));
        scrollContactPanel = new JScrollPane(contactList);

        //Output area when a contact is selected or a new one is to added
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        JLabel givenName = new JLabel("Name");
        givenNameField = new JTextField();
        givenNameField.setEditable(false);

        JLabel lastName = new JLabel("Surname");
        lastNameField = new JTextField();
        lastNameField.setEditable(false);

        JLabel email = new JLabel("Email");
        emailField = new JTextField();
        emailField.setEditable(false);

        JLabel phone = new JLabel("Phone");
        phoneField = new JTextField();
        phoneField.setEditable(false);

        contactList.setOutputPanel(givenNameField, lastNameField, emailField, phoneField, edit, delete);
        displayPanel.add(givenName); displayPanel.add(givenNameField);
        displayPanel.add(lastName); displayPanel.add(lastNameField);
        displayPanel.add(email); displayPanel.add(emailField);
        displayPanel.add(phone); displayPanel.add(phoneField);

        optionPanel.setLayout(new GridLayout(1, 2));
        optionPanel.add(edit); optionPanel.add(delete);

        leftPanel.add(contextPanel, BorderLayout.NORTH);
        leftPanel.add(scrollContactPanel, BorderLayout.CENTER);
        leftPanel.add(addNew, BorderLayout.SOUTH);

        rightPanel.add(displayPanel, BorderLayout.NORTH);
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
                        buttonProperties(addNew, addNewImg, width, windowHeight, 0, true);
                        buttonProperties(edit, editImg, width, windowHeight, 0, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 0, true);
                    }else{
                        buttonProperties(addNew, addNewImg, width, windowHeight, 16, true);
                        buttonProperties(edit, editImg, width, windowHeight, 16, true);
                        buttonProperties(delete, deleteImg, width, windowHeight, 16, true);
                    }

                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(Integer.min(width, windowHeight), -1, Image.SCALE_DEFAULT);

                }else{
                    buttonProperties(back, backImg, 50, 50, font, false);
                    buttonProperties(addNew, addNewImg, 50, 50, font, true);
                    buttonProperties(edit, editImg, 50, 50, font, true);
                    buttonProperties(delete, deleteImg, 50, 50, font, true);
                    searchField.setFont(new Font("Arial", Font.PLAIN, Integer.max(font*2, 16)));
                    saveImg = saveImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
                    tempImg = tempImg.getScaledInstance(50, -1, Image.SCALE_DEFAULT);
                }
            }
        });
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
            case "Add New":
                contactList.setContactSelected(0);
                givenNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                edit.setEnabled(true);
            case "Edit":
                givenNameField.setEditable(true);
                lastNameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);
                edit.setText("Save");
//                tempImg = editImg;
                editImg = saveImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
            case "Save":
                givenNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);
                if(contactList.getContactSelected() == 0){
                    contactList.addContact(givenNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText());
                    edit.setEnabled(false);
                    delete.setEnabled(false);
                    givenNameField.setText("");
                    lastNameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                }else {
                    contactList.updateContact(givenNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText());
                }
                edit.setText("Edit");
                editImg = tempImg;
                edit.setIcon(new ImageIcon(editImg));
                break;
            case "Delete":
                contactList.deleteContact();
                delete.setEnabled(false);
                edit.setEnabled(false);
                givenNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                givenNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);
//                contactList.setContactSelected(0);
                break;
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
        contactList.searchForContact(searchField.getText());
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