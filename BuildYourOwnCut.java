import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BuildYourOwnCut {
    /**
     * this class is our own CLI cut tool
     *
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 0) {
            // if there is no arguments passed
            System.out.println("No arguments provided!");
            return;
        }
        // this is the variable that tells us the command flag
        String flag = args[0];
        // this is the variable that tells us the delimiter flag
        String delimiter = args[1];
        // this is the variable that stores the file name
        String fileName;
        if (!delimiter.startsWith("-d")) {
            delimiter = null;
            fileName = args[1];
        } else
            fileName = args[2];
        // calling out method to perform the cut operations as specified
        performCutFunction(flag, delimiter, fileName);
    }

    public static void performCutFunction(String flag, String delimiter, String fileName) {
        /**
         * this method performs the cut operation
         */
        String fileContent = readFile(fileName);
        if (flag.startsWith("-f")) {
            // fetching the value of the column number from the "-f" flag
            Integer required_tab_count = Integer.parseInt(String.valueOf(flag.charAt(flag.length() - 1)));

            // getting the number of tabs from the file, to fetch the number of rows
            Integer tab_count = 0;
            for (int i = 0; i < fileContent.length(); i++) {
                if (fileContent.charAt(i) == '\n')
                    break;
                if (fileContent.charAt(i) == '\t')
                    tab_count = tab_count + 1;
            }

            // this is to apply a delimiter character
            String delimiter_regex;
            if (Objects.nonNull(delimiter))
                delimiter_regex = delimiter.substring(2);
            else
                delimiter_regex = "\\t|\\n";

            // splitting the file content based on the tab or next line character
            String[] split_list_based_on_tab = fileContent.split(delimiter_regex);

            // running this loop which prints the column mentioned in the flag
            Integer some_count = -1;
            for (int i = required_tab_count; i < split_list_based_on_tab.length; i += tab_count) {
                try {
                    System.out.println(split_list_based_on_tab[i + some_count]);
                    some_count++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }
        }
    }

    public static String readFile(String filePath) {
        /**
         * this method reads the content of a file
         */
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int character;
            // read characters from the file until end of file
            while ((character = reader.read()) != -1) {
                content.append((char) character); // append each character to the content
            }
        } catch (IOException e) {
            // handling IOException if any
            e.printStackTrace();
        }
        return content.toString();
    }
}
