package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {
//    此处的comparator就是一个比较函数，并不是一系列数据，数据来自Deque本身的array
    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max(Comparator<T> c) {
        int maxIndex = 0;
        for (int i = 0; i < size(); i++) {
            if (c.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

    public T max() {
        return max(comparator);
    }

//    public boolean equals(Object o) {
//        if (o == null) {
//            return false;
//        }
//        if (o == this) {
//            return true;
//        }
//        if (!(o instanceof MaxArrayDeque)) {
//            return false;
//        }
//        if (((MaxArrayDeque<T>) o).max() != max()) {
//            return false;
//        }
//        return super.equals(o);
//    }


}
