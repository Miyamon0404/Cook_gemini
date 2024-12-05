package cook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class Ingredient{
    private String name;         	// 食材の名前
    private String expiryDate;   	// 賞味期限
    private double quantity;     	// 食材の量 
    private double protein;      	// タンパク質の量
    private double lipids;		 	// 脂質の量
    private double carbohydrates;	// 炭水化物の量

    // コンストラクタ
    public Ingredient(String name, String expiryDate, double quantity,double protein,double lipids,double carbohydrates) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.protein = protein;
        this.lipids = lipids;
        this.carbohydrates = carbohydrates;
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

    public double getProtein() {
    	return protein;
    }
    
    public void setProtein(double protein) {
    	this.protein = protein;
    }

    public double getLipids() {
    	return lipids;
    }
    
    public void setLipids(double lipids) {
    	this.lipids = lipids;
    }
    public double getCarbohydrates() {
    	return carbohydrates;
    }
    
    public void setCarbohydrates(double carbohydrates) {
    	this.carbohydrates = carbohydrates;
    }

    
    // 情報をわかりやすく表示するtoStringメソッド
    @Override
    public String toString() {
        return String.format("名前: %s, 有効期限: %s, 残量: %.2f g, タンパク質: %.2f g, 脂質: %.2f g, 炭水化物: %.2f g",
                name, expiryDate, quantity, protein, lipids, carbohydrates);
    }

    
    // 賞味期限からコスト
    public int calculateCost(String strDate1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate today = LocalDate.parse(strDate1, formatter);
        LocalDate expiry = LocalDate.parse(this.expiryDate, formatter);
        return (int) ChronoUnit.DAYS.between(today, expiry) < 0 ?  Integer.MAX_VALUE: (int) ChronoUnit.DAYS.between(today, expiry) ;
    }
}
