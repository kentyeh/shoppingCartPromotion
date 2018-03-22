package scpc.model.support;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * A JavaScript formula evaluator using JDK build in JS Engin.
 * <br>使用JDK內建的JS引擎做為公式解譯工具
 *
 * @author Kent Yeh
 * @param <T> type of real cart item.
 */
public abstract class JsEvalHelper<T> extends EvalHelper<T> {

    private static final ScriptEngineManager SMGR = new ScriptEngineManager();
    private Bindings bindings = null;

    protected ScriptEngine getScriptEngine() {
        return SMGR.getEngineByName("JavaScript");
    }

    /**
     * @see EvalHelper#bindVarValue(java.lang.String, java.lang.Object)
     * @param variable
     * @param value
     * @return
     */
    @Override
    public EvalHelper<T> bindVarValue(String variable, Object value) {
        bindings.put(variable, value);
        return this;
    }

    /**
     * @see EvalHelper#purgeBind()
     */
    @Override
    protected void purgeBind() {
        bindings = new SimpleBindings();
    }

    /**
     * @see EvalHelper#eval(java.lang.String)
     * @param formula
     * @return
     * @throws ScriptException
     */
    @Override
    public Object eval(String formula) throws ScriptException {
        validateJSVariable();
        return getScriptEngine().eval(formula, bindings);
    }

}
