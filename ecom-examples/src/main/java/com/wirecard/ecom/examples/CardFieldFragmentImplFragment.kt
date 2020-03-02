package com.wirecard.ecom.examples

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.wirecard.ecom.Client
import com.wirecard.ecom.card.CardFieldFragment
import com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT
import com.wirecard.ecom.examples.Constants.URL_EE_TEST
import com.wirecard.ecom.examples.providers.OptionalFieldsProvider
import com.wirecard.ecom.examples.providers.PaymentObjectProvider
import com.wirecard.ecom.model.out.PaymentResponse
import com.wirecard.ecom.util.Observer

class CardFieldFragmentImplFragment: Fragment(), Observer<PaymentResponse> {

    private val mPaymentObjectProvider = PaymentObjectProvider(OptionalFieldsProvider())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardFieldFragment = CardFieldFragment.Builder()
                .setRequireManualCardBrandSelection(true)
                .build()

        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.card_field_container, cardFieldFragment)
                ?.commit()

        cardFieldFragment
                .getEventObserver()
                .subscribe { state -> Log.i("event", state.toString()) }

        activity?.findViewById<Button>(R.id.button_submit)?.setOnClickListener {
            if (cardFieldFragment.getCardBundle() != null) {
                Client(this, URL_EE_TEST, REQUEST_TIMEOUT).startPayment(mPaymentObjectProvider.getCardFormPayment(cardFieldFragment.getCardBundle()))
                activity?.findViewById<View>(R.id.progress)?.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Card bundle is null!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onObserve(paymentResponse: PaymentResponse) {
        activity?.runOnUiThread {
            Toast.makeText(context, ResponseHelper.getFormattedResponse(paymentResponse), Toast.LENGTH_SHORT).show()
            activity?.findViewById<View>(R.id.progress)?.visibility = View.GONE
        }
    }
}