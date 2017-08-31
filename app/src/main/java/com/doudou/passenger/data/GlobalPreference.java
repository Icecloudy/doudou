package com.doudou.passenger.data;

import android.content.Context;

/**
 * Created by jimmy on 2016/11/14.
 */

public class GlobalPreference extends BaseDataPreference {
    public GlobalPreference(Context context) {
        super(context, "global_data");
    }

    public int getCurrentUid(){
        int fakeId = getDataInt("current_uid", 0);
        if (fakeId > 0){
            return decodeUserId(fakeId);
        } else {
            return -1;
        }
    }

    public void setCurrentUid(int uid){
        setDataInt("current_uid", encodeUserId(uid));
    }

    private int encodeUserId(int oriUid){
        return oriUid*7+100;
    }

    private int decodeUserId(int encodedUid){
        return (encodedUid-100)/7;
    }

}
