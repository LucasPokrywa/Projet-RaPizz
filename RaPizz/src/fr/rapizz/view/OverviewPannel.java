package fr.rapizz.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.rapizz.dao.StatistiqueDAO;

public class OverviewPannel extends JPanel {

    private static final long serialVersionUID = 1L;

    public OverviewPannel() {

        setLayout(new BorderLayout());

        JLabel titre = new JLabel("Vue d'ensemble de RaPizz");
        titre.setFont(new Font("Arial", Font.BOLD, 24));

        add(titre, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 2, 15, 15));

        statsPanel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        StatistiqueDAO dao = new StatistiqueDAO();

        ajouterStat(
            statsPanel,
            "Chiffre d'affaires :",
            dao.chiffreAffairesTotal() + " €"
        );

        ajouterStat(
            statsPanel,
            "Meilleur client :",
            dao.meilleurClient()
        );

        ajouterStat(
            statsPanel,
            "Plus mauvais livreur :",
            dao.plusMauvaisLivreur()
        );

        ajouterStat(
            statsPanel,
            "Véhicule le plus utilisé :",
            dao.vehiculePlusUtilise()
        );

        ajouterStat(
            statsPanel,
            "Pizza la plus demandée :",
            dao.pizzaPlusDemandee()
        );

        ajouterStat(
            statsPanel,
            "Pizza la moins demandée :",
            dao.pizzaMoinsDemandee()
        );

        ajouterStat(
            statsPanel,
            "Ingrédient favori :",
            dao.ingredientFavori()
        );

        add(statsPanel, BorderLayout.CENTER);
    }

    private void ajouterStat(
            JPanel panel,
            String titre,
            String valeur
    ) {

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel labelValeur = new JLabel(valeur);
        labelValeur.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.add(labelTitre);
        panel.add(labelValeur);
    }
}