#!/bin/bash
echo "PFC - Historials - Execució del Gestor..."

if [ ! -n "$1" ]
then
  echo "Us: `basename $0` port-RMI (ex. `basename $0` 2099)"
  exit 0
fi  

if [ ! -n "$JAVA_HOME" ]
then
  echo "Definiu la variable JAVA_HOME per a poder executar el programa gestor"
  exit 0
fi  

echo "Executem Gestor en el port RMI " $1
PATHACTUAL=`pwd`
CLASSPATH=".:lib/jdom.jar:lib/iaik_jce_full.jar:lib/mysql-connector-java-5.0.7-bin.jar:$PATHACTUAL";
echo "Establint CLASSPATH:" $CLASSPATH
$JAVA_HOME/bin/rmiregistry $1 &
$JAVA_HOME/bin/java -classpath lib/iaik_jce_full.jar:lib/jdom.jar:lib/mysql-connector-java-5.0.7-bin.jar:$APP_PATH pfcGuiGestor $1 


