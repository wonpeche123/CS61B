package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int DEFAULT_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private double loadFactor;

    private int size;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array --16
     * @param maxLoad maximum load factor --0.75
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        size = 0;
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        Node newNode = new Node(key, value);
        return newNode;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }



    public void clear() {
        buckets = createTable(DEFAULT_SIZE);
        size = 0;
    }

    private int getIndex(K key) {
        return getBucketsIndex(key, buckets);
    }

    private int getBucketsIndex(K key, Collection<Node>[] curBuckets) {
        int hashcode = key.hashCode();
        int length = curBuckets.length;
        int index = Math.floorMod(hashcode, length);
        return index;
    }

    private Node getNode(K key) {
        return getNode(key, buckets);
    }

    private Node getNode(K key, Collection<Node>[] curBuckets) {
        int index = getBucketsIndex(key, curBuckets);
        for (Node node : curBuckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }


    public boolean containsKey(K key) {
        int index = getIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    private boolean overload() {
        return (double)size/ buckets.length > loadFactor;
    }

    private void resize() {
        Collection<Node>[] newBuckets = createTable(buckets.length * 2);
        Iterator<Node> iter = new nodeIterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            int index = getBucketsIndex(node.key, newBuckets);
            newBuckets[index].add(node);
        }
        buckets = newBuckets;
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        Node curNode = getNode(key);
        if (curNode != null) {
            curNode.value = value;
            return;
        }else {
            curNode = createNode(key, value);
        }
        buckets[index].add(curNode);
        size++;
        if (overload()) {
            resize();
        }
    }

    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        Iterator<K> iter = new keyIterator();
        for (K key : this) {
            keySet.add(iter.next());
        }
        return keySet;
    }

    public V remove(K key) {
//        throw new UnsupportedOperationException();
        int index = getIndex(key);
        Node curNode = getNode(key);
        if (curNode == null) {
            return null;
        }
        buckets[index].remove(curNode);
        size--;
        return curNode.value;
    }

    public V remove(K key, V value) {
//        throw new UnsupportedOperationException();
        int index = getIndex(key);
        Node curNode = getNode(key);
        if (curNode == null) {
            return null;
        }else if (curNode.value != value) {
            return null;
        }
        buckets[index].remove(curNode);
        size--;
        return curNode.value;
    }

    public Iterator<K> iterator() {
        return new keyIterator();
    }

    private class keyIterator implements Iterator<K> {
        private final Iterator<Node> iter = new nodeIterator();

        public boolean hasNext() {
            return iter.hasNext();
        }

        public K next() {
            return iter.next().key;
        }
    }

    private class nodeIterator implements Iterator<Node> {
        private final Iterator<Collection<Node>> bucketsIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> curBucketIterator;
        int curSize = 0;

        public boolean hasNext() {
            return curSize < size;
        }

        public Node next() {
            if (curBucketIterator == null || !curBucketIterator.hasNext()) {
                Collection<Node> curBuckets = bucketsIterator.next();
                while (curBuckets.size() == 0) {
                    curBuckets = bucketsIterator.next();
                }
                curBucketIterator = curBuckets.iterator();
            }
            curSize++;
            return curBucketIterator.next();
        }
    }



}
