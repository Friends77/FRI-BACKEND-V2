package com.friends.alarm.entity

import com.friends.member.entity.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class MemberLastReadAlarm(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    val alarm: Alarm,
)
