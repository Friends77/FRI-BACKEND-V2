package com.friends.support

import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

fun MockHttpServletRequestBuilder.withAuth(): MockHttpServletRequestBuilder = this.header("user_id", 1)

fun getWithAuthentication(url: String) = get(url).withAuth()

fun postWithAuthentication(url: String) = post(url).withAuth()

fun patchWithAuthentication(url: String) = patch(url).withAuth()

fun putWithAuthentication(url: String) = put(url).withAuth()

fun deleteWithAuthentication(url: String) = delete(url).withAuth()

fun multipartPatchWithAuthentication(url: String) = multipart(HttpMethod.PATCH, url).apply { header("user_id", 1) }

fun multipartWithAuthentication(url: String) = multipart(url).apply { header("user_id", 1) }
