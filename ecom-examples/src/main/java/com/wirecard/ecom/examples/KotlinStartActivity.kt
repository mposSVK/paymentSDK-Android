package com.wirecard.ecom.examples

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.wirecard.ecom.Client
import com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT
import com.wirecard.ecom.examples.Constants.URL_EE_TEST
import com.wirecard.ecom.model.out.PaymentResponse

class KotlinStartActivity : AppCompatActivity() {
    private val mContext = this
    private val mPaymentObjectProvider = PaymentObjectProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        findViewById<Button>(R.id.button_kotlin_example).visibility = View.GONE
    }

    fun makeSepaPayment(view: View) {
        Client(mContext, URL_EE_TEST, REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.sepaPaymentObject)
    }

    fun makeSimpleCardPayment(view: View) {
        Client(mContext, URL_EE_TEST,  REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.getCardPayment(false))
    }

    fun makeAnimatedCardPayment(view: View) {
        Client(mContext, URL_EE_TEST, REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.getCardPayment(true))
    }

    fun makeCardFormPayment(view: View) {
        startActivity(Intent(mContext, KotlinCardFormActivity::class.java))
    }

    fun makeZappPayment(view: View) {
        // Do not forget to add wd_ecom_zapp_host and wd_ecom_zapp_scheme to your string file and initialise it!
        Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.zappPayment)
    }

    fun makePaypalPayment(view: View) {
        // Do not forget to add wd_ecom_paypal_scheme and wd_ecom_paypal_host to your string file and initialise it!
        Client(mContext, URL_EE_TEST, REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.payPalPayment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val paymentSdkResponse = data.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE)
        if (paymentSdkResponse is PaymentResponse) {
            val formattedResponse = ResponseHelper.getFormattedResponse(paymentSdkResponse)
            Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show()
        }
    }
}