package suffixtree;

import java.util.TreeMap;

public class SuffixTrieNode {

    SuffixTrieData data;
    int numChildren = 0;
    TreeMap<Character, SuffixTrieNode> childrenNodes;

    public SuffixTrieNode(){
        data = new SuffixTrieData();
        childrenNodes = new TreeMap<Character, SuffixTrieNode>();
    }

    public boolean hasChild(char label){
        return childrenNodes.containsKey(label);
    }

    public SuffixTrieNode getChild(char label) {
        return childrenNodes.get(label);
    }

    public void addChild(char label, SuffixTrieNode node) {
        childrenNodes.put(label, node);
        numChildren++;
    }

    public void addData(Object obj, int characterPos) {
        data.addStartIndex(new SuffixIndex(obj, characterPos));
    }

    public SuffixTrieData getData(){
        return data;
    }

    public String toString() {
        return data.toString();
    }

    //Below is for testing purposes
    public int getNumChildren() {
        return numChildren;
    }
}
