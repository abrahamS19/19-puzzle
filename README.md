# 19-puzzle
An AI program to solve a variant of 8- and 15-puzzles. Uses A-* search algorithm with Manhattan distance as heuristic function

Works fine for test cases #1-4.
Needs a larger heap space for test case #5. Use 
```java -Xmx2g Nineteen``` 
to increase the heap space. 

This command currently allots 2 gb of heap. The upper limit depends on your operating system. x64 allows more space than 32-bit systems.   
Even with a larger heap the program may run for a while on test case #5 before solving the puzzle. 

The heuristic function used by the program is not optimal enough to solve the test case #6.
