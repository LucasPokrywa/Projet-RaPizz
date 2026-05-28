package fr.rapizz.view;

import fr.rapizz.dao.ClientDAO;
import fr.rapizz.model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientPannel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField tfNom     = new JTextField(15);
    private JTextField tfPrenom  = new JTextField(15);
    private JTextField tfAdresse = new JTextField(20);
    private JTextField tfSolde   = new JTextField(8);

    private DefaultTableModel tableModel;
    private ClientDAO clientDAO = new ClientDAO();

    ClientPannel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulaire de création -----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Créer un nouveau client"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfPrenom, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfAdresse, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Solde initial (€) :"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfSolde, gbc);

        JButton btnCreer = new JButton("Créer le client");
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnCreer, gbc);

        // ----- Tableau des clients existants -----
        String[] colonnes = {"ID", "Nom", "Prénom", "Adresse", "Solde (€)", "Pizzas commandées"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Clients enregistrés"));

        add(formPanel, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);

        // ----- Actions -----
        btnCreer.addActionListener(e -> creerClient());
        rafraichirTableau();
    }

    private void creerClient() {
        String nom      = tfNom.getText().trim();
        String prenom   = tfPrenom.getText().trim();
        String adresse  = tfAdresse.getText().trim();
        String soldeStr = tfSolde.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir le nom, prénom et adresse.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double solde = 0.0;
        if (!soldeStr.isEmpty()) {
            try {
                solde = Double.parseDouble(soldeStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Le solde doit être un nombre (ex: 10.50).",
                    "Solde invalide", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Client client = new Client();
        client.setNomClient(nom);
        client.setPrenomClient(prenom);
        client.setAdresse(adresse);
        client.setSoldeCompte(solde);

        boolean ok = clientDAO.creerClient(client);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Client créé avec succès !",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            tfNom.setText("");
            tfPrenom.setText("");
            tfAdresse.setText("");
            tfSolde.setText("");
            rafraichirTableau();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création du client.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirTableau() {
        tableModel.setRowCount(0);
        List<Client> clients = clientDAO.getAllClients();
        for (Client c : clients) {
            tableModel.addRow(new Object[]{
                c.getIdClient(),
                c.getNomClient(),
                c.getPrenomClient(),
                c.getAdresse(),
                String.format("%.2f", c.getSoldeCompte()),
                c.getPizzaCommande()
            });
        }
    }
}