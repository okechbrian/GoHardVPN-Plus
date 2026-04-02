package com.gohardvpn.plus.fmt

import com.gohardvpn.plus.AppConfig
import com.gohardvpn.plus.dto.ProfileItem
import com.gohardvpn.plus.dto.V2rayConfig.OutboundBean
import com.gohardvpn.plus.enums.EConfigType
import com.gohardvpn.plus.extension.idnHost
import com.gohardvpn.plus.extension.isNotNullEmpty
import com.gohardvpn.plus.handler.V2rayConfigManager
import com.gohardvpn.plus.util.Utils
import java.net.URI

object SshFmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        val config = ProfileItem.create(EConfigType.SSH)

        val uri = URI(Utils.fixIllegalUrl(str))
        if (uri.idnHost.isEmpty()) return null
        if (uri.port <= 0) return null

        config.remarks = Utils.decodeURIComponent(uri.fragment.orEmpty()).let { it.ifEmpty { "none" } }
        config.server = uri.idnHost
        config.serverPort = uri.port.toString()

        if (uri.userInfo?.isEmpty() == false) {
            val result = Utils.decode(uri.userInfo).split(":", limit = 2)
            if (result.count() == 2) {
                config.username = result.first()
                config.password = result.last()
            } else {
                config.username = uri.userInfo
            }
        }

        return config
    }

    fun toUri(config: ProfileItem): String {
        val pw =
            if (config.username.isNotNullEmpty() && config.password.isNotNullEmpty())
                "${config.username}:${config.password}"
            else if (config.username.isNotNullEmpty())
                config.username
            else
                ":"

        return "${AppConfig.SSH}${Utils.encode(pw, true)}@${config.server}:${config.serverPort}#${Utils.encode(config.remarks)}"
    }

    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = V2rayConfigManager.createInitOutbound(EConfigType.SSH)

        outboundBean?.settings?.servers?.first()?.let { server ->
            server.address = getServerAddress(profileItem)
            server.port = profileItem.serverPort.orEmpty().toInt()
            if (profileItem.username.isNotNullEmpty()) {
                val socksUsersBean = OutboundBean.OutSettingsBean.ServersBean.SocksUsersBean()
                socksUsersBean.user = profileItem.username.orEmpty()
                socksUsersBean.pass = profileItem.password.orEmpty()
                server.users = listOf(socksUsersBean)
            }
        }

        return outboundBean
    }
}
