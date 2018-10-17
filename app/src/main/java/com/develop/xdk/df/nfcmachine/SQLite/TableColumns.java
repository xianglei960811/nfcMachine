package com.develop.xdk.df.nfcmachine.SQLite;

import com.develop.xdk.df.nfcmachine.constant.C;

/**
 *
 * 数据库表，字段（方便统一管理）
 */
public interface TableColumns {
    public static interface USER_COLUMNS{
        public static final String TABLE_USERS = C.U_TABLE_NAME;
        public static final String FIELD_ID = C.U_ID_NAME;//字段名
        public static final String FIELD_NAME = C.U_NAME_NAME;
        public static final String FIELD_CARD_ID =C.U_CARD_ID_NAME;
        public static final String FIELD_ACCONT_ID = C.U_ACCONT_ID;
        public static final String FIELD_CASH_MONEY = C.U_CASH_MONEY_NAME;
        public static final String FIELD_SUBSIDY_MONEY = C.U_SUBSIDY_MONEY_NAME;
        public static final String FIELD_IS_LOSS = C.U_IS_LOSS_NAME;
        public static final String FIELD_CONSUME_DATE =C.U_CONSUME_DATA_NAME;
    }
    public static interface CONSUME_COUMNS{
        public static final String TABLE_USERS = C.C_TABLE_NAME;
        public static final String FIELD_ID = C.C_ID_NAME;//字段名
        public static final String FIELD_NAME = C.C_NAME_NAME;
        public static final String FIELD_CARD_ID =C.C_CARD_ID_NAME;
        public static final String FIELD_CASH_MONEY = C.C_CASH_MONEY_NAME;
        public static final String FIELD_SUBSIDY_MONEY = C.C_SUBSIDY_MONEY_NAME;
        public static final String FIELD_CONSUME_MONEY = C.C_CONSUME_MONEY_NAME;
        public static final String FIELD_IDATE =C.C_DATA_NAME;
        public static final String FIFLE_IS_HANDLE = C.C_IS_HANDLE_NAME;
        public static final String FIFLE_IS_ONLINE = C.C_IS_ONLINE_NAME;
    }
}
