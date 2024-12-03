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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanCodeTree huffmanCodeTree;
    private int headerInformation;
    private Compression newCompressedFile;
    private int totalBitsOriginal;
    private int totalBitsCompressed;

    
    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
    	totalBitsOriginal = 0;
    	totalBitsCompressed = 0;
    	headerInformation = headerFormat;
    	int[] valAndFreq = new int[257];

    	BitInputStream newBitInputStream = new BitInputStream(in);
    	int currentByte = newBitInputStream.readBits(BITS_PER_WORD);
    	// Reads one byte at a time from the input file, converts the byte to its decimal
    	// representation, and increments the frequency corresponding to the decimal representation
    	// in the valAndFreq array
    	while (currentByte != -1) {
    		valAndFreq[currentByte]++;
    		// Increments the total number of bits in the original file by 8
    		totalBitsOriginal += BITS_PER_WORD;
    		currentByte = newBitInputStream.readBits(BITS_PER_WORD);
    	}
    	// Increments the frequency corresponding to PEOF value
    	valAndFreq[PSEUDO_EOF]++;
    	
    	showString("Array containing values and frequencies has been created.");
    	
    	newBitInputStream.close();
    	
    	// Creates a new huffman code tree from the given array containing values and their
    	// corresponding frequencies in the original file
    	newCompressedFile = new Compression(valAndFreq);
    	HashMap<Integer, String> originalAndNewCodes = 
    			newCompressedFile.getOriginalAndNewCodesMap();
    	huffmanCodeTree = newCompressedFile.getNewHCT();
    	
    	// Adds the number of bits representing the magic number and the number of bits representing  
    	// the header format (STF or SCF) to the total number of bits in the compressed file
    	totalBitsCompressed = BITS_PER_INT * 2;
    	
    	// Adds the length of each of the new codes that were generated for each character
    	// (according to their frequency) to the total number of bits in the compressed file
    	for (Integer key : originalAndNewCodes.keySet()) {
        	totalBitsCompressed += valAndFreq[key] * originalAndNewCodes.get(key).length();
    	}
    	
    	// If the header format is SCF, adds the 32 bits for each character to the total number of
    	// bits in the compressed file
        if (headerInformation == STORE_COUNTS) {
        	totalBitsCompressed += BITS_PER_INT * ALPH_SIZE;
        } else if (headerInformation == STORE_TREE) {
        	// Otherwise, if the header format is STF, adds 32 bits which represents the size
        	// of the huffman code tree, the size (total number of nodes) of the huffman code tree, 
        	// and nine times the number of leaf nodes in the huffman code tree to the total
        	// number of bits in the compressed file
        	totalBitsCompressed += BITS_PER_INT + huffmanCodeTree.treeSize() + (BITS_PER_WORD + 1) 
        			* originalAndNewCodes.keySet().size();
        }
    	return totalBitsOriginal - totalBitsCompressed;
    }
    
    
    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
    	if (totalBitsOriginal - totalBitsCompressed < 0 && !force) {
			return -1;
    	}
		
		int numBitsWritten = 0;
		
		BitInputStream newInputStream = new BitInputStream(in);
		BitOutputStream newOutputStream = new BitOutputStream(out);
		
		// Writes out the bits corresponding to the magic number to the output file and adds 
		// the number of bits corresponding to the magic number to the total number of bits
		// written out to the output file
		newOutputStream.writeBits(BITS_PER_INT, MAGIC_NUMBER);
		showString("Magic number has been written out.");

		numBitsWritten += BITS_PER_INT;
		
		// If the header format is SCF, adds the number of bits written out to the output file
		// for this header format to the total number of bits written out to the output file
		if (headerInformation == STORE_COUNTS) {
			numBitsWritten += standardCountFormat(newOutputStream, 
					newCompressedFile.getValAndFreqArray());
		} else if (headerInformation == STORE_TREE) {
			// Otherwise, if the header format is STF, adds the number of bits written out to the 
			// output file for this header format to the total number of bits written out 
			// to the output file
			numBitsWritten += standardTreeFormat(newOutputStream, huffmanCodeTree);
		}
		showString("Header has been written out.");

		// Adds the bits representing all of the data written out to the output file to the
		// total number of bits written out to the output file
		numBitsWritten += writingOutData(newInputStream, newOutputStream, 
				newCompressedFile.getOriginalAndNewCodesMap());
		showString("Compressed data has been written out.");
		
		newInputStream.close();
		newOutputStream.close();
		
		return numBitsWritten;
    }
    
    
    /**
     * Returns the number of bits that were used to write out the standard count format to the
     * output file
     * @param newOutputStream BitOutputStream that is used to write one bit at a time to the output
     * file
     * @param valAndFreq array which contains the frequencies of each character in the original
     * file
     * @return the number of bits written out to the output file that were used to represent
     * the standard count format
     * @throws IOException
     */
    private int standardCountFormat(BitOutputStream newOutputStream, int[] valAndFreq) 
    		throws IOException {
    	// Writes out the bits representing the standard count format to the output file
    	newOutputStream.writeBits(BITS_PER_INT, STORE_COUNTS);
    	// Adds the number of bits used to represent the standard count format to the total number
    	// of bits for the standard count format header
    	int numBitsSCF = BITS_PER_INT;
        for (int i = 0; i < ALPH_SIZE; i++) {
        	// Writes out the bits corresponding to the frequency of each character to the output
        	// file
        	newOutputStream.writeBits(BITS_PER_INT, valAndFreq[i]);
        	// Adds the number of bits used to represent each frequency to the total number of bits
        	// for the standard count format header
        	numBitsSCF += BITS_PER_INT;
        }
        return numBitsSCF;
    }
    
    
    /**
     * Returns the number of bits that were used to write out the standard tree format to the
     * ouput file
     * @param newOutputStream BitOutputStream that is used to write one bit at a time to the output
     * file
     * @param newHCT the HuffmanCodeTree that contains all of the characters from the original
     * file and their corresponding frequencies
     * @return the number of bits written out to the output file that were used to represent the
     * standard tree format
     * @throws IOException
     */
    private int standardTreeFormat(BitOutputStream newOutputStream, HuffmanCodeTree newHCT) 
    		throws IOException {
    	int numBitsSTF = 0;
    	// Writes out the bits representing the standard tree format to the output file
    	newOutputStream.writeBits(BITS_PER_INT, STORE_TREE);
    	// Adds the number of bits used to represent the standard tree format to the total number
    	// of bits for the standard tree format header
    	numBitsSTF += BITS_PER_INT;
    	// Writes out the bits representing the size of the huffman code tree to the output file
    	newOutputStream.writeBits(BITS_PER_INT, (newHCT.queueSize() * 9) + newHCT.treeSize());
    	// Adds the number of bits representing the size of the huffman code tree to the total
    	// number of bits for the standard tree format header
    	numBitsSTF += BITS_PER_INT;
    	// Adds the number of bits representing the nodes of the tree to the total number
    	// of bits for the standard tree format header
    	numBitsSTF += preOrderTraversalHCT(newHCT.getRoot(), newOutputStream);
    	return numBitsSTF;
    }
    
    
    /**
     * Returns the number of bits that were used to write out values representing the nodes
     * in the Huffman Code Tree
     * @param current a node in the Huffman Code Tree that is currently being evaluated
     * @param newOutputStream BitOutput Stream that is used to write one bit at a time to the 
     * output file
     * @return the number of bits written to the output file that were used to represent
     * the nodes in the Huffman Code Tree
     */
    private int preOrderTraversalHCT(TreeNode current, BitOutputStream newOutputStream) {
    	int numBits = 0;
    	// Base Case: If the current node is a leaf, writes 1 to the output file to represent
    	// a leaf node and writes out the value associated with that leaf node to the output
    	// file
    	if (current.isLeaf()) {
    		newOutputStream.writeBits(1, 1);
    		newOutputStream.writeBits(BITS_PER_WORD + 1, current.getValue());
    		// Adds 10 to the number of bits representing the standard tree format 
    		// (one bit to represent a leaf node, and 9 bits to represent the value of the 
    		// leaf node)
    		numBits += BITS_PER_WORD + 2;
    	} else {
    		// Recursive Case: Otherwise writes 0 to the ouput file to represent a node that is not
    		// a leaf node, and recursively calls this method on the left and right nodes of the
    		// current node
    		newOutputStream.writeBits(1, 0);
    		// Adds one to the number of bits representing the standard tree format to represent
    		// a node that is not a leaf
    		numBits++;
    		numBits += preOrderTraversalHCT(current.getLeft(), newOutputStream);
    		numBits += preOrderTraversalHCT(current.getRight(), newOutputStream);
    	}
    	return numBits;
    }
    
    
    /**
     * Returns the number of bits that were used to write out to the output file the new codes 
     * representing the original data from the input file  
     * @param newInputStream BitInputStream that is used to read bits from the input file
     * @param newOutputStream BitOutputStream that is used to write one bit at a time to the
     * output file
     * @param originalAndNewCodes HashMap containing the codes representing each character
     * as keys and the new codes (constructed from the Huffman Code Tree) for each character as 
     * values
     * @return the total number of bits written out to the output file that were used to represent
     * the data from the input file
     * @throws IOException
     */
    private int writingOutData(BitInputStream newInputStream, BitOutputStream newOutputStream, 
    		HashMap<Integer, String> originalAndNewCodes) throws IOException {
    	int numBitsWrittenOut = 0;
        int currentByte = newInputStream.read();
    	while (currentByte != -1) {
    		// Writes out the bits of the new code representing the current byte from the input
    		// file
			for (int i = 0; i < originalAndNewCodes.get(currentByte).length(); i++) {
				newOutputStream.writeBits(1, originalAndNewCodes.get(currentByte).charAt(i) - '0');
				// Adds the length of the new code representing the current byte to the total
				// number of bits in the output file
				numBitsWrittenOut++;
			}
            currentByte = newInputStream.read();
        }
    	// Writes out the PEOF value to the output file
		for (int i = 0; i < originalAndNewCodes.get(PSEUDO_EOF).length(); i++) {
			newOutputStream.writeBits(1, originalAndNewCodes.get(PSEUDO_EOF).charAt(i) - '0');
			// Adds the length of the PEOF value to the total number of bits in the output file 
			numBitsWrittenOut++;
		}
		newOutputStream.close();
		return numBitsWrittenOut;
    }
    
    
    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
    	BitInputStream newBitInputStream = new BitInputStream(in);
    	BitOutputStream newOutputStream = new BitOutputStream(out);

    	int[] valAndFreq = new int[256];
    	
		int magicNumber = newBitInputStream.readBits(BITS_PER_INT);
    	int headerInfo = newBitInputStream.readBits(BITS_PER_INT);
    	
    	// If the header format is SCF, reads in each byte from the header of the file, 
    	// which represents a frequency, and assigns this frequency to the position associated with
    	// the value corresponding to this frequency 
    	if (headerInfo == STORE_COUNTS) {
			for (int i = 0; i < ALPH_SIZE; i++) {
				valAndFreq[i] = newBitInputStream.readBits(BITS_PER_INT);
			}
    	}
		showString("The value and frequency array has been reconstructed.");

    	
    	// Creates a new huffman code tree from the array which contains values and their
    	// corresponding frequencies (if the header format was SCF) that was created from the header
    	Uncompress newUncompressedFile = new Uncompress(valAndFreq);
    	HuffmanCodeTree uncompressHCT = newUncompressedFile.getHuffmanCodeTree();

    	// Otherwise, if the header format was SCF, reconstructs the Huffman Code Tree based
    	// on the information from the header
    	if (headerInfo == STORE_TREE) {
			uncompressHCT.setRoot(uncompressTreeFormat(newBitInputStream));
		}
		return writeBitsUncompress(newBitInputStream, uncompressHCT.getRoot(), newOutputStream);
    }
    
    
    /**
     * Traverses through the Huffman Code Tree until each leaf node has been reached
     * and returns the total number of bits that were used to write out to the output file the 
     * uncompressed data
     * @param newInputStream BitInputStream that is used to read bits from the input file
     * @param currentNode a node in the HuffmanCode Tree that is currently being evaluated
     * @param newOutputStream BitOutputStream that is used to write one bit at a time to the
     * output file
     * @return the total number of bits written out that represent the uncompressed data
     * @throws IOException
     */
	private int writeBitsUncompress(BitInputStream newInputStream, TreeNode currentNode, 
			BitOutputStream newOutputStream) throws IOException {
		TreeNode root = currentNode;
		currentNode = root;
		int numBitsUncompressedData = 0;
		int currentBit = newInputStream.readBits(1);
		boolean done = false;
		while (currentBit != -1 && !done) {
			// If the current bit is 0, assigns the current node its left node, otherwise, assigns
			// the current node its right node
			currentNode = (currentBit == 0) ? currentNode.getLeft() : currentNode.getRight();
			if (currentNode.isLeaf()) {
				// If the current node is a leaf and represents the PEOF value, the end of the data
				// has been reached
				if (currentNode.getValue() == PSEUDO_EOF) {
					done = true;
				}
				else {
					// Otherwise, if the current node is a leaf but does not represent the PEOF
					// value, writes out the bits representing the value of the current node
					// and starts at the top of the Huffman Code Tree again
					newOutputStream.writeBits(BITS_PER_WORD, currentNode.getValue());
					currentNode = root;
					// Adds the number of bits that were written out to the total number of bits
					// representing the uncompressed data
					numBitsUncompressedData += BITS_PER_WORD;
				}
			}
			currentBit = newInputStream.readBits(1);
		}
		newOutputStream.close();
		return numBitsUncompressedData;
	}
	
	
	/**
	 * Returns the root of tree created from the standard tree header format
	 * @param newBitInputStream BitInputStream that is used to read bits from the input file
	 * @return a TreeNode representing the root of the tree created from the standard tree
	 * header format
	 * @throws IOException
	 */
	private TreeNode uncompressTreeFormat(BitInputStream newBitInputStream) 
			throws IOException {
	    int sizeDecompTree = newBitInputStream.readBits(BITS_PER_INT);
	    TreeNode root = new TreeNode(0, 0);
	    // Assigns the root of the new tree created from the standard tree header format to the 
	    // result of calling uncompressTreeFormatHelper
	    root = uncompressTreeFormatHelper(newBitInputStream, root);
	    return root;

	}
	
	
	/**
	 * Helper method for uncomoressTreeFormat that creates a tree by reading in the bits 
	 * associated with the standard tree format header
	 * @param newBitInputStream BitInputStream that is used to read bits from the input file
	 * @param currentNode the node to which other nodes are currently being added to
	 * @return a TreeNode representing the root of the tree created from the STF header
	 * with all of the connected nodes
	 * @throws IOException
	 */
	private TreeNode uncompressTreeFormatHelper(BitInputStream newBitInputStream, 
			TreeNode currentNode) throws IOException {
		int currentBit = newBitInputStream.readBits(1);
		if (currentBit == 0) { 
			// If the current bit is 1, this represents a node that is not a leaf node, so create
			// a new node with a value of 0 and set the left and right node of this new node to
			// the result of recursively calling this method on current node
			TreeNode newNode = new TreeNode(0, 0);
			newNode.setLeft(uncompressTreeFormatHelper(newBitInputStream, currentNode));
			newNode.setRight(uncompressTreeFormatHelper(newBitInputStream, currentNode));
			return newNode;
		} else if (currentBit == 1) {
			// Otherwise, if the current bit is 1, a leaf node has been reached, so read in 9
	    	// bits (which will represent the value of the leaf node) and create a new TreeNode
	    	// with this value
			currentBit = newBitInputStream.readBits(BITS_PER_WORD + 1);
			TreeNode newNode = new TreeNode(currentBit, 1);
			return newNode;
		}
		else {
			throw new IOException();
		}
	}
	
	
    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}