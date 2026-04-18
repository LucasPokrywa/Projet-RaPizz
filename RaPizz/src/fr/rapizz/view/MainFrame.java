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

        tabs.addTab("Menu", new MenuPanel()); 
        tabs.addTab("Commande", new CommandePannel()); 
        tabs.addTab("Client", new ClientPannel());
        tabs.addTab("Apprivoisement", new ApprivoisementPannel());
        tabs.addTab("Overview", new OverviewPannel());


        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }
}