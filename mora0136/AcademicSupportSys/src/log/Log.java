package log;

import contacts.ContactDB;
import upload.UploadDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/*
 The representation of a log. A log can have either an Upload or Contact Associated with it. Log is instntiated with a ResultSet, so it is limited to being used with LogDB in it's current form, this can be changed if necessary.
 */


public class Log {
    int logID;
    LocalDate date; //The date at which the log was recorded
    String type; //Upload or Contact
    String action; //What action the log has tracked
    Object data; //Either Upload or Contact instantiations

    public Log(ResultSet rs) throws SQLException {
        this.logID = rs.getInt("log_ID");
        this.date = rs.getDate("date").toLocalDate();
        this.type = rs.getString("type");
        this.action = rs.getString("action");

        //Find associated data for its particular type
        if(this.type.equals("Upload")){
            int uploadID = rs.getInt("associate_ID");
            data = UploadDB.getUpload(uploadID);
        }else {
            int contactID = rs.getInt("associate_ID");
            ContactDB db = new ContactDB();
            data = db.getContactDetails(contactID);
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object at) {
        this.data = at;
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

    public String toString(){
        return String.valueOf(logID);
    }
}
