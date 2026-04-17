package fr.rapizz.model;

public class Vehicule {
    private int idVehicule;
    private String immatriculation;
    private String typeVehicule;

    public Vehicule() {}

    public Vehicule(int idVehicule, String immatriculation, String typeVehicule) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.typeVehicule = typeVehicule;
    }

    public int getIdVehicule() { return idVehicule; }
    public void setIdVehicule(int idVehicule) { this.idVehicule = idVehicule; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public String getTypeVehicule() { return typeVehicule; }
    public void setTypeVehicule(String typeVehicule) { this.typeVehicule = typeVehicule; }
}