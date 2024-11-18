package cook;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 現在の日付を設定
        String todayDate = "2024/11/18"; // yyyy/MM/dd 形式

        // 食材リストを作成
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Tomato", "2024/11/20", 2.5));
        ingredientList.add(new Ingredient("Chicken Breast", "2024/11/22", 1.0));
        ingredientList.add(new Ingredient("Rice", "2025/06/01", 5.0));
        ingredientList.add(new Ingredient("Onion", "2024/11/19", 1.5));

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
        MenuMatcher menuMatcher = new MenuMatcher(ingredientDatabase);
        Menu optimalMenu = menuMatcher.findOptimalMenuBFS(menus);

        // 結果を表示
        if (optimalMenu != null) {
            System.out.println("\n最適なメニューは [" + optimalMenu.getName() + "] です！");
        } else {
            System.out.println("\n適切なメニューが見つかりませんでした。");
        }
    }
}
