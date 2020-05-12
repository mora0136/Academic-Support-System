package contacts;

import panel.ComProps;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int headerFont = mainFont;
                int listFont = (int)(mainFont*(0.75));
                int bodyFont = (int)(mainFont*(0.5));
                int checkBoxFont = (int)(mainFont*(0.75));
                int width = 50;
                int height = 100;
                int heightInsets = 40;

                if (windowWidth < 650 || windowHeight < 450) {
                    height = (int) (windowWidth / (650/100));
                    heightInsets = (int)(windowWidth / (650/40));
                    System.out.println(height+" "+width);
                    System.out.println(windowHeight+" "+ windowWidth);
                    headerFont = (int)(Double.min(windowWidth /(650/headerFont), windowHeight/(450/headerFont)));
                    listFont = (int)(Double.min(windowWidth/(650/listFont), windowHeight/(450/listFont)));
                    bodyFont = (int)(Double.min(windowWidth/(650/bodyFont), windowHeight/(450/bodyFont)));
                    checkBoxFont = (int)(Double.min(windowWidth/(650/checkBoxFont), windowHeight/(450/checkBoxFont)));
                }

                setBorder(new EmptyBorder(height, 0, height, 0));

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
}
