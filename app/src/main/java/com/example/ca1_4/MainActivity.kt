package com.example.ca1_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

data class Contact(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val phoneNumber: String,
    val email: String
)

class ContactViewModel : ViewModel() {
    private val _contacts = MutableStateFlow(
        listOf(
            Contact(id = 1, name = "Alice Smith", phoneNumber = "555-0101", email = "alice@example.com"),
            Contact(id = 2, name = "Bob Jones", phoneNumber = "555-0102", email = "bob@example.com"),
            Contact(id = 3, name = "Charlie Brown", phoneNumber = "555-0103", email = "charlie@example.com")
        )
    )
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addContact(name: String, phoneNumber: String, email: String) {
        if (name.isBlank()) return
        _contacts.value = _contacts.value + Contact(id = System.currentTimeMillis(), name = name, phoneNumber = phoneNumber, email = email)
    }

    fun deleteContact(contact: Contact) {
        _contacts.value = _contacts.value.filter { it.id != contact.id }
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val contacts by viewModel.contacts.collectAsState()
                    val searchQuery by viewModel.searchQuery.collectAsState()

                    ContactScreenUi(
                        contacts = contacts.map { ContactUiModel(it.id, it.name, it.phoneNumber, it.email) },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onAddContact = { name, phone, email -> viewModel.addContact(name, phone, email) },
                        onDeleteContact = { contact -> viewModel.deleteContact(Contact(contact.id, contact.name, contact.phoneNumber, contact.email)) }
                    )
                }
            }
        }
    }
}
