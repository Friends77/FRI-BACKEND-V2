package com.friends.board.entity

import com.friends.board.exception.VoteOptionPositiveCountException
import com.friends.common.entity.BaseModifiableEntity
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
import jakarta.persistence.OneToOne

@Entity
class Vote(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    val id: Long = 0L,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    var board: Board,
    @OneToMany(mappedBy = "vote", cascade = [CascadeType.ALL], orphanRemoval = true)
    var options: MutableList<VoteOption> = mutableListOf(),
) : BaseModifiableEntity() {
    fun addOption(content: String): VoteOption {
        val option = VoteOption(vote = this, content = content)
        options.add(option)
        return option
    }
}

/*
* 투표 선택지 테이블
*/
@Entity
class VoteOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_option_id")
    val id: Long = 0L,
    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = false)
    val vote: Vote,
    @Column(length = 255, nullable = false)
    var content: String,
    var voteCount: Int = 0,
) {
    fun increaseVoteCount() {
        voteCount++
    }

    fun decreaseVoteCount() {
        if (voteCount > 0) {
            voteCount--
        } else {
            throw VoteOptionPositiveCountException()
        }
    }
}
