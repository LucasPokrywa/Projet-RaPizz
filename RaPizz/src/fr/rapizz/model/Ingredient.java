package fr.rapizz.model;

public class Ingredient {
    private int idIngredient;
    private String nomIngredient;

    public Ingredient() {}

    public Ingredient(int idIngredient, String nomIngredient) {
        this.idIngredient = idIngredient;
        this.nomIngredient = nomIngredient;
    }

    public int getIdIngredient() { 
        return idIngredient; 
    }

    public void setIdIngredient(int idIngredient) { 
        this.idIngredient = idIngredient; 
    }

    public String getNomIngredient() { 
        return nomIngredient; 
    }

    public void setNomIngredient(String nomIngredient) { 
        this.nomIngredient = nomIngredient; 
    }

    
    @Override
    public String toString() {
        return nomIngredient;
    }
}