package mff.agents.astarHelper;

import java.util.Comparator;

public class CompareByCostReversed implements Comparator<SearchNode> {
    public int compare(SearchNode o1, SearchNode o2) {
        return -Float.compare(o1.cost, o2.cost);
    }
}
