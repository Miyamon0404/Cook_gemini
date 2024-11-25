package cook;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 現在の日付を設定
        String todayDate = "2024/11/18"; // yyyy/MM/dd 形式

        // 食材リストを作成
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Tomato", "2024/11/20", 150, 1.4, 0.3, 6.0));
        ingredientList.add(new Ingredient("Chicken Breast", "2024/11/22", 150, 31.0, 3.6, 0.0));
        ingredientList.add(new Ingredient("Rice", "2025/06/01", 500, 7.1, 0.6, 77.0));
        ingredientList.add(new Ingredient("Onion", "2024/11/19", 150, 1.1, 0.1, 9.3));


        // 食材データベースを作成
        IngredientDatabase ingredientDatabase = new IngredientDatabase(ingredientList);

        // メニューリストを作成
        Map<String, Double> menu1Ingredients = new HashMap<>();
        menu1Ingredients.put("Tomato", 2.0);
        menu1Ingredients.put("Onion", 1.0);
        menu1Ingredients.put("Rice", 1.0);

        Map<String, Double> menu2Ingredients = new HashMap<>();
        menu2Ingredients.put("Chicken Breast", 1.0);
        menu2Ingredients.put("Rice", 2.0);

        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("Tomato Rice", menu1Ingredients));
        menus.add(new Menu("Chicken Rice", menu2Ingredients));

        // MenuMatcherを使用して最適なメニューを探索
        // 炭水化物→300,脂質→30%(2000kcal * 0.3 / 9(1g=9kcal)),炭水化物→2000 * 0.5 /4
        MenuMatcher menuMatcher = new MenuMatcher(ingredientDatabase,70,65,300);
        Menu optimalMenu = menuMatcher.findOptimalMenuBFS(menus);//最適なメニューの格納
       
    }
}
