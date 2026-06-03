package fr.rapizz.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import fr.rapizz.dao.ClientDAO;
import fr.rapizz.model.Client;

public class ClientPannel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ClientDAO clientDAO = new ClientDAO();
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtAdresse;
    private JTextField txtTelephone;
    private JSpinner spSolde;
    private DefaultTableModel modelClients;
    private JTable tableClients;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private Integer idClientSelectionne;
    private boolean rechargementTableau;

    ClientPannel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildFormulaire(), BorderLayout.NORTH);
        add(buildTableau(), BorderLayout.CENTER);

        chargerClients();
    }

    private JPanel buildFormulaire() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nouveau client"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNom = new JTextField(20);
        txtPrenom = new JTextField(20);
        txtAdresse = new JTextField(35);
        txtTelephone = new JTextField(15);
        spSolde = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 99999.99, 1.00));

        ajouterChamp(panel, gbc, 0, "Nom :", txtNom);
        ajouterChamp(panel, gbc, 1, "Prenom :", txtPrenom);
        ajouterChamp(panel, gbc, 2, "Adresse :", txtAdresse);
        ajouterChamp(panel, gbc, 3, "Telephone :", txtTelephone);
        ajouterChamp(panel, gbc, 4, "Solde initial :", spSolde);

        btnCreer = new JButton("Creer le client");
        btnCreer.setBackground(new Color(40, 167, 69));
        btnCreer.setForeground(Color.WHITE);
        btnCreer.setFocusPainted(false);
        btnCreer.addActionListener(e -> creerClient());

        btnModifier = new JButton("Modifier");
        btnModifier.setBackground(new Color(0, 123, 255));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.setFocusPainted(false);
        btnModifier.addActionListener(e -> modifierClient());

        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setBackground(new Color(220, 53, 69));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.addActionListener(e -> supprimerClient());

        btnNouveau = new JButton("Nouveau");
        btnNouveau.addActionListener(e -> viderFormulaire());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnNouveau);
        actions.add(btnCreer);
        actions.add(btnModifier);
        actions.add(btnSupprimer);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(actions, gbc);

        mettreAJourModeFormulaire();
        return panel;
    }

    private void ajouterChamp(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component champ) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(champ, gbc);
    }

    private JScrollPane buildTableau() {
        String[] colonnes = {"ID", "Nom", "Prenom", "Adresse", "Telephone", "Solde", "Pizzas commandees"};
        modelClients = new DefaultTableModel(colonnes, 0) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableClients = new JTable(modelClients);
        tableClients.setRowHeight(24);
        tableClients.getTableHeader().setFont(tableClients.getTableHeader().getFont().deriveFont(Font.BOLD));
        tableClients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerClientSelectionneDansFormulaire();
            }
        });
        return new JScrollPane(tableClients);
    }

    public void rafraichirClients() {
        chargerClients(idClientSelectionne);
    }

    private void chargerClients() {
        chargerClients(idClientSelectionne);
    }

    private void chargerClients(Integer idClientASelectionner) {
        rechargementTableau = true;
        modelClients.setRowCount(0);
        List<Client> clients = clientDAO.getAllClients();
        int ligneASelectionner = -1;

        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            modelClients.addRow(new Object[]{
                client.getIdClient(),
                client.getNomClient(),
                client.getPrenomClient(),
                client.getAdresse(),
                client.getTelephone(),
                String.format("%.2f EUR", client.getSoldeCompte()),
                client.getPizzaCommande()
            });
            if (idClientASelectionner != null && client.getIdClient() == idClientASelectionner) {
                ligneASelectionner = i;
            }
        }

        if (ligneASelectionner >= 0) {
            tableClients.setRowSelectionInterval(ligneASelectionner, ligneASelectionner);
            idClientSelectionne = idClientASelectionner;
        } else {
            idClientSelectionne = null;
            viderChamps();
            mettreAJourModeFormulaire();
        }
        rechargementTableau = false;

        if (ligneASelectionner >= 0) {
            chargerClientSelectionneDansFormulaire();
        }
    }

    private void creerClient() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String adresse = txtAdresse.getText().trim();
        String telephone = txtTelephone.getText().trim();
        double solde = ((Number) spSolde.getValue()).doubleValue();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez renseigner le nom, le prenom, l'adresse et le telephone.",
                "Champs obligatoires", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Client client = new Client();
        client.setNomClient(nom);
        client.setPrenomClient(prenom);
        client.setAdresse(adresse);
        client.setTelephone(telephone);
        client.setSoldeCompte(solde);
        client.setPizzaCommande(0);

        boolean ok = clientDAO.creerClient(client);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Client cree avec succes.",
                "Creation effectuee", JOptionPane.INFORMATION_MESSAGE);
            viderFormulaire();
            chargerClients();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la creation du client.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viderFormulaire() {
        idClientSelectionne = null;
        if (tableClients != null) {
            tableClients.clearSelection();
        }
        viderChamps();
        mettreAJourModeFormulaire();
    }

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtAdresse.setText("");
        txtTelephone.setText("");
        spSolde.setValue(0.00);
    }

    private void chargerClientSelectionneDansFormulaire() {
        if (rechargementTableau) {
            return;
        }

        int ligne = tableClients.getSelectedRow();
        if (ligne < 0) {
            idClientSelectionne = null;
            mettreAJourModeFormulaire();
            return;
        }

        int modelRow = tableClients.convertRowIndexToModel(ligne);
        idClientSelectionne = (Integer) modelClients.getValueAt(modelRow, 0);
        txtNom.setText((String) modelClients.getValueAt(modelRow, 1));
        txtPrenom.setText((String) modelClients.getValueAt(modelRow, 2));
        txtAdresse.setText((String) modelClients.getValueAt(modelRow, 3));
        txtTelephone.setText((String) modelClients.getValueAt(modelRow, 4));
        mettreAJourModeFormulaire();
    }

    private void mettreAJourModeFormulaire() {
        boolean edition = idClientSelectionne != null;
        if (btnCreer != null) btnCreer.setEnabled(!edition);
        if (btnModifier != null) btnModifier.setEnabled(edition);
        if (btnSupprimer != null) btnSupprimer.setEnabled(edition);
        if (btnNouveau != null) btnNouveau.setEnabled(edition);
        if (spSolde != null) spSolde.setEnabled(!edition);
    }

    private void modifierClient() {
        if (idClientSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                "Selectionnez un client dans le tableau.",
                "Aucun client", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String adresse = txtAdresse.getText().trim();
        String telephone = txtTelephone.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez renseigner le nom, le prenom, l'adresse et le telephone.",
                "Champs obligatoires", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = clientDAO.modifierInfosClient(idClientSelectionne, nom, prenom, adresse, telephone);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Client modifie avec succes.",
                "Modification effectuee", JOptionPane.INFORMATION_MESSAGE);
            chargerClients(idClientSelectionne);
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification du client.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerClient() {
        if (idClientSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                "Selectionnez un client dans le tableau.",
                "Aucun client", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomComplet = txtPrenom.getText().trim() + " " + txtNom.getText().trim();
        int reponse = JOptionPane.showConfirmDialog(this,
            "Supprimer le client " + nomComplet + " ?",
            "Confirmation suppression", JOptionPane.YES_NO_OPTION);
        if (reponse != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = clientDAO.supprimerClient(idClientSelectionne);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Client supprime avec succes.",
                "Suppression effectuee", JOptionPane.INFORMATION_MESSAGE);
            viderFormulaire();
            chargerClients(null);
        } else {
            JOptionPane.showMessageDialog(this,
                "Impossible de supprimer ce client car il est associe a une ou plusieurs ventes enregistrees.",
                "Suppression impossible", JOptionPane.ERROR_MESSAGE);
        }
    }
}