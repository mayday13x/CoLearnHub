package com.example.colearnhub.viewModelLayer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.LanguageData
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.repositoryLayer.UserRepository
import com.example.colearnhub.repositoryLayer.TagRepository
import com.example.colearnhub.repositoryLayer.LanguageRepository
import com.example.colearnhub.modelLayer.User
import com.example.colearnhub.modelLayer.TagData
import com.example.colearnhub.repositoryLayer.MaterialsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {

    private val materialRepository = MaterialsRepository()
    private val userRepository = UserRepository()
    private val tagRepository = TagRepository()
    private val languageRepository = LanguageRepository()

    // Estados para UI
    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials: StateFlow<List<Material>> = _materials.asStateFlow()

    private val _userMaterials = MutableStateFlow<List<Material>>(emptyList())
    val userMaterials: StateFlow<List<Material>> = _userMaterials.asStateFlow()

    private val _selectedMaterial = MutableStateFlow<Material?>(null)
    val selectedMaterial: StateFlow<Material?> = _selectedMaterial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Cache de utilizadores para mostrar nomes em vez de IDs
    private val _usersCache = MutableStateFlow<Map<String, User>>(emptyMap())
    val usersCache: StateFlow<Map<String, User>> = _usersCache.asStateFlow()

    // Cache de tags
    private val _tagsCache = MutableStateFlow<Map<Long, TagData>>(emptyMap())
    val tagsCache: StateFlow<Map<Long, TagData>> = _tagsCache.asStateFlow()

    // Cache de languages
    private val _languagesCache = MutableStateFlow<Map<Long, LanguageData>>(emptyMap())
    val languagesCache: StateFlow<Map<Long, LanguageData>> = _languagesCache.asStateFlow()

    init {
        // Carregar dados iniciais
        loadPublicMaterials()
    }

    /**
     * Cria um novo material
     */
    fun createMaterial(
        title: String,
        description: String? = null,
        file_url: String? = null,
        visibility: Boolean = true,
        language: Long? = null,
        author_id: String? = null,
        tagIds: List<Long>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.createMaterial(
                title = title,
                description = description,
                file_url = file_url,
                visibility = visibility,
                language = language,
                author_id = author_id,
                tagIds = tagIds
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

            val result = materialRepository.getMaterialByIdWithTags(materialId)

            if (result != null) {
                _selectedMaterial.value = result
                // Carregar dados do autor if needed
                result.author_id?.let { author_id ->
                    loadUserInfo(author_id)
                }
                // Carregar language se necessário
                result.language?.let { languageId ->
                    loadLanguageInfo(languageId)
                }
                // Carregar tags
                result.tags?.forEach { tag ->
                    loadTagInfo(tag.id)
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

            // Carregar informações dos autores, tags e languages
            loadAuthorsInfo(result)
            loadTagsInfo(result)
            loadLanguagesInfo(result)

            _isLoading.value = false
        }
    }

    /**
     * Carrega materiais por autor
     */
    fun loadMaterialsByAuthor(author_id: String) {
        viewModelScope.launch {
            Log.d("MaterialViewModel", "loadMaterialsByAuthor: userId = $author_id")
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.getMaterialsByAuthor(author_id)
            _userMaterials.value = result

            // Carregar info do autor, tags e languages
            loadAuthorsInfo(result)
            loadTagsInfo(result)
            loadLanguagesInfo(result)

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

            // Carregar informações dos autores, tags e languages
            loadAuthorsInfo(result)
            loadTagsInfo(result)
            loadLanguagesInfo(result)

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
        visibility: Boolean? = null,
        language: Long? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = materialRepository.updateMaterial(
                materialId = materialId,
                title = title,
                description = description,
                visibility = visibility,
                language = language
            )

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
                _userMaterials.value = _userMaterials.value.filter { it.id != materialId }
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

        // Carregar apenas autores que não estão no cache
        val missingIds = authorIds.filterNot { currentCache.containsKey(it) }

        if (missingIds.isNotEmpty()) {
            userRepository.getUsersByIds(missingIds).forEach { user ->
                user.id.let { userId ->
                    currentCache[userId] = user
                }
            }
            _usersCache.value = currentCache
        }
    }

    /**
     * Carrega informações das tags dos materiais
     */
    private suspend fun loadTagsInfo(materials: List<Material>) {
        val tagIds = materials.flatMap { it.tags!! }.map { it.id }.distinct()
        val currentCache = _tagsCache.value.toMutableMap()

        tagIds.forEach { tagId ->
            if (!currentCache.containsKey(tagId)) {
                tagRepository.getTagById(tagId)?.let { tag ->
                    currentCache[tagId] = tag
                }
            }
        }

        _tagsCache.value = currentCache
    }

    /**
     * Carrega informações das languages dos materiais
     */
    private suspend fun loadLanguagesInfo(materials: List<Material>) {
        val languageIds = materials.mapNotNull { it.language }.distinct()
        val currentCache = _languagesCache.value.toMutableMap()

        languageIds.forEach { languageId ->
            if (!currentCache.containsKey(languageId)) {
                languageRepository.getLanguageById(languageId)?.let { language ->
                    currentCache[languageId] = language
                }
            }
        }

        _languagesCache.value = currentCache
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
     * Carrega informação de uma tag específica
     */
    private suspend fun loadTagInfo(tagId: Long) {
        if (!_tagsCache.value.containsKey(tagId)) {
            tagRepository.getTagById(tagId)?.let { tag ->
                val currentCache = _tagsCache.value.toMutableMap()
                currentCache[tagId] = tag
                _tagsCache.value = currentCache
            }
        }
    }

    /**
     * Carrega informação de uma language específica
     */
    private suspend fun loadLanguageInfo(languageId: Long) {
        if (!_languagesCache.value.containsKey(languageId)) {
            languageRepository.getLanguageById(languageId)?.let { language ->
                val currentCache = _languagesCache.value.toMutableMap()
                currentCache[languageId] = language
                _languagesCache.value = currentCache
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
     * Obtém o nome da tag pelo ID
     */
    fun getTagName(tagId: Long?): String {
        return tagId?.let {
            _tagsCache.value[it]?.description ?: "Tag"
        } ?: ""
    }

    /**
     * Obtém informações da language (nome e bandeira) pelo ID
     */
    fun getLanguageInfo(languageId: Long?): Pair<String, String> {
        return if (languageId != null) {
            // Primeiro tenta buscar do cache
            _languagesCache.value[languageId]?.let { languageData ->
                languageRepository.getLanguageDisplayInfo(languageId)
            } ?: languageRepository.getLanguageDisplayInfo(languageId)
        } else {
            languageRepository.getLanguageDisplayInfo(null)
        }
    }

    /**
     * Obtém apenas o nome da language pelo ID
     */
    fun getLanguageName(languageId: Long?): String {
        return getLanguageInfo(languageId).first
    }

    /**
     * Obtém apenas a bandeira da language pelo ID
     */
    fun getLanguageFlag(languageId: Long?): String {
        return getLanguageInfo(languageId).second
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

    /**
     * Filtra materiais por categoria (highlights vs others)
     */
    fun getHighlightMaterials(): List<Material> {
        return _materials.value.take(2) // Primeiros 2 como highlights
    }

    fun getOtherMaterials(): List<Material> {
        return _materials.value.drop(2) // Resto como others
    }
}