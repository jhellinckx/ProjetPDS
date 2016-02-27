.PHONY: all client server clean run% info testClient runClient

SERVERDIR = "server"
CLIENTDIR = "android client"
TESTCLIENTDIR = "testClient"

all: server client

infoClientBuild:
	@echo "\033[1;31mTo build client, adapt your .bash_profile :";\
	echo "1) add gradle bin in PATH variable (need to download gradle)";\
	echo "2) export JAVA_HOME as java 7 sdk\033[0m";

infoEmulate:
	@echo "\033[1;31mTo start emulator, adapt your .bash_profile :";\
	echo "export ANDROID_HOME as Android home directory (/Users/username/Library/Android)\033[0m" ;\


client: infoClientBuild
	cd ${CLIENTDIR}; gradle build;

testClient:
	cd ${TESTCLIENTDIR}; gradle build;

server:
	cd ${SERVERDIR}; gradle build;

runclient:
	java -jar ${TESTCLIENTDIR}/build/libs/client-1.0-SNAPSHOT.jar

runserver:
	java -jar ${SERVERDIR}/build/libs/server-1.0-SNAPSHOT.jar         

# TODO Add tests in default directory. Tests are not working ATM.
runtestDAO:
	@echo "\033[1;31mTests do not work. Need to move them in default gradle directory. (src/test)\033[0m";

runservertest:
	@echo "\033[1;31mTests do not work. Need to move them in default gradle directory. (src/test)\033[0m";

runtestrecom:
	@echo "\033[1;31mTests do not work. Need to move them in default gradle directory. (src/test)\033[0m";

clean:
	cd ${SERVERDIR}; gradle clean;
	cd ..;
	cd ${CLIENTDIR}; gradle clean;
	cd ..;
	cd ${TESTCLIENTDIR}; gradle clean;

startEmulator: infoEmulate
	${ANDROID_HOME}/sdk/tools/android avd &

installApp: infoEmulate
	${ANDROID_HOME}/sdk/platform-tools/adb install -r "android client/app/build/outputs/apk/app-debug.apk"