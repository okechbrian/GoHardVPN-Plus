package com.gohardvpn.plus.handler

import com.gohardvpn.plus.AppConfig
import com.gohardvpn.plus.dto.ProfileItem

object PayloadManager {
    private const val PREF_PAYLOAD_ENABLED = "pref_payload_enabled"
    private const val PREF_PAYLOAD_TEMPLATE = "pref_payload_template"
    private const val PREF_PAYLOAD_CUSTOM = "pref_payload_custom"

    val defaultPayloads = listOf(
        PayloadTemplate(
            name = "HTTP Standard",
            description = "Standard HTTP injection",
            payload = "GET / HTTP/1.1[crlf]Host: {host}[crlf][crlf]",
            tlsVersion = "tlshello"
        ),
        PayloadTemplate(
            name = "HTTP + Keep-Alive",
            description = "HTTP with keep-alive connection",
            payload = "GET / HTTP/1.1[crlf]Host: {host}[crlf]Connection: keep-alive[crlf]User-Agent: Mozilla/5.0[crlf][crlf]",
            tlsVersion = "tlshello"
        ),
        PayloadTemplate(
            name = "HTTP + X-Online-Host",
            description = "Common payload for mobile carriers",
            payload = "GET / HTTP/1.1[crlf]Host: {host}[crlf]X-Online-Host: {host}[crlf][crlf]",
            tlsVersion = "tlshello"
        ),
        PayloadTemplate(
            name = "HTTP + Split",
            description = "Split header injection",
            payload = "GET / HTTP/1.1[crlf]Host: {host}[crlf][crlf]GET / HTTP/1.1[crlf]Host: {host}[crlf][crlf]",
            tlsVersion = "tlshello"
        ),
        PayloadTemplate(
            name = "SSL/TLS Direct",
            description = "Direct TLS connection",
            payload = "",
            tlsVersion = "1-3"
        ),
        PayloadTemplate(
            name = "SSL + SNI",
            description = "TLS with custom SNI",
            payload = "",
            tlsVersion = "1-3"
        )
    )

    fun isPayloadEnabled(): Boolean {
        return MmkvManager.decodeSettingsBool(PREF_PAYLOAD_ENABLED, false)
    }

    fun setPayloadEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(PREF_PAYLOAD_ENABLED, enabled)
    }

    fun getSelectedPayloadIndex(): Int {
        return MmkvManager.decodeSettingsInt(PREF_PAYLOAD_TEMPLATE, 0)
    }

    fun setSelectedPayloadIndex(index: Int) {
        MmkvManager.encodeSettings(PREF_PAYLOAD_TEMPLATE, index)
    }

    fun getCustomPayload(): String {
        return MmkvManager.decodeSettingsString(PREF_PAYLOAD_CUSTOM) ?: ""
    }

    fun setCustomPayload(payload: String) {
        MmkvManager.encodeSettings(PREF_PAYLOAD_CUSTOM, payload)
    }

    fun generatePayload(template: PayloadTemplate, host: String): String {
        return template.payload
            .replace("{host}", host)
            .replace("[crlf]", "\r\n")
    }

    fun applyPayloadToProfile(profileItem: ProfileItem, host: String? = null): ProfileItem {
        if (!isPayloadEnabled()) return profileItem

        val targetHost = host ?: profileItem.host ?: profileItem.server ?: ""
        val index = getSelectedPayloadIndex()

        if (index >= 0 && index < defaultPayloads.size) {
            // Use a built-in template
            val template = defaultPayloads[index]
            if (template.payload.isNotEmpty()) {
                profileItem.headerType = AppConfig.HEADER_TYPE_HTTP
                profileItem.path = generatePayload(template, targetHost)
            }
        } else {
            // Fall back to custom payload when index is out of range (i.e. "Custom" option selected)
            val custom = getCustomPayload()
            if (custom.isNotEmpty()) {
                profileItem.headerType = AppConfig.HEADER_TYPE_HTTP
                profileItem.path = custom
                    .replace("{host}", targetHost)
                    .replace("[crlf]", "\r\n")
            }
        }

        return profileItem
    }

    data class PayloadTemplate(
        val name: String,
        val description: String,
        val payload: String,
        val tlsVersion: String
    )
}
