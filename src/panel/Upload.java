package panel;

public class Upload {
    public int getUploadID() {
        return uploadID;
    }

    public void setUploadID(int uploadID) {
        this.uploadID = uploadID;
    }

    int uploadID;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;
    Upload(int uploadID, String title){
        this.uploadID = uploadID;
        this.title = title;
    }

    public String toString(){
        return title;
    }


}
