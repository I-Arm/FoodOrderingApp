import java.awt.Font;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.swing.UIManager;

public class FoodMenuLoader {
    // ... (loadFoodFromCSV และ saveFoodToCSV เหมือนเดิม) ...
    public static List<Food> loadFoodFromCSV(String filename) {
        List<Food> foodList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
                    
            String line;
            br.readLine(); // ข้าม header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // ต้องมี 5 คอลัมน์
                    String name = parts[0];
                    // ... (การโหลดข้อมูลที่เหลือเหมือนเดิม) ...
                    double normal = Double.parseDouble(parts[1]);
                    double special = Double.parseDouble(parts[2]);
                    String img = parts[3];
                    String category = parts[4];
                    
                    foodList.add(new Food(name, normal, special, img, category));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodList;
    }

    // 💾 บันทึกกลับไปยัง CSV
    public static void saveFoodToCSV(String filename, List<Food> foodList) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {

            // เขียน header
            bw.write("Name,NormalPrice,SpecialPrice,Image,Category");
            bw.newLine();

            // เขียนข้อมูล
            for (Food f : foodList) {
                bw.write(f.getName() + "," +
                         f.getNormalPrice() + "," +
                         f.getSpecialPrice() + "," + // ใช้ getSpecialPrice
                         f.getImagePath() + "," +
                         f.getCategory());
                bw.newLine();
            }

            System.out.println("✅ บันทึกข้อมูลกลับไปที่ไฟล์เรียบร้อยแล้ว");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ➕ เพิ่มอาหารใหม่
    public static void addFood(List<Food> foodList, String name, double normal, double special, String img, String category) {
        foodList.add(new Food(name, normal, special, img, category));
        System.out.println("✅ เพิ่มเมนูใหม่เรียบร้อย: " + name);
    }

    // ✏️ แก้ไขอาหารตามชื่อ
    public static void editFood(List<Food> foodList, String oldName, String newName, Double newNormal, Double newSpecial, String newImg, String newCategory) {
        for (Food f : foodList) {
            if (f.getName().equalsIgnoreCase(oldName)) {
                if (newName != null && !newName.isEmpty()) f.setName(newName);
                if (newNormal != null) f.setNormalPrice(newNormal);
                if (newSpecial != null) f.setSpecialPrice(newSpecial); // แก้ไขเป็น setSpecialPrice
                if (newImg != null && !newImg.isEmpty()) f.setImagePath(newImg); // แก้ไขเป็น setImagePath
                if (newCategory != null && !newCategory.isEmpty()) f.setCategory(newCategory);
                System.out.println("✏️ แก้ไขเมนูเรียบร้อย: " + oldName + " → " + f.getName());
                return;
            }
        }
        System.out.println("❌ ไม่พบเมนูชื่อ: " + oldName);
    }

    // ❌ ลบอาหารตามชื่อ
    public static void deleteFood(List<Food> foodList, String name) {
        Iterator<Food> iterator = foodList.iterator();
        while (iterator.hasNext()) {
            Food f = iterator.next();
            if (f.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                System.out.println("🗑️ ลบเมนูเรียบร้อย: " + name);
                return;
            }
        }
        System.out.println("❌ ไม่พบเมนูชื่อ: " + name);
    }
    
    // *************** เมธอดใหม่สำหรับใช้ใน Admin Panel ***************
    /**
     * อัพเดท foodList จากข้อมูลใน DefaultTableModel
     * @param model DefaultTableModel จากหน้า Admin
     * @param foodList foodList ที่จะถูกอัพเดท
     */
    public static void updateFoodListFromAdminModel(javax.swing.table.DefaultTableModel model, List<Food> foodList) {
        foodList.clear(); // ล้างรายการเก่า

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 0).toString();
            double normal = Double.parseDouble(model.getValueAt(i, 1).toString());
            double special = Double.parseDouble(model.getValueAt(i, 2).toString());
            String img = model.getValueAt(i, 3).toString();
            String category = model.getValueAt(i, 4).toString();

            foodList.add(new Food(name, normal, special, img, category));
        }

        // บันทึกกลับไปยัง CSV ทันที
        saveFoodToCSV("data/menu.csv", foodList);
    }
    // ***************************************************************
    
    // ... (main method เหมือนเดิม) ...
    public static void main(String[] args) {
        String filename = "food.csv";
        List<Food> foods = loadFoodFromCSV(filename);

        // ทดสอบเพิ่ม / ลบ / แก้ไข
        addFood(foods, "ข้าวผัดกุ้ง", 45, 55, "img/friedrice.png", "อาหารจานเดียว");
        editFood(foods, "ข้าวผัดกุ้ง", "ข้าวผัดทะเล", 50.0, 60.0, null, "อาหารทะเล");
        deleteFood(foods, "ต้มยำกุ้ง");

        saveFoodToCSV(filename, foods);
    }
}