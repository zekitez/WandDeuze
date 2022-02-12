# WandDeuze
Android app WandDeuze communicates with your "Wallbox (Pulsar (Plus))" via Wifi only.

It does not use Bluetooth.
It may communicate with other Wallbox devices but I have only a Pulsar Plus to test it with.
You could also just use the Wallbox app but refuse Location access which then disables Bluetooth.
WandDeuze does only 4 simple things what the Wallbox app also does:
- display the status of the wallbox
- lock or unlock the wallbox
- pauze or resume the charge session
- display and adjust the charging currrent

These are the very basic things needed to use the wallbox, more capabilities are not needed.

WandDeuze is my interpretation in dialect (NederSaksisch) for the words wall (wand) and box (deuze).
The app is based on some scripts that I found on the internet in Python and HomeyScript.
The sourcecode for the app is available on GIT:

The labels "Connected", ""Locked, "Unlocked", "Pauze", "Resume" and "Change charge current" can have one of the next colors:
- white, available option or reported by wallbox as current state
- gray, currently not allowed option
- green, change confirmed by the wallbox
- red, change not confirmed by the wallbox

![two](https://user-images.githubusercontent.com/35424390/153719031-36eaab07-7608-4490-8668-156be3a8c9d1.png)

![one](https://user-images.githubusercontent.com/35424390/153719019-cb00e762-8c51-4c38-b9b6-b4f791609084.png)

![three](https://user-images.githubusercontent.com/35424390/153719024-03034b6e-3b40-4aa2-a2e7-ac1668cd50e1.png)

https://www.youtube.com/watch?v=NtlvPnDq5qw

Sources:
 https://pypi.org/project/wallbox/#files
 https://community.homey.app/t/wallbox-pulsar-plus-charger-lock-unlock-pause-resume/54616

Icon:
 Icon: https://iconmonstr.com/car-7-png/
