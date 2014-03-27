import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordNet {

    private final HashMap<String, ArrayList<Integer>> synsetId;
    private final HashMap<String, List<Integer>> wordSynsets;
    private final HashMap<Integer, String> idSynset;
    private final HashMap<Integer, String> definitions;
    private final Digraph digraph;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        String[] synsetsContent = new In(synsets).readAll().split("\n");
        String[] hypernymsContent = new In(hypernyms).readAll().split("\n");

        this.synsetId = new HashMap<String, ArrayList<Integer>>(synsetsContent.length);
        this.wordSynsets = new HashMap<String, List<Integer>>(synsetsContent.length);
        this.idSynset = new HashMap<Integer, String>(synsetsContent.length);
        this.definitions = new HashMap<Integer, String>(synsetsContent.length);
        this.digraph = new Digraph(synsetsContent.length);

        for (String line : synsetsContent) {
            String[] values = line.split(",");
            String synset = values[1];
            String[] words = values[1].split(" ");
            int synsetId = Integer.parseInt(values[0]);


            if (this.synsetId.containsKey(synset)) {
                this.synsetId.get(synset).add(synsetId);
            }
            else {
                ArrayList<Integer> ids = new ArrayList<Integer>();
                ids.add(synsetId);
                this.synsetId.put(synset, ids);
            }

            this.idSynset.put(synsetId, synset);

            for (String word : words) {
                if (this.wordSynsets.containsKey(word))
                    this.wordSynsets.get(word).add(synsetId);
                else {
                    List<Integer> synsetIds = new ArrayList<Integer>();
                    synsetIds.add(synsetId);
                    this.wordSynsets.put(word, synsetIds);
                }
            }

            this.definitions.put(synsetId, values[2]);
        }

        this.sap = new SAP(digraph);

        for (String line : hypernymsContent) {
            String[] values = line.split(",");
            int hyponymId = Integer.parseInt(values[0]);

            for (int i = 1; i < values.length; i++) {
                int hypernymId = Integer.parseInt(values[i]);
                this.digraph.addEdge(hyponymId, hypernymId);
            }
        }
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return this.wordSynsets.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return this.wordSynsets.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        List<Integer> nounANum = this.wordSynsets.get(nounA);
        List<Integer> nounBNum = this.wordSynsets.get(nounB);
        return this.sap.length(nounANum, nounBNum);
    }

    // a synset (second field of synsetId.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        List<Integer> nounANum = this.wordSynsets.get(nounA);
        List<Integer> nounBNum = this.wordSynsets.get(nounB);

        int sapId = this.sap.ancestor(nounANum, nounBNum);
        return this.idSynset.get(sapId);
    }

    // for unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/synsets.txt",
                "/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/hypernyms.txt");
        System.out.print(wn.sap("penis", "vagina"));
    }
}