package upload;

import contacts.Contact;
import log.LogDB;

import javax.swing.*;
import java.sql.*;

/*
 * A talker between the GUI and database for uploads.
 */


public class UploadDB {

    public static Upload getUpload(int uploadID){
        String sql = "SELECT * FROM uploads " +
                "WHERE upload_ID = "+ uploadID;

        String sqlSelectAuthors = "SELECT * FROM upload_Authors " +
                "JOIN contacts ON upload_Authors.contact_ID = contacts.contact_ID " +
                "WHERE upload_ID = "+ uploadID;

        String sqlSelectFiles = "SELECT * FROM upload_Files " +
                "WHERE upload_ID = "+ uploadID;
        try(Connection conn = connect();
            Statement stmt  = conn.createStatement();) {

            //Retrieve the information found in the Uploads tables
            ResultSet rs = stmt.executeQuery(sql);
            Upload u = new Upload(rs);

            //Retrieve the information found in the authors table
            rs = stmt.executeQuery(sqlSelectAuthors);
            u.setAuthors(rs);

            //Retrieve the information found in the upload_Files table.
            rs = stmt.executeQuery(sqlSelectFiles);
            u.setFiles(rs);

            return u;
        }catch(SQLException e){
            System.out.println(e);
        }
        return null;
    }

    public static int addExistingUpload(Upload u, boolean isUpload, boolean isExisting){
        String sqlUpload, sqlAuthors, sqlFiles, deleteAuthors, deleteFiles;
        if(isExisting){
            sqlUpload = "UPDATE uploads SET Title = ?,Description = ?, type = ?, Date = ?, cv = ?, resGate = ?, orcid = ?, inst = ?, publ = ?, wos = ?, gSch = ?, linIn = ?, scopus = ?, pure = ?, acad = ?, twit = ?, isUploaded = ? WHERE upload_ID = ?";
        }else{
            sqlUpload = "INSERT INTO uploads(Title,Description, type, Date, cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit, isUploaded) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        }
        sqlAuthors = "INSERT INTO upload_Authors(contact_ID, nameUsed, upload_ID) VALUES(?,?,?)";
        sqlFiles = "INSERT INTO upload_Files(File, upload_ID) VALUES(?,?)";
        deleteAuthors = "DELETE FROM upload_Authors WHERE upload_ID = "+u.getUploadID();
        deleteFiles = "DELETE FROM upload_Files WHERE upload_ID = "+u.getUploadID();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpload, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, u.getTitle());
            pstmt.setString(2, u.getDescription());
            pstmt.setString(3, String.valueOf(u.getType()));
            if(u.getDate() != null) {
                pstmt.setDate(4, Date.valueOf(u.getDate()));
            }
            pstmt.setBoolean(5, u.isCv());pstmt.setBoolean(6, u.isResGate());pstmt.setBoolean(7, u.isOrcid());
            pstmt.setBoolean(8, u.isInst());pstmt.setBoolean(9, u.isPubl());pstmt.setBoolean(10, u.isWos());
            pstmt.setBoolean(11, u.isgSch());pstmt.setBoolean(12, u.isLinIn());pstmt.setBoolean(13, u.isScopus());
            pstmt.setBoolean(14, u.isPure());pstmt.setBoolean(15, u.isAcad());pstmt.setBoolean(16, u.isTwit());
            //isUpload column defines if it has been uploaded or saved, so if the upload btn is pressed, then true
            pstmt.setBoolean(17, isUpload);
            //If the upload already exists then the update requires a primary key, uploadID
            if(isExisting){
                pstmt.setInt(18, u.getUploadID());
            }

            pstmt.executeUpdate(); //Execute the statement
            ResultSet rs = pstmt.getGeneratedKeys(); //generated keys has the row that was entered
            // if we are working with an upload that didn't exist before
            if (rs.next() && u.getUploadID() == 0) { //if we are working with an upload that didn't exist before
                u.setUploadID(rs.getInt(1)); //Allows for authors and files to be associated with upload
            }

            //Delete any previous upload. This method is inconsistent with the approach of setting the file to deleted.
            //But this is done to make this system easier to implement.
            PreparedStatement pstmtDeleteAuthors = conn.prepareStatement(deleteAuthors);
            pstmtDeleteAuthors.executeUpdate();
            PreparedStatement pstmtDeleteFiles = conn.prepareStatement(deleteFiles);
            pstmtDeleteFiles.executeUpdate();

            //Delete all previous contacts in db before
            //add in each contact into upload_Authors, associating it with the upload.
            for (int i = 0; i < u.addedContacts.getSize(); i++) {
                PreparedStatement pstmtAuthors = conn.prepareStatement(sqlAuthors);
                Contact c = (Contact) u.addedContacts.getElementAt(i);
                pstmtAuthors.setInt(1, c.getContact_ID());
                pstmtAuthors.setBoolean(2, true);
                pstmtAuthors.setInt(3, u.getUploadID());
                pstmtAuthors.executeUpdate();
            }

            //Delete all previous files in db before

            //add in each file into upload_Files, associating it with the upload.
            for (int i = 0; i < u.attachedFiles.getSize(); i++) {
                PreparedStatement pstmtFiles = conn.prepareStatement(sqlFiles);
                pstmtFiles.setString(1, u.attachedFiles.getElementAt(i).toString());
                pstmtFiles.setInt(2, u.getUploadID());
                pstmtFiles.executeUpdate();
            }
            if(isUpload) {
                LogDB.logUploadedUpload(u.getUploadID());
            }else{
                LogDB.logSavedUpload(u.getUploadID());
            }

        } catch (SQLException et) {
            System.out.println(et.getMessage());
        }
        return u.getUploadID();
    }

    public static DefaultListModel getAllEditableUploads(){
        String sql = "SELECT * FROM uploads WHERE isUploaded = false AND isDeleted = false";
        DefaultListModel<Upload> editable = new DefaultListModel<>();
        try(Connection conn = connect();
            Statement stmt  = conn.createStatement();) {

            //Retrieve the information found in the Uploads tables
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                editable.addElement(new Upload(rs));
            }
        }catch(SQLException ev){

        }
        return editable;
    }

    public static void deleteUpload(Upload upload){
        String sqlUploads = "UPDATE uploads SET isDeleted = ? WHERE upload_ID = ?";

        int uploadID = upload.getUploadID();

        try (Connection conn = connect()) {
            PreparedStatement pstmtUpload = conn.prepareStatement(sqlUploads);
            pstmtUpload.setBoolean(1, true);
            pstmtUpload.setInt(2, uploadID);
            pstmtUpload.executeUpdate();
        } catch (SQLException ev) {
            System.out.println(ev.getMessage());
        }
    }

    private static Connection connect() {
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
}
