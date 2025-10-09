public class Food {
    String name;
    private double normalPrice;
    private double specialPrice;
    private String imagePath;
    private String category; 

    public Food(String name, double normalPrice, double specialPrice, String imagePath, String category) {
        this.name = name;
        this.normalPrice = normalPrice;
        this.specialPrice = specialPrice;
        this.imagePath = imagePath;
        this.category = category;
    }

    public String getName() { return name; }
    public double getNormalPrice() { return normalPrice; }
    public double getSpecialPrice() { return specialPrice; }
    public String getImagePath() { return imagePath; }
    public String getCategory() { return category; }

    // *************** ส่วนที่แก้ไข/เพิ่มเติม ***************

    public void setName(String newName) {
        this.name = newName;
    }

    public void setNormalPrice(Double newNormal) {
        this.normalPrice = newNormal;
    }

    // เมธอดที่เพิ่มเข้ามา
    public void setSpecialPrice(Double newSpecial) { 
        this.specialPrice = newSpecial; 
    }

    // เมธอดที่เพิ่มเข้ามา
    public void setImagePath(String newPath) { 
        this.imagePath = newPath; 
    }

    public void setCategory(String newCategory) {
        this.category = newCategory;
    }
    
    // *******************************************************
}