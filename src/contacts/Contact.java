package contacts;

import java.util.Objects;

public class Contact{
    int contact_ID;
    String givenName;
    String surname;
    String email;
    String phone;

    public Contact(int contact_ID, String givenName, String surname, String email, String phone) {
        this.contact_ID = contact_ID;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public int getContact_ID() {
        return contact_ID;
    }

    public void setContact_ID(int contact_ID) {
        this.contact_ID = contact_ID;
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

    public String toString(){
        return givenName + " " + surname;
    }

    @Override
    public boolean equals(Object obj) {
        return contact_ID == ((Contact)obj).getContact_ID();
    }
}
