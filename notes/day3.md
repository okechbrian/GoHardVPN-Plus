# GoHardVPN-Plus - Day 3 Notes

## Date: 2026-04-01 (Wednesday)

## What We Did
- Added SSH tunneling support to the app

## Changes Made

### 1. AppConfig.kt
- Added SSH protocol scheme: `const val SSH = "ssh://"`

### 2. EConfigType.kt
- Added SSH to enum: `SSH(11, AppConfig.SSH)`

### 3. SshFmt.kt (New File)
- Created SSH formatter for parsing and converting SSH configs
- `parse()`: Parse ssh:// URI strings
- `toUri()`: Convert ProfileItem to SSH URI
- `toOutbound()`: Convert to SOCKS outbound (SSH tunnel acts as SOCKS proxy)

### 4. menu_main.xml
- Added SSH menu item: "Add [SSH]"

### 5. strings.xml
- Added string: `menu_item_import_config_manually_ssh`

### 6. MainActivity.kt
- Added handler for SSH menu item

### 7. ServerActivity.kt
- Added SSH to layout mapping (uses SOCKS layout)
- Added SSH validation in save
- Added SSH to saveCommon

### 8. V2rayConfigManager.kt
- Added SshFmt import
- Added SSH to convertProfile2Outbound

### 9. AngConfigManager.kt
- Added SshFmt import
- Added SSH parsing in parseConfig

### 10. V2rayConfig.kt
- Added SSH to getServerAddress and getServerPort

## SSH Implementation Notes
- SSH tunnels are handled as SOCKS proxies
- The actual SSH connection would require an SSH library (e.g., Apache MINA SSHD)
- For now, SSH configs are stored and can be imported/exported as `ssh://` URIs
- Connection functionality requires additional native code integration

## Next Steps (Day 4)
1. Implement HTTP/SSL payload injector
2. Add payload templates for free internet
3. Test SSH config import/export

## Timeline
- Day 1: Extract & Setup ✅
- Day 2: Rebranding ✅
- Day 3: SSH Tunneling ✅ (basic support added)
- Day 4: Payload Injector (pending)
- Day 5: Config Export + Time Manager (pending)
- Day 6: Polish & Bug Fixes (pending)
- Day 7: Final Delivery (pending)
