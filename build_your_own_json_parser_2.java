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
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett/step_1_valid.json");
        testFilePaths.add("/Users/pushpjain/Desktop/john-crickett/step_1_invalid.json");
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
        if (jsonStr.length() == 0) {
            responseEntity.isValid = Boolean.FALSE;
            responseEntity.errorMsg = "Empty JSON object found!";
            return responseEntity;
        }
        Integer countOfParentheses = 0;
        for (Character jsonChar : jsonStr.toCharArray()) {
            if (jsonChar.equals('{'))
                countOfParentheses += 1;
            if (jsonChar.equals('}'))
                countOfParentheses -= 1;
        }
        if (countOfParentheses == 0) {
            responseEntity.isValid = Boolean.TRUE;
            responseEntity.errorMsg = "Everything looks good :)";
            return responseEntity;
        }
        responseEntity.isValid = Boolean.FALSE;
        responseEntity.errorMsg = "Unequal parentheses found!";
        return responseEntity;
    }
}
