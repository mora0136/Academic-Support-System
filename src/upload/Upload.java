package upload;

import contacts.Contact;

import javax.swing.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


/*
 * A simple class to instantiate an upload and have multiple of them to select from in a list.
 */

public class Upload {
    int uploadID;
    String title;
    String description;
    String type;

    LocalDate date;
    boolean cv, orcid, inst, publ, wos, gSch, linIn, scopus, pure, acad, twit, resGate;
    DefaultListModel addedContacts, displayedContacts, attachedFiles;

    public Upload(){

    }

    public Upload(int uploadID, String title){
        this.uploadID = uploadID;
        this.title = title;

    }

    public Upload(ResultSet rs){
        addedContacts = new DefaultListModel<Contact>();
        attachedFiles = new DefaultListModel<File>();
        try {
            this.uploadID = rs.getInt("upload_ID");
            this.title = rs.getString("Title");
            this.description = rs.getString("Description");
            this.type = rs.getString("Type");
            this.date = (rs.getDate("Date")).toLocalDate();

            cv = (rs.getBoolean("cv"));resGate = (rs.getBoolean("resGate"));orcid = (rs.getBoolean("orcid"));
            inst = (rs.getBoolean("inst"));publ = (rs.getBoolean("publ"));wos = (rs.getBoolean("wos"));
            gSch = (rs.getBoolean("gSch"));linIn = (rs.getBoolean("linIn"));scopus = (rs.getBoolean("scopus"));
            pure = (rs.getBoolean("pure"));acad = (rs.getBoolean("acad"));twit = (rs.getBoolean("twit"));
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    public void setFiles(ResultSet rs){
        try {
            while (rs.next()) {
                System.out.println("adding file");
                attachedFiles.addElement(new File(rs.getString("File")));
            }
        }catch(SQLException e){

        }
    }
    public void setAuthors(ResultSet rs){
        try {
            while (rs.next()) {
                Contact c = new Contact(rs.getInt("contact_ID"), rs.getString("givenName"),
                        rs.getString("surname"), rs.getString("email"),
                        rs.getString("phone"));
                addedContacts.addElement(c);
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUploadID() {
        return uploadID;
    }

    public void setUploadID(int uploadID) {
        this.uploadID = uploadID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCv() {
        return cv;
    }

    public void setCv(boolean cv) {
        this.cv = cv;
    }

    public boolean isOrcid() {
        return orcid;
    }

    public void setOrcid(boolean orcid) {
        this.orcid = orcid;
    }

    public boolean isInst() {
        return inst;
    }

    public void setInst(boolean inst) {
        this.inst = inst;
    }

    public boolean isPubl() {
        return publ;
    }

    public void setPubl(boolean publ) {
        this.publ = publ;
    }

    public boolean isWos() {
        return wos;
    }

    public void setWos(boolean wos) {
        this.wos = wos;
    }

    public boolean isgSch() {
        return gSch;
    }

    public void setgSch(boolean gSch) {
        this.gSch = gSch;
    }

    public boolean isLinIn() {
        return linIn;
    }

    public void setLinIn(boolean linIn) {
        this.linIn = linIn;
    }

    public boolean isScopus() {
        return scopus;
    }

    public void setScopus(boolean scopus) {
        this.scopus = scopus;
    }

    public boolean isPure() {
        return pure;
    }

    public void setPure(boolean pure) {
        this.pure = pure;
    }

    public boolean isAcad() {
        return acad;
    }

    public void setAcad(boolean acad) {
        this.acad = acad;
    }

    public boolean isTwit() {
        return twit;
    }

    public void setTwit(boolean twit) {
        this.twit = twit;
    }

    public boolean isResGate() {
        return resGate;
    }

    public void setResGate(boolean resGate) {
        this.resGate = resGate;
    }

    public DefaultListModel getAddedContacts() {
        return addedContacts;
    }

    public void setAddedContacts(DefaultListModel addedContacts) {
        this.addedContacts = addedContacts;
    }

    public DefaultListModel getDisplayedContacts() {
        return displayedContacts;
    }

    public void setDisplayedContacts(DefaultListModel displayedContacts) {
        this.displayedContacts = displayedContacts;
    }

    public DefaultListModel getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(DefaultListModel attachedFiles) {
        this.attachedFiles = attachedFiles;
    }

    public String toString(){
        return title;
    }
}
