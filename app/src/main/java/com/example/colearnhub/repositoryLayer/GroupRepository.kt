package com.example.colearnhub.repositoryLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.*
import com.example.colearnhub.modelLayer.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.ktor.client.utils.EmptyContent.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Objects.isNull

class GroupRepository {

    /**
     * Cria um novo grupo
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGroup(request: CreateGroupRequest, ownerId: String): Groups? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("GroupRepository", "Criando grupo: ${request.name}")

            // Criar o grupo
            val group = SupabaseClient.client
                .from("Groups")
                .insert(mapOf(
                    "name" to request.name,
                    "description" to (request.description ?: ""),
                    "owner_id" to ownerId,
                    "created_at" to java.time.LocalDateTime.now().toString(),
                    "group_picture" to null
                )) {
                    select()
                }
                .decodeSingle<Groups>()

            Log.d("GroupRepository", "Grupo criado com ID: ${group.id}")

            // Adicionar o owner como membro aceito
            group.id?.let { groupId ->
                addGroupMember(ownerId, groupId, true)

                // Convidar outros usuários se especificados
                request.invitedUserIds.forEach { userId ->
                    addGroupMember(userId, groupId, null)
                }
            }

            group
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao criar grupo: ${e.message}")
            Log.e("GroupRepository", "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    /**
     * Cria um GroupMember (usado para convites pendentes)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGroupMember(groupMember: Group_Members): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val memberWithTimestamp = groupMember.copy(
                joined_at = java.time.LocalDateTime.now().toString()
            )

            SupabaseClient.client
                .from("Group_Members")
                .insert(memberWithTimestamp)

            Log.d("GroupRepository", "GroupMember criado: userId=${groupMember.user_id}, groupId=${groupMember.group_id}")
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao criar GroupMember: ${e.message}")
            false
        }
    }

    /**
     * Adiciona um membro ao grupo
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addGroupMember(userId: String, groupId: Long, accept: Boolean?): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val member = Group_Members(
                user_id = userId,
                group_id = groupId,
                joined_at = java.time.LocalDateTime.now().toString(),
                accept = accept
            )

            SupabaseClient.client
                .from("Group_Members")
                .insert(member)

            Log.d("GroupRepository", "Membro $userId adicionado ao grupo $groupId")
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao adicionar membro: ${e.message}")
            false
        }
    }

    /**
     * Busca grupos do usuário (incluindo convites pendentes)
     */
    suspend fun getUserGroups(userId: String): List<GroupResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("GroupRepository", "Buscando grupos do usuário: $userId")

            // Buscar todos os grupos onde o usuário é membro (aceito ou pendente)
            val memberGroups = SupabaseClient.client
                .from("Group_Members")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Group_Members>()

            val groupResponses = mutableListOf<GroupResponse>()

            memberGroups.forEach { membership ->
                val group = getGroupById(membership.group_id)
                group?.let {
                    val members = getGroupMembers(membership.group_id)
                    groupResponses.add(
                        GroupResponse(
                            group = it,
                            members = members
                        )
                    )
                }
            }

            Log.d("GroupRepository", "Encontrados ${groupResponses.size} grupos")
            groupResponses
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao buscar grupos do usuário: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca um grupo por ID
     */
    suspend fun getGroupById(groupId: Long): Groups? = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Groups")
                .select {
                    filter {
                        eq("id", groupId)
                    }
                }
                .decodeSingleOrNull<Groups>()
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao buscar grupo: ${e.message}")
            null
        }
    }

    /**
     * Busca membros de um grupo
     */
    suspend fun getGroupMembers(groupId: Long): List<Group_Members> = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Group_Members")
                .select {
                    filter {
                        eq("group_id", groupId)
                    }
                }
                .decodeList<Group_Members>()
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao buscar membros do grupo: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca usuários por nome/email para convites
     */
    suspend fun searchUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (query.length < 2) return@withContext emptyList()

            SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        or {
                            ilike("username", "%$query%")
                            ilike("email", "%$query%")
                        }
                    }
                }
                .decodeList<User>()
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao buscar usuários: ${e.message}")
            emptyList()
        }
    }

    /**
     * Aceita convite para grupo
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun acceptGroupInvite(userId: String, groupId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Group_Members")
                .update({
                    set("accept", true)
                    set("joined_at", java.time.LocalDateTime.now().toString())
                }) {
                    filter {
                        eq("user_id", userId)
                        eq("group_id", groupId)
                    }
                }

            Log.d("GroupRepository", "Convite aceito para usuário $userId no grupo $groupId")
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao aceitar convite: ${e.message}")
            false
        }
    }

    /**
     * Rejeita convite para grupo (remove o registro)
     */
    suspend fun rejectGroupInvite(userId: String, groupId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Group_Members")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("group_id", groupId)
                        isNull("accept") // Só remove se for convite pendente
                    }
                }

            Log.d("GroupRepository", "Convite rejeitado para usuário $userId no grupo $groupId")
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao rejeitar convite: ${e.message}")
            false
        }
    }

    /**
     * Remove membro do grupo
     */
    suspend fun removeGroupMember(userId: String, groupId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Group_Members")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("group_id", groupId)
                    }
                }

            Log.d("GroupRepository", "Membro $userId removido do grupo $groupId")
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Erro ao remover membro: ${e.message}")
            false
        }
    }
}