package com.tomo.thereisway.management.utilities.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class InventoryGui {

    private Inventory inventory;

    Map<Integer, Runnable> slotToMethodMap;

    public void run(int slot) {
        if (slotToMethodMap.containsKey(slot)){
            slotToMethodMap.get(slot).run();
        }
    }

    Inventory getInventory() {
        return inventory;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }


    public static class Builder {
        Inventory inventory;

        int rowCount;
        String name;
        Map<Integer, ItemStack> slotToItemMap;
        Map<Integer, Runnable> slotToMethodMap;

        public static Builder createBuilder() {
            return new Builder();
        }

        public Builder withRowCount(int rowCount) {
            if (rowCount <= 0) {
                throw new IllegalArgumentException("Row count has to be greater than 0, but is equal to: " + rowCount);
            }
            this.rowCount = rowCount;
            return this;
        }

        public Builder withName(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder withItems(Map<Integer, ItemStack> slotToItemMap) {
            this.slotToItemMap = Objects.requireNonNull(slotToItemMap);
            return this;
        }

        public Builder withMethods(Map<Integer, Runnable> slotToMethodMap) {
            this.slotToMethodMap = Objects.requireNonNull(slotToMethodMap);
            return this;
        }

        public InventoryGui build() {
            InventoryGui gui = new InventoryGui();
            inventory = Bukkit.createInventory(null, rowCount*9, Component.text(name));
            setItems();
            gui.inventory = inventory;
            gui.slotToMethodMap = this.slotToMethodMap;
            return gui;
        }

        private void setItems() {
            for (Map.Entry<Integer, ItemStack> entry : slotToItemMap.entrySet()) {
                if (entry.getKey() <0 || entry.getKey() >= rowCount * 9) {
                    continue;
                }
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }
    }

}
