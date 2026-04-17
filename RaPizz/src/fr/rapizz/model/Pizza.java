package fr.rapizz.model;

import java.util.List;

public class Pizza {
	private int idPizza;
	private String nomPizza;
	private double prixBase;
	private List<String> ingredients;

	public Pizza() {
	}

	public Pizza(int id, String nom, double prixBase) {
		this.idPizza = id;
		this.nomPizza = nom;
		this.prixBase = prixBase;
	}

	public int getIdPizza() {
		return idPizza;
	}

	public void setIdPizza(int idPizza) {
		this.idPizza = idPizza;
	}

	public String getNomPizza() {
		return nomPizza;
	}

	public void setNomPizza(String nomPizza) {
		this.nomPizza = nomPizza;
	}

	public double getPrixBase() {
		return prixBase;
	}

	public void setPrixBase(double prixBase) {
		this.prixBase = prixBase;
	}

	public List<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}
}
