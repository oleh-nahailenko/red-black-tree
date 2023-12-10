package datastructure;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class RedBlackTreeTest {

    @Test
    void breadthFirstSearch_findsAllNodes() {
        //GIVEN
        List<Integer> expectedKeys = List.of(3, 6, 1, 8);
        List<Integer> actualKeys = new ArrayList<>(4);

        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(3, new Object(), null, true);
        var rightRootChild = new RedBlackTree.Node<>(6, new Object(), root, true);
        root.leftChild = new RedBlackTree.Node<>(1, new Object(), root, true);
        rightRootChild.rightChild = new RedBlackTree.Node<>(8, new Object(), rightRootChild, false);
        root.rightChild = rightRootChild;
        tree.root = root;

        //WHEN
        tree.breadthFirstSearch(n -> actualKeys.add(n.key));

        //THEN
        assertThat(expectedKeys).hasSameElementsAs(actualKeys);
    }

    @Test
    void findBlackHeight_subtreesHaveSameBlackHeight_returnsBlackHeight() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(3, new Object(), null, true);
        var rightRootChild = new RedBlackTree.Node<>(6, new Object(), root, false);
        var leftRootChild = new RedBlackTree.Node<>(1, new Object(), root, true);

        var rightRightRootChild = new RedBlackTree.Node<>(8, new Object(), rightRootChild, true);
        rightRootChild.leftChild = new RedBlackTree.Node<>(5, new Object(), rightRootChild, true);
        rightRootChild.rightChild = rightRightRootChild;

        rightRightRootChild.rightChild = new RedBlackTree.Node<>(9, new Object(), rightRightRootChild, false);
        rightRightRootChild.leftChild = new RedBlackTree.Node<>(7, new Object(), rightRightRootChild, false);

        rightRootChild.rightChild = new RedBlackTree.Node<>(6, new Object(), rightRootChild, true);
        root.leftChild = leftRootChild;
        root.rightChild = rightRootChild;
        tree.root = root;

        //WHEN
        int blackHeight = tree.findBlackHeight(tree.root);

        //THEN
        assertThat(blackHeight).isEqualTo(2);
    }

    @Test
    void findBlackHeight_subtreesHaveNotSameBlackHeight_returnsMinusOne() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, new Object(), null, true);
        var rightRootChild = new RedBlackTree.Node<>(5, new Object(), root, true);
        var leftRootChild = new RedBlackTree.Node<>(2, new Object(), root, false);

        rightRootChild.rightChild = new RedBlackTree.Node<>(6, new Object(), rightRootChild, false);
        root.leftChild = leftRootChild;
        root.rightChild = rightRootChild;
        tree.root = root;

        //WHEN
        int blackHeight = tree.findBlackHeight(tree.root);

        //THEN
        assertThat(blackHeight).isEqualTo(-1);
    }

    @Test
    void rotateLeft_passedNodeIsValid_subtreeRotatedToTheLeft() {
        //GIVEN
        RedBlackTree<Integer, Object> tree = new RedBlackTree<>(Integer::compareTo);
        var x = new RedBlackTree.Node<>(10, new Object());
        var a = new RedBlackTree.Node<>(5, new Object(), x);
        var y = new RedBlackTree.Node<>(15, new Object(), x);
        var b = new RedBlackTree.Node<>(13, new Object(), y);
        var u = new RedBlackTree.Node<>(20, new Object(), y);

        x.leftChild = a;
        x.rightChild = y;
        y.leftChild = b;
        y.rightChild = u;

        //WHEN
        tree.rotateLeft(x);

        //THEN
        assertThat(x.parent == y).isTrue();
        assertThat(x.leftChild == a).isTrue();
        assertThat(x.rightChild == b).isTrue();
        assertThat(y.rightChild == u).isTrue();
        assertThat(y.leftChild == x).isTrue();
    }

    @Test
    void rotateRight_passedNodeIsValid_subtreeRotatedToTheRight() {
        //GIVEN
        RedBlackTree<Integer, Object> tree = new RedBlackTree<>(Integer::compareTo);
        var y = new RedBlackTree.Node<>(15, new Object());
        var u = new RedBlackTree.Node<>(20, new Object(), y);
        var x = new RedBlackTree.Node<>(10, new Object(), y);
        var a = new RedBlackTree.Node<>(5, new Object(), x);
        var b = new RedBlackTree.Node<>(13, new Object(), x);

        y.leftChild = x;
        y.rightChild = u;
        x.leftChild = a;
        x.rightChild = b;

        //WHEN
        tree.rotateRight(y);

        //THEN
        assertThat(y.parent == x).isTrue();
        assertThat(x.leftChild == a).isTrue();
        assertThat(x.rightChild == y).isTrue();
        assertThat(y.rightChild == u).isTrue();
        assertThat(y.leftChild == b).isTrue();
    }

    @Test
    void insert_treeIsEmpty_newNodeIsAddedAsRoot() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        Integer expectedRootKey = 10;
        Object expectedRootValue = new Object();

        //WHEN
        tree.insert(expectedRootKey, expectedRootValue);

        //THEN
        var actualRoot = tree.root;
        assertThat(actualRoot).isNotNull();
        assertThat(actualRoot.key).isEqualTo(expectedRootKey);
        assertThat(actualRoot.value).isEqualTo(expectedRootValue);
        assertThat(actualRoot.isBlack).isTrue();
    }

    @Test
    void insert_newNodeAdded_treeIsBalanced() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, new Object(), null, true);
        root.rightChild = new RedBlackTree.Node<>(5, new Object(), root, false);
        root.leftChild = new RedBlackTree.Node<>(2, new Object(), root, false);
        tree.root = root;

        //WHEN
        tree.insert(10, new Object());

        //THEN
        assertThatRedBlackTreeContainsKey(tree, 10);
        assertThatRedBlackTreeHasNoConsecutiveRedNodes(tree);
        assertThatRedBlackTreeHasSameNumberOfBlackNodes(tree);
    }

    @Test
    void insert_existingKeyAdded_treeIsBalanced() {
        //GIVEN
        Integer existingKey = 5;
        String expectedValue = "new-value";

        var tree = new RedBlackTree<Integer, String>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, "value", null, true);
        root.rightChild = new RedBlackTree.Node<>(existingKey, "old-value", root, false);
        root.leftChild = new RedBlackTree.Node<>(2, "value", root, false);
        tree.root = root;

        //WHEN
        tree.insert(existingKey, expectedValue);

        //THEN
        assertThatRedBlackTreeContainsKey(tree, existingKey);
        assertThatRedBlackTreeContainsExpectedValueByKey(tree, existingKey, expectedValue);
        assertThatRedBlackTreeHasNoConsecutiveRedNodes(tree);
        assertThatRedBlackTreeHasSameNumberOfBlackNodes(tree);
    }

    @Test
    void search_existingKeyIsRequested_returnsOptionalWithNode() {
        //GIVEN
        Integer searchKey = 8;
        Object expectedValue = new Object();

        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, new Object(), null, true);
        var rightRootChild = new RedBlackTree.Node<>(5, new Object(), root, true);
        var leftRootChild = new RedBlackTree.Node<>(2, new Object(), root, true);

        rightRootChild.rightChild = new RedBlackTree.Node<>(searchKey, expectedValue, rightRootChild, false);
        root.leftChild = leftRootChild;
        root.rightChild = rightRootChild;
        tree.root = root;

        //WHEN
        Optional<Object> actualValueOptional = tree.search(searchKey);

        //THEN
        assertThat(actualValueOptional).isNotEmpty();
        assertThat(actualValueOptional.get()).isNotNull().isEqualTo(expectedValue);
    }

    @Test
    void search_nonExistentKeyIsRequested_returnsEmptyOptional() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, new Object(), null, true);
        root.leftChild = new RedBlackTree.Node<>(2, new Object(), root, false);
        root.rightChild = new RedBlackTree.Node<>(5, new Object(), root, false);
        tree.root = root;

        //WHEN
        Optional<Object> actualValueOptional = tree.search(10);

        //THEN
        assertThat(actualValueOptional).isEmpty();
    }

    @Test
    void delete_existingKeyIsRequested_treeDoesNotContainKeyAnaTreeIsBalanced() {
        //GIVEN
        Integer keyToDelete = 6;

        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(3, new Object(), null, true);
        var rightRootChild = new RedBlackTree.Node<>(keyToDelete, new Object(), root, false);
        var leftRootChild = new RedBlackTree.Node<>(1, new Object(), root, true);

        var rightRightRootChild = new RedBlackTree.Node<>(8, new Object(), rightRootChild, true);
        rightRootChild.leftChild = new RedBlackTree.Node<>(5, new Object(), rightRootChild, true);
        rightRootChild.rightChild = rightRightRootChild;

        rightRightRootChild.rightChild = new RedBlackTree.Node<>(9, new Object(), rightRightRootChild, false);
        rightRightRootChild.leftChild = new RedBlackTree.Node<>(7, new Object(), rightRightRootChild, false);

        root.leftChild = leftRootChild;
        root.rightChild = rightRootChild;
        tree.root = root;

        //WHEN
        tree.delete(keyToDelete);

        //THEN
        assertThatRedBlackTreeDoesNotContainKey(tree, keyToDelete);
        assertThatRedBlackTreeHasNoConsecutiveRedNodes(tree);
        assertThatRedBlackTreeHasSameNumberOfBlackNodes(tree);
    }

    @Test
    void delete_nonExistentKeyIsRequested_treeIsBalanced() {
        //GIVEN
        var tree = new RedBlackTree<>(Integer::compareTo);
        var root = new RedBlackTree.Node<>(4, new Object(), null, true);
        root.rightChild = new RedBlackTree.Node<>(5, new Object(), root, false);
        root.leftChild = new RedBlackTree.Node<>(2, new Object(), root, false);
        tree.root = root;

        //WHEN
        tree.delete(10);

        //THEN
        assertThatRedBlackTreeHasNoConsecutiveRedNodes(tree);
        assertThatRedBlackTreeHasSameNumberOfBlackNodes(tree);
    }

    private <K,V> void assertThatRedBlackTreeHasSameNumberOfBlackNodes(RedBlackTree<K,V> tree) {
        int blackHeight = tree.findBlackHeight(tree.root);
        assertThat(blackHeight)
                .overridingErrorMessage("Number of black nodes is not the same")
                .isNotEqualTo(-1);
    }

    private <K,V> void assertThatRedBlackTreeContainsExpectedValueByKey(RedBlackTree<K,V> tree, K key, V expectedValue) {
        Consumer<RedBlackTree.Node<K,V>> assertion = n -> {
            if (n.key.equals(key) && !n.value.equals(expectedValue)) {
                throw new AssertionError("Node " + n.key + " contains value: " + n.value + " but expected: " + expectedValue);
            }
        };

        tree.breadthFirstSearch(assertion);
    }

    private <K,V> void assertThatRedBlackTreeContainsKey(RedBlackTree<K,V> tree, K key) {
        boolean result = doesTreeContainKey(tree, key);
        assertThat(result)
                .overridingErrorMessage("Tree does not contain key: " + key)
                .isTrue();
    }

    private <K,V> void assertThatRedBlackTreeDoesNotContainKey(RedBlackTree<K,V> tree, K key) {
        boolean result = doesTreeContainKey(tree, key);
        assertThat(result)
                .overridingErrorMessage("Tree contains key: " + key)
                .isFalse();
    }

    private <K,V> boolean doesTreeContainKey(RedBlackTree<K,V> tree, K key) {
        AtomicBoolean keyWasFound = new AtomicBoolean(false);
        tree.breadthFirstSearch(n -> {
            if (n.key.equals(key)) keyWasFound.set(true);
        });

        return keyWasFound.get();
    }

    private <K,V> void assertThatRedBlackTreeHasNoConsecutiveRedNodes(RedBlackTree<K,V> tree) {
        Consumer<RedBlackTree.Node<K,V>> assertion = n -> {
            if (!n.isBlack && !isBlack(n.parent)) {
                throw new AssertionError("Tree contains consecutive red nodes: " + n.parent.key + " -> " + n.key);
            }
        };

        tree.breadthFirstSearch(assertion);
    }

    private <K,V> boolean isBlack(RedBlackTree.Node<K,V> node) {
        return node == null || node.isBlack;
    }
}
