package com.friends.common.util

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

fun <T : Any> KotlinJdslJpqlExecutor.getSlice(
    pageable: Pageable,
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): Slice<T> = this.findSlice(pageable, init) as Slice<T>

fun <T : Any> KotlinJdslJpqlExecutor.getList(
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): List<T> = this.findAll(init = init) as List<T>

fun <T : Any> KotlinJdslJpqlExecutor.getSingle(
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): T = this.findAll(init = init).first() as T

fun <T : Any> KotlinJdslJpqlExecutor.find(
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): T? = this.findAll(init = init).firstOrNull()

fun <T : Any> KotlinJdslJpqlExecutor.getLimitList(
    offset: Int,
    limit: Int,
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): List<T> = this.findAll(offset = offset, limit = limit, init = init) as List<T>
