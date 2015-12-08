# Extern JAR dependencies
EXTERNLIBS := libs

# Find all the java files in dir
FIND_JAVA_FILES = $(wildcard $(dir)/*.java)

# Dirs containing server sources files
SERV_SOURCES_DIRS := nioserver items dao

# For each directory in SERV_SOURCES_DIRS, get all its java files
SERV_SOURCES := $(foreach dir,${SERV_SOURCES_DIRS},${FIND_JAVA_FILES}) 

# Replace .java by .class in SERV_SOURCES
SERV_DEPENDENCIES := $(patsubst %.java,%.class,${SERV_SOURCES})

CLI_SOURCES_DIRS :=
CLI_SOURCES := $(foreach dir,${CLI_SOURCES_DIRS},${FIND_JAVA_FILES}) 
CLI_DEPENDENCIES := $(patsubst %.java,%.class,${CLI_SOURCES})

# Set compiler & its flags
ifeq (${JFLAGS},)
	JFLAGS = -g -cp "${EXTERNLIBS}/*:."
endif

ifeq (${JC},)
	JC = javac
endif

# Set dummy rules
.PHONY: all client server test

# Default
all: client server test

client: ${CLI_DEPENDENCIES}

server: ${SERV_DEPENDENCIES} AppliServer.class

test: Test.class doTest.class testDAO.class

# Pattern substitution, to build MyClass.class we need MyClass.java. 
# To achieve that, execute the task below. $^ = %.java
%.class: %.java
	${JC} ${JFLAGS} $^

# Recursively delete all .class files in directory
clean:
		rm -f $(shell find . -name "*.class")

runclient: client
		java -cp "${EXTERNLIBS}/*:." Test

runserver: server
		java -cp "${EXTERNLIBS}/*:." AppliServer

runtestDAO: test
		java -cp "${EXTERNLIBS}/*:." testDAO

	