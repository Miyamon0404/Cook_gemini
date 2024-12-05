package cook;
import java.util.List;

public class IngredientDatabase {
    private List<Ingredient> ingredients;

    public IngredientDatabase(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    
 // 材料を追加
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    // ゲッター
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
 

    public Ingredient findIngredient(String name) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName().equalsIgnoreCase(name)) {
                return ingredient;
            }
        }
        return null;
    }
    
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    // データベースの内容を表示するメソッド
    public void printDatabase() {
        System.out.println("\n--- 現在の食材データベース ---");
        for (Ingredient ingredient : ingredients) {
            System.out.println(ingredient);
        }
    }
    
}