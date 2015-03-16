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
