package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	Graph<Player,DefaultWeightedEdge> grafo;
	PremierLeagueDAO dao;
	Map<Integer, Player> playersMap;
	
	public Model() {
		super();
		this.dao = new PremierLeagueDAO();
	}
	
	public List<Match> getAllMatches(){
		return dao.listAllMatches();
	}
	
	public void creaGrafo(Match m) {
		this.grafo = new SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.playersMap = new HashMap<>();
		
		// aggiungo i vertici
		Graphs.addAllVertices(this.grafo, dao.getAllPlayersByMatch(m, playersMap));
		
		// aggiungo gli archi
		for(Arco ai : dao.getAllEdges(m, playersMap)) {
			if(ai.getPeso() > 0) {
				Graphs.addEdgeWithVertices(this.grafo, ai.getP1(), ai.getP2(), ai.getPeso());
			}else {
				Graphs.addEdgeWithVertices(this.grafo, ai.getP2(), ai.getP1(), (-1)*ai.getPeso());
			}
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String getBestPlayer() {
		Player best = null;
		Double max = (double)Integer.MIN_VALUE;
		
		for(Player pi : this.grafo.vertexSet()) {
			double pesoU = 0.0;
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(pi)) {
	    	   pesoU += this.grafo.getEdgeWeight(e);
	    	}
			double pesoE = 0.0;
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(pi)){
				pesoE -= this.grafo.getEdgeWeight(e);	
			}
			double delta = pesoU - pesoE;
			if( delta > max) {
				max = delta;
				best = pi;
			}
	    }
		return best.playerID+" "+best.getName()+" ,"+"delta massimo= "+max;
	}
	
	
	
	
}
