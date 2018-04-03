package scpc.model;

import java.math.BigDecimal;
import javax.script.ScriptException;

/**
 * Promotion rule which could be applicable to shopping cart.
 * <br>促眅規則
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public interface IRule<T> {

    /**
     * Priority, the smaller the value the higher the priority.
     * <br>優先順序，數值越小越優先
     *
     * @return priority of rules.
     */
    public int getPriority();

    /**
     * Detecting item whether is applicable to this rule or not.
     * <br>購物車品項是否適用於此規則
     *
     * @param item object in shopping cart.
     * @return
     */
    public boolean contains(IItem<T> item);

    /**
     * Determined iterable items if matched.
     * <br>走訪購物車品項時決定是否觸發的公式
     *
     * @param item cart item
     * @return true if item matched.
     * @throws javax.script.ScriptException
     */
    public boolean isTriggered(SingleItem<T> item) throws ScriptException;

    /**
     * Whether this rule is a leaf rule or not.
     * <br>是否為最未端規則
     *
     * @return true if this rule is terminal.
     */
    public boolean isLeaf();

    /**
     * Previous rule.
     * <br>串連的前規則
     *
     * @return previus link rule;
     */
    public IChainRule<T> getPrevious();

    /**
     * 價格精度
     *
     * @return price number scale.
     */
    public int getPriceScale();

    /**
     * Increase one count of items that is applicable to this rule.
     * <br>增加符合此規則的品項計次
     *
     * @return this rule
     */
    public IRule<T> containsCountInc();

    /**
     * 符合此規則的品項數量
     *
     * @return number of items that could applicable to this rule.
     */
    public int getContainsCount();

    /**
     *
     * @param doAdd true to add one to the serial number otherwise reset serial number to one.
     * @return this rule
     */
    public IRule<T> serialNumInc(boolean doAdd);

    /**
     * 目前走訪商品之同品項物品的編號(從1起算)
     *
     * @return n'th item of same serial items current visited.(Starting from 1)
     */
    public int getSerialNum();

    /**
     * Increase the summary sale price of applicable items.
     * <br>增加符合此規則的品項售價小計
     *
     * @param salePrice
     * @return
     */
    public IRule<T> sumOfContainsSalePriceInc(BigDecimal salePrice);

    /**
     * 符合此規則的品項售價小計
     *
     * @return The summary sale price of applicable items.
     */
    public BigDecimal getSumOfContainsSalePrice();

    /**
     * Increase the summary original price of applicable items.
     * <br>增加符合此規則的品項原價小計
     *
     * @param salePrice
     * @return
     */
    public IRule<T> sumOfContainsOriginalPriceInc(BigDecimal salePrice);

    /**
     * 符合此規則的品項原價小計
     *
     * @return The summary original price of applicable items.
     */
    public BigDecimal getSumOfContainsOriginalPrice();

    /**
     * Increase the summary sale price of the same applicable items.
     * <br>增加符合此規則的同品項物品售價小計
     *
     * @param saleprice
     * @return this rule.
     */
    public IRule<T> sumOfSerialSalePriceInc(BigDecimal saleprice);

    /**
     * 符合此規則的同品項售價小計
     *
     * @return The summary sale price of the same applicable items.
     */
    public BigDecimal getSumOfSerialSalePrice();

    /**
     * Reset the summary sale price of the same applicable items.
     *
     * @return this rule.
     */
    public IRule<T> resetSumOfSerialSalePrice();

    /**
     * Increase the summary oringal price of the same applicable items.
     * <br>增加符合此規則的同品項原價小計
     *
     * @param oriprice original price
     * @return this rule
     */
    public IRule<T> sumOfSerialOriginalPriceInc(BigDecimal oriprice);

    /**
     * 符合此規則的同品項原價小計
     *
     * @return The summary oringal price of the same applicable items.
     */
    public BigDecimal getSumOfSerialOriginalPrice();

    /**
     * Reset the summary original price of the same applicable items.
     *
     * @return this rule
     */
    public IRule<T> resetSumOfSerialOriginalPrice();

    /**
     * Reset sumary price to zero <br>
     * 重置所有價格累計為零
     */
    public void resetSumOfPrice();
}
