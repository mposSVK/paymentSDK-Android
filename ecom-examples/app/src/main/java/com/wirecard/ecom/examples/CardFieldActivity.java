package com.wirecard.ecom.examples;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wirecard.ecom.Client;
import com.wirecard.ecom.card.CardFieldFragment;
import com.wirecard.ecom.examples.providers.OptionalFieldsProvider;
import com.wirecard.ecom.examples.providers.PaymentObjectProvider;
import com.wirecard.ecom.model.out.PaymentResponse;
import com.wirecard.ecom.util.Observer;

import static com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT;
import static com.wirecard.ecom.examples.Constants.URL_EE_TEST;

public class CardFieldActivity extends AppCompatActivity implements Observer<PaymentResponse> {
    private Context mContext = this;
    private PaymentObjectProvider mPaymentObjectProvider = new PaymentObjectProvider(new OptionalFieldsProvider());
    CardFieldFragment cardFieldFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        cardFieldFragment = new CardFieldFragment.Builder()
                .setRequireManualCardBrandSelection(true)
                .build();
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
        if (cardFieldFragment.getCardBundle() != null) {
            new Client(this, URL_EE_TEST, REQUEST_TIMEOUT).startPayment(mPaymentObjectProvider.getCardFormPayment(cardFieldFragment.getCardBundle()));
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(mContext, "Card bundle is null!", Toast.LENGTH_SHORT).show();
        }
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
