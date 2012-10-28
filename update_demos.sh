#!/bin/sh
# Script to update demos deployed in svn

S=war
[ ! -d "$S" ] && echo "Do not exist folder: $S" && exit

cp -r  $S/* demo/
rm -rf demo/WEB-INF demo/META-INF demo/Issue7Test

for i in `find demo -type d | grep -v .svn | sed -e 's#^demo/##g'`
do
   [ ! -d $S/$i ] && svn delete demo/$i 
done

for i in `find demo -type f | grep -v .svn | sed -e 's#^demo/##g'`
do
   [ ! -f $S/$i ] && svn delete demo/$i 
done

find demo  | grep -v .svn | xargs svn add

find demo -type f -name "*html" -exec svn propset svn:mime-type text/html '{}' ';'
find demo -type f -name "*js" -exec svn propset svn:mime-type text/javascript '{}' ';'
find demo -type f -name "*css" -exec svn propset svn:mime-type text/css '{}' ';'