# Adsensors

Adsensors dynamically analyzes applications with in-app advertisements and monitors access to all available mobile sensors and records any potential leakage of this type of data.

### Prerequisites

* Xposed Framework
* MitM proxy
* Reaper (https://github.com/Michalis-Diamantaris/Reaper)

### Android Layer

*  adsensors.XposedHooks

This module is responsible for monitoring sensor function calls and redirecting any console messages to the logcat.

Download the module,compile and build the apk <br />
Install the module <br />
Activate the module <br />
Reboot the device <br />
Parse the output of the module using the command adb logcat | grep -i “OsLog” <br />

* Use Reaper’s UIHarvester module  to automatically traverse apps

### Android Layer

*  mitm-injector.py

This script is responsible for monitoring and logging HTML5 WebAPI calls and saving network flows and HTML/JS files. The script saves the output to the file named “output.txt”.  You can distinguish different flows using  “---###---###---###---###---”

Setup mitmproxy using the transparent proxy option. 

Run the proxy using the command:
```
mitmdump -p 8080 --mode transparent --showhost  --anticache -s mitm-injector.py 
```

## Paper

For technical details please refer to our publication:

This Sneaky Piggy Went to the Android Ad Market: Misusing Mobile Sensors for Stealthy Data Exfiltration


*Designed by Serafeim Moustakas and Michalis Diamantaris, implemented by Serafeim Moustakas.*
