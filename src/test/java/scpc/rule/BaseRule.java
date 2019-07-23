package scpc.rule;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import scpc.model.CartItem;
import scpc.model.IChainRule;
import scpc.model.IItem;
import scpc.model.ILeafRule;
import scpc.model.IRule;
import scpc.model.support.AbstractRuleBase;

/**
 *
 * @author Kent Yeh
 */
public abstract class BaseRule extends AbstractRuleBase<CartItem> implements ILeafRule<CartItem>, IChainRule<CartItem> {

    public static final String JS_EVAL = "JS";
    public static final String SPEL_EVAL = "Spel";

    private final int priority;
    private final boolean lastQuantityOnly;
    private final boolean leaf;
    private final List<String> itemCodes;
    private final String name;
    private IRule<CartItem> next;

    public BaseRule(String name, String evalHeler, String triggerFormula, String quantityFormula, int priority, String... itemcode) {
        this(name, evalHeler, true, false, triggerFormula, quantityFormula, priority, itemcode);
    }

    public BaseRule(String name, String evalHeler, boolean isLeaf, boolean lastQuantityOnly, String triggerFormula, String quantityFormula, int priority, String... itemcode) {
        this.name = name;
        this.leaf = isLeaf;
        this.priority = priority;
        this.lastQuantityOnly = lastQuantityOnly;
        super.setTriggerFormula(triggerFormula);
        super.setQuantityFormula(quantityFormula);
        setEvalHelper("JS".equals(evalHeler) ? JsEvalFactory.getInstance() : SpelEvalFactory.getInstance());
        itemCodes = new ArrayList<>(Arrays.asList(itemcode));
    }

    public <R extends IRule<CartItem>> R as() {
        return (R) this;
    }

    public <R extends IRule<CartItem>> R setNext(IRule<CartItem> next) {
        this.next = next;
        if (AbstractRuleBase.class.isAssignableFrom(next.getClass())) {
            ((AbstractRuleBase) this.next).setPrevious(this);
        }
        return (R) this;
    }

    @Override
    public IRule<CartItem> getNext() {
        return next;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public int getPriceScale() {
        return 2;
    }

    @Override
    public IItem<CartItem> getCurrentAsBonus(IItem<CartItem> item) {
        return new CartItem(item.as().getProductId(), item.as().getProductDesc(), item.as().getRegularPrice(), 0);
    }

    @Override
    public boolean isLastQuantityOnly() {
        return lastQuantityOnly;
    }

    /**
     * Check an item if applicable.
     * <br>檢查品項是否適用規則
     *
     * @param item
     * @return
     */
    @Override
    public boolean contains(IItem<CartItem> item) {
        return itemCodes.contains(item.as().getProductId());
    }

    @Override
    public String toString() {
        return name;
    }
}
