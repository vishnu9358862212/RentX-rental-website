import javax.swing.*;
import java.awt.*;

public class PropertyStatusLabel extends JLabel {

    public PropertyStatusLabel(String status) {
        super(status);
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        setStatus(status);
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        setText(status);
        switch (status.toLowerCase()) {
            case "free":
                setForeground(Color.GREEN);
                break;
            case "waiting list":
                setForeground(Color.ORANGE);
                break;
            case "booked":
                setForeground(Color.RED);
                break;
            default:
                setForeground(Color.GRAY);
                setText("Unknown");
        }
    }
}