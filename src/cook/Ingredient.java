package cook;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class Ingredient{
    private String name;         // 食材の名前
    private String expiryDate;   // 賞味期限
    private double quantity;     // 食材の量 

    // コンストラクタ
    public Ingredient(String name, String expiryDate, double quantity) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // GetterとSetter（必要に応じて）
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }


    // 情報をわかりやすく表示するtoStringメソッド
    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", quantity=" + quantity + 
                '}';
    }
    
    // 賞味期限からコスト
    public int calculateCost(String strDate1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate today = LocalDate.parse(strDate1, formatter);
        LocalDate expiry = LocalDate.parse(this.expiryDate, formatter);
        return (int) ChronoUnit.DAYS.between(today, expiry);
    }
//
//    public static void main(String[] args) {
//        // 食材リストを作成
//        ArrayList<Ingredient> ingredients = new ArrayList<>();
//
//        // 食材を追加
//        ingredients.add(new Ingredient("Tomato", "2024-11-20", 2.5));
//        ingredients.add(new Ingredient("Chicken Breast", "2024-11-22", 1.0));
//        ingredients.add(new Ingredient("Rice", "2025-06-01", 5.0));
//
//        // リストを表示
//        for (Ingredient ingredient : ingredients) {
//            System.out.println(ingredient);
//        }
//    }
}
