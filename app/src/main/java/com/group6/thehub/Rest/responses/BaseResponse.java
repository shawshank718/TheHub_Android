package com.group6.thehub.Rest.responses;

import com.group6.thehub.Rest.models.BaseDetails;

/**
 * Created by sathwiksingari on 9/17/15.
 */
public class BaseResponse {

    public BaseDetails meta;

    public BaseDetails getMeta() {
        return meta;
    }

    public void setMeta(BaseDetails meta) {
        this.meta = meta;
    }

}
