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
    }
}
