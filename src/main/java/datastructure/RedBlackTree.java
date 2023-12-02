package datastructure;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Red–Black Tree is a special type of binary search tree.
 */
public class RedBlackTree<K,V> {

    private final Comparator<K> comparator;

    protected Node<K,V> root;

    public RedBlackTree(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public void insert(K key, V value) {
        // TODO: 12/1/2023 Implement
    }

    public Object get(K key) {
        // TODO: 12/1/2023 Implement
        return null;
    }

    public void delete(K key) {
        // TODO: 12/1/2023 Implement
    }

    /**
     * Traverses all nodes in the tree using the BFS algorithm.
     * The passed consumer performs its operation on each node.
     * @param consumer function to be performed on each node.
     */
    public void breadthFirstSearch(Consumer<Node<K,V>> consumer) {
        Queue<Node<K,V>> nodeQueue = new LinkedList<>();
        nodeQueue.add(this.root);
        while (!nodeQueue.isEmpty()) {
            RedBlackTree.Node<K,V> currentNode = nodeQueue.poll();
            consumer.accept(currentNode);

            if (currentNode.leftChild != null)
                nodeQueue.add(currentNode.leftChild);

            if (currentNode.rightChild != null)
                nodeQueue.add(currentNode.rightChild);
        }
    }

    /**
     * Counts the number of black nodes in each subtree.
     * @param root root node of the tree.
     * @return the number of black nodes from the root to the leaf
     *         or -1 if the number of black nodes in both subtrees does not match.
     */
    protected int findBlackHeight(RedBlackTree.Node<K,V> root) {
        if (root == null)
            return 0;

        int leftBlackHeight = findBlackHeight(root.leftChild);
        int rightBlackHeight = findBlackHeight(root.rightChild);
        int currentNodeColor = root.isBlack ? 1 : 0;

        if (rightBlackHeight == -1 || leftBlackHeight != rightBlackHeight)
            return -1;

        return leftBlackHeight + currentNodeColor;
    }

    /**
     * Node in the Tree.
     * @param <K> key type.
     * @param <V> value type.
     */
    static class Node<K,V> {
        protected K key;
        private V value;

        protected Node<K,V> parent;
        protected Node<K,V> leftChild;
        protected Node<K,V> rightChild;

        protected boolean isBlack;

        /**
         * Creates a new node with given key and value.
         */
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
