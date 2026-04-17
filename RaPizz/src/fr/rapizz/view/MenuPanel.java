package fr.rapizz.view;

import fr.rapizz.dao.PizzaDAO;
import fr.rapizz.model.Pizza;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuPanel extends JPanel {

    private static final long serialVersionUID = 1L;

	public MenuPanel() {
        setLayout(new BorderLayout());
        
        String[] colonnes = {"ID", "Nom", "Prix de Base", "Ingrédients"};
        DefaultTableModel model = new DefaultTableModel(colonnes, 0);
        JTable table = new JTable(model);

        PizzaDAO pizzaDAO = new PizzaDAO();
        List<Pizza> pizzas = pizzaDAO.getMenu();

        for (Pizza p : pizzas) {
        	System.out.println("J'ai trouvé une pizza : " + p.getNomPizza());
            String ingredientsAffichage = "";
            if (p.getIngredients() != null && !p.getIngredients().isEmpty()) {
                ingredientsAffichage = String.join(", ", p.getIngredients());
            } else {
                ingredientsAffichage = "Aucun ingrédient";
            }

            Object[] row = {
                p.getIdPizza(),
                p.getNomPizza(),
                p.getPrixBase() + " €",
                ingredientsAffichage
            };
            model.addRow(row);
        }
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }
}