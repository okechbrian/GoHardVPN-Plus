# GoHardVPN-Plus - Day 2 Notes

## Date: 2026-04-01 (Wednesday)

## What We Did
- Rebranded the app from v2rayNG to GoHardVPN-Plus
- Changed package name from `com.v2ray.ang` to `com.gohardvpn.plus`
- Updated app name, version, and APK output names
- Updated all Kotlin imports

## Changes Made
1. **build.gradle.kts**:
   - namespace: com.v2ray.ang → com.gohardvpn.plus
   - applicationId: com.v2ray.ang → com.gohardvpn.plus
   - versionCode: 717 → 100
   - versionName: 2.0.17 → 1.0.0
   - APK output names: v2rayNG_* → GoHardVPN-Plus_*

2. **strings.xml**:
   - app_name: v2rayNG → GoHardVPN-Plus

3. **settings.gradle.kts**:
   - rootProject.name: v2rayNG → GoHardVPN-Plus

4. **Java package structure**:
   - Renamed from `com/v2ray/ang` to `com/gohardvpn/plus`
   - Updated all import statements in .kt files

## Next Steps (Day 2 - continued)
1. Add default server subscription URLs
2. Create custom config templates
3. Test the build
4. Add custom icons/colors (optional)

## Timeline
- Day 1: Extract & Setup ✅
- Day 2: Rebranding ✅
- Day 2: Import Servers (in progress)
- Day 3: SSH Tunneling
- Day 4: Payload Injector
- Day 5: Config Export + Time Manager
- Day 6: Polish & Bug Fixes
- Day 7: Final Delivery
