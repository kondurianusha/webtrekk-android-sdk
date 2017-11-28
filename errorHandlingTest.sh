#!/usr/bin/env bash
adb shell am start -n com.webtrekk.SDKTest/.ThrowExceptionActivity --ez NULL true
adb shell am force-stop com.webtrekk.SDKTest
adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.ErrorHandlerTest#testFatalCompeteSimple com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner

adb shell am start -n com.webtrekk.SDKTest/.ThrowExceptionActivity --ez NULL true
adb shell am force-stop com.webtrekk.SDKTest
adb shell am start -n com.webtrekk.SDKTest/.ThrowExceptionActivity
adb shell am force-stop com.webtrekk.SDKTest
adb shell am instrument -w -e external yes -e class com.webtrekk.SDKTest.ErrorHandlerTest#testFatalCompeteComplex com.webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
