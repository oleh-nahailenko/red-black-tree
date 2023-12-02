package datastructure;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class RedBlackTreeTest {

    @Test
    void breadthFirstSearch_findsAllNodes() {
        //GIVEN
        List<Integer> expectedKeys = List.of(3, 5, 1, 7);
        List<Integer> actualKeys = new ArrayList<>(4);
        RedBlackTree<Integer, Object> tree = createConsecutiveRedTree();

        //WHEN
        tree.breadthFirstSearch(n -> actualKeys.add(n.key));

        //THEN
        assertThat(expectedKeys).hasSameElementsAs(actualKeys);
    }

    @Test
    void findBlackHeight_subtreesHaveSameBlackHeight_returnsBlackHeight() {
        //GIVEN
        RedBlackTree<Integer, Object> tree = createConsecutiveRedTree();
        tree.root.rightChild.isBlack = true;
        tree.root.leftChild.isBlack = true;

        //WHEN
        int blackHeight = tree.findBlackHeight(tree.root);

        //THEN
        assertThat(blackHeight).isEqualTo(2);
    }

    @Test
    void findBlackHeight_subtreesHaveNotSameBlackHeight_returnsMinusOne() {
        //GIVEN
        RedBlackTree<Integer, Object> tree = createConsecutiveRedTree();
        tree.root.isBlack = true;
        tree.root.rightChild.isBlack = true;

        //WHEN
        int blackHeight = tree.findBlackHeight(tree.root);

        //THEN
        assertThat(blackHeight).isEqualTo(-1);
    }

    // TODO: 12/2/2023 : Create test for 'insert' method

    // TODO: 12/2/2023 : Create test for 'get' method

    // TODO: 12/2/2023 : Create test for 'delete' method

    private RedBlackTree<Integer,Object> createConsecutiveRedTree() {
        RedBlackTree<Integer, Object> tree = new RedBlackTree<>(Integer::compareTo);

        RedBlackTree.Node<Integer, Object> root = new RedBlackTree.Node<>(3, new Object());
        RedBlackTree.Node<Integer, Object> rightChild = new RedBlackTree.Node<>(5, new Object());
        RedBlackTree.Node<Integer, Object> leftChild = new RedBlackTree.Node<>(1, new Object());
        RedBlackTree.Node<Integer, Object> rightRightChild = new RedBlackTree.Node<>(7, new Object());

        tree.root = root;
        tree.root.isBlack = true;

        root.leftChild = leftChild;
        leftChild.parent = root;

        root.rightChild = rightChild;
        rightChild.parent = root;

        rightChild.rightChild = rightRightChild;
        rightRightChild.parent = rightChild;

        return tree;
    }

    private <K,V> void assertThatRedBlackTreeHasSameNumberOfBlackNodes(RedBlackTree<K,V> tree) {
        int blackHeight = tree.findBlackHeight(tree.root);
        Assertions.assertNotEquals(blackHeight, -1);
    }

    private <K,V> void assertThatRedBlackTreeHasNoConsecutiveRedNodes(RedBlackTree<K,V> tree) {
        Consumer<RedBlackTree.Node<K,V>> action = n -> {
            if (!n.isBlack && !isBlack(n.parent)) {
                throw new IllegalStateException("Tree contains consecutive red nodes: " + n.parent.key + " -> " + n.key);
            }
        };

        tree.breadthFirstSearch(action);
    }

    private <K,V> boolean isBlack(RedBlackTree.Node<K,V> node) {
        return node == null || node.isBlack;
    }
}
