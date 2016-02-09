COMMONDIR := ../common

COMMONBUILD := ${COMMONDIR}/build

# Extern server JAR dependencies
EXTERNSERVERLIBS := libs

# Extern common JAR dependencies
EXTERNCOMMONLIBS := ${COMMONDIR}/libs

# Local common JAR dependencies
LOCALCOMMONLIBS := ${COMMONBUILD}/libs

# Find all the java files in dir
FIND_JAVA_FILES = $(wildcard $(dir)/*.java)

# Dirs containing server sources files
SERV_SOURCES_DIRS := nioserver items dao recommender .

# For each directory in SERV_SOURCES_DIRS, get all its java files
SERV_SOURCES := $(foreach dir,${SERV_SOURCES_DIRS},${FIND_JAVA_FILES}) 

# Replace .java by .class in SERV_SOURCES
SERV_DEPENDENCIES := $(patsubst %.java,%.class,${SERV_SOURCES})

SERV_PATHS = ":nioserver:items:dao:recommender:."

SERV_CLASSPATHS = "${LOCALCOMMONLIBS}/*:${EXTERNCOMMONLIBS}/*:${EXTERNSERVERLIBS}/*${SERV_PATHS}"

define buildInit
	@echo "\033[1;35mBuilding $@ for server\033[0m"
endef

define buildEnd
	@echo "\033[1;35mFinished building $@ for server\033[0m"
endef

# Set compiler & its flags
ifeq (${JFLAGS},)
	JFLAGS = -cp "${SERV_CLASSPATHS}"
endif

ifeq (${JC},)
	JC = javac
endif

# Set dummy rules
.PHONY: all server common test clean run%

# Default
all: server test

server: common ${SERV_DEPENDENCIES}

common:
	$(call buildInit)
	@cd ${COMMONDIR}; gradle build
	$(call buildEnd)


test: Test.class doTest.class testDAO.class TestRecommender.class

# Pattern substitution, to build MyClass.class we need MyClass.java. 
# To achieve that, execute the task below. $< = %.java
%.class: %.java common
	$(call buildInit)
	${JC} ${JFLAGS} $<
	$(call buildEnd)

# Recursively delete all .class files in directory
clean:
	rm -f $(shell find . -name "*.class");\
	cd ${COMMONDIR}; gradle clean;
	

runserver:
	java ${JFLAGS} AppliServer

runtestDAO:
	java ${JFLAGS} testDAO

runservertest:
	java ${JFLAGS} Test

runtestrecom:
	java $(JFLAGS) -ea TestRecommender

	
