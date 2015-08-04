/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a hierarchy of a set of packages, the main purpose of which is to find the most specific package of the
 * set which a particular class is under. This class is implemented as a trie-tree.
 *
 * @author nathan.taylor
 * @since 2015-02-27
 */
public class PackageTree {

    private final Node rootNode = new Node(null, null);
    private final Elements elementUtils;

    public PackageTree(Elements elementUtils, Set<PackageElement> packageElements) {
        this.elementUtils = elementUtils;
        for (PackageElement element : packageElements) {
            addPackageToTree(element);
        }
    }

    /**
     * Find the most specific package from the set this instance was initialized with (see {@link #PackageTree(Elements,
     * Set)}), which the given element is under.
     * <p>
     * Consider the set of packages {{@code "com.workday", "com.workday.model", "com.workday.model.xml.base"} }. The
     * following table illustrates the behavior of this method.
     * <table border="1" summary="">
     *     <tr>
     *         <th>Element</th>
     *         <th>Return Value</th>
     *     </tr>
     *     <tr>
     *         <td>{@code com.workday.model.xml.GridModel}</td>
     *         <td>{@code com.workday.model}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code com.workday.util.GridHelper}</td>
     *         <td>{@code com.workday}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code org.chart.DataSet}</td>
     *         <td>{@code null}</td>
     *     </tr>
     * </table>
     *
     * @param element The element whose matching package we are trying to find.
     *
     * @return The most specific matching package for the element, or null if there is not matching package.
     */
    public PackageElement getMatchingPackage(Element element) {
        Node node = findDeepestMatchingNode(getPackageHierarchy(elementUtils.getPackageOf(element)));
        return node == null ? null : node.packageElement;
    }

    private void addPackageToTree(PackageElement element) {
        List<PackageElement> hierarchy = getPackageHierarchy(element);
        addPackageToNode(hierarchy, rootNode);
    }

    /**
     * Returns the package tree of a package element as a list. For instance, if the element is {@code
     * com.workday.model}, then this method returns {@code ["com", "workday", "model"]}.
     * <p>
     * This method always returns at a list of at least size 1 -- the list containing the element.
     */
    private static List<PackageElement> getPackageHierarchy(PackageElement element) {
        LinkedList<PackageElement> hierarchy = new LinkedList<>();
        Element nextElement = element;
        while (nextElement instanceof PackageElement) {
            hierarchy.push((PackageElement) nextElement);
            nextElement = nextElement.getEnclosingElement();
        }
        return Collections.unmodifiableList(hierarchy);
    }

    /**
     * Find the node deepest in the tree that corresponds to one of the original packages and matches the provided
     * package hierarchy, or null if no match is found.
     * <p>
     * Note that this will only return nodes that have a matching package element from the original set, even if there
     * is a deeper node match without a matching package element.
     */
    private Node findDeepestMatchingNode(List<PackageElement> hierarchy) {
        Node nextNode = rootNode;
        Node lastMatch = null;
        while (!hierarchy.isEmpty()) {
            String nextName = hierarchy.get(0).getSimpleName().toString();
            nextNode = nextNode.children.get(nextName);
            if (nextNode == null) {
                break;
            }
            if (nextNode.packageElement != null) {
                lastMatch = nextNode;
            }
            hierarchy = hierarchy.subList(1, hierarchy.size());
        }
        return lastMatch;
    }

    /**
     * Recursively traverses through the tree until the Node matching the provided hierarchy is found or an insertion
     * point is reached. This will update an existing Node with a PackageElement, or create a new subtree of Nodes if
     * the Node for the given hierarchy does not exist.
     *
     * @param hierarchy The package hierarchy indicating the new Node(s) to insert / update, starting after {@code
     * rootNode}.
     * @param rootNode The Node to start after.
     */
    private void addPackageToNode(List<PackageElement> hierarchy, Node rootNode) {
        if (hierarchy.isEmpty()) {
            return;
        }

        PackageElement root = hierarchy.get(0);
        String rootName = root.getSimpleName().toString();
        Node parentNode = rootNode.children.get(rootName);
        if (parentNode == null) {
            parentNode = createNodeForPackage(hierarchy);
            rootNode.children.put(parentNode.packagePoint, parentNode);
        } else if (hierarchy.size() == 1) {
            parentNode.packageElement = root;
        } else {
            addPackageToNode(hierarchy.subList(1, hierarchy.size()), parentNode);
        }
    }

    /**
     * Recursively creates Nodes for the given package hierarchy. Only the leaf Node will be created with a
     * PackageElement.
     */
    private Node createNodeForPackage(List<PackageElement> packageHierarchy) {
        if (packageHierarchy.size() == 1) {
            PackageElement leaf = packageHierarchy.get(0);
            return new Node(leaf.getSimpleName().toString(), leaf);
        } else {
            PackageElement rootPackage = packageHierarchy.get(0);
            Node root = new Node(rootPackage.getSimpleName().toString(), null);
            Node child = createNodeForPackage(packageHierarchy.subList(1, packageHierarchy.size()));
            root.children.put(child.packagePoint, child);
            return root;
        }
    }

    private static class Node {

        /** The simple name of the package this Node represents. */
        public final String packagePoint;
        /** The corresponding PackageElement from the original set, or null if there is not matching package. */
        public PackageElement packageElement;
        public Map<String, Node> children = new HashMap<>();

        public Node(String packagePoint, PackageElement packageElement) {
            this.packagePoint = packagePoint;
            this.packageElement = packageElement;
        }
    }
}
