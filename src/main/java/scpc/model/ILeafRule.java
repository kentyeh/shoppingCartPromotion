package scpc.model;

import javax.script.ScriptException;

/**
 * Promotion rule that result in bonus item.
 * <br>定義優惠的規則
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public interface ILeafRule<T> extends IRule<T> {

    /**
     * 優惠品項.
     *
     * @return Instance of Bonus.
     */
    public IItem<T> getBonus();

    /**
     * Current visit item is bonus item.
     * <br>優惠品項就是當下走訪的品項
     *
     * @param item current visit item.
     * @return Clone item when returning bonus is current visit item.
     */
    public IItem<T> getCurrentAsBonus(IItem<T> item);

    /**
     * Quantity measure of bonus after {@link IRule#isTriggered(scpc.model.SingleItem)
     * }.
     * <br> 觸發觸發後，優惠品項的數量估值
     *
     * @return evulate quantity of bonus
     * @throws ScriptException Error prone when quantity measure meet an error.
     */
    public double evalQuantity() throws ScriptException;

    /**
     * Only the last measure set to quantity.<br>
     * <br>只承認最後一次的數量估值
     *
     * @return whether the last measure set to quantity.
     */
    public boolean isLastQuantityOnly();

}
