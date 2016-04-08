./gradlew assembleRelease
./gradlew assembleDebug

#do unit tests

 #run emulator
gnome-terminal -x sh -c "emulator -avd Nexus_S_API_21"
 #wait untill emulator started
sleep 30
 #do test
./gradlew cAT

bash testIntallAttribution.sh yes


 #extract logs
cd ..
adb logcat -d > all_log.txt
adb logcat -d WebtrekkSDK:* *:S > webtrekk_log.txt

#kill emulator
adb emu kill