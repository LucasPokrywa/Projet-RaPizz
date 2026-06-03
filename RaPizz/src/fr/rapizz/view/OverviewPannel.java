package fr.rapizz.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.rapizz.dao.StatistiqueDAO;

public class OverviewPannel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JPanel statsPanel;
    private final StatistiqueDAO dao = new StatistiqueDAO();

    private final Color backgroundColor = new Color(255, 248, 240);
    private final Color redColor = new Color(170, 20, 20);

    public OverviewPannel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        setBackground(backgroundColor);

        JLabel titre = new JLabel("Vue d'ensemble de RaPizz");
        titre.setFont(new Font("Arial", Font.BOLD, 30));
        titre.setForeground(redColor);

        JButton boutonActualiser = new JButton("↻ Actualiser");
        boutonActualiser.setFont(new Font("Arial", Font.BOLD, 15));
        boutonActualiser.setForeground(Color.WHITE);
        boutonActualiser.setBackground(redColor);
        boutonActualiser.setFocusPainted(false);

        boutonActualiser.addActionListener(e -> chargerStatistiques());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.add(titre, BorderLayout.WEST);
        header.add(boutonActualiser, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        statsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        statsPanel.setBackground(backgroundColor);

        add(statsPanel, BorderLayout.CENTER);

        chargerStatistiques();
    }

    private void chargerStatistiques() {
        statsPanel.removeAll();

        ajouterCarte("€", "Chiffre d'affaires", dao.chiffreAffairesTotal() + " €");
        ajouterCarte("👤", "Meilleur client", dao.meilleurClient());
        ajouterCarte("🛵", "Plus mauvais livreur", dao.plusMauvaisLivreur());
        ajouterCarte("🚗", "Véhicule le plus utilisé", dao.vehiculePlusUtilise());
        ajouterCarte("🍕", "Pizza la plus demandée", dao.pizzaPlusDemandee());
        ajouterCarte("🍕", "Pizza la moins demandée", dao.pizzaMoinsDemandee());
        ajouterCarte("🌿", "Ingrédient favori", dao.ingredientFavori());
        ajouterCarte("🛍", "Nombre total de ventes", dao.nombreTotalVentes() + " ventes");

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void ajouterCarte(String icone, String titre, String valeur) {
        JPanel carte = new JPanel(new BorderLayout(18, 10));
        carte.setBackground(Color.WHITE);

        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel labelIcone = new JLabel(icone);
        labelIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitre.setForeground(redColor);

        JLabel labelValeur = new JLabel(valeur);
        labelValeur.setFont(new Font("Arial", Font.BOLD, 22));
        labelValeur.setForeground(Color.BLACK);

        JPanel textePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textePanel.setBackground(Color.WHITE);
        textePanel.add(labelTitre);
        textePanel.add(labelValeur);

        carte.add(labelIcone, BorderLayout.WEST);
        carte.add(textePanel, BorderLayout.CENTER);

        statsPanel.add(carte);
    }
}