package com.itheima.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {
    private String nodeId;
    private String label;
    private List children;

    public TreeNode() {
    }

    public TreeNode(String nodeId, String label, List children) {
        this.nodeId = nodeId;
        this.label = label;
        this.children = children;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "nodeId=" + nodeId +
                ", label='" + label + '\'' +
                ", children=" + children +
                '}';
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
}
