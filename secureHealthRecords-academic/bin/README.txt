En aquesta carpeta /bin hi ha les classes del projecte compilades. 

Passos per a provar el sistema:

1. Crear la base de dades "pfchistorials" i afegir-hi el fitxer src/pfchistorials.sql:
	mysqladmin -u root -p create pfchistorials
	mysql -u root -p pfchistorials < src/pfchistorials.sql

2. Configurar el fitxer cfgBBDD.txt amb els paràmetres de connexió a la base de
dades MySQL.

El format d'aquest fitxer és el següent:

	1era línia: Ip o host del servidor MySQL (ex. 127.0.0.1)
	2ona línia: Nom de la base de dades (ex. pfchistorials)
	3ra línia: Usuari que emprarem per a la connexió al servidor (ex. root)
	4arta línia: Contrasenya de l'usuari al servidor (ex. passwd)

2. Executar l'script per a arrencar el gestor: ./executaGestor.sh port-RMI & (ex. ./executaGestor.sh 2099)
3. Executar l'script per a arrencar el client: ./executaClient.sh &

