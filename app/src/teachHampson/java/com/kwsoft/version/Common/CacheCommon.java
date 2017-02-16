package com.kwsoft.version.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/11/23 0023.
 */

public class CacheCommon {
    public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(context.getString(R.string.confirm), onClickListener);
        builder.setNegativeButton(context.getString(R.string.confirm), null);
        return builder;
    }
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }
}
