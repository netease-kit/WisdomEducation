/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import com.google.gson.Gson
import com.netease.nimlib.sdk.passthrough.model.PassthroughProxyData
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import okhttp3.internal.and
import okio.Buffer
import retrofit2.http.*
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.regex.Pattern

/**
 * Created by hzsunyj on 2021/5/27.
 */
class PassthroughDataFactory {

    companion object {
        private const val PARAM = "[a-zA-Z][a-zA-Z0-9_-]*"
        private const val PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#"
        private val PARAM_URL_REGEX = Pattern.compile("\\{(${Companion.PARAM})\\}")
        private val PATH_TRAVERSAL = Pattern.compile("(.*/)?(\\.|%2e|%2E){1,2}(/.*)?")
        private val HEX_DIGITS = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        )
        private val gson: Gson = Gson()
    }

    lateinit var method: Method
    lateinit var args: Array<out Any>
    lateinit var methodAnnotations: Array<Annotation>
    lateinit var parameterAnnotationsArray: Array<Array<Annotation>>
    lateinit var parameterTypes: Array<Type>
    private var httpMethod: Int = 0
    private var hasBody: Boolean = false
    private var relativeUrl: String? = null
    private var body: String? = null

    fun builder(method: Method, args: Array<out Any>): PassthroughProxyData {
        this.method = method
        this.args = args
        this.methodAnnotations = method.annotations
        this.parameterTypes = method.genericParameterTypes
        this.parameterAnnotationsArray = method.parameterAnnotations
        methodAnnotations.forEach {
            parseMethodAnnotation(it)
        }
        parseParameter()
        return PassthroughProxyData(relativeUrl, httpMethod, getHeader(), body)
    }

    private fun getHeader(): String {
        return gson.toJson(RetrofitManager.instance().getHeader())
    }

    private fun parseParameter() {
        var count = parameterAnnotationsArray.size
        for (index in 0 until count) {
            parameterAnnotationsArray[index].forEach {
                parseParameterAnnotation(index, parameterTypes[index], it)
            }
        }
    }

    private fun parseParameterAnnotation(index: Int, type: Type, annotation: Annotation) {
        when (annotation) {
            is Path -> addPathParam(annotation.value, args[index].toString(), false)
            is Body -> parseBody(args[index])
        }
    }

    private fun parseBody(arg: Any) {
        body = gson.toJson(arg)
    }

    private fun parseMethodAnnotation(annotation: Annotation) {
        when (annotation) {
            is DELETE -> {
                parseHttpMethodAndPath(PassthroughProxyData.Method.DELETE, annotation.value, false)
            }
            is GET -> {
                parseHttpMethodAndPath(PassthroughProxyData.Method.GET, annotation.value, false)
            }
            is POST -> {
                parseHttpMethodAndPath(PassthroughProxyData.Method.POST, annotation.value, true)
            }
            is PUT -> {
                parseHttpMethodAndPath(PassthroughProxyData.Method.PUT, annotation.value, true)
            }
        }
    }

    private fun addPathParam(name: String, value: String, encoded: Boolean) {
        if (relativeUrl == null) {
            // The relative URL is cleared when the first query parameter is set.
            throw AssertionError()
        }
        val replacement = canonicalizeForPath(value, encoded)
        val newRelativeUrl = relativeUrl!!.replace("{$name}", replacement)
        require(!PATH_TRAVERSAL.matcher(newRelativeUrl)
            .matches()) { "@Path parameters shouldn't perform path traversal ('.' or '..'): $value" }
        relativeUrl = newRelativeUrl
    }

    private fun canonicalizeForPath(input: String, alreadyEncoded: Boolean): String {
        var codePoint: Int
        var i = 0
        val limit = input.length
        while (i < limit) {
            codePoint = input.codePointAt(i)
            if (codePoint < 0x20 || codePoint >= 0x7f || Companion.PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint.toChar()) != -1 || !alreadyEncoded && (codePoint == '/'.toInt() || codePoint == '%'.toInt())) {
                // Slow path: the character at i requires encoding!
                val out = Buffer()
                out.writeUtf8(input, 0, i)
                canonicalizeForPath(out, input, i, limit, alreadyEncoded)
                return out.readUtf8()
            }
            i += Character.charCount(codePoint)
        }

        // Fast path: no characters required encoding.
        return input
    }

    private fun canonicalizeForPath(out: Buffer, input: String, pos: Int, limit: Int, alreadyEncoded: Boolean) {
        var utf8Buffer: Buffer? = null // Lazily allocated.
        var codePoint: Int
        var i = pos
        while (i < limit) {
            codePoint = input.codePointAt(i)
            if (alreadyEncoded && (codePoint == '\t'.toInt() || codePoint == '\n'.toInt() || codePoint == '\r'.toInt())) {
                // Skip this character.
            } else if (codePoint < 0x20 || codePoint >= 0x7f || Companion.PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(
                    codePoint.toChar()) != -1 || !alreadyEncoded && (codePoint == '/'.toInt() || codePoint == '%'.toInt())
            ) {
                // Percent encode this character.
                if (utf8Buffer == null) {
                    utf8Buffer = Buffer()
                }
                utf8Buffer.writeUtf8CodePoint(codePoint)
                while (!utf8Buffer.exhausted()) {
                    val b: Int = utf8Buffer.readByte() and 0xff
                    out.writeByte('%'.toInt())
                    out.writeByte(HEX_DIGITS[b shr 4 and 0xf].toInt())
                    out.writeByte(HEX_DIGITS[b and 0xf].toInt())
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint)
            }
            i += Character.charCount(codePoint)
        }
    }


    private fun parseHttpMethodAndPath(httpMethod: Int, value: String, hasBody: Boolean) {
        require(this.httpMethod == 0) {
            "Only one HTTP method is allowed"
        }
        this.httpMethod = httpMethod
        this.hasBody = hasBody
        if (value.isEmpty()) {
            return
        }
        // Get the relative URL path and existing query string, if present.
        val question = value.indexOf('?')
        if (question != -1 && question < value.length - 1) {
            // Ensure the query string does not have any named parameters.
            val queryParams = value.substring(question + 1)
            val queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams)
            if (queryParamMatcher.find()) {
                require(false) {
                    "URL query string \"%s\" must not have replace block. For dynamic query parameters use @Query."
                }
            }
        }
        this.relativeUrl = value
    }
}
