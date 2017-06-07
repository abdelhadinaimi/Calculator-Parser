import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by whizzmirray on 4/01/17.
 * Parser Class uses the reverse polish expression to parse arithmetical and mathematical equations
 */
public class Parser {

    private static final Pattern tokenPattern = Pattern.compile("\\s*([0-9]*\\.?[0-9]+E(\\+|-)?[0-9]+|[A-Za-z]+|[0-9]*\\.?[0-9]+|\\S)\\s*");
    private static final Pattern operanPattern = Pattern.compile("^[0-9]*\\.?[0-9]+|[A-Za-z]|π");//[0-9]+E(\\+{0,1}|-)[0-9]+
    private static final Pattern namePattern = Pattern.compile("^[A-Za-z]$");//
    private static final Pattern opPattern = Pattern.compile("^\\+|-|\\*|/|\\(|\\)|\\^|\\$|%|!$");
    private static final Pattern funcPattern = Pattern.compile("^cos|sin|tan|ln|log|√|arcos|arcsin|arctan$");


    static final int BINARY  = 0;
    static final int UNARY = 1;

    public static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
    static
    {
        // Map<       "token" , []{Precedence, Arity} >
        OPERATORS.put("cos"   , new int[] { 0, UNARY });
        OPERATORS.put("sin"   , new int[] { 0, UNARY });
        OPERATORS.put("tan"   , new int[] { 0, UNARY });
        OPERATORS.put("arccos", new int[] { 0, UNARY });
        OPERATORS.put("arcsin", new int[] { 0, UNARY });
        OPERATORS.put("arctan", new int[] { 0, UNARY });
        OPERATORS.put("ln"    , new int[] { 0, UNARY });
        OPERATORS.put("log"   , new int[] { 0, UNARY });
        OPERATORS.put("+"     , new int[] { 1, BINARY });
        OPERATORS.put("-"     , new int[] { 1, BINARY });
        OPERATORS.put("*"     , new int[] { 2, BINARY });
        OPERATORS.put("/"     , new int[] { 2, BINARY });
        OPERATORS.put("^"     , new int[] { 3, BINARY });
        OPERATORS.put("√"     , new int[] { 3, UNARY });
        OPERATORS.put("!"     , new int[] { 3, UNARY });
        OPERATORS.put("%"     , new int[] { 3, UNARY });
        OPERATORS.put("$"     , new int[] { 4, UNARY });//unary negation {-a,-(a),(-a)}
        OPERATORS.put("("     , new int[] { 5, BINARY });


    }

    static int isBinary(String s) throws Exception{
        return OPERATORS.get(s)[1];
    }

    private static String[] toPostFix(String[] tokens){
        ArrayList<String> post = new ArrayList<>();//Result
        Stack<String> stack = new Stack<>();
        for(String s : tokens){
            if(isFunction(s)){//if its a function then just push it, it will process it last
                stack.push(s);
            }
            if(isOperator(s)) {
                if (s.equals(")")) {//if its a closing parenthesis pop the stack until we find and opening partenthesis
                    while (!stack.isEmpty()) {
                        String a = stack.peek();
                        if(!a.equals("("))
                            post.add(stack.pop());
                        else{
                            stack.pop();
                            if(!stack.isEmpty() && isFunction(stack.peek())){
                                post.add(stack.pop());
                            }
                            break;
                        }
                    }
                } else {
                    if(!stack.contains("("))
                        try {
                            while (!stack.isEmpty() && cmpPrecedence(s, stack.peek()) <= 0) {
                                if (!stack.peek().equals("(")) {
                                    post.add(stack.pop());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    stack.push(s);
                }
            }
            else if(isOperand(s)){
                post.add(s);
            }
        }
        while(!stack.isEmpty()){
            String s = stack.pop();
            if(!s.equals("("))
                post.add(s);
        }
        String[] output = new String[post.size()];
        return post.toArray(output);
    }

    public static String[] toPostFix(String code){
        String[] tokens = tokenizer(code);
        return toPostFix(expressionCleaner(tokens));
    }

    /**
     * Divides the expression into tokens, for example "5+5" will become ["5","+","5"]
     * @param code A mathematical expression
     * @return a String array with
     */
    public static String[] tokenizer(String code){
        ArrayList<String> result = new ArrayList<>();
        Matcher matcher = tokenPattern.matcher(code);

        while(matcher.find()){
            result.add(matcher.group(1));
        }
        String[] output = new String[result.size()];
        return result.toArray(output);
    }

    /**
     * Changes π and e to their values, changes the minus operator to the unary minus($) and add * when 2 parenthesis meet
     */
    private static String[] expressionCleaner(String[] tokens){
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < tokens.length ;i++){

            switch (tokens[i]) {
                case "-": //if equals to - and is at the beginning of the expression or before ( change it to $
                    if (i == 0 || result.get(i - 1).equals("("))
                        tokens[i] = "$";
                    break;
                case "π":
                    tokens[i] = String.valueOf(Math.PI);
                    break;
                case "e":
                    tokens[i] = String.valueOf(Math.E);
                    break;
            }

            //() () | n funct | n! n | n n | n (). become ()*() | n*funct | n*n | n!*n | n*()
            if(i > 0 && ( tokens[i].equals("(") || isFunction(tokens[i]) || isOperand(tokens[i]) ) ){
                if(Parser.isOperand(tokens[i-1]) || tokens[i-1].equals(")") || tokens[i-1].equals("!")) {
                    result.add("*");
                }
            }
            //() n | funct() n . become ()*n | funct()*n
            if(i < tokens.length-1){
                if(tokens[i].equals(")") && Parser.isOperand(tokens[i+1])) {
                    result.add(")");
                    result.add("*");
                    i++;
                }

            }
            if(tokens[i].contains("E")){
                BigDecimal bd = new BigDecimal(tokens[i]);
                tokens[i] = bd.toPlainString();
            }
            result.add(tokens[i]);
        }

        String[] output = new String[result.size()];
        return result.toArray(output);

    }

    //Returns if the token matches the corresponding regular expression
    public static boolean isOperand(String token){
        return operanPattern.matcher(token).matches();
    }
    public static boolean isName(String token){
        return namePattern.matcher(token).matches();
    }
    public static boolean isOperator(String token){
        return opPattern.matcher(token).matches();
    }
    public static boolean isFunction(String token){
        return funcPattern.matcher(token).matches();
    }

    /**
     * Compares the precedence of two operators if they exist in the OPERATOR map
     * @return The difference between the two precedences
     */
    public static int cmpPrecedence(String t1,String t2) throws Exception{
        return OPERATORS.get(t1)[0]-OPERATORS.get(t2)[0];
    }
    /**
     * Removes the last ".0" from a number
     */
    public static String toInt(String number){
        
    	if(number.endsWith(".0")){
    		return number.substring(0,number.length() - 2);
    	}
        
        return number;
    }
}
