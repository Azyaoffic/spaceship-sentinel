package ee.taltech.crossovertwo.game.HUD.inventory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.HUD.messages.MsgType;
import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.items.ItemToCraft;
import ee.taltech.crossovertwo.game.items.turret.ItemTurret;
import ee.taltech.crossovertwo.game.items.turret.TurretSelection;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.utilities.ControlKeys;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.List;

import static ee.taltech.crossovertwo.game.players.Player.keyMap;


public class CraftingTable {

    private static Table mainTable, tableCraft, tableCraftItems, tableCraftItem, tableButtons;
    private static Label descLabel, coalLabel, ironLabel, goldLabel;
    private static Inventory inventory;
    private static ItemToCraft selectedItemToCraft;
    private static Skin skin;
    protected static boolean isCrafting = false;
    private static List<ItemToCraft> items = List.of(); // Here we can hardcode the items that can be crafted, for example when Alex finishes the turret :/

    /**
     * This method initializes the crafting table
     * @return The table with the crafting table
     */
    public static Table initialize(Inventory inventory) {
        CraftingTable.inventory = inventory;
        skin = GraphicVariables.getSkin();

        mainTable = new Table();
        // mainTable.setDebug(true);
        mainTable.setFillParent(true);
        mainTable.padTop(75);
        mainTable.defaults().growX().space(10);
        mainTable.setHeight(500);
        mainTable.setWidth(500);

        GraphicVariables.getGeneralFontForLabels().getData().setScale(2f); // Set the scale factor according to your desired size

        // Create the Label with the font
        Label.LabelStyle labelStyleForLabels = GraphicVariables.getLabelStyleForLabels();
        Label.LabelStyle labelStyle = GraphicVariables.getLabelStyle();

        tableCraftItems = new Table();
        // tableInventory.setDebug(true);
        Label inventoryLabel = new Label("Crafting Table", labelStyleForLabels);
        inventoryLabel.setSize(20, 20);
        tableCraftItems.pad(5);
        tableCraftItems.defaults().fill().space(5);
        tableCraftItems.add(inventoryLabel).height(10).row();
        tableCraftItems.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.add(tableCraftItems).expandX().grow().fill().uniformX();;

        tableCraftItem = new Table();
        // tableInventoryItems.setDebug(true);
        tableCraftItem.defaults().growX().space(5);
        tableCraftItems.add(tableCraftItem).expand().padTop(10).padLeft(40).padRight(40).row();

        tableCraft = new Table();
        tableCraft.pad(5);
        descLabel = new Label("Here will be Description\n-\n-\n-", labelStyle);
        descLabel.setAlignment(1);
        descLabel.setWrap(true);
        // tableTrade.setDebug(true);
        tableCraft.defaults().fill().space(5);
        tableCraft.add(descLabel).grow().fill().expand().center().row();
        tableCraft.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.add(tableCraft).expandX().grow().fill().uniformX().row();

        tableButtons = new Table();

        coalLabel = new Label("Coal: " + Player.getResourceAmount("coal"), labelStyle);
        tableButtons.add(coalLabel).grow().padTop(10).padLeft(40);
        ironLabel = new Label("Iron: " + Player.getResourceAmount("iron"), labelStyle);
        tableButtons.add(ironLabel).grow().padTop(10).padLeft(20);
        goldLabel = new Label("Gold: " + Player.getResourceAmount("gold"), labelStyle);
        tableButtons.add(goldLabel).grow().padTop(10).padLeft(20).row();

        tableCraft.add(tableButtons).bottom().fill().expandX();

        generateItems();
        Button buttonCraft = new TextButton("Craft", skin);
        tableButtons.add(buttonCraft).fill().growX().expandX().height(50).pad(10).colspan(3);

        mainTable.setVisible(isCrafting);

        Button closeButton = new TextButton("<- Return to the inventory (" + Input.Keys.toString(keyMap.get(ControlKeys.CRAFT)) + ")", skin);

        mainTable.add(closeButton).height(20).colspan(2).padBottom(10);

        buttonCraft.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedItemToCraft != null && hasEnoughResources()) {
                    craftItem();
                } else {
                    Message.showMessage("Not enough resources or you did not chose an item", MsgType.ERROR);
                }
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventory.switchCrafting();
            }
        });

        return mainTable;
    }

    /**
     * This method generates the items that can be crafted
     */
    private static void generateItems() {
        items = new ArrayList<>();
        ItemToCraft item1 = new ItemToCraft("Energy pack", Item.Type.TURRET, 2, 2, 2);
        item1.setDescription("These energy packs are very powerful and have a lot of energy accumulated in them. Commonly used for trading.");
        items.add(item1);
        items.add(new ItemTurret(TurretSelection.TURRET));
        items.add(new ItemTurret(TurretSelection.ENERGYTURRET));
        for (ItemToCraft item : items) {
            TextButton button = new TextButton(item.getName(), skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedItemToCraft = item;
                    descLabel.setText(item.getName() +
                            "\nof type " + item.getType() +
                            "\nresources needed: " +
                            "\nCoal - " + item.getCoal() +
                            "\nIron - " + item.getIron() +
                            "\nGold - " + item.getGold() +
                            "\n" + item.getDescription());
                }
            });
            tableCraftItem.add(button).height(50).row();
        }
    }

    /**
     * This method checks if the player has enough resources to craft the selected item
     * @return True if the player has enough resources, false otherwise
     */
    private static boolean hasEnoughResources() {
        if (selectedItemToCraft != null) {
            return Player.getResourceAmount("coal") >= selectedItemToCraft.getCoal() &&
                    Player.getResourceAmount("iron") >= selectedItemToCraft.getIron() &&
                    Player.getResourceAmount("gold") >= selectedItemToCraft.getGold();
        }
        return false;
    }

    /**
     * This method crafts the selected item
     */
    public static void craftItem() {
        if (selectedItemToCraft != null && hasEnoughResources()) {
            Player.removeResource(selectedItemToCraft.getCoal(), selectedItemToCraft.getIron(), selectedItemToCraft.getGold());
            inventory.addItem(selectedItemToCraft);
            updateResourceAmounts();
            Message.showMessage("Item crafted and added to your inventory", MsgType.INFO);
        }
    }

    /**
     * This method updates the resource amounts
     */
    public static void updateResourceAmounts() {
        coalLabel.setText("Coal: " + Player.getResourceAmount("coal"));
        ironLabel.setText("Iron: " + Player.getResourceAmount("iron"));
        goldLabel.setText("Gold: " + Player.getResourceAmount("gold"));
    }

    /**
     * This method shows the crafting table
     * @param show True if the crafting table should be shown, false otherwise
     */
    public static void show(boolean show) {
        isCrafting = show;
        mainTable.setVisible(show);
    }

    /**
     * This method checks if the player is crafting
     * @return True if the player is crafting, false otherwise
     */
    public static boolean isCrafting() {
        return isCrafting;
    }
}
