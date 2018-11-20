package com.wirecard.ecom.examples;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wirecard.ecom.Client;
import com.wirecard.ecom.card.model.CardBundle;
import com.wirecard.ecom.card.model.CardFieldPayment;
import com.wirecard.ecom.card.CardFieldFragment;
import com.wirecard.ecom.model.AccountHolder;
import com.wirecard.ecom.model.TransactionType;
import com.wirecard.ecom.model.out.PaymentResponse;
import com.wirecard.ecom.util.Observer;

import java.math.BigDecimal;
import java.util.UUID;

import static com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT;
import static com.wirecard.ecom.examples.Constants.URL_EE_TEST;

public class CardFormActivity extends AppCompatActivity implements Observer<PaymentResponse> {
    private Context mContext = this;
    private PaymentObjectProvider mPaymentObjectProvider = new PaymentObjectProvider();
    CardFieldFragment cardFieldFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        cardFieldFragment = new CardFieldFragment.Builder().build();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.card_field_container, cardFieldFragment)
                .commit();

        cardFieldFragment
                .getEventObserver()
                .subscribe(
                        state -> {
                            Log.i("event", state.toString());
                        }
                );
    }

    public void onSubmitButtonClicked(View view) {
        if(cardFieldFragment.getCardBundle() != null) {
            new Client(this, URL_EE_TEST, REQUEST_TIMEOUT).startPayment(getCardFormPayment(cardFieldFragment.getCardBundle()));
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_SHORT).show();
        }
    }

    public CardFieldPayment getCardFormPayment(CardBundle cardBundle) {
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1";
        String secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(5);
        String currency = "EUR";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        CardFieldPayment cardFieldPayment = new CardFieldPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setCardBundle(cardBundle)
                .build();

        AccountHolder accountHolder = new AccountHolder("John", "Doe");
        cardFieldPayment.setAccountHolder(accountHolder);

        return cardFieldPayment;
    }

    @Override
    public void onObserve(PaymentResponse paymentResponse) {
        runOnUiThread(() -> {
                    Toast.makeText(this, ResponseHelper.getFormattedResponse(paymentResponse), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }

        );
    }
}
