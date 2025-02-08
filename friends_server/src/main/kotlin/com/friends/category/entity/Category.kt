package com.friends.category.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "category", uniqueConstraints = [UniqueConstraint(columnNames = ["name"], name = "category_name_unique")])
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val id: Long = 0L,
    @Column(nullable = false, length = 40, unique = true)
    val name: String,
    @Column(nullable = false)
    val type: CategoryType,
    val image: String? = null,
)

enum class CategoryType {
    SUBJECT,
    REGION,
}
