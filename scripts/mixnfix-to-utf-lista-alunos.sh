#!/bin/bash
iconv -f ISO-8859-1 -t UTF-8 $1 > /tmp/__to-utf__
mv /tmp/__to-utf__ $1
flip -u $1