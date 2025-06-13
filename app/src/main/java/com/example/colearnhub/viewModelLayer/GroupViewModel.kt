package com.example.colearnhub.viewModelLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.*
import com.example.colearnhub.repositoryLayer.GroupRepository
import com.example.colearnhub.modelLayer.User
import com.example.colearnhub.repositoryLayer.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateGroupUiState(
    val isLoading: Boolean = false,
    val groupName: String = "",
    val groupDescription: String = "",
    val searchQuery: String = "",
    val searchResults: List<User> = emptyList(),
    val invitedUsers: List<User> = emptyList(),
    val errorMessage: String? = null,
    val isCreated: Boolean = false,
    val isSearching: Boolean = false
)

data class GroupsUiState(
    val isLoading: Boolean = false,
    val groups: List<GroupResponse> = emptyList(),
    val errorMessage: String? = null
)

data class GroupDetailsUiState(
    val isLoading: Boolean = false,
    val groupDetails: GroupResponse? = null,
    val errorMessage: String? = null
)

class GroupViewModel : ViewModel() {

    private val groupRepository = GroupRepository()
    private val userRepository = UserRepository()

    // Estado para criação de grupo
    private val _createGroupUiState = MutableStateFlow(CreateGroupUiState())
    val createGroupUiState: StateFlow<CreateGroupUiState> = _createGroupUiState.asStateFlow()

    // Estado para listagem de grupos
    private val _groupsUiState = MutableStateFlow(GroupsUiState())
    val groupsUiState: StateFlow<GroupsUiState> = _groupsUiState.asStateFlow()

    // Estado para detalhes do grupo
    private val _groupDetailsUiState = MutableStateFlow(GroupDetailsUiState())
    val groupDetailsUiState: StateFlow<GroupDetailsUiState> = _groupDetailsUiState.asStateFlow()

    /**
     * Atualiza o nome do grupo
     */
    fun updateGroupName(name: String) {
        _createGroupUiState.value = _createGroupUiState.value.copy(groupName = name)
    }

    /**
     * Atualiza a descrição do grupo
     */
    fun updateGroupDescription(description: String) {
        _createGroupUiState.value = _createGroupUiState.value.copy(groupDescription = description)
    }

    /**
     * Atualiza a query de pesquisa
     */
    fun updateSearchQuery(query: String) {
        _createGroupUiState.value = _createGroupUiState.value.copy(searchQuery = query)

        if (query.length >= 2) {
            searchUsers(query)
        } else {
            _createGroupUiState.value = _createGroupUiState.value.copy(
                searchResults = emptyList(),
                isSearching = false
            )
        }
    }

    /**
     * Pesquisa usuários através do UserRepository
     */
    private fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                _createGroupUiState.value = _createGroupUiState.value.copy(isSearching = true)

                // Buscar usuários através do GroupRepository
                val searchResults = groupRepository.searchUsers(query)

                // Filtrar usuários já convidados
                val currentState = _createGroupUiState.value
                val availableUsers = searchResults.filter { user ->
                    !currentState.invitedUsers.any { invitedUser -> invitedUser.id == user.id }
                }

                _createGroupUiState.value = _createGroupUiState.value.copy(
                    searchResults = availableUsers,
                    isSearching = false
                )
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao pesquisar usuários: ${e.message}")
                _createGroupUiState.value = _createGroupUiState.value.copy(
                    isSearching = false,
                    errorMessage = "Erro ao pesquisar usuários"
                )
            }
        }
    }

    /**
     * Adiciona usuário à lista de convidados
     */
    fun addUserToInvite(user: User) {
        val currentInvited = _createGroupUiState.value.invitedUsers.toMutableList()
        if (!currentInvited.any { it.id == user.id }) {
            currentInvited.add(user)
            _createGroupUiState.value = _createGroupUiState.value.copy(
                invitedUsers = currentInvited,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    /**
     * Remove usuário da lista de convidados
     */
    fun removeUserFromInvite(user: User) {
        val currentInvited = _createGroupUiState.value.invitedUsers.toMutableList()
        currentInvited.removeAll { it.id == user.id }
        _createGroupUiState.value = _createGroupUiState.value.copy(invitedUsers = currentInvited)
    }

    /**
     * Cria o grupo e envia convites
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createGroup(ownerId: String) {
        viewModelScope.launch {
            try {
                val currentState = _createGroupUiState.value

                if (currentState.groupName.trim().isEmpty()) {
                    _createGroupUiState.value = currentState.copy(
                        errorMessage = "Nome do grupo é obrigatório"
                    )
                    return@launch
                }

                _createGroupUiState.value = currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val request = CreateGroupRequest(
                    name = currentState.groupName.trim(),
                    description = currentState.groupDescription.trim().takeIf { it.isNotEmpty() },
                    invitedUserIds = currentState.invitedUsers.map { it.id }
                )

                val createdGroup = groupRepository.createGroup(request, ownerId)

                if (createdGroup != null) {
                    // Após criar o grupo, criar as entradas de convite na tabela group_members
                    // com accept = null para os usuários convidados
                    currentState.invitedUsers.forEach { user ->
                        try {
                            val groupMember = Group_Members(
                                user_id = user.id,
                                group_id = createdGroup.id!!,
                                accept = null // Null indica que é um convite pendente
                            )
                            groupRepository.createGroupMember(groupMember)
                            Log.d("GroupViewModel", "Convite enviado para usuário: ${user.username}")
                        } catch (e: Exception) {
                            Log.e("GroupViewModel", "Erro ao enviar convite para ${user.username}: ${e.message}")
                        }
                    }

                    _createGroupUiState.value = currentState.copy(
                        isLoading = false,
                        isCreated = true
                    )
                    Log.d("GroupViewModel", "Grupo criado com sucesso: ${createdGroup.name}")
                } else {
                    _createGroupUiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Erro ao criar grupo"
                    )
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao criar grupo: ${e.message}")
                _createGroupUiState.value = _createGroupUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado ao criar grupo"
                )
            }
        }
    }

    /**
     * Carrega grupos do usuário
     */
    fun loadUserGroups(userId: String) {
        viewModelScope.launch {
            try {
                _groupsUiState.value = _groupsUiState.value.copy(isLoading = true)

                val groups = groupRepository.getUserGroups(userId)

                _groupsUiState.value = _groupsUiState.value.copy(
                    isLoading = false,
                    groups = groups
                )
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao carregar grupos: ${e.message}")
                _groupsUiState.value = _groupsUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar grupos"
                )
            }
        }
    }

    /**
     * Aceita convite para grupo
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptGroupInvite(userId: String, groupId: Long) {
        viewModelScope.launch {
            try {
                val success = groupRepository.acceptGroupInvite(userId, groupId)

                if (success) {
                    // Recarregar grupos
                    loadUserGroups(userId)
                    Log.d("GroupViewModel", "Convite aceito com sucesso para o grupo: $groupId")
                } else {
                    _groupsUiState.value = _groupsUiState.value.copy(
                        errorMessage = "Erro ao aceitar convite"
                    )
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao aceitar convite: ${e.message}")
                _groupsUiState.value = _groupsUiState.value.copy(
                    errorMessage = "Erro inesperado ao aceitar convite"
                )
            }
        }
    }

    /**
     * Rejeita convite para grupo
     */
    fun rejectGroupInvite(userId: String, groupId: Long) {
        viewModelScope.launch {
            try {
                val success = groupRepository.rejectGroupInvite(userId, groupId)

                if (success) {
                    // Recarregar grupos
                    loadUserGroups(userId)
                    Log.d("GroupViewModel", "Convite rejeitado com sucesso para o grupo: $groupId")
                } else {
                    _groupsUiState.value = _groupsUiState.value.copy(
                        errorMessage = "Erro ao rejeitar convite"
                    )
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao rejeitar convite: ${e.message}")
                _groupsUiState.value = _groupsUiState.value.copy(
                    errorMessage = "Erro inesperado ao rejeitar convite"
                )
            }
        }
    }

    /**
     * Limpa mensagem de erro
     */
    fun clearErrorMessage() {
        _createGroupUiState.value = _createGroupUiState.value.copy(errorMessage = null)
        _groupsUiState.value = _groupsUiState.value.copy(errorMessage = null)
    }

    /**
     * Reseta estado de criação
     */
    fun resetCreateGroupState() {
        _createGroupUiState.value = CreateGroupUiState()
    }

    /**
     * Carrega os detalhes de um grupo específico
     */
    fun loadGroupDetails(groupId: Long) {
        viewModelScope.launch {
            try {
                _groupDetailsUiState.value = _groupDetailsUiState.value.copy(isLoading = true)

                val groupDetails = groupRepository.getGroupDetails(groupId)

                _groupDetailsUiState.value = _groupDetailsUiState.value.copy(
                    isLoading = false,
                    groupDetails = groupDetails
                )
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao carregar detalhes do grupo: ${e.message}")
                _groupDetailsUiState.value = _groupDetailsUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar detalhes do grupo"
                )
            }
        }
    }

    /**
     * Remove um usuário de um grupo
     */
    fun leaveGroup(groupId: Long, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val success = groupRepository.leaveGroup(groupId)
                onSuccess(success)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao sair do grupo: ${e.message}")
                _groupDetailsUiState.value = _groupDetailsUiState.value.copy(
                    errorMessage = "Erro ao sair do grupo"
                )
                onSuccess(false)
            }
        }
    }


    /**
     * Remove um usuário de um grupo (versão atualizada)
     */
    fun leaveGroup(groupId: Long, userId: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Usar o método removeGroupMember que já existe
                val success = groupRepository.removeGroupMember(userId, groupId)
                onSuccess(success)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Erro ao sair do grupo: ${e.message}")
                _groupDetailsUiState.value = _groupDetailsUiState.value.copy(
                    errorMessage = "Erro ao sair do grupo"
                )
                onSuccess(false)
            }
        }
    }

    /**
     * Limpa mensagem de erro dos detalhes do grupo
     */
    fun clearGroupDetailsError() {
        _groupDetailsUiState.value = _groupDetailsUiState.value.copy(errorMessage = null)
    }

}