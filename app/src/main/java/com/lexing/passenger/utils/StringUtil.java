package com.lexing.passenger.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 根据传入参数，获取随机数的个数
     *
     * @param count 从1开始
     * @return
     */
    public static String getRandomNumber(int count) {

        long sum = 1;
        for (int i = 1; i < count; i++) {
            sum = sum * 10;
        }

        long num = (long) ((Math.random() * 9 + 1) * sum);
        return String.valueOf(num);
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getCurrentTimeDay() {
        return getCurrentTime("HH");
    }

    /**
     * 把分钟 转化成 天、时 、分
     *
     * @param min
     * @return day hour min
     */
    public static String minConvertDayHourMin(Double min) {
        String html = "0分";
        if (min != null) {
            Double m = (Double) min;
            String format;
            Object[] array;
            Integer days = (int) (m / (60 * 24));
            Integer hours = (int) (m / (60) - days * 24);
            Integer minutes = (int) (m - hours * 60 - days * 24 * 60);
            if (days > 0) {
                format = "%1$,d天%2$,d小时%3$,d分";
                array = new Object[]{days, hours, minutes};
            } else if (hours > 0) {
                format = "%1$,d小时%2$,d分";
                array = new Object[]{hours, minutes};
            } else {
                format = "%1$,d分";
                array = new Object[]{minutes};
            }
            html = String.format(format, array);
        }

        return html;
    }

    /**
     * 把 天、小时、分转换成分钟
     *
     * @param day
     * @param hour
     * @param min
     * @return min
     */
    public static int dayHourMinConvertMi2(int day, int hour, int min) {
        int days = day * 24 * 60;
        int hours = hour * 60;
        return days + hours + min;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 获取手机的MAC地址
     * 通过以下的方法，完美的解决了Android6.0获取不到MAC地址的问题了
     *
     * @return
     */

    public static String getLocalMacAddressFromWifiInfo() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }

    // 获取手机序列号
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 判断是否是中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        boolean isC = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            isC = true;
        } else {
            isC = false;
        }
        return isC;
    }


    /**
     * 处理含特殊字符的URL
     *
     * @param url
     * @return
     */
    public static String getRealUrl(String url) {
        try {
            url = URLEncoder.encode(url, "utf-8").replaceAll("\\+", "%20");
            url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getPackageVersionName(Context context) {
        String appVersion = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    /**
     * 获取app Name
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        String appName = context.getApplicationInfo().loadLabel(pm).toString();
        return appName;
    }


    /**
     * 返回小数点后一位的double
     *
     * @param a
     * @return
     */
    public static String doubleFormat(double a) {
        DecimalFormat df = new DecimalFormat("#####0.00");
        return String.valueOf(df.format(a));
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static String formatDateToStr(String strDate) {
        String dateString = strDate.substring(0, 10);
        return dateString;
    }

    /**
     * 比较日期
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            }
            if (dt1.getTime() < dt2.getTime()) {
                return -1;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 比较日期
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static long compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            long diff;
            if (dt1.getTime() - dt2.getTime() > 0) {
                diff = dt1.getTime() - dt2.getTime();
            } else {
                diff = dt2.getTime() - dt1.getTime();
            }
            return diff / (1000 * 60);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
//    public static void main(String args[]) {
//        Calendar c;
//        SimpleDateFormat df=new SimpleDateFormat("MM月dd日");
//
//        c=Calendar.getInstance();
//        System.out.println("今天 "+df.format(c.getTime()));
//
//        c=Calendar.getInstance();
//        c.setTime(new Date(c.getTime().getTime()+1000*60*60*24));
//        System.out.println("明天"+df.format(c.getTime()));
//
//        c=Calendar.getInstance();
//        c.setTime(new Date(c.getTime().getTime()+2*1000*60*60*24));
//        System.out.println("后天"+df.format(c.getTime()));
//    }

    /**
     * 获取今天明天后台
     *
     * @return
     */
    public static String[] getThreeDays() {
        String[] Day = new String[3];
        Calendar c;
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        c = Calendar.getInstance();
        Day[0] = "今天" + df.format(c.getTime());

        c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() + 1000 * 60 * 60 * 24));
        Day[1] = "明天" + df.format(c.getTime());

        c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() + 2 * 1000 * 60 * 60 * 24));
        Day[2] = "后天" + df.format(c.getTime());
        return Day;
    }
    public static String[] getTwoDays() {
        String[] Day = new String[2];
        Calendar c;
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() + 1000 * 60 * 60 * 24));
        Day[0] = "明天" + df.format(c.getTime());

        c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() + 2 * 1000 * 60 * 60 * 24));
        Day[1] = "后天" + df.format(c.getTime());
        return Day;
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobiles 手机号码
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[7])|(15[^4,\\D])|(17[0,3,5,6,7,8])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 返回金额
     *
     * @param editable
     * @return
     */
    public static double parseDoubleMoney(String editable) {
        double otherFee;
        if (!TextUtils.isEmpty(editable)) {
            if ((editable.startsWith("."))) {
                otherFee = Double.parseDouble(("0" + editable));
            } else {
                otherFee = Double.parseDouble(editable);
            }
            DecimalFormat df = new DecimalFormat("#####0.00");
            otherFee = Double.parseDouble(df.format(otherFee));
        } else {
            otherFee = 0;
        }
        return otherFee;
    }

    /** * 获得指定日期的前一天 *
     @param specifiedDay
      * @return
     * @throws Exception */
    public static String getSpecifiedDayBefore(String specifiedDay,int delayed){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = simpleDateFormat.parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int min=c.get(Calendar.MINUTE);
        c.set(Calendar.MINUTE,min+delayed);
        String minAfter=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(c.getTime());
        return minAfter;
    }



  /*
  public static String[] firsname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
            "楮", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许", "何", "吕",
            "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢",
            "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范",
            "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任",
            "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺",
            "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于",
            "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟",
            "平", "黄", "和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛",
            "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋",
            "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮",
            "蓝", "闽", "席", "季", "麻", "强", "贾", "路", "娄", "危", "江", "童",
            "颜", "郭", "梅", "盛", "林", "刁", "锺", "徐", "丘", "骆", "高", "夏",
            "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管",
            "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗", "丁", "宣",
            "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉",
            "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁", "荀", "羊",
            "於", "惠", "甄", "麹", "家", "封", "芮", "羿", "储", "靳", "汲", "邴",
            "糜", "松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓", "牧", "隗",
            "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲",
            "伊", "宫", "宁", "仇", "栾", "暴", "甘", "斜", "厉", "戎", "祖", "武",
            "符", "刘", "景", "詹", "束", "龙", "叶", "幸", "司", "韶", "郜", "黎",
            "蓟", "薄", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸",
            "籍", "赖", "卓", "蔺", "屠", "蒙", "池", "乔", "阴", "郁", "胥", "能",
            "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄", "姬", "申",
            "扶", "堵", "冉", "宰", "郦", "雍", "郤", "璩", "桑", "桂", "濮", "牛",
            "寿", "通", "边", "扈", "燕", "冀", "郏", "浦", "尚", "农", "温", "别",
            "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾",
            "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居",
            "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄",
            "阙", "东", "欧", "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩",
            "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简",
            "饶", "空", "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关",
            "蒯", "相", "查", "后", "荆", "红", "游", "竺", "权", "逑", "盖", "益",
            "桓", "公", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连",
            "皇甫", "尉迟", "公羊", "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔",
            "申屠", "公孙", "仲孙", "轩辕", "令狐", "锺离", "宇文", "长孙", "慕容", "鲜于",
            "闾丘", "司徒", "司空", "丌官", "司寇", "仉", "督", "子车", "颛孙", "端木", "巫马",
            "公西", "漆雕", "乐正", "壤驷", "公良", "拓拔", "夹谷", "宰父", "谷梁", "晋", "楚",
            "阎", "法", "汝", "鄢", "涂", "钦", "段干", "百里", "东郭", "南门", "呼延",
            "归", "海", "羊舌", "微生", "岳", "帅", "缑", "亢", "况", "后", "有", "琴",
            "梁丘", "左丘", "东门", "西门", "商", "牟", "佘", "佴", "伯", "赏", "南宫",
            "墨", "哈", "谯", "笪", "年", "爱", "阳", "佟"};
    public static String[] namelist = {"伟", "伟", "芳", "伟", "秀英", "秀英", "娜", "秀英", "伟",
            "敏", "静", "丽", "静", "丽", "强", "静", "敏", "敏", "磊", "军", "洋",
            "勇", "勇", "艳", "杰", "磊", "强", "军", "杰", "娟", "艳", "涛", "涛",
            "明", "艳", "超", "勇", "娟", "杰", "秀兰", "霞", "敏", "军", "丽", "强",
            "平", "刚", "杰", "桂英", "芳", "　嘉懿", "煜城", "懿轩", "烨伟", "苑博", "伟泽",
            "熠彤", "鸿煊", "博涛", "烨霖", "烨华", "煜祺", "智宸", "正豪", "昊然", "明杰",
            "立诚", "立轩", "立辉", "峻熙", "弘文", "熠彤", "鸿煊", "烨霖", "哲瀚", "鑫鹏",
            "致远", "俊驰", "雨泽", "烨磊", "晟睿", "天佑", "文昊", "修洁", "黎昕", "远航",
            "旭尧", "鸿涛", "伟祺", "荣轩", "越泽", "浩宇", "瑾瑜", "皓轩", "擎苍", "擎宇",
            "志泽", "睿渊", "楷瑞", "子轩", "弘文", "哲瀚", "雨泽", "鑫磊", "修杰", "伟诚",
            "建辉", "晋鹏", "天磊", "绍辉", "泽洋", "明轩", "健柏", "鹏煊", "昊强", "伟宸",
            "博超", "君浩", "子骞", "明辉", "鹏涛", "炎彬", "鹤轩", "越彬", "风华", "靖琪",
            "明诚", "高格", "光华", "国源", "冠宇", "晗昱", "涵润", "翰飞", "翰海", "昊乾",
            "浩博", "和安", "弘博", "宏恺", "鸿朗", "华奥", "华灿", "嘉慕", "坚秉", "建明",
            "金鑫", "锦程", "瑾瑜", "晋鹏", "经赋", "景同", "靖琪", "君昊", "俊明", "季同",
            "开济", "凯安", "康成", "乐语", "力勤", "良哲", "理群", "茂彦", "敏博", "明达",
            "朋义", "彭泽", "鹏举", "濮存", "溥心", "璞瑜", "浦泽", "奇邃", "祺祥", "荣轩",
            "锐达", "睿慈", "绍祺", "圣杰", "晟睿", "思源", "斯年", "泰宁", "天佑", "同巍",
            "奕伟", "祺温", "文虹", "向笛", "心远", "欣德", "新翰", "兴言", "星阑", "修为",
            "旭尧", "炫明", "学真", "雪风", "雅昶", "阳曦", "烨熠", "英韶", "永贞", "咏德",
            "宇寰", "雨泽", "玉韵", "越彬", "蕴和", "哲彦", "振海", "正志", "子晋", "自怡",
            "德赫", "君平"};
    private static String[] road = "重庆大厦,黑龙江路,十梅庵街,遵义路,湘潭街,瑞金广场,仙山街,仙山东路,仙山西大厦,白沙河路,赵红广场,机场路,民航街,长城南路,流亭立交桥,虹桥广场,长城大厦,礼阳路,风岗街,中川路,白塔广场,兴阳路,文阳街,绣城路,河城大厦,锦城广场,崇阳街,华城路,康城街,正阳路,和阳广场,中城路,江城大厦,顺城路,安城街,山城广场,春城街,国城路,泰城街,德阳路,明阳大厦,春阳路,艳阳街,秋阳路,硕阳街,青威高速,瑞阳街,丰海路,双元大厦,惜福镇街道,夏庄街道,古庙工业园,中山街,太平路,广西街,潍县广场,博山大厦,湖南路,济宁街,芝罘路,易州广场,荷泽四路,荷泽二街,荷泽一路,荷泽三大厦,观海二广场,广西支街,观海一路,济宁支街,莒县路,平度广场,明水路,蒙阴大厦,青岛路,湖北街,江宁广场,郯城街,天津路,保定街,安徽路,河北大厦,黄岛路,北京街,莘县路,济南街,宁阳广场,日照街,德县路,新泰大厦,荷泽路,山西广场,沂水路,肥城街,兰山路,四方街,平原广场,泗水大厦,浙江路,曲阜街,寿康路,河南广场,泰安路,大沽街,红山峡支路,西陵峡一大厦,台西纬一广场,台西纬四街,台西纬二路,西陵峡二街,西陵峡三路,台西纬三广场,台西纬五路,明月峡大厦,青铜峡路,台西二街,观音峡广场,瞿塘峡街,团岛二路,团岛一街,台西三路,台西一大厦,郓城南路,团岛三街,刘家峡路,西藏二街,西藏一广场,台西四街,三门峡路,城武支大厦,红山峡路,郓城北广场,龙羊峡路,西陵峡街,台西五路,团岛四街,石村广场,巫峡大厦,四川路,寿张街,嘉祥路,南村广场,范县路,西康街,云南路,巨野大厦,西江广场,鱼台街,单县路,定陶街,滕县路,钜野广场,观城路,汶上大厦,朝城路,滋阳街,邹县广场,濮县街,磁山路,汶水街,西藏路,城武大厦,团岛路,南阳街,广州路,东平街,枣庄广场,贵州街,费县路,南海大厦,登州路,文登广场,信号山支路,延安一街,信号山路,兴安支街,福山支广场,红岛支大厦,莱芜二路,吴县一街,金口三路,金口一广场,伏龙山路,鱼山支街,观象二路,吴县二大厦,莱芜一广场,金口二街,海阳路,龙口街,恒山路,鱼山广场,掖县路,福山大厦,红岛路,常州街,大学广场,龙华街,齐河路,莱阳街,黄县路,张店大厦,祚山路,苏州街,华山路,伏龙街,江苏广场,龙江街,王村路,琴屿大厦,齐东路,京山广场,龙山路,牟平街,延安三路,延吉街,南京广场,东海东大厦,银川西路,海口街,山东路,绍兴广场,芝泉路,东海中街,宁夏路,香港西大厦,隆德广场,扬州街,郧阳路,太平角一街,宁国二支路,太平角二广场,天台东一路,太平角三大厦,漳州路一路,漳州街二街,宁国一支广场,太平角六街,太平角四路,天台东二街,太平角五路,宁国三大厦,澳门三路,江西支街,澳门二路,宁国四街,大尧一广场,咸阳支街,洪泽湖路,吴兴二大厦,澄海三路,天台一广场,新湛二路,三明北街,新湛支路,湛山五街,泰州三广场,湛山四大厦,闽江三路,澳门四街,南海支路,吴兴三广场,三明南路,湛山二街,二轻新村镇,江南大厦,吴兴一广场,珠海二街,嘉峪关路,高邮湖街,湛山三路,澳门六广场,泰州二路,东海一大厦,天台二路,微山湖街,洞庭湖广场,珠海支街,福州南路,澄海二街,泰州四路,香港中大厦,澳门五路,新湛三街,澳门一路,正阳关街,宁武关广场,闽江四街,新湛一路,宁国一大厦,王家麦岛,澳门七广场,泰州一路,泰州六街,大尧二路,青大一街,闽江二广场,闽江一大厦,屏东支路,湛山一街,东海西路,徐家麦岛函谷关广场,大尧三路,晓望支街,秀湛二路,逍遥三大厦,澳门九广场,泰州五街,澄海一路,澳门八街,福州北路,珠海一广场,宁国二路,临淮关大厦,燕儿岛路,紫荆关街,武胜关广场,逍遥一街,秀湛四路,居庸关街,山海关路,鄱阳湖大厦,新湛路,漳州街,仙游路,花莲街,乐清广场,巢湖街,台南路,吴兴大厦,新田路,福清广场,澄海路,莆田街,海游路,镇江街,石岛广场,宜兴大厦,三明路,仰口街,沛县路,漳浦广场,大麦岛,台湾街,天台路,金湖大厦,高雄广场,海江街,岳阳路,善化街,荣成路,澳门广场,武昌路,闽江大厦,台北路,龙岩街,咸阳广场,宁德街,龙泉路,丽水街,海川路,彰化大厦,金田路,泰州街,太湖路,江西街,泰兴广场,青大街,金门路,南通大厦,旌德路,汇泉广场,宁国路,泉州街,如东路,奉化街,鹊山广场,莲岛大厦,华严路,嘉义街,古田路,南平广场,秀湛路,长汀街,湛山路,徐州大厦,丰县广场,汕头街,新竹路,黄海街,安庆路,基隆广场,韶关路,云霄大厦,新安路,仙居街,屏东广场,晓望街,海门路,珠海街,上杭路,永嘉大厦,漳平路,盐城街,新浦路,新昌街,高田广场,市场三街,金乡东路,市场二大厦,上海支路,李村支广场,惠民南路,市场纬街,长安南路,陵县支街,冠县支广场,小港一大厦,市场一路,小港二街,清平路,广东广场,新疆路,博平街,港通路,小港沿,福建广场,高唐街,茌平路,港青街,高密路,阳谷广场,平阴路,夏津大厦,邱县路,渤海街,恩县广场,旅顺街,堂邑路,李村街,即墨路,港华大厦,港环路,馆陶街,普集路,朝阳街,甘肃广场,港夏街,港联路,陵县大厦,上海路,宝山广场,武定路,长清街,长安路,惠民街,武城广场,聊城大厦,海泊路,沧口街,宁波路,胶州广场,莱州路,招远街,冠县路,六码头,金乡广场,禹城街,临清路,东阿街,吴淞路,大港沿,辽宁路,棣纬二大厦,大港纬一路,贮水山支街,无棣纬一广场,大港纬三街,大港纬五路,大港纬四街,大港纬二路,无棣二大厦,吉林支路,大港四街,普集支路,无棣三街,黄台支广场,大港三街,无棣一路,贮水山大厦,泰山支路,大港一广场,无棣四路,大连支街,大港二路,锦州支街,德平广场,高苑大厦,长山路,乐陵街,临邑路,嫩江广场,合江路,大连街,博兴路,蒲台大厦,黄台广场,城阳街,临淄路,安邱街,临朐路,青城广场,商河路,热河大厦,济阳路,承德街,淄川广场,辽北街,阳信路,益都街,松江路,流亭大厦,吉林路,恒台街,包头路,无棣街,铁山广场,锦州街,桓台路,兴安大厦,邹平路,胶东广场,章丘路,丹东街,华阳路,青海街,泰山广场,周村大厦,四平路,台东西七街,台东东二路,台东东七广场,台东西二路,东五街,云门二路,芙蓉山村,延安二广场,云门一街,台东四路,台东一街,台东二路,杭州支广场,内蒙古路,台东七大厦,台东六路,广饶支街,台东八广场,台东三街,四平支路,郭口东街,青海支路,沈阳支大厦,菜市二路,菜市一街,北仲三路,瑞云街,滨县广场,庆祥街,万寿路,大成大厦,芙蓉路,历城广场,大名路,昌平街,平定路,长兴街,浦口广场,诸城大厦,和兴路,德盛街,宁海路,威海广场,东山路,清和街,姜沟路,雒口大厦,松山广场,长春街,昆明路,顺兴街,利津路,阳明广场,人和路,郭口大厦,营口路,昌邑街,孟庄广场,丰盛街,埕口路,丹阳街,汉口路,洮南大厦,桑梓路,沾化街,山口路,沈阳街,南口广场,振兴街,通化路,福寺大厦,峄县路,寿光广场,曹县路,昌乐街,道口路,南九水街,台湛广场,东光大厦,驼峰路,太平山,标山路,云溪广场,太清路".split(",");

    *//**
     * 返回中文姓名
     *//*
    public static String ChineseName() {
        int a = (int) Math.abs(firsname.length * Math.random());
        int b = (int) Math.abs(namelist.length * Math.random());
        return firsname[a] + namelist[b];
    }

    *//**
     * 返回手机号码
     *//*
    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153,186,185,181,177".split(",");

    public static String getTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + thrid;
    }

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    *//**
     * 返回地址
     *
     * @return
     *//*
    public static String getRoad() {
        int index = getNum(0, road.length - 1);
        String first = road[index];
//		String second=String.valueOf(getNum(11,150))+"号";
//		String third="-"+getNum(1,20)+"-"+getNum(1,10);
        return first;
    }
    */


}

