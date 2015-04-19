Installation des SDK:

wtracksdk.jar in das lib Verzeichnis der App kopieren

dann in android studio, rechtsklick auf die lib und dann Add as Library aus dem menü wählen, add to module app hier ok 




unter res/xml/tracking/ liegen die entsprechenden xml configurationen fürs tracking, je nach bedarf können diese auch auf einem entfernten server liegen

res/xml/tracking/wt_tracking_config.xml enthält alle globalen tracking einstellungen:


<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="webtrekk_track_domain">q3.webtrekk.net</string>
  <string name="webtrekk_track_id">111111111111111</string>
  <integer name="webtrekk_sampling">0</integer>
</resources>

Eine neue Basis Klasse Application Anlegen und diese von WTRackApplication erben lassen:

import wtrack.tracking.WTrackApplication;

public class MyApplication extends WTrackApplication {
}

Folgende Permissions werden benötigt:
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PROFILE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />


Dann in der build.gradle des Modules das getrackt werden soll die wtracksdk-release.aar Datei
als Abhängigkeit hinzufügen:

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile 'com.android.support:appcompat-v7:22.0.0'
    compile(name:'wtracksdk-release', ext:'aar')
    //compile files('libs/wtracksdk.jar')
}

repositories{
    flatDir{
        dirs 'libs'
    }
}
 Und die entsprechende .aar Datei in den libs/ Ordner kopieren
