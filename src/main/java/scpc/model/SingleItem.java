package scpc.model;

import java.io.Serializable;
import java.util.Objects;
import scpc.Calculator;

/**
 * 購物車內的單一物品<br>
 * Present a single item of shopping cart.
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public class SingleItem<T> implements IItem<T> {

    private final IItem<T> item;
    private final int sequenceNum;
    private boolean serialLast = false;
    private boolean exclusiveLock = false;

    public SingleItem(IItem<T> item, int sequenceNum) {
        this.item = item;
        this.sequenceNum = sequenceNum;
    }

    /**
     * @see IItem#getIdentity()
     * @return
     */
    @Override
    public Serializable getIdentity() {
        return item.getIdentity();
    }

    /**
     * @see IItem#as()
     * @return
     */
    @Override
    public T as() {
        return item.as();
    }

    /**
     * @see IItem#getQuantity()
     * @return
     */
    @Override
    public long getQuantity() {
        return 1;
    }

    /**
     * @see IItem#setQuantity(long)
     * @param quantity
     */
    @Override
    public void setQuantity(long quantity) {

    }

    /**
     * @see IItem#getSalePrice()
     * @return
     */
    @Override
    public double getSalePrice() {
        return item.getSalePrice();
    }

    /**
     * @see IItem#getOriginalPrice()
     * @return
     */
    @Override
    public double getOriginalPrice() {
        return item.getOriginalPrice();
    }

    /**
     * 購物車裡的物品<br>
     *
     * @return a Shopping cart item.
     */
    public IItem<T> getItem() {
        return item;
    }

    /**
     * 排序編號
     *
     * @return ordered sequence number of item.
     */
    public int getSequenceNum() {
        return sequenceNum;
    }

    /**
     * 是否為同品項物品的最後一項
     *
     * @return the last item of same serial items.
     */
    public boolean isSerialLast() {
        return serialLast;
    }

    /**
     * Detect whether the current visit item is the last item of the same series
     * or not.
     * <br>當下走访品項是否為同系列中的最後一個
     *
     * @param serialLast
     */
    public void setSerialLast(boolean serialLast) {
        this.serialLast = serialLast;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @return
     */
    public boolean isExclusiveLock() {
        return exclusiveLock;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @param exclusiveLock
     * @return
     */
    public SingleItem<T> setExclusiveLock(boolean exclusiveLock) {
        this.exclusiveLock = exclusiveLock;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.sequenceNum;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleItem<?> other = (SingleItem<?>) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return this.sequenceNum == other.sequenceNum;
    }

    @Override
    public String toString() {
        return "singularize \"" + sequenceNum + "." + item.toString() + "\"";
    }

}
