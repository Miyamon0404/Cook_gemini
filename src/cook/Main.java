package cook;

import java.util.*;
public class Main {
    public static void main(String[] args) {
        // 現在の日付を設定
        String todayDate = "2024/11/25"; // yyyy/MM/dd 形式

        // 食材リストを作成
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Tomato", "2024/12/15", 150, 1.4, 0.3, 6.0));
        ingredientList.add(new Ingredient("Chicken Breast", "2024/12/30", 150, 31.0, 3.6, 0.0));
        ingredientList.add(new Ingredient("Rice", "2025/06/01", 500, 7.1, 0.6, 77.0));
        ingredientList.add(new Ingredient("Onion", "2025/01/01", 150, 1.1, 0.1, 9.3));

        // 食材データベースを作成
        IngredientDatabase ingredientDatabase = new IngredientDatabase(ingredientList);
        ingredientDatabase.printDatabase();
        
        // メニューリストを作成
        Map<String, Double> menu1Ingredients = new HashMap<>();
        menu1Ingredients.put("Tomato", 100.0);
        menu1Ingredients.put("Onion", 100.0);
        menu1Ingredients.put("Rice", 100.0);

        Map<String, Double> menu2Ingredients = new HashMap<>();
        menu2Ingredients.put("Chicken Breast", 300.0);
        menu2Ingredients.put("Rice", 100.0);
        menu2Ingredients.put("Onion", 200.0);

        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("Tomato Rice", menu1Ingredients));
        menus.add(new Menu("Chicken Rice", menu2Ingredients));

        // MenuMatcherを使用して最適なメニューを探索
        MenuMatcher menuMatcher = new MenuMatcher(ingredientDatabase, 70, 65, 300);
        Menu optimalMenu = menuMatcher.findOptimalMenuBFS(menus);
        
        // 最後にデータベースを表示
        ingredientDatabase.printDatabase();
    }
}
