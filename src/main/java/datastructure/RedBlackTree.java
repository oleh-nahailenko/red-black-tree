package datastructure;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * Inserts the provided key and value into the tree.
     * If the specified key already exists in the tree, the new value replaces the old one.
     * @param key key to determine the value.
     * @param value value to store.
     * @throws NullPointerException if the provided key is null.
     */
    public void insert(K key, V value) {
        Objects.requireNonNull(key, "Cannot save null key");

        Node<K, V> newNode = new Node<>(key, value);
        Node<K, V> currentNode = this.root;
        Node<K, V> parent = null;
        while (currentNode != null) {
            parent = currentNode;
            int comparisonResult = this.comparator.compare(newNode.key, currentNode.key);
            if (comparisonResult < 0) {
                currentNode = currentNode.leftChild;
            } else if (comparisonResult > 0) {
                currentNode = currentNode.rightChild;
            } else {
                currentNode.value = newNode.value;
                return;
            }
        }

        newNode.parent = parent;
        if (parent == null) {
            this.root = newNode;
            this.root.isBlack = true;
        } else if (this.comparator.compare(newNode.key, parent.key) < 0) {
            parent.leftChild = newNode;
        } else {
            parent.rightChild = newNode;
        }

        fixAfterInsertion(newNode);
    }

    /**
     * Returns the {@code Optional} value associated with the passed key if the association exists in the tree.
     * @param key search key.
     * @return {@code Optional} value associated with the key.
     */
    // TODO: 12/9/2023 : handle null keys
    public Optional<V> search(K key) {
        Node<K, V> currentNode = this.root;
        while (currentNode != null) {
            int comparisonResult = this.comparator.compare(currentNode.key, key);
            if (comparisonResult < 0) {
                currentNode = currentNode.rightChild;
            } else if (comparisonResult > 0) {
                currentNode = currentNode.leftChild;
            } else {
                return Optional.of(currentNode.value);
            }
        }

        return Optional.empty();
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
    // TODO: 12/10/2023 : rewrite without recursion
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

    private void fixAfterInsertion(Node<K, V> node) {
        while (!isNodeBlack(node.parent)) {
            if (node.parent.equals(node.parent.parent.leftChild)) {
                Node<K, V> uncleNode = node.parent.parent.rightChild;
                if (!isNodeBlack(uncleNode)) {
                    uncleNode.isBlack = true;
                    node.parent.isBlack = true;
                    node.parent.parent.isBlack = false;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.rightChild) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.isBlack = true;
                    node.parent.parent.isBlack = false;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node<K, V> uncleNode = node.parent.parent.leftChild;
                if (!isNodeBlack(uncleNode)) {
                    uncleNode.isBlack = true;
                    node.parent.isBlack = true;
                    node.parent.parent.isBlack = false;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.leftChild) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.isBlack = true;
                    node.parent.parent.isBlack = false;
                    rotateLeft(node.parent.parent);
                }
            }
        }

        this.root.isBlack = true;
    }

    protected void rotateLeft(Node<K, V> node) {
        if (node == null) return;

        Node<K, V> newParent = node.rightChild;
        node.rightChild = newParent.leftChild;
        if (newParent.leftChild != null) {
            newParent.leftChild.parent = node;
        }

        newParent.parent = node.parent;
        if (node.parent == null) {
            this.root = newParent;
        } else if (node == node.parent.leftChild) {
            node.parent.leftChild = newParent;
        } else {
            node.parent.rightChild = newParent;
        }

        newParent.leftChild = node;
        node.parent = newParent;
    }

    protected void rotateRight(Node<K, V> node) {
        if (node == null) return;

        Node<K, V> newParent = node.leftChild;
        node.leftChild = newParent.rightChild;
        if (newParent.rightChild != null) {
            newParent.rightChild.parent = node;
        }

        newParent.parent = node.parent;
        if (node.parent == null) {
            this.root = newParent;
        } else if (node == node.parent.leftChild) {
            node.parent.leftChild = newParent;
        } else {
            node.parent.rightChild = newParent;
        }

        newParent.rightChild = node;
        node.parent = newParent;
    }

    private boolean isNodeBlack(Node<K, V> node) {
        return node == null || node.isBlack;
    }

    /**
     * Node in the Tree.
     * @param <K> key type.
     * @param <V> value type.
     */
    static class Node<K,V> {
        protected K key;
        protected V value;

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

        Node(K key, V value, Node<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        Node(K key, V value, Node<K,V> parent, boolean isBlack) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.isBlack = isBlack;
        }
    }

}
