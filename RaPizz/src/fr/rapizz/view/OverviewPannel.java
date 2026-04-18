package fr.rapizz.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.rapizz.dao.ClientDAO;
import fr.rapizz.model.Client;

public class OverviewPannel extends JPanel {
	private static final long serialVersionUID = 1L;

	OverviewPannel(){
		setLayout(new FlowLayout(FlowLayout.LEFT));

	    JPanel wrapper = new JPanel(new GridLayout(0, 2, 5, 2));
	    
	    ClientDAO dao = new ClientDAO();
	    Client top = dao.meilleurClient();

	    wrapper.add(new JLabel("Meilleur Client :"));
	    wrapper.add(new JLabel(top != null ? top.getPrenomClient() + " " + top.getNomClient() : "Aucun"));

	    wrapper.add(new JLabel("Total Pizzas :"));
	    wrapper.add(new JLabel(top != null ? String.valueOf(top.getPizzaCommande()) : "0"));

	    add(wrapper);          
	}
}
