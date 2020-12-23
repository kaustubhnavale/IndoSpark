package mipl.indospark;

import java.util.ArrayList;

public class ProdPojo {
    String ida;
    String sku;
    String name;
    String price;
    String Date;
    String title, detail;
    String imageValue;
    String qty, QuoteID, subTotal;
    String count;
    String short_desc;
    String Status;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }


    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getQuoteID() {
        return QuoteID;
    }

    public void setQuoteID(String quoteID) {
        QuoteID = quoteID;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    private ArrayList<ProdPojo> allItemsInSection;


    public ProdPojo(String name, String sku, String price, String image) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.imageValue = image;
    }

    public ProdPojo() {

    }


    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getIda() {
        return ida;
    }

    public void setIda(String ida) {
        this.ida = ida;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageValue() {
        return imageValue;
    }

    public void setImageValue(String imageValue) {
        this.imageValue = imageValue;
    }


    public ArrayList<ProdPojo> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<ProdPojo> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}