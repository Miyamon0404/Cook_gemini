package cook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuMatcher {
    private IngredientDatabase ingredientDatabase;
    private double targetProtein;      // 必要なタンパク質
    private double targetLipids;       // 必要な脂質
    private double targetCarbohydrates; // 必要な炭水化物

    public MenuMatcher(IngredientDatabase ingredientDatabase, double targetProtein, double targetLipids, double targetCarbohydrates) {
        this.ingredientDatabase = ingredientDatabase;
        this.targetProtein = targetProtein;
        this.targetLipids = targetLipids;
        this.targetCarbohydrates = targetCarbohydrates;
    }

    // 最適なメニューを選択
    public Menu findOptimalMenuBFS(List<Menu> menus) {
        Menu optimalMenu = null;
        double maxScore = -Double.MAX_VALUE;

        //メニューの比較
        for (Menu menu : menus) {
            double score = calculateMenuScoreBFS(menu);
            System.out.println("メニュー [" + menu.getName() + "] のスコア: " + score);

            if (score > maxScore) {
                maxScore = score;
                optimalMenu = menu;
            }
        }
        consumeIngredients(optimalMenu);
        

        if (optimalMenu != null) {
            System.out.println("\n選ばれた最適なメニュー: " + optimalMenu.getName());
        } else {
            System.out.println("\n最適なメニューは見つかりませんでした。");
        }
        return optimalMenu;
    }
    
    // 材料を消費する
   public void consumeIngredients(Menu menu) {
        System.out.println("\n--- 材料を消費中 ---");
        for (Map.Entry<String, Double> entry : menu.getRequiredIngredients().entrySet()) {
            String ingredientName = entry.getKey();
            double quantityNeeded = entry.getValue();

            Ingredient ingredient = ingredientDatabase.findIngredient(ingredientName);
            if (ingredient != null) {
                if (ingredient.getQuantity() >= quantityNeeded) {
                    ingredient.setQuantity(ingredient.getQuantity() - quantityNeeded);
                    System.out.println("材料 [" + ingredientName + "] を " + quantityNeeded + " 消費しました。残り: " + ingredient.getQuantity());
                } else {
                    System.out.println("材料 [" + ingredientName + "] が不足しています。必要量: " + quantityNeeded + ", 在庫: " + ingredient.getQuantity());
                }
            } else {
                System.out.println("材料 [" + ingredientName + "] がデータベースに存在しません。");
            }
        }
    }
    

    // 幅優先探索でメニューのスコアを計算
    private double calculateMenuScoreBFS(Menu menu) {
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 初期ノードを追加
        Node startNode = new Node("Start", 0, 0, null);
        queue.add(startNode);

        double totalCost = 0.0;
        double totalProtein = 0.0;      // タンパク質の合計
        double totalLipids = 0.0;       // 脂質の合計
        double totalCarbohydrates = 0.0; // 炭水化物の合計

        // 幅優先探索のループ
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            String currentIngredient = currentNode.getIngredientName();
            visited.add(currentIngredient);

            // メニュー内の材料を探索
            for (Map.Entry<String, Double> entry : menu.getRequiredIngredients().entrySet()) {
                String ingredientName = entry.getKey();
                double quantityNeeded = entry.getValue();

                if (visited.contains(ingredientName)) {
                    continue;
                }

                Ingredient ingredient = ingredientDatabase.findIngredient(ingredientName);
                if (ingredient == null || ingredient.getQuantity() < quantityNeeded) {
                    return Double.MIN_VALUE;  // 材料が不足している場合はスコアを最低に設定
                }

                // 材料のコストと栄養素を加算
                double ingredientCost = ingredient.calculateCost(todayDate) * quantityNeeded;
                totalCost += ingredientCost;
                totalProtein += ingredient.getProtein();
                totalLipids += ingredient.getLipids();
                totalCarbohydrates += ingredient.getCarbohydrates();

                visited.add(ingredientName);

                // 目標状態チェック: 全ての材料が揃ったかどうか
                if (visited.containsAll(menu.getRequiredIngredients().keySet())) {
                    // 栄養価の適合スコアを計算
                    double nutritionScore = calculateNutritionScore(totalProtein, totalLipids, totalCarbohydrates);
                    
                    // 詳細な栄養情報を出力
                    System.out.println("\n--- メニュー [" + menu.getName() + "] の詳細 ---");
                    System.out.println("総コスト: " + totalCost);
                    System.out.println("栄養価:");
                    System.out.println("  - タンパク質: " + totalProtein + "g (目標: " + targetProtein + "g)");
                    System.out.println("  - 脂質: " + totalLipids + "g (目標: " + targetLipids + "g)");
                    System.out.println("  - 炭水化物: " + totalCarbohydrates + "g (目標: " + targetCarbohydrates + "g)");
                    System.out.println("スコア:");
                    System.out.println("  - タンパク質スコア: " + (1 / (1 + Math.abs(totalProtein - targetProtein))));
                    System.out.println("  - 脂質スコア: " + (1 / (1 + Math.abs(totalLipids - targetLipids))));
                    System.out.println("  - 炭水化物スコア: " + (1 / (1 + Math.abs(totalCarbohydrates - targetCarbohydrates))));
                    System.out.println("  - 合計スコア: " + (nutritionScore - totalCost));
                    
                    return nutritionScore - totalCost;
                }
            }
        }

        return Double.MIN_VALUE; // すべての材料が揃わなかった場合
    }

    // 栄養価スコアを計算
    private double calculateNutritionScore(double protein, double lipids, double carbohydrates) {
        double proteinScore = 1 / (1 + Math.abs(protein - targetProtein));         // 差が小さいほど高スコア
        double lipidsScore = 1 / (1 + Math.abs(lipids - targetLipids));
        double carbohydratesScore = 1 / (1 + Math.abs(carbohydrates - targetCarbohydrates));
        return proteinScore + lipidsScore + carbohydratesScore; // 合計スコアを返す
    }
}
