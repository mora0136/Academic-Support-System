package contacts;

import javax.swing.*;

public class ContactDisplayPanel extends JPanel{
    JTextField givenNameField, lastNameField, emailField, phoneField;
    Contact c;

    public ContactDisplayPanel(Contact c){
        this();
        setContact(c);
    }
    public ContactDisplayPanel(){
        //Output area when a contact is selected or a new one is to added
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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

        add(givenName); add(givenNameField);
        add(lastName); add(lastNameField);
        add(email); add(emailField);
        add(phone); add(phoneField);
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
