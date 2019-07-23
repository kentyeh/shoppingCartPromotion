package scpc.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * 購物車品項
 *
 * @author kent
 */
public class CartItem implements IItem<CartItem>, Serializable {

    private static final long serialVersionUID = 4497605167531430528L;

    private String productId;
    private String productDesc;
    private double regularPrice;
    private double salePrice;
    private long quantity = 0;

    public CartItem(String productId, String productDesc, double regularPrice, double salePrice) {
        this.productId = productId;
        this.productDesc = productDesc;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
    }

    @Override
    public Serializable getIdentity() {
        return productId;
    }

    @Override
    public CartItem as() {
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    @Override
    public double getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(double regularPrice) {
        this.regularPrice = regularPrice;
    }

    @Override
    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    @Override
    public long getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setAmount(int amount) {
        this.quantity = amount;
    }

    public CartItem buy(int amount) {
        setAmount(amount);
        return this;
    }

    public CartItem items() {
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.productId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final CartItem other = (CartItem) obj;
        return Objects.equals(this.productId, other.productId);
    }

    @Override
    public String toString() {
        NumberFormat nf = new DecimalFormat("'$'#,###.00");
        return String.format("[%s]%s %s  × %d",
                productId, productDesc, regularPrice == salePrice ? nf.format(salePrice)
                        : nf.format(regularPrice) + " ↘" + nf.format(salePrice),
                quantity);
    }

}
