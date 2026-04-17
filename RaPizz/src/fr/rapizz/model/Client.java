package fr.rapizz.model;

public class Client {
    private int idClient;
    private String nomClient;
    private String prenomClient;
    private String adresse;
    private String telephone;
    private double soldeCompte;

    public Client() {}

    public Client(int idClient, String nomClient, String prenomClient, String adresse, String telephone, double soldeCompte) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.adresse = adresse;
        this.telephone = telephone;
        this.soldeCompte = soldeCompte;
    }

    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public String getPrenomClient() { return prenomClient; }
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public double getSoldeCompte() { return soldeCompte; }
    public void setSoldeCompte(double soldeCompte) { this.soldeCompte = soldeCompte; }
}