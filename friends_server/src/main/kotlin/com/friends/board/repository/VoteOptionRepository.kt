package com.friends.board.repository

import com.friends.board.entity.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionRepository : JpaRepository<VoteOption, Long>
