package com.fioletowi.farma.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TeamRepositoryTest {

    @Autowired TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        // seedujemy kilka zespołów
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Team t = new Team();
            t.setName("Team " + i);
            teamRepository.save(t);
        });
    }

    @Test @DisplayName("findAll(Pageable) zwraca paginację")
    void testFindAllPageable() {
        var page = teamRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 3));
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(3);
    }

    @Test @DisplayName("existsById i deleteById")
    void testExistsAndDelete() {
        Team t = teamRepository.findAll(org.springframework.data.domain.PageRequest.of(0,1))
                .getContent().get(0);
        Long id = t.getId();
        assertThat(teamRepository.existsById(id)).isTrue();
        teamRepository.deleteById(id);
        assertThat(teamRepository.existsById(id)).isFalse();
    }
}