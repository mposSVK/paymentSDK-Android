package com.wirecard.ecom.examples;

import com.google.android.gms.wallet.PaymentData;
import com.wirecard.ecom.card.model.CardBundle;
import com.wirecard.ecom.card.model.CardFieldPayment;
import com.wirecard.ecom.card.model.CardPayment;
import com.wirecard.ecom.googlepay.model.GooglePayPayment;
import com.wirecard.ecom.model.AccountHolder;
import com.wirecard.ecom.model.CardToken;
import com.wirecard.ecom.model.Notification;
import com.wirecard.ecom.model.TransactionType;
import com.wirecard.ecom.paypal.model.PayPalPayment;
import com.wirecard.ecom.sepa.model.SepaPayment;
import com.wirecard.ecom.zapp.model.ZappPayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class PaymentObjectProvider {

    public SepaPayment getSepaPaymentObject() {
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "9105bb4f-ae68-4768-9c3b-3eda968f57ea";
        String secretKey = "d1efed51-4cb9-46a5-ba7b-0fdc87a66544";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.AUTHORIZATION;
        BigDecimal amount = new BigDecimal(5);
        String currency = "EUR";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        return new SepaPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setMerchantName("JOHN DOE")
                .build();

    }

    public CardPayment getCardPayment(boolean isAnimated) {
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1";
        String secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(5);
        String currency = "EUR";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        CardPayment cardPayment = new CardPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .build();
        cardPayment.setRequireManualCardBrandSelection(true);
        cardPayment.setAnimatedCardPayment(isAnimated);
        return cardPayment;
    }

    public CardPayment getCardTokenPayment(){
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "9105bb4f-ae68-4768-9c3b-3eda968f57ea";
        String secretKey = "d1efed51-4cb9-46a5-ba7b-0fdc87a66544";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(5);
        String currency = "EUR";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        CardPayment cardPayment = new CardPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .build();
        cardPayment.setRequireManualCardBrandSelection(true);

        CardToken cardToken = new CardToken();
        cardToken.setTokenId("4304509873471003");
        cardToken.setMaskedAccountNumber("401200******1003");

        cardPayment.setCardToken(cardToken);

        return cardPayment;
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

    public PayPalPayment getPayPalPayment(){
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "9abf05c1-c266-46ae-8eac-7f87ca97af28";
        String secretKey = "5fca2a83-89ca-4f9e-8cf7-4ca74a02773f";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.DEBIT;
        BigDecimal amount = new BigDecimal(5);
        String currency = "EUR";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        PayPalPayment payPalPayment = new PayPalPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setPeriodic(null)
                .setRiskReferenceId(null)
                .build();

        ArrayList<Notification> notifications = new ArrayList<>();
        Notification notification = new Notification();
        notification.setUrl("api-test.wirecard.com/engine/mobile/v2/notify");
        notifications.add(notification);
        payPalPayment.setNotifications(notifications);

        return payPalPayment;
    }

    public ZappPayment getZappPayment(){
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "70055b24-38f1-4500-a3a8-afac4b1e3249";
        String secretKey = "4a4396df-f78c-44b9-b8a0-b72b108ac465";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.DEBIT;
        BigDecimal amount = new BigDecimal(5);
        String currency = "GBP";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        ZappPayment zappPayment = new ZappPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setZappTransactionType("PAYMT")
                .setZappDeliveryType("SERVICE")
                .build();

        zappPayment.setIpAddress("127.0.0.1");
        return zappPayment;
    }

    public GooglePayPayment getGooglePayPayment(PaymentData paymentData){
        String timestamp = SignatureHelper.generateTimestamp();
        String merchantID = "9fcacb0d-b46a-4ce2-867b-6723687fdba1";
        String secretKey = "bd60d7b0-b5a0-4ffe-b2db-e004a0fce893";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(5);
        String currency = "USD";
        String signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.getValue(), amount, currency, secretKey);

        GooglePayPayment googlePayPayment = new GooglePayPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setPaymentData(paymentData)
                .build();

        return googlePayPayment;
    }

    public GooglePayPayment getGooglePayPayment(){
        return getGooglePayPayment(null);
    }
}
