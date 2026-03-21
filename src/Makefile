ROOT=$(shell pwd)/..
BUILD=$(ROOT)/build
DERBY_HOME=$(ROOT)/lib/derby

comma:= :
empty:=
space:= $(empty) $(empty)

_LIBS_AUX_1_=QTJava.zip derby.jar jxl.jar
_LIBS_AUX_2_=$(addprefix $(ROOT)/lib/, $(_LIBS_AUX_1_))
LIBS=$(subst $(space),$(comma),$(_LIBS_AUX_2_))
# LIBS2=$(patsubst %, $(ROOT)/lib/%, $(LIBS))

all:
	find . | egrep "[.]java$$" > file.list
	javac -encoding utf-8 -classpath $(LIBS):. @file.list

db:
	java -jar $(DERBY_HOME)/lib/derbyrun.jar ij script

run:
	echo "java -classpath $(LIBS):. mixnfix.bin.MIXnFIX db data"
	java -classpath $(LIBS):. mixnfix.bin.MIXnFIX db data

install:
	java -classpath $(LIBS):src mixnfix.bin.Install -d

$(FILES):
	javac -classpath . $@

lib:
	javac  -encoding UTF-8 mixnfix/MFI2Java.java
	javah  -classpath . -jni mixnfix.MFI2Java
	gcc -c mfi.c
	gcc -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers -c MFI2Java.c -framework JavaVM
	gcc -dynamiclib -Wl,-headerpad_max_install_names,-undefined,dynamic_lookup,-compatibility_version,1.0,-current_version,1.0,-install_name,/usr/local/lib/libmfi.jnilib -o libmfi.jnilib mfi.o MFI2Java.o

clear:
	find . -name "*.class" | xargs -I {} mv {} /tmp
	find . -name "*.o" | xargs -I {} mv {} /tmp

# gcc -shared -o mfi mfi.o MFI2Java.o
# gcc -dynamiclib -Wl,-headerpad_max_install_names,-undefined,dynamic_lookup,-compatibility_version,1.0,-current_version,1.0,-install_name,/usr/local/lib/libfoo.1.dylib -o libfoo.1.dylib $(OBJ)
