package fr.rapizz.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame() {
        setTitle("RaPizz - Gestion");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        CommandePannel commandePanel = new CommandePannel();
        ClientPannel clientPanel = new ClientPannel();
        ApprovisionnementPanel approvisionnementPanel = new ApprovisionnementPanel();

        tabs.addTab("Menu", new MenuPanel()); 
        tabs.addTab("Commande", commandePanel); 
        tabs.addTab("Client", clientPanel);
        tabs.addTab("Approvisionnement", approvisionnementPanel);
        tabs.addTab("Overview", new OverviewPannel());

        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected == commandePanel) {
                commandePanel.rafraichirClients();
            } else if (selected == clientPanel) {
                clientPanel.rafraichirClients();
            } else if (selected == approvisionnementPanel) {
                approvisionnementPanel.rafraichirClients();
            }
        });

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }
}
