package scpc.rule;

import scpc.model.support.EvalHelper;
import scpc.model.support.SpelEvalHelper;

/**
 *
 * @author Kent Yeh
 */
public class SpelEvalFactory extends SpelEvalHelper {

    private static final EvalHelper HELPER = new SpelEvalFactory();

    public static EvalHelper getInstance() {
        return HELPER;
    }

    private SpelEvalFactory() {
    }

    @Override
    public String getPreviousRulePrefix() {
        return "P";
    }

    @Override
    public String getVarSalePrice() {
        return "SP";
    }

    @Override
    public String getVarRegularPrice() {
        return "OP";
    }

    @Override
    public String getVarSerialNum() {
        return "NN";
    }

    @Override
    public String getVarContainsCount() {
        return "N";
    }

    @Override
    public String getVarSumOfContainsRegularPrice() {
        return "SCOP";
    }

    @Override
    public String getVarSumOfContainsSalePrice() {
        return "SCSP";
    }

    @Override
    public String getVarSumOfSerialRegularPrice() {
        return "SSOP";
    }

    @Override
    public String getVarSumOfSerialSalePrice() {
        return "SSSP";
    }
}
