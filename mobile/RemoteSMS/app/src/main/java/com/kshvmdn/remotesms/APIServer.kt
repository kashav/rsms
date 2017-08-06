package com.kshvmdn.remotesms

import android.content.Context
import android.content.res.AssetManager
import android.util.Log

import com.kshvmdn.remotesms.controller.SMSController
import com.kshvmdn.remotesms.service.ServerService
import com.kshvmdn.remotesms.utils.sendMessage
import com.kshvmdn.remotesms.utils.toJson

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD

val TAG = "WebServer"

class APIServer(val context: Context) : RouterNanoHTTPD(ServerService.apiPort) {
    init {
        addMappings()
    }

    override fun addMappings() {
        super.addMappings()
        addRoute("sms/conversation", ConversationHandler::class.java)
        addRoute("sms/send", SendHandler::class.java)
        addRoute("/(.)+", StaticHandler::class.java, context.assets)
        addRoute("/", StaticHandler::class.java, context.assets)
        setNotFoundHandler(NotFoundHandler::class.java)
    }

    class ConversationHandler : JSONHandler() {
        override fun handleGet(query: Map<String, Any?>): CommonResponse {
            return try {
                val numbers = query["n"].toString().split(",")
                val messages = SMSController(App.context).execute(numbers).get()
                CommonResponse(true, null, messages)
            } catch (e: Exception) {
                CommonResponse(false, e.message, null)
            }
        }
    }

    class SendHandler : JSONHandler() {
        override fun handleGet(query: Map<String, Any?>) = handleSend(query)

        override fun handlePost(query: Map<String, Any?>, body: String?) = handleSend(query)

        fun handleSend(query: Map<String, Any?>): Any {
            if (!(query.containsKey("to") && query.containsKey("body"))) {
                return CommonResponse(false, "Missing fields", null)
            }

            try {
                val to = (query["to"] as ArrayList<String>)[0]
                val body = (query["body"] as ArrayList<String>)[0]
                sendMessage(to, body)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return CommonResponse(false, e.message, null)
            }

            return CommonResponse(true, "OK", null)
        }
    }

    class StaticHandler : DefaultHandler() {
        override fun getStatus() = Response.Status.OK

        override fun getText(): String? {
            throw UnsupportedOperationException()
        }

        override fun getMimeType(): String? {
            throw UnsupportedOperationException()
        }

        override fun get(uriResource: UriResource?, urlParams: MutableMap<String, String>?,
                         session: IHTTPSession?): Response? {
            if (uriResource != null && session != null) {
                val manager = uriResource.initParameter(AssetManager::class.java)

                try {
                    val uri = if (session.uri == "/") {
                        "index.html"
                    } else {
                        session.uri.subSequence(1, session.uri.length).toString()
                    }
                    val stream = manager.open(uri)
                    return NanoHTTPD.newChunkedResponse(status, getMimeTypeForFile(uri), stream)
                } catch (e: Exception) {
                    // TODO: Do something with this exception.
                }
            }

            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_FOUND,
                    "application/json",
                    toJson(CommonResponse(false, "Not found", null)))
        }
    }

    class NotFoundHandler : JSONHandler() {
        override fun handleGet(query: Map<String, Any?>) =
                CommonResponse(false, "Not found", null)

        override fun handlePost(query: Map<String, Any?>, body: String?) =
                CommonResponse(false, "Not found", null)
    }
}

open class JSONHandler : RouterNanoHTTPD.DefaultHandler() {
    override fun getMimeType(): String = "application/json"

    override fun getText(): String {
        throw UnsupportedOperationException()
    }

    override fun getStatus() = NanoHTTPD.Response.Status.OK

    override fun get(uriResource: RouterNanoHTTPD.UriResource?, urlParams: MutableMap<String, String>?,
                     session: NanoHTTPD.IHTTPSession?): NanoHTTPD.Response {
        if (!requireAuth(session)) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.UNAUTHORIZED,
                    mimeType, toJson(CommonResponse(false, "Not authorized", null)))
        }

        return when (session?.method) {
            NanoHTTPD.Method.GET -> {
                Log.d(TAG, "GET: ${session.uri}")
                val query = session.parameters ?: mapOf<String, Any?>()
                val data = handleGet(query)
                NanoHTTPD.newFixedLengthResponse(status, mimeType, toJson(data))
            }
            NanoHTTPD.Method.POST -> {
                Log.d(TAG, "POST: ${session.uri}")
                val query = session.parameters ?: mapOf<String, String?>()
                val body: MutableMap<String, String> = mutableMapOf()
                session.parseBody(body)
                val data = handlePost(query, body["postData"])
                NanoHTTPD.newFixedLengthResponse(status, mimeType, toJson(data))
            }
            else ->
                NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,
                        mimeType, toJson(CommonResponse(false, "Not found", null)))
        }
    }

    open fun handlePost(query: Map<String, Any?>, body: String?): Any {
        Log.w(TAG, "POST method not supported")
        return Any()
    }

    open fun handleGet(query: Map<String, Any?>): Any {
        Log.w(TAG, "GET method not supported")
        return Any()
    }

    private fun requireAuth(session: NanoHTTPD.IHTTPSession?): Boolean {
        return ServerService.token == "" || session!!.headers["token"] == ServerService.token
    }
}

