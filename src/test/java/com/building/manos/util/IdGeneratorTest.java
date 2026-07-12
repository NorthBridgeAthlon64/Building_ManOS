package com.building.manos.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link IdGenerator} 的主键格式与唯一性测试。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-12
 */
class IdGeneratorTest {

    @Test
    void shouldGenerateExpectedPrefixes() {
        assertTrue(IdGenerator.nextBuildingId().startsWith("B"));
        assertTrue(IdGenerator.nextHouseId().startsWith("H"));
        assertTrue(IdGenerator.nextSaleId().startsWith("S"));
    }

    @Test
    void shouldNotDuplicateIdsInBurst() {
        Set<String> ids = new HashSet<>();
        for (int index = 0; index < 300; index++) {
            ids.add(IdGenerator.nextHouseId());
        }
        assertEquals(300, ids.size());
    }
}
