package fr.rapizz.model;

public class Livreur {
    private int idLivreur;
    private String nomLivreur;
    private String prenomLivreur;

    public Livreur() {}

    public Livreur(int idLivreur, String nomLivreur, String prenomLivreur) {
        this.idLivreur = idLivreur;
        this.nomLivreur = nomLivreur;
        this.prenomLivreur = prenomLivreur;
    }

    public int getIdLivreur() { return idLivreur; }
    public void setIdLivreur(int idLivreur) { this.idLivreur = idLivreur; }
    public String getNomLivreur() { return nomLivreur; }
    public void setNomLivreur(String nomLivreur) { this.nomLivreur = nomLivreur; }
    public String getPrenomLivreur() { return prenomLivreur; }
    public void setPrenomLivreur(String prenomLivreur) { this.prenomLivreur = prenomLivreur; }
}