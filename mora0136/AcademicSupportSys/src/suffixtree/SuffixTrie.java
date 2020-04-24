package suffixtree;

import contacts.Contact;

import java.io.*;
import java.util.Scanner;

public class SuffixTrie {

    private SuffixTrieNode root = new SuffixTrieNode();

    /**
     * Insert a String into the suffix trie.  For the assignment the string str
     * is a sentence from the given text file.
     *
     * @param str the sentence to insert
     * @param contact the associated contact for this name
     * @return the final node inserted
     */
    public SuffixTrieNode insert(String str, Contact contact) {
        SuffixTrieNode currentNode = root;
        int charactersInSentence = 0;
        str = str.toLowerCase();
        char[] c = str.toCharArray();
        for(int i = 0; i <c.length; i++){
            currentNode = root;
            for(int j = 0; j < c.length-i; j++){
                SuffixTrieNode childNode = currentNode.getChild((c[i+j]));
                if(childNode != null){
                    childNode.addData(contact, charactersInSentence);
                }else{
                    childNode = new SuffixTrieNode();
                    childNode.addData(contact, charactersInSentence);
                    currentNode.addChild(c[i+j], childNode);
                }
                currentNode = childNode;
                charactersInSentence++;
            }
            charactersInSentence = charactersInSentence - c.length+i+1;
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
        SuffixTrieNode nodeFound; // to store the node data found so that it can edited to correct index
        char[] word = str.toLowerCase().toCharArray();

        for (char c : word) {
            currentNode = currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }
        nodeFound = currentNode;
        for (SuffixIndex index : nodeFound.getData().getStartIndexes()) {
            index.setCharacter(index.getCharacter() - str.length() + 1);
        }

        return nodeFound;
    }

//    /**
//     * Helper/Factory method to build a SuffixTrie object from the text in the
//     * given file.  The text file is broken up into sentences and each sentence
//     * is inserted into the suffix trie.
//     *
//     * It is called in the following way
//     * <code>SuffixTrie st = SuffixTrie.readInFromFile("Frank01e.txt");</code>
//     *
//     * @param fileName
//     * @return
//     */
//    public static SuffixTrie readInFromFile(String fileName) throws IOException {
//        SuffixTrie st = new SuffixTrie();
//        File inputFileName = new File(fileName);
//        BufferedReader fileReader = new BufferedReader(new FileReader(inputFileName));
//        int currentChar = fileReader.read();
//        StringBuilder currentSentence = new StringBuilder();
//        int numOfSentence = 0;
//
//        while(currentChar != -1){
//            if(currentChar == '.' || currentChar == '!' || currentChar == '?'){
//                st.insert(currentSentence.toString(), numOfSentence);
//                currentSentence = new StringBuilder();
//                numOfSentence++;
//            }else {
//                currentSentence.append((char) currentChar);
//            }
//            currentChar = fileReader.read();
//        }
//        if(currentSentence.length() > 0){
//            st.insert(currentSentence.toString(), numOfSentence);
//        }
//
//        return st;
//    }

//    public static SuffixTrie readInFromDatabase(String ){
//
//    }

    //Below is only used for testing purposes
    public SuffixTrieNode getTrieRoot(){
        return root;
    }
}