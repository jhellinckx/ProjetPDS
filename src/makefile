.PHONY: all client server clean run% info

SERVERDIR = "server"
CLIENTDIR = "android client"

all: server client
	
info:
	@echo "\033[1;31mTo build client, adapt your .bash_profile :";\
	echo "1) add gradle bin in PATH variable (need to download gradle)";\
	echo "2) export ANDROID_HOME as Android sdk";\
	echo "3) export JAVA_HOME as java 7 sdk\033[0m";

client: info
	cd ${CLIENTDIR}; gradle build;

server:
	make -C ${SERVERDIR}

run%:
	make -C ${SERVERDIR} $@

clean:
	make -C ${SERVERDIR} clean;
	cd ${CLIENTDIR}; gradle clean;


