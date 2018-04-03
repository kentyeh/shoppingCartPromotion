package scpc.model.support;

import java.math.BigDecimal;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scpc.Calculator;
import scpc.model.IChainRule;
import scpc.model.IRule;
import scpc.model.SingleItem;

/**
 * Implement basic function of {@link IRule}
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public abstract class AbstractRuleBase<T> implements IRule<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRuleBase.class);

    private int containsCount = 0;
    private int serialNum = 1;
    private BigDecimal containsSumOfOriginalPrice = BigDecimal.ZERO;
    private BigDecimal containsSumOfSalePrice = BigDecimal.ZERO;
    private BigDecimal sumOfSerialOriginalPrice = BigDecimal.ZERO;
    private BigDecimal serialSumOfSalePrice = BigDecimal.ZERO;
    private IChainRule<T> previousRule = null;
    private String triggerFormula = "";
    private String quantityFormula = "";
    private EvalHelper evalHelper = null;

    /**
     * A trigger formula is evaluated to determine whether to get a bonus or
     * not.
     * <br>走訪品項時決定是否觸發取得優惠品項的決策公式
     *
     * @return
     */
    public String getTriggerFormula() {
        return triggerFormula;
    }

    /**
     * Setting a trigger formula is evaluated to determine whether to get a
     * bonus or not.
     *
     * @param triggerFormula
     */
    public void setTriggerFormula(String triggerFormula) {
        this.triggerFormula = triggerFormula;
    }

    /**
     * When triggered, a formula to check how many quantities should offer.
     * <br>品項走訪觸發後，以公式決定優惠品項的數量
     *
     * @return
     */
    public String getQuantityFormula() {
        return isLeaf() ? quantityFormula : null;
    }

    /**
     * Setting a formula to check how many quantities should offer after
     * triggered.
     *
     * @param quantityFormula
     */
    public void setQuantityFormula(String quantityFormula) {
        this.quantityFormula = quantityFormula;
    }

    /**
     * @see IRule#getPrevious()
     * @return
     */
    @Override
    public IChainRule<T> getPrevious() {
        return previousRule;
    }

    /**
     * Set previous rule if exists.
     * <br>如果是串連規則，設定前串連規則
     *
     * @param previousRule previous rule
     */
    public void setPrevious(IChainRule<T> previousRule) {
        this.previousRule = previousRule;
    }

    /**
     * for {@link Calculator} use only.
     *
     * @return
     */
    @Override
    public IRule<T> containsCountInc() {
        containsCount++;
        return this;
    }

    /**
     * @see IRule#getContainsCount()
     * @return
     */
    @Override
    public int getContainsCount() {
        return containsCount;
    }

    /**
     * @see IRule#serialNumInc(boolean)
     * @param doAdd
     * @return
     */
    @Override
    public IRule<T> serialNumInc(boolean doAdd) {
        if (doAdd) {
            serialNum++;
        } else {
            serialNum = 1;
        }
        return this;
    }

    /**
     * @see IRule#getSerialNum()
     * @return
     */
    @Override
    public int getSerialNum() {
        return serialNum;
    }

    /**
     * @see IRule#sumOfContainsOriginalPriceInc(java.math.BigDecimal)
     * @param originalPrice
     * @return
     */
    @Override
    public IRule<T> sumOfContainsOriginalPriceInc(BigDecimal originalPrice) {
        containsSumOfOriginalPrice = containsSumOfOriginalPrice.add(originalPrice);
        return this;
    }

    /**
     * @see IRule#sumOfContainsSalePriceInc(java.math.BigDecimal)
     * @param salePrice
     * @return
     */
    @Override
    public IRule<T> sumOfContainsSalePriceInc(BigDecimal salePrice) {
        containsSumOfSalePrice = containsSumOfSalePrice.add(salePrice.setScale(getPriceScale()));
        return this;
    }

    /**
     * @see IRule#getSumOfContainsOriginalPrice()
     * @return
     */
    @Override
    public BigDecimal getSumOfContainsOriginalPrice() {
        return containsSumOfOriginalPrice;
    }

    /**
     * @see IRule#getSumOfContainsSalePrice()
     * @return
     */
    @Override
    public BigDecimal getSumOfContainsSalePrice() {
        return containsSumOfSalePrice;
    }

    /**
     * @see IRule#getSumOfSerialOriginalPrice()
     * @return
     */
    @Override
    public BigDecimal getSumOfSerialOriginalPrice() {
        return sumOfSerialOriginalPrice;
    }

    /**
     * @see IRule#getSumOfSerialSalePrice()
     * @return
     */
    @Override
    public BigDecimal getSumOfSerialSalePrice() {
        return serialSumOfSalePrice;
    }

    /**
     * Setting the summary sale price of the same applicable items.
     * <br>設定符合此規則的同品項售價小計
     *
     * @param serialSumOfSalePrice
     */
    public void setSerialSumOfSalePrice(BigDecimal serialSumOfSalePrice) {
        this.serialSumOfSalePrice = serialSumOfSalePrice;
    }

    /**
     * @see IRule#sumOfSerialOriginalPriceInc(java.math.BigDecimal)
     * @param saleprice
     * @return
     */
    @Override
    public IRule<T> sumOfSerialOriginalPriceInc(BigDecimal saleprice) {
        this.sumOfSerialOriginalPrice = this.sumOfSerialOriginalPrice.add(saleprice.setScale(getPriceScale()));
        return this;
    }

    /**
     * @see IRule#sumOfSerialSalePriceInc(java.math.BigDecimal)
     * @param saleprice
     * @return
     */
    @Override
    public IRule<T> sumOfSerialSalePriceInc(BigDecimal saleprice) {
        this.serialSumOfSalePrice = this.serialSumOfSalePrice.add(saleprice.setScale(getPriceScale()));
        return this;
    }

    /**
     * @see IRule#resetSumOfSerialOriginalPrice()
     * @return
     */
    @Override
    public IRule<T> resetSumOfSerialOriginalPrice() {
        this.sumOfSerialOriginalPrice = BigDecimal.ZERO;
        return this;
    }

    /**
     * @see IRule#resetSumOfSerialSalePrice()
     * @return
     */
    @Override
    public IRule<T> resetSumOfSerialSalePrice() {
        this.serialSumOfSalePrice = BigDecimal.ZERO;
        return this;
    }

    @Override
    public void resetSumOfPrice() {
        containsSumOfOriginalPrice = BigDecimal.ZERO;
        containsSumOfSalePrice = BigDecimal.ZERO;
        sumOfSerialOriginalPrice = BigDecimal.ZERO;
        serialSumOfSalePrice = BigDecimal.ZERO;
    }

    /**
     * A formula evaluator to help analysis.
     * <br>公式解譯工具
     *
     * @return
     */
    public EvalHelper getEvalHelper() {
        return evalHelper;
    }

    public void setEvalHelper(EvalHelper evalHelper) {
        this.evalHelper = evalHelper;
    }

    /**
     * @see IRule#isTriggered(scpc.model.SingleItem)
     * @param item
     * @return
     * @throws ScriptException
     */
    @Override
    public boolean isTriggered(SingleItem<T> item) throws ScriptException {
        if (contains(item)) {
            if (evalHelper == null) {
                throw new RuntimeException("Not any evalHelper already setting yet!");
            } else {
                boolean res = Boolean.TRUE.equals(evalHelper.bindVaribles(this, item).eval(getTriggerFormula()));
                logger.debug("eval(\"{}\") = {}", getTriggerFormula(), res);
                return res;
            }
        } else {
            logger.debug("not triggered owing not counts");
            return false;
        }
    }

    /**
     * Quantity measure of bonus after {@link #isTriggered(scpc.model.SingleItem)
     * }.
     * * <br> 觸發觸發後，優惠品項的數量估值
     *
     * @return
     * @throws ScriptException
     */
    public double evalQuantity() throws ScriptException {
        if (isLeaf()) {
            if (evalHelper == null) {
                throw new RuntimeException("Not any evalHelper already setting yet!");
            } else {
                Number mb = (Number) evalHelper.eval(getQuantityFormula());
                logger.debug("evail quantity(\"{}\")={}", getQuantityFormula(), mb);
                return mb.doubleValue();
            }
        } else {
            return 0d;
        }
    }

}
