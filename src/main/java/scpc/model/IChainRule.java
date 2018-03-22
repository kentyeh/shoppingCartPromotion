package scpc.model;

/**
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public interface IChainRule<T> extends IRule<T> {

    /**
     * A chained-rule need to know where the next.
     * <br>下一個配對規則的.
     *
     * @return The next rule of a chained-rule.
     */
    public IRule<T> getNext();

}
