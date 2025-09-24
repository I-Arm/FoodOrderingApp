import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;


public class FoodOrderingCSV extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0.0;

    private List<Food> foodList;
    private JButton deleteBtn; // <-- ย้ายมาประกาศเป็น field

    public FoodOrderingCSV(List<Food> foodList) {
        this.foodList = foodList;
        setTitle("ระบบสั่งอาหาร");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 14));

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 16);

        // ส่วนเมนู 
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 3, 10, 10));

        for (Food food : foodList) {
            ImageIcon icon = new ImageIcon(food.getImagePath());
            Image img = icon.getImage().getScaledInstance(120, 90, Image.SCALE_SMOOTH);

            JButton btn = new JButton("<html>" + food.getName() + "</html>", new ImageIcon(img));
            btn.setFont(thaiFont);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);

            btn.addActionListener(e -> showFoodOption(food));
            menuPanel.add(btn);
        }


        // ส่วนตะกร้า 
        String[] columns = {"Category", "Menu", "Type", "Price"}; 
        tableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(tableModel);
        orderTable.setFont(thaiFont);
        orderTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(orderTable);

        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("รวมทั้งหมด: 0.0 บาท");
        totalLabel.setFont(thaiFont);
        orderPanel.add(totalLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("บันทึก");
        JButton payBtn = new JButton("ชำระเงิน");
        JButton adminBtn = new JButton("จัดการระบบ");
        JButton deleteBtn = new JButton("ลบรายการ");

        saveBtn.setFont(thaiFont);
        payBtn.setFont(thaiFont);
        adminBtn.setFont(thaiFont);
        deleteBtn.setFont(thaiFont);
        
        deleteBtn.setVisible(false);

        adminBtn.addActionListener(e -> openAdminPanel());
        deleteBtn.addActionListener(e -> removeSelectedItem());
        saveBtn.addActionListener(e -> {
            saveToCSV(); 
            showReceiptPopup(getName());});
        payBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "ยอดที่ต้องชำระ: " + total + " บาท"));

        buttonPanel.add(saveBtn);
        buttonPanel.add(payBtn);
        buttonPanel.add(deleteBtn);

        orderPanel.add(buttonPanel, BorderLayout.NORTH);

        add(new JScrollPane(menuPanel), BorderLayout.CENTER);
        add(orderPanel, BorderLayout.EAST);

         orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteBtn.setVisible(orderTable.getSelectedRow() != -1);
            }
        });

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(thaiFont);

        loginBtn.addActionListener(e -> {
            if (loginBtn.getText().equals("Login")) {
                JTextField userField = new JTextField();
                JPasswordField passField = new JPasswordField();
                Object[] fields = {
                    "Username:", userField,
                    "Password:", passField
                };

                int option = JOptionPane.showConfirmDialog(this, fields, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String user = userField.getText();
                    String pass = new String(passField.getPassword());
                    if (user.equals("admin") && pass.equals("1234")) {
                        JOptionPane.showMessageDialog(this, "เข้าสู่ระบบ Admin สำเร็จ!");
                        loginBtn.setText("Admin: Logout");
                        openAdminPanel(); // เปิดหน้า Admin
                        buttonPanel.add(adminBtn); // ปุ่มจัดการ
                        adminBtn.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Username หรือ Password ไม่ถูกต้อง");
                    }
                }
            } else {
                loginBtn.setText("Login");
                JOptionPane.showMessageDialog(this, "ออกจากระบบแล้ว");
                adminBtn.setVisible(false);
            }
        });



        // เอาไปวางในมุมขวาบน
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(loginBtn);
        add(topPanel, BorderLayout.NORTH);
    }

    private void showFoodOption(Food food) {
        if (food.getCategory().equals("เครื่องดื่ม")) {
            Object[] options = {
                "เล็ก (" + food.getNormalPrice() + " บาท)",
                "ใหญ่ (" + food.getSpecialPrice() + " บาท)"
            };

            int choice = JOptionPane.showOptionDialog(
                this,
                "เลือกขนาดสำหรับ " + food.getName(),
                "เลือกราคา",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
        
            if (choice == JOptionPane.YES_OPTION) {
                addToOrder(food, "เล็ก", food.getNormalPrice());
            } else if (choice == JOptionPane.NO_OPTION) {
                addToOrder(food, "ใหญ่", food.getSpecialPrice());
            }
        } else {  

            Object[] options = {
                    "ปกติ (" + food.getNormalPrice() + " บาท)",
                    "พิเศษ (" + food.getSpecialPrice() + " บาท)"
            };
    
            int choice = JOptionPane.showOptionDialog(
                this,
                "เลือกขนาดสำหรับ " + food.getName(),
                "เลือกราคา",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
        
            if (choice == JOptionPane.YES_OPTION) {
                addToOrder(food, "ปกติ", food.getNormalPrice());
            } else if (choice == JOptionPane.NO_OPTION) {
                addToOrder(food, "พิเศษ", food.getSpecialPrice());
            }
        };
    }

    private void openAdminPanel() {
        JFrame adminFrame = new JFrame("จัดการเมนู (Admin)");
        adminFrame.setSize(600, 400);
        adminFrame.setLocationRelativeTo(this);
        Font thaiFont = new Font("Tahoma", Font.PLAIN, 16);
        
        String[] columns = {"ชื่อเมนู", "ราคา ปกติ", "ราคา พิเศษ", "รูปภาพ", "ประเภท"};
        DefaultTableModel adminModel = new DefaultTableModel(columns, 0);
        
        // โหลดข้อมูลจาก foodList
        for (Food f : foodList) {
            adminModel.addRow(new Object[]{
                f.getName(), f.getNormalPrice(), f.getSpecialPrice(),
                f.getImagePath(), f.getCategory()
            });
        }

        JTable adminTable = new JTable(adminModel);
        JScrollPane scrollPane = new JScrollPane(adminTable);

        JButton addBtn = new JButton("เพิ่มเมนู");
        JButton editBtn = new JButton("แก้ไขเมนู");
        JButton deleteBtn = new JButton("ลบเมนู");

        addBtn.addActionListener(e -> {
            addOrEditMenu(adminModel, null);
        });

        editBtn.addActionListener(e -> {
            int row = adminTable.getSelectedRow();
            if (row != -1) {
                addOrEditMenu(adminModel, row);
            } else {
                JOptionPane.showMessageDialog(adminFrame, "กรุณาเลือกเมนูที่จะแก้ไข");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = adminTable.getSelectedRow();
            if (row != -1) {
                adminModel.removeRow(row);
                JOptionPane.showMessageDialog(adminFrame, "ลบเมนูเรียบร้อย");
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        adminFrame.add(scrollPane, BorderLayout.CENTER);
        adminFrame.add(btnPanel, BorderLayout.SOUTH);

        adminFrame.setVisible(true);

        
    }

    private void addOrEditMenu(DefaultTableModel model, Integer rowIndex) {
        JTextField nameField = new JTextField();
        JTextField normalField = new JTextField();
        JTextField specialField = new JTextField();
        JTextField imgField = new JTextField();
        JTextField categoryField = new JTextField();

        if (rowIndex != null) {
            nameField.setText(model.getValueAt(rowIndex, 0).toString());
            normalField.setText(model.getValueAt(rowIndex, 1).toString());
            specialField.setText(model.getValueAt(rowIndex, 2).toString());
            imgField.setText(model.getValueAt(rowIndex, 3).toString());
            categoryField.setText(model.getValueAt(rowIndex, 4).toString());
        }

        Object[] fields = {
            "ชื่อเมนู:", nameField,
            "ราคา ปกติ:", normalField,
            "ราคา พิเศษ:", specialField,
            "รูปภาพ:", imgField,
            "ประเภท:", categoryField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, 
            (rowIndex == null ? "เพิ่มเมนู" : "แก้ไขเมนู"), JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double normal = Double.parseDouble(normalField.getText());
            double special = Double.parseDouble(specialField.getText());
            String img = imgField.getText();
            String category = categoryField.getText();

            if (rowIndex == null) {
                model.addRow(new Object[]{name, normal, special, img, category});
            } else {
                model.setValueAt(name, rowIndex, 0);
                model.setValueAt(normal, rowIndex, 1);
                model.setValueAt(special, rowIndex, 2);
                model.setValueAt(img, rowIndex, 3);
                model.setValueAt(category, rowIndex, 4);
            }

            JOptionPane.showMessageDialog(this, "บันทึกเมนูเรียบร้อย");
        }
    }

    private void removeSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            String category = tableModel.getValueAt(selectedRow, 0).toString(); // คอลัมน์ Category
            String menu = tableModel.getValueAt(selectedRow, 1).toString();     // คอลัมน์ Menu

            int confirm = JOptionPane.showOptionDialog(
                    this,
                    "คุณต้องการลบเมนู '" + menu + "' (" + category + ") ออกจากรายการหรือไม่?",
                    "ยืนยันการลบ",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"ลบ", "ยกเลิก"}, // ปุ่ม
                    "ยกเลิก"
            );

            if (confirm == JOptionPane.YES_OPTION) {
                double price = (double) tableModel.getValueAt(selectedRow, 3); // คอลัมน์ Price
                total -= price;
                tableModel.removeRow(selectedRow);
                totalLabel.setText("รวมทั้งหมด: " + total + " บาท");
            }
            } else {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกรายการที่จะลบ");
            }
    }


    public void addToOrder(Food food, String type, double price) {
        tableModel.addRow(new Object[]{food.getCategory(), food.getName(), type, price});
        total += price;
        totalLabel.setText("รวมทั้งหมด: " + total + " บาท");
    }

    private void showReceiptPopup(String fileName) {
        JDialog receiptDialog = new JDialog(this, "บิลใบเสร็จ", true);
        receiptDialog.setSize(400, 500);
        receiptDialog.setLocationRelativeTo(this);

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 16);

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(thaiFont);

        //หัวบิล
        billArea.append("*********** ใบเสร็จ ***********\n");
        billArea.append("วันที่: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\n");
        billArea.append("---------------------------------\n");

        //รายการจากตาราง
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String category = tableModel.getValueAt(i, 0).toString();
            String menu = tableModel.getValueAt(i, 1).toString();
            String type = tableModel.getValueAt(i, 2).toString();
            String price = tableModel.getValueAt(i, 3).toString();

            billArea.append(category + " - " + menu + " (" + type + ") " + price + " บาท\n");
        }

        billArea.append("---------------------------------\n");
        billArea.append("รวมทั้งหมด: " + total + " บาท\n");
        billArea.append("บันทึกไฟล์: " + fileName + "\n");
        billArea.append("********* ขอบคุณที่ใช้บริการ *********\n");

        JScrollPane scrollPane = new JScrollPane(billArea);
        receiptDialog.add(scrollPane, BorderLayout.CENTER);

        //ปุ่มปิด
        JButton closeBtn = new JButton("ปิด");
        closeBtn.addActionListener(e -> receiptDialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        receiptDialog.add(btnPanel, BorderLayout.SOUTH);

        receiptDialog.setVisible(true);
    }


    private void saveToCSV() {
    try {
        //ตั้งชื่อไฟล์โดยใช้วันที่และเวลา
        String fileName = "D:/Work/Code/mini-project/FoodOrderingApp/data/bills.csv";
        String Num = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new java.util.Date());

        
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), "UTF-8"));

        // เขียนหัวตาราง
        writer.println("---------------ใบเสร็จ---------------");
        writer.println(Num);
        writer.println("Category,Menu,Type,Price");

        // เขียนข้อมูลแต่ละแถวจากตาราง orderTable
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String category = tableModel.getValueAt(i, 0).toString();
            String menu = tableModel.getValueAt(i, 1).toString();
            String type = tableModel.getValueAt(i, 2).toString();
            String price = tableModel.getValueAt(i, 3).toString();

            writer.println(category + "," + menu + "," + type + "," + price);
        }

        // เขียนรวมยอดท้ายไฟล์
        writer.println(",,รวมทั้งหมด," + total);
        writer.println("*********************************************");
        writer.close();

        JOptionPane.showMessageDialog(this, "บันทึกออเดอร์เรียบร้อย! (" + fileName + ")");
        
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดขณะบันทึกไฟล์");
        }
}

}
