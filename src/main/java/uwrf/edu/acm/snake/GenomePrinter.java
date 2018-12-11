package uwrf.edu.acm.snake;

import java.util.Random;

import com.evo.NEAT.ConnectionGene;
import com.evo.NEAT.Genome;
import com.evo.NEAT.com.evo.NEAT.config.NEAT_Config;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

public class GenomePrinter {
	
	protected String STYLESHEET =
			"edge {" +
	        		"text-background-mode: rounded-box;" +
	        		"text-background-color: black;" +
	        		"text-alignment: center;" +
	        		"text-color: white;" +
	        		"arrow-size:5;" +
	        		"text-size: 10;" +
	        "}" +
	        		
			"edge.inactive {" +
				"fill-color:gray;" +
			"}" +
	        		
	        "node {" +
	        		"fill-color: black;" +
	        		"text-background-mode: rounded-box;" +
	        		"text-background-color: black;" +
	        		"text-alignment: center;" +
	        		"text-color: white;" +
	        		"size: 30;" +
	        		"text-size: 10;" +
	        "}" +
	        
	        "node.i {" +
	        	"fill-color: red;" +
	        "}" +
	        
	        "node.h {" +
	        	"fill-color: green;" +
	        "}" +
	        
			"node.o {" +
				"fill-color: blue;" +
			"}";
	
	public Graph showGenome(Genome genome, String title) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); // use advanced viewer
		
		Random random = new Random(1337L);
		
		Graph graph = new MultiGraph("Genome 1");
		graph.setAttribute("ui.title", title);
		graph.addAttribute("ui.stylesheet", STYLESHEET);

		for (int i = 0; i < NEAT_Config.INPUTS + 1; i++) {
			Node n = graph.addNode("N"+i);
			n.addAttribute("ui.label", "id="+i);
			
			n.addAttribute("layout.frozen");
			n.addAttribute("y", 0);
			n.addAttribute("x", 1f/(NEAT_Config.INPUTS+2) * (i+1));
			n.addAttribute("ui.class", "i");
		}
		
		for (int i = 0; i < NEAT_Config.OUTPUTS; i++) {
			Node n = graph.addNode("N" + (NEAT_Config.INPUTS + NEAT_Config.HIDDEN_NODES + i));
			n.addAttribute("ui.label", "id="+(NEAT_Config.INPUTS + NEAT_Config.HIDDEN_NODES + i));
			
			n.addAttribute("layout.frozen");
			n.addAttribute("y", 1);
			n.addAttribute("x", 1f/(NEAT_Config.OUTPUTS+1) * (i+1));
			n.addAttribute("ui.class", "o");
		}

		for (ConnectionGene connectionGene : genome.getConnectionGeneList()) {

			if (graph.getNode("N"+connectionGene.getInto()) == null){
				Node n = graph.addNode("N"+connectionGene.getInto());
				n.addAttribute("ui.label", "id="+connectionGene.getInto());
				n.addAttribute("layout.frozen");
				n.addAttribute("y", random.nextFloat()*0.5f+0.25f);
				n.addAttribute("x", random.nextFloat());
				n.addAttribute("ui.class", "h");
			}

			if (graph.getNode("N"+connectionGene.getOut()) == null){
				Node n = graph.addNode("N"+connectionGene.getOut());
				n.addAttribute("ui.label", "id="+connectionGene.getOut());
				n.addAttribute("layout.frozen");
				n.addAttribute("y", random.nextFloat()*0.5f+0.25f);
				n.addAttribute("x", random.nextFloat());
				n.addAttribute("ui.class", "h");
			}

		}

		
		
		for (ConnectionGene connection : genome.getConnectionGeneList()) {
			Edge e = graph.addEdge("C"+connection.getInnovation(), "N"+connection.getInto(), "N"+connection.getOut(), true);
			e.addAttribute("ui.label", "w="+connection.getWeight()+"\n"+" in="+connection.getInnovation());
			
			if (!connection.isEnabled()) {
				e.addAttribute("ui.class", "inactive");
			}
		}
		
		graph.display();
		return graph;
	}
}
