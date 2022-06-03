package  CommonClasses;

import java.io.*;

public enum Request implements Serializable {
    CHECK_USERNAME,
    SIGN_IN,
    SIGN_UP,
    CHANNEL_LIST,
    ENTER_CHANNEL,
    LOAD_CHANNEL,
    LOAD_PV,
    SERVER_LIST,
    ENTER_SERVER,
    PV_LIST,
    ENTER_PV,
    BLOCK_IN_PV,
    BACK,
    SHOW_FRIENDSHIP_REQESTS,
    ACCEPT_REQUEST,
    REJECT_REQUEST,
    CREATE_SERVER,
    CREATE_CHANNEL,
    CHECK_SERVER_NAME,
    SIGN_OUT,
    CHECK_CHANNEL_NAME,
}
