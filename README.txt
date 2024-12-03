1. What kinds of file lead to lots of compressions?

The text files lead to a lot of compression, since around 40% of the original bits are compressed.

2. What kind of files had little or no compression?

Meanwhile, the image files had to little-to-no compression, with some of the resulting
compressed files containing a higher number of bytes than the original files.

Clearly, there were large compression ratios for text-heavy files like those in the Calgary Corpus 
and BooksAndHTML directories, which suggests that Huffman encoding is useful when applied to files
that have large frequencies for certain character and small frequencies for others.
However, the TIFF image files from Waterloo directory compressed less effectively, suggesting that
Huffman encoding is less useful when applied to files containing small amounts varying data/
characters that have about the same frequencies.

3. What happens when you try and compress a huffman code file?

When trying to compress huffman code files in the BooksAndHTML folder, the size of the 
resulting files sometimes increased. For example, syllabus.htm.hf grew from 21342 bytes
to 21841 bytes. Another example is how the numbr of bytes in melville.txt decreased from
82140 bytes to 47364 bytes in .hf when initially compressed, but recompression
to .hf.hf increased the size from 47364 to 47551 bytes. This occurs because .hf files are
already compressed, leaving little area for our program to compress further.