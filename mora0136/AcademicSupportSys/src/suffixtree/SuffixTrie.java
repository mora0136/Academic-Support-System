package suffixtree;

import contacts.Contact;

public class SuffixTrie {

    private final SuffixTrieNode root = new SuffixTrieNode();

    /**
     * Insert a String into the suffix trie.  For the assignment the string str
     * is a sentence from the given text file.
     *
     * @param str the sentence to insert
     * @param contact the starting index/position of the sentence
     * @return the final node inserted
     */
    public SuffixTrieNode insert(String str, Contact contact) {
        SuffixTrieNode currentNode = root;
        int charactersInSentence = 0;
        str = str.toLowerCase();
        char[] c = str.toCharArray();
        //The start of a new suffix(first iteration is full string)
        for(int i = 0; i <c.length; i++){
            currentNode = root;
            //c[i+j] uses i as an offset, it first pass through enters str as per normal since i=0
            //Next iteration i = 1, then it will start with offset i and continue until the end. Since
            //c.lenggth-i ensures i+j never exceeds the Array bounds.
            for(int j = 0; j < c.length-i; j++){
                SuffixTrieNode childNode = currentNode.getChild((c[i+j]));
                if(childNode == null){
                    childNode = new SuffixTrieNode();
                    childNode.addData(contact, charactersInSentence);
                    currentNode.addChild(c[i+j], childNode);
                }else{
                    childNode.addData(contact, charactersInSentence);
                }
                currentNode = childNode; //Move down to that node to prepare for next insertion
                charactersInSentence++;
            }
            charactersInSentence = i+1; //i+1 is the offset of the next loop
        }
        return currentNode;
    }

    /**
     * Get the suffix trie node associated with the given (sub)string.
     *
     * @param str the (sub)string to search for
     * @return  the final node in the (sub)string
     */
    public SuffixTrieNode get(String str) {
        SuffixTrieNode currentNode = root;
        SuffixTrieNode nodeFound = new SuffixTrieNode(); // to store the node data found so as to not overwrite what was already inside the suffix Node

        char[] word = str.toLowerCase().toCharArray();

        for (char c : word) {
            currentNode = currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }

        //Copy the Nodes data of indexes to a dummy node for display.
        //This is necessary is the program searches for the same node successively
        //Note that the character is also adjusted to appropriately show where the suffix starts
        for(SuffixIndex index : currentNode.getData().getStartIndexes()){
            nodeFound.addData(index.getContact(), index.getCharacter()-str.length()+1);
        }

        return nodeFound;
    }

    //Below is only used for testing purposes
    public SuffixTrieNode getTrieRoot(){
        return root;
    }
}