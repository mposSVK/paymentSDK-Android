package com.wirecard.ecom.examples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wirecard.ecom.Client;
import com.wirecard.ecom.model.out.PaymentResponse;

import java.io.Serializable;

import static com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT;
import static com.wirecard.ecom.examples.Constants.URL_EE_TEST;

public class StartActivity extends AppCompatActivity {
    private Context mContext = this;
    private PaymentObjectProvider mPaymentObjectProvider = new PaymentObjectProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void makeSepaPayment(View view) {
        new Client(mContext, URL_EE_TEST,  REQUEST_TIMEOUT)
                .startPayment(mPaymentObjectProvider.getSepaPaymentObject());
    }

    public void makeSimpleCardPayment(View view) {
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getCardPayment(false));
    }

    public void makeAnimatedCardPayment(View view) {
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getCardPayment(true));
    }

    public void makeCardFormPayment(View view) {
        startActivity(new Intent(mContext, CardFormActivity.class));
    }

    public void makeZappPayment(View view) {
        // Do not forget to add wd_ecom_zapp_host and wd_ecom_zapp_scheme to your string file and initialise it!
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getZappPayment());
    }

    public void makePaypalPayment(View view) {
        // Do not forget to add wd_ecom_paypal_scheme and wd_ecom_paypal_host to your string file and initialise it!
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getPayPalPayment());
    }

    public void kotlinDemo(View view){
        startActivity(new Intent(mContext, KotlinStartActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Serializable paymentSdkResponse = data.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE);
        if (paymentSdkResponse instanceof PaymentResponse) {
            String formattedResponse = ResponseHelper.getFormattedResponse((PaymentResponse) paymentSdkResponse);
            Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show();
        }
    }
}
