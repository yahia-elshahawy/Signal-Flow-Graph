
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.graphstream.graph.Edge;
//import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
//import org.graphstream.graph.implementations.MultiGraph;

public class SFG {
	public static int V;
	public static ArrayList<Node> Nodes = new ArrayList<>();
	public static ArrayList<String> loops = new ArrayList<>();
	public static ArrayList<String> forwrdPath = new ArrayList<>();
	public static ArrayList<Double> deltas = new ArrayList<>();
	public static ArrayList<Double> loopsGain = new ArrayList<Double>();
	public static ArrayList<Double> forwrdPathGain = new ArrayList<Double>();
	public static ArrayList<ArrayList<String>> untouchingloops = new ArrayList<>();
	public static ArrayList<ArrayList<String>> untouchingloops1 = new ArrayList<>();
	public static ArrayList<ArrayList<Double>> untouchingloops1Gain = new ArrayList<>();
	public static ArrayList<ArrayList<Double>> untouchingloopsGains = new ArrayList<>();

	public SFG(int V, ArrayList<Node> Nodes) {
		SFG.V = V;
		SFG.Nodes = Nodes;

	}

	public void getAllForwardPaths(Node s, Node d) {
		// Mark all the vertices as not visited
		Hashtable<Node, String> visited = new Hashtable<>();
		// Create an array to store paths
		Node path[] = new Node[V];
		int path_index = 0; // Initialize path[] as empty

		// Initialize all vertices as not visited
		for (int i = 0; i < Nodes.size(); i++) {
			visited.put(Nodes.get(i), "0");
		}
		// Call the recursive helper function to print all paths
		printAllPathsUtil(s, d, visited, path, path_index);
	}

	// A recursive function to print all paths from 'u' to 'd'.
	// visited[] keeps track of vertices in current path.
	// path[] stores actual vertices and path_index is current
	// index in path[]
	public void printAllPathsUtil(Node u, Node d, Hashtable<Node, String> visited, Node path[], int path_index) {
		// Mark the current node and store it in path[]
		visited.remove(u);
		visited.put(u, "1");
		path[path_index] = u;
		path_index++;
		// If current vertex is same as destination, then print
		// current path[]
		if (u.equals(d)) {
			ArrayList<Node> fPath = new ArrayList<>();
			String Path = "";
			for (int i = 0; i < path_index; i++) {
				fPath.add(path[i]);
				Path = Path + path[i].getId();
			}
			forwrdPath.add(Path);
			forwrdPathGain.add(gainForwardPath(fPath));
		} else // If current vertex is not destination
		{
			// Recur for all the vertices adjacent to current vertex
			Iterator<? extends Node> k = u.getLeavingEdgeIterator();
			while (k.hasNext()) {
				Edge next = (Edge) k.next();
				if (visited.get(next.getTargetNode()).equals("0")) {
					printAllPathsUtil(next.getTargetNode(), d, visited, path, path_index);
				}
			}
		}
		// Remove current vertex from path[] and mark it as unvisited
		path_index--;
		visited.remove(u);
		visited.put(u, "0");
	}



	public void getLoops() {
		Hashtable<Node, String> visited = new Hashtable<>();
		for (int i = 0; i < Nodes.size(); i++) {
			visited.put(Nodes.get(i), "0");
		}
		Node path[] = new Node[V];
		int path_index = 0;
		for (int i = 0; i < Nodes.size(); i++) {
			dfs(Nodes.get(i), adj(Nodes.get(i)), visited, Nodes.get(i), path, path_index);
			for (int j = 0; j < Nodes.size(); j++) {
				visited.put(Nodes.get(j), "0");
			}
			path = new Node[1000];
			path_index = 0;
		}
	}

	public ArrayList<Node> adj(Node node) {
		ArrayList<Node> nodes = new ArrayList<>();
		Iterator<? extends Node> k = node.getLeavingEdgeIterator();
		while (k.hasNext()) {
			Edge next = (Edge) k.next();
			nodes.add(next.getTargetNode());
		}
		return nodes;
	}

	public void dfs(Node node, ArrayList<Node> adj, Hashtable<Node, String> visited, Node start, Node path[],
			int path_index) {
		for (int i = 0; i < path_index; i++) {
			if (path[i].equals(node) && !node.equals(start))
				return;
		}
		path[path_index] = node;
		path_index++;
		if (visited.get(node).equals("1")) {
			if (node.equals(start)) {
				String loop = "";
				ArrayList<Node> loop1 = new ArrayList<>();
				for (int i = 0; i < path_index; i++) {
					if (i < path_index - 1) {
						loop = loop + path[i].getId();
						loop1.add(path[i]);
					}
				}
				boolean exist = false;
				char[] chars = loop.toCharArray();
				Arrays.sort(chars);
				loop = new String(chars);
				for (int i = 0; i < loops.size(); i++) {
					if (loops.get(i).equals(loop))
						exist = true;
				}
				if (!exist) {
					loops.add(loop);
					loopsGain.add(gainLoop(loop1));
				}
				exist = false;
				return;
			}
		}
		visited.replace(node, "1");
		for (Node child : adj)
			dfs(child, adj(child), visited, start, path, path_index);
		visited.replace(node, "0");
		path_index--;
	}

	public double gainForwardPath(ArrayList<Node> path) {
		double gain = 1;
		if (path.size() < 3) {
			Edge edge = path.get(0).getEdgeToward(path.get(1));
			gain *= Double.parseDouble(edge.getAttribute("ui.label"));
		} else {
			for (int i = 0; i < path.size(); i++) {
				if (i < path.size() - 1) {
					Edge edge = path.get(i).getEdgeToward(path.get(i + 1));
					gain *= Double.parseDouble(edge.getAttribute("ui.label"));
				}
			}
		}
		return gain;
	}

	public double gainLoop(ArrayList<Node> path) {
		double gain = 1;
		if (path.size() == 1) {
			Edge edge = path.get(0).getEdgeToward(path.get(0));
			gain *= Double.parseDouble(edge.getAttribute("ui.label"));
		} else if (path.size() == 2) {
			Edge edge = path.get(0).getEdgeToward(path.get(1));
			gain *= Double.parseDouble(edge.getAttribute("ui.label"));
			edge = path.get(1).getEdgeToward(path.get(0));
			gain *= Double.parseDouble(edge.getAttribute("ui.label"));
		} else {
			for (int i = 0; i < path.size(); i++) {
				if (i < path.size() - 1) {
					Edge edge = path.get(i).getEdgeToward(path.get(i + 1));
					gain *= Double.parseDouble(edge.getAttribute("ui.label"));
				}
			}
			Edge edge = path.get(path.size() - 1).getEdgeToward(path.get(0));
			gain *= Integer.parseInt(edge.getAttribute("ui.label"));
		}
		return gain;
	}

	public double denominator() {
		double answer = 1;
		untouchingloops.add(loops);
		untouchingloopsGains.add(loopsGain);
		int factor = -1;
		getUntouchedloop(loops, loops, loopsGain, loopsGain);
		for (int i = 0; i < untouchingloopsGains.size(); i++) {
			double gain = 0;
			for (int j = 0; j < untouchingloopsGains.get(i).size(); j++) {
				gain += untouchingloopsGains.get(i).get(j);
			}
			gain *= factor;
			answer += gain;
			factor *= -1;
		}
		return answer;
	}

	

	public void getUntouchedloopForwardPath(ArrayList<String> s1, ArrayList<String> s2, ArrayList<Double> s1Gain,
			ArrayList<Double> s2Gain) {
		if (s2.size() == 0)
			return;
		ArrayList<String> rank = new ArrayList<>();
		ArrayList<Double> rankGain = new ArrayList<>();
		if (s1.equals(s2)) {
			for (int i = 0; i < s1.size(); i++) {
				for (int j = i + 1; j < s2.size(); j++) {
					Set<Character> ss1 = toSet(s1.get(i).trim());
					ss1.retainAll(toSet(s2.get(j).trim()));
					if (ss1.isEmpty()) {
						String H = s1.get(i).trim() + s2.get(j).trim();
						char[] chars = H.toCharArray();
						Arrays.sort(chars);
						H = new String(chars);
						double gain = s1Gain.get(i) * s2Gain.get(j);
						rank.add(H);
						rankGain.add(gain);
					}
				}
			}
		} else {
			for (int i = 0; i < s1.size(); i++) {
				for (int j = 0; j < s2.size(); j++) {
					Set<Character> ss1 = toSet(s1.get(i).trim());
					ss1.retainAll(toSet(s2.get(j).trim()));
					if (ss1.isEmpty()) {
						String H = s1.get(i).trim() + s2.get(j).trim();
						char[] chars = H.toCharArray();
						Arrays.sort(chars);
						H = new String(chars);
						double gain = s1Gain.get(i) * s2Gain.get(j);
						rank.add(H);
						rankGain.add(gain);
					}
				}
			}
		}
		untouchingloops1.add(rank);
		untouchingloops1Gain.add(rankGain);
		getUntouchedloopForwardPath(s1, untouchingloops1.get(untouchingloops1.size() - 1), s1Gain,
				untouchingloops1Gain.get(untouchingloops1Gain.size() - 1));
	}

	public void getUntouchedloop(ArrayList<String> s1, ArrayList<String> s2, ArrayList<Double> s1Gain,
			ArrayList<Double> s2Gain) {
		if (s2.size() == 0)
			return;
		ArrayList<String> rank = new ArrayList<>();
		ArrayList<Double> rankGain = new ArrayList<>();
		if (s1.equals(s2)) {
			for (int i = 0; i < s1.size(); i++) {
				for (int j = i + 1; j < s2.size(); j++) {
					Set<Character> ss1 = toSet(s1.get(i).trim());
					ss1.retainAll(toSet(s2.get(j).trim()));
					if (ss1.isEmpty()) {
						String H = s1.get(i).trim() + s2.get(j).trim();
						char[] chars = H.toCharArray();
						Arrays.sort(chars);
						H = new String(chars);
						double gain = s1Gain.get(i) * s2Gain.get(j);
						rank.add(H);
						rankGain.add(gain);
					}
				}
			}
		} else {
			for (int i = 0; i < s1.size(); i++) {
				for (int j = 0; j < s2.size(); j++) {
					Set<Character> ss1 = toSet(s1.get(i).trim());
					ss1.retainAll(toSet(s2.get(j).trim()));
					if (ss1.isEmpty()) {
						String H = s1.get(i).trim() + s2.get(j).trim();
						char[] chars = H.toCharArray();
						Arrays.sort(chars);
						H = new String(chars);
						double gain = s1Gain.get(i) * s2Gain.get(j);
						rank.add(H);
						rankGain.add(gain);
					}
				}
			}
		}
		untouchingloops.add(rank);
		untouchingloopsGains.add(rankGain);
		getUntouchedloop(s1, untouchingloops.get(untouchingloops.size() - 1), s1Gain,
				untouchingloopsGains.get(untouchingloopsGains.size() - 1));
	}

	public double numerator() {
		double answer = 0;
		for (int i = 0; i < forwrdPath.size(); i++) {
			ArrayList<String> individualLoop = new ArrayList<>();
			ArrayList<Double> individualLoopGains = new ArrayList<>();
			for (int j = 0; j < loops.size(); j++) {
				Set<Character> ss1 = toSet(forwrdPath.get(i).trim());
				ss1.retainAll(toSet(loops.get(j).trim()));
				if (ss1.size() == 0) {
					individualLoop.add(loops.get(j));
					individualLoopGains.add(loopsGain.get(j));
				}
			}
			untouchingloops1.add(individualLoop);
			untouchingloops1Gain.add(individualLoopGains);
			getUntouchedloopForwardPath(individualLoop, individualLoop, individualLoopGains, individualLoopGains);
			int factor = -1;
			double delta = 0;
			untouchingloops1.remove(untouchingloops1.size() - 1);
			untouchingloops1Gain.remove(untouchingloops1Gain.size() - 1);
			for (int k = 0; k < untouchingloops1Gain.size(); k++) {
				double gain = 0;
				for (int j = 0; j < untouchingloops1Gain.get(k).size(); j++) {
					gain += untouchingloops1Gain.get(k).get(j);
				}
				gain *= factor;
				delta += gain;
				factor *= -1;
			}
			delta = 1 + delta;
			deltas.add(delta);
			answer += (delta * forwrdPathGain.get(i));
			untouchingloops1.clear();
			untouchingloops1Gain.clear();
		}
		return answer;
	}

	public double overAllFun() {
		return numerator() / denominator();
	}

	public Set<Character> toSet(String s) {
		Set<Character> ss = new HashSet<Character>(s.length());
		for (char c : s.toCharArray())
			ss.add(Character.valueOf(c));
		return ss;
	}
}
