#!/bin/sh

# generate documentation for the RWMidi project

########################################################

DOCLETPATH=./build/rwdoclet/rwdoclet.jar

javadoc -doclet com.ruinwesen.doclet.RWDoclet -docletpath "$DOCLETPATH"  -sourcepath ./src rwmidi
cp src/documentation/RWMidi.html src/documentation/index.html

mkdir ./jar
mkdir ./jar/rwmidi
mkdir ./jar/rwmidi/library
cp ./jar/rwmidi.jar ./jar/rwmidi/library/
mv ./src/documentation ./jar/rwmidi/
#cp -r ./src/rwmidi/docuImages ./jar/rwmidi/documentation
#cp -r ./source/rwmidi/examples ./jar/oscP5/


########################################################
# distribution
########################################################
mkdir ./distribution

cp -r jar/rwmidi distribution
find ./distribution -name .DS_Store -ls -exec rm {} \;
find ./distribution -name .svn -ls -exec rm -rf {} \;
tar cvzf distribution/rwmidi.tgz distribution/rwmidi
cd distribution
zip -r rwmidi rwmidi
cd ..

find . -name .DS_Store -ls -exec rm {} \;

########################################################
# cleanup
#rm -rf jar/*
#mv distribution ../../c/
#rm -rf distribution/rwmidi
