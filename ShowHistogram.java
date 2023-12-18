import javax.swing.*;

public class ShowHistogram {
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
