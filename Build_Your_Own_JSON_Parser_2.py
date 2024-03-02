import sys

def get_file_content():
    file_name = sys.argv[1]
    with open(file_name) as file_content:
        return file_content


# main() method
def main():
    file_content = get_file_content()
    print(file_content)

if __name__ == "__main__":
    main()