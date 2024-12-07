package cook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuMatcher {
    private IngredientDatabase ingredientDatabase;
    private double targetProtein;      	// 1日に必要なタンパク質
    private double targetLipids;       	// 1日に必要な脂質
    private double targetCarbohydrates; // 1日に必要な炭水化物

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

        for (Menu menu : menus) {
            if (!areIngredientsSufficient(menu)) {
                System.out.println("メニュー [" + menu.getName() + "] は材料が不足しているためスコア計算をスキップします。");
                continue;
            }

            double score = calculateMenuScoreBFS(menu);
            System.out.println("メニュー [" + menu.getName() + "] のスコア: " + score);
            System.out.println();
            // 最適なメニューの更新。最初は-∞からスタートだから最初に作成できるメニューは絶対に作成できる。
            if (score > maxScore) {
                maxScore = score;
                optimalMenu = menu;
            }
        }

        //　最適なメニューの表示
        if (optimalMenu != null) {
            consumeIngredients(optimalMenu);
            System.out.println("\n選ばれた最適なメニュー: " + optimalMenu.getName());
        } else {
            System.out.println("\n最適なメニューは見つかりませんでした。");
        }
        return optimalMenu;
    }

    // 材料が十分に揃っているか確認
    private boolean areIngredientsSufficient(Menu menu) {
        for (Map.Entry<String, Double> entry : menu.getRequiredIngredients().entrySet()) {
            String ingredientName = entry.getKey();
            double quantityNeeded = entry.getValue();
            
            //材料の確認
            Ingredient ingredient = ingredientDatabase.findIngredient(ingredientName);
            if (ingredient == null || ingredient.getQuantity() < quantityNeeded) {
                System.out.println("材料 [" + ingredientName + "] が不足しています。必要量: " + quantityNeeded +
                                   ", 在庫: " + (ingredient != null ? ingredient.getQuantity() : 0));
                return false;
            }
        }
        return true;
    }
    
 // 材料を消費する
 // MenuMatcherクラス内のconsumeIngredientsメソッドを確認・修正

    public void consumeIngredients(Menu menu) {
        Map<String, Double> requiredIngredients = menu.getRequiredIngredients();
        Set<String> consumedIngredients = new HashSet<>(); // 消費済み材料のトラッキング

        System.out.println("メニュー名: " + menu.getName());
        System.out.println("必要材料: " + requiredIngredients);

        for (Map.Entry<String, Double> entry : requiredIngredients.entrySet()) {
            String ingredientName = entry.getKey();
            double requiredQuantity = entry.getValue();

            // 同じ材料を複数回消費しない
            if (consumedIngredients.contains(ingredientName)) {
                System.out.println("材料 [" + ingredientName + "] はすでに消費済み。スキップします。");
                continue;
            }

            consumedIngredients.add(ingredientName);

            Ingredient ingredient = ingredientDatabase.findIngredient(ingredientName);
            if (ingredient != null) {
                System.out.println("消費前の [" + ingredientName + "] の残量: " + ingredient.getQuantity());
                double remainingQuantity = ingredient.getQuantity() - requiredQuantity;
                System.out.println("必要量: " + requiredQuantity + " -> 消費後の残量: " + (remainingQuantity > 0 ? remainingQuantity : 0));

                // 残量を更新または削除
                if (remainingQuantity > 0) {
                    ingredient.setQuantity(remainingQuantity);
                } else {
                    System.out.println("材料 [" + ingredientName + "] が足りなくなったため、データベースから削除されます。");
                    ingredientDatabase.removeIngredient(ingredient);
                }
            } else {
                System.out.println("材料 [" + ingredientName + "] がデータベースに見つかりませんでした。");
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
                double ingredientCost = ingredient.calculateCost(todayDate);
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
                    System.out.println("  - 合計スコア: " + -(nutritionScore - totalCost));
                    
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
