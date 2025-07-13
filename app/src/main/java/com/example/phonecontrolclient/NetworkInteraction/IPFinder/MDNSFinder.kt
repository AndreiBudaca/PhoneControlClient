package com.example.phonecontrolclient.NetworkInteraction.IPFinder

import javax.jmdns.JmDNS

class MDNSFinder: IPFinder {
    override fun findIPs(): List<String> {
        val jmdns = JmDNS.create()
        val info = jmdns.getServiceInfo("service.tcp", "phone.control", 10000)
        jmdns.close()

        return info?.inet4Addresses?.map { it.toString().substring(1) } ?: listOf()
    }
}