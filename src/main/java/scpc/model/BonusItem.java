package scpc.model;

import java.io.Serializable;
import java.util.Objects;
import scpc.Calculator;

/**
 * Bonus item of promotions.
 * <br>優惠品項
 *
 * @author Kent Yeh
 * @param <T> type of real bonus item.
 */
public class BonusItem<T> implements IItem<T> {

    private final IRule<T> rule;
    private final IItem<T> item;

    private double fracQuantity = 0d;

    public BonusItem(IRule<T> rule, IItem<T> item) {
        this.rule = rule;
        this.item = item;
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
     * The quantity of bonus.
     * <br>優惠數量
     *
     * @return The quantity of bonus.
     */
    @Override
    public long getQuantity() {
        return Math.round(Math.floor(this.fracQuantity));
    }

    /**
     * Not supported.
     * <br>方法不可使用
     *
     * @param quantity
     */
    @Override
    public void setQuantity(long quantity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * The assoicate rule of this bonus.
     * <br>產生本優惠的規類
     *
     * @return The assoicate rule of this bonus.
     */
    public IRule<T> getRule() {
        return rule;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @return
     */
    public double getFracQuantity() {
        return fracQuantity;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @param fracQuantity
     * @return
     */
    public BonusItem<T> incFracQuantity(double fracQuantity) {
        this.fracQuantity += fracQuantity;
        item.setQuantity(getQuantity());
        return this;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @param fracQuantity
     */
    public void setFracQuantity(double fracQuantity) {
        this.fracQuantity = fracQuantity;
        item.setQuantity(getQuantity());
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
     * @see IItem#getRegularPrice()
     * @return
     */
    @Override
    public double getRegularPrice() {
        return item.getRegularPrice();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.rule);
        hash = 11 * hash + Objects.hashCode(this.item);
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
        final BonusItem other = (BonusItem) obj;
        return Objects.equals(this.item.as(), other.as())
                && getQuantity() == other.getQuantity();
    }

    @Override
    public String toString() {
        return item.toString();
    }

}
