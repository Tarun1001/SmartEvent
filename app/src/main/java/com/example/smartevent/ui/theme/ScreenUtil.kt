
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmartEventDemoScreen(
    eventLog: String,
    onLogEvent: () -> Unit,
    onLogEventWithProperties: () -> Unit,
    onLogDebugEvent: () -> Unit,
    onFlushEvents: () -> Unit,
    onFilterToggle: (Boolean) -> Unit
) {
    var isFilterEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SmartEvent SDK Demo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        item {
            Button(
                onClick = onLogEvent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Event")
            }
        }

        item {
            Button(
                onClick = onLogEventWithProperties,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Event with Properties")
            }
        }

        item {
            Button(
                onClick = onLogDebugEvent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Debug Event (Filtered)")
            }
        }

        item {
            Button(
                onClick = onFlushEvents,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Flush Events")
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isFilterEnabled,
                    onCheckedChange = { newValue ->
                        isFilterEnabled = newValue
                        onFilterToggle(newValue)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Enable Debug Filter",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Text(
                text = "Event Log:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            val scrollState = rememberScrollState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = eventLog,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}