package com.test.importproducts;

public class ImportProduct implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Label(value = "Id")
    private java.lang.String id;
    @org.kie.api.definition.type.Label(value = "Category")
    private java.lang.String category;
    @org.kie.api.definition.type.Label(value = "Status")
    private java.lang.String status;
    @org.kie.api.definition.type.Label(value = "Price")
    private java.lang.Double price;

    public ImportProduct() {
    }

    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getCategory() {
        return this.category;
    }

    public void setCategory(java.lang.String category) {
        this.category = category;
    }

    public java.lang.String getStatus() {
        return this.status;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public java.lang.Double getPrice() {
        return this.price;
    }

    public void setPrice(java.lang.Double price) {
        this.price = price;
    }

    public ImportProduct(java.lang.String id, java.lang.String category,
                         java.lang.String status, java.lang.Double price) {
        this.id = id;
        this.category = category;
        this.status = status;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", category=" + category + ", status=" + status + ", price=" + price + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImportProduct) {
            return ((ImportProduct) obj).id.equals(id);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Integer.parseInt(this.id);
    }
}
