package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.data.repository.FakeNoteRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddNoteTest{

    private lateinit var addNote : AddNote
    private lateinit var fakeRepository: FakeNoteRepository

    @Before
    fun setUp(){
        fakeRepository = FakeNoteRepository()
        addNote = AddNote(fakeRepository)
    }

    @Test
    fun `Check the title of note is blank, correct check`(){
        val blankTitleNote = Note(
            title = " ",
            content = "hello, world",
            color = 2,
            timestamp = 22
        )
        runBlocking {
            try {
                addNote(blankTitleNote)
            } catch (e: InvalidNoteException) {
                assertThat(e.message == "The title of the note can't be empty.").isTrue()
            }
        }
    }

    @Test
    fun `Check the content of note is blank, correct check`(){
        val blankTitleNote = Note(
            title = "hello, world",
            content = " ",
            color = 2,
            timestamp = 22
        )
        runBlocking {
            try {
                addNote(blankTitleNote)
            } catch (e: InvalidNoteException) {
                assertThat(e.message == "The content of the note can't be empty.").isTrue()
            }
        }
    }

    @Test
    fun `Insert note into Repository, correct Inserting`(){
        val noteItem = Note(
            title = "hello",
            content = "world",
            color = 1,
            timestamp = 2
        )
        runBlocking {
            addNote(noteItem)

            fakeRepository.getNotes().collect { noteList ->
                assertThat(noteItem in noteList).isTrue()
            }
        }
    }
}