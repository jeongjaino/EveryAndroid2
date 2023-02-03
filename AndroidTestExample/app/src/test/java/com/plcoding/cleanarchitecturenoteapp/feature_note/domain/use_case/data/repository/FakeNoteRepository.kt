package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.data.repository

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNoteRepository: NoteRepository {

    private val notes = mutableListOf<Note>()

    // 가짜 레포지토리를 만들어 간단한 리스트를 통해서 실제 데이터베이스를 시뮬레이션하고
    // 해당 유스케이스는 가짜 레포지토리를 사용함으로 실제 레포지토리에 적용
    override fun getNotes(): Flow<List<Note>> {
        return flow { emit(notes) }
    }

    override suspend fun getNoteById(id: Int): Note? {
        return notes.find{ it.id == id}
    }

    override suspend fun insertNote(note: Note) {
        notes.add(note)
    }

    override suspend fun deleteNote(note: Note) {
        notes.remove(note)
    }
}