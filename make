#!/bin/bash

PRG=libjdm

# -----------------------------------------------------------------------------

pack () {
  rm -fR classes
  mkdir classes
  javac -d classes src/es/dm/*.java
  if [ -e pack/tmp ]
  then
    rm -fR pack/tmp
  fi
  mkdir pack/tmp
  cd pack/tmp
#  jar xvf $YETI
  cp -fR ../../classes/* ./
#  rm -fR META-INF
  mkdir META-INF
  echo 'Manifest-Version: 1.0' > META-INF/MANIFEST.MF
  echo 'Created-By: ºDeme' >> META-INF/MANIFEST.MF
#  echo 'Main-Class: Main' >> META-INF/MANIFEST.MF
  echo ''>> META-INF/MANIFEST.MF
  jar cvfm ../$PRG.jar META-INF/MANIFEST.MF *
  cd ../..
  rm -fR pack/tmp
}

case $1 in
c*)
  javac -d classes src/es/dm/*.java
  javac -cp classes -d tests/classes tests/src/*.java
  ;;
x*)
  javac -d classes src/es/dm/*.java
  javac -cp classes -d tests/classes tests/src/*.java
  java -ea -cp classes:tests/classes Main
  ;;
pack*)
  pack
  ;;
run*)
  pack
  javac -cp classes -d tests/classes tests/src/*.java
  java -cp pack/$PRG.jar:tests/classes Main
  ;;
doc*)
  javadoc -quiet -d api -sourcepath src es.dm
  ;;
*)
  echo $1: Unknown option
  ;;
esac


