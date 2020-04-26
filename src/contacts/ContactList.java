package contacts;

import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ContactList extends JPanel {
    HashMap<Integer, Contact> listOfContacts; // be aware that the int saved to the button, but this identifies index using a contactID
    JTextField givenNameField, lastNameField, emailField, phoneField;
    SuffixTrie sf; // stores names of contacts exclusively for searching purposes, does not contain contacts data
    int contactSelected = 0;
    JButton edit, delete; //Will be borrowed from the Panel to allow for this class to edit them.


    public ContactList(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        extractContactsFromDB();
    }

    public void extractContactsFromDB(){
        removeAll();
        revalidate();
        repaint();

        String sql = "SELECT * FROM contacts";

        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            sf = new SuffixTrie();
            listOfContacts = new HashMap<>();
            while(rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                        rs.getString("surname"), rs.getString("email"),
                        rs.getString("phone"));
                sf.insert(rs.getString("givenName") + " " + rs.getString("surname"), c);
                listOfContacts.put(rs.getInt("contact_ID"), c);
                add(contactButton(c));
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    /*
    Validates and connects to the local database located in url.
     */
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

    //may still need, up in the air so will keep for prosperity sake
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

    //Associate the desired output location of the information contained within contactlist.
    //Allows for buttons press to influence other panels.
    public void setOutputPanel(JTextField givenNameField, JTextField lastNameField, JTextField emailField,
                               JTextField phoneField, JButton edit, JButton delete){
        this.givenNameField = givenNameField;
        this.lastNameField = lastNameField;
        this.emailField = emailField;
        this.phoneField = phoneField;
        this.edit = edit;
        this.delete = delete;
    }

    /**
     * Will search the SuffixTrie for names that could associate with the search term and add the results to the ContactPanel
     * @param search The search term to find in the Suffix Trie
     */
    public void searchForContact(String search){
        removeAll();
        revalidate();
        repaint();
        //JtextArea defaults to Search... when focus is lost, to ensure the SuffixTrie does not search for this term
        // and display no results a catch is here to prevent this error
        if(search.length() == 0 || search.equals("Search...")){
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
                    //Prevents duplicate contacts being displayed as a node may contain many subStrings of itself
                    if (!displayedContacts.contains(s.getContact())) {
                        displayedContacts.add(s.getContact());
                        add(contactButton(s.getContact()));
                    }
                }
            }else{
                //Still needs to be styled properly
                JLabel noContent = new JLabel("Contact not Found");
                noContent.setFont(new Font("Arial", Font.PLAIN, 16));
                add(noContent);
            }
        }
    }

    /**
     * Creates a button that displays the contacts name and associates a button pres to it+styling
     * (not sure button is best approach)
     * @param c The contact to which the button should be tied to
     * @return The Jbutton associated with the contact
     */
    public JButton contactButton(Contact c){
        JButton btn = new JButton(c.getName()+" "+c.getSurname());
        btn.setMaximumSize(new Dimension(1000, 100));
        btn.setMargin(new Insets(20, 0, 20, 0));
        btn.setFont(new Font("Arial", Font.PLAIN, 32));
        btn.addActionListener(this::actionPerformed);
        btn.setActionCommand(String.valueOf(c.getContact_ID()));
        return btn;
    }

    /**
     * When a contact is edited and a save is requested, this will update the database, regenerate the
     * contact lists and display all contacts
     * @param givenName Edited givenName of the contact
     * @param surname Edited surname of the contact
     * @param email Edited email of the contact
     * @param phone Edited phone of the contact
     */
    public void updateContact(String givenName, String surname, String email, String phone){
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

    /**
     * When a new contact is saved, this will update the database, regenerate the contact lists and display all contacts
     * @param givenName New contact given name
     * @param surname New contact surname
     * @param email New contact email
     * @param phone New contact phone
     */
    public void addContact(String givenName, String surname, String email, String phone){
        String sql = "INSERT INTO contacts(givenName,surname, email, phone) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, givenName);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
            extractContactsFromDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This will delete the contact currently selected. The currently selected contact is determined by
     * the last contact that was pressed. If no contact has ever been pressed, then the option should
     * never be presented to the user
     */
    public void deleteContact(){
        String sql = "DELETE FROM contacts WHERE contact_ID = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, contactSelected);
            // execute the delete statement
            pstmt.executeUpdate();
            extractContactsFromDB();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setContactSelected(int c){
        contactSelected = c;
    }
    public int getContactSelected(){
        return contactSelected;
    }

    /**
     * a contact button which is pressed will use this method to update the display field, current Contact selected
     * and enable the edit and delete buttons
     * @param e Action Event
     */
    public void actionPerformed(ActionEvent e){
        edit.setEnabled(true);
        delete.setEnabled(true);
        contactSelected = Integer.parseInt(e.getActionCommand());
        Contact c = listOfContacts.get(Integer.parseInt(e.getActionCommand()));
        givenNameField.setText(c.getName());
        lastNameField.setText(c.getSurname());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
    }

}
