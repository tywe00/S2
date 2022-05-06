import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String input;
    public List<Token> tokens;
    private int currentToken;

    // Hjälpmetod som läser in innehållet i en inputstream till en
    // sträng
    private static String readInput(InputStream f) throws java.io.IOException {
        Reader stdin = new InputStreamReader(f);
        StringBuilder buf = new StringBuilder();
        char input[] = new char[1024];
        int read = 0;
        while ((read = stdin.read(input)) != -1) {
            buf.append(input, 0, read);
        }
        return buf.toString();
    }

    public Lexer(InputStream in) throws java.io.Exception {
        String input = Lexer.readInput(in);
        Pattern tokenPattern = Pattern.compile("FORW|BACK|LEFT|RIGHT|DOWN|UP|COLOR|REP|#[A-Fa-f0-9]{6}|\\.|\"|[1-9][0-9]*|%|\\s+");   //lägg till regex för de olika tokens vi ska använda, regex for comments --> %(?!.*\\n).*
        Matcher m = tokenPattern.matcher(input);
        int inputPos = 0;
        tokens = new ArrayList<Token>();
        boolean isComment;     //false by default
        currentToken = 0;
        int currentRow = 1;     //Kolla vilken rad kommandon ligger på
        // Hitta förekomster av tokens/whitespace i indata
        while(m.find()) {

            if (m.start() != inputPos && (isComment == false)) {
                tokens.add(new Token(TokenType.INVALID));
            }

            if(m.group().contains("\n")) {    //Om vi har en ny rad så har vi gått ett steg till. 
                isComment = false;
                currentRow++;
            }
            else if(m.group().equals("FORW")) {
                tokens.add(new Token(TokenType.FORW));
                //currentToken++;
            }
            else if(m.group().equals("BACK")) {
                tokens.add(new Token(TokenType.BACK));
            }
            else if(m.group().equals("LEFT")) {
                tokens.add(new Token(TokenType.LEFT));
            }
            else if(m.group().equals("RIGHT")) {
                tokens.add(new Token(TokenType.RIGHT));
            }
            else if(m.group().equals("DOWN")) {
                tokens.add(new Token(TokenType.DOWN));
            }
            else if(m.group().equals("UP")) {
                tokens.add(new Token(TokenType.UP));
            }
            else if(m.group().equals("COLOR")) {
                tokens.add(new Token(TokenType.COLOR));
            }
            else if(m.group().contains("#")) {
                tokens.add(new Token(TokenType.HEX));
            }
            else if(m.group().equals(".")) {
                tokens.add(new Token(TokenType.PERIOD));
            }
            else if(m.group().equals("\"")) {
                tokens.add(new Token(TokenType.QUOTE));
            }
            else if(m.group().matches("\\d+")) {
                tokens.add(new Token(TokenType.DECIMAL));
            }
            else if(m.group().equals("%")) {
                isComment = true;
            }
            

        }
        // Kolla om det fanns något kvar av indata som inte var ett token
        if(inputPos != input.length()) {
            tokens.add(new Token(TokenType.INVALID));
        }
        //token som signalerar slut på indata
        tokens.add(new Token(TokenType.EOF));
    }


    // Kika på nästa token i indata, utan att gå vidare
    public Token peekToken() throws SyntaxError {
        // Slut på indataströmmen
        if (!hasMoreTokens())
            throw new SyntaxError();
        return tokens.get(currentToken);
    }

    public boolean isComment(String raw) {
        if()
    }

    // Hämta nästa token i indata och gå framåt i indata
    public Token nextToken() throws SyntaxError {
        Token res = peekToken();
        ++currentToken;
        return res;
    }

    public boolean hasMoreTokens() {
        return currentToken < tokens.size();
    }

}