#!/bin/bash
unzip -q $1/$2 -d $1/dir
sed 's/none//g' <$1/dir/content.xml >$1/dir/ctt.xml
sed 's/none//g' <$1/dir/styles.xml >$1/dir/stl.xml
python3 insert_default_styles.py $1/dir
cd $1/dir
mv ctt.xml content.xml
mv stl.xml styles.xml
zip -qr $2 *
cd ..
mv dir/$2 $2
rm -r dir
