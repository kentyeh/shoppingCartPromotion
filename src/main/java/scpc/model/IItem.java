package scpc.model;

import java.io.Serializable;

/**
 * 購物車裡的項目<br>
 * Present a Shopping cart item.
 *
 * @author Knet Yeh
 * @param <T> type of real cart item.
 */
public interface IItem<T> {

    /**
     * 購物車品項識別，例如 產品代號
     *
     * @return cart item's identity,e.g. product code
     */
    public Serializable getIdentity();

    /**
     * 購物車品項的真實類別
     *
     * @return type of real cart item
     */
    public T as();

    /**
     * 品項的數量
     *
     * @return the quantity of the item.
     */
    public long getQuantity();

    /**
     * 設定品項數量
     *
     * @param quantity of item
     */
    public void setQuantity(long quantity);

    /**
     * 售價
     *
     * @return sale price of item.
     */
    public double getSalePrice();

    /**
     * 原價
     *
     * @return original price of item.
     */
    public double getOriginalPrice();

}
