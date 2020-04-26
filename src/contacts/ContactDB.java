package contacts;

import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.sql.*;

public class ContactDB {
    SuffixTrie sf; // stores names of contacts exclusively for searching purposes, does not contain contacts data
    DefaultListModel<Contact> listModel;

    public ContactDB(){
        extractContactsFromDB();
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public void extractContactsFromDB(){

        String sql = "SELECT * FROM contacts";

        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            sf = new SuffixTrie();
//            listOfContacts = new HashMap<>();
            listModel = new DefaultListModel();
            while(rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                        rs.getString("surname"), rs.getString("email"),
                        rs.getString("phone"));
                sf.insert(rs.getString("givenName") + " " + rs.getString("surname"), c);
                listModel.addElement(c);
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

    /**
     * Will search the SuffixTrie for names that could associate with the search term and add the results to the ContactPanel
     * @param search The search term to find in the Suffix Trie
     */
    public void searchForContact(String search){
        //JtextArea defaults to Search... when focus is lost, to ensure the SuffixTrie does not search for this term
        // and display no results a catch is here to prevent this error
        if(search.length() == 0 || search.equals("Search...")){
            extractContactsFromDB();
        }else {
            ArrayList<SuffixIndex> startIndexes;
            SuffixTrieNode sn = sf.get(search);
            if(sn != null) {
                startIndexes = sn.getData().getStartIndexes();
                for (SuffixIndex s : startIndexes) {
                    //Prevents duplicate contacts being displayed as a node may contain many subStrings of itself
                    if (!listModel.contains(s.getContact())) {
                        listModel.addElement(s.getContact());
                    }
                }

            }else{
                //This is where an error message should be returned as nothing found.
                JLabel noContent = new JLabel("Contact not Found");
                noContent.setFont(new Font("Arial", Font.PLAIN, 16));
            }
        }
    }

    /**
     * When a contact is edited and a save is requested, this will update the database, regenerate the
     * contact lists and display all contacts
     * @param givenName Edited givenName of the contact
     * @param surname Edited surname of the contact
     * @param email Edited email of the contact
     * @param phone Edited phone of the contact
     */
    public void updateContact(String givenName, String surname, String email, String phone, int contact_ID){
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
            pstmt.setInt(5, contact_ID);
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
    public void deleteContact(int contact_ID){
        String sql = "DELETE FROM contacts WHERE contact_ID = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, contact_ID);
            // execute the delete statement
            pstmt.executeUpdate();
            extractContactsFromDB();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
