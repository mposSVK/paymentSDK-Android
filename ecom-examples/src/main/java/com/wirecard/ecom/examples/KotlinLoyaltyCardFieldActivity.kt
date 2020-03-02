package com.wirecard.ecom.examples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.wirecard.ecom.Client
import com.wirecard.ecom.card.CardFieldFragment
import com.wirecard.ecom.card.model.CardBundle
import com.wirecard.ecom.card.model.CardFieldPayment
import com.wirecard.ecom.cardbrand.CardBrand
import com.wirecard.ecom.model.AccountHolder
import com.wirecard.ecom.model.BasePayment
import com.wirecard.ecom.model.LoyaltyCard
import com.wirecard.ecom.model.TransactionType
import com.wirecard.ecom.model.out.PaymentResponse
import com.wirecard.ecom.util.Observer
import java.util.*

class KotlinLoyaltyCardFieldActivity : AppCompatActivity(), Observer<PaymentResponse> {

    private var cardFieldFragment : CardFieldFragment? = null
    private val  mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_form)

        cardFieldFragment = CardFieldFragment.Builder()
                .setRequireManualCardBrandSelection(true)
                .setSupportedCardBrands(setOf(CardBrand.VISA, CardBrand.MASTERCARD))
                .build()

        cardFieldFragment?.let {cardFieldFragment ->
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.card_field_container, cardFieldFragment)
                    .commit()
        } ?: kotlin.run {
            Toast.makeText(mContext, "Card fragment is null!", Toast.LENGTH_LONG).show()
        }
    }

    fun onSubmitButtonClicked(view: View){
        cardFieldFragment?.getCardBundle()?.let {cardBundle ->
            Client(mContext,"https://api-test.wirecard.com", 30)
                    .startPayment(getPayment(cardBundle))
        } ?: kotlin.run {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_LONG).show()
        }
   }

    override fun onObserve(paymentResponse: PaymentResponse){
            Toast.makeText(mContext, ResponseHelper.getFormattedResponse(paymentResponse), Toast.LENGTH_SHORT).show()
    }

    private fun getPayment(cardBundle: CardBundle) :BasePayment{
        val timestamp = SignatureHelper.generateTimestamp()

        //MasterCard merchant ID and secretKey
        val merchantID = "3a645860-9f9d-4846-83a3-aa3451f842c4"
        val secretKey = "ca0cd974-8523-4bba-8cca-0572f9f30c74"

        //Visa merchant ID and secretKey
//        val merchantID = "f82ea856-be50-4899-988d-697574df65f0"
//        val secretKey = "d40ec430-89eb-4c52-ba2c-98d9e8910fb3"

        val requestID = UUID.randomUUID().toString()
        val transactionType = TransactionType.ENROLLMENT
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.value, null,null,secretKey)

        val cardFieldPayment = CardFieldPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setTransactionType(transactionType)
                .setCardBundle(cardBundle)
                .build()

        cardFieldPayment.accountHolder = getAccountHolder()

        if (cardBundle.cardBrand == CardBrand.MASTERCARD.toString().toLowerCase()){
            cardFieldPayment.loyaltyCard = getLoyaltyCard()
        }

        return cardFieldPayment
    }

    private fun getAccountHolder() : AccountHolder{
        return AccountHolder().apply {
            email = "john.doe${(1..10000000).random()}@gmail.com"
        }
    }

    private fun getLoyaltyCard() : LoyaltyCard{
        return LoyaltyCard().apply {
            userId = Math.random().toString()
            promotionCode = "SHOPBACKVN"
            productCode = "MCCSHOPBACK"
        }
    }

}
