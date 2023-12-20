import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HotelViewer extends JFrame {

    private JPanel hotelPanel;

    public HotelViewer() {
        setTitle("Évaluations d'Hôtels");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        hotelPanel = new JPanel(new GridLayout(0, 1));

        JScrollPane scrollPane = new JScrollPane(hotelPanel);
        add(scrollPane);

        // Se connecter à la base de données et récupérer les données de l'hôtel
        fetchHotelData();

        pack(); // Pack the frame to ensure proper sizing
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private void fetchHotelData() {
        try {
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Se connecter à la base de données
            String url = "jdbc:mysql://localhost:3306/projet_java";
            String username = "root";
            String password = "ramzi";

            try (Connection connection = DriverManager.getConnection(url, username, password)) {

                // Exécuter la requête pour récupérer les données de l'hôtel
                String query = "SELECT nomhotel FROM hotel";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    var resultSet = preparedStatement.executeQuery();

                    // Afficher les noms d'hôtels avec la couleur de texte rouge
                    while (resultSet.next()) {
                        String hotelName = resultSet.getString("nomhotel");
                        createHotelPanel(hotelName);
                    }
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createHotelPanel(String hotelName) {
        JButton hotelButton = new JButton(hotelName);
        hotelButton.setFont(new Font("Arial", Font.PLAIN, 16));
        hotelButton.setForeground(Color.RED);

        hotelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHotelPage(hotelName);
            }
        });

        hotelPanel.add(hotelButton);
    }

    private void openHotelPage(String hotelName) {
        JFrame hotelFrame = new JFrame(hotelName);
        hotelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel hotelPanel = new JPanel(new GridLayout(10, 2));

        // Ajouter des composants pour les détails de l'hôtel
        addLabelAndTextField(hotelPanel, "Nom de l'hôtel:", hotelName);
        addLabelAndStarRatingPanel(hotelPanel, "Note de sécurité:");
        addLabelAndStarRatingPanel(hotelPanel, "Note de nourriture:");
        addLabelAndStarRatingPanel(hotelPanel, "Note de propreté:");
        addLabelAndStarRatingPanel(hotelPanel, "Note de la chambre:");
        addLabelAndStarRatingPanel(hotelPanel, "Note de l'emplacement:");
        addLabelAndStarRatingPanel(hotelPanel, "Note du service:");

        // Ajouter Commentaire Label et TextArea
        JLabel commentLabel = new JLabel("Commentaire:");
        JTextArea commentTextArea = new JTextArea();
        JScrollPane commentScrollPane = new JScrollPane(commentTextArea);
        hotelPanel.add(commentLabel);
        hotelPanel.add(commentScrollPane);

        JButton submitButton = new JButton("Soumettre");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Effectuer des actions lorsque le bouton de soumission est cliqué
                // Vous pouvez enregistrer les notes dans la base de données ou effectuer d'autres actions
                int securityRating = getStarRatingPanelValue(hotelPanel, "Note de sécurité:");
                int foodRating = getStarRatingPanelValue(hotelPanel, "Note de nourriture:");
                int cleanlinessRating = getStarRatingPanelValue(hotelPanel, "Note de propreté:");
                int roomRating = getStarRatingPanelValue(hotelPanel, "Note de la chambre:");
                int locationRating = getStarRatingPanelValue(hotelPanel, "Note de l'emplacement:");
                int serviceRating = getStarRatingPanelValue(hotelPanel, "Note du service:");
                String comment = commentTextArea.getText();

                // Insérer les notes dans la table "notation" avec un timestamp
                insertRatingsIntoTable(hotelName, securityRating, foodRating, cleanlinessRating,
                        roomRating, locationRating, serviceRating, comment);

                // Fermer hotelFrame
                hotelFrame.dispose();
            }
        });

        hotelPanel.add(submitButton);

        // Ajouter hotelPanel à hotelFrame
        hotelFrame.add(hotelPanel);

        // Définir la taille, la visibilité et l'emplacement de hotelFrame
        hotelFrame.pack();
        hotelFrame.setLocationRelativeTo(null);
        hotelFrame.setVisible(true);
    }

    private void addLabelAndTextField(JPanel panel, String labelText, String initialValue) {
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(initialValue);
        panel.add(label);
        panel.add(textField);
    }

    private void addLabelAndStarRatingPanel(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        StarRatingPanel starRatingPanel = new StarRatingPanel();
        panel.add(label);
        panel.add(starRatingPanel);
    }

    private int getStarRatingPanelValue(JPanel panel, String labelText) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel && ((JLabel) component).getText().equals(labelText)) {
                int index = panel.getComponentZOrder(component);
                StarRatingPanel starRatingPanel = (StarRatingPanel) panel.getComponent(index + 1);
                return starRatingPanel.getRating();
            }
        }
        return 0;
    }

    private void insertRatingsIntoTable(String hotelName, int security, int food, int cleanliness,
                                        int room, int location, int service, String comment) {
        try {
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Se connecter à la base de données
            String url = "jdbc:mysql://localhost:3306/projet_java";
            String username = "root";
            String password = "ramzi";

            try (Connection connection = DriverManager.getConnection(url, username, password)) {

                // Insérer les notes dans la table "notation" avec gestion de conflit
                String insertQuery = "INSERT INTO notation (hotelid, securite, nourriture, proprete, chambre, emplacement, service, commentaire, timestamp) " +
                        "VALUES ((SELECT hotelid FROM hotel WHERE nomhotel = ?), ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "securite = VALUES(securite), nourriture = VALUES(nourriture), proprete = VALUES(proprete), " +
                        "chambre = VALUES(chambre), emplacement = VALUES(emplacement), service = VALUES(service), " +
                        "commentaire = VALUES(commentaire), timestamp = CURRENT_TIMESTAMP";

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, hotelName);
                    insertStatement.setInt(2, security);
                    insertStatement.setInt(3, food);
                    insertStatement.setInt(4, cleanliness);
                    insertStatement.setInt(5, room);
                    insertStatement.setInt(6, location);
                    insertStatement.setInt(7, service);
                    insertStatement.setString(8, comment);

                    insertStatement.executeUpdate();

                    System.out.println("Notes insérées dans la table 'notation' pour " + hotelName);
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelViewer());
    }
}
