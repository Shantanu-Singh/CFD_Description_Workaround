import xml.etree.ElementTree as ET
import json

def main():
    # Parse the Sonetto Product XML file
    tree = ET.parse('/Users/IN22902705/Documents/CFD_Description_Workaround/product-input.xml')
    root = tree.getroot()

    # Define the namespace
    ns = {'ns': 'urn:tesco.com:schemas:grocery:2.0'}

    # Initialize data dictionary
    data = {}

    # Iterate over each BaseProduct element in the XML
    for base_product in root.findall('ns:BaseProduct', ns):
        # Extract tpnb attribute and CustomerFriendlyDescription
        tpnb = base_product.attrib['tpnb']
        tpnb = tpnb[1:]
        description = base_product.find('ns:CustomerFriendlyDescription', ns).text

        # Add to the data dictionary
        data[tpnb] = description

    # Write our dictionary to a text file
    with open('sonetto_hashmap.csv', 'w') as f:
        for key, value in data.items():
            f.write(f'{key}|{value}\n')

if __name__ == '__main__':
    main()
