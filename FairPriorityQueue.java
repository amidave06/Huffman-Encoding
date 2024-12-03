/*  Student information for assignment:
 *
 *  On OUR honor, Ami and Sneha, this programming assignment is OUR own work
 *  and WE have not provided this code to any other student.
 *
 *  Number of slip days used: 2
 *
 *  Student 1 (Student whose Canvas account is being used): Ami Dave
 *  UTEID: ad56333
 *  email address: amidave@utexas.edu
 *  Grader name: Devon
 *
 *  Student 2: Sneha Bhamidipaty
 *  UTEID: ssb3462
 *  email address: snehab@utexas.edu
 */

import java.util.ArrayList;

public class FairPriorityQueue<E extends Comparable<? super E>> {
	private int size;
	private ArrayList<E> newList;

	/**
	 * Default Constructor
	 */
	public FairPriorityQueue() {
		newList = new ArrayList<E>();
		size = 0;
	}
	
	/**
	 * Adds the given element to the correct position in the list (which represents the queue)
	 * @param element the element to be added to the queue
	 */
	public void enqueue(E element) {
		// If there are no elements in the list, the specified element is added to the list
		if (size() == 0) {
			newList.add(element);
		} else {
			// Otherwise, the given element is added to the list after the last element which 
			// has the same value as the specified element
			int index = 0;
			while (index < size() && element.compareTo(newList.get(index)) >= 0) {
				index++;
			}
			newList.add(index, element);
		}
		size++;
	}
	
	
	/**
	 * Removes and returns the first element from the list (which represents the queue)
	 * @return the element that was removed from the queue
	 */
	public E dequeue() {
		size--;
		// Removes and returns the first element from this list
		return newList.remove(0);
	}


	/**
	 * Returns the size of the queue
	 * @return the size of the queue
	 */
	public int size() {
		return size;
	}
}