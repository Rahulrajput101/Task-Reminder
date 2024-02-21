package com.rkcoding.taskreminder.todo_features.presentation.todoTaskListScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rkcoding.taskreminder.R
import com.rkcoding.taskreminder.core.navigation.Screen
import com.rkcoding.taskreminder.core.utils.UiEvent
import com.rkcoding.taskreminder.todo_features.domain.model.UserData
import com.rkcoding.taskreminder.todo_features.presentation.todoTaskAddScreen.components.DialogBox
import com.rkcoding.taskreminder.todo_features.presentation.todoTaskListScreen.components.TaskCardItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    userData: UserData?,
    onSinOut: () -> Unit,
    navController: NavController,
    viewModel: TaskListViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    // coroutine scope
    val scope = rememberCoroutineScope()

    // show sinOut dialog state
    var showSinOutDialog by remember { mutableStateOf(false) }

    // snackBar state
    val snackBarState = remember { SnackbarHostState() }

      // floating action button functionality
    val listState = rememberLazyListState()
    val isFabExtended by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    // Show SinOut Dialog
    DialogBox(
        text = "Google SinOut",
        bodyText = "Are you sure you want to SinOut your Account",
        iconImage = Icons.Default.GroupOff,
        isDialogShow = showSinOutDialog,
        onConfirmButtonClick = {
            showSinOutDialog = false
            onSinOut()
        },
        onDismissRequest = { showSinOutDialog = false }
        )

    // swipe to dismiss
    val dismissState = rememberDismissState()



    LaunchedEffect(key1 = Unit){
        viewModel.uiEvent.collectLatest { event ->
            when(event){
                UiEvent.NavigateTo -> Unit
                UiEvent.NavigateUp -> Unit
                is UiEvent.ShowSnackBar -> {
                    snackBarState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddTaskScreen.route) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add tasks")},
                text = { Text(text = "Add Tasks") },
                expanded = isFabExtended
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Task Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (userData?.profileImageUrl != null){
                    AsyncImage(
                        model = userData.profileImageUrl,
                        contentDescription = "profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { showSinOutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentPadding = PaddingValues(12.dp)
            ){
                if(state.tasks.isEmpty()){
                    item {
                        Image(
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = R.drawable.task),
                            contentDescription = "Books"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "You don't have any Tasks. \n Click the + Icon to Add Tasks",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                items(state.tasks){ task ->

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            // background color
                           val backgroundColor by animateColorAsState(
                               when(dismissState.targetValue){
                                   DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.8f)
                                   else -> Color.White
                               },
                               label = "background color animation"
                           )
                            // icon size
                            val iconScale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1.3f else 0.5f,
                                label = "icon animation"
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color = backgroundColor)
                                    .padding(end = 16.dp), // inner padding
                                contentAlignment = Alignment.CenterEnd // place the icon at the end (left)
                            ) {
                                Icon(
                                    modifier = Modifier.scale(iconScale),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            TaskCardItem(
                                task = task,
                                onTaskCardClick = {
                                    navController.navigate(
                                        route = Screen.AddTaskScreen.route + "?taskId=${task.taskId}"
                                    )
                                },
                                onDeleteTaskClick = {
                                    viewModel.onEvent(TaskListEvent.DeleteTask(task))
                                    scope.launch {
                                        val result = snackBarState.showSnackbar(
                                            message = "Task Deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed){
                                            viewModel.onEvent(TaskListEvent.RestoreTask)
                                        }
                                    }
                                },
                                onCheckBoxClick = {
                                    viewModel.onEvent(TaskListEvent.OnTaskCompleteChange(task))
                                },
                                switchState = state.switchState,
                                onSwitchValueChange = {  }
                            )
                        }
                    )

                }
            }

        }
    }

}