package com.example.myevents.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.User
import com.example.myevents.data.repositories.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UserState(val user: String, val isLogged: Boolean)
data class UserEditState(val newName: String, val newSurname: String, val newImage: String)

interface UserActions {
    fun addUser(user: User): Job
    fun removeUser(user: User): Job
}

class UserViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(UserState("", false))
        private set
    var user by mutableStateOf<User?>(null)
        private set
    var imageUri by mutableStateOf<String?>(null)
        private set
    private var editState by mutableStateOf(UserEditState("", "", ""))
    var bioUser by mutableStateOf("")
        private set
    var bioPassword by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            state = UserState(repository.user.first(), repository.isLogged.first())
            if (state.isLogged) {
                user = repository.getUserByUsername(state.user)
            }
        }
    }

    fun setLoggedUser(value: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            repository.setLoggedUser(value)
            repository.setIsLogged(rememberMe)
            if (repository.bioUser.first().isEmpty() && repository.bioPassword.first().isEmpty()) {
                repository.setBiometricUser(value)
                repository.setBiometricPassword(password)
            }
        }
        state = UserState(value, true)
    }

    fun logout() {
        viewModelScope.launch {
            repository.setLoggedUser("")
            repository.setIsLogged(false)
        }
        state = UserState("", false)
        user = null
    }

    fun checkLogin(username: String, password: String) = viewModelScope.launch {
        user = repository.getUserForLogin(username, password)
    }

    fun isUsernameAlreadyTaken(username: String) : Boolean {
        viewModelScope.launch {
            user = repository.getUserByUsername(username)
        }
        return user != null
    }

    fun getImageUri(username: String) : String? {
        viewModelScope.launch {
            imageUri = repository.getImageUriByUsername(username)
        }
        return imageUri
    }

    fun saveEditState() {
        if (user!! != User(state.user, editState.newName, editState.newSurname, user!!.password, user!!.imageUri)
        ) {
            viewModelScope.launch {
                repository.upsertUser(
                    User(
                        state.user,
                        if (editState.newName == "" || editState.newName == user!!.name) user!!.name else editState.newName,
                        if (editState.newSurname == "" || editState.newSurname == user!!.surname) user!!.surname else editState.newSurname,
                        user!!.password,
                        if (editState.newImage == "" || editState.newImage == user!!.imageUri) user!!.imageUri else editState.newImage,
                    )
                )
                user = repository.getUserByUsername(state.user)
            }
        }
        clearEditState()
    }

    fun setNewName(newName: String) {
        editState = editState.copy(newName = newName)
    }

    fun setNewSurname(newSurname: String) {
        editState = editState.copy(newSurname = newSurname)
    }

    fun setNewImage(newImage: String) {
        editState = editState.copy(newImage = newImage)
    }

    fun clearEditState() {
        editState = UserEditState("", "", "")
    }

    val actions = object : UserActions {
        override fun addUser(user: User) = viewModelScope.launch {
            repository.upsertUser(user)
        }

        override fun removeUser(user: User) = viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun canLogWithBiometric() = viewModelScope.launch {
        bioUser = repository.bioUser.first()
        bioPassword = repository.bioPassword.first()
        user = repository.getUserForLogin(bioUser, bioPassword)
    }
}
