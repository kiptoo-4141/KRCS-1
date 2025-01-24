package com.kenyaredcross.domain_model;

public class SupplierInventoryModel {

    String category, itemId, itemName, lastRestocked, status;
    Integer stock, reorderLevel;

    // Default constructor for Firebase
    public SupplierInventoryModel() {
    }

    public SupplierInventoryModel(String category, Integer reorderLevel, Integer stock, String status, String lastRestocked, String itemName, String itemId) {
        this.category = category;
        this.reorderLevel = reorderLevel;
        this.stock = stock;
        this.status = status;
        this.lastRestocked = lastRestocked;
        this.itemName = itemName;
        this.itemId = itemId;
    }

    // Getters and setters for all fields
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(String lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
