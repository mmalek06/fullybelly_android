package com.fullybelly.services.base;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.fullybelly.services.results.ServiceResultCodes;

public abstract class BaseIntentService extends IntentService {

    //region fields

    protected boolean mPermissionsGranted;

    //endregion

    //region constructor

    public BaseIntentService(String name) {
        super(name);
    }

    //endregion

    //region methods


    @Override
    protected void onHandleIntent(Intent intent) {
        ServiceResultCodes baseValidationResult = validateBase(intent);
        ServiceResultCodes customValidationResult = validateCustom(intent);
        boolean isBaseValidationOk = baseValidationResult == ServiceResultCodes.SERVICE_OK;
        boolean isCustomValidationOk = customValidationResult == ServiceResultCodes.SERVICE_OK;

        if (!isBaseValidationOk || !isCustomValidationOk) {
            sendErrorMessage(isBaseValidationOk ? customValidationResult : baseValidationResult);
        } else {
            run(intent);
        }
    }

    protected ServiceResultCodes validateBase(Intent intent) throws IllegalArgumentException {
        if (intent == null) {
            return ServiceResultCodes.WRONG_ARGUMENT;
        }
        if (!mPermissionsGranted) {
            return ServiceResultCodes.NO_PERMISSIONS;
        }

        return ServiceResultCodes.SERVICE_OK;
    }

    protected ServiceResultCodes validateCustom(Intent intent) {
        return ServiceResultCodes.SERVICE_OK;
    }

    protected abstract void run(Intent intent);

    protected void sendErrorMessage(ServiceResultCodes code) { }

    //endregion

}
