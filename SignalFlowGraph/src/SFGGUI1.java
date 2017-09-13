import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class SFGGUI1 {
	static ArrayList<String> connections = new ArrayList<>();
	public static Graph graph = new MultiGraph("Toturial");
	public static JTextArea textArea = new JTextArea();
	public static String styleSheet =
			"node {"+
				"shape: rounded-box;"+
				"stroke-mode:plain;"+
				"stroke-width:1px;"+
				"size-mode:fit;"+
				"fill-color:white;"+
				"text-alignment:center;"+
				"text-size:15px;"+
				"stroke-color:black;"+
				"padding:5px;"+
				"text-style:bold-italic;"+
				
			"}"+
			"edge { 	"+
				"shape: line;"+
				"size:2;	"+
			       " fill-color: blue;"+
				"text-size:15px;"+
				"text-style:bold;"+
				"arrow-size:15px,7px;"+
				"text-background-mode:rounded-box;"+
				"text-background-color:white;"+
				"text-padding:2px,2px;"+
				"}" +
			"node.marked {"+
			            "fill-color: blue;"+
			          " }"+

			"node:clicked {"+
				"fill-color: green;"+
			"}";
	public class Container extends JFrame {
		/**
		 * Launch the application.
		 */

		private JFrame window = new JFrame();

		private Container() {
			String line = JOptionPane.showInputDialog("Enter each connection:(ex: A B 5,B C 6,..etc)");
			String lines[] = line.split(",");
			for (String i : lines) {
				connections.add(i);
//				System.out.println(i);

			}
			Build_Gui();
		}

		void Build_Gui() {

			JMenuBar menuBar = new JMenuBar();

			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.getContentPane().setLayout(null);

			textArea.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 18));
			textArea.setEditable(false);

//			textArea.setBounds(10, 11, 1330, 658);
			JScrollPane scroll = new JScrollPane(textArea);
//			scroll.setPreferredSize(new Dimension(100,100)); 

			scroll.setBounds(10, 11, 1330, 658);
		    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		  
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			 scroll.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			window.getContentPane().add(scroll);

			menuBar = new JMenuBar();

			JMenu file = new JMenu("File");
			file.setFont(new Font("Segoe UI", Font.BOLD, 14));
			menuBar.add(file);
			JMenuItem clear = new JMenuItem("New");
			clear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			clear.setHorizontalAlignment(SwingConstants.LEFT);
			clear.setIcon(new ImageIcon("D:\\workshop\\oop\\newb.png"));
			clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					connections.clear();
					graph.clear();
					graph.display(false);
					textArea.setText(null);
//					window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));

					
//					new Container();
					String line = JOptionPane.showInputDialog("Enter each connection:(ex: A B 5,B C 6,..etc)");
					String lines[] = line.split(",");
					for (String i : lines) {
						connections.add(i);
					}
					BuildSignalFlowGraph();
					Overall();
				}
			});
			file.add(clear);
			JMenuItem exit = new JMenuItem("Exit");
			exit.setIcon(new ImageIcon("D:\\workshop\\oop\\exit.png"));
			exit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			file.add(exit);

			window.setJMenuBar(menuBar);
			window.setVisible(true);
			window.setSize(1366, 740);

			// Sim.setBorder(new TitledBorder(null, "Drawing Area",
			// TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}

		public void append2text(String text) {
			textArea.append(text);
		}
	}

	public static void main(String args[]) {
		// Read();
		// BuildSignalFlowGraph();
		// Overall();

		new SFGGUI1();

	}

	public SFGGUI1() {
		new Container();

		BuildSignalFlowGraph();
		Overall();
	}

	public static void Overall() {

		// TODO Auto-generated method stub
		String line = JOptionPane.showInputDialog("Enter the Required overall function IO Nodes: (eg Nout/Nin)");

		int V = graph.getNodeCount();
		ArrayList<Node> Nodes = new ArrayList<>();
		for (Node node : graph.getEachNode()) {
			Nodes.add(node);
		}

		SFG sfg = new SFG(V, Nodes);
		String IOnodes[] = line.split("/");
//		System.out.println(V + Nodes.toString());

		sfg.getAllForwardPaths(graph.getNode(IOnodes[1]), graph.getNode(IOnodes[0]));

		sfg.getLoops();

		double answer = sfg.overAllFun();
		
		textArea.append("|Forward Paths|\n");
		textArea.append("--------------------------------\n");
		int i = 1;
		for (String s : SFG.forwrdPath) {
			textArea.append("Forward Path" + Integer.toString(i) + ":" + s + "                             "
					+ "its Gain" + Integer.toString(i) + ":" + SFG.forwrdPathGain.get(i-1).toString()+"\n");
			i++;
		}
		textArea.append("\n");
		textArea.append("|Loops|\n");
		textArea.append("--------------------------------\n");
		i = 1;
		for (String s : SFG.loops) {
			textArea.append("Loop" + Integer.toString(i) + ":" + s + "                             "
					+ "its Gain" + Integer.toString(i) + ":" + SFG.loopsGain.get(i-1).toString()+"\n");
			i++;
		}
		textArea.append("\n");
		textArea.append("|Un touching Loops|\n");
		textArea.append("--------------------------------\n");
		sfg.untouchingloops.remove(sfg.untouchingloops.size()-1);
		for ( i = 0; i < sfg.untouchingloops.size(); i++) {
			textArea.append("level "+Integer.toString(i+1)+"\n");
			for (int j = 0; j < sfg.untouchingloops.get(i).size(); j++) {
				textArea.append(sfg.untouchingloops.get(i).get(j) + "                       ");
				textArea.append(sfg.untouchingloopsGains.get(i).get(j) + " ");
				textArea.append("\n");
			}
			textArea.append("\n");
		}
		
		textArea.append("\n");

		textArea.append("--------------------------------\n");
		textArea.append("|Delta M|\n");
		textArea.append("--------------------------------\n");

		for(int j = 0;j<sfg.deltas.size();j++){
			textArea.append("/_\\"+Integer.toString(j+1)+"          =           "+sfg.deltas.get(j));
			textArea.append("\n");
		}
		textArea.append("\n");

		textArea.append("--------------------------------\n");
		textArea.append("The overall Gain = "+answer);
		System.out.println();
	}

	public static void BuildSignalFlowGraph() {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

//		graph.addAttribute("ui.stylesheet", "url('file:stylesheet.css')");
		graph.addAttribute("ui.stylesheet", styleSheet);
		 
		graph.setAutoCreate(true);
		graph.setStrict(false);

		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.display();

		for (int i = 0; i < connections.size(); i++) {
			String s[] = connections.get(i).split(" ");
			graph.addEdge(s[0] + s[1], s[0], s[1], true);
			graph.getEdge(i).addAttribute("ui.label", s[2]);

		}
		for (Node node : graph) {
			node.addAttribute("ui.label", node.getId());
		}
		graph.getNode(0).setAttribute("xy", 0, 0);
		graph.getNode(graph.getNodeCount() - 1).setAttribute("xy", graph.getNodeCount() + 300, 0);
	}

	

}