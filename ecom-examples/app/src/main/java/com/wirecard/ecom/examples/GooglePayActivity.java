package com.wirecard.ecom.examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.wirecard.ecom.googlepay.model.googlepay.Transaction;

import java.math.BigDecimal;
import java.util.Arrays;

public class GooglePayActivity extends AppCompatActivity {

    private PaymentsClient paymentsClient;

    public final static int GOOGLE_PAY_ACTIVITY_REQUEST_CODE = 600;
    public final static int LOAD_PAYMENT_DATA_REQUEST_CODE = 992;
    public final static String TAG_PAYMENT_RESPONSE_GOOGLE_PAY = "TAG_PAYMENT_RESPONSE_GOOGLE_PAY";
    public final static String TAG_USER_CANCELED = "TAG_USER_CANCELED";
    public final static String TAG_ERROR_STATUS = "TAG_ERROR_STATUS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_pay);

        paymentsClient = Wallet.getPaymentsClient(
                this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build());

        TransactionInfo transactionInfo = new Transaction(new BigDecimal(5.00).toPlainString(),
                "USD").getTransactionInfo();

        PaymentMethodTokenizationParameters.Builder paramsBuilder = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "wirecard")
                .addParameter("gatewayMerchantId", "9fcacb0d-b46a-4ce2-867b-6723687fdba1");

        PaymentDataRequest paymentDataRequest = PaymentDataRequest.newBuilder()
                .setPhoneNumberRequired(true)
                .setEmailRequired(true)
                .setShippingAddressRequired(true)
                .addAllowedPaymentMethods(Arrays.asList(WalletConstants.PAYMENT_METHOD_CARD, WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD))
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(
                                        Arrays.asList(
                                                WalletConstants.CARD_NETWORK_VISA,
                                                WalletConstants.CARD_NETWORK_AMEX,
                                                WalletConstants.CARD_NETWORK_DISCOVER,
                                                WalletConstants.CARD_NETWORK_JCB,
                                                WalletConstants.CARD_NETWORK_MASTERCARD,
                                                WalletConstants.CARD_NETWORK_OTHER
                                        )
                                )
                                .setAllowPrepaidCards(true)
                                .setBillingAddressRequired(true)
                                .build()
                )
                .setTransactionInfo(transactionInfo)
                .setPaymentMethodTokenizationParameters(paramsBuilder.build())
                .setUiRequired(true)
                .build();

        Task<PaymentData> futurePaymentData = paymentsClient.loadPaymentData(paymentDataRequest);
        AutoResolveHelper.resolveTask(futurePaymentData, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();

        if (LOAD_PAYMENT_DATA_REQUEST_CODE != requestCode)
            return;

        switch (resultCode){
            case Activity.RESULT_OK:
                PaymentData paymentData = PaymentData.getFromIntent(data);
                intent.putExtra(TAG_PAYMENT_RESPONSE_GOOGLE_PAY, paymentData);
                break;
            case Activity.RESULT_CANCELED:
                intent.putExtra(TAG_USER_CANCELED, true);
                break;
            case AutoResolveHelper.RESULT_ERROR:
                Status status = AutoResolveHelper.getStatusFromIntent(intent);
                intent.putExtra(TAG_ERROR_STATUS, status.getStatusMessage());
                break;
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
