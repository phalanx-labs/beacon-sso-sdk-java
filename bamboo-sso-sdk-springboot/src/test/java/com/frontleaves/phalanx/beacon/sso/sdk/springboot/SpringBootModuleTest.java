package com.frontleaves.phalanx.beacon.sso.sdk.springboot;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.UserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.config.BeaconSsoSpringBootAutoConfiguration;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthStateRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthTokenRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.UserinfoRepository;
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
                    // Base 模块 Bean
                    assertThat(context).hasBean("ssoClient");
                    assertThat(context).hasSingleBean(SsoOAuthApi.class);
                    assertThat(context).hasSingleBean(SsoUserApi.class);
                    assertThat(context).hasSingleBean(UserinfoClient.class);

                    // SpringBoot 模块 Bean
                    assertThat(context).hasBean("authLogic");
                    assertThat(context).hasBean("userLogic");
                    assertThat(context).hasBean("oauthStateRepository");
                    assertThat(context).hasBean("oauthTokenRepository");
                    assertThat(context).hasBean("userinfoRepository");
                });
    }
}
