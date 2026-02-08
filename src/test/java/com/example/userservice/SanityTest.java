package com.example.userservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SanityTest {

    @Test
    void простойТест_чтобыПроверитьЧтоJUnitРаботает() {
        // Этот тест нужен только для того, чтобы убедиться,
        // что JUnit 5 запускается и Maven видит тесты.
        assertEquals(2, 1 + 1);
    }
}
