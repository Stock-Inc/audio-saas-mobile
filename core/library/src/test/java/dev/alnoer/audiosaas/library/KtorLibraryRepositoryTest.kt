package dev.alnoer.audiosaas.library

import dev.alnoer.audiosaas.auth.model.AuthTokens
import dev.alnoer.audiosaas.auth.repository.AccessTokenProvider
import dev.alnoer.audiosaas.auth.repository.AuthSessionRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class KtorLibraryRepositoryTest {
    private val audio = """{"id":"audio-1","originalFilename":"Meeting.webm","status":"COMPLETED","folder":null,"tags":[],"sizeBytes":42,"createdAt":"2026-11-18T08:00:00Z","errorMessage":null}"""

    @Test
    fun rootAllCombinedFiltersAndSortAreDistinct() = runBlocking {
        val requests = mutableListOf<HttpRequestData>()
        withRepository { request ->
            requests.add(request)
            respond("[$audio]", HttpStatusCode.OK)
        }.use { fixture ->
            fixture.repository.audio()
            fixture.repository.audio("null", "tag-1", AudioSort.NAME_ASC)
            fixture.repository.audio("folder-1", "tag-1", AudioSort.SIZE_DESC)
            assertNull(requests[0].url.parameters["folderId"])
            assertEquals("null", requests[1].url.parameters["folderId"])
            assertEquals("tag-1", requests[1].url.parameters["tagId"])
            assertEquals("NAME_ASC", requests[1].url.parameters["sort"])
            assertEquals("folder-1", requests[2].url.parameters["folderId"])
            assertEquals("SIZE_DESC", requests[2].url.parameters["sort"])
            assertEquals("Bearer access", requests[0].headers[HttpHeaders.Authorization])
        }
    }

    @Test
    fun movesAndTagClearingSendExactBodies() = runBlocking {
        val requests = mutableListOf<HttpRequestData>()
        withRepository { request ->
            requests.add(request)
            respond(if (request.url.encodedPath == "/api/v1/audio/folder") "[$audio]" else audio)
        }.use { fixture ->
            fixture.repository.move("audio-1", null)
            fixture.repository.moveMany(setOf("audio-1", "audio-2"), "folder-1")
            fixture.repository.assignTags("audio-1", emptySet())
            assertEquals(HttpMethod.Put, requests[0].method)
            assertEquals("/api/v1/audio/audio-1/folder", requests[0].url.encodedPath)
            assertEquals(JsonNull, body(requests[0])["folderId"])
            assertEquals(2, body(requests[1]).getValue("audioIds").jsonArray.size)
            assertEquals("folder-1", body(requests[1]).getValue("folderId").jsonPrimitive.content)
            assertEquals(JsonArray(emptyList()), body(requests[2])["tagIds"])
        }
    }

    @Test
    fun creationRenameChildrenAndNoContentDelete() = runBlocking {
        val requests = mutableListOf<HttpRequestData>()
        withRepository { request ->
            requests.add(request)
            when (request.method) {
                HttpMethod.Delete -> respond("", HttpStatusCode.NoContent)
                HttpMethod.Post -> respond("""{"id":"folder-1","name":"Child","parentId":"parent"}""", HttpStatusCode.Created)
                HttpMethod.Patch -> respond("""{"id":"folder-1","name":"Renamed","parentId":"parent"}""")
                else -> respond("[]")
            }
        }.use { fixture ->
            assertEquals("parent", fixture.repository.saveFolder(null, " Child ", "parent").parentId)
            fixture.repository.saveFolder("folder-1", "Renamed", null)
            fixture.repository.children(null)
            fixture.repository.deleteFolder("folder-1")
            assertEquals("Child", body(requests[0]).getValue("name").jsonPrimitive.content)
            assertEquals("parent", body(requests[0]).getValue("parentId").jsonPrimitive.content)
            assertFalse(body(requests[1]).containsKey("parentId"))
            assertEquals("/api/v1/folders/null/children", requests[2].url.encodedPath)
        }
    }

    @Test
    fun statusesDoNotParseErrorBodiesOrRetryMutations() = runBlocking {
        for (status in listOf(400, 401, 404, 409, 302, 500)) {
            var calls = 0
            withRepository {
                calls++
                respond("not JSON", HttpStatusCode.fromValue(status))
            }.use { fixture ->
                try {
                    fixture.repository.deleteFolder("folder-1")
                    fail("Expected failure")
                } catch (e: LibraryException) { assertEquals(status, e.status) }
                assertEquals(1, calls)
                assertEquals(status == 401, fixture.session.cleared)
            }
        }
    }

    @Test
    fun nameValidationTrimsAndRejectsInvalidLength() {
        assertEquals("Встречи", validatedName("  Встречи  "))
        for (name in listOf("", "   ", "a".repeat(101))) {
            assertThrows(IllegalArgumentException::class.java) { validatedName(name) }
        }
        assertEquals(100, validatedName("a".repeat(100)).length)
    }

    private fun body(request: HttpRequestData) = Json.parseToJsonElement((request.body as TextContent).text).jsonObject

    private fun withRepository(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): Fixture {
        val client = HttpClient(MockEngine(handler))
        val session = Session()
        val repository = KtorLibraryRepository(client, object : AccessTokenProvider {
            override suspend fun validAccessToken() = "access"
        }, session)
        repository.baseUrl = "https://example.test/"
        return Fixture(repository, session, client)
    }

    private class Session : AuthSessionRepository {
        var cleared = false
        override val tokens = flowOf<AuthTokens?>(null)
        override suspend fun saveTokens(tokens: AuthTokens) = Unit
        override suspend fun clearTokens() { cleared = true }
    }

    private class Fixture(val repository: KtorLibraryRepository, val session: Session, val client: HttpClient) : AutoCloseable {
        override fun close() = client.close()
    }
}
