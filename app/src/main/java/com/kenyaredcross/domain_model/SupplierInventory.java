package com.kenyaredcross.domain_model;


public class SupplierInventory {
    private String itemId, category, itemName, lastRestocked, status;
    private int reorderLevel, stock, unitPrice;

    public SupplierInventory() {
        // Empty constructor required for Firebase
    }

    public SupplierInventory(String itemId, String category, String itemName, String lastRestocked,
                             String status, int reorderLevel, int stock, int unitPrice) {
        this.itemId = itemId;
        this.category = category;
        this.itemName = itemName;
        this.lastRestocked = lastRestocked;
        this.status = status;
        this.reorderLevel = reorderLevel;
        this.stock = stock;
        this.unitPrice = unitPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public String getCategory() {
        return category;
    }

    public String getItemName() {
        return itemName;
    }

    public String getLastRestocked() {
        return lastRestocked;
    }

    public String getStatus() {
        return status;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public int getStock() {
        return stock;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}
