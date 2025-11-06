package sk.dusanbartos.grison.domain.scratch

interface ScratchService {
    suspend fun scratchCard(): Result<String>
}