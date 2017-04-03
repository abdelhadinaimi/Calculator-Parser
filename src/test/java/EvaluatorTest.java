import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by whizzmirray on 4/03/17.
 */

public class EvaluatorTest {

    @Test
    public void eval_Test() throws Exception{
        try {

            //for(String r : Parser.toPostFix("3 * 6 + cos(5)"))
            //    System.out.print(r+" ");
            //System.out.println("\n"+Evaluator.evaluate("5E5"));
            assertEquals("0.0",Evaluator.evaluate("tan(-π)"));
            assertEquals("0.0",Evaluator.evaluate("sin(π)"));
            assertEquals("-1.0" ,Evaluator.evaluate("cos(π)"));
            assertEquals("136.16948584154855",Evaluator.evaluate("99/cos(sin(4))"));
            assertEquals("25.0",Evaluator.evaluate("5*(3+2)1"));
            assertEquals("2.0",Evaluator.evaluate("√(2^2)"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}