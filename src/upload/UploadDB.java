package upload;

import contacts.Contact;
import log.LogDB;

import java.sql.*;

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

    public static void addExistingUpload(Upload u, boolean isUpload, boolean isExisting){
        String sqlUpload, sqlAuthors, sqlFiles;
        if(u.getUploadID() != 0){
            sqlUpload = "UPDATE uploads SET Title = ?,Description = ?, type = ?, Date = ?, cv = ?, resGate = ?, orcid = ?, inst = ?, publ = ?, wos = ?, gSch = ?, linIn = ?, scopus = ?, pure = ?, acad = ?, twit = ?, isUploaded = ? WHERE upload_ID = ?";
            sqlAuthors = "UPDATE upload_Authors SET contact_ID = ?, nameUsed = ? WHERE upload_ID = ?";
            sqlFiles = "UPDATE upload_Files SET File = ? WHERE upload_ID = ?";
        }else{
            sqlUpload = "INSERT INTO uploads(Title,Description, type, Date, cv, resGate, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit, isUploaded) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            sqlAuthors = "INSERT INTO upload_Authors(contact_ID, nameUsed, upload_ID) VALUES(?,?,?)";
            sqlFiles = "INSERT INTO upload_Files(File, upload_ID) VALUES(?,?)";

        }
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
            if(isUpload) {
                pstmt.setBoolean(17, true);
            }else{
                pstmt.setBoolean(17, false);
            }
            //If the upload already exists then the update requires a primary key, uploadID
            if(u.getUploadID() != 0){
                pstmt.setInt(18, u.uploadID);
            }

            pstmt.executeUpdate(); //Execute the statment
            ResultSet rs = pstmt.getGeneratedKeys(); //generated keys has the row that was entered
            // if we are working with an upload that didn't exist before
            if (rs.next() && u.getUploadID() == 0) { //if we are working with an upload that didn't exist before
                u.setUploadID(rs.getInt(1)); //Allows for authors and files to be associated with upload
            }
            //add in each contact into upload_Authors, associating it with the upload.
            for (int i = 0; i < u.addedContacts.getSize(); i++) {
                PreparedStatement pstmtAuthors = conn.prepareStatement(sqlAuthors);
                Contact c = (Contact) u.addedContacts.getElementAt(i);
                pstmtAuthors.setInt(1, c.getContact_ID());
                pstmtAuthors.setBoolean(2, true);
                pstmtAuthors.setInt(3, u.uploadID);
                pstmtAuthors.executeUpdate();
            }
            //add in each file into upload_Files, associating it with the upload.
            for (int i = 0; i < u.attachedFiles.getSize(); i++) {
                PreparedStatement pstmtFiles = conn.prepareStatement(sqlFiles);
                pstmtFiles.setString(1, u.attachedFiles.getElementAt(i).toString());
                pstmtFiles.setInt(2, u.uploadID);
                pstmtFiles.executeUpdate();
            }
            if(isUpload) {
                LogDB.logUploadedUpload(u.uploadID);
            }else{
                LogDB.logSavedUpload(u.uploadID);
            }

        } catch (SQLException et) {
            System.out.println(et.getMessage());
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
