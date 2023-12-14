import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelRatingsHistogram extends JFrame {

    public HotelRatingsHistogram(String title) {
        super(title);

        // Connect to the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://your_database_url", "your_username", "your_password")) {
            // Query to get ratings from the database
            String query = "SELECT securite, nourriture, proprete, chambre, emplacement, service FROM notation";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Create dataset
                    CategoryDataset dataset = createDataset(resultSet);

                    // Create chart based on the dataset
                    JFreeChart chart = ChartFactory.createBarChart(
                            "Hotel Ratings",
                            "Services",
                            "Rating",
                            dataset
                    );

                    // Create and set up the chart panel
                    ChartPanel chartPanel = new ChartPanel(chart);
                    chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
                    setContentPane(chartPanel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CategoryDataset createDataset(ResultSet resultSet) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        while (resultSet.next()) {
            // Get ratings for each service
            int securite = resultSet.getInt("securite");
            int nourriture = resultSet.getInt("nourriture");
            int proprete = resultSet.getInt("proprete");
            int chambre = resultSet.getInt("chambre");
            int emplacement = resultSet.getInt("emplacement");
            int service = resultSet.getInt("service");

            // Add ratings to the dataset
            dataset.addValue(securite, "Security", "Hotel");
            dataset.addValue(nourriture, "Food", "Hotel");
            dataset.addValue(proprete, "Cleanliness", "Hotel");
            dataset.addValue(chambre, "Room", "Hotel");
            dataset.addValue(emplacement, "Location", "Hotel");
            dataset.addValue(service, "Service", "Hotel");
        }

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelRatingsHistogram example = new HotelRatingsHistogram("Hotel Ratings Histogram");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}

