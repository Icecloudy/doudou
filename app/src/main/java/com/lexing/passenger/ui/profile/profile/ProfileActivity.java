package com.lexing.passenger.ui.profile.profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.lexing.passenger.views.MyBottomSheetDialog;
import com.lexing.passenger.views.RoundAngleImageView;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.rest.Response;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity implements View.OnClickListener, HttpListener {


    @BindView(R.id.profile_image)
    RoundAngleImageView profileImage;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editSex)
    TextView editSex;
    @BindView(R.id.editMobile)
    TextView editMobile;
    @BindView(R.id.tvIsAuthentication)
    TextView tvIsAuthentication;
    @BindView(R.id.tvAuthentication)
    TextView tvAuthentication;

    UserDataPreference userDataPreference;

    private File file;
    private String saveDir = Environment.getExternalStorageDirectory().getPath() + "/lexing_image";// 拍照图片保存路径
    File mars_file = new File(saveDir);
    private Bitmap photo;
    private String img_driver_url = "";

    private static final int UPDATA = 0x001;
    private static final int HEAD = 0x002;
    int sex = 0;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        setTitle(R.string.profile);
        setRightText(R.string.save, this);
        setRightTextColor(R.color.text_content);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        String userinfo = userDataPreference.getUserInfo();
        UserBean userBean = JSON.parseObject(userinfo, UserBean.class);
        initData(userBean);
    }

    //保存
    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(editName.getText().toString())) {
            ToastUtil.showToast(this, "请输入姓名");
        } else if (TextUtils.isEmpty(editSex.getText().toString())) {
            ToastUtil.showToast(this, "请选择性别");
        } else {
            updatahead(editName.getText().toString(), sex, img_driver_url);
        }
    }

    private void initData(UserBean userBean) {
        if (!TextUtils.isEmpty(userBean.getAccount())) {
            String phone = userBean.getAccount();
            phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
            editMobile.setText(phone);
        }
        editName.setText(userBean.getNickname());
        Glide.with(this)
                .load(userBean.getPhoto())
                .placeholder(R.drawable.ic_head)
                .error(R.drawable.ic_head)
                .centerCrop()
                .crossFade()
                .into(profileImage);
        sex = userBean.getSex();
        if (sex == 1) {
            editSex.setText("男");
        } else {
            editSex.setText("女");
        }
        if (!TextUtils.isEmpty(userBean.getIdcard())) {
            tvIsAuthentication.setText("已认证");
            tvAuthentication.setEnabled(false);
        }
    }

    @OnClick({R.id.layoutChangeHead, R.id.tvChangePwd, R.id.tvAuthentication, R.id.editSex})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.layoutChangeHead:
                ImgDialog();
                break;
            case R.id.tvChangePwd:
                intent = new Intent(this, ChangePasswordActivity.class);
                break;
            case R.id.tvAuthentication:
                intent = new Intent(this, AuthenticationActivity.class);
                break;
            case R.id.editSex:
                sexDialog();
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    /**
     * @param name
     * @param sex
     * @param path
     * @ account 账号
     * @ photo 头像
     * @ nickname 昵称
     * @ sex 性别[1男,0女]
     */
    private void updatahead(String name, int sex, String path) {

        BaseRequest baseRequest = new BaseRequest(ConfigUtil.UPDATA_USERINFO)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("nickname", name)
                .add("sex", sex);
        if (!TextUtils.isEmpty(path)) {
            FileBinary fileBinary1 = new FileBinary(new File(path));
            baseRequest.add("photo", fileBinary1);
        }
        request(HEAD, baseRequest, this, false, true);

    }

    @Override
    public void onSucceed(int what, String response) {
        showMsg("修改成功");
        userDataPreference.setUserInfo(response);
        Intent intent = new Intent();
        intent.setAction(ConfigUtil.FILTER_CODE);
        intent.putExtra(ConfigUtil.EXTRA_CODE, ConfigUtil.UPDATE_USER);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (code==201){
            showMsg("信息未修改");
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void sexDialog() {
        new MyBottomSheetDialog(this).build().setFirstItem("男", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sex = 1;
                editSex.setText("男");
            }
        }).setSecondItem("女", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sex = 0;
                editSex.setText("女");
            }
        }).show();
    }

    /**
     * 选择照片窗口
     */
    private void ImgDialog() {
        new MyBottomSheetDialog(this).build().setFirstItem("拍照", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionResult = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA);
                    if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    }
                } else {
                    openCamera();
                }
            }
        }).setSecondItem("从手机相册选择", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionResult = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                        selectPhoto();
                    } else if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                    }
                } else {
                    selectPhoto();
                }
            }
        }).show();

    }

    private static final int PERMISSION_REQUEST_STORAGE = 200;
    private static final int PERMISSION_REQUEST_CAMERA = 201;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                int permissionResult = grantResults[0];
                if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                    //扫描照片
                    selectPhoto();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.album_dialog_permission_failed)
                            .setMessage(R.string.album_permission_storage_failed_hint)
                            .setPositiveButton(R.string.album_dialog_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            }
            case PERMISSION_REQUEST_CAMERA: {
                int permissionResult = grantResults[0];
                if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.album_dialog_permission_failed)
                            .setMessage(R.string.album_permission_camera_failed_hint)
                            .setPositiveButton(R.string.album_dialog_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 拍照
     */
    public void openCamera() {

        destoryImage();
        if (checkSDcard()) {
            // 先创建父目录，如果新创建一个文件的时候，父目录没有存在，那么必须先创建父目录，再新建文件。
            if (!mars_file.exists()) {
                mars_file.mkdirs();
            }
            file = new File(saveDir, ".jpg");
            file.delete();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "照片创建失败!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
        }

    }

    // 选择照片
    public void selectPhoto() {
        if (checkSDcard()) {
            // 先创建父目录，如果新创建一个文件的时候，父目录没有存在，那么必须先创建父目录，再新建文件。
            if (!mars_file.exists()) {
                mars_file.mkdirs();
            }
            file = new File(saveDir, ".jpg");
            file.delete();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "照片创建失败!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        innerIntent.setType("image/*"); // 查看类型
        // StringIMAGE_UNSPECIFIED="image/*";详细的类型在com.google.android.mms.ContentType中
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        startActivityForResult(wrapperIntent, SELECT_PHOTO_REQUEST_CODE);
    }

    private void destoryImage() {
        if (photo != null) {
            photo.recycle();
            photo = null;
        }
    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public final boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    public static final int TAKE_PHOTO_REQUEST_CODE = 1;
    public static final int SELECT_PHOTO_REQUEST_CODE = 2;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == TAKE_PHOTO_REQUEST_CODE || requestCode == SELECT_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;
                if (requestCode == SELECT_PHOTO_REQUEST_CODE) {
                    uri = intent.getData();
                } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
                    if (file != null && file.exists()) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        try {
                            photo = BitmapFactory.decodeFile(file.getPath(), option);
                            File myCaptureFile = new File(saveDir + "/" + file.getName());
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                            photo.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                            bos.flush();
                            bos.close();
                            // Log.d(TAG, saveDir + "/"+file.getName());
                            uri = Uri.fromFile(new File(saveDir + "/" + file.getName()));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
                if (uri != null) {

                    Bitmap bm = getBitmapFromUri(uri);
                    // 转换成图片，保存到sdcard中
                    bm = ratio(bm, 480, 480);
                    String npicName = "" + System.currentTimeMillis();
                    saveMyBitmap(bm, npicName);

                }

            }
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {

            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compress image by size, this will modify image width/height. Used to get thumbnail
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        // 压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    // Bitmap 转换成jpeg并保存
    public void saveMyBitmap(Bitmap mBitmap, String bitName) {
        File f = new File(saveDir + "/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);// 30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
            img_driver_url = saveDir + "/" + bitName + ".jpg";
            profileImage.setImageBitmap(createImageThumbnail(img_driver_url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
