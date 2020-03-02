package com.wirecard.ecom.examples

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.wallet.PaymentData
import com.wirecard.ecom.Client
import com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT
import com.wirecard.ecom.examples.Constants.URL_EE_TEST
import com.wirecard.ecom.examples.GooglePayActivity.*
import com.wirecard.ecom.examples.providers.OptionalFieldsProvider
import com.wirecard.ecom.examples.providers.PaymentObjectProvider
import com.wirecard.ecom.model.out.PaymentResponse

class KotlinStartActivity : AppCompatActivity() {
    private val mContext = this
    private val mPaymentObjectProvider = PaymentObjectProvider(OptionalFieldsProvider())

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

    fun makeFragmentCardFieldPayment(view: View?) {
        startActivity(Intent(mContext, KotlinCardFieldFragmentImplActivity::class.java))
    }

    fun makeCardPaymentWithOptionalParameters(view: View) {
        Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.cardPaymentWithOptionalData)
    }

    fun makeSimpleCardTokenPayment(view: View) {
        Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.cardTokenPayment)
    }

    fun makeAnimatedCardPayment(view: View) {
        Client(mContext, URL_EE_TEST, REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.getCardPayment(true))
    }

    fun makeAnimatedCardFieldPayment(view: View) {
        startActivity(Intent(mContext, KotlinAnimatedCardFieldActivity::class.java))
    }

    fun makeTokenAnimatedCardFieldPayment(view: View){
        startActivity(Intent(mContext, KotlinTokenAnimatedCardFieldActivity::class.java))
    }

    fun makeCardFieldPayment(view: View) {
        startActivity(Intent(mContext, KotlinCardFieldActivity::class.java))
    }

    fun makeZappPayment(view: View) {
        // Do not forget to add wd_ecom_zapp_host and wd_ecom_zapp_scheme to your string file and initialise it!
        Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.zappPayment)
    }

    fun makeLoyaltyCard(view: View?) {
        startActivity(Intent(mContext, KotlinLoyaltyCardFieldActivity::class.java))
    }

    fun makePaypalPayment(view: View) {
        // Do not forget to add wd_ecom_paypal_scheme and wd_ecom_paypal_host to your string file and initialise it!
        Client(mContext, URL_EE_TEST, REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.payPalPayment)
    }

    fun makeSDKManagedGooglePayPayment(view: View) {
        Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.googlePayPayment)
    }

    fun makeMerchantManagedGooglePayPayment(view: View) {
        startActivityForResult(Intent(this, GooglePayActivity::class.java), GOOGLE_PAY_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        if (requestCode == GOOGLE_PAY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val paymentData = data.getParcelableExtra<PaymentData>(TAG_PAYMENT_RESPONSE_GOOGLE_PAY)
                val errorMessage = data.getStringExtra(TAG_ERROR_STATUS)
                val userCanceled = data.getBooleanExtra(TAG_USER_CANCELED, false)
                if (paymentData != null) {
                    val googlePayPayment = mPaymentObjectProvider.getGooglePayPayment(null)
                    googlePayPayment.paymentData = paymentData
                    Client(mContext, URL_EE_TEST).startPayment(googlePayPayment)
                } else if (userCanceled) {
                    val builder = AlertDialog.Builder(this)
                            .setTitle("User canceled")
                            .setPositiveButton("OK") { dialog, which -> dialog.cancel() }
                    builder.setMessage("User canceled")
                            .show()
                } else if (errorMessage != null) {
                    val builder = AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setPositiveButton("OK") { dialog, which -> dialog.cancel() }
                    builder.setMessage(errorMessage)
                            .show()
                }
            }
        } else if (requestCode == Client.PAYMENT_SDK_REQUEST_CODE) {
            val paymentSdkResponse = data.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE)
            if (paymentSdkResponse is PaymentResponse) {
                val formattedResponse = ResponseHelper.getFormattedResponse(paymentSdkResponse)
                Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show()
            }
        }
    }
}