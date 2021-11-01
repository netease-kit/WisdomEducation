package com.netease.edu.sample;

import com.google.gson.Gson;
import com.netease.edu.sample.configuration.AppHttpConfig;
import com.netease.edu.sample.parameter.PutRoomParam;
import com.netease.edu.sample.parameter.UserCreateParam;
import com.netease.edu.sample.service.EduRoomService;
import com.netease.edu.sample.service.EduUserService;
import com.netease.util.Conf;
import com.netease.util.gson.GsonBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@SpringBootApplication(scanBasePackageClasses = {SampleMain.class}, scanBasePackages = {"com.netease.edu.sample.service"}, exclude = {RedisAutoConfiguration.class})
@Import({AppHttpConfig.class})
public class SampleMain {

    private static Logger logger = LoggerFactory.getLogger(SampleMain.class);

    @Bean
    @Primary
    public Gson gson() {
        return GsonBox.OPEN.gson();
    }

    @Bean
    Conf.Runner confRunner(){
        return new Conf.Runner();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder(SampleMain.class)
                .bannerMode(Banner.Mode.OFF).build();
        ApplicationContext applicationContext = app.run(args);
    }
}
