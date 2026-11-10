package com.upsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {

    @Test
    void appClassLoads() {
        assertNotNull(App.class);
    }
}
