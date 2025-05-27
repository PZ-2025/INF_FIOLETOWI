package com.fioletowi.farma.auth;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing {@link Token} entities.
 * Provides CRUD operations and a method to find a token by its string value.
 */
public interface TokenRepository extends CrudRepository<Token, Long> {

    /**
     * Finds a {@link Token} entity by its token string.
     *
     * @param token the token string to search for
     * @return the {@link Token} entity matching the token string, or null if not found
     */
    Token findByToken(String token);

}
