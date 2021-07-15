/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util;

import android.app.AlertDialog;

public class AlertDialogUtil {
    private static AlertDialog alertDialog;

    public static void setAlertDialog(AlertDialog alertDialog) {
        AlertDialogUtil.alertDialog = alertDialog;
    }

    public static AlertDialog getAlertDialog() {
        return alertDialog;
    }
}
