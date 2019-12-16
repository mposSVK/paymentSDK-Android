package com.wirecard.ecom.examples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wallet.PaymentData;
import com.wirecard.ecom.Client;
import com.wirecard.ecom.card.model.CardPayment;
import com.wirecard.ecom.examples.providers.OptionalFieldsProvider;
import com.wirecard.ecom.examples.providers.PaymentObjectProvider;
import com.wirecard.ecom.googlepay.model.GooglePayPayment;
import com.wirecard.ecom.model.AccountHolder;
import com.wirecard.ecom.model.Address;
import com.wirecard.ecom.model.CustomerData;
import com.wirecard.ecom.model.LoyaltyCard;
import com.wirecard.ecom.model.Notification;
import com.wirecard.ecom.model.Notifications;
import com.wirecard.ecom.model.OrderItem;
import com.wirecard.ecom.model.RequestedAmount;
import com.wirecard.ecom.model.Shipping;
import com.wirecard.ecom.model.ShippingMethod;
import com.wirecard.ecom.model.out.PaymentResponse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.wirecard.ecom.examples.Constants.REQUEST_TIMEOUT;
import static com.wirecard.ecom.examples.Constants.URL_EE_TEST;
import static com.wirecard.ecom.examples.GooglePayActivity.GOOGLE_PAY_ACTIVITY_REQUEST_CODE;
import static com.wirecard.ecom.examples.GooglePayActivity.TAG_ERROR_STATUS;
import static com.wirecard.ecom.examples.GooglePayActivity.TAG_PAYMENT_RESPONSE_GOOGLE_PAY;
import static com.wirecard.ecom.examples.GooglePayActivity.TAG_USER_CANCELED;

public class StartActivity extends AppCompatActivity {
    private Context mContext = this;
    private PaymentObjectProvider mPaymentObjectProvider = new PaymentObjectProvider(new OptionalFieldsProvider());

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

    public void makeSimpleCardTokenPayment(View view) {
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getCardTokenPayment());
    }

    public void makeAnimatedCardPayment(View view) {
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getCardPayment(true));
    }

    public void makeCardFieldPayment(View view) {
        startActivity(new Intent(mContext, CardFieldActivity.class));
    }

    public void makeAnimatedCardFieldPayment(View view) {
        startActivity(new Intent(mContext, AnimatedCardFieldActivity.class));
    }

    public void makeTokenAnimatedCardFieldPayment(View view){
        startActivity(new Intent(mContext, TokenAnimatedCardFieldActivity.class));
    }

    public void makeFragmentCardFieldPayment(View view){
        startActivity(new Intent(mContext, KotlinCardFieldFragmentImplActivity.class));
    }

    public void makeCardPaymentWithOptionalParameters(View view){
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getCardPaymentWithOptionalData());
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

    public void makeSDKManagedGooglePayPayment(View view){
        new Client(mContext, URL_EE_TEST)
                .startPayment(mPaymentObjectProvider.getGooglePayPayment());
    }

    public void makeMerchantManagedGooglePayPayment(View view){
        startActivityForResult(new Intent(this, GooglePayActivity.class), GOOGLE_PAY_ACTIVITY_REQUEST_CODE);
    }

    public void kotlinDemo(View view){
        startActivity(new Intent(mContext, KotlinStartActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_PAY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentData paymentData = data.getParcelableExtra(TAG_PAYMENT_RESPONSE_GOOGLE_PAY);
                String errorMessage = data.getStringExtra(TAG_ERROR_STATUS);
                boolean userCanceled = data.getBooleanExtra(TAG_USER_CANCELED, false);
                if (paymentData != null) {
                    GooglePayPayment googlePayPayment = mPaymentObjectProvider.getGooglePayPayment(null);
                    googlePayPayment.setPaymentData(paymentData);
                    new Client(mContext, URL_EE_TEST).startPayment(googlePayPayment);
                } else if (userCanceled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("User canceled")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.cancel();
                            });
                    builder.setMessage("User canceled")
                            .show();
                } else if (errorMessage != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.cancel();
                            });
                    builder.setMessage(errorMessage)
                            .show();
                }
            }
        } else if(requestCode == Client.PAYMENT_SDK_REQUEST_CODE) {
            Serializable paymentSdkResponse = data.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE);
            if (paymentSdkResponse instanceof PaymentResponse) {
                String formattedResponse = ResponseHelper.getFormattedResponse((PaymentResponse) paymentSdkResponse);
                Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
