import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class build_your_own_json_parser {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"|true|false|null|\\d+(?:\\.\\d*)?|\\.\\d+|[-+]?\\d+\\.\\d+(?:[eE][-+]?\\d+)?|\\w+|[\\[\\]{}:,]");

    public static Object parse(String jsonString) throws JSONException {
        List<String> tokens = tokenize(jsonString);
        return parseValue(tokens);
    }

    private static List<String> tokenize(String jsonString) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(jsonString);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private static Object parseValue(List<String> tokens) throws JSONException {
        if (tokens.isEmpty()) {
            throw new JSONException("Unexpected end of input");
        }

        String token = tokens.remove(0);
        if ("{".equals(token)) {
            return parseObject(tokens);
        } else if ("[".equals(token)) {
            return parseArray(tokens);
        } else if ("true".equals(token)) {
            return true;
        } else if ("false".equals(token)) {
            return false;
        } else if ("null".equals(token)) {
            return null;
        } else if (token.startsWith("\"") && token.endsWith("\"")) {
            return parseString(token);
        } else {
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e1) {
                try {
                    return Double.parseDouble(token);
                } catch (NumberFormatException e2) {
                    throw new JSONException("Invalid token: " + token);
                }
            }
        }
    }

    private static String parseString(String token) {
        return token.substring(1, token.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static JSONObject parseObject(List<String> tokens) throws JSONException {
        JSONObject obj = new JSONObject();
        boolean isFirst = true;
        while (!tokens.isEmpty() && !"}".equals(tokens.get(0))) {
            if (!isFirst) {
                if (!",".equals(tokens.remove(0))) {
                    throw new JSONException("Expected ',' or '}', got " + tokens.get(0));
                }
                if ("}".equals(tokens.get(0))) {
                    throw new JSONException("Trailing comma after last value");
                }
            }

            String keyToken = tokens.remove(0);
            if (!keyToken.startsWith("\"") || !keyToken.endsWith("\"")) {
                throw new JSONException("Expected string key surrounded by double quotes, got " + keyToken);
            }
            String key = parseString(keyToken);

            if (!":".equals(tokens.remove(0))) {
                throw new JSONException("Expected ':', got " + tokens.get(0));
            }

            System.out.println("Parsing value for key: " + key);
            try {
                Object value = parseValue(tokens);
                obj.put(key, value);
            } catch (JSONException e) {
                System.out.println("Error while parsing value for key: " + key);
                throw e;
            }
            isFirst = false;
        }
        if (tokens.isEmpty()) {
            throw new JSONException("Unexpected end of input");
        }
        tokens.remove(0);
        return obj;
    }

    private static JSONArray parseArray(List<String> tokens) throws JSONException {
        JSONArray array = new JSONArray();
        while (!tokens.isEmpty() && !"]".equals(tokens.get(0))) {
            array.add(parseValue(tokens));
            if (",".equals(tokens.get(0))) {
                tokens.remove(0);
            }
        }
        if (tokens.isEmpty()) {
            throw new JSONException("Unexpected end of input");
        }
        tokens.remove(0);
        return array;
    }

    public static void main(String[] args) throws JSONException {
        try {
            String filePath = "add_file_path_here";
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileContent = "";
            String lineContent;
            while ((lineContent = bufferedReader.readLine()) != null) {
                fileContent = fileContent.concat(lineContent);
            }
            bufferedReader.close();
            Object parsedJson = parse(fileContent);
            System.out.println(parsedJson);
            System.out.println(parsedJson.getClass());
        } catch (IOException ioException) {
            System.out.println("Error occurred while reading the file: " + ioException);
        }
    }
}

class JSONException extends Exception {
    public JSONException(String message) {
        super(message);
    }
}

class JSONObject extends java.util.HashMap<String, Object> {}

class JSONArray extends ArrayList<Object> {}
