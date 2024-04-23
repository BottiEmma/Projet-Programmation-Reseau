all:
	javac Diffuseur/*.java
	javac Client/*.java
	javac Messages/*.java
	cd Gestionnaire && $(MAKE)

clean:
	rm Diffuseur/*.class
	rm Client/*.class

diff1:
	javac Diffuseur/*.java
	java Diffuseur.Diffuseur Configs/Diffuseur/diff-1.txt 

diff2:
	javac Diffuseur/*.java
	java Diffuseur.Diffuseur Configs/Diffuseur/diff-2.txt 

diffA:
	javac Diffuseur/*.java
	java Diffuseur.Diffuseur Configs/Diffuseur/diff-A.txt 

cli1:
	javac Client/*.java
	java Client.Client Configs/Client/client-1.txt 

cli2:
	javac Client/*.java
	java Client.Client Configs/Client/client-2.txt 

cliA:
	javac Client/*.java
	java Client.Client Configs/Client/client-A.txt 

ges1:
	cd Gestionnaire && ./main ../Configs/Gestionnaire/gestionnaire-1.txt

ges2:
	cd Gestionnaire && ./main ../Configs/Gestionnaire/gestionnaire-2.txt

ges3:
	cd Gestionnaire && ./main ../Configs/Gestionnaire/gestionnaire-3.txt

