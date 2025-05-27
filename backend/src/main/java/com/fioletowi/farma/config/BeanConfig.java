package com.fioletowi.farma.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Configuration class for defining common Spring beans.
 */
@Configuration
public class BeanConfig {

    /**
     * Provides the {@link AuthenticationManager} bean.
     *
     * @param config the {@link AuthenticationConfiguration} used to get the authentication manager
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if an error occurs while retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates and configures a {@link ModelMapper} bean.
     * The mapping strategy is set to LOOSE to allow flexible property matching.
     *
     * @return a configured {@link ModelMapper} instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }

}
