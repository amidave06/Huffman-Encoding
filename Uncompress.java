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

public class Uncompress {
	private HuffmanCodeTree huffmanCodeTree;
	
	/**
	 * Creates a fair priority queue and Huffman Code Tree from the given array of frequencies
	 * @param valAndFreqArray the array containing the frequencies for each character
	 */
	public Uncompress(int[] valAndFreqArray) {
        FairPriorityQueue<TreeNode> newFairPriorityQueue = new FairPriorityQueue<TreeNode>();
        // For each non-zero element in the given array, a TreeNode with a value equal to the 
        // position of the current element in the array and a frequency equal to the element, 
        // is created and inserted into a fair priority queue according to its position 
        // (TreeNodes with lower frequencies are located closer to the beginning of the queue, 
        // and if a TreeNode has the same frequency as another TreeNode that is already in the 
        // queue, it is inserted after the TreeNode already existing in the queue)
        for (int i = 0; i < valAndFreqArray.length; i++) {
            if (valAndFreqArray[i] > 0) {
                TreeNode currentNode = new TreeNode(i, valAndFreqArray[i]);
                newFairPriorityQueue.enqueue(currentNode);
            }
        }
        // The PEOF value is added to the fair priority queue
        newFairPriorityQueue.enqueue(new TreeNode(256, 1));

        // A Huffman Code Tree is created using the newly created fair priority queue 
        // (which contains TreeNodes that have values and the frequencies associated with those
        // values)
        huffmanCodeTree = new HuffmanCodeTree(newFairPriorityQueue);
        
        if (huffmanCodeTree == null) {
            throw new NullPointerException("Huffman code tree is null");
        }
	}
	
	/**
	 * Returns a Huffman Code Tree
	 * @return a Huffman Code tree
	 */
	public HuffmanCodeTree getHuffmanCodeTree() {
        if (huffmanCodeTree == null) {
            throw new NullPointerException("Huffman code tree is null");
        }
		return huffmanCodeTree;
	}	
}
