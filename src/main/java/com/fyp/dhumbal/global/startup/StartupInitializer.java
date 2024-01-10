package com.fyp.dhumbal.global.startup;

import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.user.dal.UserType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${dhumbal.admin.default.create}")
    private boolean defaultAdminCreate;

    @Value("${dhumbal.admin.default.email}")
    private String defaultAdminEmail;

    @Value("${dhumbal.admin.default.password}")
    private String defaultAdminPassword;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (defaultAdminCreate && userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setEmail(defaultAdminEmail);
            user.setPassword(passwordEncoder.encode(defaultAdminPassword));
            user.setVerified(true);
            user.setUserType(UserType.ADMIN);
            user.setName("Default Admin");
            userRepository.save(user);
            log.info("Default admin created with name '{}' and email '{}'", user.getName(), user.getEmail());
        }
    }
}
