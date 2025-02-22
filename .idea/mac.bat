set scriptDir=%~dp0
cd "%scriptDir%"
cd ..\out\artifacts\revisionProgram_jar
jpackage ^
    --input "." ^
    --main-jar revisionProgram.jar ^
    --type dmg ^
    --name "Revision Program" ^
    --mac-package-identifier com.personinexistence.revisionprogram