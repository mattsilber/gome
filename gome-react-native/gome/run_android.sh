cd android
echo 'Cleaning Android project'
./gradlew clean
cd ..
echo 'Grabbing files that should have already fucking moved'
curl "http://localhost:8081/index.android.bundle?platform=android" -o "android/app/src/main/assets/index.android.bundle"
echo 'Running "react-native run-android"'
react-native run-android
