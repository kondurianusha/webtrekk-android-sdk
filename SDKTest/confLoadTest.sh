adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testConfigOK com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testLoadDefaultOK com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testBrokenConfigLoad com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testEmptyConfigLoad com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testLocked com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testLargeSize com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
adb shell am instrument -w -e class com.Webtrekk.SDKTest.ConfigLoadTest#testTagIntegration com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner
