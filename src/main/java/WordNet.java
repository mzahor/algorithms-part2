import java.util.HashMap;

public class WordNet {

    private final HashMap<String, Integer> synsets;
    private final HashMap<Integer, String> synsetsBack;
    private final HashMap<Integer, String> definitions;
    private final Digraph digraph;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        String[] synsetsContent = new In(synsets).readAll().split("\n");
        String[] hypernymsContent = new In(hypernyms).readAll().split("\n");

        this.synsets = new HashMap<String, Integer>(synsetsContent.length);
        this.synsetsBack = new HashMap<Integer, String>(synsetsContent.length);
        this.definitions = new HashMap<Integer, String>(synsetsContent.length);

        for (String line : synsetsContent) {
            String[] values = line.split(",");
            String[] words = values[1].split(" ");
            int id = Integer.parseInt(values[0]);
            for (String word : words) {
                this.synsets.put(word, id);
                this.synsetsBack.put(id, word);
            }
            this.definitions.put(id, values[2]);
        }

        this.digraph = new Digraph(this.synsets.size());
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
        return this.synsets.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return this.synsets.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        int nounANum = this.synsets.get(nounA);
        int nounBNum = this.synsets.get(nounB);
        return this.sap.length(nounANum, nounBNum);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int nounANum = this.synsets.get(nounA);
        int nounBNum = this.synsets.get(nounB);
        int sapId = this.sap.ancestor(nounANum, nounBNum);
        return this.synsetsBack.get(sapId);
    }

    // for unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/synsets.txt",
                "/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/hypernyms.txt");
        assert wn.isNoun("greeting");
        System.out.print(wn.sap("plate", "cup"));
    }
}