import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StarRatingPanel extends JPanel {
    private int rating;

    public StarRatingPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        for (int i = 1; i <= 5; i++) {
            JLabel starLabel = new JLabel("\u2605");
            starLabel.setFont(new Font("Serif", Font.PLAIN, 30));
            starLabel.addMouseListener(new StarClickListener(i));
            add(starLabel);
        }
        updateStarColors();
    }

    public int getRating() {
        return rating;
    }

    private class StarClickListener extends MouseAdapter {
        private int starNumber;

        public StarClickListener(int starNumber) {
            this.starNumber = starNumber;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            rating = starNumber;
            updateStarColors();
        }
    }

    private void updateStarColors() {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JLabel) {
                JLabel starLabel = (JLabel) components[i];
                if (i < rating) {
                    starLabel.setForeground(Color.orange);
                } else {
                    starLabel.setForeground(Color.lightGray);
                }
            }
        }
    }
}
