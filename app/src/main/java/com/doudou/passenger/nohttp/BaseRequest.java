package com.doudou.passenger.nohttp;

import com.doudou.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.StringRequest;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * Created by Franco on 2017-4-19 14:56:52.
 */

public class BaseRequest extends StringRequest {


    public BaseRequest(String url) {
        super(ConfigUtil.SERVER_URL + url, RequestMethod.POST);
    }

    public BaseRequest(String url, RequestMethod method) {
        super(url, method);
    }

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, int value) {

        super.add(key, value);
        return this;
    }


    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, long value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, boolean value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@code char} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, char value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, double value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, float value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link Short} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, short value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link Byte} param.
     *
     * @param key   param name.
     * @param value param value, for example, the result is {@code 1} of {@code 0x01}.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, byte value) {
        super.add(key, value);
        return this;
    }

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, String value) {
        super.add(key, value);
        return this;
    }


    /**
     * Add {@link Binary} param.
     *
     * @param key    param name.
     * @param binary param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, Binary binary) {
        super.add(key, binary);
        return this;
    }


    /**
     * Add {@link File} param.
     *
     * @param key  param name.
     * @param file param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, File file) {
        super.add(key, file);
        return this;
    }

    /**
     * Add {@link Binary} param;
     *
     * @param key      param name.
     * @param binaries param value.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(String key, List<Binary> binaries) {
        super.add(key, binaries);
        return this;
    }

    /**
     * Add all param.
     *
     * @param params params {@link Map}.
     * @return {@link public BaseRequest}.
     */
    public BaseRequest add(Map<String, String> params) {
        super.add(params);
        return this;
    }

}
