package tokenizer;

import java.util.Optional;
import java.util.ArrayList;

public class Tokenizer{
    public final String input;
    private int position;

    public Tokenizer(final String input) {
        this.input = input;
        position = 0;
    }

    public int getPosition() {
        return position;
    }

    public void skipWhitespace() {
        while (position < input.length() &&
               Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    } // skipWhitespace

    public Optional<Token> tryReadSymbol(){
        if(input.startsWith(",", position)){
            position++;
            return Optional.of(new CommaToken());
        } else if(input.startsWith("(", position)){
            position++;
            return Optional.of(new LParenToken());
        } else if(input.startsWith(")", position)){
            position++;
            return Optional.of(new RParenToken());
        } else if(input.startsWith(":", position)){
            position++;
            return Optional.of(new ColonToken());
        } else if(input.startsWith("{", position)){
            position++;
            return Optional.of(new LCurlyToken());
        } else if(input.startsWith("}", position)){
            position++;
            return Optional.of(new RCurlyToken());
        } else if(input.startsWith(";", position)){
            position++;
            return Optional.of(new SemicolonToken());
        } else if(input.startsWith(".", position)){
            position++;
            return Optional.of(new DotToken());
        } else if(input.startsWith("*", position)){
            position++;
            return Optional.of(new StarToken());
        } else if(input.startsWith("/", position)){
            position++;
            return Optional.of(new SlashToken());
        } else if(input.startsWith("+", position)){
            position++;
            return Optional.of(new PlusToken());
        } else if(input.startsWith("-", position)){
            position++;
            return Optional.of(new MinusToken());
        } else if(input.startsWith("<", position)){
            position++;
            return Optional.of(new LessThanToken());
        } else if(input.startsWith("=>", position)){
            position++;
            return Optional.of(new RArrowToken());
        } else if(input.startsWith("==", position)){
            position++;
            return Optional.of(new DEqualsToken());
        } else if(input.startsWith("!=", position)){
            position++;
            return Optional.of(new NEqualsToken());
        } else if(input.startsWith("=", position)){
            position++;
            return Optional.of(new EqualsToken());
        } else{
            return Optional.empty();
        }
    } // tryReadSymbol

    public Optional<Token> tryReadInteger(){
        String digits = "";

        while (position < input.length() &&
               Character.isDigit(input.charAt(position))) {
            digits += input.charAt(position);
            position++;
        }

        if (digits.length() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new IntegerLiteralToken(Integer.parseInt(digits)));
        }
    }// tryReadInteger

    public Optional<Token> tryReadReservedWordOrIdentifier(){
        String chars = "";

        if (position < input.length() &&
            Character.isLetter(input.charAt(position))) {
            chars = chars + input.charAt(position);
            position++;
            while(position < input.length() &&
                   Character.isLetterOrDigit(input.charAt(position))) {
                chars += input.charAt(position);
                position++;
            }
            if(chars.equals("println")) {
                return Optional.of(new PrintlnToken());
            } else if(chars.equals("Void")){
                return Optional.of(new VoidToken());
            } else if(chars.equals("Self")){
                return Optional.of(new SelfToken());
            } else if(chars.equals("struct")){
                return Optional.of(new StructToken());
            } else if(chars.equals("method")){
                return Optional.of(new MethodToken());
            } else if(chars.equals("trait")){
                return Optional.of(new TraitToken());
            } else if(chars.equals("impl")){
                return Optional.of(new ImplToken());
            } else if(chars.equals("for")){
                return Optional.of(new ForToken());
            } else if(chars.equals("func")){
                return Optional.of(new FuncToken());
            } else if(chars.equals("let")){
                return Optional.of(new LetToken());
            } else if(chars.equals("if")){
                return Optional.of(new IfToken());
            } else if(chars.equals("else")){
                return Optional.of(new ElseToken());
            } else if(chars.equals("while")){
                return Optional.of(new WhileToken());
            } else if(chars.equals("break")){
                return Optional.of(new BreakToken());
            } else if(chars.equals("return")){
                return Optional.of(new ReturnToken());
            } else if(chars.equals("new")){
                return Optional.of(new NewToken());
            } else if(chars.equals("Int")){
                return Optional.of(new IntToken());
            } else if(chars.equals("true")){
                return Optional.of(new BooleanLiteralToken(true));
            } else if(chars.equals("false")){
                return Optional.of(new BooleanLiteralToken(false));
            } else if(chars.equals("Boolean")){
                return Optional.of(new BooleanToken());
            } else{
                return Optional.of(new IdentifierToken(chars));
            }
        } else{
            return Optional.empty();
        }
    }// tryReadReservedWordOrIdentifier

    public Token readToken() throws TokenizerException{
        Optional<Token> token;
    
    token = tryReadInteger();
    if (token.isPresent()) {
        return token.get();
    }

    token = tryReadSymbol();
    if (token.isPresent()) {
        return token.get();
    }

    token = tryReadReservedWordOrIdentifier();
    if (token.isPresent()) {
        return token.get();
    }

    throw new TokenizerException("Invalid char: " + input.charAt(position));
    } // readToken

    public ArrayList<Token> tokenize() throws TokenizerException{
        final ArrayList<Token> tokens = new ArrayList<Token>();
        skipWhitespace();
        while (position < input.length()) {
            tokens.add(readToken());
            skipWhitespace();
        }
        return tokens;
    } // tokenize
    
}// Tokenizer