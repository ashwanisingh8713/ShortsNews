package com.ns.shortsnews.user.domain.connectivity

sealed class ConnectionStatus {
  object Available : ConnectionStatus()
    object Unavailable: ConnectionStatus()
}