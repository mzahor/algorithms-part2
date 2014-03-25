import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SAP {
    Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = G;
    }

    class Tuple {
        public int node;
        public int len;

        Tuple() {
            node = -1;
            len = -1;
        }

        Tuple(int node, int len) {
            this.node = node;
            this.len = len;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        ArrayList<Integer> vArr = new ArrayList<Integer>();
        vArr.add(v);

        ArrayList<Integer> wArr = new ArrayList<Integer>();
        wArr.add(w);

        return anc(vArr, wArr, new HashSet<Integer>(), 0, 0).len;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        ArrayList<Integer> vArr = new ArrayList<Integer>();
        vArr.add(v);

        ArrayList<Integer> wArr = new ArrayList<Integer>();
        wArr.add(w);

        return anc(vArr, wArr, new HashSet<Integer>(), 0, 0).node;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return anc(v, w, new HashSet<Integer>(), 0, 0).len;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return anc(v, w, new HashSet<Integer>(), 0, 0).node;
    }

    public Tuple anc(
            Iterable<Integer> v,
            Iterable<Integer> w,
            Set<Integer> commonNodes,
            Integer vCount,
            Integer wCount) {

        for (Integer currV : v) {
            if (commonNodes.contains(v))
                return new Tuple(currV, vCount + wCount);
            commonNodes.add(currV);
        }

        for (Integer currW : w) {
            if (commonNodes.contains(w))
                return new Tuple(currW, vCount + wCount);
            commonNodes.add(currW);
        }

        Iterable<Integer> vParents = getAllParents(v);
        Iterable<Integer> wParents = getAllParents(w);

        boolean vHasParent = vParents.iterator().hasNext();
        boolean wHasParent = wParents.iterator().hasNext();

        if (!vHasParent && !wHasParent) return new Tuple();

        if (vHasParent) vCount++;
        if (wHasParent) wCount++;

        return anc(vParents, wParents, commonNodes, vCount, wCount);
    }

    public Iterable<Integer> getAllParents(Iterable<Integer> vertexes) {
        ArrayList<Integer> parents = new ArrayList<Integer>();

        for (Integer currVertex : vertexes)
            for (Integer v : graph.adj(currVertex))
                parents.add(v);

        return parents;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}