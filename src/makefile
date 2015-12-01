# Variable for compilation flags
#	-g : debugging information
JFLAGS = -g -cp $(JARS)

# Variable for the java compiler
JC = javac

# Clear any definitions for .java and .class suffixes
.SUFFIXES: .java .class

# Here is our target entry for creating .class files from .java files 
# This is a target entry that uses the suffix rule syntax:
#	DSTS:
#		rule
#  'TS' is the suffix of the target file, 'DS' is the suffix of the dependency 
#  file, and 'rule'  is the rule for building a target	
# '$*' is a built-in macro that gets the basename of the current target 
# Remember that there must be a < tab > before the command line ('rule') 
.java.class:
	$(JC) $(JFLAGS) $*.java 

# Variable consisting of each java source file
CLASSES = \
		test.java

# JAR dependencies
JARS = libs/json-simple-1.1.1.jar

# Default target entry
default: classes

# This target entry uses Suffix Replacement within a macro: 
# $(name:string1=string2)
# 	In the words in the macro named 'name' replace 'string1' with 'string2'
# Below we are replacing the suffix .java of all words in the macro CLASSES 
# with the .class suffix
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class

run: 
		java -cp $(JARS):. test