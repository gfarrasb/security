#!/bin/bash
#
if [ ! -n "$JAVA_HOME" ]
then
  echo "Definiu la variable JAVA_HOME per a poder compilar el programa"
  exit 0
fi  

echo "Compilant projecte... Es desaran les classes directament a la carpeta /bin/"
rm -f *.class
rm -f *.xml
rm -f ../bin/*.class
rm -f ../bin/*.xml
$JAVA_HOME/bin/javac -nowarn -classpath .:../bin/lib/iaik_jce_full.jar:../bin/lib/jdom.jar:../bin/lib/mysql-connector-java-5.0.7-bin.jar *.java -Xlint -d ../bin/
$JAVA_HOME/bin/rmic -classpath ../bin:../bin/lib/iaik_jce_full.jar:../bin/lib/jdom.jar:../bin/lib/mysql-connector-java-5.0.7-bin.jar -d ../bin/ Gestor

echo "I, aprofitant, generarem ja els docs en Javadoc ;-)"
echo "Aquests aniran ja directament a /doc/javadocs/";
$JAVA_HOME/bin/javadoc -classpath ../bin/:../bin/lib/iaik_jce_full.jar:../bin/lib/jdom.jar:../bin/lib/mysql-connector-java-5.0.7-bin.jar -quiet -d ../doc/javadocs/ -author -encoding UTF-8 *.java
