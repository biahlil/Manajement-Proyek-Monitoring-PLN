package com.pln.monitoringpln.presentation.task.detail

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import com.pln.monitoringpln.domain.usecase.alat.FakeAlatRepository
import com.pln.monitoringpln.domain.usecase.tugas.DeleteTaskUseCase
import com.pln.monitoringpln.domain.usecase.tugas.FakeTugasRepository
import com.pln.monitoringpln.domain.usecase.tugas.GetTaskDetailUseCase
import com.pln.monitoringpln.domain.usecase.tugas.UpdateTaskStatusUseCase
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var authRepo: FakeAuthRepository
    private lateinit var tugasRepo: FakeTugasRepository
    private lateinit var alatRepo: FakeAlatRepository
    private lateinit var userRepo: FakeUserRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        tugasRepo = FakeTugasRepository()
        alatRepo = FakeAlatRepository()
        userRepo = FakeUserRepository()
        authRepo = FakeAuthRepository()

        // Seed Data
        tugasRepo.addDummyTasks(listOf(TestObjects.TUGAS_TODO)) // ID: task-1
        alatRepo.addDummy(TestObjects.ALAT_VALID) // ID: alat-1
        // Need to add user for GetTaskDetailUseCase to work properly
        val teknisi = com.pln.monitoringpln.domain.model.User(
            id = TestObjects.TUGAS_TODO.idTeknisi,
            email = "teknisi@pln.co.id",
            namaLengkap = "Teknisi 1",
            role = "Teknisi",
        )
        userRepo.addDummy(teknisi)

        val getTaskDetailUseCase = GetTaskDetailUseCase(tugasRepo, alatRepo, userRepo)
        val deleteTaskUseCase = DeleteTaskUseCase(tugasRepo)
        val updateTaskStatusUseCase = UpdateTaskStatusUseCase(tugasRepo)

        viewModel = TaskDetailViewModel(
            authRepository = authRepo,
            getTaskDetailUseCase = getTaskDetailUseCase,
            deleteTaskUseCase = deleteTaskUseCase,
            updateTaskStatusUseCase = updateTaskStatusUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTask success, matches initial state`() = runTest {
        val taskId = TestObjects.TUGAS_TODO.id // "task-1"

        viewModel.loadTask(taskId)

        val state = viewModel.state.value
        assertFalse("Loading harus false setelah selesai", state.isLoading)
        assertEquals("Initial status should be TODO", "TODO", state.taskStatus)
        assertEquals(taskId, state.task?.id)
    }

    @Test
    fun `startTask defaults status to IN_PROGRESS`() = runTest {
        // Setup state awal
        viewModel.loadTask(TestObjects.TUGAS_TODO.id)

        // Act
        viewModel.startTask()

        // Assert
        val state = viewModel.state.value
        assertEquals("Status should be IN_PROGRESS", "IN_PROGRESS", state.taskStatus)
        assertEquals("IN_PROGRESS", state.task?.status)

        // Verify Repo
        val taskInRepo = tugasRepo.getTaskDetail(TestObjects.TUGAS_TODO.id).getOrNull()
        assertEquals("IN_PROGRESS", taskInRepo?.status)
    }

    @Test
    fun `stopTask defaults status back to TODO`() = runTest {
        // Setup: Start dulu biar jadi IN_PROGRESS
        viewModel.loadTask(TestObjects.TUGAS_TODO.id)
        viewModel.startTask()
        assertEquals("IN_PROGRESS", viewModel.state.value.taskStatus)

        // Act: Stop
        viewModel.stopTask()

        // Assert
        val state = viewModel.state.value
        assertEquals("Status should be TODO", "TODO", state.taskStatus)
        assertEquals("TODO", state.task?.status)

        // Verify Repo
        val taskInRepo = tugasRepo.getTaskDetail(TestObjects.TUGAS_TODO.id).getOrNull()
        assertEquals("TODO", taskInRepo?.status)
    }
}
