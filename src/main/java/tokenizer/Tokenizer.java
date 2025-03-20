package tokenizer;

import java.util.Optional;

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
    
}