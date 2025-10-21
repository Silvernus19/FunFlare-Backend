package com.funflare.funflare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mpesa")
public class MpesaConfig {

    private String consumerKey;
    private String consumerSecret;
    private String passkey;
    private String businessShortcode;
    private String businessAccount;
    private String callbackUrl;
    private String stkPushUrl;
    private String tokenUrl;

    // Getters and setters
    public String getConsumerKey() { return consumerKey; }
    public void setConsumerKey(String consumerKey) { this.consumerKey = consumerKey; }

    public String getConsumerSecret() { return consumerSecret; }
    public void setConsumerSecret(String consumerSecret) { this.consumerSecret = consumerSecret; }

    public String getPasskey() { return passkey; }
    public void setPasskey(String passkey) { this.passkey = passkey; }

    public String getBusinessShortcode() { return businessShortcode; }
    public void setBusinessShortcode(String businessShortcode) { this.businessShortcode = businessShortcode; }

    public String getBusinessAccount() { return businessAccount; }
    public void setBusinessAccount(String businessAccount) { this.businessAccount = businessAccount; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public String getStkPushUrl() { return stkPushUrl; }
    public void setStkPushUrl(String stkPushUrl) { this.stkPushUrl = stkPushUrl; }

    public String getTokenUrl() { return tokenUrl; }
    public void setTokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; }
}