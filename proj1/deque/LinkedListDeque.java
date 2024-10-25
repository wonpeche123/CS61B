package deque;

import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Iterable<T> {
    private Node<T> head;
    private int size;

    public class Node<T> {
        public Node<T> pre;
        public T data;
        public Node<T> next;
        public Node(T item) {
            data = item;
        }
    }
//    初始化
    public LinkedListDeque() {
        head = new Node<>(null);
        size = 0;
        head.next = head;
        head.pre = head;
    }

    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = head.next;
        head.next.pre = newNode;
        newNode.pre = head;
        head.next = newNode;
        size += 1;
    }

    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.pre = head.pre;
        head.pre.next = newNode;
        newNode.next = head;
        head.pre = newNode;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node<T> current = head.next;
        for(int i = 0; i < size(); i++){
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public T removeFirst(){
        if(size <= 0){
            return null;
        }
        Node<T> current = head.next;
        head.next = current.next;
        current.next.pre = head;
        size -= 1;
        return current.data;
    }

    public T removeLast(){
        Node<T> current = head.pre;
        current.pre.next = head;
        head.pre = current.pre;
        size -= 1;
        return current.data;
    }

    public T get(int index){
        if(index < 0 || index >= size()){
            return null;
        }
        Node<T> current = head.next;
        for(int i = 0; i < size(); i++){
            if(index == i){
                return current.data;
            }
        }
        return null;
    }

    public T getRecursive(int index){
        if(index < 0 || index >= size()){
            return null;
        }
        return getRecursiveHelper(index, head.next);
    }

    private T getRecursiveHelper(int index, Node<T> node){
        if (index == 0){
            return node.data;
        }
        return getRecursiveHelper(index - 1, node.next);
    }

    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o == null){
            return false;
        }
        if(!(o instanceof LinkedListDeque)){
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (this.size() != other.size()){
            return false;
        }
        for (int i = 0; i < size(); i++){
            if (this.get(i) != other.get(i)){
                return false;
            }
        }
        return true;

    }

    public Iterator<T> iterator(){
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T>{
        private int pos ;

        public DequeIterator(){
            pos = 0;
        }

        public boolean hasNext(){
            return pos < size;
        }

        @Override
        public T next() {
            T returnItem = get(pos);
            pos++;
            return returnItem;
        }
    }

}
