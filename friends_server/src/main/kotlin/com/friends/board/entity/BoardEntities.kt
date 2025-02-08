package com.friends.board.entity

import com.friends.board.dto.BoardRequestDto
import com.friends.category.entity.Category
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy

@Entity
class Board(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(length = 500, nullable = false)
    var content: String,
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @OrderBy("id asc")
    var comments: List<Comment> = mutableListOf(),
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,
) : BaseModifiableEntity() {
    fun updateBoard(boardUpdateDto: BoardRequestDto) {
        content = boardUpdateDto.content
    }

    fun increaseLike() {
        likeCount++
    }

    fun decreaseLike() {
        if (likeCount > 0) likeCount--
    }
}

@Entity
class BoardCategory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    var board: Board,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
)
