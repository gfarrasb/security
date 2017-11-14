#!/bin/bash
echo "PFC - Historials - Execució del Client..."

if [ ! -n "$JAVA_HOME" ]
then
  echo "Definiu la variable JAVA_HOME per a poder executar el programa client"
  exit 0
fi  

$JAVA_HOME/bin/java -classpath .:lib/iaik_jce_full.jar:lib/jdom.jar:lib/mysql-connector-java-5.0.7-bin.jar pfcGuiLogin &

