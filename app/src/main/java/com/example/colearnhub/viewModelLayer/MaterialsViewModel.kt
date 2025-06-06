package com.example.colearnhub.viewModelLayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.repositoryLayer.MaterialRepository
import com.example.colearnhub.repositoryLayer.UserRepository
import com.example.colearnhub.repositoryLayer.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {

    private val materialRepository = MaterialRepository()
    private val userRepository = UserRepository()

    // Estados para UI
    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials: StateFlow<List<Material>> = _materials.asStateFlow()

    private val _selectedMaterial = MutableStateFlow<Material?>(null)
    val selectedMaterial: StateFlow<Material?> = _selectedMaterial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Cache de utilizadores para mostrar nomes em vez de IDs
    private val _usersCache = MutableStateFlow<Map<String, User>>(emptyMap())
    val usersCache: StateFlow<Map<String, User>> = _usersCache.asStateFlow()

    /**
     * Cria um novo material
     */
    fun createMaterial(
        title: String,
        description: String? = null,
        fileUrl: String? = null,
        visibility: Boolean = true,
        languageId: Long? = null,
        authorId: String? = null,
        tagId: Long? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.createMaterial(
                title = title,
                description = description,
                fileUrl = fileUrl,
                visibility = visibility,
                languageId = languageId,
                authorId = authorId,
                tagId = tagId
            )

            if (result != null) {
                _selectedMaterial.value = result
                // Atualizar lista se necessário
                loadPublicMaterials()
            } else {
                _errorMessage.value = "Erro ao criar material"
            }

            _isLoading.value = false
        }
    }

    /**
     * Carrega material por ID
     */
    fun loadMaterialById(materialId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.getMaterialById(materialId)

            if (result != null) {
                _selectedMaterial.value = result
                // Carregar dados do autor if needed
                result.author_id?.let { authorId ->
                    loadUserInfo(authorId)
                }
            } else {
                _errorMessage.value = "Material não encontrado"
            }

            _isLoading.value = false
        }
    }

    /**
     * Carrega todos os materiais públicos
     */
    fun loadPublicMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.getPublicMaterials()
            _materials.value = result

            // Carregar informações dos autores
            loadAuthorsInfo(result)

            if (result.isEmpty()) {
                _errorMessage.value = null // Não mostrar erro quando vazio
            }

            _isLoading.value = false
        }
    }

    /**
     * Carrega materiais por autor (String agora)
     */
    fun loadMaterialsByAuthor(authorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.getMaterialsByAuthor(authorId)
            _materials.value = result

            // Carregar info do autor
            loadUserInfo(authorId)

            _isLoading.value = false
        }
    }

    /**
     * Pesquisa materiais por título
     */
    fun searchMaterials(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.searchMaterialsByTitle(query)
            _materials.value = result

            // Carregar informações dos autores
            loadAuthorsInfo(result)

            _isLoading.value = false
        }
    }

    /**
     * Atualiza um material
     */
    fun updateMaterial(
        materialId: Long,
        title: String? = null,
        description: String? = null,
        visibility: Boolean? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.updateMaterial(materialId, title, description, visibility)

            if (result != null) {
                _selectedMaterial.value = result
                // Atualizar lista se necessário
                loadPublicMaterials()
            } else {
                _errorMessage.value = "Erro ao atualizar material"
            }

            _isLoading.value = false
        }
    }

    /**
     * Elimina um material
     */
    fun deleteMaterial(materialId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val success = materialRepository.deleteMaterial(materialId)

            if (success) {
                // Remover da lista atual
                _materials.value = _materials.value.filter { it.id != materialId }
                _selectedMaterial.value = null
            } else {
                _errorMessage.value = "Erro ao eliminar material"
            }

            _isLoading.value = false
        }
    }

    /**
     * Carrega informações dos autores dos materiais
     */
    private suspend fun loadAuthorsInfo(materials: List<Material>) {
        val authorIds = materials.mapNotNull { it.author_id }.distinct()
        val currentCache = _usersCache.value.toMutableMap()

        authorIds.forEach { authorId ->
            if (!currentCache.containsKey(authorId)) {
                userRepository.getUserById(authorId)?.let { user ->
                    currentCache[authorId] = user
                }
            }
        }

        _usersCache.value = currentCache
    }

    /**
     * Carrega informação de um utilizador específico
     */
    private suspend fun loadUserInfo(userId: String) {
        if (!_usersCache.value.containsKey(userId)) {
            userRepository.getUserById(userId)?.let { user ->
                val currentCache = _usersCache.value.toMutableMap()
                currentCache[userId] = user
                _usersCache.value = currentCache
            }
        }
    }

    /**
     * Obtém o nome do utilizador pelo ID
     */
    fun getUserName(userId: String?): String {
        return userId?.let {
            _usersCache.value[it]?.name ?: _usersCache.value[it]?.username ?: "Utilizador"
        } ?: "Anónimo"
    }

    /**
     * Obtém o username pelo ID
     */
    fun getUserUsername(userId: String?): String {
        return userId?.let {
            _usersCache.value[it]?.username ?: "user"
        } ?: "anonymous"
    }

    /**
     * Limpa mensagens de erro
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Limpa material selecionado
     */
    fun clearSelectedMaterial() {
        _selectedMaterial.value = null
    }
}