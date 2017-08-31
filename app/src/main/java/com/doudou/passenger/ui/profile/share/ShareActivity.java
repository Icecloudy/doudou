package com.doudou.passenger.ui.profile.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yolanda.nohttp.rest.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareActivity extends BaseActivity implements HttpListener{


    @BindView(R.id.weChat)
    TextView weChat;
    @BindView(R.id.QQ)
    TextView QQ;
    @BindView(R.id.pYQ)
    TextView pYQ;

    private IWXAPI api;

    UserDataPreference userDataPreference;
    ShareData  shareData;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        setTitle("推荐有奖");
        api = WXAPIFactory.createWXAPI(this, ConfigUtil.WECHAT_PAY_APPID);
        userDataPreference = new UserDataPreference(this);
        getShareData(1);
    }


    @OnClick({R.id.weChat, R.id.QQ, R.id.pYQ})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.weChat:
                shareWx(R.id.weChat);
                break;
            case R.id.QQ:
                showMsg("暂不支持");
                break;
            case R.id.pYQ:
                shareWx(R.id.pYQ);
                break;
        }
    }

    //
    private void shareWx(int id) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareData.getUrls();
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "豆豆打车";
        msg.description = shareData.getTxt();
        new Thread(new Runnable() {
            @Override
            public void run() {
                msg.thumbData = bmpToByteArray(returnBitmap(shareData.getLogo()), true);
            }
        }).start();


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (id == R.id.weChat) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        api.sendReq(req);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 20,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    private Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }


    private void getShareData(int usertype) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_SHARE_DATA)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("type", 1)
                .add("usertype", usertype);
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            shareData = new ShareData();
            shareData = JSON.parseObject(response,ShareData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
}
