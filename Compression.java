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

import java.util.HashMap;

public class Compression {
	private int[] valAndFreq;
	private HashMap<Integer, String> originalAndNewCodes;
	private HuffmanCodeTree newHCT;
	
	/**
	 * Creates a fair priority queue and Huffman Code Tree from the given array of frequencies
	 * @param valAndFreqArray the array containing the frequencies for each character
	 */
	public Compression(int[] valAndFreqArray) {
		valAndFreq = valAndFreqArray;
        FairPriorityQueue<TreeNode> newFairPriorityQueue = new FairPriorityQueue<TreeNode>();
        
        // For each non-zero element in the given array, a TreeNode with a value equal to the 
        // position of the current element in the array and a frequency equal to the element, 
        // is created and inserted into a fair priority queue according to its position 
        // (TreeNodes with lower frequencies are located closer to the beginning of the queue, 
        // and if a TreeNode has the same frequency as another TreeNode that is already in the 
        // queue, it is inserted after the TreeNode already existing in the queue)
        for (int i = 0; i < valAndFreq.length; i++) {
            if (valAndFreq[i] > 0) {
                TreeNode currentNode = new TreeNode(i, valAndFreq[i]);
                newFairPriorityQueue.enqueue(currentNode);
            }
        }

        // A Huffman Code Tree is created using the newly created fair priority queue 
        // (which contains TreeNodes that have values and the frequencies associated with those
        // values)
        HuffmanCodeTree newHuffmanCodeTree = new HuffmanCodeTree(newFairPriorityQueue);
        newHCT = newHuffmanCodeTree;
        originalAndNewCodes = treeTraversal(newHuffmanCodeTree);
	}
	
	/**
	 * Returns a new HashMap containing the original and new string of bits (which is found by
	 * traversing a Huffman Code Tree) for each character
	 * @param newHuffmanCodeTree the huffmanCode tree from which the original and new 
	 * codes/string of bits for each character will be obtained
	 * @return a new HashMap which contains the original string of bits for each character (as
	 * keys) and the new string of bits representing each character as a result of compression (as 
	 * values)
	 */
    public HashMap<Integer, String> treeTraversal(HuffmanCodeTree newHuffmanCodeTree) {
        HashMap<Integer, String> originalAndNewCodes = new HashMap<Integer, String>();
        treeTraversalHelper(newHuffmanCodeTree.getRoot(), originalAndNewCodes, "");
        return originalAndNewCodes;
    }
    
    /**
     * Traverses through the Huffman Code Tree and adds the original and new codes 
     * for each character to a Hashmap
     * @param current the current node in the Huffman Code Tree that is being evaluated
     * @param result the HashMap to which the original and new codes for each character
     * will need to be added to
     * @param newCode String representing the new code for a character
     */
    private void treeTraversalHelper(TreeNode current, HashMap<Integer, String> result, 
    		String newCode) {
        // Base Case: If the current node is a leaf, a character has been reached so the
    	// value of the leaf node (original string of bits representing the character) and 
    	// the newCode (new string of bits representing the value) are added to the given map
        if (current.isLeaf()) {
            result.put(current.getValue(), newCode);
        }
        // Recursive Case: Otherwise, go left and append 0 to the newCode and go right and append 1
        // to the newCode
        else if (current != null) {
            treeTraversalHelper(current.getLeft(), result, newCode + "0");
            treeTraversalHelper(current.getRight(), result, newCode + "1");
        }
    }
    
    /**
     * Returns an array containing the frequencies for each character 
     * @return an array containing the frequencies for each character
     */
    public int[] getValAndFreqArray() {
    	return valAndFreq;
    }
    
    /**
     * Returns a HashMap containing the original codes and new codes (as a result of compression)
     * for each character
     * @return a HashMap containing the original and new codes for each character
     */
    public HashMap<Integer, String> getOriginalAndNewCodesMap(){
    	return originalAndNewCodes;
    }
    
    /**
     * Returns a Huffman Code Tree that was constructed based on a fair priority queue
     * @return a HuffmanCode Tree created from a fair priority queue
     */
    public HuffmanCodeTree getNewHCT() {
    	return newHCT;
    } 
}
