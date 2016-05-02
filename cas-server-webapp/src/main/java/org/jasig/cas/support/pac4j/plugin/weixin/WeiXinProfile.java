package org.jasig.cas.support.pac4j.plugin.weixin;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * 用于添加返回用户信息
 * Created by Administrator on 2016/4/28 0028.
 */
public class WeiXinProfile extends OAuth20Profile {

    private static final long serialVersionUID = -7969484323692570444L;

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return new WeiXinAttributesDefinition();
    }

}
