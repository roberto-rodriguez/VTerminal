package com.voltcash.vterminal.interfaces;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public interface TxConnector {
    public void checkAuthLocationConfig(ServiceCallback callback)throws Exception;

    public void tx(final ServiceCallback callback) throws Exception;
}
