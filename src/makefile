
JFLAGS = -g -cp "$(LIBDIR)/*:."

JC = javac

# Clear any definitions for .java and .class suffixes
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java 

PKG=nioserver

CLASSES = \
		$(PKG)/Constants.java \
		$(PKG)/Message.java \
		$(PKG)/InStream.java \
		$(PKG)/OutStream.java \
		$(PKG)/StreamController.java \
		$(PKG)/ServerShutdownHook.java \
		$(PKG)/AbstractNIOServer.java \
		Test.java \
		CalorieCounterServer.java 

# JAR dependencies
LIBDIR = libs

# Default target entry
default: classes

classes: $(CLASSES:.java=.class)

clean:
		rm *.class && rm -f $(PKG)/*.class

runclient: 
		java -cp "$(LIBDIR)/*:." Test

runserver:
		java -cp "$(LIBDIR)/*:." CalorieCounterServer

	