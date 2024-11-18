package cook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuMatcher {
    private IngredientDatabase ingredientDatabase;

    public MenuMatcher(IngredientDatabase ingredientDatabase) {
        this.ingredientDatabase = ingredientDatabase;
    }

    // 幅優先探索で最適なメニューを選択
    public Menu findOptimalMenuBFS(List<Menu> menus) {
        Menu optimalMenu = null;
        double minCost = Double.MAX_VALUE;

        for (Menu menu : menus) {
            double cost = calculateMenuCostBFS(menu);
            System.out.println("メニュー [" + menu.getName() + "] の総コスト: " + cost);

            if (cost < minCost) {
                minCost = cost;
                optimalMenu = menu;
            }
        }
        return optimalMenu;
    }

    // 幅優先探索でメニューの総コストを計算し、探索過程を出力
    private double calculateMenuCostBFS(Menu menu) {
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); 
        // 現在の日付を取得String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 現在の日付を取得
        
        // 初期ノードを追加
        Node startNode = new Node("Start", 0, 0, null);
        queue.add(startNode);

        double totalCost = 0.0;

        System.out.println("\n--- メニュー [" + menu.getName() + "] の探索開始 ---");

        // 幅優先探索のループ
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            String currentIngredient = currentNode.getIngredientName();
            visited.add(currentIngredient);

            System.out.println("現在のノード: " + currentIngredient + ", コスト: " + currentNode.getCost());

            // メニュー内の材料を探索
            for (Map.Entry<String, Double> entry : menu.getRequiredIngredients().entrySet()) {
                String ingredientName = entry.getKey();
                double quantityNeeded = entry.getValue();

                // 既に訪問済みの材料はスキップ
                if (visited.contains(ingredientName)) {
                    continue;
                }

                Ingredient ingredient = ingredientDatabase.findIngredient(ingredientName);
                if (ingredient == null || ingredient.getQuantity() < quantityNeeded) {
                    System.out.println("材料が不足しています: " + ingredientName);
                    return Double.MAX_VALUE;  // 必要な材料が不足している場合、コストを最大に設定
                }

                // 材料のコスト計算（消費期限を考慮）
                double ingredientCost = ingredient.calculateCost(todayDate) * quantityNeeded;
                Node childNode = new Node(ingredientName, quantityNeeded, currentNode.getCost() + ingredientCost, currentNode);

                System.out.println("探索中の材料: " + ingredientName + ", 必要量: " + quantityNeeded + ", 累積コスト: " + childNode.getCost());

                queue.add(childNode);
                visited.add(ingredientName);

                // 目標状態チェック: 全ての材料が揃ったかどうか
                if (visited.containsAll(menu.getRequiredIngredients().keySet())) {
                    totalCost = childNode.getCost();
                    System.out.println("目標状態に到達しました！ 累積コスト: " + totalCost);
                    return totalCost;
                }
            }
        }
        return totalCost;
    }
}
