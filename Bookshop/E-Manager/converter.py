import sys

import openpyxl


def convert_xlsx_to_xls(xlsx_file, xls_file):
    """Converts an XLSX file to an XLS file."""
    try:
        workbook = openpyxl.load_workbook(xlsx_file)
        workbook.save(xls_file)
        print(f"Conversion completed successfully.")
    except Exception as e:
        print(f"Error during conversion: {e}")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python converter.py input.xlsx output.xls")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]
    convert_xlsx_to_xls(input_file, output_file)
    print(f"File '{input_file}' has been saved as '{output_file}' in .xls format.")
