package contacts;

import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ContactList extends JPanel {
    HashMap<Integer, Contact> listOfContacts; // be aware that the int saved to the button, but this identifies index using a contactID
    JTextField givenNameField;
    JTextField lastNameField;
    JTextField emailField;
    JTextField phoneField;
    SuffixTrie sf; // stores names of contacts exclusively for searching purposes, does not contain contacts data
    Connection connection;
    int contactSelected = 0;
    public ContactList(){
//        connect();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        extractContactsFromDB();
        //Should be in selectAll
    }

    public void extractContactsFromDB(){
        removeAll();
        revalidate();
        repaint();

        String sql = "SELECT * FROM contacts";

        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);){
            connection = conn;
            sf = new SuffixTrie();
            listOfContacts = new HashMap<>();
            while(rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"), rs.getString("surname"), rs.getString("email"), rs.getString("phone"));
                sf.insert(rs.getString("givenName") + " " + rs.getString("surname"), c);
                listOfContacts.put(rs.getInt("contact_ID"), c);
                add(contactButton(c)); // nessecary
            }
        }catch(SQLException e){
            System.out.println(e);
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
    public void searchForContact(String search){
        removeAll();
        revalidate();
        repaint();
        System.out.println(search);

        if(search.length() == 0 || search == "Search..."){
            for(Map.Entry<Integer, Contact> entry: listOfContacts.entrySet()) {
                add(contactButton(entry.getValue()));
            }
        }else {
            ArrayList<SuffixIndex> startIndexes;
            ArrayList<Contact> displayedContacts = new ArrayList<>();
            SuffixTrieNode sn = sf.get(search);
            if(sn != null) {
                startIndexes = sn.getData().getStartIndexes();
                for (SuffixIndex s : startIndexes) {
                    if (!displayedContacts.contains(s.getContact())) {
                        displayedContacts.add(s.getContact());
                        add(contactButton(s.getContact()));
                    }
                }
            }else{
                JLabel noContent = new JLabel("Contact not Found");
                noContent.setFont(new Font("Arial", Font.PLAIN, 16));
                add(noContent);
            }
        }
    }

    public JButton contactButton(Contact c){
        JButton btn = new JButton(c.getName()+" "+c.getSurname());
        btn.setMaximumSize(new Dimension(1000, 500));
        btn.setMinimumSize(new Dimension(80, 400));
        btn.setMargin(new Insets(25, 0, 25, 0));
        btn.setFont(new Font("Arial", Font.PLAIN, 32));
        btn.addActionListener(this::actionPerformed);
        btn.setActionCommand(String.valueOf(c.getContact_ID()));
        return btn;
    }

    public void editContact(String givenName, String surname, String email, String phone){
        // execute edit in database
//        String sql = "UPDATE contacts SET givenName = '"+givenName+"', surname = '"+surname+"', email = '"+email+"', phone = '"+phone+"' WHERE contact_ID = "+contactSelected;
//        try(Connection conn = this.connect();
//            Statement stmt  = conn.createStatement();
//            ResultSet rs    = stmt.executeUpdate(sql);) {
//            generateDataStructures(rs);
//        }catch(SQLException e){
//            System.out.println(e);
//        }

        String sql = "UPDATE contacts SET givenName = ? , "
                + "surname = ?, "
                + "email = ?, "
                + "phone = ? "
                + "WHERE contact_ID = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, givenName);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setInt(5, contactSelected);
            // update
            pstmt.executeUpdate();
            extractContactsFromDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e){
        System.out.println(e.getActionCommand());
        contactSelected = Integer.parseInt(e.getActionCommand());
        Contact c = listOfContacts.get(Integer.parseInt(e.getActionCommand()));
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

}
