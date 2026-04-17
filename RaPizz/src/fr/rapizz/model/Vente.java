package fr.rapizz.model;

import java.sql.Date;
import java.sql.Time;

public class Vente {
	private int idVente;
	private Date dateVente;
	private Time heureCommande;
	private Time heureLivraison;
	private String taille;
	private boolean offerteFidelite;
	private boolean offerteRetard;

	private Vehicule vehicule;
	private Livreur livreur;
	private Pizza pizza;
	private Client client;

	public Vente() {
	}

	public int getIdVente() {
		return idVente;
	}

	public void setIdVente(int idVente) {
		this.idVente = idVente;
	}

	public Date getDateVente() {
		return dateVente;
	}

	public void setDateVente(Date dateVente) {
		this.dateVente = dateVente;
	}

	public Time getHeureCommande() {
		return heureCommande;
	}

	public void setHeureCommande(Time heureCommande) {
		this.heureCommande = heureCommande;
	}

	public String getTaille() {
		return taille;
	}

	public void setTaille(String taille) {
		this.taille = taille;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Pizza getPizza() {
		return pizza;
	}

	public void setPizza(Pizza pizza) {
		this.pizza = pizza;
	}

	public Livreur getLivreur() {
		return livreur;
	}

	public void setLivreur(Livreur livreur) {
		this.livreur = livreur;
	}
}