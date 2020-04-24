package suffixtree;

import contacts.Contact;

public class SuffixIndex {

    Contact contact;
    int character;

    public SuffixIndex(Contact contact, int character) {
        this.contact = contact;
        this.character = character;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getCharacter() {
        return character;
    }

    public void setCharacter(int character) {
        this.character = character;
    }

    /**
     * The sentence and character position in the format sentence.character notation
     * @return the position.
     */
    @Override
    public String toString() {
        return contact.getName() + " "+ contact.getSurname() +"." + character;
    }
}
