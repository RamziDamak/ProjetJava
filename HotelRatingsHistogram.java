import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelRatingsHistogram extends JFrame {

    private JComboBox<String> hotelComboBox;
    private Connection connection;

    public HotelRatingsHistogram(String title) {
        super(title);

        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projet_java", "root", "ramzi");

            // Query to get hotels from the database
            String hotelQuery = "SELECT hotelid, nomhotel FROM hotel";

            try (PreparedStatement hotelStatement = connection.prepareStatement(hotelQuery)) {
                try (ResultSet hotelResultSet = hotelStatement.executeQuery()) {
                    // Create a combo box with hotel names
                    hotelComboBox = new JComboBox<>();
                    while (hotelResultSet.next()) {
                        int hotelId = hotelResultSet.getInt("hotelid");
                        String hotelName = hotelResultSet.getString("nomhotel");
                        hotelComboBox.addItem(hotelName);

                        // Add action listener to handle hotel selection
                        hotelComboBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // When a hotel is selected, update the chart panel
                                updateChartPanel();
                            }
                        });
                    }
                }
            }

            // Create and set up the chart panel
            ChartPanel chartPanel = createChartPanel();
            chartPanel.setPreferredSize(new Dimension(560, 370));

            // Create a panel for the combo box and chart
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(hotelComboBox, BorderLayout.NORTH);
            panel.add(chartPanel, BorderLayout.CENTER);

            setContentPane(panel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ChartPanel createChartPanel() {
        // Create dataset
        CategoryDataset dataset = createDataset();

        // Create chart based on the dataset
        JFreeChart chart = ChartFactory.createBarChart(
                "Hotel Ratings",
                "Services",
                "Rating",
                dataset
        );

        return new ChartPanel(chart);
    }

    private CategoryDataset createDataset() {
        int selectedHotelIndex = hotelComboBox.getSelectedIndex() + 1;
        // Query to get ratings from the database
        String query = "SELECT securite, nourriture, proprete, chambre, emplacement, service FROM notation WHERE hotelid = ?";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedHotelIndex);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private void updateChartPanel() {
        // Update the chart panel when the hotel selection changes
        ChartPanel chartPanel = createChartPanel();
        chartPanel.setPreferredSize(new Dimension(560, 370));

        // Get the existing content pane and replace it with the updated chart panel
        getContentPane().removeAll();
        getContentPane().add(hotelComboBox, BorderLayout.NORTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);

        // Repaint the frame
        revalidate();
        repaint();
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
