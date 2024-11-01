package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node<K, V> root;
    //Node
    private class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left, right;
        private int size;

        public Node(K keyPassIn, V valuePassIn, int size) {
            key = keyPassIn;
            value = valuePassIn;
            left = null;
            right = null;
            this.size = size;
        }

    }

    public BSTMap() {
    }

    private boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    private boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) !=null;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        if (key == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node.value;
        } else if (cmp < 0) {
            return get(node.left, key);
        }else {
            return get(node.right, key);
        }
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        if (value == null) {
            delete(key);
            return;
        }
        root = put(root, key, value);
    }

    private Node put(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            node.value = value;
        }else if (cmp < 0) {
            node.left = put(node.left, key, value);
        }else{
            node.right = put(node.right, key, value);
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    private Node deleteMax(Node<K, V> node) {
        if (node == null) {
            return null;
        }
        if (node.right != null) {
            node.right = deleteMax(node.right);
        }else {
            return node.left;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }
    private void delete(K key) {
        if (key == null) {
            return;
        }
        root = delete(root, key);
    }

    private Node delete(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        }else if (cmp > 0) {
            node.right = delete(node.right, key);
        }else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node temp = node;
            node = max(node.right);
            node.right = deleteMax(node.right);
            node.left = temp.left;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    private Node max(Node node) {
        if (node.right != null) {
            return max(node.right);
        }
        return node;
    }
    //BSTMap method
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return !(get(key) == null);
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key);
        printInOrder(node.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        delete(key);
        return value;
    }

    @Override
    public V remove(K key, V value) {
        V existValue = get(key);
        if (existValue == value) {
            delete(key);
            return value;
        }else {
            return null;
        }
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

}
