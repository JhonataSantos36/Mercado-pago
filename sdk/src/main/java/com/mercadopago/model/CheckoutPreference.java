package com.mercadopago.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CheckoutPreference implements Serializable {

    private String id;
    private List<Item> items;
    private Payer payer;
    private PaymentMethodPreference paymentMethods;

    //////////////////////////////////////////////////////////////////
    private Date expirationDateTo;
    private Date expirationDateFrom;


    public void validate() {
        if (!this.itemsValid())
        {
            throw new IllegalStateException("Los items son invalidos");
        }
        else if(this.isExpired()){
            throw new IllegalStateException("La preferencia expiró");
        }
        else if (!this.isActive()){
            throw new IllegalStateException("La preferencia está inactiva");
        }
        else if (!this.isInstallmentsValid()){
            throw new IllegalStateException("Installments inválidas");
        }
        else if (!this.isExcludedPaymentTypesValid()){
            throw new IllegalStateException("Se excluyen todos los PaymentTypes");
        }
    }

    public boolean isExcludedPaymentTypesValid() {
        return paymentMethods.getExcludedPaymentTypes().size()<PaymentType.getAllPaymentTypes().size();
    }

    public boolean isInstallmentsValid() {
        return (paymentMethods.getInstallments()>0) && (paymentMethods.getDefaultInstallments()>0);
    }


    public Boolean itemsValid() {

        boolean valid = true;

        if(this.items == null || this.items.isEmpty())
        {
            valid = false;
        }
        else {
            String firstItemCurrencyId = items.get(0).getCurrencyId();

            if(firstItemCurrencyId != null) {
                for (Item item : this.items) {
                    if (!isItemValid(item) ||
                            item.getCurrencyId() == null ||
                            !firstItemCurrencyId.equals(item.getCurrencyId())) {
                        valid = false;
                        break;
                    }
                }
            }
            else {
                valid = false;
            }
        }
        return valid;
    }


    private boolean isItemValid(Item item) {
        Boolean valid = false;

        if (item.getId() != null) {
            if (item.getQuantity() != null && item.getQuantity() > 0 ) {
                if (item.getUnitPrice() != null && item.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0){
                    valid = true;
                }
            }
        }
        return valid;
    }


    public Boolean isExpired(){
        Date date = new Date();
        if (expirationDateTo != null){
            return date.after(expirationDateTo);
        }
        else{
            return false;
        }
    }

    public Boolean isActive(){
        Date date = new Date();
        if (expirationDateFrom != null){
            return date.after(expirationDateFrom);
        }
        else{
            return false;
        }
    }


    public void setExpirationDate(Date date) {
        this.expirationDateTo = date;
    }

    public void setActiveFrom(Date date) {
        this.expirationDateFrom = date;
    }

    public void setPaymentMethods(PaymentMethodPreference paymentMethods){
        this.paymentMethods = paymentMethods;
    }

    ////////////////////////////////////////////////////////////////

    public BigDecimal getAmount() {

        BigDecimal totalAmount = BigDecimal.ZERO;
        for(Iterator<Item> i = items.iterator(); i.hasNext(); ) {
            Item item = i.next();
            if ((item != null) && (item.getUnitPrice() != null) && (item.getQuantity() != null)) {
                totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            } else {
                return null;
            }
        }
        return totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public Integer getMaxInstallments() {
        if(paymentMethods != null)
            return paymentMethods.getInstallments();
        else
            return null;    }


    public Integer getDefaultInstallments() {
        if(paymentMethods != null)
            return paymentMethods.getDefaultInstallments();
        else
            return null;    }

    public List<String> getExcludedPaymentMethods() {
        if(paymentMethods != null)
            return paymentMethods.getExcludedPaymentMethodIds();
        else
            return null;
    }

    public List<String> getExcludedPaymentTypes() {
        if(paymentMethods != null)
            return paymentMethods.getExcludedPaymentTypes();
        else
            return null;    }

    public String getDefaultPaymentMethodId() {
        if(paymentMethods != null)
            return paymentMethods.getDefaultPaymentMethodId();
        else
            return null;
    }



}
