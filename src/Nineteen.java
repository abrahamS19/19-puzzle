
// ADAPTED FROM DR. SIMON'S CODE

import java.util.Stack;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Nineteen {

    //final static int SIZE = 8;
    //final static int LENGTH = 3; // Sqrt(SIZE+1)
    //final static int SIZE = 15;
    //final static int LENGTH = 4; // Sqrt(SIZE+1)
    
    final static int SIZE = 19;
    final static int LENGTH = 6;

    int tiles[];
    int blankPos;

    public Nineteen(int[] x) {
		tiles = Arrays.copyOf(x, x.length);
		for(int i=0; i<=SIZE; i++)
		    if(tiles[i] == 0) {
				blankPos = i;
				return;
		    }
    }

    public Nineteen(int tiles[], int blankPos) {
		this.tiles = Arrays.copyOf(tiles, tiles.length);
		this.blankPos = blankPos;
    }

    public String toString() {
		String s = "";
		int n=-1;
		for(int i=0; i<LENGTH; i++) {
			for(int j=0; j<LENGTH; j++) {
				if(i==2 || i==3)
					s += String.format(" %2d", tiles[++n]);
				else
					s+= (j==2 || j==3) ? String.format(" %2d", tiles[++n]) : "   ";
	        }
		    s += "\n";
		}
		return s;
    }

    public boolean equals(Object o) {
		Nineteen r = (Nineteen)o;
		return blankPos == r.blankPos && Arrays.equals(tiles, r.tiles);
    }

    public int hashCode() { return Arrays.hashCode(tiles); }

    interface MoveAction { boolean valid(); void move(); }

    private MoveAction[] moveActions = new MoveAction[] {
        new MoveAction() { // up
        	
		    public boolean valid() { 
		    	return (blankPos != 0 && blankPos != 1 && blankPos != 4 && blankPos != 5 && blankPos != 8 && blankPos != 9) ; 
		    }
		    public void move() { 
		    	int n = 0;
		    	
		    	if(blankPos > 9 && blankPos < 16) n = LENGTH;	
		    	else if(blankPos == 6 || blankPos == 7 || blankPos == 16 || blankPos == 17) n = 4;
		    	else n = 2;
		    	
		    	tiles[blankPos] = tiles[blankPos-n]; 
		    	blankPos -= n; tiles[blankPos] = 0; 		
		    }
        },
        new MoveAction() { // down
		    public boolean valid() { 
		    	return (blankPos != 10 && blankPos != 11 && blankPos != 14 && blankPos != 15 && blankPos != 18 && blankPos != 19); 
		    }
		    public void move() { 
		    	int n = 0;
		    	
		    	if(blankPos > 3 && blankPos < 10) n = LENGTH;	
		    	else if(blankPos == 2 || blankPos == 3 || blankPos == 12 || blankPos == 13) n = 4;
		    	else n = 2;
		    	
		    	tiles[blankPos] = tiles[blankPos+n]; 
		    	blankPos += n; tiles[blankPos] = 0;
		    }
        },
        new MoveAction() { // left
		    public boolean valid() { 
		    	return (blankPos != 0 && blankPos != 2 && blankPos != 4 && blankPos != 10 && blankPos != 16 && blankPos != 18); 
		    }
		    public void move() { tiles[blankPos] = tiles[blankPos-1]; blankPos -= 1; tiles[blankPos] = 0;}
        },
        new MoveAction() { // right
		    public boolean valid() { 
		    	return (blankPos != 1 && blankPos != 3 && blankPos != 9 && blankPos != 15 && blankPos != 17 && blankPos != 19); 
		    }
		    public void move() { tiles[blankPos] = tiles[blankPos+1]; blankPos += 1; tiles[blankPos] = 0;}
        }
    };

    private static int[] opp = {1, 0, 3, 2};

    static class Node implements Comparable<Node>, Denumerable {
		public Nineteen state;
		public Node parent;
		public int g, h;
		public boolean inFrontier;
		public int x;
		
		Node(Nineteen state, Node parent, int g, int h) {
		    this.state = state;
		    this.parent = parent;
		    this.g = g;
		    this.h = h;
		    inFrontier = true;
		    x = 0;
		}
		public int compareTo(Node a) {
		    return g + h - a.g - a.h;
		}
		public int getNumber() { return x; }
		public void setNumber(int x) { this.x = x; }
		public String toString() { return state + ""; }
    }
    
    static class Position {		// to hold elements' coordinates on 6x6 array
    	public int x = 0, y = 0;
    	Position(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException {
		int[] x = new int[SIZE+1];
		int[] y = new int[SIZE+1];
		for(int i=0; i<SIZE+1; i++)
		    x[i] = i;
		Nineteen goal = new Nineteen(x);
		
		File file = new File("test6.txt");
		Scanner sc = new Scanner(file);
		
		int i = 0;
		while(sc.hasNextInt()) {
			int n = sc.nextInt();
			y[i] = n;
			i++;
		}
		Nineteen r = new Nineteen(y);
		
		//ids(r, goal);
		astar(r, goal);
    }

//    public static int ids(Nineteen r, Nineteen goal) {
//		for(int limit=0;;limit++) {
//		    System.out.print(limit + " ");
//		    int result = bdfs(r, goal, limit);
//		    if(result != 1) {
//				System.out.println();
//				return result;
//		    }
//		}
//    }
//
//    public static int bdfs(Nineteen r, Nineteen goal, int limit) {
//	// returns 0: failure, 1: cutoff, 2: success
//		if(r.equals(goal))
//		    return 2;
//		else if(limit == 0)
//		    return 1;
//		else {
//		    boolean cutoff = false;
//		    for(int i=0; i<4; i++) {
//				if(r.moveActions[i].valid()) {
//				    r.moveActions[i].move();
//				    switch(bdfs(r, goal, limit-1)) {
//					    case 1: cutoff = true; break;
//					    case 2: return 2;
//					    default:
//				    }
//				    r.moveActions[opp[i]].move();
//				}
//		    }
//		    return (cutoff ? 1 : 0);
//		}
//    }

    public static int h(Nineteen r, Nineteen goal) {
    	HashMap<Integer, Position> currentPos = new HashMap<>();
    	HashMap<Integer, Position> goalPos = new HashMap<>();
		int total = 0, n = -1;
		
		for(int i=0;i<LENGTH; i++) {
			for(int j=0; j<LENGTH; j++) {
				if(i==2 || i==3) {
					goalPos.put((Integer)goal.tiles[++n], new Position(i,j));
					currentPos.put((Integer)r.tiles[n], new Position(i,j));
				}
		        else {
		             if (j==2 || j==3) {
		            	 goalPos.put((Integer)goal.tiles[++n], new Position(i,j));
		            	 currentPos.put((Integer)r.tiles[n], new Position(i,j));
		             }
		        }
				
			}
		}
			
		for(int i=1; i<=SIZE; i++)
		    total += Math.abs(currentPos.get(i).x - goalPos.get(i).x) + Math.abs(currentPos.get(i).y - goalPos.get(i).y);
		return total;
    }

    public static void printAnswer(Node x) {
		Stack<Node> stack = new Stack<>();
		int numMoves = 0;
		for(Node y = x; y != null; y = y.parent) {
		    stack.push(y);
		    numMoves++;
		}
		while(!stack.isEmpty())
		    System.out.println(stack.pop());
		System.out.println((numMoves-1) + " moves.");
    }
    
    public static int astar(Nineteen start, Nineteen goal) {
		// returns 0: failure, 2: success
		System.out.println("  f    |frontier|  |explored|");
		int maxF = 0;
		Node z = new Node(start, null, 0, h(start, goal));
		IndexMinPQ<Node> frontier = new IndexMinPQ<>();
		frontier.add(z);
		HashMap<Nineteen,Node> explored = new HashMap<>();
		explored.put(start, z);
		
		while(true) {
		    if(frontier.isEmpty())
		    	return 0;
		    Node x = frontier.remove();
		    x.inFrontier = false;
		    if(x.g + x.h > maxF) { maxF = x.g + x.h; System.out.printf("%3d %10d %10d\n", maxF, frontier.size(), explored.size()); }
		    if(x.state.equals(goal)) {
		    	System.out.println();
				printAnswer(x);
				return 2;
		    }
		    for(int i=0; i<4; i++) {
				if(x.state.moveActions[i].valid()) {
				    x.state.moveActions[i].move();
				    Node n = explored.get(x.state);
				    if(n == null) {
						Nineteen s = new Nineteen(x.state.tiles, x.state.blankPos);
						n = new Node(s, x, x.g+1, h(x.state,goal));
						explored.put(s, n);
						frontier.add(n);
				    }
				    else if(n.inFrontier) {
						if(x.g+1 < n.g) {
						    n.parent = x;
						    n.g = x.g + 1;
						    frontier.update(n);
						}
				    }
				    x.state.moveActions[opp[i]].move();
				}
		    }
		    
		}
    
    }
}
