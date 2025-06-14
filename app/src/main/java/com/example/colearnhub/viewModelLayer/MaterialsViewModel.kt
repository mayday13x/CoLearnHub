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
import java.time.ZoneOffset
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toInstant

@RequiresApi(Build.VERSION_CODES.O)
class MaterialViewModel : ViewModel() {

    private val materialRepository = MaterialsRepository()
    private val userRepository = UserRepository()
    private val tagRepository = TagRepository()
    private val languageRepository = LanguageRepository()

    // Estados para UI
    private val _allMaterials = MutableStateFlow<List<Material>>(emptyList())
    val allMaterials: StateFlow<List<Material>> = _allMaterials.asStateFlow()

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

    // Novos estados para filtros de data
    private val _startDateFilter = MutableStateFlow<LocalDateTime?>(null)
    val startDateFilter: StateFlow<LocalDateTime?> = _startDateFilter.asStateFlow()

    private val _endDateFilter = MutableStateFlow<LocalDateTime?>(null)
    val endDateFilter: StateFlow<LocalDateTime?> = _endDateFilter.asStateFlow()

    init {
        // Carregar dados iniciais
        loadAllTags()
        loadAllMaterials()
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
                loadAllMaterials()
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
     * Carrega todos os materiais na memória
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAllMaterials() {    // public materials
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Carregar todos os materiais públicos
                val publicMaterials = materialRepository.getPublicMaterials()
                _allMaterials.value = publicMaterials

                // Carregar informações dos autores, tags e languages para todos os materiais
                loadAuthorsInfo(publicMaterials)
                loadTagsInfo(publicMaterials)
                loadLanguagesInfo(publicMaterials)

                // Aplicar filtros iniciais
                applyAllFilters()

                // Garantir que todos os dados relacionados foram carregados
                val allAuthorsLoaded = publicMaterials.all { material ->
                    material.author_id?.let { authorId ->
                        _usersCache.value.containsKey(authorId)
                    } ?: true
                }

                val allTagsLoaded = publicMaterials.all { material ->
                    material.tags?.all { tag ->
                        _tagsCache.value.containsKey(tag.id)
                    } ?: true
                }

                val allLanguagesLoaded = publicMaterials.all { material ->
                    material.language?.let { languageId ->
                        _languagesCache.value.containsKey(languageId)
                    } ?: true
                }

                // Só finaliza o loading quando todos os dados estiverem carregados
                if (allAuthorsLoaded && allTagsLoaded && allLanguagesLoaded) {
                    _isLoading.value = false
                } else {
                    // Se algum dado não foi carregado, tenta carregar novamente
                    loadAuthorsInfo(publicMaterials)
                    loadTagsInfo(publicMaterials)
                    loadLanguagesInfo(publicMaterials)
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error loading materials: ${e.message}"
                Log.e("MaterialViewModel", "Error loading materials", e)
                _isLoading.value = false
            }
        }

    }

    /**
     * Carrega materiais públicos
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPublicMaterials() {
        applyAllFilters() // Aplica todos os filtros (sem filtros iniciais = todos os públicos)
    }

    /**
     * Carrega materiais por autor
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMaterialsByAuthor(author_id: String) {
        applyAllFilters(author_id) // Aplica todos os filtros para o autor específico
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
                loadAllMaterials()
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
        // _currentFilterTime.value = timeFilter
        // Reset date pickers if time filter is selected
        // _startDateFilter.value = null
        // _endDateFilter.value = null
        // applyAllFilters()
    }

    /**
     * Define a data de início para o filtro.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartDateFilter(date: LocalDateTime?) {
        _startDateFilter.value = date
        // Reset time filter if date picker is used
        // _currentFilterTime.value = null
        applyAllFilters()
    }

    /**
     * Define a data de fim para o filtro.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setEndDateFilter(date: LocalDateTime?) {
        _endDateFilter.value = date
        // Reset time filter if date picker is used
        // _currentFilterTime.value = null
        applyAllFilters()
    }

    /**
     * Carrega todas as tags disponíveis.
     */
    fun loadAllTags() {
        viewModelScope.launch {
            //_isLoading.value = true
            try {
                _allTags.value = tagRepository.getAllTags()
            } catch (e: Exception) {
                _errorMessage.value = "Error loading tags: ${e.message}"
                Log.e("MaterialViewModel", "Error loading tags", e)
            }
            //_isLoading.value = false
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
        // _currentFilterTime.value = null // Removed
        _selectedFilterTags.value = emptyList()
        _selectedFilterFileTypes.value = emptyList()
        _startDateFilter.value = null
        _endDateFilter.value = null
        //loadAllMaterials() // Recarrega tudo sem filtros
    }

    /**
     * Aplica todos os filtros combinados.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun applyAllFilters(authorId: String? = null) {
        viewModelScope.launch {
            //_isLoading.value = true
            _errorMessage.value = null

            try {
                var filteredMaterials = _allMaterials.value

                // Aplicar filtro de busca
                if (_currentSearchQuery.value.isNotBlank()) {
                    filteredMaterials = filteredMaterials.filter { material ->
                        material.title.contains(_currentSearchQuery.value, ignoreCase = true) ||
                        (material.description?.contains(_currentSearchQuery.value, ignoreCase = true) == true)
                    }
                }

                // Aplicar filtro de data
                _startDateFilter.value?.let { startDate ->
                    filteredMaterials = filteredMaterials.filter { material ->
                        material.created_at?.let { created ->
                            val materialDate = LocalDateTime.parse(created)
                            !materialDate.isBefore(startDate)
                        } ?: false
                    }
                }

                _endDateFilter.value?.let { endDate ->
                    filteredMaterials = filteredMaterials.filter { material ->
                        material.created_at?.let { created ->
                            val materialDate = LocalDateTime.parse(created)
                            !materialDate.isAfter(endDate)
                        } ?: false
                    }
                }

                // Aplicar filtro de tags
                if (_selectedFilterTags.value.isNotEmpty()) {
                    filteredMaterials = filteredMaterials.filter { material ->
                        material.tags?.any { tag ->
                            _selectedFilterTags.value.any { selectedTag -> selectedTag.id == tag.id }
                        } == true
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

                // Separar materiais por autor se necessário
                if (authorId != null) {
                    filteredMaterials = filteredMaterials.filter { it.author_id == authorId }
                    _userMaterials.value = filteredMaterials
                } else {
                    _materials.value = filteredMaterials
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error applying filters: ${e.message}"
                Log.e("MaterialViewModel", "Error applying filters", e)
            }

            //_isLoading.value = false
        }
    }
}