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

            Connection connection = DriverManager.getConnection(url, username, password);

            // Exécuter la requête pour récupérer les données de l'hôtel
            String query = "SELECT nomhotel FROM hotel";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();

            // Afficher les noms d'hôtels avec la couleur de texte rouge
            while (resultSet.next()) {
                String hotelName = resultSet.getString("nomhotel");
                createHotelPanel(hotelName);
            }

            // Fermer les ressources
            resultSet.close();
            preparedStatement.close();
            connection.close();

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

        JPanel hotelPanel = new JPanel(new GridLayout(8, 2));

        // Ajouter des composants pour les détails de l'hôtel (vous pouvez personnaliser cela)
        JLabel nameLabel = new JLabel("Nom de l'hôtel:");
        JTextField nameField = new JTextField(hotelName);
        JLabel securityLabel = new JLabel("Note de sécurité:");
        StarRatingPanel securityRatingPanel = new StarRatingPanel();
        JLabel foodLabel = new JLabel("Note de nourriture:");
        StarRatingPanel foodRatingPanel = new StarRatingPanel();
        JLabel cleanlinessLabel = new JLabel("Note de propreté:");
        StarRatingPanel cleanlinessRatingPanel = new StarRatingPanel();
        JLabel roomLabel = new JLabel("Note de la chambre:");
        StarRatingPanel roomRatingPanel = new StarRatingPanel();
        JLabel locationLabel = new JLabel("Note de l'emplacement:");
        StarRatingPanel locationRatingPanel = new StarRatingPanel();
        JLabel serviceLabel = new JLabel("Note du service:");
        StarRatingPanel serviceRatingPanel = new StarRatingPanel();

        // Ajouter des composants à hotelPanel
        hotelPanel.add(nameLabel);
        hotelPanel.add(nameField);
        hotelPanel.add(securityLabel);
        hotelPanel.add(securityRatingPanel);
        hotelPanel.add(foodLabel);
        hotelPanel.add(foodRatingPanel);
        hotelPanel.add(cleanlinessLabel);
        hotelPanel.add(cleanlinessRatingPanel);
        hotelPanel.add(roomLabel);
        hotelPanel.add(roomRatingPanel);
        hotelPanel.add(locationLabel);
        hotelPanel.add(locationRatingPanel);
        hotelPanel.add(serviceLabel);
        hotelPanel.add(serviceRatingPanel);

        JButton submitButton = new JButton("Soumettre");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Effectuer des actions lorsque le bouton de soumission est cliqué
                // Vous pouvez enregistrer les notes dans la base de données ou effectuer d'autres actions
                int securityRating = securityRatingPanel.getRating();
                int foodRating = foodRatingPanel.getRating();
                int cleanlinessRating = cleanlinessRatingPanel.getRating();
                int roomRating = roomRatingPanel.getRating();
                int locationRating = locationRatingPanel.getRating();
                int serviceRating = serviceRatingPanel.getRating();

                // Insérer les notes dans la table "notation" avec un timestamp
                insertRatingsIntoTable(hotelName, securityRating, foodRating, cleanlinessRating,
                        roomRating, locationRating, serviceRating);

                // Fermer hotelFrame
                hotelFrame.dispose();
            }
        });

        // Ajouter submitButton à hotelPanel
        hotelPanel.add(submitButton);

        // Ajouter hotelPanel à hotelFrame
        hotelFrame.add(hotelPanel);

        // Définir la taille, la visibilité et l'emplacement de hotelFrame
        hotelFrame.pack();
        hotelFrame.setLocationRelativeTo(null);
        hotelFrame.setVisible(true);
    }

    private void insertRatingsIntoTable(String hotelName, int security, int food, int cleanliness,
                                        int room, int location, int service) {
        try {
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Se connecter à la base de données
            String url = "jdbc:mysql://localhost:3306/projet_java";
            String username = "root";
            String password = "ramzi";

            Connection connection = DriverManager.getConnection(url, username, password);

            // Insérer les notes dans la table "notation" avec gestion de conflit
            String insertQuery = "INSERT INTO notation (hotelid, securite, nourriture, proprete, chambre, emplacement, service, timestamp) " +
                    "VALUES ((SELECT hotelid FROM hotel WHERE nomhotel = ?), ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "securite = VALUES(securite), nourriture = VALUES(nourriture), proprete = VALUES(proprete), " +
                    "chambre = VALUES(chambre), emplacement = VALUES(emplacement), service = VALUES(service), " +
                    "timestamp = CURRENT_TIMESTAMP";

            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, hotelName);
            insertStatement.setInt(2, security);
            insertStatement.setInt(3, food);
            insertStatement.setInt(4, cleanliness);
            insertStatement.setInt(5, room);
            insertStatement.setInt(6, location);
            insertStatement.setInt(7, service);

            insertStatement.executeUpdate();

            // Fermer les ressources
            insertStatement.close();
            connection.close();

            System.out.println("Notes insérées dans la table 'notation' pour " + hotelName);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }



}
