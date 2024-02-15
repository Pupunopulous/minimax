# Minimax Algorithm Implementation

### Author: 
Rahi Krishna / rk4748

## Running the Program

The `.java` file can be executed on any operating system with JDK 8.0 or higher. Follow the steps below:

###Compile the Java file:

```shell
javac Minimax.java
java Minimax {arg1 arg2 arg3 ...}
```
#### The following arguments are accepted (if appended after "java Minimax"):
IMPORTANT: Order of these arguments does not matter

#### Required command line arguments:
1. filename - a text file "input.txt" must be provided as an argument to parse into
IMPORTANT: This file MUST be present in the same directory as the .java file

#### Optional command line arguments:
1. "max"/"min" - Specifies if the root of the minimax tree is a max or a min player, otherwise DEFAULTS to a min player
2. "-v" - Gives a verbose solution along with the answer
3. "-ab" - Gives the alpha-beta pruned solution of the given minimax tree
4. "-range x" - Specifies the range of leaf values inside the tree as [+x, -x]

#### Example commands:
```
java Minimax max input.txt
java Minimax min -v input.txt
java Minimax min -ab input.txt -range 5
java Minimax -v max input.txt -range 13 -ab
```

#### If you enter multiple arguments of the same type, the program plucks the LAST argument
```"java Minimax max input.txt -range 7 -range 20``` will produce a range of [-20, 20]
```java Minimax max input.txt input2.txt``` will produce a solution using "input2.txt"
