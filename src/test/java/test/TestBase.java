package test;

import java.util.Collection;
import java.util.Set;
import scpc.model.BonusItem;
import scpc.model.CartItem;
import scpc.model.IItem;
import scpc.model.IRule;

/**
 *
 * @author Kent Yeh
 */
public class TestBase {

    public static final String SHOW_CART = "showCart";

    public <T extends BonusItem> BonusItem filter(Collection<T> bonuses, IRule rule, IItem item) {
        for (BonusItem bonus : bonuses) {
            if (bonus.as().equals(item.as()) && bonus.getRule().equals(rule)) {
                return bonus;
            }
        }
        return null;
    }

    public <T extends IItem> IItem summary(Collection<T> cartitems) {
        double sop = 0d, ssp = 0d;
        long sq = 0;
        for (IItem item : cartitems) {
            sop += item.getRegularPrice() * item.getQuantity();
            ssp += item.getSalePrice() * item.getQuantity();
            sq += item.getQuantity();
        }
        CartItem res = new CartItem("Summary", "Shopping Cart summary", sop, ssp);
        res.setQuantity(sq);
        return res;
    }

    public String limit(String s, int size) {
        if (s == null || s.isEmpty()) {
            return "";
        } else if (s.length() > size) {
            return s.substring(0, size - 2) + "..";
        } else {
            return s;
        }
    }

    public String center(String s, int size) {
        if (s == null || s.isEmpty()) {
            return String.format("%" + size + "s", " ");
        } else if (s.length() == size) {
            return s;
        } else if (s.length() > size) {
            return s.substring(0, size - 2) + "..";
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(' ');
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public String left(String s, int size) {
        if (s == null || s.isEmpty()) {
            return String.format("%" + size + "s", " ");
        } else if (s.length() == size) {
            return s;
        } else if (s.length() > size) {
            return s.substring(0, size - 2) + "..";
        } else {
            return String.format("%-" + size + "s", s);
        }
    }

    public String right(String s, int size) {
        if (s == null || s.isEmpty()) {
            return String.format("%" + size + "s", " ");
        } else if (s.length() == size) {
            return s;
        } else if (s.length() > size) {
            return s.substring(0, size);
        } else {
            return String.format("%" + size + "s", s);
        }
    }

    public String right(double f, int size, int scale) {
        String s = String.format("%" + size + "." + scale + "f", f);
        return s.length() > size ? s.substring(0, size) : s;
    }

    public StringBuilder dashLine(StringBuilder sb, int size) {
        for (int i = 0; i < size; i++) {
            sb.append("-");
        }
        return sb;
    }

    public String showCart(String title, Set<IItem<CartItem>> cartitems, Collection<BonusItem<CartItem>> bonuses) {
        int len = 100;
        int numLen = 7;
        StringBuilder sb = new StringBuilder(center(title, len));
        dashLine(sb.append("\n"), len)
                .append("\n").append(left("identity", 10)).append(" ").append(left("description", 63))
                .append(" ").append(right("RegPric", numLen)).append(" ").append(right("SalPrice", numLen + 1))
                .append(" ").append("quantity");
        dashLine(sb.append("\n"), len);
        double summary = 0;
        for (IItem<CartItem> cartItem : cartitems) {
            CartItem item = cartItem.as();
            summary = summary + item.getSalePrice() * item.getQuantity();
            sb.append("\n").append(left(item.getIdentity().toString(), 10))
                    .append(" ").append(left(item.getProductDesc(), 63))
                    .append(" ").append(right(item.getRegularPrice(), numLen, 2))
                    .append(" ").append(right(item.getSalePrice(), numLen, 2))
                    .append(" x ").append(right(String.format("%,d", item.getQuantity()), 7));
        }
        if (bonuses.size() > 0) {
            dashLine(sb.append("\nBonus"), len - 5);
            for (BonusItem<CartItem> bonus : bonuses) {
                summary = summary + bonus.getSalePrice() * bonus.getQuantity();
                sb.append("\n").append(left(bonus.as().getIdentity().toString(), 10))
                        .append(" ").append(left(limit(bonus.getRule().toString(), 30) + ":" + bonus.as().getProductDesc(), 63))
                        .append(" ").append(right(bonus.as().getRegularPrice(), numLen, 2))
                        .append(" ").append(right(bonus.as().getSalePrice(), numLen, 2))
                        .append(" x ").append(right(String.format("%,d", bonus.getQuantity()), numLen));
            }
            dashLine(sb.append("\n"), len);
        }
        sb.append("\n").append(right(String.format("TOTAL: %.2f", summary), len));
        return sb.toString();
    }
}
