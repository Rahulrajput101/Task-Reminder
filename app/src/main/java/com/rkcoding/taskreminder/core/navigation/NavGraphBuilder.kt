package com.rkcoding.taskreminder.core.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.rkcoding.taskreminder.core.utils.SnackBarEvent
import com.rkcoding.taskreminder.todo_features.presentation.sinInScreen.SinInEvent
import com.rkcoding.taskreminder.todo_features.presentation.sinInScreen.SinInScreen
import com.rkcoding.taskreminder.todo_features.presentation.sinInScreen.SinInViewModel
import com.rkcoding.taskreminder.todo_features.presentation.sinInScreen.component.GoogleAuthUiClient
import com.rkcoding.taskreminder.todo_features.presentation.todoTaskAddScreen.AddTaskScreen
import com.rkcoding.taskreminder.todo_features.presentation.todoTaskListScreen.TaskListScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraphBuilder() {


    val context = LocalContext.current
//
    val scope = rememberCoroutineScope()
//
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            onTapClient = Identity.getSignInClient(context)
        )
    }


    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SinInScreen.route,
    ){

        composable(Screen.SinInScreen.route){

            SinInScreen(
                navController = navController
            )
        }


        composable(Screen.TaskListScreen.route){
            TaskListScreen(
                userData = googleAuthUiClient.getSingedInUser(),
                onSinOut = {
                    scope.launch {
                        googleAuthUiClient.sinOut()
                        Toast.makeText(context, "SinOut Successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                navController = navController
            )
        }

        composable(Screen.AddTaskScreen.route){
            AddTaskScreen(navController = navController)
        }

    }

}



