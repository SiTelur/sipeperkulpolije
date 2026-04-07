package com.polije.sipeperpolije.feature.master.presentation.jadwal.list.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalSearchBar(modifier: Modifier = Modifier, query: String, onQueryChange: (String) -> Unit) {
    val colors1 = SearchBarDefaults.colors()
    SearchBar(
        state = rememberSearchBarState(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { },
                expanded = false,
                onExpandedChange = {},
                enabled = true,
                placeholder = { Text("Cari Jadwal") },
                leadingIcon = null,
                trailingIcon = null,
                colors = colors1.inputFieldColors,
                interactionSource = null,
            )
        },
        modifier = modifier,
    )
}
