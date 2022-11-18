package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.UserDetailsService;

public final class UserDetailsServiceMock implements UserDetailsService {
    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public void setEmailAddress(String emailAddress) {

    }
}
