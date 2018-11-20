package com.wirecard.ecom.examples;

import com.wirecard.ecom.model.Status;
import com.wirecard.ecom.model.out.PaymentResponse;

public class ResponseHelper {

    public static String getFormattedResponse(PaymentResponse paymentSdkResponse) {
        StringBuilder sb = new StringBuilder();
        // see com.wirecard.ecom.ResponseCode.kt
        sb.append("Response code: ")
                .append(paymentSdkResponse.getResponseCode());
        if(paymentSdkResponse.getErrorMessage() != null){
            sb.append(paymentSdkResponse.getErrorMessage());
        }
        if(paymentSdkResponse.getPayment() != null && paymentSdkResponse.getPayment().getStatuses() != null){
            sb.append("\n");
            for (Status status: paymentSdkResponse.getPayment().getStatuses()){
                sb.append(status.getCode());
                sb.append(": ");
                sb.append(status.getDescription());
            }
        }
        return sb.toString();
    }
}
