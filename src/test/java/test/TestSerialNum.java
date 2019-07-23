package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.script.ScriptException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import scpc.Calculator;
import scpc.model.BonusItem;
import scpc.model.CartItem;
import scpc.model.CurrentItem;
import scpc.model.IItem;
import scpc.model.IRule;
import scpc.model.IChainRule;
import scpc.model.LiquidPaper;
import scpc.model.OneCentDiscount;
import scpc.model.PrecisionCompass;
import scpc.model.Stapler;
import scpc.model.WaterSolubleBrightPinkFabricMarkerPen;
import scpc.model.WaterSolublePurpleFabricMarkerPen;
import scpc.rule.BaseRule;
import static test.TestBase.SHOW_CART;

/**
 *
 * @author Kent Yeh //
 */
@Test(groups = "debug")
public class TestSerialNum extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestSerialNum.class);

    @Parameters(SHOW_CART)
    public void testBuy2SamePensGetFreeOffer(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(5);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(5);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(3));
        cartItems.add(new Stapler().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("Buy two same pens get free one offer!", BaseRule.JS_EVAL, "NN%2==0", "1", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return CurrentItem.getInstance();
                    }
                });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        List<CartItem> itemList = Calculator.purification(bonuses);
        assertThat("can't found purle pen free offer!", itemList, hasItem(purplePen));
        assertThat("can't found birhgt pink pen free offer!", itemList, hasItem(briPinkPen));
        long quantity = 0;
        for (CartItem bonus : itemList) {
            quantity += bonus.getQuantity();
        }
        assertThat("Should got 4 pens", quantity, is(equalTo(4l)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy2SamePensGetSame1FreeOffer", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testBuy2SamePensSave3Cents(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(5);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(5);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(3));
        cartItems.add(new Stapler().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("Buy two same pens save 3 cents!", BaseRule.SPEL_EVAL, "NN%2==0", "3", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return new OneCentDiscount();
                    }
                });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        List<CartItem> itemList = Calculator.purification(bonuses);
        assertThat("No saveing cents found.", itemList, hasItem(new OneCentDiscount()));
        assertThat("Should save 6 cents", itemList.get(0).getQuantity(), is(equalTo(12l)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy2SamePensSave3Cents", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void test1StaplerWith2SamePensSave14cents(@Optional String showCart) throws ScriptException {
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(new WaterSolubleBrightPinkFabricMarkerPen().buy(3));
        cartItems.add(new WaterSolublePurpleFabricMarkerPen().buy(3));
        cartItems.add(new Stapler().buy(2));
        cartItems.add(new LiquidPaper().buy(1));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add((IChainRule<CartItem>) new BaseRule("Buy 1 Stapler with 2 Same Pens Save 14 Cents!", BaseRule.JS_EVAL, false, false, "true", "", 0, Stapler.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }.setNext(new BaseRule("", BaseRule.SPEL_EVAL, "NN%2==0", "14", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return new OneCentDiscount();
                    }
                }));
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        assertThat("Should save 28 cents", bonuses.iterator().next().getQuantity(), is(equalTo(28l)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestSerialNumMixRules", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void test2SamePensGetRegularPrice30PctDiscount(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(5);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(5);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(3));
        cartItems.add(new Stapler().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("2 Same Pens Get RegPrice 30% discount!", BaseRule.SPEL_EVAL, "NN%2==0",
                "(SP*10-OP*7)*2", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return new OneCentDiscount();
                    }
                });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        long saving = Math.round(Math.floor(
                (briPinkPen.getSalePrice() + purplePen.getSalePrice()) * 4 * 10
                - (briPinkPen.getRegularPrice() + purplePen.getRegularPrice()) * 4 * 7));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("Test2ndPenGetRegularPrice30PctDiscount", cartItems, bonuses));
        }
        rules.clear();
        rules.add(new BaseRule("2nd Pen Get SalePrice 30% discount!", BaseRule.SPEL_EVAL, "NN%2==0",
                "SP*2*3", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return new OneCentDiscount();
                    }
                });
        bonuses = Calculator.calcBonus(rules, cartItems);
        saving = Math.round(Math.floor((briPinkPen.getSalePrice() + purplePen.getSalePrice()) * 4 * 3));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("Test2ndPenGetSalePrice30PctDiscount", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testBuyGreaterThen$10StationeryBundle2SamePensThen2PensGet10PctDiscount(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(3);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(3);
        CartItem stapler = new Stapler().buy(1);
        CartItem compass = new PrecisionCompass().buy(1);
        CartItem liquidPaper = new LiquidPaper().buy(1);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(stapler);
        cartItems.add(compass);
        cartItems.add(liquidPaper);
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("$10↗ stationery bundle 2 Same Pens,2 same pens get 10% discount.", BaseRule.JS_EVAL, false, false,
                "SP>=10", "", 0, Stapler.CODE, PrecisionCompass.CODE, LiquidPaper.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }.setNext(new BaseRule("", BaseRule.SPEL_EVAL, "NN%2==0", "SSSP", 0,
                                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return new OneCentDiscount();
                    }
                }));
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        long saving = Math.round(Math.floor(briPinkPen.getSalePrice() * 2 + purplePen.getSalePrice() * 2));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuyGreaterThen$10StationeryWith2SamePensAnd2PensGet10PctDiscount", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testSerialNumMixRules(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(3);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(3);
        CartItem stapler = new Stapler().buy(3);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(stapler);
        cartItems.add(new LiquidPaper().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("Buy two same pens get free one offer!", BaseRule.JS_EVAL, "NN%2==0", "1", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
                    @Override
                    public IItem<CartItem> getBonus() {
                        return CurrentItem.getInstance();
                    }

                });
        rules.add(new BaseRule("Buy two same staplers save 9 cents", BaseRule.SPEL_EVAL, "NN%2==0", "9", 1, Stapler.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        assertThat("Totally should got 3 free offer!", bonuses.size(), is(equalTo(3)));
        List<CartItem> itemList = Calculator.purification(bonuses);
        assertThat("can't found purle pen free offer!", itemList, hasItem(purplePen));
        assertThat("can't found birhgt pink pen free offer!", itemList, hasItem(briPinkPen));
        assertThat("Should save 9 cents", filter(bonuses, rules.get(1), new OneCentDiscount()).getQuantity(), is(equalTo(9l)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestSerialNumMixRules", cartItems, bonuses));
        }
    }
}
