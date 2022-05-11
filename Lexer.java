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

    public Lexer(InputStream in) throws Exception {
        String input = Lexer.readInput(in).toUpperCase();       // Gör om alla små bokstäver till stora
        Pattern tokenPattern = Pattern.compile("(FORW\\s)|(BACK\\s)|(LEFT\\s)|(RIGHT\\s)|(DOWN)|(UP)|(COLOR\\s)|(REP\\s)|(#[A-Fa-f0-9]{6})|(\\.)|(\")|([1-9][0-9]*)|(%)|(\\s+)");   //lägg till regex för de olika tokens vi ska använda, regex for comments --> %(?!.*\\n).*
        
        /**
        * Ändrade regex lite
        * 1. Varje grej är inom parentes()
        * 2. Läste i labbhäftet att man skulle ha 1 whitespace efter FORW, BACK, COLOR och dem
         */
        
        Matcher m = tokenPattern.matcher(input);
        int inputPos = 0;
        tokens = new ArrayList<Token>();
        boolean isComment = false;     //false by default
        currentToken = 0;
        int currentRow = 1;     //Kolla vilken rad kommandon ligger på
        // Hitta förekomster av tokens/whitespace i indata
        while(m.find()) {

            if (m.start() != inputPos && (isComment == false)) {
                tokens.add(new Token(TokenType.INVALID, currentRow));
            }

            if(m.group().contains("\n")) {    //Om vi har en ny rad så har vi gått ett steg till. 
                isComment = false;              // Ny rad ==> Avsluta kommentar
                String tempGroup = m.group();   
                for(int i = 0; i < tempGroup.length(); i++) {       // Ifall matchningen har flera nyrader ska vi räkna alla
                    if(tempGroup.charAt(i) == '\n') {
                        currentRow++;
                    }
                }
            }
            
            if(isComment) {       
                continue;                       //Om vi är i en kommentar så ska vi inte lägga till tokens
            }
            else if(m.group().startsWith("FORW")) {     //.startsWith eftersom group kommer att innehålla en whitespace-karaktär ()
                tokens.add(new Token(TokenType.FORW));
                currentToken++;
            }
            else if(m.group().startsWith("BACK")) {
                tokens.add(new Token(TokenType.BACK));
                currentToken++;

            }
            else if(m.group().startsWith("LEFT")) {
                tokens.add(new Token(TokenType.LEFT));
                currentToken++;

            }
            else if(m.group().startsWith("RIGHT")) {
                tokens.add(new Token(TokenType.RIGHT));
                currentToken++;

            }
            else if(m.group().equals("DOWN")) {
                tokens.add(new Token(TokenType.DOWN));
                currentToken++;

            }
            else if(m.group().equals("UP")) {
                tokens.add(new Token(TokenType.UP));
                currentToken++;

            }
            else if(m.group().startsWith("COLOR")) {
                tokens.add(new Token(TokenType.COLOR));
                currentToken++;

            }
            else if(m.group().contains("#")) {
                int hashtagIndex = m.group().indexOf("#");
                tokens.add(new Token(TokenType.HEX, m.group().substring(hashtagIndex, hashtagIndex+7)));
                currentToken++;

            }
            else if(m.group().equals(".")) {
                tokens.add(new Token(TokenType.PERIOD));
                currentToken++;

            }
            else if(m.group().equals("\"")) {
                tokens.add(new Token(TokenType.QUOTE));
                currentToken++;

            }
            else if(m.group().matches("\\d+")) {
                tokens.add(new Token(TokenType.DECIMAL, Integer.valueOf(m.group())));
                currentToken++;

            }
            else if(m.group().equals("%")) {
                isComment = true;
                currentToken++;        

            }
            

        }
        // Kolla om det fanns något kvar av indata som inte var ett token
        if(inputPos != input.length()) {
            tokens.add(new Token(TokenType.INVALID, currentRow));
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