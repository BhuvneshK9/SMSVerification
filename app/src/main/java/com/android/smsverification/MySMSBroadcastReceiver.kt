package com.android.smsverification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class MySMSBroadcastReceiver(val onOtpDetect: (otp: String) -> Unit) : BroadcastReceiver() {
    val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    Log.d(TAG, message.toString())
                    val p = Pattern.compile("\\d+")
                    val m = p.matcher(message)
                    if (m.find()) {
                        val otp = m.group()
                        onOtpDetect(otp)

                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    Log.d(TAG, "TIMEOUT")

                }
            }
        }
    }

    companion object {
        private val TAG = MySMSBroadcastReceiver::class.java.simpleName
    }
}