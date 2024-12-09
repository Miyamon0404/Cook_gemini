package cook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CookGUI extends JFrame {

    private IngredientDatabase ingredientDatabase;
    private List<Menu> menus;
    private DefaultTableModel tableModel; // クラス変数として宣言

    // コンストラクタ
    public CookGUI(IngredientDatabase ingredientDatabase, List<Menu> menus) {
        this.ingredientDatabase = ingredientDatabase;
        this.menus = menus;

        setTitle("料理管理システム");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton addIngredientButton = new JButton("材料を追加");
        JButton delButton = new JButton("材料の削除");
        JButton searchMenuButton = new JButton("最適メニュー探索");
        JButton exitButton = new JButton("終了");


        
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(delButton);
        buttonPanel.add(searchMenuButton);
        buttonPanel.add(exitButton);


     // テーブルの作成
        
        String[] columnNames = {"名前", "賞味期限", "残量(g)", "タンパク質(g)", "脂質(g)", "炭水化物(g)"};
        tableModel = new DefaultTableModel(columnNames, 0); // ここで初期化
        JTable ingredientTable = new JTable(tableModel);
        
        // テーブルのデータ更新
        updateIngredientTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(ingredientTable);

        mainPanel.add(new JLabel("材料データベース"), BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ボタンのアクションリスナー
        addIngredientButton.addActionListener(e -> addIngredientDialog(tableModel));
        searchMenuButton.addActionListener(e -> searchOptimalMenu());
        exitButton.addActionListener(e -> System.exit(0));
        delButton.addActionListener(e -> delIngredientDialog(ingredientTable));
        
        // ウィンドウにコンポーネントを追加
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
    }

    // 材料を追加するダイアログ
    private void addIngredientDialog(DefaultTableModel tableModel) {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        JTextField nameField = new JTextField();
        JTextField expiryDateField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField proteinField = new JTextField();
        JTextField lipidsField = new JTextField();
        JTextField carbohydratesField = new JTextField();

        inputPanel.add(new JLabel("名前:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("賞味期限 (YYYY/MM/DD):"));
        inputPanel.add(expiryDateField);
        inputPanel.add(new JLabel("量 (g):"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("タンパク質 (g):"));
        inputPanel.add(proteinField);
        inputPanel.add(new JLabel("脂質 (g):"));
        inputPanel.add(lipidsField);
        inputPanel.add(new JLabel("炭水化物 (g):"));
        inputPanel.add(carbohydratesField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "材料を追加", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	try {
        	    String name = nameField.getText();
        	    System.out.println("Name: " + name); // ログ
        	    
        	    String expiryDate = expiryDateField.getText();
        	    System.out.println("ExpiryDate: " + expiryDate); // ログ

        	    double quantity = parseDoubleWithValidation(quantityField.getText(), "量");
        	    double protein = parseDoubleWithValidation(proteinField.getText(), "タンパク質");
        	    double lipids = parseDoubleWithValidation(lipidsField.getText(), "脂質");
        	    double carbohydrates = parseDoubleWithValidation(carbohydratesField.getText(), "炭水化物");

        	    Ingredient ingredient = new Ingredient(name, expiryDate, quantity, protein, lipids, carbohydrates);
        	    ingredientDatabase.addIngredient(ingredient);

        	    updateIngredientTable(tableModel);

        	    JOptionPane.showMessageDialog(this, "材料を追加しました: " + ingredient);
        	} catch (IllegalArgumentException ex) {
        	    JOptionPane.showMessageDialog(this, "入力エラー: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        	} catch (Exception ex) {
        	    ex.printStackTrace(); // 詳細エラー情報をコンソールに出力
        	    JOptionPane.showMessageDialog(this, "不明なエラーが発生しました: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        	}

        }
    }

 // 最適なメニューを探索する
    private void searchOptimalMenu() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JTextField proteinField = new JTextField();
        JTextField lipidsField = new JTextField();
        JTextField carbohydratesField = new JTextField();

        inputPanel.add(new JLabel("タンパク質 (g):"));
        inputPanel.add(proteinField);
        inputPanel.add(new JLabel("脂質 (g):"));
        inputPanel.add(lipidsField);
        inputPanel.add(new JLabel("炭水化物 (g):"));
        inputPanel.add(carbohydratesField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "栄養目標を設定", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double protein = Double.parseDouble(proteinField.getText());
                double lipids = Double.parseDouble(lipidsField.getText());
                double carbohydrates = Double.parseDouble(carbohydratesField.getText());

                MenuMatcher menuMatcher = new MenuMatcher(ingredientDatabase, protein, lipids, carbohydrates);
                Menu optimalMenu = menuMatcher.findOptimalMenuBFS(menus);

                if (optimalMenu != null) {
                    updateIngredientTable(tableModel); // テーブルを更新
                    JOptionPane.showMessageDialog(this, "最適なメニュー: " + optimalMenu.getName()
                    + "\n" + optimalMenu.getRequiredIngredients().entrySet().stream()
                    .map(s -> "-" + s.getKey() + " " + s.getValue() + "g")
                    .collect(Collectors.joining("\n"))
                    );
                    
                } else {
                    JOptionPane.showMessageDialog(this, "最適なメニューは見つかりませんでした。");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "入力エラー: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateIngredientTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        System.out.println("現在の材料リスト:");
        for (Ingredient ingredient : ingredientDatabase.getIngredients()) {
            System.out.println(ingredient); // デバッグログ
            tableModel.addRow(new Object[]{
                    ingredient.getName(),
                    ingredient.getExpiryDate(),
                    ingredient.getQuantity(),
                    ingredient.getProtein(),
                    ingredient.getLipids(),
                    ingredient.getCarbohydrates()
            });
        }
    }
    
    // 選択した行の削除
   public void delIngredientDialog(JTable ingredientTable) {
          // 選択されている行
          int row = ingredientTable.getSelectedRow();
          // 選択されていないときは戻る
          if (row < 0) {
            return;
          }

          // 指定行を削除する
          tableModel.removeRow(row);
          System.out.println(ingredientDatabase.getIngredients().remove(row));
  }



    // 入力値を数値として検証するメソッド
    private double parseDoubleWithValidation(String value, String fieldName) {
        try {
            double result = Double.parseDouble(value);
            if (result < 0) {
                throw new IllegalArgumentException(fieldName + "は0以上の数値を入力してください。");
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + "は数値で入力してください。");
        }
    }

    // メインメソッド
 // メインメソッド
    public static void main(String[] args) {
        // サンプルデータ
        IngredientDatabase ingredientDatabase = new IngredientDatabase(new ArrayList<>()); // 修正箇所
        List<Ingredient> ingredients = List.of(
        		new Ingredient("Tomato", "2025/01/01", 500, 7.76, 6.11, 0.17),
        		new Ingredient("Chicken Breast", "2025/01/01", 300, 21.3, 5.9, 0.1),
        		new Ingredient("Rice", "2025/06/01", 1000, 6.1, 0.9, 77.6),
        		new Ingredient("Onion", "2025/01/01", 300, 1.1, 0.1, 8.5),
        		//new Ingredient("Curry Powder", "2025/01/01", 100, 0.5, 8.0, 10.0),
        		new Ingredient("Carrot", "2025/01/01", 250, 0.9, 0.2, 9.6),
        		new Ingredient("Radish", "2000/01/01", 550, 0.9, 0.2, 9.6));
        ingredients.stream().forEach( s -> ingredientDatabase.addIngredient(s));
        
        List<Menu> menus = List.of(
                new	Menu("Vegetable Curry Rice", Map.of(
                	    "Onion", 200.0,
                	    "Tomato", 300.0,
                	"Curry Powder", 15.0,
                	    "Carrot", 150.0,
                	    "Potato", 200.0,
                	    "Rice", 200.0
                	)),
                new Menu("Chicken Curry", Map.of(
                	    "Chicken Breast", 300.0,
                	    "Onion", 150.0,
                	    "Tomato", 200.0,
                	    "Curry Powder", 15.0
                	)),
                new	Menu("Tomato Chicken Stew", Map.of(
                	"Chicken Breast", 300.0,
                	    "Onion", 150.0,
                	    "Tomato", 400.0,
                	    "Carrot", 100.0,
                	    "Potato", 200.0
                	)),
                new	Menu("Tomato Chicken Stir-fry", Map.of(
                	"Chicken Breast", 250.0,
                	    "Tomato", 300.0,
                	    "Onion", 150.0,
                	    "Carrot", 100.0
                	)),
                new	Menu("Potato Tomato Soup", Map.of(
                	    "Potato", 300.0,
                	    "Tomato", 400.0,
                	    "Onion", 150.0,
                	    "Carrot", 100.0
                	))
        );

        SwingUtilities.invokeLater(() -> {
            CookGUI cookGUI = new CookGUI(ingredientDatabase, menus);
            cookGUI.setVisible(true);
        });
    }


}
