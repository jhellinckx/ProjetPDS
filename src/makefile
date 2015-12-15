.PHONY: all client server clean run% info

SERVERDIR = "server"
CLIENTDIR = "android client"

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

server:
	make -C ${SERVERDIR}

run%:
	make -C ${SERVERDIR} $@

clean:
	make -C ${SERVERDIR} clean;
	cd ${CLIENTDIR}; gradle clean;

startEmulator: infoEmulate
	${ANDROID_HOME}/sdk/tools/android avd &

installApp: infoEmulate
	${ANDROID_HOME}/sdk/platform-tools/adb install -r "android client/app/build/outputs/apk/app-debug.apk"