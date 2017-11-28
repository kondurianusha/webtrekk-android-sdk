#!/bin/bash

echo "Uninstall application"
adb uninstall com.webtrekk.SDKTest
adb uninstall com.webtrekk.SDKTest.test
echo "Install back"
adb install app/build/outputs/apk/app-debug.apk
adb install app/build/outputs/apk/app-debug-androidTest-unaligned.apk


 if  [ "${1}" = "yes" ];
    then
      echo "set advID"
      adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.AttributionTest#testAdID com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
      echo "Do redirect request and set clickID with advID"
      adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.AttributionTest#testAttributionRunLinkWithAdID com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
    else
      echo "Do redirect request and set clickID without advID"
      adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.AttributionTest#testAttributionRunLinkWithoutAdID com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
  fi

clickIDPath="$(adb shell ls /data/data/com.webtrekk.SDKTest/files/*.clk)"
clickIDFileName="${clickIDPath##*/}"
clickID="${clickIDFileName%.*}"

echo "ClickID is ${clickID}"

echo "do first start"
adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.AttributionTest#testFirstStart com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner &
cmd_pid=$!
sleep 15

echo "send broadcast message"
adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n com.webtrekk.SDKTest/com.webtrekk.webtrekksdk.ReferrerReceiver --es referrer "wt_clickid=${clickID}"
wait $cmd_pid