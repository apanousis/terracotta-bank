package com.joshcummings.codeplay.terracotta.security;

import org.zaproxy.clientapi.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ZapProxyScanner {

    private final ClientApi clientApi;
    private final boolean activeScanning;

    public ZapProxyScanner(String host, int port, boolean activeScanning) throws IllegalArgumentException, ClientApiException {
        this.clientApi = new ClientApi(host, port);
        this.activeScanning = activeScanning;

        if (!activeScanning) {
            clientApi.pscan.setEnabled("true");
        }
    }

    public void setScannerAttackStrength(String scannerId, String strength) throws ProxyException {
        try {
            clientApi.ascan.setScannerAttackStrength(scannerId, strength, null);
        } catch (ClientApiException e) {
            throw new ProxyException("Error occurred for setScannerAttackStrength for scannerId: " + scannerId + " and strength: " + strength, e);
        }
    }

    public void setScannerAlertThreshold(String scannerId, String threshold) throws ProxyException {
        try {
            clientApi.ascan.setScannerAlertThreshold(scannerId, threshold, null);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void setEnableScanners(String ids, boolean enable) throws ProxyException {
        if (!activeScanning) {
            return;
        }
        try {
            if (enable) {
                clientApi.ascan.enableScanners(ids, null);
            } else {
                clientApi.ascan.disableScanners(ids, null);
            }
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    List<Alert> getAlerts() throws ProxyException {
        return getAlerts(-1, -1);
    }

    List<Alert> getAlerts(int start, int count) throws ProxyException {
        try {
            return clientApi.getAlerts(null, start, count);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void scan(String url) throws ProxyException {
        try {
            clientApi.ascan.scan(url, "true", "false", null, null, null);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    int getScanProgress(int id) throws ProxyException {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.ascan.scans();
            return new ScanResponse(response).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void clear() throws ProxyException {
        try {
            clientApi.ascan.removeAllScans();
            clientApi.core.newSession("", "");
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void spider(String url) {
        try {
            clientApi.spider
                    .scan(url, null, null, null, null);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    public void spider(String url, boolean recurse, String contextName) {
        //Something must be specified else zap throws an exception
        String contextNameString = contextName == null ? "Default Context" : contextName;

        try {
            clientApi.spider
                    .scan(url, null, String.valueOf(recurse), contextNameString, null);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void excludeFromSpider(String regex) {
        try {
            clientApi.spider.excludeFromScan(regex);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void setMaxDepth(int depth) {
        try {
            clientApi.spider.setOptionMaxDepth(depth);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    public void setPostForms(boolean post) {
        try {
            clientApi.spider.setOptionPostForm(post);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    public void setThreadCount(int threads) {
        try {
            clientApi.spider.setOptionThreadCount(threads);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    int getLastSpiderScanId() {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.spider.scans();
            return new ScanResponse(response).getLastScan().getId();
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    int getLastScannerScanId() {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.ascan.scans();
            return new ScanResponse(response).getLastScan().getId();
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    int getSpiderProgress(int id) {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.spider.scans();
            return new ScanResponse(response).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }


    List<String> getSpiderResults(int id) {
        List<String> results = new ArrayList<>();
        try {
            ApiResponseList responseList = (ApiResponseList) clientApi.spider
                    .results(Integer.toString(id));
            for (ApiResponse response : responseList.getItems()) {
                results.add(((ApiResponseElement) response).getValue());
            }
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }

        return results;
    }

    public void createContext(String contextName, boolean inScope) throws ProxyException {
        try {
            clientApi.context.newContext(contextName);
            clientApi.context.setContextInScope(contextName, String.valueOf(inScope));
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void includeUrlTreeInContext(String contextName, String parentUrl)
            throws ProxyException {
        Pattern pattern = Pattern.compile(parentUrl);
        try {
            clientApi.context
                    .includeInContext(contextName, Pattern.quote(pattern.pattern()) + ".*");
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }

    public void setIncludeInContext(String contextName, String regex) {
        try {
            clientApi.context.includeInContext(contextName, regex);
        } catch (ClientApiException e) {
            if ("does_not_exist".equalsIgnoreCase(e.getCode())) {
                createContext(contextName);
                setIncludeInContext(contextName, regex);
            } else {
                throw new ProxyException(e);
            }
        }
    }

    public void createContext(String contextName) {
        try {
            clientApi.context.newContext(contextName);
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }
    }
}
