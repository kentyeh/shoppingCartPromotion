package scpc.model.support;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import scpc.model.IChainRule;
import scpc.model.IRule;
import scpc.model.SingleItem;

/**
 * A Spring Expression Language formula evaluator.
 * <br>使用Spring Expression Language做為公式解譯工具
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public abstract class SpelEvalHelper<T> extends EvalHelper<T> {

    private static final Logger logger = LoggerFactory.getLogger(SpelEvalHelper.class);

    private static final EvaluationContext context = new StandardEvaluationContext();
    private static final SpelExpressionParser sep = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, SpelEvalHelper.class.getClassLoader(), false, true, Integer.MAX_VALUE));
    private static final Pattern escapePtn = Pattern.compile("[\\\\\\^\\$\\.\\|\\?\\*\\+\\(\\)\\[\\{]");
    private BindHolder binder = null;
    private IRule<T> bindRule = null;
    private SingleItem<T> bindItem = null;

    public Expression getExpression(String spel) {
        return sep.parseExpression(spel);
    }

    private String escape(String src) {
        Matcher m = escapePtn.matcher(src);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (m.find()) {
            sb.append(src.substring(pos, m.start())).append("\\").append(src.substring(m.start(), m.start() + 1));
            pos = m.start() + 1;
        }
        return sb.append(src.substring(pos, src.length())).toString();
    }

    /**
     * @see EvalHelper#bindVaribles(scpc.model.IRule, scpc.model.SingleItem)
     * @param rule
     * @param item
     * @return
     */
    @Override
    public EvalHelper<T> bindVaribles(IRule<T> rule, SingleItem<T> item) {
        binder = new BindHolder(rule, item);
        bindRule = rule;
        bindItem = item;
        return this;
    }

    /**
     * @see EvalHelper#bindVarValue(java.lang.String, java.lang.Object)
     * @param variable <br>Must be a javascript qualified variable name.</b>
     * @param value
     * @return
     */
    @Override
    public EvalHelper<T> bindVarValue(String variable, Object value) {
        return this;
    }

    /**
     * @see EvalHelper#eval(java.lang.String)
     * @param <R>
     * @param formula
     * @return
     * @throws ScriptException
     */
    @Override
    public <R> R eval(String formula) throws ScriptException {
        if (binder == null) {
            throw new ScriptException("Not any binder bind (IRule,SingleItem) yet");
        }
        Map<String, String> prefix = new HashMap<>();
        Map<String, String> replacers = new HashMap<>();
        IChainRule<T> prule = bindRule.getPrevious();
        String rpfx = "";
        String rrpfx = "rule";
        while (prule != null) {
            rpfx += getPreviousRulePrefix();
            rrpfx += ".previous";
            prefix.put(rpfx, rrpfx);
            prule = prule.getPrevious();
        }
        for (Map.Entry<String, String> entry : prefix.entrySet()) {
            String varname = validJSVarName(entry.getKey() + getVarContainsCount());
            if (null != replacers.put(varname, entry.getValue() + ".containsCount")) {
                throw new ScriptException(String.format(" 2.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(entry.getKey() + getVarSumOfContainsOriginalPrice());
            if (null != replacers.put(varname, entry.getValue() + ".sumOfContainsOriginalPrice")) {
                throw new ScriptException(String.format(" 3.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(entry.getKey() + getVarSumOfContainsSalePrice());
            if (null != replacers.put(varname, entry.getValue() + ".sumOfContainsSalePrice")) {
                throw new ScriptException(String.format(" 4.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(entry.getKey() + getVarSumOfSerialOriginalPrice());
            if (null != replacers.put(varname, entry.getValue() + ".sumOfSerialOriginalPrice")) {
                throw new ScriptException(String.format(" 5.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(entry.getKey() + getVarSumOfSerialSalePrice());
            if (null != replacers.put(varname, entry.getValue() + ".sumOfSerialSalePrice")) {
                throw new ScriptException(String.format(" 6.Variable[%s] already set!", varname));
            }
        }
        if (bindRule != null) {
            String varname = validJSVarName(getVarContainsCount());
            if (null != replacers.put(varname, "rule.containsCount")) {
                throw new ScriptException(String.format(" 8.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarSerialNum());
            if (null != replacers.put(varname, "rule.serialNum")) {
                throw new ScriptException(String.format(" 9.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarSumOfContainsOriginalPrice());
            if (null != replacers.put(varname, "rule.sumOfContainsOriginalPrice")) {
                throw new ScriptException(String.format("10.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarSumOfContainsSalePrice());
            if (null != replacers.put(varname, "rule.sumOfContainsSalePrice")) {
                throw new ScriptException(String.format("11.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarSumOfSerialOriginalPrice());
            if (null != replacers.put(varname, "rule.sumOfSerialOriginalPrice")) {
                throw new ScriptException(String.format("12,Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarSumOfSerialSalePrice());
            if (null != replacers.put(varname, "rule.sumOfSerialSalePrice")) {
                throw new ScriptException(String.format("13.Variable[%s] already set!", varname));
            }
        }
        if (bindItem != null) {
            String varname = validJSVarName(getVarSalePrice());
            if (null != replacers.put(varname, "item.salePrice")) {
                throw new ScriptException(String.format("14.Variable[%s] already set!", varname));
            }
            varname = validJSVarName(getVarOriginalPrice());
            if (null != replacers.put(varname, "item.originalPrice")) {
                throw new ScriptException(String.format("15.Variable[%s] already set!", varname));
            }
        }
        Matcher m = JS_NAME_LOCATOR.matcher(formula);
        int pos = 0;
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String rep = replacers.get(m.group());
            if (rep != null) {
                sb.append(formula.substring(pos, m.start())).append(rep);
            }
            pos = m.end();
        }
        formula = sb.append(formula.substring(pos)).toString();
        logger.debug("Spel.eval(\"{}\")", formula);
        Expression exp = sep.parseExpression(formula);
        return (R) exp.getValue(context, binder);
    }

    @Override
    protected void purgeBind() {
        binder = null;
        bindRule = null;
        bindItem = null;
    }

    private static class BindHolder<T> {

        private final IRule<T> rule;
        private final SingleItem<T> item;

        public BindHolder(IRule<T> rule, SingleItem<T> item) {
            this.rule = rule;
            this.item = item;
        }

        public IRule<T> getRule() {
            return rule;
        }

        public SingleItem<T> getItem() {
            return item;
        }

    }
}
