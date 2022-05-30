package it.polito.tdp.itunes.model;

public class TestModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model m= new Model();
		m.creaGrafo(new Genre(2,"Jazz"));
		System.out.println(m.getPesoMassimo());
		
	}

}
