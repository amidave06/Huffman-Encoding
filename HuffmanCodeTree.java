public class HuffmanCodeTree {
	private TreeNode root;
	private int treeSize;
	private int queueSize;
	
	/**
	 * Creates a HuffmanCodeTree based on the given fair priority queue
	 * @param newFairPriorityQueue the fair priority queue from which this Huffman Code Tree 
	 * needs to be constructed
	 */
	public HuffmanCodeTree(FairPriorityQueue<TreeNode> newFairPriorityQueue) {
		queueSize = newFairPriorityQueue.size();
		treeSize += queueSize;
		while (newFairPriorityQueue.size() >= 2) {
			// Removes the first element from the given fair priority queue and creates a TreeNode
			// for it
			TreeNode tempLeft = newFairPriorityQueue.dequeue();
			// Removes the second element from the given fair priority queue and creates a TreeNode
			// for it
			TreeNode tempRight = newFairPriorityQueue.dequeue();
			int combinedFrequency = tempLeft.getFrequency() + tempRight.getFrequency();
			// Creates a parent TreeNode with a value of -1 and a frequency equal to the sum of the 
			// two values which were just dequeued from the queue
			TreeNode parent = new TreeNode(-1, combinedFrequency);
			// Sets the root of this tree to this new parent node
			root = parent;
			// Sets the left child of this parent node to the first element that was removed
			// from the queue
			parent.setLeft(tempLeft);
			// Sets the right child of this parent node to the second element that was removed
			// from the queue
			parent.setRight(tempRight);
			// Adds this new parent node to queue
			newFairPriorityQueue.enqueue(parent);
			treeSize++;
		}
	}
	
	/**
	 * Returns the total number of nodes in this Huffman Code Tree
	 * @return the size of this Huffman Code Tree
	 */
	public int treeSize() {
		return treeSize;
	}
	
	/**
	 * Returns the total number of leaf nodes in this Huffman Code Tree 
	 * @return the size of the fair priority queue (which is the number of leaf nodes in this tree) 
	 * that was used to construct this Huffman Code Tree 
	 */
	public int queueSize() {
		return queueSize;
	}
	
	/**
	 * Returns the root of this Huffman Code Tree
	 * @return the root of this Huffman Code Tree
	 */
	public TreeNode getRoot() {
		return root;
	}

	/**
	 * Sets the root of this Huffman Code Tree to the given TreeNode
	 * @param node the node to set the root of this Huffman Code Tree to
	 */
	public void setRoot(TreeNode node) {
		root = node;
	}
}
