
import javax.swing.*;

public class ShowHotels {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelViewer();
        });
    }
}
