scriptDir=$(dirname "$0")
cd "$scriptDir"
cd ../target
jpackage \
    --input "." \
    --main-jar revisionProgram.jar \
    --type dmg \
    --icon "../res/mac.icns"\
    --name "Revision Program" \
    --mac-package-identifier com.personinexistence.revisionprogram