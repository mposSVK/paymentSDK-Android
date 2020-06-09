package com.wirecard.ecom.examples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wirecard.ecom.Client
import com.wirecard.ecom.card.CardFieldFragment
import com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT
import com.wirecard.ecom.examples.Constants.URL_EE_TEST
import com.wirecard.ecom.examples.providers.OptionalFieldsProvider
import com.wirecard.ecom.examples.providers.PaymentObjectProvider
import com.wirecard.ecom.model.out.PaymentResponse
import com.wirecard.ecom.util.Observer

class KotlinCardFieldActivity : AppCompatActivity(), Observer<PaymentResponse> {
    private val mContext = this
    private val mPaymentObjectProvider = PaymentObjectProvider(OptionalFieldsProvider())
    private lateinit var cardFieldFragment: CardFieldFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_form)

        cardFieldFragment = CardFieldFragment.Builder()
                .setRequireManualCardBrandSelection(true)
                .build()
        supportFragmentManager
                .beginTransaction()
                .add(R.id.card_field_container, cardFieldFragment)
                .commit()

        cardFieldFragment
                .getEventObserver()
                .subscribe { state -> Log.i("event", state.toString()) }
    }

    fun onSubmitButtonClicked(view: View) {
        if (cardFieldFragment.getCardBundle() != null) {
            Client(this, URL_EE_TEST,  REQUEST_TIMEOUT).startPayment(mPaymentObjectProvider.getCardFormPayment(cardFieldFragment.getCardBundle()))
            findViewById<View>(R.id.progress).visibility = View.VISIBLE
        } else {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onObserve(paymentResponse: PaymentResponse) {
        runOnUiThread {
            Toast.makeText(mContext, ResponseHelper.getFormattedResponse(paymentResponse), Toast.LENGTH_SHORT).show()
            findViewById<View>(R.id.progress).visibility = View.GONE
        }
    }
}
