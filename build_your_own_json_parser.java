import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class build_your_own_json_parser {

    /**
     * this class acts as the response structure for getting the validation flag and error message, if any
     */
    public static class ResponseEntity {
        private Boolean isValid;
        private String errorMsg;
    }

    /**
     * this enum creates a token type enum, providing support for the possible tokens a JSON object can contain
     */
    enum TokenType {
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        COMMA,
        COLON,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL,
        INVALID_CHAR
    }

    /**
     * this is a token class defining the token type and value
     */
    public static class Token {
        private TokenType tokenType;
        private String tokenValue;

        @Override
        public String toString() {
            return "[" + tokenType + ", " + tokenValue + "]";
        }
    }

    /**
     * utilizing this class to store the token and the updated index
     */
    public static class TokenAndIndex {
        private Token token;
        private int i;
    }

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        List<String> testFilePaths = new ArrayList<>(1);
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_1_valid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_1_invalid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_valid_1.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_invalid_1.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_invalid_2.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_valid_2.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_3_valid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_3_invalid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_4_valid_1.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_4_invalid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_4_valid_2.json");
        for (String filePath : testFilePaths) {
            runTestCase(filePath);
            System.out.println();
        }
    }

    /**
     * this method runs the test case
     */
    public static void runTestCase(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileContent = "";
            String lineContent;
            while ((lineContent = bufferedReader.readLine()) != null) {
                fileContent = fileContent.concat(lineContent);
            }
            bufferedReader.close();
            ResponseEntity responseEntity = validateJson(fileContent);
            System.out.println("For case: " + getFileName(filePath));
            System.out.println("isValid: " + responseEntity.isValid);
            System.out.println("errorMsg: " + responseEntity.errorMsg);
        } catch (IOException ioException) {
            System.out.println("Error occurred while reading the file: " + ioException);
        }
    }

    /**
     * this method gets the file name from the file path
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return path.substring(lastSlashIndex + 1);
    }

    /**
     * this method is actually responsible to validate the JSON object
     *
     * @param jsonStr
     * @return
     */
    public static ResponseEntity validateJson(String jsonStr) {
        ResponseEntity responseEntity = new ResponseEntity();
        // generating a token list from the JSON object
        List<Token> tokenList = tokenize(jsonStr);
        System.out.println(tokenList);
        for (Token token : tokenList) {
            if (token.tokenType.equals(TokenType.INVALID_CHAR)) {
                responseEntity.isValid = Boolean.FALSE;
                responseEntity.errorMsg = "Invalid JSON character found! (" + token.tokenValue + ")";
                return responseEntity;
            }
        }
        return responseEntity;
    }

    /**
     * this method creates a list of tokens form the json object
     *
     * @return
     */
    public static List<Token> tokenize(String jsonStr) {
        List<Token> tokenList = new ArrayList<>();
        for (int i = 0; i < jsonStr.length(); i++) {
            Character jsonChar = jsonStr.charAt(i);
            Token token = new Token();
            TokenAndIndex tokenAndIndex;
            switch (jsonChar) {
            case '{':
                token.tokenType = TokenType.LEFT_BRACE;
                token.tokenValue = "{";
                tokenList.add(token);
                break;
            case '}':
                token.tokenType = TokenType.RIGHT_BRACE;
                token.tokenValue = "}";
                tokenList.add(token);
                break;
            case '[':
                token.tokenType = TokenType.LEFT_BRACKET;
                token.tokenValue = "[";
                tokenList.add(token);
                break;
            case ']':
                token.tokenType = TokenType.RIGHT_BRACKET;
                token.tokenValue = "]";
                tokenList.add(token);
                break;
            case ',':
                token.tokenType = TokenType.COMMA;
                token.tokenValue = ",";
                tokenList.add(token);
                break;
            case ':':
                token.tokenType = TokenType.COLON;
                token.tokenValue = ":";
                tokenList.add(token);
                break;
            case '"':
                tokenAndIndex = readStringAsValue(i, jsonStr);
                tokenList.add(tokenAndIndex.token);
                i = tokenAndIndex.i;
                break;
            default:
                if (Character.isDigit(jsonChar)) {
                    tokenAndIndex = readDigitAsValue(i, jsonStr);
                    tokenList.add(tokenAndIndex.token);
                    i = tokenAndIndex.i - 1;
                } else if (jsonChar.equals('t') || jsonChar.equals('f')) {
                    tokenAndIndex = readBooleanAsValue(i, jsonStr);
                    tokenList.add(tokenAndIndex.token);
                    if (tokenAndIndex.token.tokenType.equals(TokenType.INVALID_CHAR))
                        return tokenList;
                    i = tokenAndIndex.i - 1;
                } else if (jsonChar.equals('n')) {
                    tokenAndIndex = readNullAsValue(i, jsonStr);
                    tokenList.add(tokenAndIndex.token);
                    if (tokenAndIndex.token.tokenType.equals(TokenType.INVALID_CHAR))
                        return tokenList;
                    i = tokenAndIndex.i - 1;
                }
//                else if (Character.isLetter(jsonChar)) {
//                    tokenAndIndex = readStringAsValue(i, jsonStr);
//                    tokenList.add(tokenAndIndex.token);
//                    i = tokenAndIndex.i;
//                }
                break;
            }
        }
        return tokenList;
    }

    /**
     * this method reads the string value and returns the corresponding token and updated index value
     *
     * @param position
     * @param jsonStr
     * @return
     */
    public static TokenAndIndex readStringAsValue(int position, String jsonStr) {
        StringBuilder sb = new StringBuilder();
        position++;
        while (position < jsonStr.length()) {
            char currentChar = jsonStr.charAt(position);
            if (currentChar == '"') {
                break;
            }
            sb.append(currentChar);
            position++;
        }

        Token token = new Token();
        token.tokenType = TokenType.STRING;
        token.tokenValue = sb.toString();

        TokenAndIndex tokenAndIndex = new TokenAndIndex();
        tokenAndIndex.token = token;
        tokenAndIndex.i = position;
        return tokenAndIndex;
    }

    /**
     * this method reads the digit value and returns the corresponding token and updated index value
     *
     * @param position
     * @param jsonStr
     * @return
     */
    public static TokenAndIndex readDigitAsValue(int position, String jsonStr) {
        StringBuilder sb = new StringBuilder();
        while (position < jsonStr.length() && (Character.isDigit(jsonStr.charAt(position)) || jsonStr.charAt(position) == '.')) {
            sb.append(jsonStr.charAt(position));
            position++;
        }

        Token token = new Token();
        token.tokenType = TokenType.NUMBER;
        token.tokenValue = sb.toString();

        TokenAndIndex tokenAndIndex = new TokenAndIndex();
        tokenAndIndex.token = token;
        tokenAndIndex.i = position;
        return tokenAndIndex;
    }

    /**
     * this method reads the Boolean value and returns the corresponding token and updated index value
     *
     * @param position
     * @param jsonStr
     * @return
     */
    public static TokenAndIndex readBooleanAsValue(int position, String jsonStr) {
        StringBuilder sb = new StringBuilder();
        while (position < jsonStr.length() && Character.isLetter(jsonStr.charAt(position))) {
            sb.append(jsonStr.charAt(position));
            position++;
        }
        String boolValue = sb.toString();
        Token token = new Token();
        if (boolValue.equals("true") || boolValue.equals("false")) {
            token.tokenType = TokenType.BOOLEAN;
        } else {
            token.tokenType = TokenType.INVALID_CHAR;
        }
        token.tokenValue = boolValue;
        TokenAndIndex tokenAndIndex = new TokenAndIndex();
        tokenAndIndex.token = token;
        tokenAndIndex.i = position;
        return tokenAndIndex;
    }

    /**
     * this method reads the Null value and returns the corresponding token and updated index value
     *
     * @param position
     * @param jsonStr
     * @return
     */
    public static TokenAndIndex readNullAsValue(int position, String jsonStr) {
        StringBuilder sb = new StringBuilder();
        while (position < jsonStr.length() && Character.isLetter(jsonStr.charAt(position))) {
            sb.append(jsonStr.charAt(position));
            position++;
        }
        String nullValue = sb.toString();
        Token token = new Token();
        if (nullValue.equals("null")) {
            token.tokenType = TokenType.NULL;
        } else {
            token.tokenType = TokenType.INVALID_CHAR;
        }
        token.tokenValue = nullValue;
        TokenAndIndex tokenAndIndex = new TokenAndIndex();
        tokenAndIndex.token = token;
        tokenAndIndex.i = position;
        return tokenAndIndex;
    }
}
