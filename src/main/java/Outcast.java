public class Outcast {
    private WordNet wordNet;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max = 0;
        int winner = -1;
        for (int i = 0; i < nouns.length; i++) {
            int dist = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                dist += this.wordNet.distance(nouns[i], nouns[j]);
            }

            if (dist >= max) {
                max = dist;
                winner = i;
            }
        }
        return nouns[winner];
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        WordNet wn = new WordNet("/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/synsets.txt",
                "/Users/marianzagoruiko/dev/algorithms-part2/src/main/java/hypernyms.txt");
        Outcast out = new Outcast(wn);
        System.out.println(out.outcast(new String[]{"apple", "pear", "peach", "banana", "lime", "lemon", "blueberry", "strawberry", "mango", "watermelon", "potato"}));
    }
}