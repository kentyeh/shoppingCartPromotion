package scpc.model;

/**
 *
 * @author kent
 */
public class OneCentDiscount extends CartItem {

    private static final long serialVersionUID = 7580055420763360265L;

    public OneCentDiscount() {
        super("-0.01", "one cent discount", -.01, -.01);
    }

}
