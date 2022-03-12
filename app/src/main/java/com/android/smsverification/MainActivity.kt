package com.android.smsverification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {

    private lateinit var mSmsRetrieverClient: SmsRetrieverClient
    private lateinit var receiver: MySMSBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClient()
        requestHint()
        receiver = MySMSBroadcastReceiver { otp ->
            Log.d(TAG, otp)

        }
        registerReceiver(receiver, receiver.intentFilter)
        startSmsRetriever()
    }

    private fun initClient() {
        mSmsRetrieverClient = SmsRetriever.getClient(this /* context */)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun startSmsRetriever() {
        val task: Task<Void> = mSmsRetrieverClient.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d(TAG, "Success")

        }

        task.addOnFailureListener {
            Log.d(TAG, "Failure")

        }
    }

    /**
     * It's Optional
     */
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender, RESOLVE_HINT, null, 0, 0, 0
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val RESOLVE_HINT = 101
    }

}