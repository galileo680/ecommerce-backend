package com.galileo.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularApplicationTests {
    @Test
    void verifiesModuleStructure() {
        ApplicationModules.of(EcommerceApplication.class).verify();
    }
}
