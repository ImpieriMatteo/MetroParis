package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		Model model = new Model();
		
		model.creaGrafo();
		
		Fermata p = model.trovaFermata("La Fourche");
		if(p==null) {
			System.out.println("Fermata non trovata");
		}
		else {
			List<Fermata> raggiungibili = model.fermateRaggiungibili(p);
			System.out.println(raggiungibili);
		}
	}
}
