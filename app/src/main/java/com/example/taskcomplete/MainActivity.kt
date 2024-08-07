package com.example.taskcomplete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskcomplete.ui.theme.TaskCompleteTheme
import com.example.taskcomplete.ui.theme.primary
import com.example.taskcomplete.ui.theme.secondary
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskCompleteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }
}

val title = buildAnnotatedString {
    append("TASK")
    withStyle(style = SpanStyle(color = secondary)){
        append("Y")
    }
}
data class TodoItems (var task:String,var isCompleted:Boolean,val id:UUID,var editingEnabled:Boolean)

@Composable
fun TodoApp(){
    var todoItems by remember {
        mutableStateOf(listOf<TodoItems>())
    }
    var value by remember {
        mutableStateOf("")
    }

    fun handleDelete(id:UUID):Unit{
        todoItems=todoItems.filter { it.id!=id }
    }
    fun handleComplete(id:UUID):Unit{
       todoItems=todoItems.map { item->if (item.id==id) item.copy(isCompleted = !item.isCompleted) else item }
    }
    fun handeEdit(id:UUID){
        todoItems=todoItems.map { item->if (item.id==id) item.copy(editingEnabled = true) else item }
    }
    fun handleUpdate(id:UUID,updatedItem:String){
        if(updatedItem.isNotBlank()) todoItems=todoItems.map { item->if (item.id==id) item.copy(task = updatedItem, editingEnabled = false) else item }
    }
    fun handleSubmit(task:String){
       if(value.isNotBlank()){
           todoItems += TodoItems(value, false, UUID.randomUUID(), false);
           value=""
       }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0d0d0d))
        .padding(vertical = 30.dp, horizontal = 20.dp)){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier.padding(bottom = 15.dp), text = title, color = primary, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                ItemProgress(todoItems.size,todoItems.count { it.isCompleted })
                InputText(value,{input-> value = input},{handleSubmit(value)})
                LazyColumn {
                    items(todoItems.size){
                        ListItem(todoItems[it], onDelete = { id -> handleDelete(id) }, onComplete = {id->handleComplete(id)}, onEdit = {id->handeEdit(id)}, handleUpdate = {id,name->handleUpdate(id,name)})
                    }
                }
            }
    }
}

@Composable
fun ItemProgress(totalTask:Int,completedTask:Int){
    Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically,modifier = Modifier
        .fillMaxWidth()
        .border(width = 1.dp, shape = RoundedCornerShape(30.dp), color = primary)
        .padding(25.dp)){
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "TODO DONE", color = primary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "keep it up", color = primary, style = MaterialTheme.typography.titleMedium)
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .clip(RoundedCornerShape(47.5.dp))
            .size(95.dp)
            .background(secondary)){
            Text(text = "$completedTask/$totalTask", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Composable
fun InputText(text:String,handleChange:(value:String)->Unit,submit:()->Unit){
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedTextField(colors = OutlinedTextFieldDefaults.colors(focusedTextColor = primary, focusedBorderColor = primary, unfocusedBorderColor = primary ), modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth(0.8f), value = text, onValueChange = {it->handleChange(it)}, shape = RoundedCornerShape(25.dp), placeholder = { Text(
            text = "Enter the value", color = Color(0XFF746c5f)
        )})
       Box(contentAlignment = Alignment.Center, modifier = Modifier
           .size(50.dp)
           .clip(CircleShape)
           .background(secondary)){
           Icon(imageVector = Icons.Default.Add, contentDescription = "Add",
               Modifier
                   .size(30.dp)
                   .clickable { submit() })
       }
    }
}

@Composable
fun ListItem(item:TodoItems,onComplete:(id:UUID)->Unit,onDelete:(id:UUID)->Unit,onEdit:(id:UUID)->Unit,handleUpdate:(id:UUID,updatesItem:String)->Unit){
    val completed = item.isCompleted
    val decoration = if(completed) TextDecoration.LineThrough else TextDecoration.None

    var updatedItem by remember {
        mutableStateOf(item.task)
    }
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .padding(vertical = 5.dp)
        .fillMaxWidth()
        .background(Color(0xFF1e1e1e))
        .border(1.dp, primary, shape = RoundedCornerShape(10.dp))
        .padding(20.dp)
        .clickable { onComplete(item.id) }
    ){
        if(item.editingEnabled){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(colors = OutlinedTextFieldDefaults.colors(focusedTextColor = primary, focusedBorderColor = primary, unfocusedBorderColor = primary, unfocusedTextColor = primary ), modifier = Modifier
                    .fillMaxWidth(0.8f), value = updatedItem,
                    onValueChange = {it->updatedItem=it}, shape = RoundedCornerShape(25.dp),
                )
                Box(contentAlignment = Alignment.Center, modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(secondary)){
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Add",
                        Modifier
                            .size(25.dp)
                            .clickable {handleUpdate(item.id,updatedItem) })
                }
            }
        }
        else{
            Row(modifier = Modifier.fillMaxWidth(0.8f), verticalAlignment = Alignment.CenterVertically){
                Box(){
                    if(completed) Completed() else InCompleted()
                }
                Text(textDecoration = decoration, text = item.task, color = primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
                Icon(modifier = Modifier.clickable { onEdit(item.id) }, imageVector = Icons.Default.Edit, contentDescription = "edit", tint = primary)
                Icon(modifier = Modifier.clickable {  onDelete(item.id)},imageVector = Icons.Default.Delete, contentDescription = "delete", tint = primary)
            }
        }
    }
}
@Composable
fun InCompleted(){
    Box(modifier = Modifier
        .padding(horizontal = 10.dp)
        .size(20.dp)
        .border(1.dp, secondary, shape = CircleShape)){}
}

@Composable
fun Completed(){
    Box(modifier = Modifier
        .padding(horizontal = 10.dp)
        .size(20.dp)
        .background(Color.Green, shape = CircleShape))
}
@Preview(showBackground = true)

@Composable
fun GreetingPreview() {
    TaskCompleteTheme {
        TodoApp()
    }
}