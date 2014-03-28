import xml.etree.ElementTree as ET
import re
import sys

file = sys.argv[1]+'/stl.xml';

#ElementTree requires the namespaces to be reintroduced every time to write them back to the file properly...
pattern = re.compile('xmlns:(.*?)="(.*?)"')
for i, line in enumerate(open(file)):
    for match in re.finditer(pattern, line):
        ET._namespace_map[match.group(2)] = match.group(1);
reverse_namespaces = {v:k for k, v in ET._namespace_map.items()};
xml = ET.parse(file)

root = xml.getroot()
if(root.find(".//style:default-style[@style:family='table-cell']",reverse_namespaces) is None):
    styles = root.find('style:styles',reverse_namespaces)
    nStyle = ET.Element('style:default-style', {'style:family':'table-cell'});
    nStyleProp = ET.Element('style:table-cell-properties',{'fo:border' : '0pt solid #000000'})
    nStyle.append(nStyleProp);
    styles.append(nStyle)
    xml.write(file)
