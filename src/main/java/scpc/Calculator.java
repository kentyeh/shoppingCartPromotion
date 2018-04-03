package scpc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scpc.model.BonusItem;
import scpc.model.CurrentItem;
import scpc.model.IChainRule;
import scpc.model.IItem;
import scpc.model.ILeafRule;
import scpc.model.IRule;
import scpc.model.SingleItem;

/**
 * Calculator of Shopping cart promotion.
 *
 * @author Kent Yeh
 */
public class Calculator {

    private static final Logger logger = LoggerFactory.getLogger(Calculator.class);

    /**
     * Spread shopping cart into single item for iteration.
     * <br>展開購物車以便逐一檢視單個品項
     *
     * @param <T> Type represented of shopping cart item.
     * @param cartItems shopping cart items.
     * @return
     */
    public static <T> List<SingleItem<T>> flat(Set<? extends IItem<T>> cartItems) {
        List<IItem<T>> items = new ArrayList<>(cartItems);
        Collections.sort(items, new Comparator<IItem<T>>() {
            @Override
            public int compare(IItem o1, IItem o2) {
                return o1.getSalePrice() < o2.getSalePrice() ? 1
                        : o1.getSalePrice() == o2.getSalePrice()
                                ? o1.getOriginalPrice() < o2.getOriginalPrice() ? 1
                                        : o1.getOriginalPrice() == o2.getOriginalPrice() ? 0 : -1 : -1;
            }
        });
        int idx = 1;
        List<SingleItem<T>> flats = new ArrayList<>();
        for (IItem item : items) {
            for (int i = 0; i < item.getQuantity(); i++) {
                SingleItem si = new SingleItem(item, idx++);
                si.setSerialLast(i == item.getQuantity() - 1);
                flats.add(si);
            }
        }
        return flats;
    }

    /**
     * Extract real cart items from a collection.
     * <br>取出集合中真正的購物品項
     *
     * @param <T> type of real cart item.
     * @param items those wrapped shopping cart items.
     * @return real cart items
     */
    public static <T> List<T> purification(Collection<? extends IItem<T>> items) {
        List<T> res = new ArrayList<>(items.size());
        for (IItem<T> item : items) {
            res.add(item.as());
        }
        return res;
    }

    /**
     * Caculate bonus from rules and shopping cart items.
     * <br>從規則與購物品項計算出優惠
     *
     * @param <T> type of real cart item.
     * @param rules Those rules will be applied to caculation.
     * @param cartItems Shopping cart items
     * @return Bonus from rules and items.
     * @throws ScriptException Error prone when quantity measure from a
     * {@link ILeafRule} meet an error.
     */
    public static <T> Collection<BonusItem<T>> calcBonus(List<? extends IRule<T>> rules, Set<? extends IItem<T>> cartItems) throws ScriptException {
        Map<Serializable, BonusItem<T>> bonuses = new HashMap<>();
        Collections.sort(rules, new Comparator<IRule<T>>() {

            @Override
            public int compare(IRule<T> o1, IRule<T> o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        List<SingleItem<T>> items = flat(cartItems);
        for (int i = 0; i < rules.size(); i++) {
            IRule<T> rule = rules.get(i);
            logger.debug("[{}] is going to apply.", rule);
            if (iterate(rule, items, bonuses) && i != rules.size() - 1) {
            }
        }
        return bonuses.values();
    }

    private static <T> void unlockItems(Collection<SingleItem<T>> items) {
        for (SingleItem<T> item : items) {
            item.setExclusiveLock(false);
        }
        items.clear();
    }

    private static <T> boolean iterate(IRule<T> rule, List<SingleItem<T>> cartItems, Map<Serializable, BonusItem<T>> bonuses) throws ScriptException {
        return iterate(rule, cartItems, bonuses, false);
    }

    private static <T> boolean iterate(IRule<T> rule, List<SingleItem<T>> cartItems, Map<Serializable, BonusItem<T>> bonuses, boolean backOnTriggered) throws ScriptException {
        List<SingleItem<T>> lockedItems = new ArrayList<>();
        boolean hasPromote = false;
        if (rule.isLeaf()) {
            ILeafRule<T> lrule = ((ILeafRule<T>) rule);
            int idx = 0;
            IItem preitem = null;
            for (SingleItem<T> item : cartItems) {
                if (!item.isExclusiveLock() && rule.contains(item)) {
                    BigDecimal op = BigDecimal.valueOf(item.getOriginalPrice()).setScale(rule.getPriceScale(), RoundingMode.HALF_EVEN);
                    BigDecimal sp = BigDecimal.valueOf(item.getSalePrice()).setScale(rule.getPriceScale(), RoundingMode.HALF_EVEN);
                    logger.debug("\t inspect [{}-{}].", ++idx, item);
                    lockedItems.add(item.setExclusiveLock(true));
                    rule.containsCountInc().serialNumInc(item.getItem().equals(preitem)).sumOfContainsOriginalPriceInc(op).sumOfContainsSalePriceInc(sp)
                            .sumOfSerialOriginalPriceInc(op).sumOfSerialSalePriceInc(sp);
                    if (rule.isTriggered(item)) {
                        double quantity = lrule.evalQuantity();
                        if (quantity > 0) {
                            IItem<T> ruleBonus = lrule.getBonus();
                            Serializable primaryKey = String.format("%d-%s", rule.hashCode(), CurrentItem.isCurrent(ruleBonus) ? item.getIdentity() : ruleBonus.getIdentity());
                            BonusItem<T> bonus = bonuses.get(primaryKey);
                            if (bonus == null) {
                                IRule<T> topMostRule = rule.getPrevious() == null ? rule : rule.getPrevious();
                                while (topMostRule.getPrevious() != null) {
                                    topMostRule = topMostRule.getPrevious();
                                }
                                bonus = new BonusItem<>(topMostRule, CurrentItem.isCurrent(ruleBonus) ? lrule.getCurrentAsBonus(item) : lrule.getBonus());
                                bonuses.put(primaryKey, bonus);
                                logger.debug("init bonus[{}:{}]", primaryKey, bonus);
                            }
                            if (lrule.isLastQuantityOnly()) {
                                bonus.setFracQuantity(quantity);
                            } else {
                                bonus.incFracQuantity(quantity);
                            }
                            lockedItems.clear();
                            hasPromote = true;
                            if (hasPromote && backOnTriggered) {
                                break;
                            }
                        }
                    }
                    if (item.isSerialLast()) {
                        rule.resetSumOfSerialOriginalPrice();
                        rule.resetSumOfSerialSalePrice();
                        preitem = null;
                    } else {
                        preitem = item.getItem();
                    }
                }
            }
        } else {
            int idx = 0;
            for (SingleItem<T> item : cartItems) {
                if (!item.isExclusiveLock() && rule.contains(item)) {
                    BigDecimal op = BigDecimal.valueOf(item.getOriginalPrice()).setScale(rule.getPriceScale(), RoundingMode.HALF_EVEN);
                    BigDecimal sp = BigDecimal.valueOf(item.getSalePrice()).setScale(rule.getPriceScale(), RoundingMode.HALF_EVEN);
                    logger.debug("\t inspect [{}-{}].", ++idx, item);
                    lockedItems.add(item.setExclusiveLock(true));
                    rule.containsCountInc().sumOfContainsOriginalPriceInc(op).sumOfContainsSalePriceInc(sp)
                            .sumOfSerialOriginalPriceInc(op).sumOfSerialSalePriceInc(sp);
                    if (rule.isTriggered(item)) {
                        if (iterate(((IChainRule<T>) rule).getNext(), cartItems, bonuses, true)) {
                            lockedItems.clear();
                            hasPromote = true;
                            if (hasPromote && backOnTriggered) {
                                break;
                            }
                        }
                    }
                    if (item.isSerialLast()) {
                        rule.resetSumOfSerialOriginalPrice();
                        rule.resetSumOfSerialSalePrice();
                    }
                }
                if (hasPromote) {
                    rule.resetSumOfPrice();
                }
            }
        }
        unlockItems(lockedItems);
        if (hasPromote && backOnTriggered) {
            rule.resetSumOfPrice();
        }
        return hasPromote;
    }
}
