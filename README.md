# GoHardVPN-Plus

A full-featured Android VPN app based on V2RayNG, enhanced with:
- V2Ray/Xray (VMess, VLESS, Reality, Trojan)
- SSH tunneling
- HTTP payload injection
- IP Hunter
- WiFi tethering proxy
- Config import/export (.hc, .json, .v2ray)
- Time manager / scheduled connections
- Server subscription URLs

## Base: V2RayNG (open source)
## Rebranded as: GoHardVPN-Plus

## Timeline: 7-day fast track
- Day 1: Extract HibaNet servers + setup base
- Day 2: Import servers + config system
- Day 3: SSH tunneling
- Day 4: Payload injector
- Day 5: Config export + time manager
- Day 6: Polish + bug fixes
- Day 7: Final delivery

## Status
- [x] Day 1: Extract & Setup ✅
- [x] Day 2: Rebranding (package, app name, imports) ✅
- [x] Day 3: SSH Tunneling ✅
- [x] Day 4: Payload Injector ✅
- [x] Day 5: Config Export + Time Manager ✅
- [x] Day 6: Polish & Bug Fixes ✅
- [ ] Day 7: Final Delivery (build APK)

## Build Instructions
```bash
cd app/V2rayNG
./gradlew assembleDebug
```
APK will be in: `app/build/outputs/apk/debug/`

## Features Added
- SSH server support
- HTTP/SSL payload injector with presets
- Time Manager for scheduled connections
- Rebranded from v2rayNG to GoHardVPN-Plus

## Project Structure
```
GoHardVPN-Plus/
├── README.md
├── servers/           # Extracted server configs
├── docs/              # Documentation & specs
├── app/               # Android app (V2RayNG fork)
├── scripts/           # Build & utility scripts
└── notes/             # Daily development notes
```
