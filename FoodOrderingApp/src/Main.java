import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Food> foods = FoodMenuLoader.loadFoodFromCSV("data/menu.csv");
            new FoodOrderingCSV(foods).setVisible(true);
        });
    }
}
