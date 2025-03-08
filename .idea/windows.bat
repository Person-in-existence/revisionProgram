set scriptDir=%~dp0
cd "%scriptDir%"
cd ..\target
jpackage --input "." --main-jar revisionProgram.jar --icon "../res/windows.ico"  --win-menu --win-shortcut --name "Revision Program"  --type exe --vendor "Person-in-existence"
