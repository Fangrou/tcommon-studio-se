package org.talend.librariesmanager.nexus.nexus3.handler;

// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.core.nexus.ArtifactRepositoryBean;
import org.talend.core.nexus.HttpClientTransport;
import org.talend.core.runtime.maven.MavenArtifact;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class AbsNexus3SearchHandler implements INexus3SearchHandler {

    protected ArtifactRepositoryBean serverBean;

    public AbsNexus3SearchHandler(ArtifactRepositoryBean serverBean) {
        this.serverBean = serverBean;
    }

    protected String getServerUrl() {
        String serverUrl = serverBean.getServer();
        if (!serverUrl.endsWith("/")) { //$NON-NLS-1$
            serverUrl = serverUrl + "/"; //$NON-NLS-1$
        }
        return serverUrl;
    }

    protected abstract String getSearchUrl();

    public List<MavenArtifact> search(String repositoryId, String groupIdToSearch, String artifactId, String versionToSearch)
            throws Exception {
        List<MavenArtifact> resultList = new ArrayList<MavenArtifact>();
        String searchUrl = getSearchUrl();
        String continuationToken = null;
        while (true) {
            String query = getQueryParameter(repositoryId, groupIdToSearch, artifactId, versionToSearch, continuationToken);
            String content = doRequest(searchUrl + query);
            continuationToken = parseResult(content, resultList);
            if (continuationToken == null) {
                break;
            }
        }
        return resultList;
    }

    protected String doRequest(final String url) throws URISyntaxException, Exception {
        final StringBuffer sb = new StringBuffer();
        new HttpClientTransport(url, serverBean.getUserName(), serverBean.getPassword()) {

            @Override
            protected HttpResponse execute(IProgressMonitor monitor, DefaultHttpClient httpClient, URI targetURI)
                    throws Exception {
                HttpGet httpGet = new HttpGet(targetURI);
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    sb.append(EntityUtils.toString(response.getEntity()));
                } else {
                    throw new Exception(response.toString());
                }
                return response;
            }

        }.doRequest(null, new URI(url));
        return sb.toString();
    }

    protected String parseResult(String content, List<MavenArtifact> resultList) throws Exception {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        JSONObject responseObject = JSONObject.fromObject(content);
        String resultStr = responseObject.getString("items"); //$NON-NLS-1$
        String continuationToken = responseObject.getString("continuationToken");
        if (StringUtils.isEmpty(continuationToken) || "null".equalsIgnoreCase(continuationToken)) {
            continuationToken = null;
        }
        JSONArray resultArray = null;
        try {
            resultArray = JSONArray.fromObject(resultStr);
        } catch (Exception e) {
            throw new Exception(resultStr);
        }
        if (resultArray != null) {
            for (int i = 0; i < resultArray.size(); i++) {
                JSONObject jsonObject = resultArray.getJSONObject(i);
                MavenArtifact artifact = new MavenArtifact();
                artifact.setGroupId(jsonObject.getString("group")); //$NON-NLS-1$
                artifact.setArtifactId(jsonObject.getString("name")); //$NON-NLS-1$
                artifact.setVersion(jsonObject.getString("version")); //$NON-NLS-1$
                JSONArray assertsArray = jsonObject.getJSONArray("assets"); //$NON-NLS-1$
                artifact.setType(getPackageType(assertsArray));
                resultList.add(artifact);
            }
        }

        return continuationToken;
    }

    protected String getPackageType(JSONArray assertsArray) {
        String type = null;
        if (assertsArray != null) {
            for (int i = 0; i < assertsArray.size(); i++) {
                JSONObject jsonObject = assertsArray.getJSONObject(i);
                String path = jsonObject.getString("path"); //$NON-NLS-1$
                if (path != null && path.endsWith(".zip")) { //$NON-NLS-1$
                    return "zip"; //$NON-NLS-1$
                }
                if (path != null && path.endsWith(".jar")) { //$NON-NLS-1$
                    return "jar"; //$NON-NLS-1$
                }
                if (path != null && path.endsWith(".pom")) { //$NON-NLS-1$
                    type = "pom"; //$NON-NLS-1$
                }
            }
        }
        return type;
    }

    protected String getQueryParameter(String repositoryId, String groupIdToSearch, String artifactId, String versionToSearch,
            String continuationToken) {
        StringBuffer sb = new StringBuffer();
        boolean hasParameter = false;
        if (StringUtils.isNoneEmpty(repositoryId)) {
            sb.append("repository=").append(repositoryId); //$NON-NLS-1$
            hasParameter = true;
        }
        if (StringUtils.isNoneEmpty(groupIdToSearch)) {
            if (hasParameter) {
                sb.append("&"); //$NON-NLS-1$
            }
            sb.append("group=").append(groupIdToSearch); //$NON-NLS-1$
            hasParameter = true;
        }
        if (StringUtils.isNoneEmpty(artifactId)) {
            if (hasParameter) {
                sb.append("&"); //$NON-NLS-1$
            }
            sb.append("name=").append(artifactId); //$NON-NLS-1$
            hasParameter = true;
        }
        if (StringUtils.isNoneEmpty(versionToSearch)) {
            if (hasParameter) {
                sb.append("&"); //$NON-NLS-1$
            }
            sb.append("version=").append(versionToSearch); //$NON-NLS-1$
            hasParameter = true;
        }
        if (StringUtils.isNoneEmpty(continuationToken)) {
            if (hasParameter) {
                sb.append("&"); //$NON-NLS-1$
            }
            sb.append("continuationToken=").append(continuationToken); //$NON-NLS-1$
            hasParameter = true;
        }
        return sb.toString();
    }

    protected String getAuthenticationItem() {
        String userPass = serverBean.getUserName() + ":" + serverBean.getPassword(); //$NON-NLS-1$
        String basicAuth = "Basic " + new String(new Base64().encode(userPass.getBytes())); //$NON-NLS-1$
        return basicAuth;
    }
}
