package com.fullybelly.viewModels.validators;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fullybelly.BR;
import com.fullybelly.R;
import com.fullybelly.services.local.interfaces.TextService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailsValidatorImpl extends BaseObservable implements UserDetailsValidator {

    //region public methods

    @Override
    public boolean validate(String emailAddress) {
        boolean emailValid = validateEmailAddress(emailAddress);

        return emailValid;
    }

    //endregion

    //region private methods

    private boolean validateEmailAddress(String email) {
        if (email == null) {
            return false;
        }

        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);

        return matcher.matches();
    }

    //endregion

}