package com.friends.config

import jakarta.persistence.EntityManager
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class IndexCreator(
    private val entityManager: EntityManager,
) {
    @EventListener
    @Transactional
    fun handleContextRefreshedEvent(event: ContextRefreshedEvent) {
        // CREATE EXTENSION btree_gin;
        entityManager.createNativeQuery("CREATE INDEX IF NOT EXISTS member_nickname_gin_idx ON member USING gin (nickname gin_trgm_ops);").executeUpdate()
    }
}
