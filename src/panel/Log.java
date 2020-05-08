package panel;

import contacts.Contact;
import contacts.ContactDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Log {
    int logID;
    LocalDate date;
    String type;
    String action;
    String description;

    Log(int logID, LocalDate time, String type, String action, String description){
        this.logID = logID;
        this.type = type;
        this.date = time;
        this.action = type;
        this.description = description;
    }

    Log(ResultSet rs) throws SQLException {
        this.logID = rs.getInt("log_ID");
        this.date = rs.getDate("date").toLocalDate();
        this.type = rs.getString("type");
        this.action = rs.getString("action");

        //Find associated data for its particular type
        if(this.action == "Upload"){
            int uploadID = rs.getInt("associate_ID");
            description = "Replace with SQL UploadDB Code";
        }else {
            int contactID = rs.getInt("associate_ID");
            ContactDB db = new ContactDB();
            Contact c = db.getContactDetails(contactID);
            description = c.toString();
            System.out.println("Description");
        }
    }
    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return String.valueOf(logID);
    }
}
