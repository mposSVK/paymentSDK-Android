package com.wirecard.ecom.examples

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast

import com.wirecard.ecom.Client
import com.wirecard.ecom.card.AnimatedCardFieldFragment
import com.wirecard.ecom.card.model.CardBundle
import com.wirecard.ecom.card.model.CardFieldPayment
import com.wirecard.ecom.model.AccountHolder
import com.wirecard.ecom.model.TransactionType
import com.wirecard.ecom.model.out.PaymentResponse
import com.wirecard.ecom.util.Observer

import java.math.BigDecimal
import java.util.UUID

import com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT
import com.wirecard.ecom.examples.Constants.URL_EE_TEST

class KotlinAnimatedCardFieldActivity : AppCompatActivity(), Observer<PaymentResponse> {
    private val mContext = this
    private lateinit var animatedCardFieldFragment: AnimatedCardFieldFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animated_card_form)

        animatedCardFieldFragment = AnimatedCardFieldFragment.Builder()
                .setRequestFocus(true)
                .setRequireManualCardBrandSelection(true)
                .build()

        supportFragmentManager
                .beginTransaction()
                .add(R.id.card_field_container, animatedCardFieldFragment)
                .commit()

        animatedCardFieldFragment
                .getEventObserver()
                .subscribe { state -> Log.i("event", state.toString()) }
    }

    fun onSubmitButtonClicked(view: View) {
        if (animatedCardFieldFragment.getCardBundle() != null) {
            Client(this, URL_EE_TEST, REQUEST_TIMEOUT).startPayment(getCardFormPayment(animatedCardFieldFragment.getCardBundle()))
            findViewById<View>(R.id.progress).visibility = View.VISIBLE
        } else {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCardFormPayment(cardBundle: CardBundle?): CardFieldPayment {
        val timestamp = SignatureHelper.generateTimestamp()
        val merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1"
        val secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc"
        val requestID = UUID.randomUUID().toString()
        val transactionType = TransactionType.PURCHASE
        val amount = BigDecimal(5)
        val currency = "EUR"
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.value, amount, currency, secretKey)

        val cardFieldPayment = CardFieldPayment.Builder()
                .setSignature(signature!!)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setCardBundle(cardBundle!!)
                .build()

        val accountHolder = AccountHolder("John", "Doe")
        cardFieldPayment.accountHolder = accountHolder

        return cardFieldPayment
    }

    override fun onObserve(paymentResponse: PaymentResponse) {
        runOnUiThread {
            Toast.makeText(this, ResponseHelper.getFormattedResponse(paymentResponse), Toast.LENGTH_SHORT).show()
            findViewById<View>(R.id.progress).visibility = View.GONE
        }
    }
}
