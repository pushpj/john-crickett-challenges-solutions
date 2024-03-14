import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class build_your_own_json_parser_2 {

    /**
     * this class acts as the response structure for getting the validation flag and error message, if any 
     */
    public static class ResponseEntity {
        private Boolean isValid;
        private String errorMsg;
    }

    private static final Logger logger = Logger.getLogger(build_your_own_json_parser_2.class.getName());
    public static void main(String[] args) {
        List<String> testFilePaths = new ArrayList<>(1);
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_1_valid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_1_invalid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_valid_1.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_invalid_1.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_valid_2.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett-challenges-solutions/step_2_invalid_2.json");
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
            logger.severe("Error occurred while reading the file: " + ioException);
        }
    }

    /**
     * this method gets the file name from the file path
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

        // this case handles the empty file case
        if (jsonStr.length() == 0) {
            responseEntity.isValid = Boolean.FALSE;
            responseEntity.errorMsg = "Empty JSON object found!";
            return responseEntity;
        }

        // this removes any whitespace character, like space, tabs, new line, etc.
        jsonStr = jsonStr.replaceAll("\\s", "");

        Integer countOfParentheses = 0;
        Integer countOfApostrophes = 0;
        Integer countOfColons = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            Character jsonChar = jsonStr.charAt(i);
            if (jsonChar.equals('{'))
                countOfParentheses += 1;
            if (jsonChar.equals('}'))
                countOfParentheses -= 1;
            if (jsonChar.equals('"'))
                countOfApostrophes += 1;

            // the character after the colon should be a space or an apostrophe
            if (jsonChar.equals(':')) {
                countOfColons += 1;
                if (Boolean.TRUE.equals(isLastChar(i, jsonStr.length()))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Colon shouldn't be at the end of the JSON object!";
                    return responseEntity;
                }
                Character nextChar = jsonStr.charAt(i+1);
                if (Boolean.FALSE.equals(nextChar.equals(' ') || nextChar.equals('"'))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Value not found at the end of colon!";
                    return responseEntity;
                }
            }

            // when a letter or a digit is encountered, the next character should be either a letter or a digit
            // or an apostrophe
            if (Character.isLetter(jsonChar) || Character.isDigit(jsonChar)) {
                if (Boolean.TRUE.equals(isLastChar(i, jsonStr.length()))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Letter or Digit shouldn't be at the end of the JSON object!";
                    return responseEntity;
                }
                Character nextChar = jsonStr.charAt(i+1);
                if (Boolean.FALSE.equals(Character.isLetter(nextChar) || nextChar.equals('"') || Character.isDigit(nextChar))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "No apostrophe found at the end of letter(s)!";
                    return responseEntity;
                }
            }

            // if a comma is encountered the previous and next character should be an apostrophe
            if (jsonChar.equals(',')) {
                if (i == 0) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Comma (,) shouldn't be at the start of the JSON object!";
                    return responseEntity;
                }
                if (Boolean.TRUE.equals(isLastChar(i, jsonStr.length()))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Comma (,) shouldn't be at the end of the JSON object!";
                    return responseEntity;
                }
                Character prevChar = jsonStr.charAt(i-1);
                Character nextChar = jsonStr.charAt(i+1);
                if (Boolean.FALSE.equals((prevChar.equals('"')) && (nextChar.equals('"')))) {
                    responseEntity.isValid = Boolean.FALSE;
                    responseEntity.errorMsg = "Invalid JSON literal surrounding comma (,)!";
                    return responseEntity;
                }
            }
        }

        // the count of parentheses should be 0, as we are adding 1 whenever we encounter an opening parentheses ({) and
        // subtract 1 whenever we encounter a closing one (})
        if (countOfParentheses != 0) {
            responseEntity.isValid = Boolean.FALSE;
            responseEntity.errorMsg = "Unequal parentheses found!";
            return responseEntity;
        }

        // the count of apostrophes should be even always, since there is always should be a closing and opening
        // parentheses
        if (countOfApostrophes % 2 != 0 ) {
            responseEntity.isValid = Boolean.FALSE;
            responseEntity.errorMsg = "Apostrophes pair missing!";
            return responseEntity;
        }

        // there should at least be 1 colon, if the json object has a key value pair
        if (countOfColons < 1 && jsonStr.length() > 2) {
            responseEntity.isValid = Boolean.FALSE;
            responseEntity.errorMsg = "No colons found!";
            return responseEntity;
        }

        // if all the checks are passed successfully!
        responseEntity.isValid = Boolean.TRUE;
        responseEntity.errorMsg = "Everything looks good :)";
        return responseEntity;
    }

    /**
     * this method checks the if it is the last character
     * @param index
     * @param strLen
     * @return
     */
    private static Boolean isLastChar(Integer index, Integer strLen) {
        if (index+1 >= strLen) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}