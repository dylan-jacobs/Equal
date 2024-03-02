package com.jacobstechnologies.equal

import android.content.Context
import android.content.res.Resources.Theme
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.switchmaterial.SwitchMaterial
import com.jacobstechnologies.equal.ui.theme.EqualTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


class MainActivity : ComponentActivity() {

    private lateinit var puzzle: Puzzle
    private lateinit var vm: ReorderListViewModel
    private lateinit var settings: Settings

    // states
    private var progressBarVisible = mutableStateOf(true)
    private var screenState = mutableStateOf(ScreenStates.PUZZLE)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        settings = Settings(applicationContext)
        setContent {
            MainActivityUI()
        }
    }

    @Preview(showSystemUi = true)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainActivityUI(){
        EqualTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Scaffold(topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name), color = Color.DarkGray) },
                        actions = {if (screenState.value == ScreenStates.PUZZLE) {
                            IconButton(onClick = {
                                screenState.value = ScreenStates.SETTINGS
                            }) {
                                Icon(Icons.Filled.Settings, stringResource(id = R.string.settings), tint = Color.DarkGray)
                            }
                        }},
                        navigationIcon = {if (screenState.value == ScreenStates.SETTINGS) {
                            IconButton(onClick = {
                                screenState.value = ScreenStates.PUZZLE
                            }) {
                                Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.new_puzzle), tint = Color.DarkGray)
                            }
                        }},
                        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                    )
                }, bottomBar = {

                }) { innerPadding ->
                    Column (modifier = Modifier
                        .padding(innerPadding) // all descending compose elements have additional padding
                    ) {
                        if (screenState.value == ScreenStates.PUZZLE) {
                            PuzzleActivity()
                        }
                        if (screenState.value == ScreenStates.SETTINGS) {
                            SettingsActivity()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PuzzleActivity() {
        Column (modifier = Modifier
            .padding(dimensionResource(id = R.dimen.main_activity_inner_padding))
        ) {
            LoadPuzzleAsync()

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(dimensionResource(id = R.dimen.button_elevation)),
                onClick = {
                    progressBarVisible.value = true // this will trigger puzzle UI refresh
                }
            ) {
                Text(stringResource(R.string.new_puzzle))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(dimensionResource(id = R.dimen.button_elevation)),
                onClick = {
                    try {
                        vm.cardData = puzzle.showSolution()
                    } catch (e : UninitializedPropertyAccessException) {
                        e.printStackTrace()
                    }
                }
            ) {
                Text(stringResource(R.string.show_solution))
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(R.string.jacobstechnologies),
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsActivity(){
        val options = listOf(easyPuzzleSettings.title, mediumPuzzleSettings.title, difficultPuzzleSettings.title, extremePuzzleSettings.title)
        var selectedOption by remember{mutableStateOf(settings.loadInt(Settings.SharedPreferencesSettings.DIFFICULTY, 0))}
        var switchCheckedState by remember { mutableStateOf(settings.loadBool(Settings.SharedPreferencesSettings.VIBRATION, true)) }
        Column (modifier = Modifier
            .padding(dimensionResource(id = R.dimen.main_activity_inner_padding))
        ) {
            var expanded by remember{ mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {

                TextField(
                    readOnly = true,
                    value = options[selectedOption],
                    onValueChange = {},
                    label = { Text(stringResource(R.string.difficulty)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    options.forEach { selected ->
                        DropdownMenuItem(
                            text = { Text(text = selected) },
                            onClick = {
                                settings.saveInt(Settings.SharedPreferencesSettings.DIFFICULTY, options.indexOf(selected))
                                selectedOption = options.indexOf(selected)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier
                .height(30.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.vibrations))
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Switch(
                    checked = switchCheckedState,
                    onCheckedChange = {
                        switchCheckedState = it
                        vibrate(context = applicationContext, vibrationLength = switchVibrationLength, settings)
                        settings.saveBool(Settings.SharedPreferencesSettings.VIBRATION, it)
                    }
                )
            }

        }
    }

    @Composable
    fun OperatorCardComposable(modifier : Modifier, operatorCard : OperatorCardData) {
        Box(
            modifier = modifier
                .background(
                    color = colorResource(id = R.color.number_slot_color),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.number_slot_radius))
                ),
            contentAlignment = Alignment.Center
            )
        {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.number_slot_size))
                    .padding(dimensionResource(id = R.dimen.number_slot_padding))
                    .background(
                        color = colorResource(R.color.number_card_color),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.number_slot_radius))
                    )
            ) {
                Text(
                    text = operatorCard.value.toString(),
                    color = colorResource(id = R.color.purple_700),
                    modifier = Modifier
                        .align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun HorizontalReorderList(
        vm: ReorderListViewModel
    ) {
        val background = if (checkSolved(vm.cardData)) Color.Green else Color.Red

        val puzzleListState = rememberReorderableLazyListState(
            onMove = vm::move,
            onDragEnd = { _, _ ->
                val vibrationLength = if (checkSolved(vm.cardData)) winVibrationLength else moveVibrationLength
                vibrate(context = this@MainActivity, vibrationLength = vibrationLength, settings = settings)
            }
        )

        LazyRow(
            state = puzzleListState.listState,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.number_slot_margins)),
            modifier = Modifier
                .combinedClickable(onClick = {}, onLongClick = { vibrate(context = this, vibrationLength = moveVibrationLength, settings = settings) })
                .reorderable(puzzleListState)
                .detectReorderAfterLongPress(puzzleListState)
                .background(color = background, shape = RoundedCornerShape(dimensionResource(id = R.dimen.number_slot_radius)))
                .padding(dimensionResource(id = R.dimen.number_slot_margins))
        ) {
            items(vm.cardData, { item -> item.key }) { item ->
                ReorderableItem(
                    reorderableState = puzzleListState,
                    key = item.key
                ) { dragging ->
                    val scale = animateFloatAsState(if (dragging) 1.2f else 1.0f, label = "")
                    val elevation = animateDpAsState(
                        if (dragging) dimensionResource(
                            id = R.dimen.number_slot_normal_elevation)
                        else dimensionResource(
                            id = R.dimen.number_slot_dragging_elevation), label = "")

                    OperatorCardComposable(
                        modifier = Modifier
                            .scale(scale.value)
                            .shadow(elevation.value),
                        operatorCard = item
                    )
                }
            }
        }
    }

    @Composable
    fun LoadPuzzleAsync(){
        if (progressBarVisible.value) {
            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.Default).launch {
                    //puzzle = generateSmarterPuzzle(puzzleSettingsPresets[settings.loadInt(Settings.SharedPreferencesSettings.DIFFICULTY, 0)])
                    puzzle = loadMorePuzzles(context = applicationContext)
                    progressBarVisible.value = false
                }
            }
        }
        if (!progressBarVisible.value) {
            vm = ReorderListViewModel(data = puzzle.operators)
            HorizontalReorderList(vm)
        }
        else{
            CircularProgressIndicator()
        }
    }
}

fun vibrate(context: Context, vibrationLength: Long, settings : Settings){
    if (!settings.loadBool(Settings.SharedPreferencesSettings.VIBRATION, true)) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
            .defaultVibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                vibrationLength,
                VibrationEffect.DEFAULT_AMPLITUDE))
    }
    else {
        @Suppress("DEPRECATION")
        val vibrator = (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        @Suppress("DEPRECATION")
        vibrator.vibrate(vibrationLength)
    }
}

enum class ScreenStates{
    PUZZLE, SETTINGS
}


