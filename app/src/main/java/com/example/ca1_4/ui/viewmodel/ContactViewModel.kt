package com.example.ca1_4.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca1_4.data.Contact
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val _contacts = MutableStateFlow(
        listOf(
            Contact(id = 1, name = "Alice Smith", phoneNumber = "555-0101", email = "alice@example.com"),
            Contact(id = 2, name = "Bob Jones", phoneNumber = "555-0102", email = "bob@example.com"),
            Contact(id = 3, name = "Charlie Brown", phoneNumber = "555-0103", email = "charlie@example.com"),
            Contact(id = 4, name = "Diana Prince", phoneNumber = "555-0104", email = "diana@example.com"),
            Contact(id = 5, name = "Evan Wright", phoneNumber = "555-0105", email = "evan@example.com")
        )
    )
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredContacts: StateFlow<List<Contact>> = combine(_contacts, _searchQuery) { contactsList, query ->
        if (query.isBlank()) {
            contactsList
        } else {
            contactsList.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addContact(name: String, phoneNumber: String, email: String) {
        if (name.isBlank()) return
        val newContact = Contact(
            id = System.currentTimeMillis(),
            name = name,
            phoneNumber = phoneNumber,
            email = email
        )
        _contacts.value = _contacts.value + newContact
    }

    fun deleteContact(contact: Contact) {
        _contacts.value = _contacts.value.filter { it.id != contact.id }
    }
}
