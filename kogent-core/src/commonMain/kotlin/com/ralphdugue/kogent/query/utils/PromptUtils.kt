package com.ralphdugue.kogent.query.utils

import com.ralphdugue.kogent.query.domain.entities.LLMResponse
import com.ralphdugue.kogent.query.domain.entities.Operation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PromptUtils {

    val json = Json {
        prettyPrint = true
    }

    // Example 1: Answer Only
    val answerOnly: String = json.encodeToString(
        LLMResponse(
            answer = "The total number of customers is 1532.",
            needsUpdate = false,
            operation = null
        )
    )

    // Example 2: SQL Update
    val sqlUpdate: String = json.encodeToString(
        LLMResponse(
            answer = "Updating customer record...",
            needsUpdate = true,
            operation = Operation.SqlQuery(
                dataSourceId = "my_database",
                query = "UPDATE customers SET age = 31 WHERE name = 'Alice'"
            )
        )
    )

    // Example 3: API Call
    val apiCall: String = json.encodeToString(
        LLMResponse(
            answer = "Fetching updated product information...",
            needsUpdate = true,
            operation = Operation.ApiCall(
                dataSourceId = "https://api.example.com",
                endpoint = "/products/123",
                method = "GET"
            )
        )
    )

    // Response Schema
    val responseSchema = """
    {
        "title": "Kogent Response",
        "type": "object",
        "properties": {
            "answer": {
                "type": "string",
                "description": "Human-readable answer to the query"
            },
            "needsUpdate": {
                "type": "boolean",
                "description": "Whether the underlying data needs an update"
            },
            "operation": {
                "type": "object",
                "description": "The operation to perform if update is needed (null if not)",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["SqlQuery", "ApiCall"]  // Add more types as needed
                    },
                    "dataSource": {
                        "type": "string",
                        "description": "Identifier of the data source to update"
                    },
                    // ... (add other properties for specific operation types)
                },
                "required": ["type", "dataSource"]
            }
        },
        "required": ["answer", "needsUpdate"]
    }
    """.trimIndent()

}