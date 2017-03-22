package com.sck.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by TEKKINCERS on 3/22/2017.
 */
@Configuration
public class MapperConfiguration {

    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }


}
