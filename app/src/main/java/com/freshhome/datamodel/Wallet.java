package com.freshhome.datamodel;

public class Wallet {
    public String getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(String wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getWallet_amount() {
        return wallet_amount;
    }

    public void setWallet_amount(String wallet_amount) {
        this.wallet_amount = wallet_amount;
    }

    public String getWallet_currency() {
        return wallet_currency;
    }

    public void setWallet_currency(String wallet_currency) {
        this.wallet_currency = wallet_currency;
    }

    public String getWallet_message() {
        return wallet_message;
    }

    public void setWallet_message(String wallet_message) {
        this.wallet_message = wallet_message;
    }

    public String getWallet_time() {
        return wallet_time;
    }

    public void setWallet_time(String wallet_time) {
        this.wallet_time = wallet_time;
    }

    public String getAmount_status() {
        return amount_status;
    }

    public void setAmount_status(String amount_status) {
        this.amount_status = amount_status;
    }

    String wallet_id, wallet_amount, wallet_currency, wallet_message, wallet_time,amount_status;
}
