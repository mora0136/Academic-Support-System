package contacts;

import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

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
    SuffixTrie sf; // stores names of contacts exclusively for searching purposes, does not contain contacts data
    public ContactList(){
        connect();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        listOfContacts = new ArrayList<Contact>();
        //Should be in selectAll
        String sql = "SELECT * FROM contacts";
        sf = new SuffixTrie();

        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);){
            while(rs.next()) {
                Contact c = new Contact(rs.getString("givenName"), rs.getString("surname"), rs.getString("email"), rs.getString("phone"));
                sf.insert(rs.getString("givenName") + " " + rs.getString("surname"), c);
                listOfContacts.add(c);
                add(contactButton(c));
            }
//            searchForContact("");
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
        if(search.length() == 0){
            for(int i = 0; i < listOfContacts.size(); i++){
                add(contactButton((Contact)listOfContacts.get(i)));
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
        return btn;
    }

    public void actionPerformed(ActionEvent e){
        System.out.println(e.getActionCommand());
        Contact c = new Contact(null, null, null, null);
        SuffixTrieNode sn = sf.get(e.getActionCommand().split("")[0]);//this ensure it only searches for first name
        ArrayList< SuffixIndex > startIndexes = new ArrayList<>();
        startIndexes = sn.getData().getStartIndexes();
        for(SuffixIndex s : startIndexes){
            c = s.getContact();
        }
//        Contact c = (Contact) listOfContacts.get(Integer.parseInt(e.getActionCommand()));
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

}
