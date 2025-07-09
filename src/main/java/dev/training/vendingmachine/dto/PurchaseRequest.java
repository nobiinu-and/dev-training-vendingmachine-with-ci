package dev.training.vendingmachine.dto;

public class PurchaseRequest {
    private String item;
    private int amount;

    public PurchaseRequest() {
    }

    public PurchaseRequest(String item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
