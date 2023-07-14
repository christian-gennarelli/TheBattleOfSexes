import Individual.*;
import java.util.LinkedList;

public class IndividualPool<T extends Individual> {

    private LinkedList<T> storeList = new LinkedList<>();
    private int size = -1;

    public synchronized void add(T dude) {
        storeList.add(dude);
        ++size;
    }

    public synchronized T remove() {
        --size;
        return storeList.poll();
    }

    public synchronized boolean empty() {
        return size == -1;
    }

    public synchronized int getSize() {
        return size + 1;
    }
}
