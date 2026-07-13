/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.PendingPaymentDao;
import entity.PendingPayment;
import java.io.IOException;
import javax.ejb.EJB;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.EsewaUtil;
import util.PaymentService;


/**
 *
 * @author DELL
 */
@WebServlet("/esewa/success")
public class EsewaSuccessServlet extends HttpServlet {

    @EJB
    private PaymentService paymentService;

    @EJB
    private PendingPaymentDao pendingPaymentDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encodedData = request.getParameter("data");
        String redirectPage = "payment-failed.xhtml";

        try {
            if (encodedData != null) {
                JsonObject json = EsewaUtil.decodeCallbackData(encodedData);

                String status = EsewaUtil.jsonValueAsString(json, "status");
                String transactionUuid = EsewaUtil.jsonValueAsString(json, "transaction_uuid");
                String totalAmount = EsewaUtil.jsonValueAsString(json, "total_amount");
                String signedFieldNames = EsewaUtil.jsonValueAsString(json, "signed_field_names");
                String signature = EsewaUtil.jsonValueAsString(json, "signature");

                String message = EsewaUtil.buildMessageFromSignedFields(json, signedFieldNames);
                String expectedSignature = EsewaUtil.generateSignature(EsewaUtil.SECRET_KEY, message);

                PendingPayment pendingPayment = pendingPaymentDao.findByUuid(transactionUuid);

               boolean signatureValid = expectedSignature.equals(signature);
                boolean statusComplete = "COMPLETE".equals(status);
                boolean amountMatches = pendingPayment != null
                        && EsewaUtil.amountsMatch(EsewaUtil.formatAmount(pendingPayment.getAmount()), totalAmount);
                boolean verifiedWithEsewaApi = EsewaUtil.verifyTransactionStatus(transactionUuid, totalAmount);

                System.out.println("=== ESEWA CALLBACK DEBUG ===");
                System.out.println("raw json: " + json);
                System.out.println("status=" + status + " uuid=" + transactionUuid + " totalAmount=" + totalAmount);
                System.out.println("expectedSignature=" + expectedSignature + " gotSignature=" + signature);
                System.out.println("pendingPayment amount=" + (pendingPayment != null ? pendingPayment.getAmount() : "null"));
                System.out.println("signatureValid=" + signatureValid + " statusComplete=" + statusComplete
                        + " amountMatches=" + amountMatches + " verifiedWithEsewaApi=" + verifiedWithEsewaApi);

                if (!signatureValid) {
                    System.out.println("FAIL REASON: signature mismatch - check SECRET_KEY / signed_field_names order.");
                } else if (!statusComplete) {
                    System.out.println("FAIL REASON: eSewa reported status=" + status + " (not COMPLETE).");
                } else if (!amountMatches) {
                    System.out.println("FAIL REASON: amount mismatch - pendingPayment not found, or amount differs. "
                            + "pendingPayment=" + (pendingPayment != null ? EsewaUtil.formatAmount(pendingPayment.getAmount()) : "null")
                            + " vs esewa totalAmount=" + totalAmount);
                } else if (!verifiedWithEsewaApi) {
                    System.out.println("FAIL REASON: server-to-server status check with eSewa failed - "
                            + "check outbound internet/HTTPS access from this server to " + EsewaUtil.STATUS_URL
                            + " (see the 'eSewa status check' log line above for the real cause).");
                }

                if (signatureValid && statusComplete && amountMatches && verifiedWithEsewaApi) {
                    String result = paymentService.finalizePayment(transactionUuid, true);
                    if ("COMPLETE".equals(result)) {
                        redirectPage = "payment-success.xhtml";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ESEWA CALLBACK EXCEPTION: " + e);
        }

        response.sendRedirect(request.getContextPath() + "/" + redirectPage);
    }
}