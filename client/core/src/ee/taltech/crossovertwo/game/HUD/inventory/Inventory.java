package ee.taltech.crossovertwo.game.HUD.inventory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import ee.taltech.crossovertwo.game.HUD.hud.HUD;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.HUD.messages.MsgType;
import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.items.weapon.ItemWeapon;
import ee.taltech.crossovertwo.game.mothership.Mothership;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.game.trader.Trader;
import ee.taltech.crossovertwo.packets.PacketPlayerWeapon;
import ee.taltech.crossovertwo.utilities.ControlKeys;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.List;

import static ee.taltech.crossovertwo.game.players.Player.keyMap;


public class Inventory {

    private final Table tableInventory;
    // The inventoryCells and tradingCells are the actual items in the inventory and trading table
    private List<Item> inventoryCells = new ArrayList<>(), tradingCells = new ArrayList<>();
    // The mirrorInventoryCells and mirrorTradingCells are the items that are displayed in the inventory and trading table while trading
    // (dragging items around while sell button not pressed or reset button not pressed or player not leaving the trading screen)
    private List<Item> mirrorInventoryCells = new ArrayList<>(), mirrorTradingCells = new ArrayList<>();
    private Item equippedItem;
    private Item selectedItem;
    private final Button buttonUseEquip;
    private int initialCostOfInventory;
    private final Skin skin;
    private final Table mainTable;
    private final Table tableInventoryItems;
    private final Table tableItemEquip;
    private final Table tableTrade;
    private final Table tableTradeItems;
    private final DragAndDrop dragAndDrop;
    private boolean isOpen = false;
    private boolean isTrading = false;
    private final Label descriptionText;

    private final Label moneyLabel;
    private int money = 100;

    private static Source currentSource = Source.NOTHING;

    public enum Source {
        TRADER, MOTHERSHIP, NOTHING
    }

    /**
     * Constructor for the Inventory class
     */
    public Inventory() {
        Stage stage = HUD.getStage();
        skin = GraphicVariables.getSkin();

        initialCostOfInventory = calculateCostCurrentOfInventory();

        mirrorTradingCells = new ArrayList<>(tradingCells);

        dragAndDrop = new DragAndDrop();

        mainTable = new Table();
        // mainTable.setDebug(true);
        mainTable.setFillParent(true);
        mainTable.padTop(75);
        mainTable.defaults().growX().space(10);
        mainTable.setHeight(500);
        mainTable.setWidth(500);
        stage.addActor(mainTable);
        stage.addActor(CraftingTable.initialize(this));

        GraphicVariables.getGeneralFontForLabels().getData().setScale(2f); // Set the scale factor according to your desired size

        // Create the Label with the font
        Label.LabelStyle labelStyleForLabels = GraphicVariables.getLabelStyleForLabels();
        Label.LabelStyle labelStyle = GraphicVariables.getLabelStyle();

        tableInventory = new Table();
        // tableInventory.setDebug(true);
        Label inventoryLabel = new Label("Inventory", labelStyleForLabels);
        inventoryLabel.setSize(20, 20);
        tableInventory.pad(5);
        tableInventory.defaults().fill().space(5);
        tableInventory.add(inventoryLabel).height(10).row();
        tableInventory.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.add(tableInventory).expandX().grow().fill().uniformX();;

        tableInventoryItems = new Table();
        // tableInventoryItems.setDebug(true);
        tableInventoryItems.defaults().growX().space(5);

        ScrollPane scrollPane = new ScrollPane(tableInventoryItems, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setTouchable(Touchable.enabled);

        tableInventory.add(scrollPane).expand().padTop(10).padLeft(40).padRight(40).row();

        tableTrade = new Table();
        tableTrade.pad(5);
        Label tradeLabel = new Label("Trade Hub", labelStyleForLabels);
        // tableTrade.setDebug(true);
        tableTrade.defaults().fill().space(5);
        tableTrade.add(tradeLabel).height(10).row();
        tableTrade.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.add(tableTrade).expandX().grow().fill().uniformX().row();

        tableTradeItems = new Table();
        // tableTradeItems.setDebug(true);

        ScrollPane scrollPaneTrade = new ScrollPane(tableTradeItems, skin);
        scrollPaneTrade.setFadeScrollBars(false);
        scrollPaneTrade.setScrollingDisabled(true, false);
        scrollPaneTrade.setTouchable(Touchable.enabled);

        tableTradeItems.defaults().growX().space(5);
        tableTrade.add(scrollPaneTrade).expand().padTop(10).padLeft(40).padRight(40).row();

        Table tableTradeButtons = new Table();
        // tableTradeButtons.setDebug(true);
        tableTrade.add(tableTradeButtons);

        TextButton buttonSell = new TextButton("Trade", skin);
        tableTradeButtons.add(buttonSell).grow().height(20).padTop(10).padLeft(40).padRight(40).row();
        TextButton buttonReset = new TextButton("Reset", skin);
        tableTradeButtons.add(buttonReset).grow().height(20).padTop(10).padLeft(40).padRight(40).row();
        moneyLabel = new Label("Money: " + money, labelStyle);
        tableTradeButtons.add(moneyLabel).grow().padTop(10).padLeft(40).padRight(40).row();

        Table tableItemDescription = new Table();
        // tableItemDescription.setDebug(true);
        tableItemDescription.setBackground(GraphicVariables.getGeneralBackground());
        tableItemDescription.pad(5);
        System.out.println("table1: " + mainTable.getHeight());
        tableItemDescription.defaults().fill().space(5);
        descriptionText = new Label("Here will be Description\n-\n-\n-", labelStyle);
        descriptionText.setWrap(true);
        tableItemDescription.add(descriptionText).grow().center().row();
        buttonUseEquip = new TextButton("Use/Equip", skin);
        tableItemDescription.add(buttonUseEquip).height(20);

        tableItemEquip = new Table();
        // tableItemEquip.setDebug(true);
        tableItemEquip.setBackground(GraphicVariables.getGeneralBackground());
        tableItemEquip.pad(5);
        System.out.println("table2: " + tableItemEquip.getWidth());
        tableItemEquip.defaults().fill().space(5);

        mainTable.add(tableItemEquip).expandX().uniformX().height(200);
        mainTable.add(tableItemDescription).expandX().uniformX().height(200).row();

        addItemsToTable(tableInventoryItems, mirrorInventoryCells);
        addItemsToTable(tableTradeItems, mirrorTradingCells);

        Button craftSwitch = new TextButton("(" + Input.Keys.toString(keyMap.get(ControlKeys.CRAFT)) + ") To crafting table ->", skin);

        mainTable.add(craftSwitch).colspan(2).height(20).padBottom(10);

        craftSwitch.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                switchCrafting();
                return true;
            }
        });

        mainTable.setVisible(isOpen);

        buttonSell.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int currentCost = calculateCostCurrentOfInventory();

                // Calculate and update the money
                if (currentCost - initialCostOfInventory > money) {
                    Message.showMessage("Not enough money, sorry you'll have to go farm :(", MsgType.ERROR);
                    return false;
                }
                money = money - (currentCost - initialCostOfInventory);
                initialCostOfInventory = currentCost;
                moneyLabel.setText("Money: " + money);

                // Update the inventory and trading cells
                tableInventoryItems.clear();
                tableTradeItems.clear();

                if (mirrorTradingCells.contains(equippedItem)) {
                    equippedItem = null;
                    Player.setMainWeapon((ItemWeapon) equippedItem);
                    tableItemEquip.clear();
                }

                inventoryCells = new ArrayList<>(mirrorInventoryCells);
                renewTradeTable();
                addItemsToTable(tableInventoryItems, mirrorInventoryCells);
                addItemsToTable(tableTradeItems, mirrorTradingCells);
                return true;
            }
        });

        buttonReset.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetAllTables();
                return true;
            }
        });

        buttonUseEquip.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return useSelectedItem();
            }
        });

    }

    /**
     * Method to use the selected item
     * If the selected item is not null, it is used
     * If the selected item is not a weapon, it is used
     * If the selected item is a weapon, it is equipped
     * @return True if the item is used, false otherwise
     */
    public boolean useSelectedItem() {
        if (selectedItem == null) return false;
        if (!(selectedItem instanceof ItemWeapon)) {
            selectedItem.use();
            selectedItem = null;
            return true;
        }
        equippedItem = selectedItem;
        Player.setMainWeapon((ItemWeapon) equippedItem);
        tableItemEquip.clear();
        TextButton buttonItem = new TextButton(equippedItem.getName(), skin);
        tableItemEquip.add(buttonItem).grow();
        PacketPlayerWeapon.sendPlayerWeapon((ItemWeapon) equippedItem);
        return true;
    }

    /**
     * Method to set the selected item
     * @param item The item to set as selected
     */
    public void setSelectedItem(Item item) {
        selectedItem = item;
    }

    private void generateTradeItems() {
        switch (currentSource) {
            case TRADER -> tradingCells = Trader.generateTradeItems();
            case MOTHERSHIP -> tradingCells = Mothership.generateTradeItems();
        }
    }

    /**
     * Method to reset the inventory
     * When the reset button is clicked, the inventory is reset to the initial state before trading
     * also resets the trading table
     */
    private void resetAllTables() {
        mirrorInventoryCells = new ArrayList<>(inventoryCells);
        mirrorTradingCells = new ArrayList<>(tradingCells);
        tableInventoryItems.clear();
        tableTradeItems.clear();
        addItemsToTable(tableInventoryItems, mirrorInventoryCells);
        addItemsToTable(tableTradeItems, mirrorTradingCells);
    }

    /**
     * Method to renew the trade table
     * Clears the table and creates new trading cells
     */
    private void renewTradeTable() {
        tableTradeItems.clear();
        tradingCells = new ArrayList<>();
        generateTradeItems();
        mirrorTradingCells = new ArrayList<>(tradingCells);
    }

    /**
     * Method to add items to the table
     * It takes all items from the list and adds them to the table. Here items must be Item class objects.
     * @param table The table to add the items to
     * @param items The items to add to the table
     */
    private void addItemsToTable(final Table table, List<Item> items) {
        for (final Item item : items) {
            final TextButton button = new TextButton(item.getName(), skin); // In the future should be ImageTextButtons as images added to represent the items
            button.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setSelectedItem(item);
                    if (inventoryCells.contains(item) || selectedItem == null) {
                        buttonUseEquip.setDisabled(false);
                        buttonUseEquip.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                    } else {
                        buttonUseEquip.setDisabled(true);
                        buttonUseEquip.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
                    }
                    descriptionText.setText(item + "\nType: " + item.getType() + "\n" + item.getDescription());
                    return true;
                }
            });

            // This block of code sets up DragAndDrop.Source for each button
            dragAndDrop.addSource(new DragAndDrop.Source(button) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(item); // Set the item as payload

                    // Create a new drag actor with the item's text, so it looks like the item is being dragged but the actual button stays in place
                    TextButton dragActor = new TextButton(item.getName(), skin);

                    // Set the drag actor's size, the same as the button
                    dragActor.setSize(button.getWidth(), button.getHeight());

                    // Set the drag actor
                    payload.setDragActor(dragActor);

                    // Set the drag actor's position, so button will stay at the center of the pointer
                    dragAndDrop.setDragActorPosition(getActor().getWidth() / 2, -getActor().getHeight() / 2);
                    return payload;
                }

                /**
                 * Method to stop the drag
                 * If the drag is stopped, the payload is removed and all elements stay in place
                 */
                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) { }
            });

            // This block of code sets up DragAndDrop.Target for each table
            dragAndDrop.addTarget(new DragAndDrop.Target(tableTrade) {
                /**
                 * Method to drag. Actually does nothing, but is required to be implemented
                 */
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                    System.out.println(payload.getObject()); // just to test if the payload is correct
                    return true;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                    Item item = (Item) payload.getObject(); // Get the item from the payload (what we drag)
                    mirrorTradingCells.add(item); // Add the item to the trading cells (from the inventory cells)

                    // This block is just for confidence that the item is removing from the table it belongs to
                    if (mirrorInventoryCells.contains(item)){
                        mirrorInventoryCells.remove(item);
                    } else {
                        mirrorTradingCells.remove(item);
                    }
                    // So as I don't know how to delete items from table, I just clear all and add all the items again
                    // P.S. Yes, I know it's not efficient at all, but it is the only way I found to make it work
                    tableInventoryItems.clear();
                    tableTradeItems.clear();
                    addItemsToTable(tableInventoryItems, mirrorInventoryCells);
                    addItemsToTable(tableTradeItems, mirrorTradingCells);
                }
            });

            // So we want to drag elements from the other table too
            dragAndDrop.addTarget(new DragAndDrop.Target(tableInventory) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                    System.out.println(payload.getObject());
                    return true;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                    Item item = (Item) payload.getObject();
                    mirrorInventoryCells.add(item);

                    if (mirrorTradingCells.contains(item)){
                        mirrorTradingCells.remove(item);
                    } else {
                        mirrorInventoryCells.remove(item);
                    }

                    tableInventoryItems.clear();
                    tableTradeItems.clear();
                    addItemsToTable(tableInventoryItems, mirrorInventoryCells);
                    addItemsToTable(tableTradeItems, mirrorTradingCells);
                }
            });

            table.add(button).height(50).row();
        }
    }

    /**
     * Method to calculate the cost of the inventory at this moment
     * Used at the moment of selling
     * @return The cost of the inventory
     */
    private int calculateCostCurrentOfInventory() {
        return mirrorInventoryCells.stream().mapToInt(Item::getCost).sum();
    }

    /**
     * Method to add items
     */
    public void addItem(Item item) {
        inventoryCells.add(item);
        initialCostOfInventory += item.getCost();
        mirrorInventoryCells.clear();
        mirrorInventoryCells = new ArrayList<>(inventoryCells);
        tableInventoryItems.clear();
        addItemsToTable(tableInventoryItems, mirrorInventoryCells);
    }

    /**
     * Method to remove items
     */
    public void removeItem(Item item) {
        inventoryCells.remove(item);
        resetAllTables();
    }

    /**
     * Used to toggle inventory when TAB is pressed
     */
    public void show() {
        CraftingTable.updateResourceAmounts();
        CraftingTable.show(false);
        isOpen = !isOpen;
        mainTable.setVisible(isOpen);
    }

    /**
     * Used to toggle inventory in case when need to close all windows.
     * @param isOpen True if inventory is open, false otherwise
     */
    public void show(boolean isOpen) {
        CraftingTable.updateResourceAmounts();
        CraftingTable.show(false);
        this.isOpen = isOpen;
        mainTable.setVisible(isOpen);
    }

    /**
     * Method to switch to the crafting table and inventory.
     */
    public void switchCrafting() {
        CraftingTable.show(isOpen);
        isOpen = !isOpen;
        mainTable.setVisible(isOpen);
    }

    /**
     * Method to check if the inventory is visible
     * @return True if the inventory is visible, false otherwise
     */
    public boolean isVisible() {
        return isOpen;
    }

    /**
     * Method to get the money
     * @return The money
     */
    public void addMoney(int money) {
        this.money += money;
        moneyLabel.setText("Money: " + this.money);
    }

    /**
     * Method to draw the inventory as stage
     * This method is called in the render method of the Player
     */
    public void draw() {
        if (!isTrading) resetAllTables();
    }

    /**
     * Method to set the trading
     * True if player close enough to the trading hub, false otherwise
     */
    public void setTrading(Source source) {
        isTrading = true;
        tableTrade.setVisible(true);
        currentSource = source;
        generateTradeItems();
        resetAllTables();
    }

    /**
     * Method to set the trading
     * False if player is not close enough to the trading hub
     */
    public void setNotTrading() {
        isTrading = false;
        tableTrade.setVisible(false);
        currentSource = Source.NOTHING;
    }

    /**
     * Method to get the items
     * @return The items
     */
    public List<Item> getItems() {
        return inventoryCells;
    }

    /**
     * Method to get the selected item
     * @return The selected item
     */
    public Source getCurrentSource() {
        return currentSource;
    }
}
