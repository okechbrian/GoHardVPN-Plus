# GoHardVPN-Plus - Day 4 Notes

## Date: 2026-04-01 (Wednesday)

## What We Did
- Added HTTP/SSL Payload Injector feature

## Changes Made

### 1. PayloadManager.kt (New File)
- Created payload manager for HTTP injection settings
- Added 6 preset payload templates:
  - HTTP Standard: Basic HTTP injection
  - HTTP + Keep-Alive: With connection keep-alive
  - HTTP + X-Online-Host: Common for mobile carriers
  - HTTP + Split: Split header injection
  - SSL/TLS Direct: Direct TLS connection
  - SSL + SNI: TLS with custom SNI
- Methods: enable/disable, template selection, custom payload

### 2. pref_settings.xml
- Added "Payload Settings" preference category
- Added enable payload injection toggle
- Added payload template selector
- Added custom payload text field

### 3. strings.xml
- Added payload settings strings

### 4. arrays.xml
- Added payload_templates array (8 options)
- Added payload_templates_value array

## How Payload Injection Works
1. User enables payload injection in settings
2. Selects a preset or enters custom payload
3. When connecting, the app applies the payload to HTTP headers
4. This helps bypass DPI (Deep Packet Inspection) used by ISPs

## Payload Format
- Use `[crlf]` for newlines (will be replaced with `\r\n`)
- Use `{host}` for target host (auto-replaced)

Example custom payload:
```
GET / HTTP/1.1[crlf]Host: {host}[crlf]Connection: keep-alive[crlf][crlf]
```

## Next Steps (Day 5)
1. Add Time Manager feature (scheduled connections)
2. Add Config Export functionality
3. Test and polish

## Timeline
- Day 1: Extract & Setup ✅
- Day 2: Rebranding ✅
- Day 3: SSH Tunneling ✅
- Day 4: Payload Injector ✅
- Day 5: Config Export + Time Manager (pending)
- Day 6: Polish & Bug Fixes (pending)
- Day 7: Final Delivery (pending)
