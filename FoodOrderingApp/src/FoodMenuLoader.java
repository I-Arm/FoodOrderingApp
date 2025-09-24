import java.awt.Font;
import java.io.*;
import java.util.*;

import javax.swing.UIManager;

public class FoodMenuLoader {
    public static List<Food> loadFoodFromCSV(String filename) {
        List<Food> foodList = new ArrayList<>();
        Font thaiFont = new Font("Tahoma", Font.PLAIN, 16);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
                    
            String line;
            br.readLine(); // ข้าม header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // ต้องมี 5 คอลัมน์
                    String name = parts[0];
                    double normal = Double.parseDouble(parts[1]);
                    double special = Double.parseDouble(parts[2]);
                    String img = parts[3];
                    String category = parts[4]; // อ่าน category
                    
                    foodList.add(new Food(name, normal, special, img, category));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodList;
    }
}
