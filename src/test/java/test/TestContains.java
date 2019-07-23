package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.script.ScriptException;
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
import scpc.model.LiquidPaper;
import scpc.model.OneCentDiscount;
import scpc.model.Stapler;
import scpc.model.WaterSolubleBrightPinkFabricMarkerPen;
import scpc.model.WaterSolublePurpleFabricMarkerPen;
import scpc.rule.BaseRule;
import static test.TestBase.SHOW_CART;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItem;

/**
 *
 * @author Kent Yeh
 */
@Test(groups = "debug")
public class TestContains extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestContains.class);

    @Parameters(SHOW_CART)
    public void testBuy2PensGet1FreeOffer(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(3);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(2);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(2));
        cartItems.add(new Stapler().buy(2));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("Buy two pens get free one offer!", BaseRule.JS_EVAL, "N%2==0", "1", 0,
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
        assertThat("Should got 2 pens", quantity, is(equalTo(2l)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy2PensGet1FreeOffer", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testBuyPensWhen3Then1AndWhen5Then2AndWhen7Then3AndWhen10Then5PctDiscount(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(2);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(2);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(2));
        cartItems.add(new Stapler().buy(1));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("Buy massive pens get different discount!", BaseRule.SPEL_EVAL, true, true, "N>1",
                "SCSP*(N>9?5:N>6?3:N>4?2:N>2?1:0)", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        double spent = purplePen.getSalePrice() * purplePen.getQuantity() + briPinkPen.getSalePrice() * briPinkPen.getQuantity();
        long saving = Math.round(Math.floor(spent));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuyMassivePensGetDiffDiscount", cartItems, bonuses));
        }
        purplePen = purplePen.buy(3);
        briPinkPen = briPinkPen.buy(3);
        rules.clear();
        rules.add(new BaseRule("Buy massive pens get different discount!", BaseRule.JS_EVAL, true, true, "N>1",
                "SCSP*(N>9?5:N>6?3:N>4?2:N>2?1:0)", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        bonuses = Calculator.calcBonus(rules, cartItems);
        spent = purplePen.getSalePrice() * purplePen.getQuantity() + briPinkPen.getSalePrice() * briPinkPen.getQuantity();
        saving = Math.round(Math.floor(spent * 2));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuyMassivePensGetDiffDiscount", cartItems, bonuses));
        }
        purplePen = purplePen.buy(6);
        briPinkPen = briPinkPen.buy(5);
        rules.clear();
        rules.add(new BaseRule("Buy massive pens get different discount!", BaseRule.SPEL_EVAL, true, true, "N>1",
                "SCSP*(N>9?5:N>6?3:N>4?2:N>2?1:0)", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        bonuses = Calculator.calcBonus(rules, cartItems);
        spent = purplePen.getSalePrice() * purplePen.getQuantity() + briPinkPen.getSalePrice() * briPinkPen.getQuantity();
        saving = Math.round(Math.floor(spent * 5));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuyMassivePensGetDiffDiscount", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void test2PensGetPrice30PctDiscount(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(3);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(3);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(3));
        cartItems.add(new Stapler().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("2 Pens Get RegPrice 30% discount!", BaseRule.SPEL_EVAL, true, true, "N%2==0",
                "(SCSP*10-SCOP*7)", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        long saving = Math.round(Math.floor(
                (briPinkPen.getSalePrice() + purplePen.getSalePrice()) * 3 * 10
                - (briPinkPen.getRegularPrice() + purplePen.getRegularPrice()) * 3 * 7));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("Test2PensGetRegularPrice30PctDiscount", cartItems, bonuses));
        }
        rules.clear();
        rules.add(new BaseRule("2 Pens Get SalePrice 30% discount!", BaseRule.SPEL_EVAL, true, true, "N%2==0",
                "SCSP*3", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        bonuses = Calculator.calcBonus(rules, cartItems);
        saving = Math.round(Math.floor((briPinkPen.getSalePrice() + purplePen.getSalePrice()) * 3 * 3));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("Test2PensGetSalePrice30PctDiscount", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testBuy1Get3PctSalePriceAndBuy10Get3PctRegularPricePctDiscount(@Optional String showCart) throws ScriptException {
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(1);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(1);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        cartItems.add(new LiquidPaper().buy(3));
        cartItems.add(new Stapler().buy(3));
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("less 10 Pens Get SalePrice 30% discount!", BaseRule.JS_EVAL, true, true, "true",
                "(N<10?SCSP:SCOP)*3", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        long saving = Math.round(Math.floor((briPinkPen.getSalePrice() + purplePen.getSalePrice()) * 3));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy1Get3PctSalePriceAndBuy10Get3PctRegularPricePctDiscount", cartItems, bonuses));
        }
        purplePen = purplePen.buy(5);
        briPinkPen = briPinkPen.buy(5);
        rules.clear();
        rules.add(new BaseRule("10 Pens Get RegPrice 30% discount!", BaseRule.JS_EVAL, true, true, "true",
                "(N<10?SCSP:SCOP)*3", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        });
        bonuses = Calculator.calcBonus(rules, cartItems);
        saving = Math.round(Math.floor((briPinkPen.getRegularPrice() + purplePen.getRegularPrice()) * 5 * 3));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy1Get3PctSalePriceAndBuy10Get3PctRegularPricePctDiscount", cartItems, bonuses));
        }
    }

    @Parameters(SHOW_CART)
    public void testbundle1Stapler_2LiquidPaper_3PensGet10PctDiscount(@Optional String showCart) throws ScriptException {
        CartItem stapler = new Stapler().buy(2);
        CartItem liquidPaper = new LiquidPaper().buy(4);
        CartItem purplePen = new WaterSolublePurpleFabricMarkerPen().buy(3 + 3);
        CartItem briPinkPen = new WaterSolubleBrightPinkFabricMarkerPen().buy(3);
        Set<IItem<CartItem>> cartItems = new HashSet<>();
        cartItems.add(stapler);
        cartItems.add(liquidPaper);
        cartItems.add(purplePen);
        cartItems.add(briPinkPen);
        List<IRule<CartItem>> rules = new ArrayList();
        rules.add(new BaseRule("1S2L3PGet10%Discount", BaseRule.JS_EVAL, false, false, "true", "", 0, Stapler.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }.setNext(new BaseRule("", BaseRule.SPEL_EVAL, false, false, "N%2==0", "", 0, LiquidPaper.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }.setNext(new BaseRule("", BaseRule.JS_EVAL, "N%3==0", "(SCSP+PSCSP+PPSCSP)*10", 0,
                WaterSolublePurpleFabricMarkerPen.CODE, WaterSolubleBrightPinkFabricMarkerPen.CODE) {
            @Override
            public IItem<CartItem> getBonus() {
                return new OneCentDiscount();
            }
        })));
        Collection<BonusItem<CartItem>> bonuses = Calculator.calcBonus(rules, cartItems);
        long saving = Math.round(Math.floor(stapler.getSalePrice() * 20 + liquidPaper.getSalePrice() * 40 + briPinkPen.getSalePrice() * 30 + purplePen.getSalePrice() * 30));
        assertThat(String.format("Should save %,d cents", saving),
                bonuses.iterator().next().getQuantity(), is(equalTo(saving)));
        if ("true".equalsIgnoreCase(showCart)) {
            logger.info("\n{}", showCart("TestBuy1Stapler_2LiquidPaper_3PensGet10%Discount", cartItems, bonuses));
        }
    }

}
