import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by whizzmirray on 2/03/17.
 */

public class ParserTest {

    @Test
    public void tokenizer_Test() throws Exception{

        assertArrayEquals(new String[]{"123"},Parser.tokenizer("123"));
        assertArrayEquals(new String[]{"12","+","3.2"},Parser.tokenizer("12+3.2"));
        assertArrayEquals(new String[]{"2","+","2"},Parser.tokenizer("2+2"));
        assertArrayEquals(new String[]{"+", "-", "*", "/"},Parser.tokenizer("+-*/"));
        assertArrayEquals(new String[]{"1", "*", "24", "+", "pi"},Parser.tokenizer("   1   * 24 +\n\n  pi"));
        assertArrayEquals(new String[]{"(", ")"},Parser.tokenizer("()"));

        assertTrue(Parser.isOperand("12.53"));



    }
    @Test
    public void postFix_Test() {
        assertArrayEquals(new String[]{"1", "2", "3", "*", "+", "14", "-"}, Parser.toPostFix("1+ 2*3-14"));
        assertArrayEquals(new String[]{"1", "2", "3", "4", "*", "+", "*", "5", "+"}, Parser.toPostFix("1 * (2 + 3 * 4) + 5"));
        assertArrayEquals(new String[]{"1", "2", "3", "+", "*", "4", "*"}, Parser.toPostFix("1 * (2 + 3) * 4)"));
        assertArrayEquals(new String[]{"1", "2", "3", "^", "*", "4", "+"}, Parser.toPostFix("1 * 2 ^ 3 + 4"));
        assertArrayEquals(new String[]{"1","2.2","2","3","*","^","*","4.555420","2","0.202","2","/","^","^","+"}, Parser.toPostFix("1 *2.2^ (2 * 3) + 4.555420^(2^(0.202/2))"));
    }

    @Test
    public void toInt_Test(){
        assertEquals("12",Parser.toInt("12.0"));
        assertEquals("150546.05",Parser.toInt("150546.05"));
        assertEquals("1.2",Parser.toInt("1.2"));

    }
}