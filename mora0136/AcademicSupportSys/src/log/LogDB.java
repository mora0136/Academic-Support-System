package log;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 *  LogDb is responsible for communicating with the database and handling requests from the GUI. As such it should not
 *  be instantiated, this class is designated as a getter and giver of information, it does not keep it. This is the
 *  preferred approach as opposed to ContactDB. The Logs being track are Upload: Saved, Deleted, Uploaded and
 *  Contact:Added, Saved, Deleted
 */

public final class LogDB {
    private LogDB(){

    }

    //Logging upload interactions
    public static int logSavedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Upload");
            pstmt.setString(3, "Saved");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int logUploadedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Upload");
            pstmt.setString(3, "Uploaded");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int logDeletedUpload(int uploadID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Upload");
            pstmt.setString(3, "Deleted");
            pstmt.setInt(4, uploadID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Logging contact interactions
    public static int logSavedContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Contact");
            pstmt.setString(3, "Saved");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int logDeletedContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Contact");
            pstmt.setString(3, "Deleted");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int logNewContact(int contactID){
        String sql = "INSERT INTO log(date, type, action, associate_ID) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Contact");
            pstmt.setString(3, "New");
            pstmt.setInt(4, contactID);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    //Day represents the first millisecond of that day, so adding a day gets the range for that single day
    public static List<Log> getLogsForDay(LocalDate day){
        String sql = "SELECT * FROM log WHERE date >= ? AND date < ? ORDER BY date DESC";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(day));
            pstmt.setDate(2, Date.valueOf(day.plusDays(1))); // less
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

    public static List<Log> getLogsForDayWithTypeFilter(LocalDate day, String type){
        String sql = "SELECT * FROM log WHERE date >= ? AND date < ? AND type = ? ORDER BY date DESC";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(day)); // less
            pstmt.setDate(2, Date.valueOf(day.plusDays(1))); // less
            pstmt.setString(3, type);
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

    public static List<Log> getLogsForDayWithTypeActionFilter(LocalDate day, String type, String action){
        String sql = "SELECT * FROM log WHERE date >= ? AND date < ? AND type = ? AND action = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(day)); // less
            pstmt.setDate(2, Date.valueOf(day.plusDays(1))); // less
            pstmt.setString(3, type);
            pstmt.setString(4, action);
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
