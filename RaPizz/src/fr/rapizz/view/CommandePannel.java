package fr.rapizz.view;

import fr.rapizz.dao.*;
import fr.rapizz.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panneau "Commande" — trois onglets :
 *  1. Nouvelle commande  (attribution automatique véhicule + livreur dispo)
 *  2. Livraisons en cours (clôture par bouton "Livré")
 *  3. Suivi activité     (livreurs & véhicules)
 */
public class CommandePannel extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final ClientDAO   clientDAO   = new ClientDAO();
    private final PizzaDAO    pizzaDAO    = new PizzaDAO();
    private final LivreurDAO  livreurDAO  = new LivreurDAO();
    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private final VenteDAO    venteDAO    = new VenteDAO();

    // ── Onglet 1 : Nouvelle commande ─────────────────────────────────────────
    private JComboBox<String>  cbClients;
    private JComboBox<String>  cbPizzas;
    private JComboBox<String>  cbTailles;
    private JComboBox<String>  cbLivreurs;
    private JLabel             lblVehiculeAttribue;
    private JLabel             lblPrix;
    private JLabel             lblSolde;
    private JButton            btnCommander;

    // données brutes correspondant aux combos
    private List<Client>  listeClients;
    private List<Pizza>   listePizzas;
    private List<Livreur> listeLivreurs;

    // ── Onglet 2 : Livraisons en cours ───────────────────────────────────────
    private DefaultTableModel modelEnCours;
    private JTable            tableEnCours;
    private List<Integer>     idsVentesEnCours;

    // ── Onglet 3 : Suivi activité ─────────────────────────────────────────────
    private DefaultTableModel modelLivreurs;
    private DefaultTableModel modelVehicules;

    // ─────────────────────────────────────────────────────────────────────────

    public CommandePannel() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Nouvelle commande",   buildOngletCommande());
        tabs.addTab("Livraisons en cours", buildOngletEnCours());
        tabs.addTab("Suivi activité",      buildOngletSuivi());

        // Rafraîchir les données à chaque changement d'onglet
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) rafraichirClients();
            if (idx == 1) rafraichirEnCours();
            if (idx == 2) rafraichirSuivi();
        });

        add(tabs, BorderLayout.CENTER);
    }

    // =========================================================================
    //  ONGLET 1 – Nouvelle commande
    // =========================================================================
    private JPanel buildOngletCommande() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ── Client ──────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Client :"), gbc);
        cbClients = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cbClients, gbc);

        lblSolde = new JLabel("Solde : —");
        lblSolde.setFont(lblSolde.getFont().deriveFont(Font.ITALIC));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblSolde, gbc);

        // ── Pizza ────────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Pizza :"), gbc);
        cbPizzas = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cbPizzas, gbc);

        // ── Taille ───────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Taille :"), gbc);
        cbTailles = new JComboBox<>(new String[]{"humaine", "naine", "ogresse"});
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cbTailles, gbc);

        lblPrix = new JLabel("Prix : —");
        lblPrix.setFont(lblPrix.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblPrix, gbc);

        // ── Livreur ──────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Livreur :"), gbc);
        cbLivreurs = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cbLivreurs, gbc);

        // ── Véhicule attribué (automatique) ──────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Véhicule attribué :"), gbc);
        lblVehiculeAttribue = new JLabel("— (calculé à la commande)");
        lblVehiculeAttribue.setForeground(new Color(0, 120, 0));
        lblVehiculeAttribue.setFont(lblVehiculeAttribue.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(lblVehiculeAttribue, gbc);

        // ── Bouton commande ───────────────────────────────────────────────────
        btnCommander = new JButton("Valider la commande");
        btnCommander.setBackground(new Color(220, 53, 69));
        btnCommander.setForeground(Color.WHITE);
        btnCommander.setFocusPainted(false);
        btnCommander.setFont(btnCommander.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(btnCommander, gbc);

        // ── Listeners ─────────────────────────────────────────────────────────
        cbClients.addActionListener(e -> mettreAJourSolde());
        cbPizzas.addActionListener(e  -> mettreAJourPrix());
        cbTailles.addActionListener(e -> mettreAJourPrix());

        btnCommander.addActionListener(e -> validerCommande());

        // Chargement initial
        chargerDonneesCommande();

        return panel;
    }

    public void rafraichirClients() {
        chargerDonneesCommande();
    }

    private void chargerDonneesCommande() {
        Integer idClientSelectionne = getIdClientSelectionne();

        // Clients
        listeClients = clientDAO.getAllClients();
        cbClients.removeAllItems();
        int indexClientASelectionner = -1;
        for (int i = 0; i < listeClients.size(); i++) {
            Client c = listeClients.get(i);
            cbClients.addItem(c.getPrenomClient() + " " + c.getNomClient());
            if (idClientSelectionne != null && c.getIdClient() == idClientSelectionne) {
                indexClientASelectionner = i;
            }
        }
        if (indexClientASelectionner >= 0) {
            cbClients.setSelectedIndex(indexClientASelectionner);
        }

        // Pizzas
        listePizzas = pizzaDAO.getMenu();
        cbPizzas.removeAllItems();
        for (Pizza p : listePizzas)
            cbPizzas.addItem(p.getNomPizza() + " (" + p.getPrixBase() + " €)");

        // Livreurs disponibles
        listeLivreurs = livreurDAO.getLivreursDisponibles();
        cbLivreurs.removeAllItems();
        if (listeLivreurs.isEmpty()) {
            cbLivreurs.addItem("Aucun livreur disponible");
        } else {
            for (Livreur l : listeLivreurs)
                cbLivreurs.addItem(l.getPrenomLivreur() + " " + l.getNomLivreur());
        }

        // Véhicule dispo (prévisualisation)
        Vehicule vDispo = vehiculeDAO.getVehiculeDisponible();
        lblVehiculeAttribue.setText(vDispo != null
                ? vDispo.toString()
                : "Aucun véhicule disponible !");

        mettreAJourSolde();
        mettreAJourPrix();
    }

    private Integer getIdClientSelectionne() {
        int idx = cbClients == null ? -1 : cbClients.getSelectedIndex();
        if (listeClients == null || idx < 0 || idx >= listeClients.size()) {
            return null;
        }
        return listeClients.get(idx).getIdClient();
    }

    private void mettreAJourSolde() {
        int idx = cbClients.getSelectedIndex();
        if (idx < 0 || idx >= listeClients.size()) return;
        double solde = clientDAO.obtenirSolde(listeClients.get(idx).getIdClient());
        lblSolde.setText(String.format("Solde : %.2f €", solde));
        lblSolde.setForeground(solde < 5 ? Color.RED : new Color(0, 100, 0));
    }

    private void mettreAJourPrix() {
        int idxP = cbPizzas.getSelectedIndex();
        if (idxP < 0 || idxP >= listePizzas.size()) return;
        double base   = listePizzas.get(idxP).getPrixBase();
        String taille = (String) cbTailles.getSelectedItem();
        double prix   = calculerPrix(base, taille);
        lblPrix.setText(String.format("Prix : %.2f €", prix));
    }

    private double calculerPrix(double base, String taille) {
        return switch (taille) {
            case "naine"   -> base * 2.0 / 3.0;
            case "ogresse" -> base * 4.0 / 3.0;
            default        -> base;
        };
    }

    private void validerCommande() {
        int idxC = cbClients.getSelectedIndex();
        int idxP = cbPizzas.getSelectedIndex();
        int idxL = cbLivreurs.getSelectedIndex();

        if (idxC < 0 || idxP < 0 || listeLivreurs.isEmpty() || idxL < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un client, une pizza et un livreur disponible.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicule vDispo = vehiculeDAO.getVehiculeDisponible();
        if (vDispo == null) {
            JOptionPane.showMessageDialog(this,
                "Aucun véhicule disponible pour le moment. Réessayez plus tard.",
                "Flotte occupée", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client  client  = listeClients.get(idxC);
        Pizza   pizza   = listePizzas.get(idxP);
        Livreur livreur = listeLivreurs.get(idxL);
        String  taille  = (String) cbTailles.getSelectedItem();
        double  prix    = calculerPrix(pizza.getPrixBase(), taille);

        // Vérification fidélité (10e pizza)
        int nbPizzas = clientDAO.recupererNbPizzaCommandees(client.getIdClient());
        boolean fidelite = nbPizzas > 0 && (nbPizzas % 10 == 0);
        double  prixFinal = fidelite ? 0.0 : prix;

        // Confirmation
        String msg = String.format(
            "Résumé de la commande :%n" +
            "  Client  : %s %s  (solde : %.2f €)%n" +
            "  Pizza   : %s — %s%n" +
            "  Livreur : %s %s%n" +
            "  Véhicule: %s%n" +
            "  Montant : %.2f €%s%n" +
            "%nConfirmer ?",
            client.getPrenomClient(), client.getNomClient(),
            clientDAO.obtenirSolde(client.getIdClient()),
            pizza.getNomPizza(), taille,
            livreur.getPrenomLivreur(), livreur.getNomLivreur(),
            vDispo,
            prixFinal,
            fidelite ? " (OFFERTE — fidélité 🎉)" : ""
        );

        int rep = JOptionPane.showConfirmDialog(this, msg,
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (rep != JOptionPane.YES_OPTION) return;

        int idVente = venteDAO.passerCommande(
            client.getIdClient(),
            livreur.getIdLivreur(),
            pizza.getIdPizza(),
            taille,
            prixFinal
        );

        if (idVente > 0) {
            JOptionPane.showMessageDialog(this,
                "✅ Commande #" + idVente + " enregistrée !\n"
                + "Véhicule attribué : " + vDispo,
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerDonneesCommande(); // rafraîchir dispos
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Échec de la commande (solde insuffisant ou erreur BDD).",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    //  ONGLET 2 – Livraisons en cours
    // =========================================================================
    private JPanel buildOngletEnCours() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Livreur", "Véhicule", "Client", "Pizza / Taille", "H. commande"};
        modelEnCours = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableEnCours = new JTable(modelEnCours);
        tableEnCours.setRowHeight(24);
        tableEnCours.getTableHeader().setFont(
                tableEnCours.getTableHeader().getFont().deriveFont(Font.BOLD));

        panel.add(new JScrollPane(tableEnCours), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnRefresh = new JButton("🔄 Actualiser");
        btnRefresh.addActionListener(e -> rafraichirEnCours());
        south.add(btnRefresh);

        JButton btnLivre = new JButton("✅ Marquer comme livré");
        btnLivre.setBackground(new Color(40, 167, 69));
        btnLivre.setForeground(Color.WHITE);
        btnLivre.setFocusPainted(false);
        btnLivre.addActionListener(e -> marquerLivre());
        south.add(btnLivre);

        panel.add(south, BorderLayout.SOUTH);

        rafraichirEnCours();
        return panel;
    }

    private void rafraichirEnCours() {
        modelEnCours.setRowCount(0);
        List<String[]> rows = venteDAO.getLivraisonsEnCours();
        idsVentesEnCours = venteDAO.getIdsVentesEnCours();
        for (String[] row : rows) modelEnCours.addRow(row);
    }

    private void marquerLivre() {
        int sel = tableEnCours.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                "Sélectionnez une livraison dans le tableau.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVente = idsVentesEnCours.get(sel);
        boolean ok  = venteDAO.enregistrerLivraison(idVente);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Livraison #" + idVente + " clôturée.\n"
                + "(Gratuité retard appliquée si > 30 min.)",
                "Livré !", JOptionPane.INFORMATION_MESSAGE);
            rafraichirEnCours();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la clôture de la livraison.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    //  ONGLET 3 – Suivi activité livreurs & véhicules
    // =========================================================================
    private JPanel buildOngletSuivi() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Tableau livreurs ─────────────────────────────────────────────────
        JPanel panelLiv = new JPanel(new BorderLayout(5, 5));
        panelLiv.setBorder(BorderFactory.createTitledBorder("Activité des livreurs"));
        String[] colsLiv = {"Livreur", "Total livraisons", "Retards", "Statut"};
        modelLivreurs = new DefaultTableModel(colsLiv, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableLiv = new JTable(modelLivreurs);
        tableLiv.setRowHeight(24);
        tableLiv.getTableHeader().setFont(
                tableLiv.getTableHeader().getFont().deriveFont(Font.BOLD));
        panelLiv.add(new JScrollPane(tableLiv), BorderLayout.CENTER);
        panel.add(panelLiv);

        // ── Tableau véhicules ─────────────────────────────────────────────────
        JPanel panelVeh = new JPanel(new BorderLayout(5, 5));
        panelVeh.setBorder(BorderFactory.createTitledBorder("Activité des véhicules"));
        String[] colsVeh = {"Immatriculation", "Type", "Total livraisons", "Statut"};
        modelVehicules = new DefaultTableModel(colsVeh, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableVeh = new JTable(modelVehicules);
        tableVeh.setRowHeight(24);
        tableVeh.getTableHeader().setFont(
                tableVeh.getTableHeader().getFont().deriveFont(Font.BOLD));
        panelVeh.add(new JScrollPane(tableVeh), BorderLayout.CENTER);
        panel.add(panelVeh);

        // Bouton rafraîchir global
        JButton btnRefresh = new JButton("🔄 Actualiser");
        btnRefresh.addActionListener(e -> rafraichirSuivi());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRefresh);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(south, BorderLayout.SOUTH);

        rafraichirSuivi();
        return wrapper;
    }

    private void rafraichirSuivi() {
        modelLivreurs.setRowCount(0);
        for (String[] row : livreurDAO.getSuiviActiviteLivreurs())
            modelLivreurs.addRow(row);

        modelVehicules.setRowCount(0);
        for (String[] row : vehiculeDAO.getSuiviActiviteVehicules())
            modelVehicules.addRow(row);
    }
}
