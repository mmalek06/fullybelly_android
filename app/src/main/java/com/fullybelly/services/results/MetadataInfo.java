package com.fullybelly.services.results;

public final class MetadataInfo {

    //region fields

    public String scheme;

    public String apiRoot;

    public String schemeStatic;

    public String apiRootStatic;

    //endregion

    //region constructor

    public MetadataInfo(String scheme, String root, String schemeStatic, String rootStatic) {
        this.scheme = scheme;
        this.apiRoot = root;
        this.schemeStatic = schemeStatic;
        this.apiRootStatic = rootStatic;
    }

    //endregion

}
