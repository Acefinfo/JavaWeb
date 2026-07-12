/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.PaymentService;

/**
 *
 * @author DELL
 */
@WebServlet("/esewa/failure")
public class EsewaFailureServlet extends HttpServlet {

    @EJB
    private PaymentService paymentService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String transactionUuid = request.getParameter("transaction_uuid");
            if (transactionUuid != null) {
                paymentService.finalizePayment(transactionUuid, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/payment-failed.xhtml");
    }
}