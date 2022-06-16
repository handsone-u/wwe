package com.whatweeat.wwe.entity;

import com.whatweeat.wwe.entity.enums.NationName;
import org.junit.jupiter.api.Test;

import static com.whatweeat.wwe.entity.enums.NationName.*;
import static org.assertj.core.api.Assertions.assertThat;

class EnumAndValueTest {

    @Test
    void enumValueNationName() {
        NationName kor = KOREAN;

        assertThat(lookup("한식")).isEqualTo(kor);
        assertThat(lookup("한식") == kor).isTrue();
        assertThat(lookup("한식")).isEqualTo(kor);

        assertThat(CHINESE.getDesc()).isEqualTo("중식");
        assertThat(lookup("no")).isNull();
    }
}