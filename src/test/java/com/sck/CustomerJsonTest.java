package com.sck;

import com.sck.domain.CustomerEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@RunWith(SpringRunner.class)
@JsonTest
public class CustomerJsonTest {

    @Autowired
    private JacksonTester<CustomerEntity> customerJson;

    @Test
    public void testCustomerSerialize() throws Exception {
        CustomerEntity customerEntity = new CustomerEntity("Steve Kincer");
        assertThat(this.customerJson.write(customerEntity)).hasJsonPathStringValue("@.name");
        assertThat(this.customerJson.write(customerEntity)).extractingJsonPathStringValue("@.name").isEqualTo("Steve Kincer");
    }

    @Test
    public void testCustomerDeserialize() throws Exception {
        String content = "{\"id\":999,\"name\":\"Kincer\"}";
        CustomerEntity testCustomer = new CustomerEntity("Kincer");
        testCustomer.setId(999L);
        assertThat(this.customerJson.parse(content)).isEqualTo(testCustomer);
        assertThat(this.customerJson.parseObject(content).getName()).isEqualTo("Kincer");
    }
}
