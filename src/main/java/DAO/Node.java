package DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by student on 2017/7/4.
 */
public class Node {
    private String name;
    private Map<Node, Integer> child = new HashMap<>();
    private ArrayList<Node> relationNodes = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Node, Integer> getChild() {
        return child;
    }

    public void setChild(Map<Node, Integer> child) {
        this.child = child;
    }

    public ArrayList<Node> getRelationNodes() {
        return relationNodes;
    }

    public void setRelationNodes(ArrayList<Node> relationNodes) {
        this.relationNodes = relationNodes;
    }
}
