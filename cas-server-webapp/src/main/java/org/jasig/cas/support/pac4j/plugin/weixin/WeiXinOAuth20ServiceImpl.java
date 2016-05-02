package org.jasig.cas.support.pac4j.plugin.weixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

/**
 * 用于添加获取ACCESS_TOKEN与用户信息添加参数并请求微信
 * Created by Administrator on 2016/4/28 0028.
 */
public class WeiXinOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {

    private static Pattern openIdPattern = Pattern.compile("\"openid\":\\s*\"(\\S*?)\"");

    public WeiXinOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    /**
     * 获取account_token的http请求参数添加
     */
    @Override
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(),
                this.api.getAccessTokenEndpoint(), this.connectTimeout,
                this.readTimeout, this.proxyHost, this.proxyPort);
        request.addBodyParameter("appid", this.config.getApiKey());
        request.addBodyParameter("secret", this.config.getApiSecret());
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
        request.addBodyParameter("grant_type", "authorization_code");
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }

    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
        String response = accessToken.getRawResponse();
        Matcher matcher = openIdPattern.matcher(response);
        if(matcher.find()){
            request.addQuerystringParameter("openid", matcher.group(1));
        }
        else{
            throw new OAuthException("微信接口返回数据miss openid: " + response);
        }
    }

}
