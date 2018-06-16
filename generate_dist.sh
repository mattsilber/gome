rm -rf dist/*
./gradlew clean build

echo "Assembling Android client"
./gradlew assembleDebug
mv gome-android-client/build/outputs/apk/debug/gome-android-client-debug.apk dist/

echo "Assembling server distribution"
./gradlew installDist
cp -R gome-server/build/install/gome-server/ dist/server/

