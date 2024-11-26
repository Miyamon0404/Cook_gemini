package cook;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GeminiRecipeFinder {

    // Database1: 材料名、在庫数、有効期限を格納するデータベース
    private static List<String[]> Database1 = new ArrayList<>();

    /**
     * Database1の材料を基にレシピを取得するメソッド
     * @return レシピのリスト (List<JSONObject>)
     * @throws Exception APIの呼び出しに失敗した場合
     */
    public static List<JSONObject> getRecipesFromDatabase() throws Exception {
        // Database1から材料名を取得
        List<String> ingredients = getIngredientNames();

        // Gemini APIを使ってレシピを生成
        JSONArray recipes = getRecipesFromIngredients(ingredients);

        // レシピリストを作成
        List<JSONObject> recipeList = new ArrayList<>();
        for (int i = 0; i < recipes.length(); i++) {
            JSONObject recipe = recipes.getJSONObject(i);
            recipeList.add(recipe);
        }

        return recipeList;
    }

    /**
     * Database1から材料名（各行の最初の列）を取得するメソッド
     * @return 材料名のリスト
     */
    private static List<String> getIngredientNames() {
        List<String> names = new ArrayList<>();
        for (String[] entry : Database1) {
            if (entry.length > 0) { // データがある場合
                names.add(entry[0]); // 材料名をリストに追加
            }
        }
        return names;
    }

    /**
     * 与えられた材料リストを元にGemini APIでレシピを生成するメソッド
     * @param ingredients 材料名のリスト
     * @return レシピ情報のJSONArray
     * @throws Exception APIの呼び出しに失敗した場合
     */
    private static JSONArray getRecipesFromIngredients(List<String> ingredients) throws Exception {
        // プロンプトを生成
        StringBuilder prompt = new StringBuilder("Given the following ingredients: ");
        for (int i = 0; i < ingredients.size(); i++) {
            prompt.append(ingredients.get(i));
            if (i < ingredients.size() - 1) prompt.append(", ");
        }
        prompt.append(", provide a list of possible recipes. For each recipe, return:\n")
        .append("- The name of the recipe (string)\n")
        .append("- Ingredients used in the recipe (string)\n")
        .append("- Amount of each ingredient in grams (float, always in grams)\n")
        .append("- Cooking time in seconds (int)\n\n")
        .append("Output the results as a JSON format where each recipe is a dictionary with the following structure:\n")
        .append("{ \"menu\": \"Recipe name\", \"ingredients\": [{\"name\": \"ingredient name\", \"amount\": float}], \"time_seconds\": int }")
        .append("\nAll ingredient amounts must be in grams.");

        // Gemini APIのURL
        String API_URL = "https://gemini-api-url.com/v1/recipes";

        // HTTPリクエストを準備
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + System.getenv("GEMINI_API_KEY"));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // リクエストボディを送信
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gemini-latest");
        requestBody.put("prompt", prompt.toString());
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.5);

        // リクエスト送信
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // レスポンスを取得
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // レスポンスのJSON解析
        JSONObject jsonResponse = new JSONObject(response.toString());
        String textResponse = jsonResponse.getString("data").trim();
        return new JSONArray(textResponse);
    }

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        try {
            // Database1にデータを追加
            Database1.add(new String[]{"tomato", "5", "2024-12-31"});
            Database1.add(new String[]{"cheese", "2", "2024-11-30"});
            Database1.add(new String[]{"basil", "10", "2024-11-20"});
            Database1.add(new String[]{"olive oil", "1", "2025-01-15"});

            // レシピを取得
            List<JSONObject> recipes = getRecipesFromDatabase();

            // レシピを表示
            System.out.println("Recipes:");
            for (JSONObject recipe : recipes) {
                System.out.println("Menu: " + recipe.getString("menu"));
                System.out.println("Time (seconds): " + recipe.getInt("time_seconds"));
                JSONArray ingredientsArray = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredient = ingredientsArray.getJSONObject(j);
                    System.out.println(" - Ingredient: " + ingredient.getString("name") + ", Amount: " + ingredient.getDouble("amount"));
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
