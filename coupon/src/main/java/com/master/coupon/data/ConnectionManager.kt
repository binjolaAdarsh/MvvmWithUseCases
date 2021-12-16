package com.master.coupon.data

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectionManager(context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val validNetwork: MutableSet<Network> = mutableSetOf()

    private fun checkValidNetwork() {
        postValue(validNetwork.size > 0)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder().addCapability(NET_CAPABILITY_INTERNET).build()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() =
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    val networkCapabilities = cm.getNetworkCapabilities(network)
                    val hasNetworkCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                    if (hasNetworkCapability == true) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                            if (hasInternet) {
                                withContext(Dispatchers.Main) {
                                    validNetwork.add(network)
                                    checkValidNetwork()
                                }
                            }
                        }

                        validNetwork.add(network)
                        checkValidNetwork()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    validNetwork.remove(network)
                    checkValidNetwork()
                }
            }
}

object DoesNetworkHaveInternet {
    fun execute(socketFactory: SocketFactory): Boolean {
        return try {
            val socket = socketFactory.createSocket() ?: throw    IOException("socket is null")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}