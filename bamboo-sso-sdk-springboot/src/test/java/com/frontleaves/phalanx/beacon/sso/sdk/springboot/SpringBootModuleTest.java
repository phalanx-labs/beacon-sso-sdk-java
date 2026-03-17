package com.frontleaves.phalanx.beacon.sso.sdk.springboot;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.OAuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthStateRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.UserinfoRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.config.BeaconSsoSpringBootAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringBoot 模块自动配置测试
 *
 * @author Xiao Lfeng &lt;xiaolfeng@x-lf.com&gt;
 */
class SpringBootModuleTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    BeaconSsoSpringBootAutoConfiguration.class
            ));

    @Test
    void shouldRegisterCoreBeans() {
        contextRunner
                .withPropertyValues(
                        "beacon.sso.enabled=true",
                        "beacon.sso.base-url=https://sso.example.com",
                        "beacon.sso.client-id=test-client-id",
                        "beacon.sso.redirect-uri=http://localhost:8080/callback"
                )
                .run(context -> {
                    assertThat(context).hasBean("ssoClient");
                    assertThat(context).hasBean("oAuthLogic");
                    assertThat(context).hasBean("businessLogic");
                    assertThat(context).hasBean("oauthStateRepository");
                    assertThat(context).hasBean("oauthTokenRepository");
                    assertThat(context).hasBean("userinfoRepository");
                });
    }
}
