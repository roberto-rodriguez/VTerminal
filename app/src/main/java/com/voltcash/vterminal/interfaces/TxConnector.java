package com.voltcash.vterminal.interfaces;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public interface TxConnector {

    public void checkAuthLocationConfig(ServiceCallback callback)throws Exception;

    public void tx(final ServiceCallback callback) throws Exception;

    public void balanceInquiry(final ServiceCallback callback) throws Exception;

    public void cardToBank(String operation, final ServiceCallback callback) throws Exception;

    public void activityReport(String startDate, String endDate, final ServiceCallback callback) throws Exception;

    public void calculateFee(String operation, String amount, String card, final ServiceCallback callback) throws Exception;
}
