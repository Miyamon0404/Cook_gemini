package cook;

import java.util.*;
public class CookUI {
	
	IngredientDatabase ingredientDatabase;
	MenuMatcher menumatcher;
//	RecipeDatabase racipedatabase;
	List<Menu> menus;//Recipedatabaseの代用。メニューのリストを参照。
	
    public static void main(String[] args) {


        // 食材リストを作成
        List<Ingredient> ingredientList = new ArrayList<>();
//        ingredientList.add(new Ingredient("Tomato", "2024/12/15", 150, 1.4, 0.3, 6.0));
//        ingredientList.add(new Ingredient("Chicken Breast", "2024/12/30", 150, 31.0, 3.6, 0.0));
//        ingredientList.add(new Ingredient("Rice", "2025/06/01", 500, 7.1, 0.6, 77.0));
//        ingredientList.add(new Ingredient("Onion", "2025/01/01", 150, 1.1, 0.1, 9.3));

        // 食材データベースを作成
        IngredientDatabase ingredientDatabase = new IngredientDatabase(ingredientList);
//        ingredientDatabase.printDatabase();
        
        // メニューリストを作成
        Map<String, Double> menu1Ingredients = new HashMap<>();
        menu1Ingredients.put("Tomato", 100.0);
//        menu1Ingredients.put("Onion", 100.0);
//        menu1Ingredients.put("Rice", 100.0);

        Map<String, Double> menu2Ingredients = new HashMap<>();
        menu2Ingredients.put("Chicken Breast", 300.0);
//        menu2Ingredients.put("Rice", 100.0);
//        menu2Ingredients.put("Onion", 200.0);

        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("Tomato Rice", menu1Ingredients));
        menus.add(new Menu("Chicken Rice", menu2Ingredients));
        
        //UIの起動
        CookUI cookUI = new CookUI();
        cookUI.setIngredientDatabase(ingredientDatabase);
        cookUI.setMenus(menus);
        cookUI.StartMenu();

        // MenuMatcherを使用して最適なメニューを探索
//        MenuMatcher menuMatcher = new MenuMatcher(ingredientDatabase, 70, 65, 300);
//        Menu optimalMenu = menuMatcher.findOptimalMenuBFS(menus);
//        
        // 最後にデータベースを表示(確認用)
        ingredientDatabase.printDatabase();
    }
    
	public CookUI() {
	}
	
	//材料データベースのセッタ
	public void setIngredientDatabase(IngredientDatabase ingredientDatabase) {
		this.ingredientDatabase = ingredientDatabase;
	}
	
	//メニューデータベースのセッタ
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
  	
  	//StartMenuの表示
  	public void StartMenu() {
  		boolean endflag = false;
  		while(endflag == false) {
  			System.out.println("*****StartMenu:*****");
  			displayIngredient();
  			System.out.println("---操作一覧---");
  			System.out.println("1.材料を追加する");			
  			System.out.println("2.最適なメニューを探索する");			
  			System.out.println("3.プログラムの終了");
  			System.out.println("-------------");
  			System.out.println("実行したい操作の番号を入力してください。");
  			Scanner scan = new Scanner(System.in);
  			int x = scan.nextInt();
  			switch(x) {
  			case 1: inputIngreadient(); break;
  			case 2: searchOptimalMenu(); break;
  			case 3: endflag = true; System.out.println("プログラムを終了します。"); break;
  			}
  		}
  	}
  	
  	//現存する素材を全て表示
  	public void displayIngredient() {
  		ingredientDatabase.printDatabase();
  	}
  	
  	//最適なメニューを探索する
  	public void searchOptimalMenu() {
  		System.out.println("目標とする栄養素を入力してください");
  		Scanner scan = new Scanner(System.in);
  		System.out.println("タンパク質の量(g):");
  		double protein = scan.nextDouble();      	// タンパク質の量
  		System.out.println("脂質の量(g):");
  		double lipids = scan.nextDouble();		 	// 脂質の量
  		System.out.println("炭水化物の量(g):");
  	    double carbohydrates= scan.nextDouble();	// 炭水化物の量
  	    System.out.println("探索を開始します。");
  		this.menumatcher = new MenuMatcher(ingredientDatabase, protein, lipids, carbohydrates);
  		menumatcher.findOptimalMenuBFS(menus);
  	}
  	
  	//素材の内容を入力
  	public void inputIngreadient() {
  		Scanner scan = new Scanner(System.in);
  		System.out.println("材料の情報を入力してください");
  		System.out.println("名前(xxxx):");
  		String name = scan.nextLine();
  		System.out.println("賞味期限(YYYY/MM/DD):");
  		String expiryDate = scan.next();   	// 賞味期限
  		System.out.println("食材の量(g):");
  		double quantity = scan.nextDouble();     	// 食材の量
  		System.out.println("タンパク質の量(g):");
  		double protein = scan.nextDouble();      	// タンパク質の量
  		System.out.println("脂質の量(g):");
  		double lipids = scan.nextDouble();		 	// 脂質の量
  		System.out.println("炭水化物の量(g):");
  	    double carbohydrates= scan.nextDouble();	// 炭水化物の量
  	    var ingredient = new Ingredient(name, expiryDate, quantity, protein, lipids, carbohydrates);
  	    System.out.println(ingredient + "を材料リストに追加しました");
  		ingredientDatabase.addIngredient(ingredient);
  	}
}
