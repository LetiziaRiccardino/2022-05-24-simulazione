package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private ItunesDAO dao;
	private Graph<Track, DefaultWeightedEdge> grafo;
	private Map<Integer, Track> idMap;
	
	private List<Track> listaMigliore;
	
	public Model() {
		dao=new ItunesDAO();
		idMap= new HashMap<>();
		
		this.dao.getAllTracks(idMap);//aggiungo gli elementi alla mappa
	}
	
	public List<Genre> getGeneri(){
		return dao.getAllGenres();
	}
	
	public void creaGrafo(Genre genere) {
		//creo il grafo
		this.grafo= new SimpleWeightedGraph<Track, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(genere, idMap));
		
		System.out.println("Grafo creato \n Vertici: "+this.grafo.vertexSet().size() );
		
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(genere, idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getT1(), a.getT2(), a.getPeso());
		}
		System.out.println("Archi: "+ this.grafo.edgeSet().size());
		
	}
	
	public List<Adiacenza> getPesoMassimo(){//posso avere più archi con valore massimo
		List<Adiacenza> result= new ArrayList<Adiacenza>();
		int max=0;
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			int peso=(int)this.grafo.getEdgeWeight(e);
			if(peso> max) {
				result.clear();
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeSource(e), peso));
				max=peso;
			}else if (peso==max){
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeSource(e), peso));
			}
		}
		return result;
	}
	
	public int nVertici(){
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

	public boolean isGrafoCreato() {
		if(this.grafo==null)
			return false;
		return true;
	}
	
	public List<Track> getVertici(){
		return new ArrayList<>(this.grafo.vertexSet());
	}
	
	public List<Track> cercaLista(Track c, int memoria){//riceve la canzone e la memoria totale
		//recupero la componente connessa di c
		Set<Track> componenteConnessa= new HashSet<Track>();
		ConnectivityInspector<Track, DefaultWeightedEdge> ci= new ConnectivityInspector<>(this.grafo);
		componenteConnessa.addAll(ci.connectedSetOf(c));
		
		List<Track> canzoniValide= new ArrayList<Track>();
		canzoniValide.add(c);
		componenteConnessa.remove(c);
		canzoniValide.addAll(componenteConnessa);
		
		listaMigliore= new ArrayList<>();
		List<Track> parziale= new ArrayList<Track>();
		parziale.add(c);
		
		cerca(parziale, canzoniValide, memoria, 1);
		
		return listaMigliore;
	}
	
	
	private void cerca(List<Track> parziale, List<Track> canzoniValide, int m, int L) {
		
		//condizioni di terminazione
		if(sommaMemoria(parziale)>m)
			return;
		  //parziale è valida
		if(parziale.size()>listaMigliore.size()) {
			listaMigliore=new ArrayList<>(parziale);
		}
		  //al posto del ciclo for che mi dava una condizione di terminazione devo aggiungere una condizione di terminazione
		if(L==canzoniValide.size())
			return;
		
		
		parziale.add(canzoniValide.get(L));
		cerca(parziale, canzoniValide, m, L+1);
		parziale.remove(canzoniValide.get(L));
		cerca(parziale, canzoniValide, m, L+1);
		
	}

	/*public List<Track> cercaLista(Track c, int memoria){//riceve la canzone e la memoria totale
		//recupero la componente connessa di c
		List<Track> canzoniValide= new ArrayList<Track>();
		ConnectivityInspector<Track, DefaultWeightedEdge> ci= new ConnectivityInspector<>(this.grafo);
		
		canzoniValide.addAll(ci.connectedSetOf(c));
		
		listaMigliore= new ArrayList<>();
		List<Track> parziale= new ArrayList<Track>();
		parziale.add(c);
		
		cerca(parziale, canzoniValide, memoria);
		
		return listaMigliore;
		
	}

	private void cerca(List<Track> parziale, List<Track> canzoniValide, int memoria) {
		
		//controllo soluzione migliore
		if(parziale.size()>listaMigliore.size()) {
			listaMigliore=new ArrayList<>(parziale);
		}
		
		for(Track t: canzoniValide) {
			if(!parziale.contains(t) //controlla che ci sia HashCode e equals nella classe Track
					&& (sommaMemoria(parziale)+ t.getBytes())<=memoria ) {   //condizione di terminazione
				parziale.add(t);
				cerca(parziale,canzoniValide, memoria);
				parziale.remove(parziale.size()-1);//parziale.remove(t);
			}
		}
	}*/
	
	private int sommaMemoria(List<Track> lista) {
		int somma=0;
		for(Track t: lista) {
			somma+= t.getBytes();
		}
		return somma;
	}
}
