package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	
	private Map<Fermata, Fermata> predecessore;
	
	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
//		for(Fermata f : fermate) {
//			this.grafo.addVertex(f);
//		}
		
		Graphs.addAllVertices(this.grafo, fermate);
		
		// Aggiungiamo gli archi
		
//		for(Fermata f1 : this.grafo.vertexSet()) {
//			for(Fermata f2 : this.grafo.vertexSet()) {
//				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
//					this.grafo.addEdge(f1, f2);
//				}
//			}
//		}
		
		List<Connessione> connessioni = dao.getAllConnessioni(fermate);
		for(Connessione c : connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
		//System.out.println(this.grafo);
		
//		Fermata f;
//		/*
//		Set<DefaultEdge> archi = this.grafo.edgesOf(f);
//		for(DefaultEdge e : archi) {
//			/*
//			Fermata f1 = this.grafo.getEdgeSource(e);
//			// oppure (se il grafo è non orientato avrò solo uno dei due collegamenti: A->B o B->A)
//			Fermata f2 = this.grafo.getEdgeTarget(e);
//			if(f1.equals(f)) {
//				// f2 è quello che mi serve
//			}
//			else {
//				// f2 è quello che mi serve
//			}
//			
//			f1 = Graphs.getOppositeVertex(this.grafo, e, f);
//		}*/
//		
//		// in questo modo riesco ad ottenere la lista dei vertici adiacenti ad f
//		List<Fermata> fermateAdiacenti = Graphs.successorListOf(this.grafo, f);
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata partenza) {
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		
		this.predecessore = new HashMap<>();
		this.predecessore.put(partenza, null);
		
		//il listener ha dei metodi che vengono eseguiti una volta che viene memorizzato un oggetto dentro bfv
		//essi sono però da definire nella creazione dell'oggetto "TraversalListener" (inline o in un altro file) 
		//(essendo un'interfaccia devo implementare per forza i metodi!)
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco = e.getEdge();
				Fermata a = grafo.getEdgeSource(arco);
				Fermata b = grafo.getEdgeTarget(arco);
				// ho scoperto 'a' arrivando da 'b' (se 'b' lo conoscevo)
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					predecessore.put(a, b);
					System.out.println(a + " scoperto da " + b);
				}
				else if(predecessore.containsKey(a) && !predecessore.containsKey(b)){
					// di sicuro conoscevo 'a' e quindi ho scoperto 'b'
					predecessore.put(b, a);
					System.out.println(b + " scoperto da " + a);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
//				System.out.println(e.getVertex());
//				Fermata nuova = e.getVertex();
//				Fermata precedente = vertice adiacente a 'nuova' che sia già raggiunto
//						(cioè è già presente nelle key della mappa); 
//				predecessore.put(nuova, precedente);
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			}
		});
		
//		DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo, partenza);
		
		List<Fermata> result = new ArrayList<>();
		
		while(bfv.hasNext()) {
			Fermata f = bfv.next();
			result.add(f);
		}
		
		return result;
	}
	
	public Fermata trovaFermata(String nome) {
		for(Fermata f : this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}
	
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {
		fermateRaggiungibili(partenza);
		
		List<Fermata> result = new LinkedList<>(); // Al posto di una ArrayList perché è meno dispendioso aggiungere in testa con la Linked
		result.add(arrivo);
		Fermata f = arrivo;
		while(predecessore.get(f)!=null) {
			f = predecessore.get(f);
			result.add(0, f); // aggiungo in testa mettendo l'indice = 0, così ho l'ordine partenza -> arrivo
		}
		
		return result;
	}
}
