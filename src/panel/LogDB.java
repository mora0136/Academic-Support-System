package panel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LogDB {
    //return of zero means a successful log was created

    //Logging upload interactions
    static int logSavedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "upload");
            pstmt.setString(3, "Saved");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static int logUploadedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "upload");
            pstmt.setString(3, "Uploaded");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    static int logDeletedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "upload");
            pstmt.setString(3, "Deleted");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Logging contact interactions
    static int logSavedContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "contact");
            pstmt.setString(3, "Saved");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static int logDeletedContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "contact");
            pstmt.setString(3, "Deleted");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static int logNewContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, "contact");
            pstmt.setString(3, "New");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static List<Log> getLogsForDay(LocalDate dateFrom, LocalDate dateTo){
        String sql = "SELECT * FROM log WHERE date >= ? AND date <= ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(dateFrom)); // less
            pstmt.setDate(2, Date.valueOf(dateTo)); // to more
            ResultSet rs = pstmt.executeQuery();
            List<Log> list = new ArrayList<>();
            while(rs.next()){
                list.add(new Log(rs));
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Connection connect() {
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
