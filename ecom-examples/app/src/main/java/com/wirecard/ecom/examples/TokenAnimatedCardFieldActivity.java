package com.wirecard.ecom.examples;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wirecard.ecom.Client;
import com.wirecard.ecom.card.AnimatedCardFieldFragment;
import com.wirecard.ecom.card.model.CardBundle;
import com.wirecard.ecom.card.model.CardFieldPayment;
import com.wirecard.ecom.cardbrand.CardBrand;
import com.wirecard.ecom.model.AccountHolder;
import com.wirecard.ecom.model.CardToken;
import com.wirecard.ecom.model.TransactionType;
import com.wirecard.ecom.model.out.PaymentResponse;
import com.wirecard.ecom.util.Observer;

import java.math.BigDecimal;
import java.util.UUID;

import static com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT;
import static com.wirecard.ecom.examples.Constants.URL_EE_TEST;

public class TokenAnimatedCardFieldActivity extends AppCompatActivity implements Observer<PaymentResponse> {
    private Context mContext = this;
    AnimatedCardFieldFragment animatedCardFieldFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_card_form);

        animatedCardFieldFragment = new AnimatedCardFieldFragment.Builder()
                .setRequestFocus(true)
                .setRequireManualCardBrandSelection(true)
                .setToken("4304509873471003")
                .setCardBrand(CardBrand.VISA)
                .build();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.card_field_container, animatedCardFieldFragment)
                .commit();

        animatedCardFieldFragment
                .getEventObserver()
                .subscribe(
                        state -> {
                            Log.i("event", state.toString());
                        }
                );
    }

    public void onSubmitButtonClicked(View view) {
        if(animatedCardFieldFragment.getCardBundle() != null) {
            new Client(this, URL_EE_TEST, REQUEST_TIMEOUT).startPayment(getCardFormPayment(animatedCardFieldFragment.getCardBundle()));
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_SHORT).show();
        }
    }

    public CardFieldPayment getCardFormPayment(CardBundle cardBundle) {
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "9105bb4f-ae68-4768-9c3b-3eda968f57ea";
        String secretKey = "d1efed51-4cb9-46a5-ba7b-0fdc87a66544";
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

        CardToken cardToken = new CardToken();
        cardToken.setTokenId("4304509873471003");
        cardFieldPayment.setCardToken(cardToken);

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
