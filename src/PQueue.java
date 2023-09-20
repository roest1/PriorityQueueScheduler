
import java.util.*;

/**
 * This class models a priority min-queue that uses an array-list-based min
 * binary heap
 * that implements the PQueueAPI interface. The array holds objects that
 * implement the
 * parameterized Comparable interface.
 * 
 * @author Duncan, Riley Oest
 * @param <E> the priority queue element type.
 * @author William Duncan
 * 
 * <pre>
 * Date: 09/21/2022
 * Course: csc 3102
 * Programming Project # 1
 * Instructor: Dr. Duncan
 *</pre>
 */

public class PQueue<E extends Comparable<E>> implements PQueueAPI<E> {
    /**
     * A complete tree stored in an array list representing the
     * binary heap
     */
    private ArrayList<E> tree;
    /**
     * A comparator lambda function that compares two elements of this
     * heap when rebuilding it;
     *
     * x < y ⟹ cmp.compare(x, y) = -1
     * x > y ⟹ cmp.compare(x, y) = 1
     * x = y ⟹ comp.compare(x, y) = 0
     */
    private Comparator<? super E> cmp;

    /**
     * Constructs an empty PQueue using the compareTo method of its data type as the
     * comparator
     */
    public PQueue() {
        tree = new ArrayList<E>();
        cmp = (one, other) -> one.compareTo(other);
    }

    /**
     * A parameterized constructor that uses an externally defined comparator
     * 
     * @param fn - a trichotomous integer value comparator function
     */
    public PQueue(Comparator<? super E> fn) {
        this.cmp = fn;
        tree = new ArrayList<E>(); // implement this method
    }

    /**
     * Returns the number of elements in the priority queue
     * @param null if tree is empty
     * @param boolean if pqueue is empty returns true else false
     */

    public boolean isEmpty() {
        return this.tree == null || this.tree.size() == 0 ? true : false;
    }
    
    /**
     * Insert object into queue
     */
    public void insert(E obj) {
        tree.add(obj); // adds to the end of the list
        int place = tree.size() - 1;
        int parent = getParent(place);
        while (parent >= 0 && cmp.compare(tree.get(place), (tree.get(parent))) < 0) {
            swap(place, parent);
            place = parent;
            parent = getParent(place);
        }
    }

    /**
     *  removes the top element of the min heap 
     * @return root of heap
     * @throws PQueueException when pq.isEmpty() is true
     */
    public E remove() throws PQueueException {
        if (tree.size() >= 1) { 
            E min = tree.get(0);
            int last = tree.size() - 1;
            tree.set(0, tree.get(last));
            tree.remove(last);
            rebuild(0, last);
            return min;
        }
        return null;
    }

    /**
     * Returns the top element of the min heap
     * @return root node 
     * @throws PQueueException
     */
    public E peek() throws PQueueException {
        return !tree.isEmpty() ? tree.get(0) : null;
    }

    /**
     * Returns the number of elements in the priority queue
     * @return 
     */
    public int size() {
        return tree.isEmpty() ? 0 : tree.size();
    }

    /**
     * Swaps a parent and child elements of this heap at the specified indices
     * 
     * @param place  an index of the child element on this heap
     * @param parent an index of the parent element on this heap
     */
    private void swap(int place, int parent) {
         Collections.swap(tree, place, parent);
    }
    
    /**
     * Rebuilds the min heap from the root element
     * @param i index to heapify from
     * @param eSize updated heap size 
     */
    private void min_heapify(int i, int eSize) {
        int leftIndex = getLeft(i);
        int rightIndex = leftIndex + 1;
        int min = i;

        if (leftIndex < eSize && leftIndex > 0) {
            if (cmp.compare(tree.get(leftIndex), tree.get(min)) == -1) {
                min = leftIndex;
            }
        }
        if (rightIndex < eSize && rightIndex > 0) {
            if (cmp.compare(tree.get(rightIndex), tree.get(min)) == -1) {
                min = rightIndex;
            }
        }
        if (min != i) {
            swap(min, i);
            min_heapify(min, eSize);
        }
    }
    /**
     * Rebuilds the heap to ensure that the heap property of the tree is preserved.
     * 
     * @param root  the root index of the subtree to be rebuilt
     * @param eSize the size of this tree
     */
    private void rebuild(int root, int eSize) {
       for (int i = (int) Math.floor((eSize - 1) / 2); i >= root; i--) {
            min_heapify(i, eSize);
       }
    }

    /**
     * Returns the parent index of the given node(i)
     * 
     * @param i index of the node to find parent of.
     * @return parent index of the given node(i)
     */
    private static int getParent(int i) {
        return (int) Math.floor((i - 1) / 2);
    }

    /**
     * Returns the index of left child in binary heap tree
     * 
     * @param i index of parent
     * @return index of left child.
     */
    private static int getLeft(int i) {
        return 2 * i + 1;
    }
}
