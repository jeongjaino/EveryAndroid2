package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.data.repository.FakeNoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetNotesTest{

    private lateinit var getNotes: GetNotes
    private lateinit var fakeRepository: FakeNoteRepository

    @Before // 이전 세팅 함수, 유닛 테스트 전 설정함수, 객체를 초기화 하는 작업이 해당
    fun setUp(){
        fakeRepository = FakeNoteRepository()
        getNotes = GetNotes(fakeRepository)

        val notesToInsert = mutableListOf<Note>()
        ('a' .. 'z').forEachIndexed{ index, c ->
            notesToInsert.add(
                Note(
                    title = c.toString(),
                    content = c.toString(),
                    timestamp = index.toLong(),
                    color = index
                )
            )
        }
        notesToInsert.shuffle()
        runBlocking {
            notesToInsert.forEach{
                fakeRepository.insertNote(it)
            }
        }
    }

    // 테스트는 무엇을 하는지, 테스트는 무엇을 반환하는지 네이밍
    @Test
    fun `Order notes by title descending, correct order`() = runBlocking(){
        val notes = getNotes(NoteOrder.Title(OrderType.Descending)).first()

        // 제목을 오름차순으로 정렬했는데, 정말 정렬이 되었는지 확인
        for(i in 0 .. notes.size - 2){
            assertThat(notes[i].title).isGreaterThan(notes[i+1].title) // 주장 함수, 무언가를 확인하고 맞으면 성공
        }
    }

    @Test
    fun `Order notes by date ascending, correct order`() = runBlocking(){
        val notes = getNotes(NoteOrder.Date(OrderType.Ascending)).first()

        for(i in 0 .. notes.size - 2){
            assertThat(notes[i].timestamp).isLessThan(notes[i+1].timestamp)
        }
    }
    @Test
    fun `Order notes by date descending, correct order`() = runBlocking(){
        val notes = getNotes(NoteOrder.Date(OrderType.Descending)).first()

        for(i in 0 .. notes.size - 2){
            assertThat(notes[i].timestamp).isGreaterThan(notes[i+1].timestamp)
        }
    }
    @Test
    fun `Order notes by color ascending, correct order`() = runBlocking(){
        val notes = getNotes(NoteOrder.Color(OrderType.Ascending)).first()

        for(i in 0 .. notes.size - 2){
            assertThat(notes[i].color).isLessThan(notes[i+1].color)
        }
    }
    @Test
    fun `Order notes by color descending, correct order`() = runBlocking(){
        val notes = getNotes(NoteOrder.Color(OrderType.Descending)).first()

        for(i in 0 .. notes.size - 2){
            assertThat(notes[i].color).isGreaterThan(notes[i+1].color)
        }
    }
}