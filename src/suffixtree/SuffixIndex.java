package suffixtree;

import contacts.Contact;

public class SuffixIndex {

    Object obj;
    int character;

    public SuffixIndex(Object data, int character) {
        this.obj = data;
        this.character = character;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Contact obj) {
        this.obj = obj;
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
        return obj.toString();
    }
}
