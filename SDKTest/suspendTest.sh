adb shell input keyevent 82 # unlock
adb shell am instrument -w -e external yes -e class com.Webtrekk.SDKTest.SuspendTest#testBeforeGoBackgroundHome com.Webtrekk.SDKTest.test/android.test.InstrumentationTestRunner &
cmd_pid=$!
sleep 12
adb shell input keyevent 3
sleep 3
adb shell am start -n com.Webtrekk.SDKTest/.SuspendActivity --activity-reorder-to-front
wait $cmd_pid