package contacts;

import suffixtree.SuffixIndex;
import suffixtree.SuffixTrie;
import suffixtree.SuffixTrieNode;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

/*
ContactDB is responsible for the communication between the GUI and the database. Other 'DB' classes have static
implementations of their methods as opposed to this one. The way to use ContactDB is to create an instance of it,
and then you can start communicating with the database via the contained methods. As can be seen instance variables are
present inside here, so anything received from the database needs to be stored here and in a location for the GUI to
access it. This implementation is flawed and a better option was later chosen, and is observable in UploadDB, TemplateDB and
LogDB. As it stands, the class works despite some of the flaws this approach has, so it will be staying this way.
 */

public class ContactDB {
    SuffixTrie sf; // stores names of contacts exclusively for searching purposes, does not contain contacts data
    DefaultListModel<Contact> listModel;

    public ContactDB(){
        extractContactsFromDB();
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    /**
     * Will Query the database to get all the contacts. The contacts are then added to the suffixTrie and saved to listModel
     */
    public void extractContactsFromDB(){

        String sql = "SELECT * FROM contacts WHERE isDeleted = false ORDER BY givenName";

        try(Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            sf = new SuffixTrie();
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
    /**
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
        // searchField defaults to Search... when focus is lost, to ensure the SuffixTrie does not search for this term
        // and display no results a catch is here to prevent this error
        if(search.length() == 0 || search.equals("Search...")){
            extractContactsFromDB();
        }else {
            ArrayList<SuffixIndex> startIndexes;
            listModel = new DefaultListModel<>();
            SuffixTrieNode sn = sf.get(search);
            if(sn != null) {
                startIndexes = sn.getData().getStartIndexes();
                for (SuffixIndex s : startIndexes) {
                    //Prevents duplicate contacts being displayed as a node may contain many subStrings of itself
                    if (!listModel.contains(s.getObj())) {
                        listModel.addElement((Contact) s.getObj());
                    }
                }

            }else{
                //When no contacts are found, display a dummy contact. In the panel this is not selectable.
                listModel = new DefaultListModel<>();
                listModel.addElement(new Contact(-1,"No Related Contacts Found", "", "", ""));
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
    public int addContact(String givenName, String surname, String email, String phone){
        String sql = "INSERT INTO contacts(givenName,surname, email, phone) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, givenName);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            extractContactsFromDB();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    /**
     * This will delete the contact currently selected. The currently selected contact is determined by
     * the last contact that was pressed. If no contact has ever been pressed, then the option should
     * never be presented to the user
     */
    public void deleteContact(int contact_ID){
        String sql = "UPDATE contacts SET isDeleted = ? WHERE contact_ID = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, contact_ID);
            // execute the delete statement
            pstmt.executeUpdate();
            extractContactsFromDB();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This will query the database for the details of an individual contact
     * @param contactID The Contact ID of the contact to search for
     * @return an instance of the contact
     */
    public Contact getContactDetails(int contactID){
        String sql = "SELECT * FROM contacts WHERE contact_ID = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactID);
            ResultSet rs = pstmt.executeQuery();

            return new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                    rs.getString("surname"), rs.getString("email"),
                    rs.getString("phone"));


        }catch(SQLException e){
            return null;
        }

    }

}
