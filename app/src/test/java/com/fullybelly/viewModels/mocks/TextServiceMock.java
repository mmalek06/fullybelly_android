package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.TextService;

public final class TextServiceMock implements TextService {
    @Override
    public String get(int id) { return "sample text"; }
}
