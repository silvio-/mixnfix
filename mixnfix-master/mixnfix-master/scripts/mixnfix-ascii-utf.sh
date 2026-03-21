#!/bin/bash
unzip $1
iconv -f ISO-8859-1 -t UTF-8 prova.xml > prova-utf8.xml
cat prova-utf8.xml | sed 's/"ASCII"/"UTF-8"/g' > prova.xml
zip utf8-$1 prova.xml

#mkdir -p /tmp/
#!/bin/bash
#iconv -f ISO-8859-1 -t UTF-8 $1 > /tmp/__to-utf__
#mv /tmp/__to-utf__ $1
#flip -u $1
#iconv -f ISO-8859-1 -t UTF-8 prova.xml > prova-utf8.xml
