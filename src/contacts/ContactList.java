package contacts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.sql.*;

public class ContactList extends JPanel {
    ArrayList listOfContacts; // be aware that the int saved to the button, but this identifies index using a contactID
    JTextField givenNameField;
    JTextField lastNameField;
    JTextField emailField;
    JTextField phoneField;
    public ContactList(){
        connect();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        listOfContacts = new ArrayList<Contact>();
        //Should be in selectAll
        String sql = "SELECT * FROM contacts";
        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);){
            while(rs.next()) {
                Contact c = new Contact(rs.getString("givenName"), rs.getString("surname"), rs.getString("email"), rs.getString("phone"));
                listOfContacts.add(c);
                add(contactButton(c, rs.getInt("contact_ID")));
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    //Search with a term via database, repaint all buttons
    public ContactList(String search){
        System.out.println(search);
    }
    public void searchContacts(String search){

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

    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setOutputPanel(JTextField givenNameField, JTextField lastNameField, JTextField emailField, JTextField phoneField){
        this.givenNameField = givenNameField;
        this.lastNameField = lastNameField;
        this.emailField = emailField;
        this.phoneField = phoneField;
    }
    //Use to implement searching
    public ContactList searchForContact(String search){
        return new ContactList();
    }

    public JButton contactButton(Contact c, int index){
        JButton btn = new JButton(c.getName()+" "+c.getSurname());
        btn.setMaximumSize(new Dimension(1000, 500));
        btn.setMinimumSize(new Dimension(80, 400));
        btn.setMargin(new Insets(25, 0, 25, 0));
        btn.setFont(new Font("Arial", Font.PLAIN, 32));
        btn.addActionListener(this::actionPerformed);
        btn.setActionCommand(String.valueOf(index));
        return btn;
    }

    public void actionPerformed(ActionEvent e){
        System.out.println(e);
        Contact c = (Contact) listOfContacts.get(Integer.parseInt(e.getActionCommand()));
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

}

class Contact{

    String givenName;
    String surname;
    String email;
    String phone;

    public Contact(String givenName, String surname, String email, String phone) {
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }
    public String getName() {
        return givenName;
    }

    public void setName(String name) {
        this.givenName = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}