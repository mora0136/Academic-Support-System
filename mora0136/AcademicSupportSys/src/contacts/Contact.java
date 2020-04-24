package contacts;

public class Contact{

    String givenName;
    String surname;
    String email;
    String phone;

    public Contact(String givenName, String surname, String email, String phone) {
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }
    public String getName() {
        return givenName;
    }

    public void setName(String name) {
        this.givenName = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
