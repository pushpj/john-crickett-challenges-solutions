import sys
import os

FILE_NAME_LITERAL = "FILE_NAME"
FILE_DATA_FROM_INPUT = ""

# this method is responsible to provide CLI params for the script
def get_user_inputs() -> str:
    global FILE_DATA_FROM_INPUT
    # command line arguments given to Python program are automatically saved in sys.argv list
    if len(sys.argv) == 1:
        command = ""
        file_name = FILE_NAME_LITERAL
        FILE_DATA_FROM_INPUT = sys.stdin.read()
    elif len(sys.argv) == 2 and sys.argv[1].startswith("-"):
        command = sys.argv[1]
        file_name = FILE_NAME_LITERAL
        FILE_DATA_FROM_INPUT = sys.stdin.read()
    elif len(sys.argv) == 2 and sys.argv[1].startswith("-") == False:
        command = ""
        file_name = sys.argv[1]
    else:
        command = sys.argv[1]
        file_name = sys.argv[2]
    return command, file_name

# this method is responsible for getting the file size in bytes
def get_file_size(file_name: str):
    # this is for the case if file name is not entered by the user, basically to support standard input
    if file_name == FILE_NAME_LITERAL:
        # this gets the file content
        file_content = FILE_DATA_FROM_INPUT
        # to encode the string in bytes
        encoded_file_content = file_content.encode('utf-8')
        return len(encoded_file_content)
    # os.stat(file_name) returns an object that contains the statistics of the file_name passed, like its size, etc.
    file_stats = os.stat(file_name)
    # we're accessing the st_size property of the file_stats object which return the size of the file in bytes
    file_size = file_stats.st_size
    return file_size

# this method is responsible for getting the no of lines
def get_no_of_lines(file_name: str):
    if file_name == FILE_NAME_LITERAL:
        file_content = FILE_DATA_FROM_INPUT
        # this returns the count of the new line escape character
        return file_content.count("\n")
    # opens the file and 'with' ensures that it is closed after its read is finished, even if an exception is raised during the processing
    with open(file_name) as file_content:
        # generates a tuple containing a count and the corresponding line from the file, index_of_line holds the line number (count) and content_of_line holds the line content
        for line_index, line_content in enumerate(file_content):
            pass
        line_index = line_index + 1
    return line_index

# this method is responsible for getting the number of words
def get_no_of_words(file_name: str) -> int:
    if file_name == FILE_NAME_LITERAL:
        file_text = FILE_DATA_FROM_INPUT
    else:
        with open(file_name) as file_content:
            # converts file content into text
            file_text = file_content.read()
    # returning the length of the list generated by splitting the file text
    return len(file_text.split())

# this method is responsible for getting the number of characters
def get_no_of_chars(file_name: str) -> int:
    if file_name == FILE_NAME_LITERAL:
        return len(FILE_DATA_FROM_INPUT)
    with open(file_name) as file_content:
        # reads the content of the file and counts the no of characters
        no_of_chars = len(file_content.read())
    return no_of_chars

# main() method
def main():
    command, file_name = get_user_inputs()
    # implementing switch case to make code command specific
    match command:
        # output the number of bytes in a file
        case "-c":
            file_size = get_file_size(file_name=file_name)
            print("File Size: ", file_size, "Bytes")

        # output the number of lines in a file
        case "-l":
            no_of_lines = get_no_of_lines(file_name=file_name)
            print("No of Lines: ", no_of_lines)
    
        # output the number of words in a file
        case "-w":
            no_of_words = get_no_of_words(file_name=file_name)
            print("No of Words: ", no_of_words)

        # output the number of characters in a file
        case "-m":
            no_of_chars = get_no_of_chars(file_name)
            print("No of Chars: ", no_of_chars)
        
        # default case, output "-c", "-l" and "-w"
        case _:
            file_size = get_file_size(file_name=file_name)
            no_of_lines = get_no_of_lines(file_name=file_name)
            no_of_words = get_no_of_words(file_name=file_name)
            print("No of Lines: ", no_of_lines, " No of Words: ", no_of_words, "File Size: ", file_size)

if __name__ == "__main__":
    main()