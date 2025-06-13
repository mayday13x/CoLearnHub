package com.example.colearnhub.viewModelLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
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

    // Filtros
    private val _allTags = MutableStateFlow<List<TagData>>(emptyList())
    val allTags: StateFlow<List<TagData>> = _allTags.asStateFlow()

    private val _selectedFilterTags = MutableStateFlow<List<TagData>>(emptyList())
    val selectedFilterTags: StateFlow<List<TagData>> = _selectedFilterTags.asStateFlow()

    private val _selectedFilterFileTypes = MutableStateFlow<List<String>>(emptyList())
    val selectedFilterFileTypes: StateFlow<List<String>> = _selectedFilterFileTypes.asStateFlow()

    private val _currentSearchQuery = MutableStateFlow("")
    private val _currentFilterTime = MutableStateFlow<String?>(null)

    init {
        // Carregar dados iniciais
        loadAllTags()
        loadPublicMaterials()
    }

    /**
     * Cria um novo material
     */
    @RequiresApi(Build.VERSION_CODES.O)
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

            val result = materialRepository.getMaterialByIdWithTags(materialId.toString())

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
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPublicMaterials() {
        viewModelScope.launch {
            _currentSearchQuery.value = "" // Reset search query
            _currentFilterTime.value = null // Reset time filter
            _selectedFilterTags.value = emptyList() // Reset tag filter
            _selectedFilterFileTypes.value = emptyList() // Reset file type filter
            applyAllFilters() // Aplica todos os filtros (sem filtros iniciais = todos os públicos)
        }
    }

    /**
     * Carrega materiais por autor
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMaterialsByAuthor(author_id: String) {
        viewModelScope.launch {
            _currentSearchQuery.value = "" // Reset search query
            _currentFilterTime.value = null // Reset time filter
            _selectedFilterTags.value = emptyList() // Reset tag filter
            _selectedFilterFileTypes.value = emptyList() // Reset file type filter
            applyAllFilters(author_id) // Aplica todos os filtros para o autor específico
        }
    }

    /**
     * Pesquisa materiais por título
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun searchMaterials(query: String) {
        _currentSearchQuery.value = query
        applyAllFilters()
    }

    /**
     * Atualiza um material
     */
    @RequiresApi(Build.VERSION_CODES.O)
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

    /**
     * Filtra materiais por período de tempo.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterMaterialsByTime(timeFilter: String?) {
        _currentFilterTime.value = timeFilter
        applyAllFilters()
    }

    /**
     * Carrega todas as tags disponíveis.
     */
    fun loadAllTags() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _allTags.value = tagRepository.getAllTags()
            } catch (e: Exception) {
                _errorMessage.value = "Error loading tags: ${e.message}"
                Log.e("MaterialViewModel", "Error loading tags", e)
            }
            _isLoading.value = false
        }
    }

    /**
     * Alterna o estado de seleção de uma tag para filtro.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTagFilter(tag: TagData) {
        val currentSelected = _selectedFilterTags.value.toMutableList()
        if (currentSelected.contains(tag)) {
            currentSelected.remove(tag)
        } else {
            currentSelected.add(tag)
        }
        _selectedFilterTags.value = currentSelected
        applyAllFilters()
    }

    /**
     * Limpa todas as tags selecionadas.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun clearTagFilter() {
        _selectedFilterTags.value = emptyList()
        applyAllFilters()
    }

    /**
     * Alterna o estado de seleção de um tipo de arquivo para filtro.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleFileTypeFilter(fileType: String) {
        val currentSelected = _selectedFilterFileTypes.value.toMutableList()
        if (currentSelected.contains(fileType)) {
            currentSelected.remove(fileType)
        } else {
            currentSelected.add(fileType)
        }
        _selectedFilterFileTypes.value = currentSelected
        applyAllFilters()
    }

    /**
     * Redefine todos os filtros.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun resetAllFilters() {
        _currentSearchQuery.value = ""
        _currentFilterTime.value = null
        _selectedFilterTags.value = emptyList()
        _selectedFilterFileTypes.value = emptyList()
        loadPublicMaterials() // Recarrega tudo sem filtros
    }

    /**
     * Aplica todos os filtros combinados.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyAllFilters(authorId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            var filteredMaterials = if (authorId != null) {
                materialRepository.getMaterialsByAuthor(authorId)
            } else {
                materialRepository.getPublicMaterials()
            }

            // Aplicar filtro de pesquisa por texto
            if (_currentSearchQuery.value.isNotBlank()) {
                filteredMaterials = filteredMaterials.filter {
                    it.title.contains(_currentSearchQuery.value, ignoreCase = true) ||
                            it.description?.contains(_currentSearchQuery.value, ignoreCase = true) == true
                }
            }

            // Aplicar filtro de tempo
            _currentFilterTime.value?.let { timeFilter ->
                if (timeFilter != "all") {
                    val now = LocalDateTime.now()
                    filteredMaterials = filteredMaterials.filter { material ->
                        material.created_at?.let { createdAtString ->
                            try {
                                val createdAt = LocalDateTime.parse(createdAtString.substringBefore("."))
                                when (timeFilter) {
                                    "24h" -> createdAt.isAfter(now.minusHours(24))
                                    "week" -> createdAt.isAfter(now.minusWeeks(1))
                                    "month" -> createdAt.isAfter(now.minusMonths(1))
                                    "year" -> createdAt.isAfter(now.minusYears(1))
                                    else -> false
                                }
                            } catch (e: DateTimeParseException) {
                                Log.e("MaterialViewModel", "Error parsing date in applyAllFilters: $createdAtString", e)
                                false
                            }
                        } ?: false
                    }
                }
            }

            // Aplicar filtro de tags
            if (_selectedFilterTags.value.isNotEmpty()) {
                filteredMaterials = filteredMaterials.filter { material ->
                    material.tags?.any { tag -> _selectedFilterTags.value.contains(tag) } == true
                }
            }

            // Aplicar filtro de tipo de arquivo
            if (_selectedFilterFileTypes.value.isNotEmpty()) {
                filteredMaterials = filteredMaterials.filter { material ->
                    material.file_url?.let { fileUrl ->
                        val extension = fileUrl.substringAfterLast(".", "").lowercase()
                        _selectedFilterFileTypes.value.contains(extension)
                    } ?: false
                }
            }

            if (authorId != null) {
                _userMaterials.value = filteredMaterials
            } else {
                _materials.value = filteredMaterials
            }

            // Carregar informações dos autores, tags e languages para os materiais filtrados
            loadAuthorsInfo(filteredMaterials)
            loadTagsInfo(filteredMaterials)
            loadLanguagesInfo(filteredMaterials)

            _isLoading.value = false
        }
    }
}