package com.example.colearnhub.viewModelLayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.repositoryLayer.MaterialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {

    private val repository = MaterialRepository()

    // Estados para UI
    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials: StateFlow<List<Material>> = _materials.asStateFlow()

    private val _selectedMaterial = MutableStateFlow<Material?>(null)
    val selectedMaterial: StateFlow<Material?> = _selectedMaterial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Cria um novo material
     */
    fun createMaterial(
        title: String,
        description: String? = null,
        fileUrl: String? = null,
        visibility: Boolean = true,
        languageId: Long? = null,
        authorId: Long? = null,
        tagId: Long? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.createMaterial(
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

            val result = repository.getMaterialById(materialId)

            if (result != null) {
                _selectedMaterial.value = result
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

            val result = repository.getPublicMaterials()
            _materials.value = result

            if (result.isEmpty()) {
                _errorMessage.value = "Nenhum material encontrado"
            }

            _isLoading.value = false
        }
    }

    /**
     * Carrega materiais por autor
     */
    fun loadMaterialsByAuthor(authorId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getMaterialsByAuthor(authorId)
            _materials.value = result

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

            val result = repository.searchMaterialsByTitle(query)
            _materials.value = result

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

            val result = repository.updateMaterial(materialId, title, description, visibility)

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

            val success = repository.deleteMaterial(materialId)

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