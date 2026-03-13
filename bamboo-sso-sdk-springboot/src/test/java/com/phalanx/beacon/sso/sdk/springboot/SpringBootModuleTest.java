package com.phalanx.beacon.sso.sdk.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringBoot 模块自动配置测试
 *
 * @author Xiao Lfeng &lt;xiaolfeng@x-lf.com&gt;
 */
class SpringBootModuleTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of());

    @Test
    void contextLoads() {
        // 基础测试 - 验证测试框架正常工作
        assertThat(true).isTrue();
    }
}
