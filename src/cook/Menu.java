package cook;
import java.util.Map;
public class Menu {
    private String name;
    private Map<String, Double> requiredIngredients;  // 材料名と必要量のマップ

    public Menu(String name, Map<String, Double> requiredIngredients) {
        this.name = name;
        this.requiredIngredients = requiredIngredients;
    }

    public String getName() {
        return name;
    }

    public Map<String, Double> getRequiredIngredients() {
        return requiredIngredients;
    }

}

