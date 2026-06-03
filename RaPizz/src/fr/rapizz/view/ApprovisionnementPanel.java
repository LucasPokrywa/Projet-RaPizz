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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import fr.rapizz.dao.ClientDAO;
import fr.rapizz.model.Client;

public class ApprovisionnementPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private final ClientDAO clientDAO = new ClientDAO();
	private JComboBox<String> cbClients;
	private JSpinner spMontant;
	private JLabel lblSolde;
	private List<Client> clients;

	public ApprovisionnementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(buildFormulaire(), BorderLayout.NORTH);
        chargerClients();
    }

	private JPanel buildFormulaire() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Approvisionner un compte client"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		panel.add(new JLabel("Client :"), gbc);

		cbClients = new JComboBox<>();
		cbClients.addActionListener(e -> mettreAJourSolde());
		gbc.gridx = 1;
		gbc.weightx = 1;
		panel.add(cbClients, gbc);

		// JButton btnActualiser = new JButton("Actualiser");
		// btnActualiser.addActionListener(e -> chargerClients());
		// gbc.gridx = 2;
		// gbc.weightx = 0;
		// panel.add(btnActualiser, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Montant a ajouter :"), gbc);

		spMontant = new JSpinner(new SpinnerNumberModel(10.00, 0.01, 99999.99, 1.00));
		gbc.gridx = 1;
		gbc.weightx = 1;
		panel.add(spMontant, gbc);

		lblSolde = new JLabel("Solde actuel : -");
		lblSolde.setFont(lblSolde.getFont().deriveFont(Font.BOLD));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		panel.add(lblSolde, gbc);

		JButton btnValider = new JButton("Valider");
		btnValider.setBackground(new Color(40, 167, 69));
		btnValider.setForeground(Color.WHITE);
		btnValider.setFocusPainted(false);
		btnValider.addActionListener(e -> validerApprovisionnement());

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		actions.add(btnValider);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		panel.add(actions, gbc);

		return panel;
	}

	public void rafraichirClients() {
		chargerClients();
	}

	private void chargerClients() {
		chargerClients(getIdClientSelectionne());
	}

	private void chargerClients(Integer idClientASelectionner) {
		clients = clientDAO.getAllClients();
		cbClients.removeAllItems();
		int indexASelectionner = -1;

		for (int i = 0; i < clients.size(); i++) {
			Client client = clients.get(i);
			cbClients.addItem(formatClient(client));
			if (idClientASelectionner != null && client.getIdClient() == idClientASelectionner) {
				indexASelectionner = i;
			}
		}

		if (indexASelectionner >= 0) {
			cbClients.setSelectedIndex(indexASelectionner);
		}

		mettreAJourSolde();
	}

	private Integer getIdClientSelectionne() {
		int idx = cbClients == null ? -1 : cbClients.getSelectedIndex();
		if (clients == null || idx < 0 || idx >= clients.size()) {
			return null;
		}
		return clients.get(idx).getIdClient();
	}

	private String formatClient(Client client) {
		return client.getPrenomClient() + " " + client.getNomClient() + " (ID " + client.getIdClient() + ")";
	}

	private void mettreAJourSolde() {
		int idx = cbClients.getSelectedIndex();
		if (clients == null || idx < 0 || idx >= clients.size()) {
			lblSolde.setText("Solde actuel : -");
			return;
		}

		Client client = clients.get(idx);
		double solde = clientDAO.obtenirSolde(client.getIdClient());
		lblSolde.setText(String.format("Solde actuel : %.2f EUR", solde));
	}

	private void validerApprovisionnement() {
		int idx = cbClients.getSelectedIndex();
		if (clients == null || idx < 0 || idx >= clients.size()) {
			JOptionPane.showMessageDialog(this,
					"Veuillez selectionner un client.",
					"Aucun client", JOptionPane.WARNING_MESSAGE);
			return;
		}

		double montant = ((Number) spMontant.getValue()).doubleValue();
		if (montant <= 0) {
			JOptionPane.showMessageDialog(this,
					"Le montant doit etre strictement positif.",
					"Montant invalide", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Client client = clients.get(idx);
		boolean ok = clientDAO.ajouterAuSolde(client.getIdClient(), montant);

		if (ok) {
			JOptionPane.showMessageDialog(this,
					String.format("%.2f EUR ont ete ajoutes au compte de %s %s.",
							montant, client.getPrenomClient(), client.getNomClient()),
					"Approvisionnement effectue", JOptionPane.INFORMATION_MESSAGE);
			chargerClients(client.getIdClient());
		} else {
			JOptionPane.showMessageDialog(this,
					"Erreur lors de l'approvisionnement du compte.",
					"Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
