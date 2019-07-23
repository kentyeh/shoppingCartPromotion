package scpc.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represent current visit item as bonus.
 * <br>表示當下走访的品項就是優惠品項
 *
 * @author Kent Yeh
 */
public class CurrentItem implements IItem {

    private static final CurrentItem CI = new CurrentItem();

    public static CurrentItem getInstance() {
        return CI;
    }

    private CurrentItem() {
    }

    @Override
    public Serializable getIdentity() {
        return "CurrentItem";
    }

    @Override
    public CurrentItem as() {
        return this;
    }

    @Override
    public long getQuantity() {
        return 1;
    }

    @Override
    public void setQuantity(long quantity) {

    }

    @Override
    public double getSalePrice() {
        return 0;
    }

    @Override
    public double getRegularPrice() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CurrentItem other = (CurrentItem) obj;
        return Objects.equals(getIdentity(), other.getIdentity());
    }

    @Override
    public int hashCode() {
        return "6d0c40fb-e3a1-49d7-bcfd-b60efb0f08d0".hashCode();
    }

    public static boolean isCurrent(Object obj) {
        return CI.hashCode() == obj.hashCode();
    }

}
