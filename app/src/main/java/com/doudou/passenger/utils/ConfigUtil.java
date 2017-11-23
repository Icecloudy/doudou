package com.doudou.passenger.utils;

public class ConfigUtil {

    //聚合数据航班接口
    public static final String APP_KEY = "2c34d50659514d5fccb322ec68fba524";
    //
    public static final String GET_FLIGHT_MSG = "http://op.juhe.cn/flight/ff";    //"http://apis.juhe.cn/plan/snew";
    //------------------------推送标识  start
    /**
     * payload(string):'neworder' 新订单，用户下单后推送                         司机端
     * payload(string):'shipping' 司机正前来接驾，司机接单后会推送                乘客端
     * payload(string):'driverReady' 司机已就位                                乘客端
     * payload(string):'startCharging' 开始收费                                乘客端
     * payload(string):'cancelOrder' 取消订单                                  司机端或乘客端
     * payload(string):'debit' 取消订单后的扣款通知                              乘客端
     * payload(string):'payment' 提醒乘客前往支付订单                            乘客端
     * payload(string):'txt' 其他推送，只用于推送文字内容，无需任何后继操作         司机端或乘客端
     */
    public static final String PAYLOAD_neworder = "neworder";
    public static final String PAYLOAD_shipping = "shipping";
    public static final String PAYLOAD_driverReady = "driverReady";
    public static final String PAYLOAD_startCharging = "startCharging";
    public static final String PAYLOAD_cancelOrder = "cancelOrder";
    public static final String PAYLOAD_debit = "debit";
    public static final String PAYLOAD_payment = "payment";
    public static final String PAYLOAD_txt = "txt";
    public static final String PAYLOAD_SUC = "orderSuccess";
    public static final String PAYLOAD_LOGOUT = "offLine";
    public static final String WECHAT_PAY_APPID = "wx20098ab040ba946d";
    public static final String QQ_SHARE_APPID = "1106378904";

    public static int WECHAT_PAY_CODE = 100;

    //------------------------推送标识  end

    public static final String SERVER_URL = "http://doudou.zzwjhy.com/index.php";
    //     public static final String SERVER_URL = "http://car.lexingcar.com/index.php";
    public static final int SUCCESS_CODE = 200;
    /**
     * broadcast start
     */
    public static final String EXTRA_CODE = "broadcast_intent";
    public static final String FILTER_CODE = "broadcast_filter";
    public static final String UPDATE_USER = "update_user";//租客登记后通知房屋房里更新数据
    /**
     * broadcast end
     */
    public static final String GET_CODE = "/App/Username/verifiDriver";
    public static final String LOGIN = "/App/Username/loginDriver";
    public static final String REGISTER = "/App/Username/realname";

    public static final String MESSAGE = "/App/Message/message";
    public static final String CLEAN_NEWS = "/App/Message/clear";
    public static final String UPDATA_USERINFO = "/App/Username/personalUpdate";
    public static final String SET_DEFAULT_ADDRESS = "/App/AppSystem/setDefaultAddress";
    public static final String GET_DEFAULT_ADDRESS = "/App/AppSystem/getDefaultAddress";
    public static final String GET_NEAR_DRIVER = "/App/Username/getNearbyDriver";
    public static final String GET_ORDER_CHARGE_PARAMS= "/App/Place/orderChargeParams";
    public static final String SEND_ORDER = "/App/Place/sendOrder";
    public static final String GET_ORDER_DRIVER_POSITION = "/App/Service/getDriverPosition";

    public static final String SET_PAY_PASSWORD = "/App/Member/setPayPassword";
    public static final String GET_PAY_PASSWORD = "/App/Member/getPayPassword";

    public static final String BALANCE_PAY = "/App/AppSystem/balancePay";
    public static final String ALI_PAY = "/App/Alipay/alipay";//

    public static final String WX_PAY = "/App/WXPay/wxPay";
    public static final String SET_THOUGHT = "/App/MyCenter/setThought";
    public static final String SET_PAY_STATE = "/App/Member/editUnPayPass";


    public static final String GET_ORDER_DETAILS = "/App/Place/getOrderDetail";

    public static final String GET_ORDER_DRIVER = "/App/Place/getDriverByOrder";
    public static final String CANCEL_ORDER = "/App/Place/CancelOrder";
    public static final String CHANGE_PAY_PWD = "/App/Api/verifiPayPass";

    public static final String GET_TRIP_RECORD = "/App/MyCenter/getTravelRecord";
    public static final String GET_TRIP_RECORD_DETAILS = "/App/MyCenter/getTravelRecordDetail";


    public static final String GET_BILL_DETAIL = "/App/AppSystem/getTransactionInfo";
    public static final String GET_CAR_INFO = "/App/Book/getBrandList";

    public static final String SEND_BOOK_ORDER = "/App/Book/sendBookOrder";

    public static final String GET_SYSTEM_AdV = "/App/AppSystem/getAdvertisement";

    public static final String GET_SYSTEM_AGREEMENT = "/App/AppSystem/getAppSystemInfo";

    public static final String TO_ANOTHER = "/App/Place/toAnother";
    public static final String TO_ONE_DRIVER = "/App/Place/toOneDriver";
    public static final String FEEDBACK = "/App/MyCenter/setFeedback";

    public static final String APP_UPDATE = "/App/App/index";
    public static final String URL_CAR_MALL = "http://mall.lexingcar.com/mobile/?u=3109";

    public static final String GET_SCORE_LIST = "/App/AppSystem/getIntegralList";

    public static final String GET_CAR_LIST = "/App/Carshop/selectbrand";

    public static final String GET_CAR_BANNER = "/App/Carshop/shopad";

    public static final String GET_CAR_MODEL = "/App/Carshop/selectmodel";

    public static final String GET_CAR_DETAILS = "/App/Carshop/modeldetails";
    public static final String GET_CAR_DETAILS_LIST = "/App/Carshop/selectcar";

    public static final String GET_COUPON = "/App/Coupon/getMyCouponList";

    public static final String GET_INVOICE_RECORD = "/App/MyCenter/getTravelRecordInvoice";

    public static final String OPEN_INVOICE = "/App/MyCenter/createInvoice";//getInvoiceList
    public static final String OPEN_INVOICE__LIST = "/App/MyCenter/getInvoiceList";//getInvoiceList

    public static final String GET_SHARE_DATA = "/App/MyCenter/shareHistory";
    // /App/MyCenter/getDiscount
    public static final String GET_DISCOUNT = "/App/MyCenter/getDiscount";

    //  http://car.lexingcar.com/index.php?s=/App/Username/getShopPwd
    public static final String GET_CAR_PWD = SERVER_URL + "?s=/App/Username/getShopPwd";

}


