package com.voltcash.vterminal.util;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public final class Field {
    public static final String ERROR_MESSAGE = "errorMessage";

    public static class AUTH{
        public static final String TERMINAL_USERNAME      = "username";
        public static final String TERMINAL_PASSWORD      = "password";
        public static final String TERMINAL_SERIAL_NUMBER = "serialNumber";
        public static final String MERCHANT_NAME          = "merchant";

        public static final String CLERK_EMAIL      = "clerkEmail";
        public static final String CLERK_PASSWORD   = "clerkPassword";
        public static final String CLERK_FIRST_NAME = "clerkFirstName";
        public static final String CLERK_LAST_NAME  = "clerkLastName";
        public static final String CLERK_ID         = "clerkID";
        public static final String SESSION_TOKEN    = "sessionToken";
    }

    public static class TX{
        public static final String TX_FIELD    = "txField";
        public static final String CHECK_BACK  = "checkBack";
        public static final String CHECK_FRONT  = "checkFront";
        public static final String ID_FRONT    = "idFront";
        public static final String ID_BACK     = "idBack";
        public static final String AMOUNT      = "amount";
        public static final String CARD_NUMBER = "cardNumber";
        public static final String OPERATION   = "operation";
        public static final String DL_DATA_SCAN= "dlDataScan";
        public static final String SSN         = "ssn";
        public static final String PHONE       = "phone";

        //------- response ------
        public static final String CARD_LOAD_FEE = "CRDLDF";
        public static final String ACTIVATION_FEE= "ACTIVATION_FEE";
        public static final String CARD_EXIST    = "CARD_EXIST";
        public static final String BALANCE       = "BALANCE";
        public static final String C2B_FEE       = "fee";

        public static final String REQUEST_ID      = "REQUEST_ID";
        public static final String EXISTACH        = "EXISTACH";
        public static final String MERCHANT_NAME   = "MERCHANT_NAME";
        public static final String CUSTUMER_ADDRESS= "CUSTUMER_ADDRESS";
        public static final String CUSTUMER_NAME   = "CUSTUMER_NAME";
        public static final String BANK_NAME       = "BANK_NAME";
        public static final String ROUTING_BANK_NUMBER = "ROUTING_BANK_NUMBER";
        public static final String ACCOUNT_NUMBER  = "ACCOUNT_NUMBER";

        public static final String CLIENT_ID        = "CLIENT_ID";
        public static final String EXCLUDE_SMS      = "EXCLUDE_SMS";
    }
}
