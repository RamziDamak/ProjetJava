import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
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
    private JTextArea commentsTextArea;
    private Connection connection;

    public HotelRatingsHistogram(String title) {
        super(title);

        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projet_java", "root", "ramzi");

            // Create a combo box with hotel names
            hotelComboBox = new JComboBox<>();
            fetchHotelData();

            // Create JTextArea for comments
            commentsTextArea = new JTextArea();
            commentsTextArea.setEditable(false);
            commentsTextArea.setFont(new Font("Arial", Font.PLAIN, 30)); // Set font size

            // Create a label for "Customer Comment"
            JLabel commentLabel = new JLabel("Commentaire client : ");
            commentLabel.setFont(new Font("Arial", Font.PLAIN, 30));

            // Create a panel for the combo box, comments, and chart
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(hotelComboBox, BorderLayout.NORTH);

            // Create a panel for comments label and text area
            JPanel commentsPanel = new JPanel(new BorderLayout());
            commentsPanel.add(commentLabel, BorderLayout.WEST);
            commentsPanel.add(new JScrollPane(commentsTextArea), BorderLayout.CENTER);

            // Add commentsPanel to the main panel
            panel.add(commentsPanel, BorderLayout.SOUTH);

            // Create and set up the chart panel
            ChartPanel chartPanel = createChartPanel();
            chartPanel.setPreferredSize(new Dimension(560, 370));

            // Add chart panel to the main panel
            panel.add(chartPanel, BorderLayout.CENTER);

            // Add action listener to handle hotel selection
            hotelComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // When a hotel is selected, update the chart panel and comments
                    updateChartPanel();
                    updateCommentsTextArea();
                }
            });

            setContentPane(panel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchHotelData() {
        // Query to get hotels from the database
        String hotelQuery = "SELECT nomhotel FROM hotel";

        try (PreparedStatement hotelStatement = connection.prepareStatement(hotelQuery)) {
            try (ResultSet hotelResultSet = hotelStatement.executeQuery()) {
                while (hotelResultSet.next()) {
                    String hotelName = hotelResultSet.getString("nomhotel");
                    hotelComboBox.addItem(hotelName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ChartPanel createChartPanel() {
        // Create dataset
        CategoryDataset dataset = createDataset();

        // Create chart based on the dataset
        JFreeChart chart = ChartFactory.createBarChart(
                "Évaluation de l'hôtel",
                "Services",
                "Évaluation",
                dataset
        );

        // Get the plot and set the range for the domain axis (vertical axis)
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 5);  // Set the range to always show values up to 5

        return new ChartPanel(chart);
    }

    private CategoryDataset createDataset() {
        int selectedHotelIndex = hotelComboBox.getSelectedIndex() + 1;
        // Query to get ratings and comments from the database
        String query = "SELECT securite, nourriture, proprete, chambre, emplacement, service, commentaire FROM notation WHERE hotelid = ?";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedHotelIndex);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Get ratings for each service
                    int securite = resultSet.getInt("securite");
                    int nourriture = resultSet.getInt("nourriture");
                    int proprete = resultSet.getInt("proprete");
                    int chambre = resultSet.getInt("chambre");
                    int emplacement = resultSet.getInt("emplacement");
                    int service = resultSet.getInt("service");

                    // Add ratings to the dataset
                    dataset.addValue(securite, "Sécurité", "Hôtel");
                    dataset.addValue(nourriture, "Nourriture", "Hôtel");
                    dataset.addValue(proprete, "Propreté", "Hôtel");
                    dataset.addValue(chambre, "Chambre", "Hôtel");
                    dataset.addValue(emplacement, "Emplacement", "Hôtel");
                    dataset.addValue(service, "Service", "Hôtel");

                    String commentaire = resultSet.getString("commentaire");
                    commentsTextArea.setText(commentaire != null ? commentaire : "");
                } else {
                    // If no data is available, set default values to 0
                    dataset.addValue(0, "Sécurité", "Hôtel");
                    dataset.addValue(0, "Nourriture", "Hôtel");
                    dataset.addValue(0, "Propreté", "Hôtel");
                    dataset.addValue(0, "Chambre", "Hôtel");
                    dataset.addValue(0, "Emplacement", "Hôtel");
                    dataset.addValue(0, "Service", "Hôtel");

                    commentsTextArea.setText("");
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
        getContentPane().add(new JScrollPane(commentsTextArea), BorderLayout.SOUTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);

        // Repaint the frame
        revalidate();
        repaint();
    }

    private void updateCommentsTextArea() {
        // Update the comments text area when the hotel selection changes
        int selectedHotelIndex = hotelComboBox.getSelectedIndex() + 1;
        String query = "SELECT commentaire FROM notation WHERE hotelid = ?";

        StringBuilder commentsBuilder = new StringBuilder();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedHotelIndex);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Append comments in the StringBuilder
                    commentsBuilder.append(resultSet.getString("commentaire")).append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set comments in the JTextArea
        commentsTextArea.setText(commentsBuilder.toString().trim());
    }
}
