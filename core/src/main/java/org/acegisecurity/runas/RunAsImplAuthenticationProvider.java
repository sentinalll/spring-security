/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acegisecurity.runas;

import org.acegisecurity.AcegiMessageSource;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.AuthenticationProvider;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;

import org.springframework.util.Assert;


/**
 * An {@link AuthenticationProvider} implementation that can authenticate a {@link RunAsUserToken}.<P>Configured in
 * the bean context with a key that should match the key used by adapters to generate the <code>RunAsUserToken</code>.
 * It treats as valid any <code>RunAsUserToken</code> instance presenting a hash code that matches the
 * <code>RunAsImplAuthenticationProvider</code>-configured key.</p>
 *  <P>If the key does not match, a <code>BadCredentialsException</code> is thrown.</p>
 */
public class RunAsImplAuthenticationProvider implements InitializingBean, AuthenticationProvider, MessageSourceAware, Ordered {
    //~ Instance fields ================================================================================================

    protected MessageSourceAccessor messages = AcegiMessageSource.getAccessor();
    private String key;
    private int order = -1; // default: same as non-Ordered

    //~ Methods ========================================================================================================

    public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void afterPropertiesSet() throws Exception {
        Assert.notNull(key, "A Key is required and should match that configured for the RunAsManagerImpl");
    }

    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        RunAsUserToken token = (RunAsUserToken) authentication;

        if (token.getKeyHash() == key.hashCode()) {
            return authentication;
        } else {
            throw new BadCredentialsException(messages.getMessage("RunAsImplAuthenticationProvider.incorrectKey",
                    "The presented RunAsUserToken does not contain the expected key"));
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public boolean supports(Class authentication) {
        if (RunAsUserToken.class.isAssignableFrom(authentication)) {
            return true;
        } else {
            return false;
        }
    }
}
