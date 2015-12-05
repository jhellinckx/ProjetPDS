
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
		AppliServer.java \
		items/User.java \
		items/Food.java \
		dao/DAOException.java \
		dao/DAOConfigurationException.java \
		dao/DAOFactory.java \
		dao/DAOUtilitaire.java \
		dao/UserDAO.java \
		dao/UserDAOImpl.java \
		dao/FoodDAO.java \
		dao/FoodDAOImpl.java \
		doTest.java \
		testDAO.java 

# JAR dependencies
LIBDIR = libs

# Default target entry
default: classes

classes: $(CLASSES:.java=.class)

clean:
		rm *.class && rm -f $(PKG)/*.class && rm dao/*.class && rm items/*.class

runclient: 
		java -cp "$(LIBDIR)/*:." Test

runserver:
		java -cp "$(LIBDIR)/*:." AppliServer

runtestDAO:
		java -cp "$(LIBDIR)/*:." testDAO

	