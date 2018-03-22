package test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;
import scpc.Calculator;
import scpc.model.CartItem;
import scpc.model.IItem;
import scpc.model.SingleItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import scpc.model.LiquidPaper;
import scpc.model.PrecisionCompass;
import scpc.model.Stapler;
import scpc.model.WaterSolubleBrightPinkFabricMarkerPen;
import scpc.model.WaterSolublePurpleFabricMarkerPen;

/**
 *
 * @author Kent Yeh
 */
public class TestFlat {

    @Test(groups = "debug")
    public void TestSpreadCartItems() {
        CartItem stapler = new Stapler().buy(1);
        CartItem liquidPaper = new LiquidPaper().buy(2);
        CartItem precisionCompass = new PrecisionCompass().buy(1);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(2);
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(2);
        Set<IItem<CartItem>> cart = new HashSet<>();
        cart.add(stapler);
        cart.add(liquidPaper);
        cart.add(precisionCompass);
        cart.add(briPinkPen);
        cart.add(purplePen);
        List<SingleItem<CartItem>> flat = Calculator.flat(cart);
        assertThat("Expensive item should be in front.", flat.get(0).getItem().as(), is(equalTo(precisionCompass)));
        assertThat("cheapest item should be in last.", flat.get(flat.size() - 1).getItem().as(), is(equalTo(liquidPaper)));
    }
}
