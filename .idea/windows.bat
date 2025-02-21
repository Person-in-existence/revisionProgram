set scriptDir=%~dp0
cd "%scriptDir%"
cd ..\out\artifacts\revisionProgram_jar
jpackage --input "." --main-jar revisionProgram.jar --icon "../../../res/windows.ico"  --win-menu --win-shortcut --name "Revision Program"  --type exe
