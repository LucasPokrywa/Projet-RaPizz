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

	
	public void effectuerCommande(Client client, Pizza pizza, String taille) {
        this.client = client;
        this.pizza = pizza;
        this.taille = taille;
        this.dateVente = new Date(System.currentTimeMillis());
        this.heureCommande = new Time(System.currentTimeMillis());
        
        if(this.client.getSoldeCompte() - this.calculerPrixVente() < 0) {
        	return;
        }
    }
	
	public double calculerPrixVente() {
        if (offerteFidelite || offerteRetard) {
            return 0.0; 
        }

        double prixBase = pizza.getPrixBase();
        switch (taille.toLowerCase()) {
            case "naine":
                return prixBase * (2.0 / 3.0);  
            case "ogresse":
                return prixBase * (4.0 / 3.0);
            case "humaine":
            default:
                return prixBase;
        }
    }
	
	public void enregistrerLivraison() {
        
        this.heureLivraison = new Time(System.currentTimeMillis());
        
        long difference = heureLivraison.getTime() - heureCommande.getTime();
        long minutes = (difference / 1000) / 60;

        if (minutes > 30) {
            this.offerteRetard = true; 
        }
    }
	
	public int getIdVente() { return idVente; }
    public void setIdVente(int idVente) { this.idVente = idVente; }

    public boolean isOfferte() { return offerteFidelite || offerteRetard; }
    
    public Pizza getPizza() { return pizza; }
    public Client getClient() { return client; }
    public Livreur getLivreur() { return livreur; }
    public Vehicule getVehicule() { return vehicule; }
}