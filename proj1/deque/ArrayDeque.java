package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>,Deque<T> {
    private T[] array = (T[]) new Object[8];
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    private void resize(int newSize) {
        T[] newArray = (T[]) new Object[newSize];
        for (int i = 0; i < size; i++) {
            int j = (nextFirst + 1 + i) % array.length;
            newArray[i] = array[j];
        }
        array = newArray;
        nextFirst = newSize - 1;
        nextLast = size;
    }
    public void addFirst(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        array[nextFirst] = item;
        size += 1;
        nextFirst = (nextFirst - 1 + array.length) % array.length;
    }

    public void addLast(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        array[nextLast] = item;
        size += 1;
        nextLast = (nextLast + 1) % array.length;
    }

//    public boolean isEmpty() {
//        return size == 0;
//    }

    public int size() {
        return size;
    }

    public void printDeque(){
        for (int i = 0; i < size; i++) {
            int j = (nextFirst + 1 + i) % array.length;
            System.out.print(array[j] + " ");
        }
        System.out.println();
    }
    private void deduce(){
        if (size < array.length * 0.25 && size > 8) {
            resize(size * 2);
        }
    }
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = (nextFirst + 1) % array.length;
        T item = array[nextFirst];
        size -= 1;
        deduce();
        return item;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = (nextLast - 1 + array.length) % array.length;
        T item = array[nextLast];
        size -= 1;
        deduce();
        return item;
    }

    public T get(int index) {
        return array[(nextFirst + index + 1) % array.length];
    }

    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o == null){
            return false;
        }
        if(!(o instanceof ArrayDeque)){
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
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

    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int count;
        public DequeIterator() {
            count = 0;
        }

        public boolean hasNext() {
            return count < size;
        }

        public T next() {
            T item = get(count);
            count += 1;
            return item;

        }
    }

}
