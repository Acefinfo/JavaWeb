/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.InvoiceDao;
import entity.Invoice;
import entity.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "invoiceBean")
@ViewScoped
public class InvoiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InvoiceDao invoiceDao;

    private List<Invoice> customerInvoices;
    private List<Invoice> sellerInvoices;
    private List<Invoice> allInvoices;

    private BarChartModel revenueByProductModel;
    private LineChartModel revenueOverTimeModel;

    private User getCurrentUser() {
        Object sessionUser = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("currentUser");
        return (sessionUser instanceof User) ? (User) sessionUser : null;
    }

    public List<Invoice> getCustomerInvoices() {
        User current = getCurrentUser();
        if (current != null) {
            customerInvoices = invoiceDao.findByBuyerId(current.getId());
        }
        return customerInvoices;
    }

    public List<Invoice> getSellerInvoices() {
        User current = getCurrentUser();
        if (current != null) {
            sellerInvoices = invoiceDao.findBySellerId(current.getId());
        }
        return sellerInvoices;
    }

    public List<Invoice> getAllInvoices() {
        if (allInvoices == null) {
            allInvoices = invoiceDao.findAll();
        }
        return allInvoices;
    }
    
    public void downloadCustomerInvoicesExcel() {
        List<Invoice> invoices = getCustomerInvoices();
        List<String[]> rows = new java.util.ArrayList<String[]>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm");
        if (invoices != null) {
            for (Invoice i : invoices) {
                rows.add(new String[]{
                    fmt.format(i.getOrderDate()),
                    i.getProductName(),
                    i.getSellerUsername(),
                    String.valueOf(i.getUnitPrice()),
                    String.valueOf(i.getQuantity()),
                    String.valueOf(i.getTotalPrice())
                });
            }
        }
        util.ExcelExportUtil.export("My Invoices",new String[]{"Date", "Product", "Seller", "Unit Price", "Qty", "Total"},rows, "my-invoices.xlsx");
    }

    public void downloadInvoicePdf(Invoice inv){
        util.InvoicePdfUtil.download(inv);
    }
    
    public void downloadSellerInvoicesExcel(){
        List<Invoice> invoices = getSellerInvoices();
        List<String[]> rows = new java.util.ArrayList<String[]>();
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy HH:mm");
        
        if (invoices != null){
            for (Invoice i : invoices){
                rows.add(new String[]{
                date.format(i.getOrderDate()),
                i.getProductName(),
               String.valueOf(i.getUnitPrice()),
               String.valueOf(i.getQuantity()),
               String.valueOf(i.getTotalPrice())
            });
            }
        }
        util.ExcelExportUtil.export("My Sales", new String[]{"Date", "Product", "Unit Price", "Qty Sold", "Total"},rows, "my-sales.xlsx");
    }

    public void downloadAllInvoicesExcel() {
    List<Invoice> invoices = getAllInvoices();
    List<String[]> rows = new java.util.ArrayList<String[]>();
    SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm");
    if (invoices != null) {
        for (Invoice i : invoices) {
            rows.add(new String[]{
                fmt.format(i.getOrderDate()),
                i.getBuyer() != null ? i.getBuyer().getUsername() : "",
                i.getProductName(),
                i.getSellerUsername(),
                String.valueOf(i.getQuantity()),
                String.valueOf(i.getTotalPrice())
            });
        }
    }
    util.ExcelExportUtil.export("All Invoices", new String[]{"Date", "Buyer", "Product", "Seller", "Qty", "Total"},rows, "all-invoices.xlsx");
}

    public int getSellerInvoiceCount() {
        return getSellerInvoices() == null ? 0 : getSellerInvoices().size();
    }

    public int getAllInvoiceCount() {
        return getAllInvoices() == null ? 0 : getAllInvoices().size();
    }

    public double getSellerTotalRevenue() {
        double total = 0.0;
        List<Invoice> invoices = getSellerInvoices();
        if (invoices != null) {
            for (Invoice i : invoices) {
                total += i.getTotalPrice();
            }
        }
        return total;
    }

    public double getPlatformTotalRevenue() {
        double total = 0.0;
        List<Invoice> invoices = getAllInvoices();
        if (invoices != null) {
            for (Invoice i : invoices) {
                total += i.getTotalPrice();
            }
        }
        return total;
    }

    private void addToTotal(Map<String, Double> map, String key, Double amount) {
        Double existing = map.get(key);
        if (existing == null) {
            map.put(key, amount);
        } else {
            map.put(key, existing + amount);
        }
    }

    public BarChartModel getRevenueByProductModel() {
        if (revenueByProductModel == null) {
            Map<String, Double> revenueByProduct = new LinkedHashMap<String, Double>();
            List<Invoice> invoices = getSellerInvoices();
            if (invoices != null) {
                for (Invoice i : invoices) {
                    addToTotal(revenueByProduct, i.getProductName(), i.getTotalPrice());
                }
            }

            ChartSeries series = new ChartSeries();
            series.setLabel("Revenue (Rs)");
            for (Map.Entry<String, Double> entry : revenueByProduct.entrySet()) {
                series.set(entry.getKey(), entry.getValue());
            }

            BarChartModel model = new BarChartModel();
            model.addSeries(series);
            model.setTitle("Revenue by Product");
            model.setLegendPosition(null);
            model.setShowPointLabels(true);

            revenueByProductModel = model;
        }
        return revenueByProductModel;
    }

    public LineChartModel getRevenueOverTimeModel() {
        if (revenueOverTimeModel == null) {
            Map<String, Double> revenueByDay = new LinkedHashMap<String, Double>();
            SimpleDateFormat fmt = new SimpleDateFormat("MMM dd");
            List<Invoice> invoices = getSellerInvoices();
            if (invoices != null) {
                for (int idx = invoices.size() - 1; idx >= 0; idx--) {
                    Invoice i = invoices.get(idx);
                    String day = fmt.format(i.getOrderDate());
                    addToTotal(revenueByDay, day, i.getTotalPrice());
                }
            }

            ChartSeries series = new ChartSeries();
            series.setLabel("Revenue (Rs)");
            for (Map.Entry<String, Double> entry : revenueByDay.entrySet()) {
                series.set(entry.getKey(), entry.getValue());
            }

            LineChartModel model = new LineChartModel();
            model.addSeries(series);
            model.setTitle("Revenue Over Time");
            model.setLegendPosition(null);

            revenueOverTimeModel = model;
        }
        return revenueOverTimeModel;
    }
}