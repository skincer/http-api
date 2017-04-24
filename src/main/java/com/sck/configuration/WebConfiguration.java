package com.sck.configuration;

import com.sck.utility.messageconversion.HibernateAwareObjectMapper;
import com.sck.utility.messageconversion.HibernateAwareXmlObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        jsonConverter.setObjectMapper(new HibernateAwareObjectMapper());
        xmlConverter.setObjectMapper(new HibernateAwareXmlObjectMapper());
        converters.add(jsonConverter);
        converters.add(xmlConverter);
    }
}
