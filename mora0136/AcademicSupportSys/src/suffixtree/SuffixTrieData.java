package suffixtree;

import java.util.ArrayList;

public class SuffixTrieData {

    private ArrayList<SuffixIndex> startIndexes = new ArrayList<>();

    public void addStartIndex(SuffixIndex startIndex) {
        startIndexes.add(startIndex);
    }
    public ArrayList<SuffixIndex> getStartIndexes(){
        return startIndexes;
    }

    public String toString() {
        return startIndexes.toString();
    }
}
