JFLAGS = -g -cp "$(LIBDIR)/*:."

JC = javac

# Clear any definitions for .java and .class suffixes
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java 

CLASSES = \
		Test.java \
		Constants.java \
		Message.java \
		InStream.java \
		OutStream.java \
		StreamController.java \
		Server.java 

# JAR dependencies
LIBDIR = libs

# Default target entry
default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class

runclient: 
		java -cp "$(LIBDIR)/*:." Test

runserver:
		java -cp "$(LIBDIR)/*:." Server
	