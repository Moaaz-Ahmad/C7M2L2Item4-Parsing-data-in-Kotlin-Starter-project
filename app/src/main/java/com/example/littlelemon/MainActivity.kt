package com.example.littlelemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.littlelemon.ui.theme.LittleLemonTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json

class MainActivity : ComponentActivity() {
    private suspend fun getMenu(category: String): List<String> {
        val response: Map<String, MenuCategory> =
            client.get("https://raw.githubusercontent.com/MetaMatter/little-lemon/main/menu.json").body()
        return response.values.first().menu
    }
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }
    private val menuItemsLiveData = MutableLiveData<List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            val menuItems = getMenu("Salad")
            menuItemsLiveData.value = menuItems
        }

        setContent {
            LittleLemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MenuItems(menuItemsLiveData.value ?: emptyList())
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItems(
    items: List<String> = emptyList(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyColumn {
            itemsIndexed(items) { _, item ->
                MenuItemDetails(item)
            }
        }
    }
}

@Composable
fun MenuItemDetails(menuItem: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = menuItem)
    }
}

