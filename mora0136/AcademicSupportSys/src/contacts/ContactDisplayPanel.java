package contacts;

import panel.ComProps;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/*
 This Panel is responsible for the displaying of the data received from ContactDB after a request was made from
 ContactPanel. Some dynamic elements include email and phone validation. This view is portable and is used for
 displaying a contact any time it is necessary. For example inside ContactPanel and HistoryPanel when a contact
 is selected.
 */
public class ContactDisplayPanel extends JPanel{
    JTextField givenNameField, lastNameField, emailField, phoneField;
    Contact c;
    int mainFont = 32;
    Insets top, bottom;

    public ContactDisplayPanel(Contact c){
        this();
        setContact(c);
    }

    public ContactDisplayPanel(){
        //Output area when a contact is selected or a new one is to added
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridBag);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        top = new Insets(10, 10, 0, 0);
        bottom = new Insets(0, 50, 10, 10);

        JLabel givenName = new JLabel("Name:");
        c.insets = top;
        gridBag.setConstraints(givenName, c);
        add(givenName);
        givenNameField = new JTextField();
        givenNameField.setEditable(false);
        c.insets = bottom;
        gridBag.setConstraints(givenNameField, c);
        add(givenNameField);

        JLabel lastName = new JLabel("Surname:");
        c.insets = top;
        gridBag.setConstraints(lastName, c);
        add(lastName);
        lastNameField = new JTextField();
        lastNameField.setEditable(false);
        c.insets = bottom;
        gridBag.setConstraints(lastNameField, c);
        add(lastNameField);

        JLabel email = new JLabel("Email:");
        c.insets = top;
        gridBag.setConstraints(email, c);
        add(email);
        emailField = new JTextField();
        emailField.setEditable(false);
        c.insets = bottom;
        gridBag.setConstraints(emailField, c);
        add(emailField);

        JLabel phone = new JLabel("Phone:");
        c.insets = top;
        gridBag.setConstraints(phone, c);
        add(phone);
        phoneField = new JTextField();
        phoneField.setEditable(false);
        c.insets = bottom;
        gridBag.setConstraints(phoneField, c);
        add(phoneField);

        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            @Override
            public void changedUpdate(DocumentEvent e) {            }
        });

        phoneField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validatePhone(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validatePhone(); }
            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getParent().getParent().getWidth();
                int windowHeight = getParent().getParent().getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int borderHeight = 40;
                int borderWidth = 20;

                if (windowWidth < 1000 || windowHeight < 500) {
                    borderHeight = (int) (borderHeight / (1000/40));
                    borderWidth = (int)(windowWidth / (500/20));
                    headerFont = (int)(Double.min(windowWidth /(1000/headerFont), windowHeight/(500/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(1000/listFont), windowHeight/(500/listFont)));
                }

                setBorder(BorderFactory.createEmptyBorder(borderHeight, borderWidth, borderHeight, borderWidth));

                ComProps.headingProperties(givenName, headerFont);
                ComProps.headingProperties(lastName, headerFont);
                ComProps.headingProperties(email, headerFont);
                ComProps.headingProperties(phone, headerFont);

                ComProps.textFieldProperties(givenNameField, listFont);
                ComProps.textFieldProperties(lastNameField, listFont);
                ComProps.textFieldProperties(emailField, listFont);
                ComProps.textFieldProperties(phoneField, listFont);
            }
        });
    }

    public void setMainFont(int size) {
        mainFont = size;
    }

    public void setContact(Contact c){
        this.c = c;
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

    public void setEditable(boolean isEdit){
        givenNameField.setEditable(isEdit);
        lastNameField.setEditable(isEdit);
        emailField.setEditable(isEdit);
        phoneField.setEditable(isEdit);
    }


    public void setTextEmpty(){
        givenNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    public String[] getTextFields(){
        return new String[]{givenNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText()};
    }

    public void validateEmail(){
        if(!isValidEmail()){
            emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }else{
            emailField.setBorder(UIManager.getBorder("TextField.border"));
        }
    }

    public void validatePhone(){
        if(!isValidPhone()){
            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        }else{
            phoneField.setBorder(UIManager.getBorder("TextField.border"));
        }
    }

    public boolean isValidEmail(){
        if(emailField.getText().isEmpty()) {
            return true;
        }
        String regexTut = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return emailField.getText().matches(regexTut);
    }

    public boolean isValidPhone(){
        if(phoneField.getText().isEmpty()) {
            return true;
        }
        String regex = "^(?=[0-9]*$)(?:.{8}|.{10})$";
        return phoneField.getText().matches(regex);
    }
}