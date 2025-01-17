package com.example.walletconnector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.OkHttpTransport
import org.walletconnect.impls.WCPeerMeta
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var session: Session
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton: Button = findViewById(R.id.connectButton)
        statusTextView = findViewById(R.id.statusTextView)

        val peerMeta = WCPeerMeta(name = "WalletConnectorApp", url = "https://example.com")
        val sessionStore = FileWCSessionStore(File(cacheDir, "wc_store.json"))
        session = Session.Builder()
            .transport(OkHttpTransport.Builder().build())
            .storage(sessionStore)
            .peer(peerMeta)
            .build()

        connectButton.setOnClickListener {
            val handshakeUrl = session.url
            statusTextView.text = "Scan this QR Code with your wallet: $handshakeUrl"

            session.addCallback(object : Session.Callback {
                override fun onStatus(status: Session.Status) {
                    when (status) {
                        is Session.Status.Connected -> statusTextView.text = "Connected!"
                        is Session.Status.Disconnected -> statusTextView.text = "Disconnected!"
                        is Session.Status.Approved -> statusTextView.text = "Approved!"
                        else -> statusTextView.text = "Status: $status"
                    }
                }
            })

            session.init()
        }
    }
}
