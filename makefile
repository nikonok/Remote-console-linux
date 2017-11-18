#make for Linux shell
#UTF-8

.SUFFIXES: .class .java

JC=javac
JVM=java

ifndef SERVER
	SERVER=Server
endif

ifndef CLIENT
	CLIENT=Client
endif

OBJCLIENT=Client.class
OBJSERVER=Server.class

OBJ=$(OBJCLIENT) $(OBJSERVER) ServerStopThread.class ServerThread.class

all: $(OBJ) run_client

run_server: $(OBJSERVER)
	$(JVM) $(SERVER)

run_client: $(OBJCLIENT)
	$(JVM) $(CLIENT)

.java.class:
	$(JC) $<

clean:
	rm -f $(OBJ)
