# GoHardVPN-Plus — Build Guide

Complete instructions for building the GoHardVPN-Plus APK from source, including the native
`AndroidLibXrayLite` AAR and `hev-socks5-tunnel` `.so` libraries.

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Go | 1.26+ | https://go.dev/dl — must match `go.mod` in `AndroidLibXrayLite` |
| gomobile | latest | Installed via `go install` (see below) |
| Android NDK | r26c or r27 | Required for hev-socks5-tunnel |
| Android SDK | API 36 | `compileSdk` in `build.gradle.kts` |
| Java / JDK | 17+ | Required by Gradle |
| Android Studio | Hedgehog+ | Optional — Gradle CLI works fine |

---

## 1. Clone the Repository

```bash
git clone https://github.com/okechbrian/GoHardVPN-Plus.git
cd GoHardVPN-Plus
```

> The `app/AndroidLibXrayLite` and `app/hev-socks5-tunnel` directories are included directly
> (not as git submodules), so no `git submodule init` is needed.

---

## 2. Build AndroidLibXrayLite (libv2ray.aar)

This produces the `libv2ray.aar` file that the Android app depends on.

### 2a. Install Go 1.26+

Download from https://go.dev/dl and install:

```bash
# Linux example
wget https://go.dev/dl/go1.26.0.linux-amd64.tar.gz
sudo rm -rf /usr/local/go
sudo tar -C /usr/local -xzf go1.26.0.linux-amd64.tar.gz
export PATH=$PATH:/usr/local/go/bin
go version  # should print go1.26.x
```

### 2b. Install gomobile

```bash
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest
export PATH=$PATH:$(go env GOPATH)/bin
gomobile init
```

### 2c. Build the AAR

```bash
cd app/AndroidLibXrayLite

# Download all Go module dependencies
go get ./...

# Build the AAR for all Android ABIs (arm64-v8a, armeabi-v7a, x86, x86_64)
gomobile bind \
    -v \
    -androidapi 24 \
    -ldflags="-w -s" \
    -o libv2ray.aar \
    .

echo "✅ libv2ray.aar built"
```

### 2d. Copy AAR into the app libs folder

```bash
cp libv2ray.aar ../V2rayNG/app/libs/libv2ray.aar
```

---

## 3. Build hev-socks5-tunnel (.so files)

This produces the native `.so` libraries used for TUN-mode VPN.

### 3a. Set NDK_HOME

```bash
# Example path — adjust to your actual NDK location
export NDK_HOME=$HOME/Android/Sdk/ndk/26.3.11579264
# Verify
echo $NDK_HOME
ls $NDK_HOME/ndk-build
```

### 3b. Fix the package name in compile script (important!)

The existing `compile-hevtun.sh` passes `com/v2ray/ang/service` as the package name.
This must match GoHardVPN-Plus's actual package. Edit the script:

```bash
# Open app/compile-hevtun.sh and change:
#   APP_CFLAGS=-O3 -DPKGNAME=com/v2ray/ang/service
# to:
#   APP_CFLAGS=-O3 -DPKGNAME=com/gohardvpn/plus/service
sed -i 's|com/v2ray/ang/service|com/gohardvpn/plus/service|g' app/compile-hevtun.sh
```

### 3c. Run the compile script

```bash
cd app
bash compile-hevtun.sh
```

Output `.so` files will be in `app/libs/`:
```
app/libs/
  arm64-v8a/libhev-socks5-tunnel.so
  armeabi-v7a/libhev-socks5-tunnel.so
  x86/libhev-socks5-tunnel.so
  x86_64/libhev-socks5-tunnel.so
```

### 3d. Copy .so files into jniLibs

```bash
mkdir -p app/V2rayNG/app/src/main/jniLibs
cp -r app/libs/* app/V2rayNG/app/src/main/jniLibs/
```

---

## 4. Download Geo Data Files

The app needs geo routing rule files at runtime. Download them using the provided script:

```bash
cd app/AndroidLibXrayLite
mkdir -p data
bash gen_assets.sh download
```

Then copy them into the app assets:

```bash
cp data/geoip.dat              ../V2rayNG/app/src/main/assets/
cp data/geosite.dat            ../V2rayNG/app/src/main/assets/
cp data/geoip-only-cn-private.dat ../V2rayNG/app/src/main/assets/
```

---

## 5. Build the APK

With all native libs and assets in place, build the APK with Gradle:

```bash
cd app/V2rayNG

# Debug APK (fastest, no signing required)
./gradlew assembleDebug

# Release APK (requires keystore — see below)
./gradlew assembleRelease
```

APK output location:
```
app/V2rayNG/app/build/outputs/apk/debug/app-<abi>-debug.apk
```

---

## 6. Signing a Release APK (Optional)

To install on a device without developer mode, or to publish, sign the APK:

### 6a. Create a keystore (one-time setup)

```bash
keytool -genkeypair \
    -alias gohardvpn \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -keystore gohardvpn.keystore
```

### 6b. Configure signing in `gradle.properties`

Create or edit `app/V2rayNG/local.properties` (never commit this):

```properties
STORE_FILE=/absolute/path/to/gohardvpn.keystore
STORE_PASSWORD=your_store_password
KEY_ALIAS=gohardvpn
KEY_PASSWORD=your_key_password
```

### 6c. Reference in `build.gradle.kts` (already configured if present)

```kotlin
signingConfigs {
    create("release") {
        storeFile = file(properties["STORE_FILE"] as String)
        storePassword = properties["STORE_PASSWORD"] as String
        keyAlias = properties["KEY_ALIAS"] as String
        keyPassword = properties["KEY_PASSWORD"] as String
    }
}
```

Then build:

```bash
./gradlew assembleRelease
```

---

## 7. Quick Build Checklist

Before running Gradle, verify these files exist:

```bash
# AAR
ls app/V2rayNG/app/libs/libv2ray.aar

# JNI .so files
ls app/V2rayNG/app/src/main/jniLibs/arm64-v8a/libhev-socks5-tunnel.so

# Geo assets
ls app/V2rayNG/app/src/main/assets/geoip.dat
ls app/V2rayNG/app/src/main/assets/geosite.dat
```

If all three checks pass, you're ready to build.

---

## Troubleshooting

**`gomobile: command not found`**
→ Ensure `$(go env GOPATH)/bin` is in your `$PATH`.

**`NDK_HOME not found`**
→ Set `export NDK_HOME` to your NDK directory before running `compile-hevtun.sh`.

**`Could not resolve libv2ray.aar`**
→ The AAR must be in `app/V2rayNG/app/libs/`. Check the copy step in Section 2d.

**`Duplicate class kotlin.collections...`**
→ Run `./gradlew --stop` then clean: `./gradlew clean assembleDebug`.

**`go: module requires go >= 1.26`**
→ Upgrade Go. The `AndroidLibXrayLite/go.mod` requires Go 1.26+.

---

## Bug Fixes Applied (as of this build)

The following bugs were identified and fixed before this build guide was written:

- **SshFmt.kt** — `toOutbound()` was creating a SOCKS outbound instead of SSH
- **V2rayConfigManager.kt** — `EConfigType.SSH` was missing from `createInitOutbound()`, causing NPE on SSH server connect
- **TimeManager.kt** — start and stop alarms used the same broadcast action, so the stop alarm silently overwrote the start alarm
- **PayloadManager.kt** — custom payload was never applied when selected; `applyPayloadToProfile()` had no `else` branch

All fixes are committed to `master`.
